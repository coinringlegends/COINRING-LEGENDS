const fs = require('fs');
const path = require('path');

function patchFile(filePath) {
  let code = fs.readFileSync(filePath, 'utf8');

  // 1. Update OP() TWINT defaults
  code = code.replace(
    'number:e[0].twint_number||"+41 79 833 93 87",pay_url:e[0].twint_pay_url||"https://twint.ch"}}catch{}return{enabled:!0,number:"+41 79 833 93 87",pay_url:"https://twint.ch"',
    'number:e[0].twint_number||"076 322 79 81",pay_url:e[0].twint_pay_url||"https://twint.ch"}}catch{}return{enabled:!0,number:"076 322 79 81",pay_url:"https://twint.ch"'
  );

  // 2. Update sw() function TWINT number fallback
  code = code.replace(
    'const i=(e.number||"+41 79 833 93 87").replace(/\\s/g,"")',
    'const i=(e.number||"076 322 79 81").replace(/\\s/g,"")'
  );

  // 3. Update checkout rb label TWINT-Nummer
  code = code.replace(
    'value:(m==null?void 0:m.number)||"+41 79 833 93 87"',
    'value:(m==null?void 0:m.number)||"076 322 79 81"'
  );

  // 4. Update sw fallback call
  code = code.replace(
    'sw(m||{number:"+41 79 833 93 87",pay_url:"https://twint.ch"}',
    'sw(m||{number:"076 322 79 81",pay_url:"https://twint.ch"}'
  );

  // 5. Update useState TWINT fallbacks in components
  code = code.replace(
    'useState({number:"+41 79 833 93 87"})',
    'useState({number:"076 322 79 81"})'
  );
  code = code.replace(
    'useState({number:"+41 79 833 93 87"})',
    'useState({number:"076 322 79 81"})'
  );

  // 6. Update SiteSetting creation default
  code = code.replace(
    'twint_number:"+41 "',
    'twint_number:"076 322 79 81"'
  );

  // 7. Ensure WhatsApp Admin number is 41798339387 and displayed as 079 833 93 87
  code = code.replace(
    'const bxe="41798339387",wxe="+41 79 833 93 87";',
    'const bxe="41798339387",wxe="079 833 93 87";'
  );

  fs.writeFileSync(filePath, code);
  console.log(`✓ Patched numbers in ${filePath}`);
}

patchFile(path.resolve('./assets/index.js'));
patchFile(path.resolve('./app/src/main/assets/assets/index.js'));
