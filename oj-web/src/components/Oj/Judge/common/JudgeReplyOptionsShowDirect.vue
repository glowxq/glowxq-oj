<template>
  <div>
    <!-- 无作答内容情况 -->
    <span v-if="!replyOptions" class="no-answer">无作答</span>
    
    <!-- 有作答内容情况 - 直接显示选项信息 -->
    <div v-else class="options-direct-wrapper">
      <div v-if="optionsArray.length === 0" class="empty-data">无作答数据</div>
      <div 
        v-for="(item, index) in optionsArray" 
        :key="index" 
        class="option-item"
        :class="{'selected-option': item.answer}"
      >
        <div class="option-header">
          <div class="option-key">
            <span class="option-key-text">选项: {{ item.optionKey || '-' }}</span>
            <el-tag v-if="item.answer" type="success" size="small" class="answer-tag">已选</el-tag>
          </div>
          <div v-if="item.answer" class="selected-icon">
            <el-icon :size="14"><Check /></el-icon>
          </div>
        </div>
        <div class="option-content">{{ item.optionContent || '-' }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElTag } from 'element-plus'
import { Check } from '@element-plus/icons-vue'

interface Props {
  /** 作答内容，可以是字符串或对象 */
  replyOptions?: string | any
}

const props = defineProps<Props>()

// 解析作答内容
const parseReplyOptions = (replyOptions: string | any) => {
  if (!replyOptions) return []
  
  try {
    const parsed = typeof replyOptions === 'string' 
      ? JSON.parse(replyOptions) 
      : replyOptions
    
    if (Array.isArray(parsed)) {
      return parsed
    } else if (parsed && typeof parsed === 'object') {
      return [parsed]
    } else {
      return []
    }
  } catch (e) {
    console.error('解析作答内容失败', e)
    return []
  }
}

// 处理后的选项数组
const optionsArray = computed(() => {
  return parseReplyOptions(props.replyOptions)
})
</script>

<style scoped lang="scss">
.no-answer {
  color: #909399;
  font-style: italic;
  font-size: 13px;
}

.options-direct-wrapper {
  padding: 8px;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
  background-color: #FFFFFF;
  
  .empty-data {
    color: #909399;
    text-align: center;
    padding: 10px;
    font-style: italic;
  }
  
  .option-item {
    background: #f8f9fa;
    border-radius: 4px;
    padding: 12px;
    margin-bottom: 10px;
    border-left: 3px solid #dcdfe6;
    transition: all 0.3s;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    &.selected-option {
      background-color: #f0f9eb;
      border-left: 3px solid #67C23A;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    }
    
    .option-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
      
      .option-key {
        font-weight: 600;
        color: #303133;
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 13px;
        
        .option-key-text {
          margin-right: 0;
        }
        
        .answer-tag {
          font-size: 11px;
          padding: 0 5px;
          height: 20px;
          line-height: 18px;
        }
      }
      
      .selected-icon {
        color: #67C23A;
        font-size: 14px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
    
    .option-content {
      color: #606266;
      background-color: white;
      padding: 10px;
      border-radius: 4px;
      border: 1px solid #EBEEF5;
      white-space: pre-wrap;
      line-height: 1.5;
      font-size: 13px;
    }
  }
}
</style> 