import createToast, { toastComponent } from "./toast.js";
import { setTotalCartItemsQuantity } from "./header.js";

// STATIC DATA
const currentUserIdMetaTag = document.querySelector(
  "meta[name='currentUserId']"
);
const deliveryMethodInputValues = {
  1: {
    deliveryMethod: 1,
    deliveryPrice: 20000,
  },
  2: {
    deliveryMethod: 2,
    deliveryPrice: 50000,
  },
};

// VOUCHER STATE
const voucherState = {
  code: null,
  discountAmount: 0,
  description: '',
};

function _formatPriceV(amount) {
  return new Intl.NumberFormat("vi-VN").format(Math.round(amount));
}

function _voucherDescription(v) {
  const min = v.minOrderValue ? ` · Đơn tối thiểu ${_formatPriceV(v.minOrderValue)}₫` : '';
  if (v.type === 'FIXED') {
    const isFreeship = v.value === 20000 || v.value === 50000 || (v.code && v.code.toUpperCase().includes('SHIP'));
    if (isFreeship) return `Miễn phí vận chuyển ${_formatPriceV(v.value)}₫${min}`;
    return `Giảm ${_formatPriceV(v.value)}₫${min}`;
  }
  const maxDisc = v.maxDiscount ? ` (tối đa ${_formatPriceV(v.maxDiscount)}₫)` : '';
  return `Giảm ${v.value}%${maxDisc}${min}`;
}

function _voucherExpireText(v) {
  if (!v.endDate) return '';
  const d = new Date(v.endDate);
  return `HSD: ${d.getDate().toString().padStart(2,'0')}/${(d.getMonth()+1).toString().padStart(2,'0')}/${d.getFullYear()}`;
}

function _isFreeship(v) {
  return v.type === 'FIXED' && (
    (v.code && v.code.toUpperCase().includes('SHIP')) ||
    v.value === 20000 || v.value === 50000
  );
}

function _buildVoucherCard(v, isSelected, subtotal) {
  const isFs = _isFreeship(v);
  const cls = isFs ? 'freeship' : 'discount';
  const icon = isFs ? 'fa-shipping-fast' : 'fa-percent';
  const desc = _voucherDescription(v);
  const expire = _voucherExpireText(v);
  const notEnough = v.minOrderValue && subtotal < v.minOrderValue;
  const selCls = isSelected ? ' selected-voucher' : '';

  let btnLabel, btnCls;
  if (isSelected) {
    btnLabel = '<i class="fas fa-check me-1"></i>Đã chọn';
    btnCls = ' applied';
  } else {
    btnLabel = 'Áp dụng';
    btnCls = '';
  }

  const needMoreHtml = notEnough
    ? `<div class="vi-need-more"><i class="fas fa-info-circle me-1"></i>Cần thêm ${_formatPriceV(v.minOrderValue - subtotal)}₫</div>`
    : '';

  return `
    <div class="voucher-item ${cls}${selCls}" data-code="${v.code}" data-type="${v.type}" data-value="${v.value}" data-max="${v.maxDiscount||0}">
      <div class="vi-icon"><i class="fas ${icon}"></i></div>
      <div class="vi-info">
        <div class="vi-name">${v.code}</div>
        <div class="vi-desc">${desc}</div>
        ${needMoreHtml}
        ${expire ? `<div class="vi-expire"><i class="fas fa-clock me-1"></i>${expire}</div>` : ''}
      </div>
      <button class="btn-apply-voucher${btnCls}" data-code="${v.code}" type="button">${btnLabel}</button>
    </div>
  `;
}

// Đọc subtotal từ DOM inputs (chính xác hơn state khi user chưa bấm "Cập nhật")
function _getLiveSubtotal() {
  let total = 0;
  state.cart.cartItems.forEach(item => {
    const inputEl = document.getElementById(`quantity-cart-item-${item.id}`);
    const qty = inputEl ? Math.max(1, parseInt(inputEl.value) || 1) : item.quantity;
    const finalPrice = item.productDiscount === 0
      ? item.productPrice
      : Math.round(item.productPrice * (100 - item.productDiscount) / 100);
    total += finalPrice * qty;
  });
  return total > 0 ? total : state.getTempPrice();
}

async function _fetchAvailableVouchers() {
  try {
    const resp = await fetch(`/api/vouchers/available`);
    if (!resp.ok) return [];
    return await resp.json();
  } catch { return []; }
}

async function _applyVoucherCode(code) {
  const subtotal = _getLiveSubtotal();          // <-- đọc live từ DOM
  const userId = currentUserIdMetaTag ? currentUserIdMetaTag.content : 'GUEST';
  try {
    const resp = await fetch(`/api/vouchers/validate?code=${encodeURIComponent(code)}&subtotal=${subtotal}&userId=${userId}`);
    const data = await resp.json();
    if (!resp.ok) throw new Error(data.error || 'Mã không hợp lệ');
    voucherState.code = data.code;
    voucherState.discountAmount = data.discountAmount;
    voucherState.description = _voucherDescription({ type: data.type, value: data.value, code: data.code });
    return { ok: true };
  } catch (e) {
    return { ok: false, error: e.message };
  }
}

