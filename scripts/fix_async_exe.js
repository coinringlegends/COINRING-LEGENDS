const fs = require('fs');
const path = require('path');

function fixAsyncExe(filePath) {
  let code = fs.readFileSync(filePath, 'utf8');
  if (code.includes('function Exe(e){')) {
    code = code.replace('function Exe(e){', 'async function Exe(e){');
    fs.writeFileSync(filePath, code);
    console.log(`✓ Fixed Exe syntax error in ${filePath}`);
  } else if (code.includes('async function Exe(e){')) {
    console.log(`✓ Already async in ${filePath}`);
  } else {
    console.warn(`! Warning: Exe declaration not found in ${filePath}`);
  }
}

fixAsyncExe(path.resolve('./assets/index.js'));
fixAsyncExe(path.resolve('./app/src/main/assets/assets/index.js'));
