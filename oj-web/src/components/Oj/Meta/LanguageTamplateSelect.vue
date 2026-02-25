<template>
  <div class="language-template-select-container">
    <!-- 头部提示信息 -->
    <div class="header-info">
      <el-alert
        title="语言模板选择"
        type="info"
        :closable="false"
        show-icon
      >
        <p>从下方选择需要的语言模板，选中后可以编辑代码模板内容，点击确定返回选中的模板</p>
      </el-alert>
    </div>

    <!-- 语言Tab选择区域 -->
    <div class="tab-select-area" v-loading="loading">
      <div class="tab-header">
        <div class="title-area">
          <h3>语言选择</h3>
          <span class="selected-count" v-if="selectedLanguages.length > 0">
            已选择 {{ selectedLanguages.length }} 个语言
          </span>
        </div>
        <div class="action-area">
          <el-button size="small" @click="clearSelection">清空选择</el-button>
          <el-button size="small" type="primary" @click="selectAll">全选</el-button>
          <el-button size="small" type="text" @click="isLanguageListCollapsed = !isLanguageListCollapsed">
            {{ isLanguageListCollapsed ? '展开' : '折叠' }}
            <el-icon class="el-icon--right">
              <component :is="isLanguageListCollapsed ? 'ArrowDown' : 'ArrowUp'" />
            </el-icon>
          </el-button>
        </div>
      </div>

      <el-collapse-transition>
        <div class="language-tabs" v-show="!isLanguageListCollapsed">
          <div
            v-for="language in languageList"
            :key="language.id"
            class="language-tab"
            :class="{
              'selected': isLanguageSelected(language.id),
              'customized': isCustomized(language.id)
            }"
            @click="toggleLanguageSelection(language)"
          >
            <span class="language-name">{{ language.name }}</span>
            <span v-if="isCustomized(language.id)" class="custom-badge">自定义</span>
            <span class="language-desc" v-if="language.description">{{ language.description }}</span>
          </div>
        </div>
      </el-collapse-transition>
    </div>

    <!-- 代码编辑区域 -->
    <div class="code-editor-area" v-if="selectedLanguages.length > 0">
      <div class="editor-tabs">
        <el-tabs v-model="activeEditor" type="card" @tab-click="handleTabClick">
          <el-tab-pane
            v-for="language in selectedLanguages"
            :key="language.id"
            :label="language.name"
            :name="language.id"
          >
            <div class="editor-header">
              <div class="language-info">
                <span class="info-label">语言描述:</span>
                <span class="info-value">{{ language.description || '无' }}</span>
              </div>
              <div class="language-info" v-if="language.compileCommand">
                <span class="info-label">编译指令:</span>
                <span class="info-value">{{ language.compileCommand }}</span>
              </div>
            </div>

            <div class="code-editor-container">
              <CodeEditor
                :modelValue="getEditorContent(language.id)"
                @update:modelValue="(val) => updateEditorContent(language.id, val)"
                :language="getEditorLanguage(language)"
                height="300px"
              />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 空状态提示 -->
    <div class="empty-state" v-else-if="!loading && languageList.length > 0">
      <el-empty description="请选择至少一种语言模板" />
    </div>

    <!-- 底部按钮区域 -->
    <div class="footer-actions">
      <el-button @click="cancel">取消</el-button>
      <el-button
        type="primary"
        @click="confirmSelection"
      >完成</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMetaLanguageListApi } from '@/api/modules/oj/meta/metaLanguage'
import type { IMetaLanguage } from '@/api/interface/oj/meta/metaLanguage'
import CodeEditor from '@/components/Common/CodeEditor/CodeEditor.vue'
import { ProgramLanguage, getEditorLanguage as getLanguageForEditor, normalizeProgramLanguage } from '@/enums/oj/common/ProgramLanguage'

// 组件属性定义
interface Props {
  // 搜索参数
  queryParams?: Partial<IMetaLanguage.Query>
  // 默认选中的语言ID
  defaultSelected?: string[]
  // 双向绑定的值
  modelValue?: any[]
  // 自定义代码模板（API返回的带有自定义代码的模板）
  customTemplates?: any[]
  // 是否优先使用自定义代码
  prioritizeCustomCode?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  queryParams: () => ({}),
  defaultSelected: () => [],
  modelValue: () => [],
  customTemplates: () => [],
  prioritizeCustomCode: false
})

