# COINRING LEGENDS — Railway CallMeBot WhatsApp & Gemini AI Support Backend

Offizielles Produktions-Backend für COINRING LEGENDS™ mit automatischer Admin-Alarmierung via CallMeBot/Baileys WhatsApp und Gemini Free AI Support-Chatbot.

## Account- & Inhaber-Konfiguration
- **Offizielle Inhaber- & Service-Email**: `COINRINGLEGENDS@gmail.com`
- **Admin WhatsApp / Meister-Goldschmied**: `079 833 93 87` (`41798339387`)
- **CallMeBot Admin API-Key**: `6653249`
- **TWINT Empfängernummer**: `076 322 79 81` (`41763227981`)
- **Gemini AI API Key**: Konfigurierbar über Railway Umgebungsvariable `GEMINI_API_KEY` (Free Tier)

## Endpunkte
- `GET /` — Status-Check (gibt Status, Inhaber-Email, Nummern und API-Status zurück)
- `GET /test` — Sendet eine Test-WhatsApp an Admin `079 833 93 87`
- `POST /api/support` — Gemini AI Support-Chatbot (100% Free Gemini Integration mit Schweizerdeutsch/Deutsch Atelier-Wissen)
- `POST /api/order` — Nimmt Bestelldaten entgegen und sendet automatischen WhatsApp-Alarm an Admin

## 1-Klick Deployment auf Railway unter COINRINGLEGENDS@gmail.com
1. Bei [Railway.app](https://railway.app) mit dem Account **`COINRINGLEGENDS@gmail.com`** einloggen (oder GitHub verknüpfen).
2. "New Project" -> "Deploy from GitHub repo" wählen (oder diesen `railway/` Ordner hochladen).
3. Railway erkennt automatisch `package.json` und startet `node index.js`.
4. Optionale Umgebungsvariablen in Railway hinterlegen:
   - `GEMINI_API_KEY`: Dein kostenloser Gemini API Key von Google AI Studio
   - `CALLMEBOT_APIKEY`: `6653249`
   - `CALLMEBOT_PHONE`: `41798339387`
5. Die generierte Railway-URL (z. B. `https://coinring-legends-production.up.railway.app`) ist sofort live.
