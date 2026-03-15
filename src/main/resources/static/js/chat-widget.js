const qs = (s, r = document) => r.querySelector(s);
const qsa = (s, r = document) => Array.from(r.querySelectorAll(s));

const els = {
  toggle: qs('#chat-toggle'),
  panel: qs('#chat-panel'),
  close: qs('#chat-close'),
  messages: qs('#chat-messages'),
  form: qs('#chat-form'),
  input: qs('#chat-text'),
  quick: qs('.chat-quick'),
  handoff: qs('#chat-handoff'),
};

if (els.toggle) {
  els.toggle.setAttribute('aria-controls', 'chat-panel');
  if (!els.toggle.hasAttribute('aria-expanded')) {
    els.toggle.setAttribute('aria-expanded', 'false');
  }
}

function appendMsg(text, author = 'bot') {
  const div = document.createElement('div');
  div.className = `msg ${author}`;
  div.textContent = text;
  els.messages.appendChild(div);
  els.messages.scrollTop = els.messages.scrollHeight;
}

async function askBot(text) {
  try {
    const res = await fetch('/api/chatbot/ask', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
      credentials: 'same-origin',
      body: JSON.stringify({ question: text }),
    });
    const json = await res.json();
    if (!res.ok) throw new Error(json.message || 'Lỗi máy chủ');
    appendMsg(json.answer || 'Xin lỗi, mình chưa rõ câu hỏi này.');
  } catch (e) {
    appendMsg('Xin lỗi, hiện tại không thể trả lời. Vui lòng thử lại sau.', 'bot');
  }
}

function togglePanel(show) {
  const wantOpen = show ?? els.panel.hasAttribute('hidden');
  if (wantOpen) {
    els.panel.removeAttribute('hidden');
    els.toggle?.setAttribute('aria-expanded', 'true');
    setTimeout(() => els.input?.focus(), 50);
  } else {
    els.panel.setAttribute('hidden', '');
    els.toggle?.setAttribute('aria-expanded', 'false');
  }
}

// Events
els.toggle?.addEventListener('click', () => togglePanel(true));
els.close?.addEventListener('click', () => togglePanel(false));
els.form?.addEventListener('submit', (e) => {
  e.preventDefault();
  const text = els.input.value.trim();
  if (!text) return;
  appendMsg(text, 'user');
  els.input.value = '';
  askBot(text);
});

els.quick && qsa('.chip', els.quick).forEach((chip) => {
  chip.addEventListener('click', () => {
    const text = chip.getAttribute('data-text');
    if (!text) return;
    appendMsg(text, 'user');
    askBot(text);
  });
});

els.handoff?.addEventListener('click', async () => {
  appendMsg('Đang kết nối nhân viên hỗ trợ...', 'bot');
  try {
    const res = await fetch('/api/support/session', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
      credentials: 'same-origin',
      body: JSON.stringify({}),
    });
    const json = await res.json();
    if (!res.ok) throw new Error(json.message || 'Lỗi tạo phiên hỗ trợ');
    appendMsg('Đã tạo yêu cầu. Nhân viên sẽ phản hồi trong ít phút. Mã phiên: ' + json.ticketId, 'bot');
  } catch (e) {
    appendMsg('Xin lỗi, không thể kết nối nhân viên lúc này. Vui lòng thử lại sau.', 'bot');
  }
});
