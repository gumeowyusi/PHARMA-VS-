const qs  = (s, r = document) => r.querySelector(s);
const qsa = (s, r = document) => Array.from(r.querySelectorAll(s));

const els = {
  toggle:   qs('#chat-toggle'),
  toggleIcon: qs('#chat-toggle-icon'),
  panel:    qs('#chat-panel'),
  close:    qs('#chat-close'),
  clear:    qs('#chat-clear'),
  messages: qs('#chat-messages'),
  typing:   qs('#chat-typing'),
  form:     qs('#chat-form'),
  input:    qs('#chat-text'),
  quick:    qs('#chat-quick-row'),
  sendBtn:  qs('.chat-send-btn'),
  unread:   qs('#chat-unread'),
};

let isOpen   = false;
let isBusy   = false;

/* ============================================================
   Simple markdown → HTML (bold, italic, code, bullet, link)
   ============================================================ */
function mdToHtml(text) {
  if (!text) return '';
  return text
    .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
    // code block
    .replace(/```[\s\S]*?```/g, m => `<pre><code>${m.slice(3, -3).trim()}</code></pre>`)
    // inline code
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    // bold
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    // italic
    .replace(/_(.+?)_/g, '<em>$1</em>')
    // bullet list
    .replace(/^[*\-] (.+)/gm, '<li>$1</li>')
    .replace(/(<li>[\s\S]+?<\/li>)/g, '<ul>$1</ul>')
    // numbered list
    .replace(/^\d+\. (.+)/gm, '<li>$1</li>')
    // links
    .replace(/\[([^\]]+)\]\((https?:\/\/[^\)]+)\)/g, '<a href="$2" target="_blank" rel="noopener">$1</a>')
    // line breaks
    .replace(/\n/g, '<br>');
}

/* ============================================================
   Append message bubble
   ============================================================ */
function appendMsg(text, author = 'bot') {
  const div = document.createElement('div');
  div.className = `msg ${author}`;
  if (author === 'bot') {
    div.innerHTML = mdToHtml(text);
  } else {
    div.textContent = text;
  }
  els.messages.appendChild(div);
  els.messages.scrollTop = els.messages.scrollHeight;
  return div;
}

/* ============================================================
   Typing indicator
   ============================================================ */
function showTyping(show) {
  if (els.typing) els.typing.style.display = show ? 'flex' : 'none';
  if (els.sendBtn) els.sendBtn.disabled = show;
}

/* ============================================================
   Call Gemini chatbot API
   ============================================================ */
async function askBot(text) {
  if (isBusy) return;
  isBusy = true;
  showTyping(true);
  try {
    const res  = await fetch('/api/chatbot/ask', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
      credentials: 'same-origin',
      body: JSON.stringify({ question: text }),
    });
    const json = await res.json();
    if (!res.ok) throw new Error(json.message || 'Lỗi máy chủ');
    appendMsg(json.answer || 'Xin lỗi, mình chưa rõ câu hỏi này.', 'bot');
  } catch (e) {
    appendMsg('Xin lỗi, trợ lý AI đang gặp sự cố. Vui lòng thử lại sau.', 'bot');
  } finally {
    showTyping(false);
    isBusy = false;
    els.messages.scrollTop = els.messages.scrollHeight;
  }
}

/* ============================================================
   Toggle panel open/close
   ============================================================ */
function togglePanel(show) {
  isOpen = show ?? !isOpen;
  if (isOpen) {
    els.panel?.removeAttribute('hidden');
    els.toggle?.setAttribute('aria-expanded', 'true');
    if (els.toggleIcon) { els.toggleIcon.className = 'fas fa-times'; }
    if (els.unread)     { els.unread.style.display = 'none'; }
    setTimeout(() => els.input?.focus(), 80);
  } else {
    els.panel?.setAttribute('hidden', '');
    els.toggle?.setAttribute('aria-expanded', 'false');
    if (els.toggleIcon) { els.toggleIcon.className = 'fas fa-robot'; }
  }
}

/* ============================================================
   Clear chat history
   ============================================================ */
function clearChat() {
  if (!els.messages) return;
  els.messages.innerHTML = `
    <div class="chat-welcome">
      <div class="chat-welcome-icon"><i class="fas fa-robot"></i></div>
      <div class="chat-welcome-text">
        Cuộc trò chuyện đã được xoá. Mình có thể giúp gì cho bạn? 😊
      </div>
    </div>`;
}

/* ============================================================
   Event listeners
   ============================================================ */
els.toggle?.addEventListener('click', () => togglePanel());
els.close?.addEventListener('click',  () => togglePanel(false));
els.clear?.addEventListener('click',  clearChat);

els.form?.addEventListener('submit', (e) => {
  e.preventDefault();
  const text = els.input?.value.trim();
  if (!text || isBusy) return;
  appendMsg(text, 'user');
  els.input.value = '';
  // hide quick chips after first message
  if (els.quick) els.quick.style.display = 'none';
  askBot(text);
});

// Quick chip clicks
els.quick && qsa('.chip', els.quick).forEach((chip) => {
  chip.addEventListener('click', () => {
    if (isBusy) return;
    const text = chip.getAttribute('data-text');
    if (!text) return;
    appendMsg(text, 'user');
    if (els.quick) els.quick.style.display = 'none';
    askBot(text);
  });
});

// Keyboard shortcut: Escape to close
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape' && isOpen) togglePanel(false);
});

// Show unread badge if panel never opened after 3s
setTimeout(() => {
  if (!isOpen && els.unread) {
    els.unread.style.display = 'flex';
  }
}, 3000);
