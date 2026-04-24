// COMPONENTS
export function toastComponent(message, color = "primary") {
  return `
    <div class="toast align-items-center text-white bg-${color} border-0" role="alert" aria-live="assertive" aria-atomic="true">
      <div class="d-flex">
        <div class="toast-body">
          ${message}
        </div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    </div>
  `;
}

// UTILS
function _getOrCreateContainer() {
  let el = document.querySelector(".toast-container");
  if (!el) {
    el = document.createElement("div");
    el.className = "toast-container position-fixed bottom-0 start-0 p-3";
    el.style.cssText = "z-index:99999;";
    document.body.appendChild(el);
  }
  return el;
}

function _showToast() {
  // Remove already-dismissed toasts
  document.querySelectorAll(".toast.hide").forEach((el) => el.remove());

  document.querySelectorAll(".toast:not(.hide)").forEach((toastEl) => {
    try {
      const t = bootstrap.Toast.getInstance(toastEl) || new bootstrap.Toast(toastEl, { delay: 3500 });
      t.show();
    } catch (_) {}
  });
}

// MAIN
function createToast(html) {
  try {
    const container = _getOrCreateContainer();
    container.insertAdjacentHTML("beforeend", html);
    _showToast();
  } catch (_) {}
}

export default createToast;