function _clearVoucher() {
  voucherState.code = null;
  voucherState.discountAmount = 0;
  voucherState.description = '';
}

function _renderVoucherUI() {
  const pickBtn = document.getElementById('voucherPickBtn');
  const pickText = document.getElementById('voucherPickText');
  const appliedRow = document.getElementById('voucherAppliedRow');
  const appliedText = document.getElementById('voucherAppliedText');
  const discountRow = document.getElementById('discount-row');
  const discountPrice = document.getElementById('discount-price');

  if (voucherState.code) {
    if (pickBtn) pickBtn.style.display = 'none';
    if (appliedRow) appliedRow.classList.remove('d-none');
    if (appliedText) appliedText.textContent = `${voucherState.code} · Giảm ${_formatPriceV(voucherState.discountAmount)}₫`;
    if (discountRow) discountRow.style.display = '';
    if (discountPrice) discountPrice.textContent = _formatPriceV(voucherState.discountAmount);
  } else {
    if (pickBtn) pickBtn.style.display = '';
    if (appliedRow) appliedRow.classList.add('d-none');
    if (discountRow) discountRow.style.display = 'none';
  }
}

let _voucherModalDelegated = false; // chỉ gắn event delegation 1 lần

async function _openVoucherModal() {
  const modalEl = document.getElementById('voucherModal');
  if (!modalEl) return;
  const modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);

  // Gắn event delegation 1 lần duy nhất (tránh duplicate listeners)
  if (!_voucherModalDelegated) {
    _voucherModalDelegated = true;
    let _applying = false;
    modalEl.querySelector('.modal-body').addEventListener('click', async (e) => {
      const btn = e.target.closest('.btn-apply-voucher');
      if (!btn || btn.classList.contains('applied') || _applying) return;
      const code = btn.dataset.code;
      _applying = true;
      btn.disabled = true;
      const origHtml = btn.innerHTML;
      btn.innerHTML = '<span class="spinner-border spinner-border-sm"></span>';
      const currentModal = bootstrap.Modal.getInstance(modalEl);
      const result = await _applyVoucherCode(code);
      _applying = false;
      btn.disabled = false;
      if (result.ok) {
        render();
        _renderVoucherUI();
        currentModal?.hide();
        createToast(toastComponent(`✅ Áp dụng voucher ${code} thành công! Giảm ${_formatPriceV(voucherState.discountAmount)}₫`, 'success'));
      } else {
        btn.innerHTML = origHtml;
        createToast(toastComponent(result.error, 'danger'));
      }
    });
  }

  // Hiện loading ngay
  const listFs = document.getElementById('voucherListFreeship');
  const listDis = document.getElementById('voucherListDiscount');
  const loadingHtml = '<div class="text-muted small p-3 text-center"><span class="spinner-border spinner-border-sm me-2"></span>Đang tải...</div>';
  if (listFs) listFs.innerHTML = loadingHtml;
  if (listDis) listDis.innerHTML = loadingHtml;
  modal.show();

  // Lấy subtotal live từ DOM inputs
  const subtotal = _getLiveSubtotal();
  const vouchers = await _fetchAvailableVouchers();
  const freeship = vouchers.filter(v => _isFreeship(v));
  const discount = vouchers.filter(v => !_isFreeship(v));

  if (listFs) {
    listFs.innerHTML = freeship.length
      ? freeship.map(v => _buildVoucherCard(v, v.code === voucherState.code, subtotal)).join('')
      : '<div class="text-muted small p-2 text-center">Không có voucher freeship nào</div>';
  }
  if (listDis) {
    listDis.innerHTML = discount.length
      ? discount.map(v => _buildVoucherCard(v, v.code === voucherState.code, subtotal)).join('')
      : '<div class="text-muted small p-2 text-center">Không có voucher giảm giá nào</div>';
  }
}

