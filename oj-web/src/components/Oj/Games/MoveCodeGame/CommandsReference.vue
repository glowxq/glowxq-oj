<template>
  <div class="commands-reference">
    <!-- 头部 -->
    <div class="reference-header">
      <h2>📚 编程指令参考手册</h2>
      <p>MoveCodeGame 支持的所有编程指令详细说明</p>
    </div>

    <!-- 筛选器 -->
    <div class="filter-section">
      <div class="filter-tabs">
        <el-button
          v-for="level in levels"
          :key="level"
          :type="selectedLevel === level ? 'primary' : 'default'"
          @click="filterByLevel(level)"
          size="small"
          class="filter-tab"
        >
          {{ level }}
        </el-button>
      </div>
      <div class="search-box">
        <el-input
          v-model="searchQuery"
          placeholder="搜索指令..."
          prefix-icon="Search"
          size="small"
          clearable
        />
      </div>
    </div>

    <!-- 指令分类列表 -->
    <div class="commands-content">
      <div
        v-for="category in filteredCategories"
        :key="category.category"
        class="command-category"
      >
        <div class="category-header">
          <span class="category-icon">{{ category.icon }}</span>
          <h3 class="category-title">{{ category.category }}</h3>
          <span class="category-count">{{ category.commands.length }} 个指令</span>
        </div>
        <p class="category-description">{{ category.description }}</p>

        <div class="commands-grid">
          <div
            v-for="command in category.commands"
            :key="command.name"
            class="command-card"
            :class="{ 'highlighted': isHighlighted(command.name) }"
          >
            <!-- 指令头部 -->
            <div class="command-header">
              <div class="command-name">
                <code>{{ command.name }}</code>
                <el-tag
                  :color="getLevelColor(command.level)"
                  size="small"
                  class="level-tag"
                >
                  {{ command.level }}
                </el-tag>
                <span v-if="command.oldName" class="old-name">
                  (原: {{ command.oldName }})
                </span>
              </div>
            </div>

            <!-- 指令描述 -->
            <div class="command-description">
              {{ command.description }}
            </div>

            <!-- 语法 -->
            <div class="command-syntax">
              <div class="syntax-label">语法：</div>
              <code class="syntax-code">{{ command.syntax }}</code>
            </div>

            <!-- 参数说明 -->
            <div v-if="command.parameters" class="command-params">
              <div class="params-label">参数：</div>
              <div class="params-content">{{ command.parameters }}</div>
            </div>

            <!-- 返回值 -->
            <div v-if="command.returns" class="command-returns">
              <div class="returns-label">返回值：</div>
              <div class="returns-content">{{ command.returns }}</div>
            </div>

            <!-- 示例代码 -->
            <div class="command-example">
              <div class="example-label">示例：</div>
              <pre class="example-code">{{ command.example }}</pre>
            </div>

            <!-- 特殊说明 -->
            <div v-if="command.note" class="command-note">
              <el-alert
                :title="command.note"
                type="info"
                :closable="false"
                show-icon
                class="note-alert"
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="filteredCategories.length === 0" class="empty-state">
      <div class="empty-icon">🔍</div>
      <h3>未找到匹配的指令</h3>
      <p>尝试调整筛选条件或搜索关键词</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { commandsData, getLevelColor } from './commands'
import type { Command, CommandCategory } from './commands.d'

// 响应式数据
const selectedLevel = ref('全部')
const searchQuery = ref('')

// 难度级别选项
const levels = ['全部', '基础', '中级', '高级']

// 筛选方法
const filterByLevel = (level: string) => {
  selectedLevel.value = level
}

