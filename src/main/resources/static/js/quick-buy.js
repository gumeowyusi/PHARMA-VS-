import createToast, { toastComponent } from "./toast.js";
import { setTotalCartItemsQuantity } from "./header.js";

const currentUserIdMetaTag = document.querySelector(
  "meta[name='currentUserId']"
);

const REQUIRED_SIGNIN_MESSAGE = "Vui lòng đăng nhập để thực hiện thao tác!";
const SUCCESS_ADD_CART_ITEM_MESSAGE = (quantity, name) =>
  `Đã thêm ${quantity} sản phẩm "${name}" vào giỏ hàng!`;
const FAILED_ADD_CART_ITEM_MESSAGE = "Đã có lỗi truy vấn!";

async function addToCartLoggedIn(productId, quantity = 1) {
  const cartItemRequest = {
    userId: currentUserIdMetaTag.content,
    productId: productId,
    quantity,
  };
  const res = await fetch("/cartItem", {
    method: "POST",
    headers: { Accept: "application/json", "Content-Type": "application/json" },
    credentials: "same-origin",
    body: JSON.stringify(cartItemRequest),
  });
  return [res.status, await res.json()];
}

async function addToCartGuest(productId, quantity = 1) {
  // ensure guest cart cookie
  await fetch("/guest/cart", { method: "POST", credentials: "same-origin" });
  const guestReq = { productId: Number(productId), quantity };
  const res = await fetch("/guest/cartItem", {
    method: "POST",
    headers: { Accept: "application/json", "Content-Type": "application/json" },
    credentials: "same-origin",
    body: JSON.stringify(guestReq),
  });
  return [res.status, await res.json()];
}

function getQtyFromButton(btn) {
  const cardBody = btn.closest(".card-body");
  const input = cardBody ? cardBody.querySelector(".qty-input") : null;
  const v = parseInt(input?.value || "1", 10);
  return isNaN(v) || v < 1 ? 1 : v;
}

async function handleAdd(btn) {
  const productId = btn.getAttribute("data-id");
  const name = btn.getAttribute("data-name") || "Sản phẩm";
  const qty = getQtyFromButton(btn);
  const prevDisabled = btn.disabled;
  btn.disabled = true;
  const [status] = currentUserIdMetaTag
    ? await addToCartLoggedIn(productId, qty)
    : await addToCartGuest(productId, qty);

  if (status === 200) {
    createToast(
      toastComponent(SUCCESS_ADD_CART_ITEM_MESSAGE(qty, name), "success")
    );
    setTotalCartItemsQuantity(String(qty));
  } else if (status === 404) {
    createToast(toastComponent(FAILED_ADD_CART_ITEM_MESSAGE, "danger"));
  }
  btn.disabled = prevDisabled;
}

async function handleBuyNow(btn) {
  const productId = btn.getAttribute("data-id");
  const qty = getQtyFromButton(btn);
  const prevDisabled = btn.disabled;
  btn.disabled = true;
  const [status] = currentUserIdMetaTag
    ? await addToCartLoggedIn(productId, qty)
    : await addToCartGuest(productId, qty);
  if (status === 200) {
    window.location.href = "/cart";
  } else {
    createToast(toastComponent(FAILED_ADD_CART_ITEM_MESSAGE, "danger"));
  }
  btn.disabled = prevDisabled;
}