function _initVoucherHandlers() {
  const pickBtn = document.getElementById('voucherPickBtn');
  const removeBtn = document.getElementById('voucherRemoveBtn');
  const applyCodeBtn = document.getElementById('applyVoucherCodeBtn');
  const codeInput = document.getElementById('voucherCodeInput');
  const codeError = document.getElementById('voucherCodeError');

  if (pickBtn) pickBtn.addEventListener('click', _openVoucherModal);

  if (removeBtn) {
    removeBtn.addEventListener('click', () => {
      _clearVoucher();
      render();
      _renderVoucherUI();
    });
  }

  if (applyCodeBtn && codeInput) {
    applyCodeBtn.addEventListener('click', async () => {
      const code = codeInput.value.trim().toUpperCase();
      if (!code) return;
      if (codeError) codeError.style.display = 'none';
      applyCodeBtn.disabled = true;
      applyCodeBtn.textContent = '...';
      const result = await _applyVoucherCode(code);
      applyCodeBtn.disabled = false;
      applyCodeBtn.textContent = 'Áp dụng';
      if (result.ok) {
        render();
        _renderVoucherUI();
        const modal = bootstrap.Modal.getInstance(document.getElementById('voucherModal'));
        modal?.hide();
        createToast(toastComponent(`✅ Áp dụng voucher ${code} thành công! Giảm ${_formatPriceV(voucherState.discountAmount)}₫`, 'success'));
      } else {
        if (codeError) { codeError.textContent = result.error; codeError.style.display = 'block'; }
        createToast(toastComponent(result.error, 'danger'));
      }
    });
    codeInput.addEventListener('keydown', e => { if (e.key === 'Enter') applyCodeBtn.click(); });
  }
}

// MESSAGES
const FAILED_OPERATION_MESSAGE = "Đã có lỗi truy vấn!";
const SUCCESS_DELETE_CART_ITEM_MESSAGE = (productName) =>
  `Đã xóa sản phẩm ${productName} khỏi giỏ hàng thành công!`;
const SUCCESS_UPDATE_CART_ITEM_MESSAGE = (productName) =>
  `Đã cập nhật số lượng của sản phẩm ${productName} thành công!`;
const SUCCESS_ADD_ORDER_MESSAGE = "Đặt hàng thành công!";

// ROOTS/ELEMENTS
const cartTableRootElement = document.querySelector("#cart-table");
const tempPriceRootElement = document.querySelector("#temp-price");
const deliveryPriceRootElement = document.querySelector("#delivery-price");
const totalPriceRootElement = document.querySelector("#total-price");
const checkoutBtnElement = document.querySelector("#checkoutBtn");
const cartRecommendationsRootElement = document.querySelector("#cart-recommendations");
const deliveryMethodRadioElements = [
  ...document.querySelectorAll("input[name='delivery-method']"),
];
const paymentMethodRadioElements = [
  ...document.querySelectorAll("input[name='payment-method']"),
];
const confirmPlaceOrderBtn = document.querySelector("#confirmPlaceOrderBtn");

// COMPONENTS
function cartItemRowComponent(props) {
  const {
    id,
    cartId,
    productId,
    productName,
    productPrice,
    productDiscount,
    productQuantity,
    productImageName,
    quantity,
  } = props;

  const hasImage = productImageName && String(productImageName).trim().length > 0;
  const imageHtml = hasImage
    ? `
      <img
        src="${"/image/" + productImageName}"
        alt="${productName}"
        width="80"
        height="80"
        onerror="this.style.display='none'; this.nextElementSibling && (this.nextElementSibling.style.display='flex');"
      >
      <div class="cart-image-fallback d-none align-items-center justify-content-center bg-light text-muted"
           style="width:80px;height:80px;border-radius:.5rem;">
        <i class="bi bi-capsule-pill fs-4"></i>
      </div>
    `
    : `
      <div class="cart-image-fallback d-flex align-items-center justify-content-center bg-light text-muted"
           style="width:80px;height:80px;border-radius:.5rem;">
        <i class="bi bi-capsule-pill fs-4"></i>
      </div>
    `;

  return `
    <tr>
      <td>
        <figure class="itemside">
          <div class="float-start me-3">
            ${imageHtml}
          </div>
          <figcaption class="info">
            <a href="${
              "/sanpham?id=" + productId
            }" class="title">${productName}</a>
          </figcaption>
        </figure>
      </td>
      <td>
        <div class="price-wrap">
          ${cartItemPriceComponent(productPrice, productDiscount)}
        </div>
      </td>
      <td>
        <div class="input-group qty-stepper">
          <button class="btn btn-outline-secondary" type="button" id="qty-dec-${id}" aria-label="Giảm số lượng">-</button>
          <input type="number" value="${quantity}" min="1" max="${productQuantity}" class="form-control" id="quantity-cart-item-${id}">
          <button class="btn btn-outline-secondary" type="button" id="qty-inc-${id}" aria-label="Tăng số lượng">+</button>
        </div>
      </td>
      <td class="text-center text-nowrap">
        <button type="button" class="btn btn-success" id="update-cart-item-${id}">Cập nhật</button>
        <button type="button" class="btn btn-danger ms-1" id="delete-cart-item-${id}">Xóa</button>
      </td>
    </tr>
  `;
}

