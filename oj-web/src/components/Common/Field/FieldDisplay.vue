<template>
  <div class="field-display">
    <!-- 枚举类型 -->
    <template v-if="type === 'enum' && enumObj">
      <EnumShow 
        :enum="enumObj" 
        :code="modelValue"
        v-bind="enumProps"
      />
    </template>
    
    <!-- 图片类型 -->
    <template v-else-if="type === 'image'">
      <el-image 
        :src="modelValue" 
        :preview-src-list="[modelValue]" 
        fit="cover"
        class="field-image"
        v-bind="imageProps"
      >
        <template #error>
          <div class="image-error">
            <el-icon><Picture /></el-icon>
            <span>无图片</span>
          </div>
        </template>
      </el-image>
    </template>
    
    <!-- 图片集类型 -->
    <template v-else-if="type === 'imageGallery'">
      <div class="image-gallery">
        <el-image 
          v-for="(url, index) in processedGallery" 
          :key="index"
          :src="url" 
          :preview-src-list="processedGallery"
          :initial-index="index"
          fit="cover"
          class="gallery-image"
          v-bind="imageProps"
        >
          <template #error>
            <div class="image-error">
              <el-icon><Picture /></el-icon>
            </div>
          </template>
        </el-image>
        <div v-if="processedGallery.length === 0" class="no-gallery">
          <el-icon><PictureFilled /></el-icon>
          <span>无图集</span>
        </div>
      </div>
    </template>
    
    <!-- 日期时间类型 -->
    <template v-else-if="type === 'datetime'">
      <span>{{ formatDateTime(modelValue) }}</span>
    </template>
    
    <!-- 日期类型 -->
    <template v-else-if="type === 'date'">
      <span>{{ formatDate(modelValue) }}</span>
    </template>
    
    <!-- 价格类型 -->
    <template v-else-if="type === 'price'">
      <span class="field-price">¥ {{ formatPrice(modelValue) }}</span>
    </template>
    
    <!-- 开关类型 -->
    <template v-else-if="type === 'switch'">
      <el-tag 
        :type="modelValue ? 'success' : 'danger'" 
        size="small"
      >
        {{ modelValue ? '是' : '否' }}
      </el-tag>
    </template>
    
    <!-- 标签类型 -->
    <template v-else-if="type === 'tag'">
      <el-tag 
        :type="tagType" 
        size="small"
        v-bind="tagProps"
      >
        {{ modelValue }}
      </el-tag>
    </template>
    
    <!-- 长文本类型 -->
    <template v-else-if="type === 'longText'">
      <el-tooltip
        :content="modelValue"
        placement="top"
        :disabled="!modelValue || modelValue.length < 20"
      >
        <div class="long-text">{{ modelValue || placeholder }}</div>
      </el-tooltip>
    </template>
    
    <!-- 默认为文本类型 -->
    <template v-else>
      <span>{{ modelValue || placeholder }}</span>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Picture, PictureFilled } from '@element-plus/icons-vue'
import EnumShow from '@/components/Common/Enum/EnumShow.vue'
import type { BaseEnum } from '@/enums/base'

// 定义组件Props
interface Props {
  // 组件绑定值
  modelValue: any
  // 字段类型
  type: 'text' | 'enum' | 'image' | 'imageGallery' | 'datetime' | 'date' | 'price' | 'switch' | 'tag' | 'longText'
  // 枚举对象（当type为enum时必须提供）
  enumObj?: { [key: string]: BaseEnum }
  // 枚举组件额外属性
  enumProps?: Record<string, any>
  // 图片组件额外属性
  imageProps?: Record<string, any>
  // 标签类型
  tagType?: 'success' | 'warning' | 'danger' | 'info' | 'primary'
  // 标签组件额外属性
  tagProps?: Record<string, any>
  // 无值时的占位符
  placeholder?: string
}

// 设置Props默认值
const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  placeholder: '无数据',
  tagType: 'primary'
})

// 处理图片集数据
const processedGallery = computed(() => {
  if (!props.modelValue) return []
  
  try {
    // 尝试解析为JSON字符串
    if (typeof props.modelValue === 'string') {
      if (props.modelValue.startsWith('[')) {
        return JSON.parse(props.modelValue)
      } else {
        // 否则视为逗号分隔的URL
        return props.modelValue.split(',').filter(url => url.trim())
      }
    }
    
    // 如果是数组直接返回
    if (Array.isArray(props.modelValue)) {
      return props.modelValue
    }
    
    return []
  } catch (e) {
    return []
  }
})

// 格式化价格
const formatPrice = (price: number | string) => {
  if (price === undefined || price === null) return '0.00'
  
  const numPrice = typeof price === 'string' ? parseFloat(price) : price
  return numPrice.toFixed(2)
}

// 格式化日期时间
const formatDateTime = (datetime: string) => {
  if (!datetime) return props.placeholder
  
  try {
    const date = new Date(datetime)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (e) {
    return datetime
  }
}

// 格式化日期
const formatDate = (date: string) => {
  if (!date) return props.placeholder
  
  try {
    const dateObj = new Date(date)
    return dateObj.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })
  } catch (e) {
    return date
  }
}
</script>

<style scoped lang="scss">
.field-display {
  display: inline-block;
  
  .field-image {
    width: 60px;
    height: 60px;
    border-radius: 4px;
    object-fit: cover;
  }
  
  .image-gallery {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
    
    .gallery-image {
      width: 50px;
      height: 50px;
      border-radius: 4px;
      object-fit: cover;
    }
    
    .no-gallery {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #909399;
      font-size: 12px;
      width: 50px;
      height: 50px;
      background-color: #f5f7fa;
      border-radius: 4px;
      
      .el-icon {
        font-size: 18px;
        margin-bottom: 2px;
      }
    }
  }
  
  .image-error {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #909399;
    font-size: 12px;
    height: 100%;
    background-color: #f5f7fa;
    
    .el-icon {
      font-size: 18px;
      margin-bottom: 2px;
    }
  }
  
  .field-price {
    color: #f56c6c;
    font-weight: 600;
  }
  
  .long-text {
    max-width: 200px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style> 