// 计算属性：筛选后的分类
const filteredCategories = computed(() => {
  let categories = [...commandsData]

  // 按难度级别筛选
  if (selectedLevel.value !== '全部') {
    categories = categories.map(category => ({
      ...category,
      commands: category.commands.filter((cmd: Command) => cmd.level === selectedLevel.value)
    })).filter(category => category.commands.length > 0)
  }

  // 按搜索关键词筛选
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    categories = categories.map(category => ({
      ...category,
      commands: category.commands.filter((cmd: Command) =>
        cmd.name.toLowerCase().includes(query) ||
        cmd.description.toLowerCase().includes(query) ||
        cmd.syntax.toLowerCase().includes(query)
      )
    })).filter(category => category.commands.length > 0)
  }

  return categories
})

// 检查指令是否被高亮
const isHighlighted = (commandName: string) => {
  if (!searchQuery.value.trim()) return false
  return commandName.toLowerCase().includes(searchQuery.value.toLowerCase())
}
</script>

<style scoped>
.commands-reference {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
}

/* 头部样式 */
.reference-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  text-align: center;
}

.reference-header h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: bold;
}

.reference-header p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

/* 筛选器样式 */
.filter-section {
  background: white;
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.filter-tabs {
  display: flex;
  gap: 10px;
}

.filter-tab {
  border-radius: 20px;
  font-weight: bold;
}

.search-box {
  width: 300px;
}

/* 内容区域 */
.commands-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

/* 分类样式 */
.command-category {
  background: white;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.category-header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  border-bottom: 1px solid #dee2e6;
}

.category-icon {
  font-size: 24px;
}

.category-title {
  margin: 0;
  font-size: 20px;
  font-weight: bold;
  color: #2c3e50;
  flex: 1;
}

.category-count {
  background: #007bff;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.category-description {
  padding: 15px 20px 0;
  margin: 0;
  color: #6c757d;
  font-size: 14px;
}

/* 指令网格 */
.commands-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  padding: 20px;
}

/* 指令卡片 */
.command-card {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s ease;
}

.command-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border-color: #007bff;
}

.command-card.highlighted {
  border-color: #ffc107;
  background: #fff3cd;
}

.command-header {
  margin-bottom: 15px;
}

.command-name {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.command-name code {
  font-size: 16px;
  font-weight: bold;
  color: #e83e8c;
  background: white;
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.level-tag {
  border: none;
  color: white;
  font-weight: bold;
}

.old-name {
  font-size: 12px;
  color: #999;
  margin-left: 8px;
  font-style: italic;
}

.optional-tag {
  font-size: 11px;
  color: #f39c12;
  background: rgba(243, 156, 18, 0.1);
  padding: 1px 4px;
  border-radius: 3px;
  margin-left: 4px;
  margin-right: 4px;
}

.command-description {
  color: #495057;
  margin-bottom: 15px;
  line-height: 1.5;
}

.command-syntax,
.command-params,
.command-returns,
.command-example {
  margin-bottom: 15px;
}

.syntax-label,
.params-label,
.returns-label,
.example-label {
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 8px;
  font-size: 14px;
}

.syntax-code {
  background: #2c3e50;
  color: #f8f9fa;
  padding: 8px 12px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
  display: block;
}

.params-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.param-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.param-name {
  background: #e9ecef;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  color: #495057;
}

.param-type {
  color: #6c757d;
  font-style: italic;
}

.param-desc {
  color: #495057;
}

.returns-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.returns-type {
  color: #6c757d;
  font-style: italic;
}

.returns-desc {
  color: #495057;
}

.example-code {
  background: #2c3e50;
  color: #f8f9fa;
  padding: 12px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.4;
  margin: 0;
  overflow-x: auto;
}

.command-note {
  margin-top: 15px;
}

.note-alert {
  border-radius: 6px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #6c757d;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.empty-state h3 {
  margin: 0 0 10px 0;
  color: #495057;
}

.empty-state p {
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-section {
    flex-direction: column;
    gap: 15px;
  }

  .search-box {
    width: 100%;
  }

  .commands-grid {
    grid-template-columns: 1fr;
  }

  .category-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}

/* 滚动条样式 */
.commands-content::-webkit-scrollbar {
  width: 6px;
}

.commands-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.commands-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.commands-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