function cartItemPriceComponent(productPrice, productDiscount) {
  if (productDiscount === 0) {
    return `<span class="price">${_formatPrice(productPrice)}₫</span>`;
  }

  return `
    <div>
      <span class="price">${_formatPrice(
        (productPrice * (100 - productDiscount)) / 100
      )}₫</span>
      <span class="ms-2 badge bg-info">-${productDiscount}%</span>
    </div>
    <small class="text-muted text-decoration-line-through">${_formatPrice(
      productPrice
    )}₫</small>
  `;
}

function cartTableComponent(cartItemRowComponentsFragment) {
  if (state.cart.cartItems.length === 0) {
    return `
      <div class="text-center p-5">
        <i class="bi bi-bag-x fs-1 text-muted d-block mb-2"></i>
        <div class="mb-3 text-muted">Chưa có sản phẩm nào trong giỏ hàng</div>
        <a href="/quick-buy" class="btn btn-primary"><i class="bi bi-lightning-charge me-1"></i> Mua nhanh</a>
      </div>
    `;
  }

  return `
    <div class="table-responsive-xl">
      <table class="cart-table table table-borderless">
        <thead class="text-muted">
          <tr class="small text-uppercase">
            <th scope="col" style="min-width: 350px;">Sản phẩm</th>
            <th scope="col" style="min-width: 160px;">Giá</th>
            <th scope="col" style="min-width: 150px;">Số lượng</th>
            <th scope="col" style="min-width: 100px;"></th>
          </tr>
        </thead>
        <tbody>${cartItemRowComponentsFragment}</tbody>
      </table>
    </div> <!-- table.responsive-md.// -->
  `;
}

function loadingComponent() {
  return `
    <div class="d-flex justify-content-center p-5">
      <div class="spinner-border text-primary" role="status">
      <span class="visually-hidden">Đang tải...</span>
      </div>
    </div>
  `;
}

// UTILS
async function _fetchGetCart() {
  const url = currentUserIdMetaTag
    ? "/cartItem?userId=" + currentUserIdMetaTag.content
    : "/guest/cartItem";
  const response = await fetch(url, {
    method: "GET",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    credentials: "same-origin",
  });
  return [response.status, await response.json()];
}

async function _fetchDeleteCartItem(cartItemId, productId) {
  const response = await fetch(
    currentUserIdMetaTag
      ? "/cartItem?cartItemId=" + cartItemId + "&productId=" + productId
      : "/guest/cartItem?productId=" + productId,
    {
      method: "DELETE",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      credentials: "same-origin",
    }
  );
  return [response.status, await response.json()];
}

async function _fetchUpdateCartItem(cartItemId, quantity, productId) {
  const cartItemRequest = {
    productId: productId,
    quantity: quantity,
  };

  let response;
  if (currentUserIdMetaTag) {
    response = await fetch("/cartItem?cartItemId=" + cartItemId, {
      method: "PUT",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(cartItemRequest),
      credentials: "same-origin",
    });
  } else {
    const guestReq = { productId: productId, quantity: quantity };
    response = await fetch("/guest/cartItem?cartItemId=" + cartItemId, {
      method: "PUT",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(guestReq),
      credentials: "same-origin",
    });
  }

  return [response.status, await response.json()];
}

async function _fetchPostAddOrder() {
  const address = document.querySelector("#diachi");
  if (!address || address.value.trim().length === 0) {
    createToast(toastComponent("Vui lòng nhập địa chỉ giao hàng", "danger"));
    return [null, null];
  }
  const orderItems = state.cart.cartItems.map((cartItem) => ({
    productId: cartItem.productId,
    price: cartItem.productPrice,
    discount: cartItem.productDiscount,
    quantity: cartItem.quantity,
  }));
  let response;
  if (currentUserIdMetaTag) {
    const orderRequest = {
      cartId: state.cart.cartItems[0].cartId,
      address: address.value,
      userId: currentUserIdMetaTag.content,
      deliveryMethod: state.order.deliveryMethod,
      deliveryPrice: state.order.deliveryPrice,
      paymentMethod: state.order.paymentMethod,
      orderItems: orderItems,
      voucherCode: voucherState.code || null,
      voucherDiscountAmount: voucherState.discountAmount || 0,
    };
    response = await fetch("/cart", {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(orderRequest),
      credentials: "same-origin",
    });
  } else {
    // guest checkout requires basic info
    const hoten = document.querySelector("#guest_hoten")?.value || "Khách lẻ";
    const sdt = document.querySelector("#guest_sdt")?.value || "";
    const email = document.querySelector("#guest_email")?.value || "";
    const guestOrder = {
      hoten,
      sdt,
      email,
      address: address.value,
      deliveryMethod: state.order.deliveryMethod,
      deliveryPrice: state.order.deliveryPrice,
      paymentMethod: state.order.paymentMethod,
      orderItems: orderItems,
    };
    response = await fetch("/guest/checkout", {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(guestOrder),
      credentials: "same-origin",
    });
  }

  return [response.status, await response.json()];
}

