<template>
  <div class="settings-panel">
    <el-dialog
      v-model="dialogVisible"
      :title="title"
      :width="width"
      :close-on-click-modal="closeOnClickModal"
      :append-to-body="appendToBody"
      class="tuc-settings-dialog"
    >
      <div class="settings-content">
        <el-tabs type="border-card" v-model="activeTab">
          <el-tab-pane
            v-for="tab in settingTabs"
            :key="tab.code"
            :label="tab.tooltip"
            :name="tab.code"
          >
            <component
              :is="getTabComponent(tab.code)"
              :speed-values="speedValues"
              :config-values="configValues"
              :line-width-value="lineWidthValue"
              :color-map="colorMap"
              @speed-change="handleSpeedChange"
              @slider-change="handleSliderChange"
              @reset-speed="resetSpeed"
              @line-width-change="handleLineWidthChange"
              @config-change="handleConfigChange"
            />
          </el-tab-pane>
        </el-tabs>

        <div class="dialog-footer">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleApply">应用</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, defineProps, defineEmits, onMounted, watchEffect, watch } from 'vue';
import { Brush, Grid, List, Operation, Connection, Refresh } from '@element-plus/icons-vue';
import { Magnet as ColorPicker } from '@element-plus/icons-vue';
import { DrawType } from '@/enums/oj/tuc';
import PenSettings from '@/components/Oj/Glowc/components/settings/PenSettings.vue';
import ArraySettings from '@/components/Oj/Glowc/components/settings/ArraySettings.vue';
import GridSettings from '@/components/Oj/Glowc/components/settings/GridSettings.vue';
import TreeSettings from '@/components/Oj/Glowc/components/settings/TreeSettings.vue';
import ListSettings from '@/components/Oj/Glowc/components/settings/ListSettings.vue';
import ColorMap from '@/components/Oj/Glowc/components/settings/ColorMap.vue';

// 定义设置项类型接口
interface SettingItem {
  code: string | number;  // 修改为接受string或number类型，与DrawTypeEnum匹配
  name: string;
  tooltip?: string;
  description?: string;
  icon?: string;
}

// 定义速度配置接口
interface SpeedValues {
  pen: number;
  arr: number;
  grid: number;
  tree: number;
  list: number;
}

// 定义工具配置接口
interface ConfigValues {
  pen: Record<string, any>;
  arr: Record<string, any>;
  grid: Record<string, any>;
  tree: Record<string, any>;
  list: Record<string, any>;
}

// 定义颜色映射项接口
interface ColorMapItem {
  id: number;
  name: string;
  value: string;
}

const props = defineProps({
  // 对话框标题
  title: {
    type: String,
    default: 'TuC设置面板'
  },

  // 对话框宽度
  width: {
    type: String,
    default: '75%'
  },

  // 点击蒙层是否关闭
  closeOnClickModal: {
    type: Boolean,
    default: true
  },

  // 是否插入到body
  appendToBody: {
    type: Boolean,
    default: true
  },

  // 对话框是否可见
  visible: {
    type: Boolean,
    default: false
  },

  // 初始激活的标签页
  defaultActiveTab: {
    type: [String, Number],
    default: 'pen'
  },

  // 速度值
  speedValues: {
    type: Object as () => SpeedValues,
    required: true
  },

  // 配置值
  configValues: {
    type: Object as () => ConfigValues,
    required: true
  },

  // 线宽值
  lineWidthValue: {
    type: Number,
    default: 2
  },

  // 颜色映射
  colorMap: {
    type: Array as () => ColorMapItem[],
    required: true
  }
});

const emit = defineEmits([
  'update:visible',
  'apply',
  'cancel',
  'speed-change',
  'slider-change',
  'reset-speed',
  'line-width-change',
  'config-change'
]);

// 对话框可见状态，与props.visible双向绑定
const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
});

// 当前激活的标签页
const activeTab = ref(props.defaultActiveTab);

// 创建本地状态副本，避免直接修改props
const lineWidthValue = ref(props.lineWidthValue);
const speedValues = ref({...props.speedValues});
const configValues = ref({...props.configValues});

// 监听props变化，更新本地状态
watch(() => props.lineWidthValue, (newValue) => {
  lineWidthValue.value = newValue;
});

watch(() => props.speedValues, (newValues) => {
  speedValues.value = {...newValues};
}, { deep: true });

watch(() => props.configValues, (newValues) => {
  configValues.value = {...newValues};
}, { deep: true });

// 将枚举对象转换为数组，方便在模板中使用
const settingTabs = computed<SettingItem[]>(() => {
  return Object.values(DrawType);
});

// 监听activeTab的变化
watchEffect(() => {
  console.log('当前激活标签页：', activeTab.value);
});

// 根据tab code获取对应的组件
const getTabComponent = (code: string | number) => {
  switch (code) {
    case DrawType.PEN.code:
      return PenSettings;
    case DrawType.ARRAY.code:
      return ArraySettings;
    case DrawType.GRID.code:
      return GridSettings;
    case DrawType.TREE.code:
      return TreeSettings;
    case DrawType.LIST.code:
      return ListSettings;
    case DrawType.COLORS.code:
      return ColorMap;
    default:
      return null;
  }
};

// 处理速度变化
const handleSpeedChange = (component: string, value: number) => {
  emit('speed-change', component, value);
};

// 处理滑动条变化
const handleSliderChange = (component: string, value: number) => {
  emit('slider-change', component, value);
};

