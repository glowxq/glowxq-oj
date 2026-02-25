<template>
  <!-- 可折叠的命令内容展示区域 -->
  <div class="commands-panel">
    <div class="commands-header" @click="togglePanel">
      <el-icon :class="{ 'is-rotate': modelValue }"><ArrowUp /></el-icon>
      <span>指令帮助</span>
    </div>
    <div v-show="modelValue" class="commands-content-panel">
      <el-tabs v-model="activeTab" type="border-card" size="small">
        <!-- C++语法标签页 -->
        <el-tab-pane label="C++语法" name="syntax">
          <div class="commands-panel-grid">
            <div 
              v-for="template in getTemplatesByGroup(TemplateGroupType.SYNTAX)" 
              :key="template.label" 
              class="cmd-item"
              @click="insertCode(template.content)"
            >
              {{ template.label }}
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 组件标签页 -->
        <el-tab-pane 
          v-for="tab in filteredDrawTypeTabs" 
          :key="tab.code" 
          :label="getTabLabel(tab)" 
          :name="tab.code"
        >
          <div class="commands-panel-groups">
            <template v-for="(group, index) in getCommandGroups(tab.code)" :key="index">
              <div class="cmd-group-title">{{ group.title }}</div>
              <div class="commands-panel-grid">
                <div 
                  v-for="(cmd, cmdIndex) in group.commands" 
                  :key="cmdIndex"
                  class="cmd-item"
                  @click="insertCode(cmd.code)"
                >
                  {{ cmd.label }}
                </div>
              </div>
            </template>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { ArrowUp } from '@element-plus/icons-vue';
import { 
  ComponentType, 
  TemplateGroupType, 
  COMMANDS_CONFIG, 
  getTemplatesByGroup as getTemplatesByGroupFn 
} from '../config/commandsConfig';

// 定义属性
interface Props {
  modelValue: boolean;  // v-model绑定值，控制面板显示/隐藏
  defaultActiveTab?: string;
  codeEditorRef?: any;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: true,
  defaultActiveTab: 'pen',
  codeEditorRef: null
});

// 定义事件
const emit = defineEmits(['update:modelValue', 'insert-code']);

// 状态变量
const activeTab = ref(props.defaultActiveTab);

// 切换面板显示状态
const togglePanel = () => {
  emit('update:modelValue', !props.modelValue);
};

// 从CommandsDialog组件获取标签页相关功能
const drawTypeTabs = computed(() => {
  return [
    { code: ComponentType.PEN, name: '画笔' },
    { code: ComponentType.ARRAY, name: '一维数组' },
    { code: ComponentType.GRID, name: '二维数组' },
    { code: ComponentType.TREE, name: '树结构' },
    { code: ComponentType.LIST, name: '链表' }
  ];
});

// 获取不包含颜色标签页的标签列表
const filteredDrawTypeTabs = computed(() => {
  return drawTypeTabs.value.filter(tab => tab.code !== ComponentType.COLORS);
});

// 获取标签页标题
const getTabLabel = (tab: { code: string; name: string }) => {
  switch (tab.code) {
    case ComponentType.PEN:
      return '画笔';
    case ComponentType.ARRAY:
      return '一维数组';
    case ComponentType.GRID:
      return '二维数组';
    case ComponentType.TREE:
      return '树结构';
    case ComponentType.LIST:
      return '链表';
    default:
      return tab.name;
  }
};

// 获取指定类型的命令组
const getCommandGroups = (code: string) => {
  return COMMANDS_CONFIG[code] || [];
};

// 从模板组中获取模板
const getTemplatesByGroup = (group: string) => {
  return getTemplatesByGroupFn(group);
};

// 插入代码
const insertCode = (code: string) => {
  if (props.codeEditorRef) {
    try {
      // 尝试获取编辑器视图
      const editorView = props.codeEditorRef.getEditorView?.();
      if (editorView) {
        const cursorPosition = editorView.state.selection.main.head;
        // 使用编辑器API在光标位置插入内容
        editorView.dispatch({
          changes: {
            from: cursorPosition,
            to: cursorPosition,
            insert: code
          }
        });
        return;
      }
      
      // 如果没有getEditorView方法，尝试使用setValue方法
      const currentCode = props.codeEditorRef.getValue?.() || '';
      props.codeEditorRef.setValue?.(currentCode + code);
    } catch (error) {
      console.error('插入代码时出错:', error);
      emit('insert-code', code);
    }
  } else {
    emit('insert-code', code);
  }
};

// 监听属性变化
watch(() => props.defaultActiveTab, (newValue) => {
  activeTab.value = newValue;
});
</script>

<style scoped>
/* 可折叠的命令内容展示区域 */
.commands-panel {
  margin-top: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  background-color: #f8f9fa;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.commands-header {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 10px 15px;
  border-bottom: 1px solid #dcdfe6;
  background-color: #f0f2f5;
  color: #303133;
  font-weight: 600;
  transition: all 0.3s;
  border-radius: 8px 8px 0 0;
}

.commands-header:hover {
  background-color: #e6e8eb;
}

.commands-header .el-icon {
  margin-right: 8px;
  transition: transform 0.3s;
}

.commands-header .is-rotate {
  transform: rotate(180deg);
}

.commands-content-panel {
  max-height: 200px;
  overflow-y: auto;
  padding-bottom: 5px;
}

:deep(.el-tabs__content) {
  max-height: 160px;
  overflow-y: auto;
  padding-right: 5px;
  scrollbar-width: thin;
}

:deep(.el-tabs__content::-webkit-scrollbar) {
  width: 6px;
}

:deep(.el-tabs__content::-webkit-scrollbar-track) {
  background: #f2f3f5;
  border-radius: 3px;
}

:deep(.el-tabs__content::-webkit-scrollbar-thumb) {
  background: #d4d7de;
  border-radius: 3px;
}

:deep(.el-tabs__content::-webkit-scrollbar-thumb:hover) {
  background: #c3c6cf;
}

:deep(.el-tabs__item) {
  height: 35px;
  line-height: 35px;
  padding: 0 12px;
  font-size: 13px;
}

:deep(.el-tabs__nav-wrap) {
  padding: 0 5px;
}

.commands-panel-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 8px 0;
  padding: 0 8px;
  max-height: 100%;
}

.cmd-item {
  padding: 6px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  cursor: pointer;
  background-color: #fff;
  transition: all 0.25s ease;
  font-size: 13px;
  color: #409EFF;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 160px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.cmd-item:hover {
  background-color: #ecf5ff;
  border-color: #409EFF;
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.commands-panel-groups {
  padding: 8px;
}

.cmd-group-title {
  font-weight: 600;
  margin: 12px 0 8px 8px;
  color: #303133;
  font-size: 14px;
  border-left: 3px solid #409EFF;
  padding-left: 8px;
}
</style>
