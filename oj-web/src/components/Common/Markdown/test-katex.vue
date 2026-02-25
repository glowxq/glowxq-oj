<template>
  <div class="test-katex-container">
    <h1>KaTeX本地安装测试</h1>
    
    <div class="test-section">
      <h2>测试内容</h2>
      <MdPreview
        :model-value="testContent"
        height="400px"
        :no-katex="false"
      />
    </div>
    
    <div class="info-section">
      <h3>配置信息</h3>
      <p>✅ KaTeX已通过npm安装: v0.16.22</p>
      <p>✅ 使用本地安装的KaTeX实例，不依赖CDN</p>
      <p>✅ CSS样式已直接导入</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import MdPreview from './MdPreview.vue'

const testContent = ref(`# KaTeX本地安装测试

## 行内公式测试
这是一个简单的行内公式：$E = mc^2$

这是一个更复杂的行内公式：$\\sum_{i=1}^{n} x_i = \\frac{n(n+1)}{2}$

## 块级公式测试

$$
\\begin{aligned}
\\nabla \\times \\vec{\\mathbf{B}} -\\, \\frac1c\\, \\frac{\\partial\\vec{\\mathbf{E}}}{\\partial t} &= \\frac{4\\pi}{c}\\vec{\\mathbf{j}} \\\\
\\nabla \\cdot \\vec{\\mathbf{E}} &= 4 \\pi \\rho \\\\
\\nabla \\times \\vec{\\mathbf{E}}\\, +\\, \\frac1c\\, \\frac{\\partial\\vec{\\mathbf{B}}}{\\partial t} &= \\vec{\\mathbf{0}} \\\\
\\nabla \\cdot \\vec{\\mathbf{B}} &= 0
\\end{aligned}
$$

## 矩阵测试

$$
\\begin{pmatrix}
a & b \\\\
c & d
\\end{pmatrix}
\\begin{pmatrix}
x \\\\
y
\\end{pmatrix}
=
\\begin{pmatrix}
ax + by \\\\
cx + dy
\\end{pmatrix}
$$

## 积分测试

$$
\\int_{-\\infty}^{\\infty} e^{-x^2} dx = \\sqrt{\\pi}
$$

如果以上公式都能正确显示，说明KaTeX本地安装配置成功！
`)
</script>

<style lang="scss" scoped>
.test-katex-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.test-section {
  margin: 20px 0;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
}

.info-section {
  margin: 20px 0;
  padding: 15px;
  background-color: #f0f9ff;
  border-radius: 8px;
  border-left: 4px solid #0ea5e9;
  
  h3 {
    margin-top: 0;
    color: #0369a1;
  }
  
  p {
    margin: 8px 0;
    color: #0c4a6e;
  }
}
</style>