function _formatPrice(price) {
  return new Intl.NumberFormat("vi-VN").format(price.toFixed());
}

async function _fetchPostAddCartItem(productId, quantity = 1) {
  if (currentUserIdMetaTag) {
    const response = await fetch("/cartItem", {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      credentials: "same-origin",
      body: JSON.stringify({
        userId: currentUserIdMetaTag.content,
        productId: Number(productId),
        quantity: Number(quantity),
      }),
    });
    return [response.status, await response.json().catch(() => ({}))];
  }

  // Guest cart: cart id is carried by httpOnly cookie
  const response = await fetch("/guest/cartItem", {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    credentials: "same-origin",
    body: JSON.stringify({
      productId: Number(productId),
      quantity: Number(quantity),
    }),
  });

  return [response.status, await response.json().catch(() => ({}))];
}

async function _fetchCartRecommendations() {
  if (!state.cart?.cartItems?.length) return [];

  const cartProductIds = new Set(
    state.cart.cartItems.map((ci) => Number(ci.productId))
  );

  // Giảm tải: chỉ cần lấy gợi ý dựa trên vài sản phẩm đang có
  const seedProductIds = Array.from(cartProductIds).slice(0, 3);
  const unique = new Map(); // idSanpham -> sp

  const respList = await Promise.all(
    seedProductIds.map(async (id) => {
      const resp = await fetch(`/api/recommend/ingredient/${id}?limit=6`, {
        method: "GET",
        headers: { Accept: "application/json" },
      });
      if (!resp.ok) return [];
      return await resp.json();
    })
  );

  for (const recs of respList) {
    for (const sp of recs || []) {
      const spId = Number(sp.idSanpham);
      if (!spId || cartProductIds.has(spId)) continue; // không gợi ý sản phẩm đã có trong giỏ
      if (!unique.has(spId)) unique.set(spId, sp);
    }
  }

  return Array.from(unique.values()).slice(0, 6);
}

function _recommendedPriceText(sp) {
  const gia = Number(sp.gia || 0);
  const giamgia = Number(sp.giamgia || 0);
  const finalPrice = giamgia === 0 ? gia : (gia * (100 - giamgia)) / 100;
  return `${_formatPrice(finalPrice)}₫`;
}

function _recommendationCard(sp) {
  const priceText = _recommendedPriceText(sp);
  const img = sp.hinh ? `/image/${sp.hinh}` : "";
  const reason = sp.reason || "Gợi ý sản phẩm liên quan";

  return `
    <div class="d-flex align-items-start gap-3 mb-3">
      <div class="flex-shrink-0">
        ${
          img
            ? `<img src="${img}" alt="${sp.tenSanpham}" width="58" height="58" style="object-fit:cover;border-radius:10px;">`
            : `<div style="width:58px;height:58px;border-radius:10px;background:#f1f4f7;"></div>`
        }
      </div>
      <div class="flex-grow-1">
        <a href="/sanpham?id=${sp.idSanpham}" class="text-decoration-none text-dark" style="display:block; line-height:1.2; font-size:.95rem; font-weight:600;">
          ${sp.tenSanpham}
        </a>
        <div class="text-primary fw-semibold mt-1" style="font-size:.95rem;">${priceText}</div>
        <div class="small text-muted mt-1">${reason}</div>
        <button type="button" class="btn btn-sm btn-outline-primary mt-2 add-recommend-to-cart" data-id="${sp.idSanpham}">
          <i class="bi bi-cart-plus me-1"></i>Thêm vào giỏ
        </button>
      </div>
    </div>
  `;
}

async function _renderCartRecommendations() {
  if (!cartRecommendationsRootElement) return;

  if (!state.cart?.cartItems?.length) {
    cartRecommendationsRootElement.innerHTML = `Bạn chưa có sản phẩm trong giỏ.`;
    return;
  }

  cartRecommendationsRootElement.innerHTML = `Đang tải gợi ý...`;
  const recs = await _fetchCartRecommendations();
  if (!recs.length) {
    cartRecommendationsRootElement.innerHTML = `Không có gợi ý phù hợp ngay lúc này.`;
    return;
  }

  cartRecommendationsRootElement.innerHTML = recs
    .map((sp) => _recommendationCard(sp))
    .join("");

  cartRecommendationsRootElement
    .querySelectorAll(".add-recommend-to-cart")
    .forEach((btn) => {
      btn.addEventListener("click", async () => {
        const productId = btn.getAttribute("data-id");
        btn.disabled = true;
        try {
          const [status] = await _fetchPostAddCartItem(productId, 1);
          if (status === 200) {
            await state.initState(); // refresh cart + recommendations
            createToast(
              toastComponent("Đã thêm sản phẩm vào giỏ hàng!", "success")
            );
          } else {
            createToast(toastComponent(FAILED_OPERATION_MESSAGE, "danger"));
          }
        } finally {
          btn.disabled = false;
        }
      });
    });
}

