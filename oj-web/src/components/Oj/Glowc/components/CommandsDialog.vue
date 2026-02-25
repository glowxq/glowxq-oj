<template>
  <!-- 指令说明弹窗 -->
  <el-dialog
    v-model="dialogVisible"
    title="TuC指令说明"
    width="70%"
    :close-on-click-modal="true"
    :append-to-body="true"
    class="tuc-commands-dialog"
  >
    <el-tabs v-model="activeTab" type="border-card">
      <!-- C++语法标签页 -->
      <el-tab-pane label="C++语法" name="syntax">
        <div class="commands-content">
          <h3>常用语法模板</h3>
          <div class="syntax-grid">
            <div 
              v-for="template in getTemplatesByGroup(TemplateGroupType.SYNTAX)" 
              :key="template.label" 
              class="syntax-item"
              @click="insertCode(template.content)"
            >
              <div class="syntax-label">{{ template.label }}</div>
              <div class="syntax-preview">{{ template.content }}</div>
            </div>
          </div>
        </div>
      </el-tab-pane>
      
      <!-- 组件标签页 -->
      <el-tab-pane 
        v-for="tab in drawTypeTabs" 
        :key="tab.code" 
        :label="getTabLabel(tab)" 
        :name="tab.code"
      >
        <div class="commands-content">
          <!-- 颜色对照表 -->
          <template v-if="tab.code === ComponentType.COLORS">
            <h3>颜色对照表</h3>
            <div class="color-grid">
              <div v-for="color in COLOR_MAPS" :key="color.code" class="color-item">
                <div class="color-box" :style="{ backgroundColor: String(color.color) }"></div>
                <div class="color-info">
                  <div class="color-code">{{ color.code }}</div>
                  <div class="color-name">{{ color.name }}</div>
                  <div class="color-value">{{ color.color }}</div>
                </div>
              </div>
            </div>
          </template>
          
          <!-- 命令组显示 -->
          <template v-else>
            <div class="command-groups">
              <template v-for="(group, index) in getCommandGroups(tab.code)" :key="index">
                <h3>{{ group.title }}</h3>
                <el-descriptions :column="2" border >
                  <el-descriptions-item 
                    v-for="(cmd, cmdIndex) in group.commands" 
                    :key="cmdIndex" 
                    :label="cmd.description"
                    class="command-item"
                  >
                    <div 
                      class="command-content"
                      @click="insertCode(cmd.code)"
                    >
                      {{ cmd.label }}
                    </div>
                  </el-descriptions-item>
                </el-descriptions>
              </template>
            </div>
          </template>
        </div>
      </el-tab-pane>
    </el-tabs>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { 
  COMMANDS_CONFIG, 
  COLOR_MAPS, 
  CODE_TEMPLATES,
  ComponentType, 
  TemplateGroupType,
  type CommandGroup, 
  type CodeTemplate,
  getTemplatesByGroup as getTemplatesByGroupFn
} from '../config/commandsConfig';

// 定义属性
interface Props {
  visible: boolean;
  defaultActiveTab?: string;
  codeEditorRef?: any;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  defaultActiveTab: 'pen',
  codeEditorRef: null
});

// 定义事件
const emit = defineEmits(['update:visible', 'insert-template']);

// 状态变量
const dialogVisible = ref(props.visible);
const activeTab = ref(props.defaultActiveTab);

// DrawType模拟 (不再导入外部的DrawType，因为有导入问题)
const DrawType = [
  { code: ComponentType.PEN, name: '画笔' },
  { code: ComponentType.ARRAY, name: '一维数组' },
  { code: ComponentType.GRID, name: '二维数组' },
  { code: ComponentType.TREE, name: '树结构' },
  { code: ComponentType.LIST, name: '链表' },
  { code: ComponentType.COLORS, name: '颜色' }
];

// 根据DrawType获取命令标签页
const drawTypeTabs = computed(() => {
  return DrawType;
});

// 获取标签页标题
const getTabLabel = (tab: { code: string; name: string }) => {
  switch (tab.code) {
    case ComponentType.PEN:
      return '画笔 (pen)';
    case ComponentType.ARRAY:
      return '一维数组 (arr)';
    case ComponentType.GRID:
      return '二维数组 (grid)';
    case ComponentType.TREE:
      return '树结构 (tree)';
    case ComponentType.LIST:
      return '链表 (list)';
    case ComponentType.COLORS:
      return '颜色对照表';
    default:
      return tab.name;
  }
};

// 获取指定类型的命令组
const getCommandGroups = (code: string): CommandGroup[] => {
  return COMMANDS_CONFIG[code] || [];
};

// 从模板组中获取模板
const getTemplatesByGroup = (group: string): CodeTemplate[] => {
  return getTemplatesByGroupFn(group);
};

// 检查组件是否有对应的模板
const hasTemplatesForComponent = (componentType: string): boolean => {
  const groupType = mapComponentToTemplateGroup(componentType);
  return groupType ? getTemplatesByGroup(groupType).length > 0 : false;
};

// 获取组件对应的模板
const getTemplatesForComponent = (componentType: string): CodeTemplate[] => {
  const groupType = mapComponentToTemplateGroup(componentType);
  return groupType ? getTemplatesByGroup(groupType) : [];
};

