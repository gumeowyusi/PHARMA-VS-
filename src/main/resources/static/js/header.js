// STATIC DATA
const currentUserIdMetaTag = document.querySelector(
  "meta[name='currentUserId']"
);

// Search autocomplete/history
function _getSearchInputs() {
  return Array.from(
    document.querySelectorAll(
      ".site-header input[name='q'], #offcanvasMenu input[name='q']"
    )
  );
}
const SEARCH_HISTORY_KEY = currentUserIdMetaTag
  ? `search_history_${currentUserIdMetaTag.content}`
  : "search_history_guest_v1";

function _loadSearchHistory() {
  try {
    const raw = localStorage.getItem(SEARCH_HISTORY_KEY);
    if (!raw) return [];
    const arr = JSON.parse(raw);
    if (!Array.isArray(arr)) return [];
    return arr.filter((x) => typeof x === "string").slice(0, 10);
  } catch {
    return [];
  }
}

function _saveSearchHistory(history) {
  try {
    localStorage.setItem(SEARCH_HISTORY_KEY, JSON.stringify(history.slice(0, 10)));
  } catch {
    // ignore
  }
}

function _pushSearchHistory(term) {
  const trimmed = (term || "").trim();
  if (!trimmed) return;
  const history = _loadSearchHistory();
  const normalized = trimmed.toLowerCase();
  const next = [
    trimmed,
    ...history.filter((h) => (h || "").toLowerCase() !== normalized),
  ].slice(0, 10);
  _saveSearchHistory(next);
}

async function _fetchSearchSuggestions(prefix) {
  const p = (prefix || "").trim();
  if (!p) return [];
  const resp = await fetch(
    `/api/search/suggestions?prefix=${encodeURIComponent(p)}`,
    { method: "GET", headers: { Accept: "application/json" } }
  );
  if (!resp.ok) return [];
  return await resp.json();
}

function _mergeHistoryFirst(history, suggestions, prefix) {
  const p = (prefix || "").trim().toLowerCase();
  const historyMatches = history
    .filter((t) => t.toLowerCase().startsWith(p))
    .slice(0, 5)
    .map((t) => ({ tenSanpham: t, idSanpham: null, source: "history" }));

  const productMatches = (suggestions || [])
    .map((s) => ({ tenSanpham: s.tenSanpham, idSanpham: s.idSanpham, source: "product" }))
    .slice(0, 10);

  const seen = new Set();
  const res = [];
  for (const item of [...historyMatches, ...productMatches]) {
    const key = (item.tenSanpham || "").toLowerCase();
    if (!key || seen.has(key)) continue;
    seen.add(key);
    res.push(item);
  }
  return res;
}

const _autocompleteMeta = new WeakMap(); // input -> { rootEl, timer, lastValue }

function _ensureAutocompleteRoot(inputEl) {
  let meta = _autocompleteMeta.get(inputEl);
  if (meta?.rootEl) return meta.rootEl;

  const wrapper =
    inputEl.closest(".search-wrapper") ||
    inputEl.closest("form") ||
    inputEl.parentElement;

  if (wrapper && wrapper.style) wrapper.style.position = "relative";

  const root = document.createElement("div");
  root.className = "search-autocomplete";
  root.style.display = "none";
  wrapper?.appendChild(root);

  meta = { rootEl: root, timer: null, lastValue: "" };
  _autocompleteMeta.set(inputEl, meta);
  return root;
}

function _renderAutocomplete(inputEl, rootEl, items) {
  rootEl.innerHTML = "";
  if (!items || items.length === 0) {
    rootEl.style.display = "none";
    return;
  }
  rootEl.style.display = "block";

  for (const item of items) {
    const btn = document.createElement("button");
    btn.type = "button";
    btn.className = "search-suggestion-item";
    btn.textContent = item.tenSanpham;

    btn.addEventListener("mousedown", (e) => {
      // dùng mousedown để tránh blur làm mất dropdown trước khi click
      e.preventDefault();
      inputEl.value = item.tenSanpham;
      rootEl.style.display = "none";
      const form = inputEl.closest("form");
      if (form && form.getAttribute("action") === "/search") {
        _pushSearchHistory(item.tenSanpham);
        form.submit();
      } else {
        // fallback
        _pushSearchHistory(item.tenSanpham);
        inputEl.form?.submit?.();
      }
    });
    rootEl.appendChild(btn);
  }
}