// STATE
const initialCart = {
  id: 0,
  userId: 0,
  cartItems: [],
};

const initialOrder = {
  deliveryMethod: 1,
  deliveryPrice: 20000,
  paymentMethod: "CASH",
};

const state = {
  cart: { ...initialCart },
  order: { ...initialOrder },
  initState: async () => {
    const [status, data] = await _fetchGetCart();
    if (status === 200) {
      state.cart = data;
      render();
      attachEventHandlersForNoneRerenderElements();
      await _renderCartRecommendations();
    } else if (status === 404) {
      createToast(toastComponent(FAILED_OPERATION_MESSAGE, "danger"));
    }
  },
  deleteCartItem: async (currentCartItem) => {
    if (confirm("Bạn có muốn xóa?")) {
      const [status] = await _fetchDeleteCartItem(
        currentCartItem.cartId,
        currentCartItem.productId
      );
      if (status === 200) {
        state.cart.cartItems = state.cart.cartItems.filter(
          (cartItem) => cartItem.id !== currentCartItem.id
        );
        render();
        createToast(
          toastComponent(
            SUCCESS_DELETE_CART_ITEM_MESSAGE(currentCartItem.productName),
            "success"
          )
        );
        setTotalCartItemsQuantity(state.cart);
      } else if (status === 404) {
        createToast(toastComponent(FAILED_OPERATION_MESSAGE, "danger"));
      }
    }
  },
  updateCartItem: async (currentCartItem, quantity) => {
    if (currentCartItem.quantity !== quantity) {
      if (quantity <= 0) {
        createToast(toastComponent("Vui lòng nhập số lượng hợp lệ", "danger"));
        return;
      }
      const [status] = await _fetchUpdateCartItem(
        currentCartItem.cartId,
        quantity,
        currentCartItem.productId
      );
      if (status === 200) {
        state.cart.cartItems = state.cart.cartItems.map((cartItem) => {
          return cartItem.id === currentCartItem.id
            ? { ...cartItem, quantity: quantity }
            : cartItem;
        });
        render();
        createToast(
          toastComponent(
            SUCCESS_UPDATE_CART_ITEM_MESSAGE(currentCartItem.productName),
            "success"
          )
        );
        setTotalCartItemsQuantity(state.cart);
      } else if (status === 404) {
        createToast(toastComponent(FAILED_OPERATION_MESSAGE, "danger"));
      }
    }
  },
  checkoutCart: async () => {
    if (state.order.paymentMethod === "BANK_TRANSFER") {
      try {
        // Bước 1: Tạo đơn hàng trước (status: awaiting_payment)
        const orderResult = await _fetchPostAddOrder();
        if (!orderResult || orderResult[0] === null) return; // address error shown above
        const [orderStatus, orderData] = orderResult;
        if (orderStatus === 422) {
          createToast(toastComponent("Số lượng tồn kho không đủ", "danger"));
          return;
        }
        if (orderStatus !== 200 || !orderData?.orderId) {
          createToast(toastComponent(FAILED_OPERATION_MESSAGE, "danger"));
          return;
        }
        const orderId = orderData.orderId;

        // Bước 2: Tạo link PayOS với orderId
        const resp = await fetch("/api/payments/bank-transfer", {
          method: "POST",
          headers: { Accept: "application/json", "Content-Type": "application/json" },
          credentials: "same-origin",
          body: JSON.stringify({
            orderId: orderId,
            deliveryPrice: state.order.deliveryPrice,
          }),
        });
        if (resp.status !== 200) {
          let errMsg = "Không tạo được liên kết thanh toán";
          try { const b = await resp.json(); if (b?.message) errMsg = b.message; } catch (_) {}
          createToast(toastComponent(errMsg, "danger"));
          return;
        }
        const data = await resp.json();

        // Bước 3: Redirect toàn trang sang PayOS (server sẽ xử lý khi quay lại)
        state.cart = { ...initialCart };
        state.order = { ...initialOrder };
        window.location.href = data.checkoutUrl;
      } catch (e) {
        createToast(toastComponent("Lỗi khi xử lý thanh toán: " + e.message, "danger"));
      }
      return;
    }
    // Cash on delivery -> place order immediately
    const codResult = await _fetchPostAddOrder();
    if (!codResult || codResult[0] === null) return;
    const [status, data] = codResult;
    if (status === 200) {
      state.cart = { ...initialCart };
      state.order = { ...initialOrder };
      render();
      const orderIdText = data?.orderId ? ` Mã đơn hàng: #${data.orderId}` : "";
      createToast(
        toastComponent(`${SUCCESS_ADD_ORDER_MESSAGE}${orderIdText}`, "success")
      );
      setTotalCartItemsQuantity(state.cart);
    } else if (status === 422) {
      createToast(toastComponent("Số lượng tồn kho không đủ", "danger"));
    } else if (status === 404) {
      createToast(toastComponent(FAILED_OPERATION_MESSAGE, "danger"));
    }
  },
  changeDeliveryMethod: (deliveryMethodValue) => {
    if (state.order.deliveryMethod !== Number(deliveryMethodValue)) {
      state.order.deliveryMethod =
        deliveryMethodInputValues[deliveryMethodValue].deliveryMethod;
      state.order.deliveryPrice =
        deliveryMethodInputValues[deliveryMethodValue].deliveryPrice;
      render();
    }
  },
  changePaymentMethod: (paymentMethodValue) => {
    if (state.order.paymentMethod !== paymentMethodValue) {
      state.order.paymentMethod = paymentMethodValue;
      render();
    }
  },
  getTempPrice: () => {
    return state.cart.cartItems
      .map((cartItem) => {
        if (cartItem.productDiscount === 0) {
          return cartItem.productPrice * cartItem.quantity;
        }
        return (
          (
            (cartItem.productPrice * (100 - cartItem.productDiscount)) /
            100
          ).toFixed() * cartItem.quantity
        );
      })
      .reduce(
        (partialSum, productTotalPrice) => partialSum + productTotalPrice,
        0
      );
  },
  getDeliveryPrice: () => state.order.deliveryPrice,
  getDiscountAmount: () => voucherState.discountAmount || 0,
  getTotalPrice: () => Math.max(0, state.getTempPrice() + state.getDeliveryPrice() - state.getDiscountAmount()),
};