// 重置速度
const resetSpeed = (component: string) => {
  emit('reset-speed', component);
};

// 添加处理线宽变化的方法
const handleLineWidthChange = (newWidth: number) => {
  lineWidthValue.value = newWidth;
  // 向父组件转发事件
  emit('line-width-change', newWidth);
  console.log('线宽已更新为:', newWidth);
};

// 添加处理配置变化的方法
const handleConfigChange = (component: string, newConfig: any) => {
  if (component === 'pen') {
    configValues.value.pen = {...newConfig};
    // 向父组件转发事件
    emit('config-change', component, newConfig);
    console.log('画笔配置已更新:', newConfig);
  } else if (component === 'arr') {
    configValues.value.arr = {...newConfig};
    emit('config-change', component, newConfig);
  } else if (component === 'grid') {
    configValues.value.grid = {...newConfig};
    emit('config-change', component, newConfig);
  } else if (component === 'tree') {
    configValues.value.tree = {...newConfig};
    emit('config-change', component, newConfig);
  } else if (component === 'list') {
    configValues.value.list = {...newConfig};
    emit('config-change', component, newConfig);
  }
};

// 取消设置
const handleCancel = () => {
  dialogVisible.value = false;
  emit('cancel');
};

// 应用设置
const handleApply = () => {
  emit('apply');
  dialogVisible.value = false;
};

// 组件挂载时
onMounted(() => {
  // 可以在这里添加初始化逻辑
});
</script>

<style scoped>
.settings-content {
  padding: 16px;
}

.dialog-footer {
  margin-top: 16px;
  text-align: right;
}

:deep(.el-tabs__item) {
  font-size: 14px;
  padding: 0 18px;
  height: 40px;
  line-height: 40px;
  color: var(--el-color-primary-light-5);
  font-weight: 500;
}

:deep(.el-tabs__item.is-active) {
  color: var(--el-color-primary);
  font-weight: 600;
}

:deep(.el-tabs--border-card) {
  border: 1px solid #e6e6e6;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-tabs--border-card > .el-tabs__header) {
  background-color: #f5f5f7;
  border-bottom: 1px solid #e6e6e6;
}

:deep(.el-button) {
  font-size: 13px;
  border-radius: 16px;
  padding: 8px 16px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  filter: brightness(1.05);
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, var(--el-color-primary-light-3), var(--el-color-primary));
  border-color: var(--el-color-primary-dark-2);
  color: #ffffff;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  color: var(--el-color-primary);
  font-size: 18px;
}

:deep(.el-dialog__header) {
  padding: 18px;
  margin-right: 0;
  border-bottom: 1px solid #e6e6e6;
}

:deep(.el-dialog__body) {
  padding: 16px;
}

:deep(.el-dialog) {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

:deep(.el-slider__runway) {
  background-color: #e6e6e6;
  height: 6px;
  border-radius: 3px;
}

:deep(.el-slider__bar) {
  background-color: var(--el-color-primary);
  height: 6px;
  border-radius: 3px;
}

:deep(.el-slider__button) {
  border-color: var(--el-color-primary);
  width: 16px;
  height: 16px;
  transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
}

:deep(.el-slider__button:hover) {
  transform: scale(1.1);
  box-shadow: 0 0 0 5px rgba(var(--el-color-primary-rgb), 0.1);
}

:deep(.el-select-dropdown__item.selected) {
  color: var(--el-color-primary);
  font-weight: 600;
}

:deep(.el-form-item__label) {
  color: #1d1d1f;
  font-weight: 500;
  font-size: 14px;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 0 12px;
  height: 36px;
  transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(var(--el-color-primary-rgb), 0.2);
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-select-dropdown) {
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

:deep(.el-select-dropdown__item) {
  height: 36px;
  line-height: 36px;
  padding: 0 12px;
  transition: all 0.2s ease;
}

:deep(.el-select-dropdown__item:hover) {
  background-color: rgba(var(--el-color-primary-rgb), 0.1);
}

@media (prefers-color-scheme: dark) {
  :deep(.el-dialog) {
    background-color: rgba(30, 30, 32, 0.9);
  }

  :deep(.el-tabs--border-card) {
    background-color: rgba(35, 35, 37, 0.8);
    border-color: rgba(255, 255, 255, 0.05);
  }

  :deep(.el-tabs--border-card > .el-tabs__header) {
    background-color: rgba(45, 45, 47, 0.8);
    border-color: rgba(255, 255, 255, 0.05);
  }

  :deep(.el-dialog__header) {
    border-color: rgba(255, 255, 255, 0.05);
  }

  :deep(.el-tabs__item) {
    color: #f5f5f7;
  }

  :deep(.el-slider__runway) {
    background-color: rgba(150, 150, 150, 0.2);
  }

  :deep(.el-form-item__label) {
    color: #f5f5f7;
  }

  :deep(.el-dialog__title) {
    color: #f5f5f7;
  }

  :deep(.el-input__wrapper) {
    background-color: rgba(60, 60, 64, 0.7);
    border-color: rgba(255, 255, 255, 0.1);
  }

  :deep(.el-button) {
    color: #ffffff;
    font-weight: 600;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  }

  :deep(.el-button--primary) {
    background: linear-gradient(135deg, var(--el-color-primary-light-3), var(--el-color-primary));
    border-color: var(--el-color-primary);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  }
}
</style>
