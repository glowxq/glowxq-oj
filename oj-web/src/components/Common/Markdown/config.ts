/**
 * Markdown编辑器配置文件
 *
 * 用于配置md-editor-v3的XSS防护和其他全局设置
 */

// 正确导入md-editor-v3的config
import { config } from 'md-editor-v3'

// 导入本地安装的KaTeX
import katex from 'katex'
import 'katex/dist/katex.min.css'

/**
 * 配置KaTeX使用本地安装的版本
 */
export function setupKaTeX() {
  try {
    // 配置md-editor-v3使用本地安装的KaTeX
    // 使用类型断言来避免TypeScript错误
    ;(config as any).editorExtensions = {
      katex: {
        instance: katex,
        js: '',  // 空字符串表示不从CDN加载
        css: ''  // 空字符串表示不从CDN加载
      }
    }

    console.log('KaTeX本地配置成功')
  } catch (error) {
    console.error('配置KaTeX失败:', error)
  }
}

/**
 * 配置Markdown编辑器XSS防护
 *
 * 使用md-editor-v3内置的XSS插件，通过扩展白名单的方式更加安全和优雅地处理XSS防护
 */
export function setupMdEditor() {
  try {
    // 首先配置KaTeX
    setupKaTeX()

    // 配置md-editor-v3的XSS防护
    if (config.markdownItConfig) {
      config.markdownItConfig.plugins = config.markdownItConfig.plugins || []

      // 添加XSS防护配置
      const xssConfig = {
        type: 'xss',
        options: {
          // 使用扩展白名单模式，在md-editor-v3 4.15.6版本后支持
          extendedWhiteList: {
            // 在这里可以添加额外允许的标签和属性
            // 示例:
            // a: ['target', 'rel'],          // 允许a标签的target和rel属性
            // img: ['data-src', 'loading'],  // 允许img标签的data-src和loading属性
          },
        },
      }

      config.markdownItConfig.plugins.push(xssConfig)
    }

    console.log('Markdown编辑器XSS防护配置成功')
  } catch (error) {
    console.error('配置Markdown编辑器XSS防护失败:', error)
  }
}

// 导出XSS配置函数
export default setupMdEditor