// RENDER
function render() {
  // Render cartTableRootElement
  const cartItemRowComponentsFragment = state.cart.cartItems
    .map(cartItemRowComponent)
    .join("");
  cartTableRootElement.innerHTML = cartTableComponent(
    cartItemRowComponentsFragment
  );

  // Render tempPriceRootElement, deliveryPriceRootElement, totalPriceRootElement
  tempPriceRootElement.innerHTML = _formatPrice(state.getTempPrice());
  deliveryPriceRootElement.innerHTML = _formatPrice(state.getDeliveryPrice());
  totalPriceRootElement.innerHTML = _formatPrice(state.getTotalPrice());
  _renderVoucherUI();

  // Render checkoutBtnElement, deliveryMethodRadioElements
  const isCartItemsEmpty = state.cart.cartItems.length === 0;
  checkoutBtnElement.disabled = isCartItemsEmpty;
  deliveryMethodRadioElements.forEach((radio) => {
    radio.disabled = isCartItemsEmpty;
    radio.checked = radio.value === String(state.order.deliveryMethod);
  });
  paymentMethodRadioElements.forEach((radio) => {
    radio.disabled = isCartItemsEmpty;
    radio.checked = radio.value === String(state.order.paymentMethod);
  });

  // Sync PayOS/COD card UI
  document.querySelectorAll('.payment-card').forEach((card) => {
    const val = card.dataset.value;
    card.classList.toggle('selected', val === state.order.paymentMethod);
    if (isCartItemsEmpty) {
      card.style.opacity = '0.5';
      card.style.pointerEvents = 'none';
    } else {
      card.style.opacity = '';
      card.style.pointerEvents = '';
    }
    card.onclick = () => {
      if (!isCartItemsEmpty) state.changePaymentMethod(val);
    };
  });

  // Attach event handlers for delete cart item buttons
  state.cart.cartItems.forEach((cartItem) => {
    const deleteCartItemBtnElement = document.querySelector(
      `#delete-cart-item-${cartItem.id}`
    );
    deleteCartItemBtnElement.addEventListener("click", () =>
      state.deleteCartItem(cartItem)
    );
  });

  // Attach event handlers for update cart item buttons
  state.cart.cartItems.forEach((cartItem) => {
    const updateCartItemBtnElement = document.querySelector(
      `#update-cart-item-${cartItem.id}`
    );
    updateCartItemBtnElement.addEventListener("click", () => {
      const quantityCartItemInputElement = document.querySelector(
        `#quantity-cart-item-${cartItem.id}`
      );
      void state.updateCartItem(
        cartItem,
        Number(quantityCartItemInputElement.value)
      );
    });
  });

  // Attach +/- stepper handlers
  state.cart.cartItems.forEach((cartItem) => {
    const inputEl = document.querySelector(
      `#quantity-cart-item-${cartItem.id}`
    );
    const decBtn = document.querySelector(`#qty-dec-${cartItem.id}`);
    const incBtn = document.querySelector(`#qty-inc-${cartItem.id}`);
    if (decBtn && inputEl) {
      decBtn.addEventListener("click", () => {
        const current = Math.max(1, (Number(inputEl.value) || 1) - 1);
        inputEl.value = String(current);
      });
    }
    if (incBtn && inputEl) {
      incBtn.addEventListener("click", () => {
        const max = Number(inputEl.getAttribute("max")) || 9999;
        const current = Math.min(max, (Number(inputEl.value) || 1) + 1);
        inputEl.value = String(current);
      });
    }
  });
}

