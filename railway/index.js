const express = require('express');
const fs = require('fs');
const path = require('path');
const QRCode = require('qrcode');

const app = express();
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization');
  res.header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  if (req.method === 'OPTIONS') return res.sendStatus(200);
  next();
});

// Fixed Numbers & Configuration
const ADMIN_EMAIL = "COINRINGLEGENDS@gmail.com";
const ADMIN_PHONE = "41798339387";
const ADMIN_DISP = "079 833 93 87";
const ADMIN_APIKEY = process.env.CALLMEBOT_APIKEY || "6653249";
const TWINT_PHONE = "41763227981";
const TWINT_DISP = "076 322 79 81";
const GEMINI_KEY = process.env.GEMINI_API_KEY || "";

global.chatHistory = {};

function addHist(p, r, t) {
  if (!global.chatHistory[p]) global.chatHistory[p] = [];
  global.chatHistory[p].push({ role: r, text: t, time: new Date() });
  if (global.chatHistory[p].length > 50) global.chatHistory[p].shift();
}

function getLast10(p) {
  return (global.chatHistory[p] || [])
    .slice(-10)
    .map(m => `${m.role.toUpperCase()}: ${m.text}`)
    .join('\n');
}

// Knowledge Base Fallback for Coinring Legends
function getKnowledgeBaseReply(question) {
  const q = (question || '').toLowerCase();
  if (q.includes('twint') || q.includes('zahl') || q.includes('bezahl')) {
    return `Zahle bequem per TWINT an ${TWINT_DISP} (+41 76 322 79 81). Bitte gib deine Bestellnummer (z. B. LEGEND-123456) als Zahlungszweck an. Nach Zahlungseingang startet sofort die handgefertigte Produktion deines Unikats!`;
  }
  if (q.includes('liefer') || q.includes('dauer') || q.includes('zeit') || q.includes('versand')) {
    return `Unsere handgefertigten Münzringe haben eine Produktionszeit von 14 Tagen + ca. 9 Tage Versand (total ca. 23 Tage, Express 2-3 Tage). Jeder Ring wird in der Schweiz mit höchster Präzision geschmiedet.`;
  }
  if (q.includes('grösse') || q.includes('grosse') || q.includes('messen') || q.includes('size')) {
    return `Wir fertigen Ringgrössen von 48 bis 72 an. Tipp: Wickle einen Papierstreifen um den Finger, markiere die Überlappung und miss die Länge in mm für den Innenumfang (z. B. 54 mm = Grösse 54). Gerne berät dich das Atelier auch direkt via WhatsApp unter ${ADMIN_DISP}.`;
  }
  if (q.includes('münze') || q.includes('muenze') || q.includes('eigene') || q.includes('silber')) {
    return `Ja! Du kannst eigene Münzen (z. B. 1 Franken, 2 Franken, 5 Franken Silber, Gold Vreneli oder Erbstücke) einsenden oder aus unserem historischen Schweizer Münzbestand auswählen. Die Details der Prägung bleiben kunstvoll auf der Ringband-Oberfläche erhalten.`;
  }
  if (q.includes('geschenk') || q.includes('box') || q.includes('verpackung')) {
    return `Wir bieten edle Holzboxen (COINRING LEGENDS WOODBOX ab 80 CHF, ROYAL WOODBOX ab 160 CHF) und die Geschenk-Option mit separater Lieferadresse und persönlicher Grusskarte.`;
  }
  if (q.includes('kontakt') || q.includes('telefon') || q.includes('admin') || q.includes('whatsapp')) {
    return `Du erreichst unseren Meister-Goldschmied und Support direkt unter ${ADMIN_DISP} (+41 79 833 93 87) per WhatsApp oder Anruf.`;
  }
  return `Grüezi! Willkommen bei COINRING LEGENDS™. Wir fertigen 100% handgemachte Schweizer Münzringe als zeitlose Unikate. TWINT: ${TWINT_DISP}. Support & WhatsApp: ${ADMIN_DISP}. Wie kann ich dir bei deiner Konfiguration helfen?`;
}

