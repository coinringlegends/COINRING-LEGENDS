const fs = require('fs');
const path = require('path');

function patchIndexJs(filePath) {
  let code = fs.readFileSync(filePath, 'utf8');

  // 1. Update function cce declaration to support basename
  const targetCce = 'function cce(e){let{basename:t,children:r,future:n,window:i}=e,a=S.useRef();';
  const newCce = 'function cce(e){let{basename:t=(typeof window!=="undefined"&&window.location.pathname.toLowerCase().includes("coinring-legends")?(window.location.pathname.includes("COINRING-LEGENDS")?"/COINRING-LEGENDS":"/coinring-legends"):(typeof window!=="undefined"&&window.__ROUTER_BASENAME__?window.__ROUTER_BASENAME__:(typeof window!=="undefined"&&window.location.protocol==="file:"?"":"/COINRING-LEGENDS"))),children:r,future:n,window:i}=e,a=S.useRef();';

  if (code.includes(targetCce)) {
    code = code.replace(targetCce, newCce);
    console.log(`✓ Patched function cce in ${filePath}`);
  } else {
    console.warn(`! Target cce not found in ${filePath}`);
  }

  // 2. Update NVe call to cce to explicitly pass basename
  const targetNVe = 'o.jsxs(cce,{children:[o.jsx($fe,{}),o.jsx(CVe,{})]})';
  const newNVe = 'o.jsxs(cce,{basename:(typeof window!=="undefined"&&window.location.pathname.toLowerCase().includes("coinring-legends")?(window.location.pathname.includes("COINRING-LEGENDS")?"/COINRING-LEGENDS":"/coinring-legends"):(typeof window!=="undefined"&&window.__ROUTER_BASENAME__?window.__ROUTER_BASENAME__:(typeof window!=="undefined"&&window.location.protocol==="file:"?"":"/COINRING-LEGENDS"))),children:[o.jsx($fe,{}),o.jsx(CVe,{})]})';

  if (code.includes(targetNVe)) {
    code = code.replace(targetNVe, newNVe);
    console.log(`✓ Patched NVe in ${filePath}`);
  } else {
    console.warn(`! Target NVe not found in ${filePath}`);
  }

  fs.writeFileSync(filePath, code);
  console.log(`✓ Successfully updated ${filePath}`);
}

patchIndexJs(path.resolve('./assets/index.js'));
patchIndexJs(path.resolve('./app/src/main/assets/assets/index.js'));
