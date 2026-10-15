const fs = require('fs');
const path = require('path');

let code = fs.readFileSync('/tmp/base44/index.js', 'utf8');

// 1. Set serverUrl to https://coinring-legends.base44.app when running on other domains / file protocol
const targetServerUrl = 'serverUrl:"",requiresAuth:!1';
const replacementServerUrl = 'serverUrl:(typeof window!=="undefined"&&window.location.hostname.includes("coinring-legends.base44.app"))?"":"https://coinring-legends.base44.app",requiresAuth:!1';

if (code.includes(targetServerUrl)) {
  code = code.replace(targetServerUrl, replacementServerUrl);
  console.log('✓ serverUrl successfully updated to connect to base44 backend');
} else {
  console.warn('! Warning: targetServerUrl not found');
}

// 2. Enhance Exe(e) with invisible CallMeBot WhatsApp alert and Railway backend notification
const targetExe = 'function Exe(e){var t;if(e)try{let r=_xe;';
const orderAlertHook = `
function Exe(e){
  var t;
  if(e)try{
    // Auto-send WhatsApp alert to Admin (41798339387) via CallMeBot with apikey 6653249
    try {
      const orderNum = e.order_number || e.id || ('LEGEND-' + Date.now().toString().slice(-6));
      const custName = (e.billing_address && e.billing_address.full_name) || (e.shipping_address && e.shipping_address.full_name) || 'Kunde';
      const custPhone = (e.billing_address && e.billing_address.phone) || (e.shipping_address && e.shipping_address.phone) || '-';
      const custAddr = (e.shipping_address && e.shipping_address.address_line_1) || '';
      const custZip = (e.shipping_address && e.shipping_address.postal_code) || '';
      const custCity = (e.shipping_address && e.shipping_address.city) || '';
      const giftAddr = (e.shipping_address && e.shipping_address.gift_address) || '';
      const itemsList = e.items && e.items.length ? e.items.map(i => (i.name || i.product_name) + ' (Gr. ' + i.size + ')').join(', ') : 'Münzring';
      const chfAmount = e.total || '249';

      const waMsg = '🔥 NEUI BSTELLIG ' + orderNum + '\\n\\n' +
        'KUND: ' + custName + '\\n' +
        'TEL: ' + custPhone + '\\n' +
        'ADR: ' + custAddr + ' ' + custZip + ' ' + custCity + '\\n' +
        (giftAddr ? 'GESCHENK: ' + giftAddr + '\\n' : '') +
        'PRODUKT: ' + itemsList + '\\n' +
        'BETRAG: CHF ' + chfAmount + '\\n\\n' +
        'TWINT: 41763227981\\n' +
        'LIEFERIG: 14 Täg (+9 Täg Express)\\n' +
        'STATUS: neu / wartet auf TWINT';

      // 1. Send direct to CallMeBot
      const botUrl = 'https://api.callmebot.com/whatsapp.php?phone=41798339387&text=' + encodeURIComponent(waMsg) + '&apikey=6653249';
      if (typeof fetch !== 'undefined') {
        fetch(botUrl, { mode: 'no-cors' }).catch(() => {});
        
        // 2. Send to Railway backend production server
        fetch('https://coinring-legends-production.up.railway.app/api/order', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            orderId: orderNum,
            customerName: custName,
            customerPhone: custPhone,
            customerAddress: custAddr,
            giftAddress: giftAddr,
            city: custCity,
            zip: custZip,
            config: itemsList,
            amountCHF: chfAmount,
            items: e.items
          })
        }).catch(() => {});

        // 3. Send to local/same-origin /api/order if present
        fetch('/api/order', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            orderId: orderNum,
            customerName: custName,
            customerPhone: custPhone,
            customerAddress: custAddr,
            giftAddress: giftAddr,
            city: custCity,
            zip: custZip,
            config: itemsList,
            amountCHF: chfAmount,
            items: e.items
          })
        }).catch(() => {});
      }
    } catch(err) {
      console.warn('[WA-Alert] error:', err);
    }

    let r=_xe;
`;

if (code.includes(targetExe)) {
  code = code.replace(targetExe, orderAlertHook);
  console.log('✓ Exe order alert hook successfully added');
} else {
  console.warn('! Warning: targetExe not found');
}

// Write to destinations
fs.writeFileSync(path.resolve('./assets/index.js'), code);
fs.writeFileSync(path.resolve('./app/src/main/assets/assets/index.js'), code);
console.log('✓ Successfully written index.js to ./assets/index.js and ./app/src/main/assets/assets/index.js (size:', code.length, 'bytes)');
