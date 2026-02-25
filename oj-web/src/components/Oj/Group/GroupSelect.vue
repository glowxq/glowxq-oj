<template>
  <div class="group-select">
    <el-select
      v-model="selectedGroups"
      :multiple="multiple"
      filterable
      remote
      :remote-method="remoteSearch"
      :loading="loading"
      :placeholder="placeholder"
      :clearable="clearable"
      @change="handleChange"
      class="apple-select"
      collapse-tags
      collapse-tags-tooltip
    >
      <template #prefix v-if="!loading">
        <i class="el-icon-search">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="16" height="16" style="fill: #007AFF;">
            <path d="M10,18c4.4,0,8-3.6,8-8s-3.6-8-8-8s-8,3.6-8,8S5.6,18,10,18z M10,4c3.3,0,6,2.7,6,6s-2.7,6-6,6s-6-2.7-6-6S6.7,4,10,4z"/>
            <path d="M21,21l-6-6l1.4-1.4l6,6L21,21z"/>
          </svg>
        </i>
      </template>
      <template #empty>
        <div class="no-data">
          <p>暂无班级</p>
          <span>请尝试其他搜索条件</span>
        </div>
      </template>
      <el-option
        v-for="item in groupOptions"
        :key="item.id"
        :label="item.name"
        :value="item.id"
      >
        <div class="group-option">
          <div class="group-pill">
            {{ item.code ? `${item.code} - ` : '' }}{{ item.name }}
          </div>
          <span v-if="item.principalName" class="principal-name">
            {{ item.principalName }}
          </span>
        </div>
      </el-option>
    </el-select>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue'
import { getGroupListApi } from '@/api/modules/oj/group/group'
import type { IGroup } from '@/api/interface/oj/group/group'

defineOptions({
  name: 'GroupSelect'
})

// 定义主题色常量
const THEME_COLOR = '#007AFF' // Apple蓝色
const THEME_COLOR_LIGHT = 'rgba(0, 122, 255, 0.1)'
const TEXT_DARK = '#1D1D1F'
const TEXT_LIGHT = '#FFFFFF'
const TEXT_SECONDARY = '#86868B'
const BG_LIGHT = '#F5F5F7'
const BG_HOVER = '#EAEAEB'

interface Props {
  modelValue?: number | number[]
  multiple?: boolean
  placeholder?: string
  clearable?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  multiple: false,
  placeholder: '搜索并选择班级',
  clearable: true,
  modelValue: undefined
})

const emit = defineEmits(['update:modelValue', 'change'])

const loading = ref(false)
const groupOptions = ref<IGroup.Row[]>([])
const selectedGroups = ref<number | number[] | undefined>(props.modelValue)
const searchQuery = ref('')

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  if (newVal !== undefined) {
    selectedGroups.value = newVal
  }
})

// 加载状态动画
const loadingStatus = computed(() => {
  return loading.value ? 'loading' : 'idle'
})

// 远程搜索
const remoteSearch = async (query: string) => {
  searchQuery.value = query
  loading.value = true
  try {
    const res = await getGroupListApi({
      name: query || undefined,
      page: 1,
      limit: 20
    })
    groupOptions.value = res.data.rows || []
  } catch (error) {
    console.error('获取班级列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 选择变化
const handleChange = (value: number | number[]) => {
  emit('update:modelValue', value)

  // 找到对应的班级对象并传递给父组件
  if (props.multiple) {
    // 多选模式
    const selectedGroupObjects = (value as number[]).map(id =>
      groupOptions.value.find(group => group.id === id)
    ).filter(Boolean);
    emit('change', value, selectedGroupObjects)
  } else {
    // 单选模式
    const selectedGroupObject = groupOptions.value.find(group => group.id === value);
    emit('change', value, selectedGroupObject)
  }
}

// 初始化加载
onMounted(async () => {
  loading.value = true
  try {
    const res = await getGroupListApi({
      page: 1,
      limit: 20
    })
    groupOptions.value = res.data.rows || []
  } catch (error) {
    console.error('获取班级列表失败:', error)
  } finally {
    loading.value = false
  }
})

defineExpose({
  groupOptions, // 暴露班级选项数据
  refresh: remoteSearch // 暴露刷新方法
})
</script>

<style scoped lang="scss">
.group-select {
  width: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', sans-serif;

  :deep(.el-select) {
    width: 100%;

    .el-input__wrapper {
      border-radius: 8px;
      box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1);
      padding: 0 12px;
      transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);

      &:hover {
        box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.15);
      }

      &.is-focus {
        box-shadow: 0 0 0 2px #007AFF, 0 0 0 4px rgba(0, 122, 255, 0.1);
      }
    }

    .el-input__inner {
      height: 40px;
      font-size: 14px;
      color: #1D1D1F;
    }

    .el-select__caret {
      color: #007AFF;
      transition: transform 0.2s;
    }
  }

  .no-data {
    padding: 16px;
    text-align: center;

    p {
      font-size: 14px;
      color: #1D1D1F;
      margin-bottom: 4px;
    }

    span {
      font-size: 13px;
      color: #007AFF;
    }
  }

  .group-option {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    padding: 8px 0;

    .group-pill {
      font-size: 13px;
      padding: 6px 10px;
      border-radius: 6px;
      font-weight: 500;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
      background-color: #007AFF;
      color: #FFFFFF;
      display: inline-block;
      text-align: center;
      transition: all 0.2s;

      &:hover {
        opacity: 0.9;
        transform: translateY(-1px);
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }
    }

    .principal-name {
      font-size: 13px;
      color: #007AFF;
      font-weight: 400;
    }
  }

  :deep(.el-select__tags) {
    max-width: calc(100% - 30px);
    margin-top: 2px;

    .el-select__tags-text {
      color: #007AFF;
    }

    .el-tag {
      background-color: rgba(0, 122, 255, 0.1);
      color: #007AFF;
      border: none;
      border-radius: 6px;
      padding: 4px 8px;
      margin-right: 6px;
      transition: all 0.2s;
      height: 26px;
      line-height: 18px;

      .el-tag__close {
        background-color: #007AFF;
        color: #FFFFFF;
        font-weight: bold;

        &:hover {
          background-color:#007AFFAA;
        }
      }

      &:hover {
        background-color: rgba(0, 122, 255, 0.15);
      }
    }
  }

  :deep(.el-select-dropdown__item) {
    height: auto;
    padding: 0 12px;

    &.hover, &:hover {
      background-color: rgba(0, 122, 255, 0.05);
    }

    &.selected {
      background-color: rgba(0, 122, 255, 0.1);
      color: #007AFF;
      font-weight: 600;
    }
  }
}
</style>