async function _onSearchInput(inputEl) {
  const meta = _autocompleteMeta.get(inputEl);
  const rootEl = _ensureAutocompleteRoot(inputEl);
  const value = (inputEl.value || "").trim();
  meta.lastValue = value;

  const history = _loadSearchHistory();
  if (!value) {
    const merged = _mergeHistoryFirst(history, [], "");
    _renderAutocomplete(inputEl, rootEl, merged);
    return;
  }

  const suggestions = await _fetchSearchSuggestions(value);
  // Chặn trường hợp user gõ tiếp trước khi fetch xong
  if (meta.lastValue !== value) return;

  const merged = _mergeHistoryFirst(history, suggestions, value);
  _renderAutocomplete(inputEl, rootEl, merged);
}

// Badge element re-queried each time to handle late DOM rendering
function _getBadgeEl() {
  return document.querySelector("#total-cart-items-quantity") ||
         document.querySelector("#cartBadge");
}

// UTILS
async function _fetchGetCart() {
  const url = currentUserIdMetaTag
    ? "/cartItem?userId=" + currentUserIdMetaTag.content
    : "/guest/cartItem";
  const response = await fetch(url, {
    method: "GET",
    headers: { Accept: "application/json", "Content-Type": "application/json" },
    credentials: "same-origin",
  });
  return [response.status, await response.json()];
}

// STATE
const state = {
  totalCartItemsQuantity: 0,
  setTotalCartItemsQuantity: (value) => {
    if (typeof value === "string") {
      state.totalCartItemsQuantity += Number(value);
    } else {
      state.totalCartItemsQuantity = value.cartItems
        .map((cartItem) => cartItem.quantity)
        .reduce(
          (partialSum, cartItemQuantity) => partialSum + cartItemQuantity,
          0
        );
    }
    render();
  },
  initState: async () => {
    const [status, data] = await _fetchGetCart();
    if (status === 200) {
      state.setTotalCartItemsQuantity(data);
    }
  },
};

// RENDER
function render() {
  const el = _getBadgeEl();
  if (el) {
    const n = state.totalCartItemsQuantity;
    el.textContent = n;
    // hide badge when 0, show when > 0
    el.style.display = n > 0 ? "" : "none";
  }
  document.dispatchEvent(
    new CustomEvent("cart:badgeUpdated", {
      detail: { count: state.totalCartItemsQuantity },
    })
  );
}

// MAIN
void state.initState();

export const setTotalCartItemsQuantity = state.setTotalCartItemsQuantity;

/** Fetch actual cart count from server and update badge — always accurate */
export async function refreshCartBadge() {
  try {
    const [status, data] = await _fetchGetCart();
    if (status === 200) {
      state.setTotalCartItemsQuantity(data);
    }
  } catch (_) {}
}

// Bind search autocomplete/history (robust init)
const _boundSearchInputs = new WeakSet();
const _boundSearchForms = new WeakSet();

function _bindSearchAutocomplete() {
  _getSearchInputs().forEach((inputEl) => {
    if (_boundSearchInputs.has(inputEl)) return;
    _boundSearchInputs.add(inputEl);

    _ensureAutocompleteRoot(inputEl);

    inputEl.addEventListener("focus", () => {
      const meta = _autocompleteMeta.get(inputEl);
      meta?.timer && clearTimeout(meta.timer);
      void _onSearchInput(inputEl); // show history / suggestions
    });

    inputEl.addEventListener("input", () => {
      const meta = _autocompleteMeta.get(inputEl);
      if (meta?.timer) clearTimeout(meta.timer);
      if (meta) {
        meta.timer = setTimeout(() => void _onSearchInput(inputEl), 120);
      } else {
        setTimeout(() => void _onSearchInput(inputEl), 120);
      }
    });

    inputEl.addEventListener("blur", () => {
      const meta = _autocompleteMeta.get(inputEl);
      setTimeout(() => {
        if (meta?.rootEl) meta.rootEl.style.display = "none";
      }, 180);
    });
  });

  document.querySelectorAll("form").forEach((formEl) => {
    const qInput = formEl.querySelector("input[name='q']");
    if (!qInput || _boundSearchForms.has(formEl)) return;
    _boundSearchForms.add(formEl);
    formEl.addEventListener("submit", () => _pushSearchHistory(qInput.value));
  });
}

// init now + after DOM ready
_bindSearchAutocomplete();
document.addEventListener("DOMContentLoaded", _bindSearchAutocomplete);