// 组件事件
const emit = defineEmits(['cancel', 'confirm', 'update:modelValue'])

// 组件状态
const loading = ref(false)
const languageList = ref<IMetaLanguage.Row[]>([])
const selectedLanguageIds = ref<string[]>([]) // 选中的语言ID
const languageEditors = ref<Record<string, string>>({}) // 编辑器内容集合
const activeEditor = ref('') // 当前活动的编辑器
const isLanguageListCollapsed = ref(false) // 语言列表折叠状态

// 选中的语言列表
const selectedLanguages = computed(() => {
  return languageList.value.filter(lang =>
    lang.id && selectedLanguageIds.value.includes(lang.id)
  )
})

// 初始化加载数据
onMounted(async () => {
  await loadLanguages()

  // 设置默认选中
  if (props.defaultSelected.length > 0) {
    selectedLanguageIds.value = [...props.defaultSelected]
  }

  // 检查自定义模板 - 从API返回的已保存模板数据
  if (props.customTemplates && props.customTemplates.length > 0) {
    console.log('发现自定义模板数据:', props.customTemplates);

    // 如果有自定义模板且优先使用自定义代码，优先选中这些模板对应的语言
    if (props.prioritizeCustomCode) {
      props.customTemplates.forEach(template => {
        const langId = template.languageId || template.id;
        if (langId && langId.toString() && !selectedLanguageIds.value.includes(langId.toString())) {
          console.log(`添加自定义模板语言ID: ${langId} 到选中列表`);
          selectedLanguageIds.value.push(langId.toString());
        }
      });
    }
  }

  // 初始化编辑器（现在包含了自定义模板处理逻辑）
  initEditors()
})

// 加载语言列表
const loadLanguages = async () => {
  loading.value = true
  try {
    const params: IMetaLanguage.Query = {
      oj: 'GlowOJ', // 默认为GlowOJ
      page: 1,
      limit: 20,
      ...props.queryParams
    }

    const res = await getMetaLanguageListApi(params)
    languageList.value = res.data.rows || []

    // 初始化编辑器内容
    initEditors()
  } catch (error) {
    console.error('加载语言列表失败:', error)
    ElMessage.error('加载语言列表失败')
  } finally {
    loading.value = false
  }
}

// 获取编辑器内容
const getEditorContent = (langId?: string): string => {
  if (!langId) return '';
  return languageEditors.value[langId] || '';
};

// 更新编辑器内容
const updateEditorContent = (langId?: string, content?: string) => {
  if (!langId || content === undefined) return;
  languageEditors.value[langId] = content;
};

// 初始化编辑器内容
const initEditors = () => {
  // 处理自定义模板代码 - 优先使用自定义模板的代码
  if (props.customTemplates && props.customTemplates.length > 0 && props.prioritizeCustomCode) {
    console.log('使用自定义模板初始化编辑器:', props.customTemplates);

    // 先处理自定义模板 - 确保使用API返回的已保存代码
    props.customTemplates.forEach(template => {
      const langId = template.languageId || template.id;
      if (langId && (template.code || template.codeTemplate)) {
        const code = template.code || template.codeTemplate || '';
        if (code && code.length > 0) {
          console.log(`应用自定义模板代码, 语言ID: ${langId}, 代码长度: ${code.length}`);
          languageEditors.value[langId] = code;

          // 确保该语言已被选中，这样才能在编辑器中显示
          if (!selectedLanguageIds.value.includes(langId.toString())) {
            selectedLanguageIds.value.push(langId.toString());
          }
        }
      }
    });
  }

  // 为选中的语言初始化编辑器内容 - 对于没有自定义代码的语言，使用默认模板
  selectedLanguages.value.forEach(lang => {
    if (lang.id) {
      // 只有当编辑器内容未初始化时才设置默认值
      // 这确保自定义代码不会被覆盖
      if (!languageEditors.value[lang.id]) {
        languageEditors.value[lang.id] = lang.codeTemplate || '';
      }
    }
  });

  // 如果有选中的语言，将第一个设为活动编辑器
  if (selectedLanguages.value.length > 0 && selectedLanguages.value[0].id) {
    activeEditor.value = selectedLanguages.value[0].id;
  }
};

