import createToast, { toastComponent } from "./toast.js";
import { setTotalCartItemsQuantity } from "./header.js";

// STATIC DATA
const currentUserIdMetaTag = document.querySelector("meta[name='currentUserId']");

// MESSAGES
const REQUIRED_SIGNIN_MESSAGE = "Vui lòng đăng nhập để thực hiện thao tác!";
const SUCCESS_ADD_CART_ITEM_MESSAGE = (productTitle) =>
  `Đã thêm <b>${productTitle}</b> vào giỏ hàng!`;
const FAILED_ADD_CART_ITEM_MESSAGE = "Đã có lỗi khi thêm vào giỏ hàng!";

// UTILS
async function _HomefetchPostAddCartItem(productId) {
  const cartItemRequest = {
    userId: currentUserIdMetaTag.content,
    productId: Number(productId),
    quantity: 1,
  };

  try {
    const response = await fetch("/cartItem", {
      method: "POST",
      headers: {
        "Accept": "application/json",
        "Content-Type": "application/json",
      },
      credentials: 'same-origin',
      body: JSON.stringify(cartItemRequest),
    });
    let body = {};
    try { body = await response.json(); } catch (_) {}
    return [response.status, body];
  } catch (e) {
    return [-1, {}];
  }
}

// EVENT HANDLERS
function noneSigninEvent() {
  createToast(toastComponent(REQUIRED_SIGNIN_MESSAGE, "warning"));
}

async function buyNowBtnEvent(productId, btn) {
  const original = btn ? btn.innerHTML : null;
  if (btn) { btn.disabled = true; btn.innerHTML = '<span class="spinner-border spinner-border-sm"></span>'; }
  try {
    const [status] = await _HomefetchPostAddCartItem(productId);
    if (status === 200) {
      window.location.href = "/cart";
    } else if (status === 401 || status === 403) {
      createToast(toastComponent(REQUIRED_SIGNIN_MESSAGE, "warning"));
    } else {
      createToast(toastComponent(FAILED_ADD_CART_ITEM_MESSAGE, "danger"));
    }
  } finally {
    if (btn) { btn.disabled = false; if (original) btn.innerHTML = original; }
  }
}

async function addCartItemBtnEvent(productId, productName, btn) {
  const original = btn ? btn.innerHTML : null;
  if (btn) { btn.disabled = true; btn.innerHTML = '<span class="spinner-border spinner-border-sm"></span>'; }
  try {
    const [status] = await _HomefetchPostAddCartItem(productId);
    if (status === 200) {
      const name = productName ||
        btn?.closest('[data-name]')?.getAttribute('data-name') ||
        btn?.closest('.product-item, .card')?.querySelector('.title, .product-title, h5, h6')?.textContent?.trim() ||
        'sản phẩm';
      createToast(toastComponent(SUCCESS_ADD_CART_ITEM_MESSAGE(name), "success"));
      setTotalCartItemsQuantity('1');
    } else if (status === 401 || status === 403) {
      createToast(toastComponent(REQUIRED_SIGNIN_MESSAGE, "warning"));
    } else {
      createToast(toastComponent(FAILED_ADD_CART_ITEM_MESSAGE, "danger"));
    }
  } finally {
    if (btn) { btn.disabled = false; if (original) btn.innerHTML = original; }
  }
}

// MAIN
const addCartItemBtns = document.querySelectorAll(".add-cart-item");
const buyNowBtns = document.querySelectorAll(".buy-now");

if (currentUserIdMetaTag) {
  addCartItemBtns.forEach((btn) => {
    btn.addEventListener("click", () => {
      const productName = btn.getAttribute("data-name") ||
        btn.closest('[data-name]')?.getAttribute('data-name') || '';
      addCartItemBtnEvent(btn.getAttribute("data-id"), productName, btn);
    });
  });

  buyNowBtns.forEach((btn) => {
    btn.addEventListener("click", () => {
      buyNowBtnEvent(btn.getAttribute("data-id"), btn);
    });
  });
} else {
  addCartItemBtns.forEach((btn) => {
    btn.addEventListener("click", noneSigninEvent);
  });

  buyNowBtns.forEach((btn) => {
    btn.addEventListener("click", noneSigninEvent);
  });
}