function main() {
  const addBtns = document.querySelectorAll(".add-cart-item");
  const buyBtns = document.querySelectorAll(".buy-now");
  const grid = document.getElementById("quickGrid");
  const chipsWrap = document.getElementById("catChips");
  const countEl = document.getElementById("resultCount");
  const noResults = document.getElementById("noResults");
  const searchInput = document.getElementById("quickSearch");
  const priceChips = document.getElementById("priceChips");
  const sortSelect = document.getElementById("sortSelect");
  const viewListBtn = document.getElementById("viewList");
  const viewGridBtn = document.getElementById("viewGrid");
  const clearBtn = document.getElementById("clearSearch");

  // have no clue cuz sometimes bootstrap fucked up so ye xD
  try {
    if (window.bootstrap) {
      document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach((el) => {
        // eslint-disable-next-line no-new
        new bootstrap.Tooltip(el);
      });
    }
  } catch (_) {
    // ignore if bootstrap not present
  }

  // Allow guests to add/buy using guest cart flow
  addBtns.forEach((btn) => btn.addEventListener("click", () => handleAdd(btn)));
  buyBtns.forEach((btn) =>
    btn.addEventListener("click", () => handleBuyNow(btn))
  );

  // Filters: text + category chips
  let activeCat = "all";
  let priceRange = "all"; // format: "min-max" or "min-"

  const applyFilters = () => {
    const q = (searchInput?.value || "").trim().toLowerCase();
    const cards = grid ? grid.querySelectorAll("[data-pname]") : [];
    let visible = 0;
    cards.forEach((card) => {
      const name = (card.getAttribute("data-pname") || "").toLowerCase();
      const catId = card.getAttribute("data-loaiid");
      const price = Number(card.getAttribute("data-price")) || 0;
      const matchText = !q || name.includes(q);
      const matchCat = activeCat === "all" || catId === String(activeCat);
      let matchPrice = true;
      if (priceRange !== "all") {
        const [minStr, maxStr] = priceRange.split("-");
        const min = minStr ? Number(minStr) : 0;
        const max = maxStr ? Number(maxStr) : Infinity;
        matchPrice = price >= min && price <= max;
      }
      const show = matchText && matchCat && matchPrice;
      card.style.display = show ? "" : "none";
      if (show) visible++;
    });
    if (countEl)
      countEl.textContent = visible > 0 ? `${visible} kết quả` : "0 kết quả";
    if (noResults) noResults.style.display = visible === 0 ? "" : "none";
  };

  // Debounce search input
  let t;
  if (searchInput) {
    searchInput.addEventListener("input", () => {
      clearTimeout(t);
      t = setTimeout(applyFilters, 160);
    });
    if (clearBtn) {
      clearBtn.addEventListener("click", () => {
        searchInput.value = "";
        applyFilters();
        searchInput.focus();
      });
    }
  }

  // Category chips
  if (chipsWrap) {
    chipsWrap.addEventListener("click", (e) => {
      const chip = e.target.closest(".chip");
      if (!chip) return;
      chipsWrap.querySelectorAll(".chip").forEach((c) => {
        c.classList.remove("active");
        c.setAttribute("aria-pressed", "false");
      });
      chip.classList.add("active");
      chip.setAttribute("aria-pressed", "true");
      activeCat = chip.getAttribute("data-cat") || "all";
      applyFilters();
    });
  }

  // Price chips
  if (priceChips) {
    priceChips.addEventListener("click", (e) => {
      const chip = e.target.closest(".chip");
      if (!chip) return;
      priceChips.querySelectorAll(".chip").forEach((c) => {
        c.classList.remove("active");
        c.setAttribute("aria-pressed", "false");
      });
      chip.classList.add("active");
      chip.setAttribute("aria-pressed", "true");
      priceRange = chip.getAttribute("data-range") || "all";
      applyFilters();
    });
  }

  // Sorting
  const sortCards = () => {
    if (!grid) return;
    const cards = Array.from(grid.children);
    const value = sortSelect?.value || "default";
    let sorted = cards.slice();
    if (value === "priceAsc" || value === "priceDesc") {
      sorted.sort(
        (a, b) =>
          Number(a.getAttribute("data-price")) -
          Number(b.getAttribute("data-price"))
      );
      if (value === "priceDesc") sorted.reverse();
    } else if (value === "nameAsc") {
      sorted.sort((a, b) =>
        String(a.getAttribute("data-pname") || "").localeCompare(
          String(b.getAttribute("data-pname") || "")
        )
      );
    }
    sorted.forEach((el) => grid.appendChild(el));
  };
  if (sortSelect)
    sortSelect.addEventListener("change", () => {
      sortCards();
    });

  // View toggle
  const setView = (mode) => {
    if (!grid) return;
    if (mode === "list") {
      grid.classList.add("view-list");
      viewListBtn?.classList.add("active");
      viewListBtn?.setAttribute("aria-pressed", "true");
      viewGridBtn?.classList.remove("active");
      viewGridBtn?.setAttribute("aria-pressed", "false");
    } else {
      grid.classList.remove("view-list");
      viewGridBtn?.classList.add("active");
      viewGridBtn?.setAttribute("aria-pressed", "true");
      viewListBtn?.classList.remove("active");
      viewListBtn?.setAttribute("aria-pressed", "false");
    }
  };
  viewListBtn?.addEventListener("click", () => setView("list"));
  viewGridBtn?.addEventListener("click", () => setView("grid"));

  // Initial count
  applyFilters();
  sortCards();

  const updateScrollFor = (target, leftBtn, rightBtn) => {
    if (!target) return;
    const overflow = target.scrollWidth > target.clientWidth + 2;
    if (!overflow) {
      leftBtn?.classList.add("hidden");
      rightBtn?.classList.add("hidden");
      return;
    }
    const atStart = target.scrollLeft <= 4;
    const atEnd =
      target.scrollLeft + target.clientWidth >= target.scrollWidth - 4;
    if (leftBtn) leftBtn.classList.toggle("hidden", atStart);
    if (rightBtn) rightBtn.classList.toggle("hidden", atEnd);
  };

  const bindScrollControls = () => {
    document.querySelectorAll(".scroll-wrap").forEach((wrap) => {
      const row = wrap.querySelector(".chips-row");
      if (!row || !row.id) return;
      const leftBtn = wrap.querySelector(".scroll-btn.left");
      const rightBtn = wrap.querySelector(".scroll-btn.right");
      // Click handlers
      [leftBtn, rightBtn].forEach((btn) => {
        if (!btn) return;
        const dir = btn.classList.contains("left") ? -1 : 1;
        if (!btn._qb_bound) {
          btn._qb_bound = true;
          btn.addEventListener("click", () => {
            row.scrollBy({ left: dir * 200, behavior: "smooth" });
          });
        }
      });
      // Update on scroll/resize
      const update = () => updateScrollFor(row, leftBtn, rightBtn);
      if (!row._qb_scroll_bound) {
        row._qb_scroll_bound = true;
        row.addEventListener("scroll", update);
      }
      update();
    });
  };

  bindScrollControls();
  window.addEventListener("resize", () => {
    document.querySelectorAll(".scroll-wrap").forEach((wrap) => {
      const row = wrap.querySelector(".chips-row");
      const leftBtn = wrap.querySelector(".scroll-btn.left");
      const rightBtn = wrap.querySelector(".scroll-btn.right");
      updateScrollFor(row, leftBtn, rightBtn);
    });
  });
}

document.addEventListener("DOMContentLoaded", main);
