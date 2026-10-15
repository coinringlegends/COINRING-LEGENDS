const fs = require('fs');
const path = require('path');

const widgetSnippet = `
  <!-- ========================================== -->
  <!-- COINRING LEGENDS™ LUXURY AI SUPPORT CHAT   -->
  <!-- ========================================== -->
  <style>
    #coinring-support-widget {
      position: fixed;
      bottom: 24px;
      right: 24px;
      z-index: 99999;
      font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
    }
    #coinring-chat-btn {
      display: flex;
      align-items: center;
      gap: 10px;
      background: linear-gradient(135deg, #1A1713 0%, #0A0A0A 100%);
      border: 1px solid rgba(212, 183, 143, 0.4);
      color: #D4B78F;
      padding: 12px 18px;
      border-radius: 9999px;
      cursor: pointer;
      box-shadow: 0 10px 25px rgba(0,0,0,0.7), 0 0 15px rgba(212, 183, 143, 0.2);
      transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
    }
    #coinring-chat-btn:hover {
      transform: translateY(-2px);
      border-color: #D4B78F;
      box-shadow: 0 14px 30px rgba(0,0,0,0.85), 0 0 22px rgba(212, 183, 143, 0.35);
    }
    .cr-pulse-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: #10B981;
      box-shadow: 0 0 8px #10B981;
      animation: crPulse 2s infinite;
    }
    @keyframes crPulse {
      0% { opacity: 0.6; transform: scale(0.95); }
      50% { opacity: 1; transform: scale(1.1); }
      100% { opacity: 0.6; transform: scale(0.95); }
    }
    #coinring-chat-box {
      display: none;
      position: fixed;
      bottom: 84px;
      right: 24px;
      width: 380px;
      max-width: calc(100vw - 32px);
      height: 520px;
      max-height: calc(100vh - 110px);
      background: #0D0D0D;
      border: 1px solid rgba(212, 183, 143, 0.35);
      border-radius: 18px;
      box-shadow: 0 25px 60px rgba(0, 0, 0, 0.9), 0 0 30px rgba(212, 183, 143, 0.15);
      flex-direction: column;
      overflow: hidden;
      z-index: 100000;
      animation: crSlideUp 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards;
    }
    @keyframes crSlideUp {
      from { opacity: 0; transform: translateY(20px) scale(0.98); }
      to { opacity: 1; transform: translateY(0) scale(1); }
    }
    .cr-chat-header {
      background: linear-gradient(135deg, #171512 0%, #0A0A0A 100%);
      border-bottom: 1px solid rgba(212, 183, 143, 0.2);
      padding: 16px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .cr-chat-title {
      font-family: 'Cormorant Garamond', Georgia, serif;
      font-size: 1.15rem;
      letter-spacing: 0.15em;
      color: #D4B78F;
      font-weight: 600;
      text-transform: uppercase;
      margin: 0;
    }
    .cr-chat-subtitle {
      font-size: 0.725rem;
      color: rgba(255, 255, 255, 0.5);
      letter-spacing: 0.05em;
      display: flex;
      align-items: center;
      gap: 5px;
      margin-top: 2px;
    }
    .cr-header-actions {
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .cr-head-btn {
      background: rgba(255, 255, 255, 0.06);
      border: 1px solid rgba(212, 183, 143, 0.2);
      color: #D4B78F;
      width: 32px;
      height: 32px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      text-decoration: none;
      transition: all 0.2s;
    }
    .cr-head-btn:hover {
      background: rgba(212, 183, 143, 0.15);
      border-color: #D4B78F;
    }
    .cr-chat-messages {
      flex: 1;
      padding: 16px;
      overflow-y: auto;
      display: flex;
      flex-direction: column;
      gap: 12px;
      background: #0A0A0A;
    }
    .cr-msg {
      max-width: 86%;
      padding: 10px 14px;
      border-radius: 14px;
      font-size: 0.85rem;
      line-height: 1.45;
      word-break: break-word;
    }
    .cr-msg-bot {
      align-self: flex-start;
      background: #141414;
      border: 1px solid rgba(212, 183, 143, 0.2);
      color: #F3F4F6;
      border-bottom-left-radius: 4px;
    }
    .cr-msg-user {
      align-self: flex-end;
      background: linear-gradient(135deg, #D4B78F 0%, #C9A86C 100%);
      color: #0A0A0A;
      font-weight: 500;
      border-bottom-right-radius: 4px;
    }
    .cr-pills {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-top: 6px;
    }
    .cr-pill {
      background: rgba(212, 183, 143, 0.08);
      border: 1px solid rgba(212, 183, 143, 0.25);
      color: #D4B78F;
      padding: 5px 10px;
      border-radius: 9999px;
      font-size: 0.72rem;
      cursor: pointer;
      transition: all 0.2s;
    }
    .cr-pill:hover {
      background: rgba(212, 183, 143, 0.2);
      border-color: #D4B78F;
    }
    .cr-chat-input-bar {
      padding: 12px;
      background: #121212;
      border-top: 1px solid rgba(212, 183, 143, 0.15);
      display: flex;
      gap: 8px;
    }
    .cr-input {
      flex: 1;
      background: #1A1A1A;
      border: 1px solid rgba(212, 183, 143, 0.2);
      border-radius: 10px;
      padding: 10px 14px;
      color: #FFFFFF;
      font-size: 0.85rem;
      outline: none;
    }
    .cr-input:focus {
      border-color: #D4B78F;
    }
    .cr-send-btn {
      background: #D4B78F;
      border: none;
      color: #0A0A0A;
      width: 40px;
      height: 40px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      font-weight: bold;
      transition: opacity 0.2s;
    }
    .cr-send-btn:hover {
      opacity: 0.9;
    }
    .cr-typing {
      font-size: 0.75rem;
      color: #D4B78F;
      font-style: italic;
      padding: 4px 8px;
      display: none;
    }
    .cr-footer-note {
      font-size: 0.65rem;
      color: rgba(212, 183, 143, 0.5);
      text-align: center;
      padding: 6px 0 2px 0;
      letter-spacing: 0.05em;
    }
  </style>

  <div id="coinring-support-widget">
    <!-- Floating Launcher -->
    <div id="coinring-chat-btn" onclick="toggleCoinringChat()">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
      </svg>
      <span style="font-size:0.8rem;letter-spacing:0.08em;font-weight:500;">Support & Atelier</span>
      <div class="cr-pulse-dot"></div>
    </div>

    <!-- Chat Window -->
    <div id="coinring-chat-box">
      <div class="cr-chat-header">
        <div>
          <h3 class="cr-chat-title">COINRING LEGENDS™</h3>
          <div class="cr-chat-subtitle">
            <div class="cr-pulse-dot" style="width:6px;height:6px;"></div>
            <span>Atelier Berater · Online</span>
          </div>
        </div>
        <div class="cr-header-actions">
          <a class="cr-head-btn" href="https://wa.me/41798339387?text=Hallo%20COINRING%20LEGENDS,%20ich%20habe%20eine%20Frage." target="_blank" rel="noopener noreferrer" title="WhatsApp 079 833 93 87">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12.04 2c-5.46 0-9.91 4.45-9.91 9.91 0 1.75.46 3.45 1.32 4.95L2 22l5.25-1.38c1.45.79 3.08 1.21 4.79 1.21h.01c5.46 0 9.91-4.45 9.91-9.91C21.96 6.45 17.5 2 12.04 2zm5.8 14.13c-.24.68-1.42 1.31-1.96 1.36-.5.05-1.14.07-1.84-.12-.42-.13-.97-.31-1.66-.61-2.92-1.26-4.83-4.2-4.98-4.39-.15-.2-1.18-1.57-1.18-3 0-1.43.75-2.13 1.02-2.42.27-.29.59-.36.79-.36.2 0 .39.002.56.01.18.008.42-.07.66.5.24.58.82 2 .89 2.15.07.15.12.32.02.52-.1.2-.15.32-.29.49-.15.17-.31.39-.44.52-.15.15-.3.31-.13.6.17.29.76 1.25 1.63 2.02 1.12 1 2.06 1.31 2.34 1.46.29.15.46.13.63-.08.17-.2.73-.85.93-1.14.2-.29.39-.24.66-.15.27.1 1.7.8 1.99.95.29.15.49.22.56.34.07.12.07.68-.17 1.36z"/>
            </svg>
          </a>
          <button class="cr-head-btn" onclick="toggleCoinringChat()" title="Schliessen">✕</button>
        </div>
      </div>

      <div class="cr-chat-messages" id="cr-messages">
        <div class="cr-msg cr-msg-bot">
          <strong>Grüezi!</strong> Willkommen im COINRING LEGENDS™ Atelier. Wie kann ich dir behilflich sein?
          <div class="cr-pills">
            <span class="cr-pill" onclick="sendQuickPrompt('Wie bezahle ich per TWINT?')">💰 TWINT 076 322 79 81</span>
            <span class="cr-pill" onclick="sendQuickPrompt('Wie lange ist die Lieferzeit?')">🚚 Lieferzeit</span>
            <span class="cr-pill" onclick="sendQuickPrompt('Wie messe ich meine Ringgrösse?')">📏 Ringgrösse 48–72</span>
            <span class="cr-pill" onclick="sendQuickPrompt('Kann ich eine eigene Münze einsenden?')">🪙 Eigene Münze</span>
            <span class="cr-pill" onclick="sendQuickPrompt('Gibt es eine Geschenkverpackung?')">🎁 Geschenkoption</span>
          </div>
        </div>
        <div class="cr-typing" id="cr-typing">Atelier tippt...</div>
      </div>

      <div class="cr-chat-input-bar">
        <input type="text" id="cr-input" class="cr-input" placeholder="Frage an den Berater stellen..." onkeydown="if(event.key==='Enter')sendUserMessage()"/>
        <button class="cr-send-btn" onclick="sendUserMessage()" title="Senden">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <line x1="22" y1="2" x2="11" y2="13"></line>
            <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
          </svg>
        </button>
      </div>
      <div class="cr-footer-note">TWINT: 076 322 79 81 · WhatsApp: 079 833 93 87</div>
    </div>
  </div>

  <script>
    var isChatOpen = false;
    var clientId = 'client_' + (localStorage.getItem('coinring_client_id') || Math.random().toString(36).substring(2, 9));
    localStorage.setItem('coinring_client_id', clientId);

    function toggleCoinringChat() {
      isChatOpen = !isChatOpen;
      var box = document.getElementById('coinring-chat-box');
      if (box) {
        box.style.display = isChatOpen ? 'flex' : 'none';
        if (isChatOpen) {
          setTimeout(function() {
            var inp = document.getElementById('cr-input');
            if (inp) inp.focus();
            scrollChatToBottom();
          }, 100);
        }
      }
    }

    function scrollChatToBottom() {
      var msgs = document.getElementById('cr-messages');
      if (msgs) msgs.scrollTop = msgs.scrollHeight;
    }

    function sendQuickPrompt(text) {
      var inp = document.getElementById('cr-input');
      if (inp) {
        inp.value = text;
        sendUserMessage();
      }
    }

    function getLocalSmartReply(q) {
      var text = (q || '').toLowerCase();
      if (text.includes('twint') || text.includes('zahl') || text.includes('bezahl') || text.includes('konto')) {
        return 'Die TWINT-Zahlung erfolgt direkt an <strong>076 322 79 81</strong> (Name: COINRING LEGENDS). Bitte gib deine Bestellnummer im Vermerk an. Nach Eingang startet umgehend die Schmiedung!';
      }
      if (text.includes('liefer') || text.includes('dauer') || text.includes('zeit') || text.includes('versand') || text.includes('tage')) {
        return 'Jeder Münzring ist 100% Handarbeit: 14 Tage Produktion + ca. 9 Tage Versand (total ca. 23 Tage, Express-Lieferung 2-3 Tage).';
      }
      if (text.includes('grösse') || text.includes('grosse') || text.includes('messen') || text.includes('size')) {
        return 'Wir fertigen Ringgrössen von 48 bis 72. Miss den Innenumfang deines Fingers in mm (z. B. 54 mm = Grösse 54). Gerne unterstützt dich das Atelier auch via WhatsApp unter <strong>079 833 93 87</strong>.';
      }
      if (text.includes('münze') || text.includes('muenze') || text.includes('eigene') || text.includes('silber') || text.includes('gold')) {
        return 'Ja, du kannst eigene Münzen (1 Fr., 2 Fr., 5 Fr. Silber, Gold Vreneli oder persönliche Erbstücke) verarbeiten lassen! Wähle dazu im Konfigurator "Eigene Münze" aus.';
      }
      if (text.includes('geschenk') || text.includes('box') || text.includes('karte') || text.includes('verpackung')) {
        return 'Wir bieten hochwertige Echtholzboxen (Woodbox ab 80 CHF, Royal Woodbox ab 160 CHF) und kostenlose Geschenk-Lieferung mit persönlicher Grusskarte.';
      }
      if (text.includes('whatsapp') || text.includes('admin') || text.includes('anruf') || text.includes('kontakt')) {
        return 'Unser Meister-Goldschmied ist persönlich für dich erreichbar unter <strong>079 833 93 87</strong> (per WhatsApp oder Anruf).';
      }
      return 'Grüezi! Vielen Dank für deine Nachricht. Unser Atelier beantwortet gerne jede Frage. Für direkte Rückfragen steht dir WhatsApp unter <strong>079 833 93 87</strong> oder TWINT unter <strong>076 322 79 81</strong> zur Verfügung.';
    }

    async function sendUserMessage() {
      var inp = document.getElementById('cr-input');
      if (!inp) return;
      var msg = inp.value.trim();
      if (!msg) return;
      inp.value = '';

      var msgs = document.getElementById('cr-messages');
      var typing = document.getElementById('cr-typing');

      // Append user msg
      var userDiv = document.createElement('div');
      userDiv.className = 'cr-msg cr-msg-user';
      userDiv.textContent = msg;
      msgs.insertBefore(userDiv, typing);
      scrollChatToBottom();

      // Show typing
      if (typing) typing.style.display = 'block';
      scrollChatToBottom();

      try {
        var replyText = '';
        var res = await fetch('/api/support', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ message: msg, customerId: clientId })
        }).catch(function() { return null; });

        if (res && res.ok) {
          var data = await res.json();
          replyText = data.reply;
        } else {
          // Fallback to knowledge base
          replyText = getLocalSmartReply(msg);
        }

        if (typing) typing.style.display = 'none';

        var botDiv = document.createElement('div');
        botDiv.className = 'cr-msg cr-msg-bot';
        botDiv.innerHTML = replyText;
        msgs.insertBefore(botDiv, typing);
        scrollChatToBottom();
      } catch (err) {
        if (typing) typing.style.display = 'none';
        var errDiv = document.createElement('div');
        errDiv.className = 'cr-msg cr-msg-bot';
        errDiv.innerHTML = getLocalSmartReply(msg);
        msgs.insertBefore(errDiv, typing);
        scrollChatToBottom();
      }
    }
  </script>
`;

// Read index.html
let html = fs.readFileSync(path.resolve('./index.html'), 'utf8');

// Check if widget already inserted
if (!html.includes('id="coinring-support-widget"')) {
  html = html.replace('</body>', `${widgetSnippet}\n</body>`);
  fs.writeFileSync(path.resolve('./index.html'), html);
  fs.writeFileSync(path.resolve('./app/src/main/assets/index.html'), html);
  console.log('✓ Successfully injected Support Chat Widget into index.html and app/src/main/assets/index.html');
} else {
  console.log('Widget already present');
}
