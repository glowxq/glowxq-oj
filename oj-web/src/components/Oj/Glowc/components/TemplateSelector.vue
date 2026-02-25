<template>
  <div class="template-selector">
    <el-dropdown @command="handleTemplateSelect">
      <el-button type="info" size="default">
        <el-icon><component :is="currentIconComponent" /></el-icon>
        {{ buttonText }}
        <el-icon class="el-icon--right"><arrow-down /></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item
            v-for="template in templateList"
            :key="template.code"
            :command="template.code"
          >
            <el-icon><component :is="template.icon" /></el-icon>
            <span>{{ template.name }}</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue';
import { ArrowDown, Brush, Grid, List, Operation, Connection } from '@element-plus/icons-vue';
import testTemplates from '../types/test_templates';
import { DrawType } from '@/enums/oj/tuc';

// 定义类型接口
interface TemplateItem {
  code: string | number;
  name: string;
  tooltip?: string;
  description?: string;
  icon?: string;
}

interface TemplateSelectEvent {
  type: TemplateItem;
  content: string;
  replaceCode: boolean;
}

const props = defineProps({
  // 是否替换当前代码
  replaceCode: {
    type: Boolean,
    default: true
  },

  // 按钮文本
  buttonText: {
    type: String,
    default: '测试示例'
  },

  // 当前选中的模板类型
  currentTemplateType: {
    type: String,
    default: ''
  }
});

const emit = defineEmits<{
  (e: 'select-template', data: TemplateSelectEvent): void
}>();

// 将枚举对象转换为数组，方便在模板中使用
const templateList = computed<TemplateItem[]>(() => {
  return Object.values(DrawType);
});

// 当前图标组件
const currentIconComponent = computed(() => {
  if (props.currentTemplateType) {
    const template = Object.values(DrawType).find(t => t.code === props.currentTemplateType);
    return template?.icon || 'Connection';
  }
  return 'Connection';
});

// 处理模板选择
const handleTemplateSelect = (code: string) => {
  // 根据code找到对应的模板类型
  const templateType = Object.values(DrawType).find(t => t.code === code) as TemplateItem;

  if (!templateType) return;

  // 获取对应的模板内容
  let templateContent = '';

  // 确保类型安全
  const PEN = DrawType.PEN as TemplateItem;
  const ARRAY = DrawType.ARRAY as TemplateItem;
  const GRID = DrawType.GRID as TemplateItem;
  const TREE = DrawType.TREE as TemplateItem;
  const LIST = DrawType.LIST as TemplateItem;

  switch (code) {
    case PEN.code:
      templateContent = testTemplates.pen;
      break;
    case ARRAY.code:
      templateContent = testTemplates.arr;
      break;
    case GRID.code:
      templateContent = testTemplates.grid;
      break;
    case TREE.code:
      templateContent = testTemplates.tree;
      break;
    case LIST.code:
      templateContent = testTemplates.list;
      break;
  }

  // 向父组件发送事件
  emit('select-template', {
    type: templateType,
    content: templateContent,
    replaceCode: props.replaceCode
  });
};
</script>

<style scoped>
.template-selector {
  display: inline-block;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
}

:deep(.el-dropdown-menu__item .el-icon) {
  margin-right: 2px;
}
</style>