// --- GEMINI FREE AI SUPPORT BOT ---
async function callGeminiFree(question, history) {
  if (!GEMINI_KEY) return getKnowledgeBaseReply(question);

  try {
    const prompt = `Du bist der offizielle Support-Berater von COINRING LEGENDS™ (Schweizer Manufaktur für handgefertigte Münzringe).
Ton: Kompetent, herzlich, Schweizer Präzision, dezent edel.

Kernwissen:
- COINRING LEGENDS macht personalisierte Ringe aus echten Münzen, 100% Handarbeit Schweiz (Luzern / Kottwil).
- Produktionszeit: 14 Tage + Versand 9 Tage = ca. 23 Tage total (Express-Option verfügbar).
- TWINT Zahlung an: ${TWINT_DISP} (+41 76 322 79 81) mit Bestellnummer als Vermerk.
- Admin / Support WhatsApp: ${ADMIN_DISP} (+41 79 833 93 87).
- Ringgrössen: 48 bis 72.
- Geschenkoption: Separate Geschenk-Lieferadresse & persönliche Grusskarte im Checkout wählbar.
- Jeder Ring ist ein nummeriertes Unikat (1 Ring = 1 LEGENDS-ID = 1 Verkauf).
- Edelverpackungen: Original Woodbox, Royal Woodbox, Premium Etui.
- Garantie auf Handarbeit und Materialechtheit.

Chat-Verlauf der letzten Nachrichten:
${history || 'Keine bisherigen Nachrichten'}

Kundenfrage: ${question}

Antworte kurz, prägnant, hilfreich (2-4 Sätze) und kundenorientiert. Nenne bei Zahlungsfragen immer TWINT ${TWINT_DISP}.`;

    const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${GEMINI_KEY}`;
    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        contents: [{ parts: [{ text: prompt }] }],
        generationConfig: {
          temperature: 0.6,
          maxOutputTokens: 350
        }
      })
    });

    if (!response.ok) {
      console.warn('[Gemini] Response not OK:', response.status);
      return getKnowledgeBaseReply(question);
    }

    const data = await response.json();
    const replyText = data.candidates?.[0]?.content?.parts?.[0]?.text;
    return replyText ? replyText.trim() : getKnowledgeBaseReply(question);
  } catch (e) {
    console.error('[Gemini] Error calling Gemini API:', e.message);
    return getKnowledgeBaseReply(question);
  }
}

// =========================================================================
// WHATSAPP BAILEYS CONNECTION MANAGER (QR-Code & 8-stelliger Pairing Code)
// =========================================================================
let sock = null;
let isConnected = false;
let currentQR = null;
let currentQRImage = null;
let currentPairingCode = null;
let pairingCodePhone = null;
let connectedUser = null;
let connectionStatusText = 'Initialisiere...';

const authDir = path.join(__dirname, 'auth_info');

function clearAuthDir() {
  try {
    if (fs.existsSync(authDir)) {
      fs.rmSync(authDir, { recursive: true, force: true });
      console.log('[Baileys] Auth-Verzeichnis zurückgesetzt.');
    }
  } catch(e) {
    console.warn('[Baileys] Fehler beim Löschen von auth_info:', e.message);
  }
}

async function startBaileys() {
  try {
    const { default: makeWASocket, useMultiFileAuthState, DisconnectReason } = require('@whiskeysockets/baileys');
    
    if (!fs.existsSync(authDir)) fs.mkdirSync(authDir, { recursive: true });
    const { state, saveCreds } = await useMultiFileAuthState(authDir);

    // CRITICAL: Browser must be set to Chrome on Linux/Ubuntu for pairing codes to work!
    sock = makeWASocket({
      auth: state,
      printQRInTerminal: false,
      browser: ['Ubuntu', 'Chrome', '20.0.04'],
      syncFullHistory: false,
      connectTimeoutMs: 60000,
      defaultQueryTimeoutMs: 60000
    });

    sock.ev.on('creds.update', saveCreds);

    sock.ev.on('connection.update', async (update) => {
      const { connection, lastDisconnect, qr } = update;

      if (qr) {
        currentQR = qr;
        try {
          currentQRImage = await QRCode.toDataURL(qr, { margin: 2, scale: 7, color: { dark: '#0A0A0A', light: '#FFFFFF' } });
        } catch(err) {
          console.warn('[Baileys] Fehler beim Erstellen des QR-Images:', err.message);
        }
        connectionStatusText = 'Warte auf QR-Scan oder Code-Eingabe...';
        console.log('[Baileys] Neuer QR-Code generiert.');
      }

      if (connection === 'open') {
        isConnected = true;
        currentQR = null;
        currentQRImage = null;
        currentPairingCode = null;
        connectedUser = sock.user?.id ? sock.user.id.split(':')[0] : ADMIN_PHONE;
        connectionStatusText = `Verbunden als +${connectedUser}`;
        console.log(`[Baileys] WhatsApp ERFOLGREICH VERBUNDEN als +${connectedUser}!`);
      }

      if (connection === 'close') {
        isConnected = false;
        const statusCode = (lastDisconnect?.error)?.output?.statusCode;
        const shouldReconnect = statusCode !== DisconnectReason.loggedOut;
        connectionStatusText = `Verbindung getrennt (Code: ${statusCode || 'unbekannt'}). Reconnect: ${shouldReconnect}`;
        console.log(`[Baileys] Verbindung geschlossen. StatusCode: ${statusCode}. Reconnect: ${shouldReconnect}`);

        if (statusCode === DisconnectReason.loggedOut) {
          clearAuthDir();
          setTimeout(startBaileys, 3000);
        } else if (shouldReconnect) {
          setTimeout(startBaileys, 5000);
        }
      }
    });

    console.log('[Baileys] WhatsApp Socket gestartet.');
  } catch (err) {
    console.error('[Baileys] Fehler beim Starten:', err.message);
    connectionStatusText = 'Fehler beim Start: ' + err.message;
  }
}

// Baileys im Hintergrund starten
startBaileys().catch(e => console.error('[Baileys] Uncaught start error:', e));

// -------------------------------------------------------------------------
// WHATSAPP API ENDPUNKTE
// -------------------------------------------------------------------------

// Status abrufen
app.get('/api/whatsapp/status', (req, res) => {
  res.json({
    connected: isConnected,
    user: connectedUser,
    statusText: connectionStatusText,
    hasQr: !!currentQR,
    pairingCode: currentPairingCode,
    pairingPhone: pairingCodePhone,
    adminPhone: ADMIN_DISP
  });
});

// QR-Code als JSON abrufen
app.get('/api/whatsapp/qr', (req, res) => {
  res.json({
    connected: isConnected,
    qr: currentQR,
    qrImage: currentQRImage,
    statusText: connectionStatusText
  });
});

// 8-stelligen Pairing-Code anfordern
app.all(['/api/whatsapp/pair', '/whatsapp/pair'], async (req, res) => {
  try {
    if (isConnected) {
      return res.json({
        success: false,
        connected: true,
        message: `Bereits verbunden als +${connectedUser}`
      });
    }

    if (!sock) {
      return res.status(500).json({ success: false, error: 'Socket noch nicht initialisiert.' });
    }

    // Telefonnummer aus Body oder Query, Fallback auf ADMIN_PHONE (41798339387)
    let phone = (req.body?.phone || req.query?.phone || ADMIN_PHONE).toString().replace(/[^0-9]/g, '');
    if (phone.startsWith('0')) {
      phone = '41' + phone.substring(1);
    }

    console.log(`[Baileys] Fordere WhatsApp Pairing-Code für +${phone} an...`);
    const code = await sock.requestPairingCode(phone);
    
    // Format: 1234-5678 für optimale Lesbarkeit
    const formattedCode = code?.match(/.{1,4}/g)?.join('-') || code;
    currentPairingCode = formattedCode;
    pairingCodePhone = phone;

    console.log(`[Baileys] ✅ Pairing-Code erhalten: ${formattedCode}`);

    res.json({
      success: true,
      phone: phone,
      code: formattedCode,
      instructions: [
        '1. Öffne WhatsApp auf deinem Smartphone',
        '2. Tippe auf "Verknüpfte Geräte" -> "Gerät hinzufügen"',
        '3. Wähle unten "Stattdessen mit Telefonnummer verknüpfen"',
        `4. Gib diesen Code ein: ${formattedCode}`
      ]
    });
  } catch (e) {
    console.error('[Baileys] Fehler beim Generieren des Pairing-Codes:', e.message);
    res.status(500).json({
      success: false,
      error: e.message || 'Pairing-Code konnte nicht generiert werden.'
    });
  }
});

// Verbindung trennen / Reset
app.all(['/api/whatsapp/reset', '/whatsapp/reset'], async (req, res) => {
  try {
    console.log('[Baileys] Manuelles Zurücksetzen angefordert.');
    if (sock) {
      try { sock.logout(); } catch(e) {}
      try { sock.end(); } catch(e) {}
    }
    clearAuthDir();
    isConnected = false;
    currentQR = null;
    currentQRImage = null;
    currentPairingCode = null;
    connectionStatusText = 'Zurückgesetzt. Starte neu...';
    setTimeout(startBaileys, 1000);

    if (req.xhr || req.headers.accept?.includes('json')) {
      res.json({ success: true, message: 'Verbindung zurückgesetzt. Neuer QR-Code wird generiert.' });
    } else {
      res.redirect('/whatsapp');
    }
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// -------------------------------------------------------------------------
// WEB-OBERFLÄCHE: /whatsapp (und /qr) FÜR DIREKTE VERKNÜPFUNG
// -------------------------------------------------------------------------
app.get(['/whatsapp', '/qr', '/pair'], (req, res) => {
  res.send(`<!DOCTYPE html>
<html lang="de">
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>WhatsApp Verknüpfung · COINRING LEGENDS™</title>
  <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Inter:wght@400;500;600&display=swap" rel="stylesheet"/>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body {
      background: #0A0A0A;
      color: #E5E7EB;
      font-family: 'Inter', sans-serif;
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 32px 16px;
    }
    .container {
      width: 100%;
      max-width: 580px;
      background: #121212;
      border: 1px solid rgba(212, 183, 143, 0.3);
      border-radius: 20px;
      padding: 32px 24px;
      box-shadow: 0 25px 60px rgba(0,0,0,0.8), 0 0 30px rgba(212, 183, 143, 0.1);
    }
    .header { text-align: center; margin-bottom: 28px; }
    .brand {
      font-family: 'Cormorant Garamond', Georgia, serif;
      font-size: 1.8rem;
      letter-spacing: 0.18em;
      color: #D4B78F;
      text-transform: uppercase;
      font-weight: 600;
    }
    .subtitle {
      font-size: 0.85rem;
      color: rgba(255,255,255,0.6);
      margin-top: 6px;
      letter-spacing: 0.05em;
    }
    .status-badge {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 8px 16px;
      border-radius: 9999px;
      font-size: 0.85rem;
      font-weight: 500;
      margin: 16px auto 0 auto;
      border: 1px solid rgba(255,255,255,0.1);
    }
    .status-dot { width: 10px; height: 10px; border-radius: 50%; }
    .status-connected { background: rgba(16, 185, 129, 0.15); color: #10B981; border-color: rgba(16, 185, 129, 0.3); }
    .status-connected .status-dot { background: #10B981; box-shadow: 0 0 8px #10B981; }
    .status-waiting { background: rgba(245, 158, 11, 0.15); color: #F59E0B; border-color: rgba(245, 158, 11, 0.3); }
    .status-waiting .status-dot { background: #F59E0B; box-shadow: 0 0 8px #F59E0B; animation: pulse 1.5s infinite; }
    @keyframes pulse { 0%, 100% { opacity: 0.5; } 50% { opacity: 1; } }

    .card {
      background: #181818;
      border: 1px solid rgba(212, 183, 143, 0.2);
      border-radius: 14px;
      padding: 22px;
      margin-bottom: 20px;
    }
    .card-title {
      font-size: 1rem;
      color: #D4B78F;
      font-weight: 600;
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .code-box {
      background: #0A0A0A;
      border: 2px dashed #D4B78F;
      border-radius: 12px;
      padding: 18px;
      text-align: center;
      margin: 16px 0;
    }
    .pairing-code {
      font-family: monospace;
      font-size: 2.2rem;
      letter-spacing: 0.25em;
      color: #D4B78F;
      font-weight: 700;
      user-select: all;
    }
    .input-row { display: flex; gap: 8px; margin-top: 10px; }
    .input-phone {
      flex: 1;
      background: #0A0A0A;
      border: 1px solid rgba(212, 183, 143, 0.3);
      border-radius: 8px;
      padding: 12px 14px;
      color: #FFF;
      font-size: 0.95rem;
      outline: none;
    }
    .input-phone:focus { border-color: #D4B78F; }
    .btn {
      background: #D4B78F;
      color: #0A0A0A;
      border: none;
      padding: 12px 20px;
      border-radius: 8px;
      font-weight: 600;
      font-size: 0.9rem;
      cursor: pointer;
      transition: opacity 0.2s;
      white-space: nowrap;
    }
    .btn:hover { opacity: 0.9; }
    .btn-secondary {
      background: rgba(255,255,255,0.08);
      color: #D4B78F;
      border: 1px solid rgba(212, 183, 143, 0.3);
    }
    .steps { margin-top: 12px; font-size: 0.85rem; color: rgba(255,255,255,0.7); line-height: 1.6; }
    .steps ol { padding-left: 20px; }
    .qr-container { text-align: center; padding: 12px; background: #0A0A0A; border-radius: 12px; margin-top: 10px; }
    .qr-img { max-width: 240px; border-radius: 8px; border: 4px solid #FFF; }
    .footer { text-align: center; margin-top: 24px; font-size: 0.75rem; color: rgba(255,255,255,0.4); }
  </style>
</head>
<body>
  <div class="container">
    <div class="header">
      <div class="brand">COINRING LEGENDS™</div>
      <div class="subtitle">Offizieller WhatsApp Admin-Kanal · Schweizer Manufaktur</div>
      <div id="status-badge" class="status-badge ${isConnected ? 'status-connected' : 'status-waiting'}">
        <div class="status-dot"></div>
        <span id="status-text">${isConnected ? 'Verbunden als +' + connectedUser : 'Warte auf Verknüpfung'}</span>
      </div>
    </div>

    <!-- OPTION 1: PAIRING CODE (EMPFOHLEN) -->
    <div class="card">
      <div class="card-title">
        <span>📱</span> Option 1: Mit 8-stelligem Code verknüpfen (Empfohlen!)
      </div>
      <p style="font-size:0.85rem;color:rgba(255,255,255,0.7);">Kein Abfotografieren nötig. Fordere deinen persönlichen Code an und tippe ihn direkt in WhatsApp ein.</p>
      
      <div class="input-row">
        <input type="text" id="phone-input" class="input-phone" value="${ADMIN_PHONE}" placeholder="z. B. 41798339387"/>
        <button class="btn" onclick="requestPairingCode()">Code anfordern</button>
      </div>

      <div id="code-result" style="${currentPairingCode ? 'display:block;' : 'display:none;'}">
        <div class="code-box">
          <div style="font-size:0.75rem;color:rgba(212,183,143,0.7);text-transform:uppercase;letter-spacing:0.1em;margin-bottom:6px;">Dein WhatsApp Verknüpfungscode:</div>
          <div class="pairing-code" id="display-code">${currentPairingCode || '----'}</div>
          <button class="btn btn-secondary" style="margin-top:10px;padding:6px 14px;font-size:0.8rem;" onclick="copyCode()">Code kopieren</button>
        </div>

        <div class="steps">
          <div style="font-weight:600;color:#D4B78F;margin-bottom:4px;">So gibst du den Code ein:</div>
          <ol>
            <li>Öffne WhatsApp auf deinem Smartphone (${ADMIN_DISP})</li>
            <li>Gehe zu <strong>Einstellungen</strong> (oder Menü oben rechts) &rarr; <strong>Verknüpfte Geräte</strong></li>
            <li>Tippe auf <strong>Gerät hinzufügen</strong></li>
            <li>Tippe ganz unten auf <strong>"Stattdessen mit Telefonnummer verknüpfen"</strong></li>
            <li>Gib den obigen Code ein — Fertig!</li>
          </ol>
        </div>
      </div>
    </div>

    <!-- OPTION 2: QR-CODE -->
    <div class="card">
      <div class="card-title">
        <span>📷</span> Option 2: Mit QR-Code scannen
      </div>
      <p style="font-size:0.85rem;color:rgba(255,255,255,0.7);">Öffne WhatsApp &rarr; Verknüpfte Geräte &rarr; Gerät hinzufügen und scanne diesen Code:</p>
      
      <div class="qr-container">
        <div id="qr-display">
          ${currentQRImage ? `<img class="qr-img" src="${currentQRImage}" alt="WhatsApp QR Code"/>` : (isConnected ? `<div style="color:#10B981;padding:24px;font-weight:600;">✅ WhatsApp ist erfolgreich verbunden!</div>` : `<div style="color:rgba(255,255,255,0.5);padding:24px;">Lade aktuellen QR-Code...</div>`)}
        </div>
        <div style="margin-top:10px;">
          <button class="btn btn-secondary" onclick="loadQR()">QR-Code aktualisieren</button>
        </div>
      </div>
    </div>

    <!-- ACTIONS -->
    <div style="display:flex;gap:10px;justify-content:center;">
      <button class="btn btn-secondary" onclick="testAlert()">Test-Nachricht senden</button>
      <button class="btn btn-secondary" style="border-color:rgba(239,68,68,0.4);color:#EF4444;" onclick="resetConnection()">Zurücksetzen</button>
    </div>

    <div class="footer">
      COINRING LEGENDS™ Production Server · Admin: ${ADMIN_DISP} · TWINT: ${TWINT_DISP}<br/>
      CallMeBot Backup API-Key: <strong>${ADMIN_APIKEY}</strong> (Permanent aktiv)
    </div>
  </div>

  <script>
    async function requestPairingCode() {
      const phone = document.getElementById('phone-input').value.trim() || '${ADMIN_PHONE}';
      const btn = event.target;
      btn.textContent = 'Generiere...';
      try {
        const res = await fetch('/api/whatsapp/pair', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ phone })
        });
        const data = await res.json();
        if (data.success && data.code) {
          document.getElementById('display-code').textContent = data.code;
          document.getElementById('code-result').style.display = 'block';
        } else {
          alert(data.message || data.error || 'Konnte Code nicht generieren. Versuche es in 5 Sekunden erneut.');
        }
      } catch (e) {
        alert('Netzwerkfehler: ' + e.message);
      } finally {
        btn.textContent = 'Code anfordern';
      }
    }

    function copyCode() {
      const code = document.getElementById('display-code').textContent.trim();
      navigator.clipboard.writeText(code.replace('-', ''));
      alert('Code ' + code + ' in Zwischenablage kopiert!');
    }

    async function loadQR() {
      try {
        const res = await fetch('/api/whatsapp/qr');
        const data = await res.json();
        const display = document.getElementById('qr-display');
        if (data.connected) {
          display.innerHTML = '<div style="color:#10B981;padding:24px;font-weight:600;">✅ WhatsApp ist erfolgreich verbunden!</div>';
        } else if (data.qrImage) {
          display.innerHTML = '<img class="qr-img" src="' + data.qrImage + '" alt="WhatsApp QR Code"/>';
        }
      } catch(e) {}
    }

    async function testAlert() {
      try {
        const res = await fetch('/test');
        const txt = await res.text();
        alert('Test gesendet: ' + txt);
      } catch(e) {
        alert('Fehler: ' + e.message);
      }
    }

    async function resetConnection() {
      if (confirm('Verbindung wirklich trennen und neu initialisieren?')) {
        await fetch('/api/whatsapp/reset');
        location.reload();
      }
    }

    // Auto-refresh Status alle 5 Sekunden
    setInterval(async () => {
      try {
        const res = await fetch('/api/whatsapp/status');
        const data = await res.json();
        const badge = document.getElementById('status-badge');
        const text = document.getElementById('status-text');
        if (data.connected) {
          badge.className = 'status-badge status-connected';
          text.textContent = 'Verbunden als +' + data.user;
        } else {
          badge.className = 'status-badge status-waiting';
          text.textContent = 'Warte auf Verknüpfung';
        }
      } catch(e) {}
    }, 5000);
  </script>
</body>
</html>`);
});

// -------------------------------------------------------------------------
// STANDARD STATUS ROUTE
// -------------------------------------------------------------------------
app.get('/', (req, res) => {
  res.json({
    status: 'LIVE',
    app: 'COINRING LEGENDS - Production Backend',
    admin_email: ADMIN_EMAIL,
    admin_phone: ADMIN_PHONE,
    admin_display: ADMIN_DISP,
    twint_display: TWINT_DISP,
    whatsapp_baileys_connected: isConnected,
    whatsapp_linked_user: connectedUser,
    whatsapp_portal: '/whatsapp',
    callmebot_active: !!ADMIN_APIKEY,
    gemini_active: !!GEMINI_KEY
  });
});

// -------------------------------------------------------------------------
// TEST ROUTE
// -------------------------------------------------------------------------
app.get('/test', async (req, res) => {
  try {
    const time = new Date().toLocaleString('de-CH', { timeZone: 'Europe/Zurich' });
    const msg = `COINRING TEST OK - ${time}\nAdmin: ${ADMIN_DISP}\nTWINT: ${TWINT_DISP}\nBaileys: ${isConnected ? 'Online' : 'CallMeBot Fallback'}`;

    let baileysSent = false;
    if (isConnected && sock) {
      try {
        const jid = `${ADMIN_PHONE}@s.whatsapp.net`;
        await sock.sendMessage(jid, { text: msg });
        baileysSent = true;
      } catch (e) {
        console.warn('[Baileys] Test direct send failed:', e.message);
      }
    }

    // CallMeBot Backup
    const url = `https://api.callmebot.com/whatsapp.php?phone=${ADMIN_PHONE}&text=${encodeURIComponent(msg)}&apikey=${ADMIN_APIKEY}`;
    const r = await fetch(url);
    const t = await r.text();

    res.send(`Test Result:\n- Baileys Direct: ${baileysSent ? 'Gesendet' : 'Nicht verbunden'}\n- CallMeBot: ${t}`);
  } catch (e) {
    res.status(500).send('Error: ' + e.message);
  }
});

// -------------------------------------------------------------------------
// AI SUPPORT CHAT ENDPOINT
// -------------------------------------------------------------------------
app.post('/api/support', async (req, res) => {
  try {
    const { message, customerId, phone } = req.body;
    if (!message || !message.trim()) {
      return res.status(400).json({ error: 'Message is required' });
    }

    const clientId = customerId || phone || 'guest';
    addHist(clientId, 'user', message);
    const hist = getLast10(clientId);

    const reply = await callGeminiFree(message, hist);
    addHist(clientId, 'bot', reply);

    res.json({
      reply,
      supportPhone: ADMIN_DISP,
      twintPhone: TWINT_DISP
    });
  } catch (e) {
    console.error('[Support] Error:', e.message);
    res.status(500).json({
      reply: getKnowledgeBaseReply(req.body?.message || ''),
      supportPhone: ADMIN_DISP,
      twintPhone: TWINT_DISP
    });
  }
});

// -------------------------------------------------------------------------
// BESTELL-ALARM ENDPOINT
// -------------------------------------------------------------------------
app.post('/api/order', async (req, res) => {
  try {
    const {
      orderId,
      order_number,
      customerName,
      customerPhone,
      customerAddress,
      giftAddress,
      config,
      amountCHF,
      total,
      city,
      zip,
      items
    } = req.body;

    const finalOrderId = orderId || order_number || `LEGEND-${Date.now().toString().slice(-6)}`;
    const finalAmount = amountCHF || total || '249';
    const itemsSummary = items && items.length
      ? items.map(i => `${i.name || i.product_name} (Gr. ${i.size})`).join(', ')
      : (config || 'Münzring Unikat');

    const msg = `🔥 NEUI BSTELLIG ${finalOrderId}

KUND: ${customerName || 'Kunde'}
TEL: ${customerPhone || '-'}
ADR: ${customerAddress || ''} ${zip || ''} ${city || ''}
${giftAddress ? 'GESCHENK: ' + giftAddress + '\n' : ''}PRODUKT: ${itemsSummary}
BETRAG: CHF ${finalAmount}

TWINT: ${TWINT_DISP}
LIEFERIG: 14 Täg (+9 Täg Express)
STATUS: neu / wartet auf TWINT`;

    let directSent = false;
    // 1. Send via Baileys if connected
    if (isConnected && sock) {
      try {
        const jid = `${ADMIN_PHONE}@s.whatsapp.net`;
        await sock.sendMessage(jid, { text: msg });
        directSent = true;
        console.log(`[Baileys] WhatsApp Order ${finalOrderId} direkt an ${ADMIN_PHONE} gesendet.`);
      } catch (err) {
        console.warn('[Baileys] Direct send error:', err.message);
      }
    }

    // 2. Guaranteed CallMeBot Backup
    const url = `https://api.callmebot.com/whatsapp.php?phone=${ADMIN_PHONE}&text=${encodeURIComponent(msg)}&apikey=${ADMIN_APIKEY}`;
    const r = await fetch(url);
    const t = await r.text();
    console.log(`[CallMeBot] Order ${finalOrderId} alert dispatched:`, t.slice(0, 100));

    res.json({
      success: true,
      sent: true,
      directWhatsapp: directSent,
      callmebot: t.slice(0, 100),
      orderId: finalOrderId,
      twintNumber: TWINT_DISP,
      adminPhone: ADMIN_DISP
    });
  } catch (e) {
    console.error('[Order] Error:', e.message);
    res.status(500).json({ error: e.message });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`COINRING Backend läuft auf Port ${PORT}`);
  console.log(`WhatsApp Portal: http://localhost:${PORT}/whatsapp`);
  console.log(`Admin Phone: ${ADMIN_DISP} (${ADMIN_PHONE})`);
  console.log(`TWINT Display: ${TWINT_DISP}`);
  console.log(`Gemini Key configured: ${!!GEMINI_KEY}`);
});
