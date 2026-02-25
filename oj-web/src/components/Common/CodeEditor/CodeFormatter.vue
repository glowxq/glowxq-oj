<template>
  <el-button type="primary" size="small" @click="formatCode" :disabled="disabled">
    <el-icon><Crop /></el-icon>
    {{ buttonText }}
  </el-button>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { Crop } from '@element-plus/icons-vue';
import { EditorView } from '@codemirror/view';
import { indentSelection } from '@codemirror/commands';

const props = defineProps({
  // 编辑器视图实例
  editorView: {
    type: Object as () => EditorView | null,
    required: true
  },
  // 当前语言
  language: {
    type: String,
    default: 'javascript'
  },
  // Tab 大小
  tabSize: {
    type: Number,
    default: 2
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 按钮文本
  buttonText: {
    type: String,
    default: '格式化'
  }
});

// 格式化代码 - 简化实现以确保可靠
const formatCode = async () => {
  if (!props.editorView || props.disabled) return;
  
  try {
    console.log("开始格式化代码...");
    const view = props.editorView;
    const doc = view.state.doc;
    const text = doc.toString();
    
    // 查看实际需要格式化的内容长度
    console.log(`原始文本长度: ${text.length}`);
    
    // 首先尝试使用CodeMirror内置的自动缩进
    indentSelection({
      state: view.state, 
      dispatch: view.dispatch
    });
    
    // 基本文本处理
    let formattedText = text;
    
    // 处理行尾空格和多余空行
    formattedText = removeTrailingWhitespace(formattedText);
    formattedText = normalizeEmptyLines(formattedText);
    console.log("完成空格和空行处理");
    
    // 根据语言应用格式化
    switch (props.language.toLowerCase()) {
      case 'javascript':
      case 'typescript':
      case 'jsx':
      case 'tsx':
        formattedText = formatJS(formattedText);
        break;
        
      case 'python':
        formattedText = formatPython(formattedText);
        break;
        
      case 'java':
      case 'cpp':
      case 'c':
      case 'cs':
      case 'rust':
        formattedText = formatCStyle(formattedText);
        break;
        
      default:
        formattedText = formatGeneric(formattedText);
    }
    
    console.log(`格式化后文本长度: ${formattedText.length}`);
    
    // 只有在文本确实改变的情况下才更新
    if (formattedText !== text) {
      console.log("文本已更改，正在更新编辑器...");
      
      // 直接替换整个文档内容
      view.dispatch({
        changes: {
          from: 0,
          to: doc.length,
          insert: formattedText
        }
      });
      
      // 格式化成功提示
      ElMessage.success({
        message: '代码格式化成功',
        duration: 2000
      });
    } else {
      console.log("文本未更改");
      ElMessage.info({
        message: '代码已经格式化',
        duration: 2000
      });
    }
  } catch (error) {
    console.error('代码格式化失败:', error);
    // 显示错误提示
    ElMessage.error({
      message: '代码格式化失败，请检查代码语法是否正确',
      duration: 3000
    });
  }
};

// 移除行尾空格 - 简单可靠的实现
const removeTrailingWhitespace = (text: string): string => {
  return text.replace(/[ \t]+$/gm, '');
};

// 标准化空行（最多保留两个连续空行）
const normalizeEmptyLines = (text: string): string => {
  return text.replace(/\n{3,}/g, '\n\n');
};

// JavaScript格式化 - 简化版本
const formatJS = (text: string): string => {
  let result = text;
  
  // 处理基本格式
  // 1. 确保运算符前后有空格
  result = result.replace(/([^=<>!+\-*/%&|^])(=|==|===|!=|!==|>=|<=|>|<|\+|-|\*|\/|%|\|\||&&)([^=<>!+\-*/%&|^])/g, '$1 $2 $3');
  
  // 2. 确保逗号后有空格
  result = result.replace(/,([^\s\n\r])/g, ', $1');
  
  // 3. 确保冒号后有空格（对象和三元运算符）
  result = result.replace(/:([^\s\n\r])/g, ': $1');
  
  // 4. 确保分号后有空格
  result = result.replace(/;([^\s\n\r})])/g, '; $1');
  
  // 5. 关键字后面应该有空格
  const jsKeywords = ['if', 'else', 'for', 'while', 'function', 'return', 'var', 'let', 'const', 'class', 'new', 'typeof', 'instanceof'];
  for (const keyword of jsKeywords) {
    // 关键字后应该有空格，但排除关键字后紧跟括号的情况
    const re = new RegExp(`\\b${keyword}\\b([^\\s({])`, 'g');
    result = result.replace(re, `${keyword} $1`);
  }
  
  return result;
};

