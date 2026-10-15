const fs = require('fs');
const code = fs.readFileSync('/tmp/base44/index.js', 'utf8');

const fetches = Array.from(new Set(code.match(/["'`]\/(?:api|entities)[^"'`]*["'`]/g) || []));
console.log('Internal endpoints count:', fetches.length);
console.log('Sample endpoints:', fetches.slice(0, 40));

const baseUrls = Array.from(new Set(code.match(/https:\/\/[a-zA-Z0-9.-]*base44[a-zA-Z0-9./_-]*/g) || []));
console.log('Base44 URLs:', baseUrls);
