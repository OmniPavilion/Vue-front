import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import {ElementPlusResolver} from 'unplugin-vue-components/resolvers'
import {fileURLToPath, URL} from 'node:url'
import {readdirSync, statSync} from 'node:fs'
import {join} from 'node:path'

// 自动查找 src 子目录中的 index.html 文件
function findHtmlEntries(dir: string): Record<string, string> {
    const entries: Record<string, string> = {}
    const srcPath = fileURLToPath(new URL('./src', import.meta.url))

    const scanDirectory = (currentDir: string, basePath = '') => {
        const files = readdirSync(currentDir)

        files.forEach(file => {
            const fullPath = join(currentDir, file)
            const stat = statSync(fullPath)

            if (stat.isDirectory()) {
                scanDirectory(fullPath, basePath ? `${basePath}/${file}` : file)
            } else if (file === 'index.html') {
                const relativePath = fullPath.replace(srcPath, '').replace(/\\/g, '/')
                const entryName = basePath || relativePath.split('/')[1] || 'main'
                entries[entryName] = fileURLToPath(new URL(`./src${relativePath}`, import.meta.url))
            }
        })
    }

    scanDirectory(dir)
    return entries
}

export default defineConfig({
    base: './',
    server: {
        port: 8050,
        strictPort: true,
    },
    build: {
        rollupOptions: {
            input: findHtmlEntries(fileURLToPath(new URL('./src', import.meta.url)))
        }
    },
    plugins: [
        vue(),
        AutoImport({
            resolvers: [ElementPlusResolver()],
        }),
        Components({
            resolvers: [ElementPlusResolver({importStyle: "sass"})],
        }),
        {
            name: 'rewrite-routes',
            configureServer(server) {
                const entries = findHtmlEntries(fileURLToPath(new URL('./src', import.meta.url)))

                server.middlewares.use((req, res, next) => {
                    if (!req.url) return next()

                    // 处理根路径重定向
                    if (req.url === '/') {
                        const defaultEntry = Object.keys(entries)[0] || 'application'
                        return res.writeHead(302, {
                            Location: `/${defaultEntry}/`
                        }).end()
                    }

                    // 处理带斜杠的路径
                    const [_, route] = req.url.match(/^\/([^\/]+)/) || []

                    if (route && entries[route]) {
                        // 确保路径以斜杠结尾，避免相对路径问题
                        if (!req.url.endsWith('/')) {
                            return res.writeHead(302, {
                                Location: `${req.url}/`
                            }).end()
                        }

                        // 重写为实际文件路径
                        req.url = `/src/${route}/index.html`
                    } else if (!req.url.startsWith('/@') && !req.url.startsWith('/src/') && !req.url.includes('.')) {
                        // 未匹配到入口且不是静态资源，直接返回 404
                        req.url = `/src/noFound/index.html`
                    }

                    next()
                })
            }
        }
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        },
    },
    css: {
        preprocessorOptions: {
            scss: {
                additionalData: `
        `,
            }
        }
    }
})