// 映射组件类型到模板组类型
const mapComponentToTemplateGroup = (componentType: string): string | null => {
  switch (componentType) {
    case ComponentType.PEN:
      return TemplateGroupType.PEN;
    case ComponentType.ARRAY:
      return TemplateGroupType.ARRAY;
    case ComponentType.GRID:
      return TemplateGroupType.GRID;
    case ComponentType.TREE:
      return TemplateGroupType.TREE;
    case ComponentType.LIST:
      return TemplateGroupType.LIST;
    default:
      return null;
  }
};

// 插入代码模板
const insertCodeTemplate = (template: CodeTemplate) => {
  if (!props.codeEditorRef) {
    console.log('编辑器引用不存在');
    // 发出事件让父组件处理
    emit('insert-template', template.content);
    return;
  }
  
  // 获取编辑器组件
  const editorComponent = props.codeEditorRef;
  
  try {
    // 尝试获取编辑器视图和光标位置
    const editorView = editorComponent.getEditorView?.();
    if (editorView) {
      const cursorPosition = editorView.state.selection.main.head;
      
      // 使用编辑器API插入内容
      editorView.dispatch({
        changes: {
          from: cursorPosition,
          to: cursorPosition,
          insert: template.content
        }
      });
      
      // 聚焦编辑器
      editorComponent.focus?.();
      console.log('成功在位置', cursorPosition, '插入代码模板:', template.content);
      return;
    }
    
    // 如果无法通过API插入，发出事件让父组件处理
    emit('insert-template', template.content);
  } catch (error) {
    console.error('插入代码模板时出错:', error);
    // 发出事件让父组件处理
    emit('insert-template', template.content);
  }
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
      emit('insert-template', code);
    }
  } else {
    emit('insert-template', code);
  }
};

// 监听属性变化
watch(() => props.visible, (newValue) => {
  dialogVisible.value = newValue;
});

// 监听对话框状态变化，同步更新父组件
watch(dialogVisible, (newValue) => {
  emit('update:visible', newValue);
});
</script>

<style scoped>
/* 命令项样式 */
.command-item {
  cursor: pointer;
}

.command-content {
  padding: 6px;
  transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  cursor: pointer;
  border-radius: 16px;
  font-weight: 500;
  color: var(--el-color-primary);
  text-align: center;
  border: 1px solid rgba(0, 0, 0, 0.05);
  background-color: #ffffff;
  width: 90%;
  margin: 0 auto;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.command-content:hover {
  background-color: #f5f5f7;
  color: var(--el-color-primary);
  border-color: var(--el-color-primary);
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.command-item:hover .command-content {
  background-color: #f5f5f7;
}

/* C++语法样式 */
.syntax-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.syntax-item {
  cursor: pointer;
  padding: 12px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 16px;
  transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  background-color: #ffffff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.syntax-item:hover {
  background-color: #f5f5f7;
  border-color: var(--el-color-primary);
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.08);
  transform: translateY(-3px);
}

.syntax-label {
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--el-color-primary);
}

.syntax-preview {
  font-family: -apple-system, BlinkMacSystemFont, 'SF Mono', monospace;
  font-size: 12px;
  color: #1d1d1f;
  white-space: pre-wrap;
  background-color: #f5f5f7;
  padding: 10px;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.03);
}

.commands-content h3 {
  font-weight: 500;
  color: var(--el-color-primary);
  font-size: 16px;
  margin: 16px 0 12px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #e6e6e6;
}

.commands-content {
  padding: 16px;
}

:deep(.el-tabs__item) {
  font-size: 14px;
  padding: 0 18px;
  height: 40px;
  color: #1d1d1f;
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

:deep(.el-descriptions__cell) {
  padding: 10px;
}

:deep(.el-descriptions__label) {
  font-size: 13px;
  color: #86868b;
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

.color-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.color-item {
  display: flex;
  align-items: center;
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 16px;
  padding: 12px;
  background-color: #ffffff;
  transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.color-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.08);
}

.color-box {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  margin-right: 10px;
  border: 1px solid #e6e6e6;
}

.color-info {
  flex: 1;
}

.color-code {
  font-weight: 600;
  font-size: 14px;
  color: var(--el-color-primary);
}

.color-name {
  font-size: 13px;
  color: #86868b;
}

.color-value {
  font-size: 12px;
  color: #86868b;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Mono', monospace;
}

@media (prefers-color-scheme: dark) {
  .syntax-item,
  .command-content,
  .color-item {
    background-color: rgba(30, 30, 32, 0.7);
    border-color: rgba(255, 255, 255, 0.05);
  }
  
  .syntax-preview {
    background-color: rgba(40, 40, 42, 0.7);
    color: #f5f5f7;
  }
  
  .syntax-item:hover,
  .command-content:hover,
  .color-item:hover {
    background-color: rgba(50, 50, 54, 0.8);
  }
  
  .color-code,
  .syntax-preview {
    color: #f5f5f7;
  }
  
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
  
  .commands-content h3 {
    border-color: rgba(255, 255, 255, 0.05);
    color: #f5f5f7;
  }
}
</style> 