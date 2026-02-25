<template>
  <div class="problem-select">
    <el-select
      v-model="selectedProblems"
      :multiple="multiple"
      filterable
      remote
      :remote-method="remoteSearch"
      :loading="loading"
      :placeholder="placeholder"
      :clearable="clearable"
      @change="handleChange"
    >
      <el-option
        v-for="item in problemOptions"
        :key="item.id"
        :label="formatProblemLabel(item)"
        :value="item.id"
      >
        <div class="problem-option">
          <div class="problem-info">
            <span class="problem-id">#{{ item.id }}</span>
            <span class="problem-key" v-if="item.problemKey">{{ item.problemKey }}</span>
            <span class="problem-title">{{ item.title || '未知题目' }}</span>
            <span class="problem-type" v-if="item.problemType">{{ item.problemType }}</span>
          </div>
        </div>
      </el-option>
    </el-select>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getProblemListApi } from '@/api/modules/oj/problem/problem'
import type { IProblem } from '@/api/interface/oj/problem/problem'

defineOptions({
  name: 'ProblemSelect'
})

interface Props {
  modelValue?: number | number[]
  multiple?: boolean
  placeholder?: string
  clearable?: boolean
  preloadOptions?: {
    id?: number;
    problemKey?: string;
    title?: string;
    problemType?: string;
    [key: string]: any;
  }[]
}

const props = withDefaults(defineProps<Props>(), {
  multiple: false,
  placeholder: '请选择题目',
  clearable: true,
  modelValue: undefined,
  preloadOptions: () => []
})

const emit = defineEmits(['update:modelValue', 'change'])

const loading = ref(false)
const problemOptions = ref<IProblem.Row[]>([])
const selectedProblems = ref<number | number[]>(props.modelValue || (props.multiple ? [] : 0))

// 格式化题目标签显示
const formatProblemLabel = (problem: IProblem.Row) => {
  const parts = [];
  parts.push(`#${problem.id}`);

  if (problem.problemKey) {
    parts.push(problem.problemKey);
  }

  // 确保标题部分有值
  const title = problem.title || '未知题目';
  parts.push(title);

  if (problem.problemType) {
    parts.push(problem.problemType);
  }
  return parts.join(' - ');
}

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  if (newVal !== undefined) {
    selectedProblems.value = newVal
  } else {
    // 当modelValue被设置为undefined时（例如重置操作），将选择项重置为默认值
    selectedProblems.value = props.multiple ? [] : 0
  }
}, { immediate: true })

// 监听预加载选项
watch(() => props.preloadOptions, (newOptions) => {
  if (newOptions && newOptions.length > 0) {
    console.log('预加载题目数据:', JSON.stringify(newOptions))
    // 将预加载选项添加到problemOptions中
    problemOptions.value = [] // 清空现有选项
    newOptions.forEach((item: any) => {
      problemOptions.value.push({
        id: item.id,
        problemKey: item.problemKey,
        title: item.title,
        problemType: item.problemType
      })
    })
  }
}, { immediate: true })

// 远程搜索
const remoteSearch = async (query: string) => {
  loading.value = true
  try {
    const res = await getProblemListApi({
      title: query || undefined,
      page: 1,
      limit: 20
    })
    problemOptions.value = res.data.rows || []
  } catch (error) {
    console.error('获取题目列表失败:', error)
    ElMessage.error('获取题目列表失败')
  } finally {
    loading.value = false
  }
}

// 选择变化
const handleChange = (value: number | number[]) => {
  emit('update:modelValue', value)

  // 找到对应的题目对象并传递给父组件
  if (props.multiple) {
    // 多选模式
    const selectedProblemObjects = (value as number[]).map(id =>
      problemOptions.value.find(problem => problem.id === id)
    ).filter(Boolean);
    emit('change', value, selectedProblemObjects)
  } else {
    // 单选模式
    const selectedProblemObject = problemOptions.value.find(problem => problem.id === value);
    emit('change', value, selectedProblemObject)
  }
}

// 初始化加载
onMounted(async () => {
  // 首先处理预加载选项
  if (props.preloadOptions && props.preloadOptions.length > 0) {
    console.log('初始化时预加载题目数据:', JSON.stringify(props.preloadOptions))
    problemOptions.value = [] // 清空现有选项
    props.preloadOptions.forEach((item: any) => {
      problemOptions.value.push({
        id: item.id,
        problemKey: item.problemKey,
        title: item.title,
        problemType: item.problemType
      })
    })
  } else {
    // 如果没有预加载选项，再加载默认数据
    loading.value = true
    try {
      const res = await getProblemListApi({
        page: 1,
        limit: 20
      })
      problemOptions.value = res.data.rows || []
    } catch (error) {
      console.error('获取题目列表失败:', error)
      ElMessage.error('初始加载题目数据失败')
    } finally {
      loading.value = false
    }
  }
})

defineExpose({
  problemOptions, // 暴露题目选项数据
})
</script>

<style scoped lang="scss">
.problem-select {
  width: 100%;
  min-height: 32px;
  display: block;

  :deep(.el-select) {
    width: 100%;
    display: block;
  }

  .problem-option {
    display: flex;
    align-items: center;
    padding: 8px 0;

    .problem-info {
      display: flex;
      align-items: center;
      width: 100%;

      .problem-id {
        font-weight: bold;
        color: #606266;
        margin-right: 8px;
      }

      .problem-key {
        font-weight: bold;
        color: #409EFF;
        margin-right: 8px;
      }

      .problem-title {
        font-weight: 500;
        margin-right: 8px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        color: #303133;
      }

      .problem-type {
        color: #909399;
        font-size: 13px;
      }
    }
  }

  :deep(.el-select__tags) {
    max-width: calc(100% - 30px);
  }
}
</style>
