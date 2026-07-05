const assert = require('assert');
const fs = require('fs');
const path = require('path');

const servicePath = path.resolve(
  __dirname,
  '../sw-app/src/main/java/com/chuncongcong/sw/biz/service/impl/ItemBizServiceImpl.java'
);

const source = fs.readFileSync(servicePath, 'utf8');

assert.ok(
  source.includes('int pageSize = normalizePageSize(param.getPageSize(), random ? 3 : 5);'),
  'exchangeCards should honor requested pageSize in random mode while defaulting to 3'
);
assert.ok(
  !source.includes('int pageSize = random ? 3 : normalizePageSize(param.getPageSize(), 5);'),
  'exchangeCards must not hard-code random mode pageSize to 3'
);

console.log('exchange cards page size test passed');