function attachEventHandlersForNoneRerenderElements() {
  // Attach event handlers for delivery method radios
  deliveryMethodRadioElements.forEach((radio) => {
    radio.addEventListener("click", () =>
      state.changeDeliveryMethod(radio.value)
    );
  });
  // Attach event handlers for payment method radios
  paymentMethodRadioElements.forEach((radio) => {
    radio.addEventListener("click", () =>
      state.changePaymentMethod(radio.value)
    );
  });

  // Attach event handlers for checkout button
  checkoutBtnElement.addEventListener("click", async () => {
    if (!currentUserIdMetaTag) {
      const nameEl = document.querySelector("#guest_hoten");
      const phoneEl = document.querySelector("#guest_sdt");
      const addressEl = document.querySelector("#diachi");
      const nameFb = document.querySelector("#guest_hoten_feedback");
      const phoneFb = document.querySelector("#guest_sdt_feedback");
      if (!nameEl?.value || nameEl.value.trim().length < 2) {
        nameEl?.classList.add("is-invalid");
        if (nameFb) nameFb.style.display = "block";
        createToast(toastComponent("Vui lòng nhập họ tên hợp lệ", "danger"));
        nameEl?.focus();
        return;
      }
      nameEl.classList.remove("is-invalid");
      if (nameFb) nameFb.style.display = "none";
      if (!addressEl?.value || addressEl.value.trim().length < 5) {
        createToast(
          toastComponent("Vui lòng nhập địa chỉ giao hàng", "danger")
        );
        addressEl?.focus();
        return;
      }
      if (phoneEl?.value && !/^\d{9,11}$/.test(phoneEl.value)) {
        phoneEl?.classList.add("is-invalid");
        if (phoneFb) phoneFb.style.display = "block";
        createToast(
          toastComponent("Số điện thoại không hợp lệ (9-11 số)", "danger")
        );
        phoneEl?.focus();
        return;
      }
      phoneEl?.classList.remove("is-invalid");
      if (phoneFb) phoneFb.style.display = "none";
    }
    const addressEl = document.querySelector("#diachi");
    const ckAddress = document.querySelector("#ck-address");
    const ckDelivery = document.querySelector("#ck-delivery");
    const ckPayment = document.querySelector("#ck-payment");
    const ckTemp = document.querySelector("#ck-temp");
    const ckShip = document.querySelector("#ck-ship");
    const ckTotal = document.querySelector("#ck-total");
    const deliveryText =
      state.order.deliveryMethod === 1
        ? "Giao hàng tiêu chuẩn"
        : "Giao hàng nhanh";
    const paymentText =
      state.order.paymentMethod === "BANK_TRANSFER"
        ? "PayOS"
        : "COD (Tiền mặt)";
    const ckDiscountRow = document.querySelector("#ck-discount-row");
    const ckDiscount = document.querySelector("#ck-discount");
    ckAddress.textContent = addressEl?.value || "";
    ckDelivery.textContent = deliveryText;
    ckPayment.textContent = paymentText;
    ckTemp.textContent = _formatPrice(state.getTempPrice()) + "₫";
    ckShip.textContent = _formatPrice(state.getDeliveryPrice()) + "₫";
    if (voucherState.code && voucherState.discountAmount > 0) {
      if (ckDiscountRow) ckDiscountRow.style.display = '';
      if (ckDiscount) ckDiscount.textContent = `-${_formatPrice(voucherState.discountAmount)}₫`;
    } else {
      if (ckDiscountRow) ckDiscountRow.style.display = 'none';
    }
    ckTotal.textContent = _formatPrice(state.getTotalPrice()) + "₫";

    const modalEl = document.getElementById("checkoutConfirmModal");
    const modal = new bootstrap.Modal(modalEl);
    modal.show();
  });

  if (confirmPlaceOrderBtn) {
    confirmPlaceOrderBtn.addEventListener("click", async () => {
      try {
        confirmPlaceOrderBtn.disabled = true;
        confirmPlaceOrderBtn.innerHTML =
          '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Đang xử lý...';
        await state.checkoutCart();
        const modalEl = document.getElementById("checkoutConfirmModal");
        const modal = bootstrap.Modal.getInstance(modalEl);
        modal?.hide();
      } finally {
        confirmPlaceOrderBtn.disabled = false;
        confirmPlaceOrderBtn.innerHTML =
          '<i class="fas fa-check me-1"></i> Xác nhận đặt hàng';
      }
    });
  }
}

// MAIN
cartTableRootElement.innerHTML = loadingComponent();
void state.initState();
_initVoucherHandlers();
