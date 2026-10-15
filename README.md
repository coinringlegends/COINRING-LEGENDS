# COINRING LEGENDS — CallMeBot WhatsApp Admin Alert Backend

> **Zero-Cost WhatsApp Admin Alerts for Swiss Shop**  
> Phone: `+41 79 833 93 87` | CallMeBot Key: `6653249` | TWINT: `+41 76 322 79 81`

---

## 1. Übersicht

Dieses schlanke Express-Backend empfängt Bestellungen aus dem COINRING LEGENDS Checkout und leitet sie sofort per WhatsApp (CallMeBot API) an den Inhaber weiter:

- **Admin-Empfänger:** `41798339387`
- **CallMeBot API-Key:** `6653249` (konfigurierbar via `CALLMEBOT_APIKEY`)
- **TWINT Referenz:** `41763227981`
- **Keine Blockade:** Der Frontend-Checkout schliesst sauber ab, auch wenn ein Netzwerkfehler vorliegen sollte.

---

## 2. Railway Deployment (Schritt-für-Schritt)

1. **Repository auf GitHub pushen** oder direkt mit Railway.app verbinden.
2. In Railway: **New Project** -> **Deploy from GitHub repo**.
3. **Umgebungsvariablen (Optional)** in den Railway Settings einrichten:
   - `PORT`: (wird automatisch von Railway gesetzt, z.B. 3000)
   - `CALLMEBOT_PHONE`: `41798339387` (Default ist bereits im Code hinterlegt)
   - `CALLMEBOT_APIKEY`: `6653249` (Default ist bereits im Code hinterlegt)
4. Unter **Settings** -> **Networking** eine öffentliche Domain generieren (z.B. `https://coinring-alerts-production.up.railway.app`).

---

## 3. Frontend-URL aktualisieren

Nachdem Railway die Domain bereitgestellt hat, öffne in `index.html` Zeile ~1979 und trage deine Railway-Domain ein:

```javascript
const RAILWAY_URL = 'https://DEINE-RAILWAY-URL.up.railway.app';
```

---

## 4. Endpunkte

### Healthcheck
```bash
GET /
# Antwort: COINRING LEGENDS - CallMeBot Backend LIVE - Phone 41798339387
```

### Manueller WhatsApp-Test
```bash
GET /test
# Sendet eine Testnachricht an +41 79 833 93 87 via CallMeBot
```

### Bestelleingang (Checkout Webhook)
```bash
POST /api/order
Content-Type: application/json

{
  "orderId": "CoinRing-849201",
  "customerName": "Hans Muster",
  "customerPhone": "+41 79 123 45 67",
  "customerAddress": "Bahnhofstrasse 10",
  "city": "Zürich",
  "zip": "8001",
  "config": "Einzelring (5-Fr.-Silber, US 10, LUXUS)",
  "amountCHF": "249.00"
}
```

Format der WhatsApp-Nachricht an den Inhaber:
```
🔥 NEUI BSTELLIG CoinRing-849201

KUND: Hans Muster
TEL: +41 79 123 45 67
ADR: Bahnhofstrasse 10 8001 Zürich
PRODUKT: Einzelring (5-Fr.-Silber, US 10, LUXUS)
BETRAG: CHF 249.00

TWINT: 41763227981
LIEFERIG: 14 Täg
STATUS: neu
```