// 切换语言选择
const toggleLanguageSelection = (language: IMetaLanguage.Row) => {
  if (!language.id) return;

  const langId = language.id;
  const index = selectedLanguageIds.value.indexOf(langId);

  if (index > -1) {
    // 取消选择
    selectedLanguageIds.value.splice(index, 1);
  } else {
    // 选择
    selectedLanguageIds.value.push(langId);

    // 初始化编辑器内容 - 先检查是否有自定义代码
    // 确保API返回的自定义代码优先级更高
    if (props.customTemplates && props.prioritizeCustomCode) {
      const customTemplate = props.customTemplates.find(t =>
        (t.languageId === langId || t.id === langId) &&
        (t.code || t.codeTemplate)
      );

      if (customTemplate) {
        const customCode = customTemplate.code || customTemplate.codeTemplate || '';
        if (customCode && customCode.length > 0) {
          console.log(`使用自定义代码初始化语言 ${langId}, 代码长度: ${customCode.length}`);
          languageEditors.value[langId] = customCode;
          return; // 跳过下面的默认初始化
        }
      }
    }

    // 如果没有自定义代码，则使用默认模板
    languageEditors.value[langId] = language.codeTemplate || '';
  }

  // 更新活动编辑器（如果当前活动编辑器被取消选择）
  if (index > -1 && activeEditor.value === langId && selectedLanguages.value.length > 0 && selectedLanguages.value[0].id) {
    activeEditor.value = selectedLanguages.value[0].id;
  } else if (selectedLanguages.value.length === 0) {
    activeEditor.value = '';
  } else if (!activeEditor.value && selectedLanguages.value.length > 0 && selectedLanguages.value[0].id) {
    activeEditor.value = selectedLanguages.value[0].id;
  }
};

// 判断语言是否被选中
const isLanguageSelected = (id?: string) => {
  if (!id) return false
  return selectedLanguageIds.value.includes(id)
}

// 清空选择
const clearSelection = () => {
  selectedLanguageIds.value = []
  activeEditor.value = ''
}

// 全选
const selectAll = () => {
  selectedLanguageIds.value = languageList.value
    .filter(lang => lang.id)
    .map(lang => lang.id as string)

  // 初始化编辑器内容
  initEditors()
}

// 处理编辑器标签点击
const handleTabClick = () => {
  // 标签切换时的逻辑，如果需要
}

// 确认选择 - 简化为仅通知父组件关闭
const confirmSelection = () => {
  // 已经实时同步数据，此处只需通知完成
  emit('confirm', selectedLanguages.value.map(lang => {
    if (lang.id) {
      return {
        ...lang,
        codeTemplate: languageEditors.value[lang.id] || lang.codeTemplate
      }
    }
    return lang
  }))
}

// 取消
const cancel = () => {
  emit('cancel')
}

// 获取编辑器对应的语言
const getEditorLanguage = (language: IMetaLanguage.Row): string => {
  // 使用公共方法获取编辑器语言，统一使用标准枚举
  const langNameStr = language.name || '';
  // 先尝试标准化语言名，再获取编辑器语言标识
  const normalizedLang = normalizeProgramLanguage(langNameStr);
  return getLanguageForEditor(normalizedLang);
}

// 判断语言是否为自定义模板
const isCustomized = (id?: string): boolean => {
  if (!id || !props.customTemplates) return false;

  // 查找是否存在对应的自定义模板
  const customTemplate = props.customTemplates.find(t =>
    (t.languageId === id || t.id === id) && t.isCustomized === true
  );

  return !!customTemplate;
}

// 监听模型值变化
watch(
  () => props.modelValue,
  (newValue) => {
    if (newValue && newValue.length > 0) {
      console.log('外部模型值变化:', newValue);

      // 如果是自定义模板且优先使用自定义代码，需要更新编辑器内容
      // 这确保当父组件更新模型值时，编辑器内容也会相应更新
      if (props.prioritizeCustomCode) {
        newValue.forEach(template => {
          const langId = template.languageId || template.id;
          if (langId && (template.code || template.codeTemplate)) {
            const code = template.code || template.codeTemplate || '';
            if (code && code.length > 0) {
              console.log(`外部模型值更新: 语言ID ${langId}, 代码长度: ${code.length}`);
              // 更新编辑器内容
              languageEditors.value[langId] = code;

              // 确保该语言被选中
              if (!selectedLanguageIds.value.includes(langId.toString())) {
                selectedLanguageIds.value.push(langId.toString());
              }
            }
          }
        });
      }
    }
  },
  { deep: true }
);

