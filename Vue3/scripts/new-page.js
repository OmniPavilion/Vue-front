const fs = require('fs');
const path = require('path');

const pageName = process.argv[2];
if (!pageName) {
  console.error('请提供页面名称，如 npm run new:page mypage');
  process.exit(1);
}

const srcDir = path.resolve(__dirname, '../src');
const templateDir = path.resolve(srcDir, 'template');
const pageDir = path.resolve(srcDir, pageName);

if (fs.existsSync(pageDir)) {
  console.error('页面目录已存在:', pageDir);
  process.exit(1);
}

fs.mkdirSync(pageDir);
['index.html', 'main.ts', 'App.vue'].forEach(file => {
  fs.copyFileSync(path.join(templateDir, file), path.join(pageDir, file));
});

['components', 'assets', 'api', 'router', 'stores', 'styles', 'utils', 'types'].forEach(dir => {
  fs.mkdirSync(path.join(pageDir, dir));
});

console.log('新页面已创建:', pageDir); 