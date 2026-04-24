import createToast, { toastComponent } from "./toast.js";
import { refreshCartBadge } from "./header.js";

// STATIC DATA
const currentUserIdMetaTag = document.querySelector("meta[name='currentUserId']");

// MESSAGES
const REQUIRED_SIGNIN_MESSAGE = "Vui lòng đăng nhập để thực hiện thao tác!";
const SUCCESS_ADD_CART_MESSAGE = (name) => `Đã thêm <b>${name}</b> vào giỏ hàng!`;
const FAILED_ADD_CART_MESSAGE  = "Đã có lỗi khi thêm vào giỏ hàng!";

// UTILS
async function _postAddToCart(productId) {
  try {
    const resp = await fetch("/cartItem", {
      method: "POST",
      headers: { "Accept": "application/json", "Content-Type": "application/json" },
      credentials: "same-origin",
      body: JSON.stringify({
        userId: currentUserIdMetaTag.content,
        productId: Number(productId),
        quantity: 1,
      }),
    });
    let body = {};
    try { body = await resp.json(); } catch (_) {}
    return [resp.status, body];
  } catch (e) {
    return [-1, {}];
  }
}

function _getProductName(btn) {
  return (
    btn.getAttribute("data-name") ||
    btn.closest("[data-name]")?.getAttribute("data-name") ||
    btn.closest(".product-item, .card, .product-card")
       ?.querySelector(".title, .product-title, h5, h6, .card-title")
       ?.textContent?.trim() ||
    "sản phẩm"
  );
}

function _setLoading(btn, loading) {
  if (!btn) return;
  if (loading) {
    btn._origHtml = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm" style="width:14px;height:14px;border-width:2px;"></span>';
  } else {
    btn.disabled = false;
    if (btn._origHtml) btn.innerHTML = btn._origHtml;
  }
}

async function handleAddToCart(btn) {
  const productId = btn.getAttribute("data-id");
  if (!productId) return;
  _setLoading(btn, true);
  try {
    const [status] = await _postAddToCart(productId);
    if (status === 200) {
      createToast(toastComponent(SUCCESS_ADD_CART_MESSAGE(_getProductName(btn)), "success"));
      refreshCartBadge(); // fetch actual count from server
    } else if (status === 401 || status === 403) {
      createToast(toastComponent(REQUIRED_SIGNIN_MESSAGE, "warning"));
    } else if (status === 422) {
      createToast(toastComponent("Sản phẩm đã hết hàng!", "danger"));
    } else {
      createToast(toastComponent(FAILED_ADD_CART_MESSAGE, "danger"));
    }
  } catch (_) {
    createToast(toastComponent(FAILED_ADD_CART_MESSAGE, "danger"));
  } finally {
    _setLoading(btn, false);
  }
}

async function handleBuyNow(btn) {
  const productId = btn.getAttribute("data-id");
  if (!productId) return;
  _setLoading(btn, true);
  try {
    const [status] = await _postAddToCart(productId);
    if (status === 200) {
      window.location.href = "/cart";
    } else if (status === 401 || status === 403) {
      createToast(toastComponent(REQUIRED_SIGNIN_MESSAGE, "warning"));
      _setLoading(btn, false);
    } else {
      createToast(toastComponent(FAILED_ADD_CART_MESSAGE, "danger"));
      _setLoading(btn, false);
    }
  } catch (_) {
    createToast(toastComponent(FAILED_ADD_CART_MESSAGE, "danger"));
    _setLoading(btn, false);
  }
}

// EVENT DELEGATION — catches static + dynamically added buttons
document.addEventListener("click", (e) => {
  const addBtn = e.target.closest(".add-cart-item");
  const buyBtn = e.target.closest(".buy-now");

  if (addBtn) {
    e.preventDefault();
    if (currentUserIdMetaTag) {
      handleAddToCart(addBtn);
    } else {
      createToast(toastComponent(REQUIRED_SIGNIN_MESSAGE, "warning"));
    }
    return;
  }

  if (buyBtn) {
    e.preventDefault();
    if (currentUserIdMetaTag) {
      handleBuyNow(buyBtn);
    } else {
      createToast(toastComponent(REQUIRED_SIGNIN_MESSAGE, "warning"));
    }
  }
});

// Export for use by other modules (wishlist add-to-cart, etc.)
export { handleAddToCart, handleBuyNow };
