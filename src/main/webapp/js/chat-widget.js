(function() {
    'use strict';

    // Avoid duplicate initialization
    if (document.getElementById('arthy-chat-widget')) return;

    // Detect context path
    const pathParts = window.location.pathname.split('/');
    const contextPath = pathParts.length > 1 && pathParts[1] ? '/' + pathParts[1] : '';

    // Create widget container
    const widget = document.createElement('div');
    widget.id = 'arthy-chat-widget';
    widget.innerHTML = `
        <style>
            #arthy-chat-widget {
                position: fixed;
                bottom: 24px;
                right: 24px;
                z-index: 9999;
                font-family: 'Segoe UI', Arial, sans-serif;
            }
            .arthy-chat-btn {
                width: 58px;
                height: 58px;
                border-radius: 50%;
                background: linear-gradient(135deg, #1565c0, #0288d1);
                color: #fff;
                border: none;
                box-shadow: 0 6px 20px rgba(21, 101, 192, 0.4);
                cursor: pointer;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 26px;
                transition: transform 0.25s, box-shadow 0.25s;
            }
            .arthy-chat-btn:hover {
                transform: scale(1.08);
                box-shadow: 0 8px 24px rgba(21, 101, 192, 0.55);
            }
            .arthy-chat-window {
                display: none;
                position: absolute;
                bottom: 70px;
                right: 0;
                width: 360px;
                max-width: calc(100vw - 32px);
                height: 500px;
                max-height: calc(100vh - 100px);
                background: #ffffff;
                border-radius: 16px;
                box-shadow: 0 12px 36px rgba(0,0,0,0.22);
                overflow: hidden;
                flex-direction: column;
                border: 1px solid #e0e7ff;
            }
            .arthy-chat-window.open {
                display: flex;
            }
            .arthy-chat-header {
                background: linear-gradient(135deg, #0d47a1, #1565c0);
                color: #ffffff;
                padding: 14px 16px;
                display: flex;
                align-items: center;
                justify-content: space-between;
            }
            .arthy-chat-title {
                display: flex;
                align-items: center;
                gap: 10px;
            }
            .arthy-chat-title h4 {
                margin: 0;
                font-size: 0.95rem;
                font-weight: 700;
            }
            .arthy-chat-status {
                font-size: 0.72rem;
                opacity: 0.85;
                display: flex;
                align-items: center;
                gap: 4px;
            }
            .arthy-chat-dot {
                width: 7px;
                height: 7px;
                border-radius: 50%;
                background: #69f0ae;
                display: inline-block;
            }
            .arthy-chat-close {
                background: transparent;
                border: none;
                color: #fff;
                font-size: 20px;
                cursor: pointer;
                line-height: 1;
            }
            .arthy-chat-messages {
                flex: 1;
                padding: 14px;
                overflow-y: auto;
                background: #f8faff;
                display: flex;
                flex-direction: column;
                gap: 12px;
            }
            .arthy-msg {
                max-width: 82%;
                padding: 10px 14px;
                border-radius: 14px;
                font-size: 0.88rem;
                line-height: 1.45;
                word-break: break-word;
            }
            .arthy-msg-bot {
                align-self: flex-start;
                background: #ffffff;
                color: #1e293b;
                border: 1px solid #e2e8f0;
                border-bottom-left-radius: 4px;
                box-shadow: 0 2px 6px rgba(0,0,0,0.04);
            }
            .arthy-msg-user {
                align-self: flex-end;
                background: #1565c0;
                color: #ffffff;
                border-bottom-right-radius: 4px;
            }
            .arthy-suggestions {
                display: flex;
                flex-wrap: wrap;
                gap: 6px;
                margin-top: 6px;
            }
            .arthy-pill {
                background: #e3f2fd;
                color: #0d47a1;
                border: 1px solid #bbdefb;
                border-radius: 99px;
                padding: 4px 10px;
                font-size: 0.75rem;
                font-weight: 600;
                cursor: pointer;
                transition: background 0.15s;
            }
            .arthy-pill:hover {
                background: #bbdefb;
            }
            .arthy-cards {
                display: flex;
                flex-direction: column;
                gap: 8px;
                margin-top: 8px;
            }
            .arthy-card-item {
                background: #ffffff;
                border: 1px solid #cbd5e1;
                border-radius: 8px;
                padding: 8px 10px;
                display: flex;
                align-items: center;
                gap: 10px;
                text-decoration: none;
                color: inherit;
                transition: border-color 0.2s, background 0.2s;
            }
            .arthy-card-item:hover {
                border-color: #1565c0;
                background: #f0f7ff;
            }
            .arthy-card-img {
                width: 42px;
                height: 42px;
                border-radius: 6px;
                object-fit: cover;
                background: #f1f5f9;
            }
            .arthy-card-details {
                flex: 1;
                min-width: 0;
            }
            .arthy-card-name {
                font-weight: 600;
                font-size: 0.8rem;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                color: #0f172a;
            }
            .arthy-card-price {
                font-size: 0.75rem;
                color: #1565c0;
                font-weight: 700;
            }
            .arthy-chat-input-area {
                padding: 10px 12px;
                background: #ffffff;
                border-top: 1px solid #e2e8f0;
                display: flex;
                gap: 8px;
            }
            .arthy-chat-input {
                flex: 1;
                border: 1px solid #cbd5e1;
                border-radius: 8px;
                padding: 8px 12px;
                font-size: 0.85rem;
                outline: none;
            }
            .arthy-chat-input:focus {
                border-color: #1565c0;
            }
            .arthy-chat-send {
                background: #1565c0;
                color: #ffffff;
                border: none;
                border-radius: 8px;
                padding: 8px 14px;
                font-weight: 600;
                cursor: pointer;
            }
            .arthy-chat-send:hover {
                background: #0d47a1;
            }
        </style>
        <button class="arthy-chat-btn" id="arthy-toggle-btn" title="Chat with Arthy AI">💬</button>
        <div class="arthy-chat-window" id="arthy-window">
            <div class="arthy-chat-header">
                <div class="arthy-chat-title">
                    <span style="font-size: 20px;">🤖</span>
                    <div>
                        <h4>Arthy AI Assistant</h4>
                        <div class="arthy-chat-status"><span class="arthy-chat-dot"></span> Online &amp; Ready</div>
                    </div>
                </div>
                <button class="arthy-chat-close" id="arthy-close-btn">&times;</button>
            </div>
            <div class="arthy-chat-messages" id="arthy-msgs"></div>
            <form class="arthy-chat-input-area" id="arthy-input-form">
                <input type="text" class="arthy-chat-input" id="arthy-text-input" placeholder="Ask about products, prices, stock..." autocomplete="off">
                <button type="submit" class="arthy-chat-send">Send</button>
            </form>
        </div>
    `;
    document.body.appendChild(widget);

    const toggleBtn = document.getElementById('arthy-toggle-btn');
    const chatWin = document.getElementById('arthy-window');
    const closeBtn = document.getElementById('arthy-close-btn');
    const msgsBox = document.getElementById('arthy-msgs');
    const form = document.getElementById('arthy-input-form');
    const input = document.getElementById('arthy-text-input');

    let initialLoaded = false;

    toggleBtn.addEventListener('click', () => {
        chatWin.classList.toggle('open');
        if (chatWin.classList.contains('open')) {
            input.focus();
            if (!initialLoaded) {
                sendMessage('hi', false);
                initialLoaded = true;
            }
        }
    });

    closeBtn.addEventListener('click', () => {
        chatWin.classList.remove('open');
    });

    form.addEventListener('submit', (e) => {
        e.preventDefault();
        const text = input.value.trim();
        if (!text) return;
        addMessage(text, 'user');
        input.value = '';
        sendMessage(text, true);
    });

    function addMessage(text, sender) {
        const div = document.createElement('div');
        div.className = 'arthy-msg arthy-msg-' + sender;
        div.textContent = text;
        msgsBox.appendChild(div);
        msgsBox.scrollTop = msgsBox.scrollHeight;
        return div;
    }

    async function sendMessage(message, isUserTriggered) {
        try {
            const res = await fetch(contextPath + '/api/chat', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message: message })
            });

            if (!res.ok) throw new Error('Network error');
            const data = await res.json();

            // Render Bot response
            const botMsg = document.createElement('div');
            botMsg.className = 'arthy-msg arthy-msg-bot';
            botMsg.innerHTML = `<div>${escapeHtml(data.reply || 'Here is what I found:')}</div>`;

            // If products are recommended, render cards
            if (data.products && data.products.length > 0) {
                const cardsContainer = document.createElement('div');
                cardsContainer.className = 'arthy-cards';
                data.products.forEach(p => {
                    const card = document.createElement('a');
                    card.className = 'arthy-card-item';
                    card.href = contextPath + '/product-detail.jsp?id=' + p.id;
                    const img = p.imageUrl ? p.imageUrl : 'https://placehold.co/80x80?text=Product';
                    card.innerHTML = `
                        <img src="${escapeHtml(img)}" class="arthy-card-img" alt="${escapeHtml(p.name)}">
                        <div class="arthy-card-details">
                            <div class="arthy-card-name">${escapeHtml(p.name)}</div>
                            <div class="arthy-card-price">₹${p.price} <span style="font-size:0.7rem; color:#64748b; font-weight:normal;">(${p.stockQty} in stock)</span></div>
                        </div>
                    `;
                    cardsContainer.appendChild(card);
                });
                botMsg.appendChild(cardsContainer);
            }

            // Quick suggestion pills
            if (data.suggestions && data.suggestions.length > 0) {
                const pillBox = document.createElement('div');
                pillBox.className = 'arthy-suggestions';
                data.suggestions.forEach(s => {
                    const pill = document.createElement('button');
                    pill.type = 'button';
                    pill.className = 'arthy-pill';
                    pill.textContent = s;
                    pill.onclick = () => {
                        addMessage(s, 'user');
                        sendMessage(s, true);
                    };
                    pillBox.appendChild(pill);
                });
                botMsg.appendChild(pillBox);
            }

            msgsBox.appendChild(botMsg);
            msgsBox.scrollTop = msgsBox.scrollHeight;

        } catch (err) {
            const errMsg = document.createElement('div');
            errMsg.className = 'arthy-msg arthy-msg-bot';
            errMsg.textContent = '⚠️ Arthy AI is currently offline. Please browse our product catalog directly.';
            msgsBox.appendChild(errMsg);
            msgsBox.scrollTop = msgsBox.scrollHeight;
        }
    }

    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
})();