// Python格式化 - 简化版本
const formatPython = (text: string): string => {
  let result = text;
  
  // 1. 确保运算符前后有空格
  result = result.replace(/([^\s=<>!+\-*/%&|^])(=|==|!=|>=|<=|>|<|\+|-|\*|\/|%|\||&)([^\s=<>!+\-*/%&|^])/g, '$1 $2 $3');
  
  // 2. 确保逗号后有空格
  result = result.replace(/,([^\s\n\r])/g, ', $1');
  
  // 3. 确保冒号后有空格（但不处理字典键值对或切片操作）
  result = result.replace(/([^:\s]):([^\s\d}])/g, '$1: $2');
  
  // 4. Python关键字的处理
  const pyKeywords = ['if', 'elif', 'else', 'for', 'while', 'def', 'class', 'with', 'as', 'and', 'or', 'not', 'in', 'is'];
  for (const keyword of pyKeywords) {
    // 关键字后面应该有空格
    const reAfter = new RegExp(`\\b${keyword}\\b([^\\s:])`, 'g');
    result = result.replace(reAfter, `${keyword} $1`);
    
    // 关键字前面应该有空格（排除行首）
    if (keyword !== 'not') { // 'not'可能在行首
      const reBefore = new RegExp(`([^\\s])\\b${keyword}\\b`, 'g');
      result = result.replace(reBefore, `$1 ${keyword}`);
    }
  }
  
  return result;
};

// C风格代码格式化 - 简化版本
const formatCStyle = (text: string): string => {
  let result = text;
  
  // 1. 确保运算符前后有空格
  result = result.replace(/([^=<>!+\-*/%&|^])(=|==|===|!=|!==|>=|<=|>|<|\+|-|\*|\/|%|\|\||&&)([^=<>!+\-*/%&|^])/g, '$1 $2 $3');
  
  // 2. 确保逗号后有空格
  result = result.replace(/,([^\s\n\r])/g, ', $1');
  
  // 3. 确保分号后有空格
  result = result.replace(/;([^\s\n\r})])/g, '; $1');
  
  // 4. 关键字后应该有空格
  const cKeywords = ['if', 'else', 'for', 'while', 'switch', 'case', 'return', 'break', 'continue', 'struct', 'enum', 'typedef', 'const', 'static'];
  for (const keyword of cKeywords) {
    // 关键字后应该有空格，排除关键字后紧跟括号的情况
    const re = new RegExp(`\\b${keyword}\\b([^\\s({])`, 'g');
    result = result.replace(re, `${keyword} $1`);
  }
  
  return result;
};

// 通用格式化 - 最基本的格式化规则
const formatGeneric = (text: string): string => {
  let result = text;
  
  // 1. 确保逗号后有空格
  result = result.replace(/,([^\s\n\r])/g, ', $1');
  
  // 2. 常见运算符前后加空格
  result = result.replace(/([^\s=<>!+\-*/%&|^])(=|>|<|\+|-|\*|\/)([^\s=<>!+\-*/%&|^])/g, '$1 $2 $3');
  
  return result;
};

// 暴露组件方法
defineExpose({
  formatCode
});
</script>

<style scoped>
/* 如果需要自定义样式 */
</style> 