// 监听语言选择和编辑内容的变化，实时更新父组件数据
watch(
  [selectedLanguageIds, languageEditors],
  () => {
    // 构建当前选中的语言数据，并实时更新父组件的codeTemplates数据
    const selectedData = selectedLanguages.value.map(lang => {
      if (lang.id) {
        // 合并原始语言数据和自定义模板数据
        let result = {
          ...lang,
          codeTemplate: languageEditors.value[lang.id] || lang.codeTemplate
        };

        // 如果有自定义模板，保留其中的特殊字段（如isCustomized标记）
        // 确保返回给父组件的数据包含所有必要信息
        if (props.customTemplates && props.prioritizeCustomCode) {
          const customTemplate = props.customTemplates.find(t =>
            (t.languageId === lang.id || t.id === lang.id)
          );

          if (customTemplate) {
            // 保留编辑后的代码，但同时合并自定义模板的其他元数据
            const editedCode = languageEditors.value[lang.id];
            result = {
              ...lang,
              ...customTemplate, // 合并自定义模板的元数据
              // 确保类型安全的方式处理code属性
              codeTemplate: editedCode || (customTemplate as any).codeTemplate || lang.codeTemplate || '',
              languageId: lang.id, // 确保language ID正确
              isCustomized: true   // 标记为自定义模板
            };

            console.log(`更新自定义模板: 语言ID ${lang.id}, 代码长度: ${result.codeTemplate?.length || 0}`);
          }
        }

        return result;
      }
      return lang;
    });

    // 通过v-model更新父组件的数据
    emit('update:modelValue', selectedData);
  },
  { deep: true }
);
</script>

<style scoped lang="scss">
.language-template-select-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
  height: 100%;
  min-height: 500px;
}

.header-info {
  margin-bottom: 8px;
}

.tab-select-area {
  background-color: var(--el-fill-color-light);
  border-radius: 4px;
  padding: 16px;
  margin-bottom: 16px;
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.title-area {
  display: flex;
  align-items: center;
  gap: 8px;

  h3 {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
  }

  .selected-count {
    color: var(--el-color-primary);
    font-size: 14px;
  }
}

.action-area {
  display: flex;
  gap: 8px;
}

.language-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.language-tab {
  display: flex;
  flex-direction: column;
  padding: 10px 16px;
  background-color: var(--el-fill-color);
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
  position: relative;

  &:hover {
    background-color: var(--el-color-primary-light-9);
  }

  &.selected {
    background-color: var(--el-color-primary-light-8);
    border-color: var(--el-color-primary);
  }

  &.customized {
    background-color: var(--el-color-success-light-9);
    border: 1px dashed var(--el-color-success);

    &.selected {
      background-color: var(--el-color-success-light-8);
      border-style: solid;
    }

    &:hover {
      background-color: var(--el-color-success-light-7);
    }
  }

  .language-name {
    font-weight: 500;
    margin-bottom: 4px;
  }

  .custom-badge {
    position: absolute;
    top: -8px;
    right: -8px;
    background-color: var(--el-color-success);
    color: white;
    border-radius: 10px;
    padding: 2px 6px;
    font-size: 10px;
    font-weight: bold;
  }

  .language-desc {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    max-width: 150px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.code-editor-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  overflow: hidden;
}

.editor-header {
  padding: 12px 16px;
  background-color: var(--el-fill-color-light);
  border-bottom: 1px solid var(--el-border-color-light);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.language-info {
  display: flex;
  align-items: center;

  .info-label {
    font-weight: 500;
    margin-right: 8px;
    color: var(--el-text-color-secondary);
  }

  .info-value {
    color: var(--el-text-color-primary);
  }
}

.code-editor-container {
  height: 300px;
  width: 100%;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  flex: 1;
  padding: 32px;
}

.footer-actions {
  display: flex;
  justify-content: flex-end;
  padding: 16px 0;
  gap: 12px;
  border-top: 1px solid var(--el-border-color-light);
  margin-top: auto;
}
</style>
