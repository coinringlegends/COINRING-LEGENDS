const express = require('express');
const fs = require('fs');
const path = require('path');

const app = express();
app.use(express.json());
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
const ADMIN_APIKEY = process.env.CALLMEBOT_APIKEY || "6653249";
const GEMINI_KEY = process.env.GEMINI_API_KEY || "";
const TWINT_DISP = "076 322 79 81";
const ADMIN_DISP = "079 833 93 87";
const TWINT_PHONE = "41763227981";

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
  if (!GEMINI_KEY) {
    return getKnowledgeBaseReply(question);
  }

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

// WhatsApp Baileys Option (Lazy-loaded if installed)
let sock = null;
let isBaileysConnected = false;

async function initBaileysIfPresent() {
  try {
    const { default: makeWASocket, useMultiFileAuthState, DisconnectReason } = require('@whiskeysockets/baileys');
    const authDir = path.join(__dirname, 'auth_info');
    if (!fs.existsSync(authDir)) fs.mkdirSync(authDir, { recursive: true });

    const { state, saveCreds } = await useMultiFileAuthState(authDir);
    sock = makeWASocket({
      auth: state,
      printQRInTerminal: true
    });

    sock.ev.on('creds.update', saveCreds);
    sock.ev.on('connection.update', (update) => {
      const { connection, lastDisconnect, qr } = update;
      if (qr) console.log('[Baileys QR available in logs]');
      if (connection === 'close') {
        const shouldReconnect = (lastDisconnect?.error)?.output?.statusCode !== DisconnectReason.loggedOut;
        isBaileysConnected = false;
        console.log('[Baileys] Verbindung geschlossen. Reconnect:', shouldReconnect);
        if (shouldReconnect) setTimeout(initBaileysIfPresent, 5000);
      } else if (connection === 'open') {
        isBaileysConnected = true;
        console.log('[Baileys] WhatsApp Verbunden als', ADMIN_PHONE);
      }
    });
  } catch (err) {
    console.log('[Baileys] Optional Baileys module not active, using CallMeBot HTTP as primary.');
  }
}
initBaileysIfPresent().catch(() => {});

// ROUTES
app.get('/', (req, res) => {
  res.json({
    status: 'LIVE',
    app: 'COINRING LEGENDS - Production Backend',
    admin_email: ADMIN_EMAIL,
    admin_phone: ADMIN_PHONE,
    admin_display: ADMIN_DISP,
    twint_display: TWINT_DISP,
    callmebot_active: !!ADMIN_APIKEY,
    gemini_active: !!GEMINI_KEY
  });
});

app.get('/test', async (req, res) => {
  try {
    const time = new Date().toLocaleString('de-CH', { timeZone: 'Europe/Zurich' });
    const msg = `COINRING TEST OK - ${time}\nAdmin: ${ADMIN_DISP}\nTWINT: ${TWINT_DISP}`;
    const url = `https://api.callmebot.com/whatsapp.php?phone=${ADMIN_PHONE}&text=${encodeURIComponent(msg)}&apikey=${ADMIN_APIKEY}`;
    const r = await fetch(url);
    const t = await r.text();
    res.send('CallMeBot Test Result: ' + t);
  } catch (e) {
    res.status(500).send('Error: ' + e.message);
  }
});

// AI SUPPORT CHAT ENDPOINT
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

// ORDER PLACEMENT & ADMIN NOTIFICATION ENDPOINT
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

    // 1. Send via CallMeBot
    const url = `https://api.callmebot.com/whatsapp.php?phone=${ADMIN_PHONE}&text=${encodeURIComponent(msg)}&apikey=${ADMIN_APIKEY}`;
    const r = await fetch(url);
    const t = await r.text();
    console.log(`[WA] Order ${finalOrderId} alert dispatched:`, t.slice(0, 100));

    // 2. Also send via Baileys if connected
    if (isBaileysConnected && sock) {
      try {
        const jid = `${ADMIN_PHONE}@s.whatsapp.net`;
        await sock.sendMessage(jid, { text: msg });
        console.log('[Baileys] WhatsApp Message sent directly');
      } catch (err) {
        console.warn('[Baileys] Direct send error:', err.message);
      }
    }

    res.json({
      success: true,
      sent: true,
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
  console.log(`Admin Phone: ${ADMIN_DISP} (${ADMIN_PHONE})`);
  console.log(`TWINT Display: ${TWINT_DISP}`);
  console.log(`Gemini Key configured: ${!!GEMINI_KEY}`);
});
