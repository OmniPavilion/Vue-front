import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import {fileURLToPath} from "node:url";

// https://vite.dev/config/
export default defineConfig({
    server: {
        port: 8050,
        strictPort: true,
    },
    build: {
        rollupOptions: {
            input: {
                application: fileURLToPath(new URL('./src/application/index.html', import.meta.url)),
                sokoban: fileURLToPath(new URL('./src/diary/index.html', import.meta.url)),
                sudoku: fileURLToPath(new URL('./src/music/index.html', import.meta.url))
            }
        }
    },
    plugins: [
        vue(),
        // 配置ele-plus按需导入
        AutoImport({ // 配置自动导入插件，指定   ElementPlusResolver   解析器
            resolvers: [ElementPlusResolver()],
        }),
        Components({
            resolvers: [ // 配置自动导入组件，指定   ElementPlusResolver   解析器，
                // 并设置   importStyle   为   "sass"  ，以支持 SCSS 预处理器。
                ElementPlusResolver({ importStyle: "sass"})],
        }),
        {
            name: 'rewrite-routes',
            configureServer(server) {
                server.middlewares.use((req, res, next) => {
                    if (req.url == null) {
                        return next()
                    }

                    if (req.url === '/') {
                        res.writeHead(302, {
                            Location: '/application' // 重定向到目标路径
                        })
                        return res.end() // ← 显式 return 终止流程
                    }

                    if(req.url === '/application') {
                        req.url = './src/application/index.html'
                    } else if (req.url.startsWith('/music')) {
                        req.url = './src/music/index.html'
                    }
                    next()
                })
            }
        }
    ],
    resolve: {
        alias: { // 定义路径别名
            // 路径转化，把@转化成src路径
            '@': fileURLToPath(new URL('./src', import.meta.url))
        },
    },
    css: {
        preprocessorOptions: { // 配置预处理器选项
            scss: {
                // 自动导入定制化样式文件进行样式覆盖
                additionalData: //  自动导入   scss   文件，以便进行样式覆盖
                    `
        `,
            }
        }
    }
})

