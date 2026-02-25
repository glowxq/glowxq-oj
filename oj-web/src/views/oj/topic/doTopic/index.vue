<template>
  <div class="dotopic-container">
    <!-- 固定顶部导航栏 -->
    <div class="top-navigation">
      <div class="nav-content">
        <div class="nav-left">
          <el-button
            class="back-button"
            :icon="ArrowLeft"
            @click="goBack"
            size="default"
            type="primary"
            plain
          >
            返回
          </el-button>
          <div class="topic-basic-info">
            <h2 class="topic-title">
              {{ topic.name || '主题详情' }}
            </h2>
            <div class="topic-meta">
              <EnumShow
                v-if="topic.topicType"
                :enum="TopicType"
                :code="topic.topicType || ''"
                class="topic-type-tag"
                :background-color="themePrimary"
                color="white"
              />
              <TimeStatusShow
                :start-time="topic.startTime ?? ''"
                :end-time="topic.endTime ?? ''"
                tag-effect="plain"
              />
              <span class="problem-total">共 {{ problemList.length }} 题</span>
            </div>
          </div>
        </div>
        <div class="nav-right">
          <!-- 时间策略提示 -->
          <el-popover
            v-if="timeStrategyInfo"
            placement="bottom"
            :width="300"
            trigger="hover"
          >
            <template #reference>
              <el-button
                :type="timeStrategyInfo.type === 'error' ? 'danger' : 'warning'"
                size="small"
                :icon="InfoFilled"
                circle
              />
            </template>
            <div class="strategy-popover">
              <h4>{{ timeStrategyInfo.title }}</h4>
              <p>{{ timeStrategyInfo.description }}</p>
            </div>
          </el-popover>
        </div>
      </div>
    </div>

    <!-- 主体内容区域 -->
    <div class="main-content-wrapper">
      <!-- 左侧主题详细信息 -->
      <div class="left-panel">
        <el-card class="topic-detail-card">
          <template #header>
            <div class="detail-header">
              <span class="detail-title">详细信息</span>
              <el-button
                size="small"
                text
                @click="toggleDetailCollapse"
                class="collapse-btn"
              >
                {{ isDetailCollapsed ? '展开' : '收起' }}
                <el-icon>
                  <ArrowDown v-if="isDetailCollapsed" />
                  <ArrowUp v-else />
                </el-icon>
              </el-button>
            </div>
          </template>
          <div
            class="detail-content"
            :class="{ 'collapsed': isDetailCollapsed }"
          >
            <!-- 时间信息 -->
            <div class="time-section">
              <div class="time-display">
                <div class="time-item">
                  <span class="time-label">开始时间</span>
                  <span class="time-value">{{ formatTime(topic.startTime) }}</span>
                </div>
                <div class="time-separator">
                  →
                </div>
                <div class="time-item">
                  <span class="time-label">结束时间</span>
                  <span class="time-value">{{ formatTime(topic.endTime) }}</span>
                </div>
              </div>
              <div
                class="time-status"
                v-if="isBeforeStartTime || isExpired"
              >
                <el-tag
                  v-if="isBeforeStartTime && isStrictTimeView"
                  type="warning"
                  size="small"
                >
                  尚未开始
                </el-tag>
                <el-tag
                  v-if="isExpired"
                  type="danger"
                  size="small"
                >
                  已过期
                </el-tag>
              </div>
            </div>

            <!-- 基本信息 -->
            <div class="basic-info">
              <div class="info-row">
                <span class="info-label">主题代码</span>
                <span class="info-value">{{ topic.code || '暂无数据' }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">负责人</span>
                <span class="info-value">{{ topic.principalName || '暂无数据' }}</span>
              </div>
              <div
                class="info-row"
                v-if="topic.timeRangeType"
              >
                <span class="info-label">时间策略</span>
                <span class="info-value">
                  {{ getTimeRangeTypeName(topic.timeRangeType) }}
                  <el-tooltip
                    v-if="timeRangeTooltip"
                    :content="timeRangeTooltip"
                    placement="top"
                  >
                    <el-icon class="ml-5"><InfoFilled /></el-icon>
                  </el-tooltip>
                </span>
              </div>
            </div>

            <!-- 标签 -->
            <div
              class="tags-section"
              v-if="topic.tagList && topic.tagList.length > 0"
            >
              <div class="section-title">
                标签
              </div>
              <div class="tags-container">
                <el-tag
                  v-for="tag in topic.tagList"
                  :key="tag.id"
                  class="topic-tag"
                  size="small"
                  effect="plain"
                  :style="{ backgroundColor: tag.backgroundColor || (tag as any).color, color: tag.textColor }"
                >
                  {{ tag.name }}
                </el-tag>
              </div>
            </div>

            <!-- 主题描述 -->
            <div
              class="description-section"
              v-if="topic.description"
            >
              <div class="section-title">
                描述
              </div>
              <div class="description-content">
                <MdPreview
                  :model-value="topic.description"
                  :height="'auto'"
                  :theme="'light'"
                />
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧题目列表 -->
      <div class="right-panel">
        <el-card class="problem-list-card">
          <template #header>
            <div class="list-header">
              <div class="header-left">
                <span class="list-title">题目列表</span>
                <div class="list-stats">
                  <span class="current-page-info">
                    第 {{ currentPage }} 页，共 {{ totalPages }} 页
                  </span>
                  <span class="total-problems">
                    总计 {{ problemList.length }} 题
                  </span>
                </div>
              </div>
              <div class="header-right">
                <el-select
                  v-model="pageSize"
                  size="small"
                  style="width: 110px"
                  @change="handlePageSizeChange"
                >
                  <el-option
                    label="5题/页"
                    :value="5"
                  />
                  <el-option
                    label="10题/页"
                    :value="10"
                  />
                  <el-option
                    label="15题/页"
                    :value="15"
                  />
                  <el-option
                    label="20题/页"
                    :value="20"
                  />
                </el-select>
              </div>
            </div>
          </template>

          <!-- 分页控制器 -->
          <div
            class="pagination-controls"
            v-if="totalPages > 1"
          >
            <el-button
              :disabled="currentPage <= 1"
              @click="goToPrevPage"
              :icon="ArrowLeft"
              size="small"
              class="page-btn"
            >
              上一页
            </el-button>

            <div class="page-indicators">
              <span
                v-for="page in visiblePages"
                :key="page"
                class="page-indicator"
                :class="{ 'active': page === currentPage, 'ellipsis': page === '...' }"
                @click="page !== '...' && goToPage(page as number)"
              >
                {{ page }}
              </span>
            </div>

            <el-button
              :disabled="currentPage >= totalPages"
              @click="goToNextPage"
              :icon="ArrowRight"
              size="small"
              class="page-btn"
            >
              下一页
            </el-button>
          </div>

          <!-- 题目列表内容 -->
          <div class="problem-list-content">
            <el-empty
              v-if="problemList.length === 0"
              description="暂无题目"
              class="empty-state"
            />
            <div
              v-else-if="isBeforeStartTime"
              class="restricted-view"
            >
              <el-empty
                description="主题未开始，无法查看题目"
                :image-size="120"
              />
            </div>
            <div
              v-else-if="isExpired && isStrictTimeView"
              class="restricted-view"
            >
              <el-empty
                description="主题已过期，无法查看题目"
                :image-size="120"
              />
            </div>
            <div
              v-else
              class="problem-grid"
            >
              <div
                v-for="(problem, index) in currentPageProblems"
                :key="problem.id"
                class="problem-card"
                :class="{ 'disabled': isExpired || isBeforeStartTime }"
                @click="goToProblem(problem, getGlobalIndex(index))"
              >
                <div class="problem-header">
                  <span class="problem-number">#{{ getGlobalIndex(index) + 1 }}</span>
                  <span class="problem-key">{{ problem.problemKey }}</span>
                  <div class="problem-status">
                    <!-- 这里可以添加题目完成状态图标 -->
                  </div>
                </div>

                <div class="problem-content">
                  <h4 class="problem-title">
                    {{ problem.title }}
                  </h4>
                  <div class="problem-meta">
                    <EnumShow
                      :enum="ProblemType || defaultProblemType"
                      :code="problem.problemType || 'PROGRAMMING'"
                      background-color="#f0f2f5"
                      color="#606266"
                      class="problem-type-tag"
                    />
                    <el-tag
                      v-if="problem.difficulty"
                      size="small"
                      :type="getDifficultyTagType(problem.difficulty)"
                      effect="light"
                      class="difficulty-tag"
                    >
                      {{ formatDifficulty(problem.difficulty) }}
                    </el-tag>
                  </div>
                </div>

                <!-- 禁用覆盖层 -->
                <div
                  v-if="isBeforeStartTime || (isExpired && isStrictTimeView)"
                  class="disabled-overlay"
                >
                  <el-icon class="lock-icon">
                    <Lock />
                  </el-icon>
                  <span class="disabled-text">
                    {{ isBeforeStartTime ? '未开始' : '已过期' }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { InfoFilled, Lock, ArrowDown, ArrowUp, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { getTopicDetailApi } from '@/api/modules/oj/topic/topic'
import type { ITopic } from '@/api/interface/oj/topic/topic'
import { JudgeSceneType } from '@/enums/oj/judge/JudgeSceneType'
import { TimeRangeType, matchCode } from '@/enums/oj/topic/TimeRangeType'
import dayjs from 'dayjs'
import TimeRangeShow from '@/components/Common/Base/TimeRangeShow.vue'
import TimeStatusShow from '@/components/Common/Base/TimeStatusShow.vue'
import { TopicType } from '@/enums/oj/topic/TopicType'
import EnumShow from '@/components/Common/Enum/EnumShow.vue'
import { ProblemType } from '@/enums/oj/problem'
import { useAppStore } from '@/stores/modules/app'
import { storeToRefs } from 'pinia'
import { getLightColor, getDarkColor } from '@/utils/color'
import MdPreview from '@/components/Common/Markdown/MdPreview.vue'

defineOptions({
  name: 'DoTopic'
})

const route = useRoute()
const router = useRouter()
const topicId = computed(() => Number(route.params.id))
const loading = ref(false)
const topic = ref<ITopic.Row>({})
const problemList = ref<any[]>([])

// 分页相关状态
const currentPage = ref<number>(1)
const pageSize = ref<number>(10) // 默认每页10题
const totalPages = computed(() => Math.ceil(problemList.value.length / pageSize.value))

// 详情折叠状态
const isDetailCollapsed = ref<boolean>(false)

// 当前页题目列表
const currentPageProblems = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return problemList.value.slice(start, end)
})

// 可见页码
const visiblePages = computed(() => {
  const total = totalPages.value
  const current = currentPage.value
  const pages: (number | string)[] = []

  if (total <= 7) {
    // 总页数少于等于7页，显示所有页码
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    // 总页数大于7页，显示省略号
    pages.push(1)

    if (current > 4) {
      pages.push('...')
    }

    const start = Math.max(2, current - 2)
    const end = Math.min(total - 1, current + 2)

    for (let i = start; i <= end; i++) {
      pages.push(i)
    }

    if (current < total - 3) {
      pages.push('...')
    }

    if (total > 1) {
      pages.push(total)
    }
  }

  return pages
})

// 分页相关方法
const goToPrevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

const goToNextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

const handlePageSizeChange = (newSize: number) => {
  pageSize.value = newSize
  currentPage.value = 1 // 重置到第一页
}

// 获取题目在全局列表中的索引
const getGlobalIndex = (localIndex: number) => {
  return (currentPage.value - 1) * pageSize.value + localIndex
}

// 切换详情折叠状态
const toggleDetailCollapse = () => {
  isDetailCollapsed.value = !isDetailCollapsed.value
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 创建一个默认的ProblemType枚举，以防导入失败
const defaultProblemType = {
  PROGRAMMING: { code: 'PROGRAMMING', name: '编程题' },
  FILL: { code: 'FILL', name: '填空题' },
  CHOICE: { code: 'CHOICE', name: '选择题' },
  JUDGMENT: { code: 'JUDGMENT', name: '判断题' }
};

// 获取主题色
const appStore = useAppStore()
const { primary: themePrimary } = storeToRefs(appStore)

// 计算颜色变量
const primaryLight = computed(() => getLightColor(themePrimary.value, 0.8))
const primaryLighter = computed(() => getLightColor(themePrimary.value, 0.95))
const primaryDark = computed(() => getDarkColor(themePrimary.value, 0.2))

// 检查主题是否过期
const isExpired = computed(() => {
  if (!topic.value.endTime) return false
  return dayjs().isAfter(dayjs(topic.value.endTime))
})

// 检查是否未到开始时间
const isBeforeStartTime = computed(() => {
  if (!topic.value.startTime) return false
  return dayjs().isBefore(dayjs(topic.value.startTime))
})

// 时间策略处理
const timeRangeTooltip = computed(() => {
  if (!topic.value.timeRangeType) return ''

  const strategy = matchCode(topic.value.timeRangeType)
  return strategy?.tooltip || ''
})

// 获取时间策略名称
const getTimeRangeTypeName = (code: string) => {
  const strategy = matchCode(code)
  return strategy?.name || '无策略'
}

// 判断是否严格查看时间策略
const isStrictTimeView = computed(() => {
  return topic.value.timeRangeType === TimeRangeType.STRICT_TIME_VIEW.code
})

// 判断是否严格提交时间策略
const isStrictTimeSubmit = computed(() => {
  return topic.value.timeRangeType === TimeRangeType.STRICT_TIME_SUBMIT.code
})

// 判断是否固定扣分策略
const isPenaltyFixed = computed(() => {
  return topic.value.timeRangeType === TimeRangeType.PENALTY_FIXED.code
})

// 判断是否递减扣分策略
const isPenaltyGradual = computed(() => {
  return topic.value.timeRangeType === TimeRangeType.PENALTY_GRADUAL.code
})

// 判断是否无限制策略
const isUnrestricted = computed(() => {
  return topic.value.timeRangeType === TimeRangeType.UNRESTRICTED.code
})

// 时间策略提示信息
const timeStrategyInfo = computed(() => {
  // 检查是否是严格查看策略且还没到开始时间
  if (isStrictTimeView.value && isBeforeStartTime.value) {
    return {
      title: '主题未开始',
      type: 'warning',
      description: `该主题仅允许在规定时间内查看，将于 ${topic.value.startTime} 开始。`
    }
  }

  if (!isExpired.value) return null

  if (isStrictTimeView.value) {
    return {
      title: '主题已过期，禁止查看',
      type: 'error',
      description: '根据主题设置，过期后不允许查看题目。'
    }
  }

  if (isStrictTimeSubmit.value) {
    return {
      title: '主题已过期，禁止提交',
      type: 'warning',
      description: '您可以查看题目详情，但不能再提交答案。'
    }
  }

  if (isPenaltyFixed.value) {
    return {
      title: '主题已过期，将固定扣分',
      type: 'warning',
      description: '您仍然可以提交答案，但最终得分将被扣除固定比例。'
    }
  }

  if (isPenaltyGradual.value) {
    return {
      title: '主题已过期，将按时间递减扣分',
      type: 'warning',
      description: '您仍然可以提交答案，但最终得分将根据超时时长递减扣分。'
    }
  }

  return null
})

// 获取主题详情
const getTopicDetail = async () => {
  if (!topicId.value) return

  loading.value = true
  try {
    const res = await getTopicDetailApi({ id: topicId.value })
    topic.value = res.data

    // 检查是否有题目列表
    if (res.data.problemList && res.data.problemList.length > 0) {
      problemList.value = res.data.problemList
    } else {
      problemList.value = []
    }

    // 检查时间策略
    checkTimeStrategy()

  } catch (error) {
    console.error('获取主题详情失败:', error)
    ElMessage.error('获取主题详情失败')
  } finally {
    loading.value = false
  }
}

// 检查时间策略
const checkTimeStrategy = () => {
  // 检查是否是严格查看时间策略且还没到开始时间
  if (isStrictTimeView.value && isBeforeStartTime.value) {
    ElMessageBox.alert(
      `该主题仅允许在规定时间内查看，将于 ${topic.value.startTime} 开始。`,
      '未到开始时间',
      {
        confirmButtonText: '返回',
        type: 'warning',
        callback: () => {
          router.back()
        }
      }
    )
    return
  }

  // 如果未过期，不做处理
  if (!isExpired.value) return

  // 严格查看时间策略
  if (isStrictTimeView.value) {
    ElMessageBox.alert(
      '该主题已过期，根据设置您不能再查看题目内容。',
      '主题已过期',
      {
        confirmButtonText: '返回',
        type: 'error',
        callback: () => {
          router.back()
        }
      }
    )
    return
  }

  // 对其他策略只需要提示用户
  if (isStrictTimeSubmit.value) {
    ElMessage.warning('主题已过期，您可以查看题目但无法提交答案')
  } else if (isPenaltyFixed.value) {
    ElMessage.warning('主题已过期，提交将扣除固定比例分数')
  } else if (isPenaltyGradual.value) {
    ElMessage.warning('主题已过期，提交将按超时时长递减扣分')
  }
}

// 格式化难度
const formatDifficulty = (difficulty: number) => {
  if (difficulty <= 3) return '简单'
  if (difficulty <= 7) return '中等'
  return '困难'
}

// 获取难度标签类型
const getDifficultyTagType = (difficulty: number) => {
  if (difficulty <= 3) return 'success'
  if (difficulty <= 7) return 'warning'
  return 'danger'
}

// 跳转到做题页面，添加索引参数
const goToProblem = (problem: any, index: number) => {
  // 如果是严格查看策略且还没到开始时间，不允许进入
  if (isStrictTimeView.value && isBeforeStartTime.value) {
    ElMessage.error(`该主题仅允许在规定时间内查看，将于 ${topic.value.startTime} 开始。`)
    return
  }

  // 如果已过期且策略是严格查看，不允许进入
  if (isExpired.value && isStrictTimeView.value) {
    ElMessage.error('主题已过期，不允许查看题目')
    return
  }

  // 如果已过期且策略是严格提交，提示用户
  if (isExpired.value && isStrictTimeSubmit.value) {
    ElMessage.warning('主题已过期，您可以查看题目但无法做题')

    return
  }

  // 如果已过期且策略是扣分，提示用户
  if (isExpired.value && (isPenaltyFixed.value || isPenaltyGradual.value)) {
    const penaltyType = isPenaltyFixed.value ? '固定' : '递减'
    ElMessageBox.confirm(
      `主题已过期，提交答案将${penaltyType}扣分。是否继续？`,
      '提示',
      {
        confirmButtonText: '继续做题',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
      .then(() => {
        navigateToProblem(problem, index)
      })
      .catch(() => {
        // 用户取消，不跳转
      })
    return
  }

  // 正常情况，直接跳转
  navigateToProblem(problem, index)
}

// 导航到题目，传递完整题目列表和当前索引
const navigateToProblem = (problem: any, index: number) => {
  // 不再传递完整题目列表，只传递必要的信息
  router.push({
    path: `/oj/problem/submit/${problem.id}`,
    query: {
      topicId: topicId.value,
      judgeSceneType: topic.value.topicType,
      topicType: topic.value.topicType,
      // 添加过期标记和策略，供提交页面使用
      isExpired: isExpired.value ? '1' : '0',
      timeRangeType: topic.value.timeRangeType,
      // 只传递当前题目索引，不传递完整题目列表
      problemIndex: index.toString(),
      // 传递题目总数，用于导航控制
      totalProblems: problemList.value.length.toString()
    }
  })
}

// 在 script setup 部分添加 getTopicTypeLabel 方法
const getTopicTypeLabel = (field: string) => {
  const type = matchCode(topic.value.topicType || '')
  if (!type) return field
  return `${type.name}${field}`
}

// 在 script setup 部分添加 formatTime 方法
const formatTime = (time: string | undefined) => {
  if (!time) return '未设置'
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

onMounted(() => {
  getTopicDetail()
})
</script>

<style scoped lang="scss">
@use "sass:color";
@import "@/styles/var";

// Apple风格颜色变量
$apple-white: #ffffff;
$apple-light-gray: #f5f5f7;
$apple-mid-gray: #e5e5e5;
$apple-gray: #86868b;
$apple-dark-gray: #1d1d1f;
$apple-blue: #0071e3;
$apple-light-blue: #f0f8ff;
$apple-red: #ff3b30;
$apple-yellow: #ffcc00;
$apple-green: #34c759;

// 基础样式变量
$border-radius-small: 6px;
$border-radius: 10px;
$border-radius-large: 14px;
$card-shadow: 0 1px 6px rgba(0, 0, 0, 0.04);
$card-shadow-hover: 0 12px 20px rgba(0, 0, 0, 0.08);
$transition-default: all 0.3s cubic-bezier(0.42, 0, 0.58, 1);
$transition-slow: all 0.5s cubic-bezier(0.42, 0, 0.58, 1);

// 主题色混合
@function theme-color($opacity: 1) {
  @return rgba(var(--el-color-primary-rgb), $opacity);
}

// 卡片基础样式
@mixin apple-card {
  background: $apple-white;
  border-radius: $border-radius-large;
  box-shadow: $card-shadow;
  border: none;
  overflow: hidden;
  transition: $transition-default;

  &:hover {
    box-shadow: $card-shadow-hover;
    transform: translateY(-2px);
  }
}

// 卡片头部样式
@mixin apple-card-header {
  padding: 14px 18px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.03);
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(to right, rgba(var(--el-color-primary-rgb), 0.02), rgba(var(--el-color-primary-rgb), 0.005));
}

.dotopic-container {
  min-height: 100vh;
  background: $apple-light-gray;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', sans-serif;
  display: flex;
  flex-direction: column;
  color: $apple-dark-gray;
  letter-spacing: -0.01em;

  // 固定顶部导航栏
  .top-navigation {
    position: sticky;
    top: 0;
    z-index: 100;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    padding: 16px 24px;

    .nav-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
      max-width: 1400px;
      margin: 0 auto;

      .nav-left {
        display: flex;
        align-items: center;
        gap: 20px;

        .back-button {
          font-weight: 500;
          border-radius: $border-radius;
          transition: $transition-default;

          &:hover {
            transform: translateX(-2px);
          }
        }

        .topic-basic-info {
          .topic-title {
            margin: 0 0 8px 0;
            font-size: 20px;
            font-weight: 600;
            color: $apple-dark-gray;
            line-height: 1.2;
          }

          .topic-meta {
            display: flex;
            align-items: center;
            gap: 12px;

            .topic-type-tag {
              padding: 4px 12px;
              border-radius: 16px;
              font-size: 12px;
              font-weight: 500;
              box-shadow: 0 2px 6px rgba(var(--el-color-primary-rgb), 0.15);
            }

            .problem-total {
              font-size: 12px;
              color: $apple-gray;
              background: $apple-light-gray;
              padding: 4px 8px;
              border-radius: 12px;
              font-weight: 500;
            }
          }
        }
      }

      .nav-right {
        .strategy-popover {
          h4 {
            margin: 0 0 8px 0;
            color: $apple-dark-gray;
            font-size: 14px;
            font-weight: 600;
          }

          p {
            margin: 0;
            color: $apple-gray;
            font-size: 13px;
            line-height: 1.4;
          }
        }
      }
    }
  }

  // 主体内容区域
  .main-content-wrapper {
    display: flex;
    gap: 24px;
    flex: 1;
    padding: 24px;
    max-width: 1400px;
    margin: 0 auto;
    width: 100%;

    // 左侧面板 - 主题详细信息
    .left-panel {
      width: 350px;
      flex-shrink: 0;

      .topic-detail-card {
        @include apple-card;
        height: fit-content;
        max-height: calc(100vh );
        overflow: hidden;

        .detail-header {
          @include apple-card-header;
          display: flex;
          justify-content: space-between;
          align-items: center;

          .detail-title {
            font-size: 15px;
            font-weight: 600;
            color: var(--el-color-primary);
            position: relative;
            padding-left: 10px;

            &::before {
              content: '';
              position: absolute;
              left: 0;
              top: 50%;
              transform: translateY(-50%);
              width: 3px;
              height: 14px;
              background: var(--el-color-primary);
              border-radius: 3px;
            }
          }

          .collapse-btn {
            font-size: 12px;
            display: flex;
            align-items: center;
            gap: 4px;
            color: $apple-gray;
            transition: $transition-default;

            &:hover {
              color: var(--el-color-primary);
            }
          }
        }

        .detail-content {
          padding: 16px;
          display: flex;
          flex-direction: column;
          gap: 16px;
          max-height: calc(100vh - 300px);
          overflow-y: auto;
          transition: all 0.3s ease;

          &.collapsed {
            max-height: 0;
            padding: 0 16px;
            opacity: 0;
          }

          // 自定义滚动条
          &::-webkit-scrollbar {
            width: 4px;
          }

          &::-webkit-scrollbar-track {
            background: rgba(0, 0, 0, 0.02);
            border-radius: 2px;
          }

          &::-webkit-scrollbar-thumb {
            background: rgba(var(--el-color-primary-rgb), 0.3);
            border-radius: 2px;
            transition: background 0.2s ease;

            &:hover {
              background: rgba(var(--el-color-primary-rgb), 0.5);
            }
          }

          // 时间信息部分
          .time-section {
            padding: 16px;
            background: linear-gradient(135deg, rgba(var(--el-color-primary-rgb), 0.03), rgba(var(--el-color-primary-rgb), 0.01));
            border-radius: $border-radius;
            border: 1px solid rgba(var(--el-color-primary-rgb), 0.08);

            .time-display {
              display: flex;
              align-items: center;
              gap: 16px;
              margin-bottom: 12px;

              .time-item {
                flex: 1;
                text-align: center;

                .time-label {
                  display: block;
                  font-size: 12px;
                  color: $apple-gray;
                  font-weight: 500;
                  margin-bottom: 4px;
                }

                .time-value {
                  font-size: 14px;
                  color: $apple-dark-gray;
                  font-weight: 600;
                }
              }

              .time-separator {
                color: $apple-gray;
                font-weight: 500;
                opacity: 0.6;
              }
            }

            .time-status {
              display: flex;
              gap: 8px;
              justify-content: center;
            }
          }

          // 基本信息部分
          .basic-info {
            .info-row {
              display: flex;
              justify-content: space-between;
              align-items: center;
              padding: 12px 0;
              border-bottom: 1px solid rgba(0, 0, 0, 0.04);

              &:last-child {
                border-bottom: none;
              }

              .info-label {
                font-size: 13px;
                color: $apple-gray;
                font-weight: 500;
                min-width: 80px;
              }

              .info-value {
                font-size: 14px;
                color: $apple-dark-gray;
                font-weight: 500;
                text-align: right;
                flex: 1;
              }
            }
          }

          // 标签部分
          .tags-section {
            .section-title {
              font-size: 13px;
              color: $apple-gray;
              font-weight: 600;
              margin-bottom: 12px;
              text-transform: uppercase;
              letter-spacing: 0.5px;
            }

            .tags-container {
              display: flex;
              flex-wrap: wrap;
              gap: 8px;

              .topic-tag {
                border-radius: 12px;
                font-size: 12px;
                font-weight: 500;
                transition: $transition-default;

                &:hover {
                  transform: translateY(-1px);
                  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                }
              }
            }
          }

          // 描述部分
          .description-section {
            .section-title {
              font-size: 13px;
              color: $apple-gray;
              font-weight: 600;
              margin-bottom: 12px;
              text-transform: uppercase;
              letter-spacing: 0.5px;
            }

            .description-content {
              :deep(.md-preview) {
                font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', sans-serif;
                color: $apple-dark-gray;
                line-height: 1.6;
                font-size: 14px;

                h1, h2, h3, h4, h5, h6 {
                  font-weight: 600;
                  margin-top: 1.5em;
                  margin-bottom: 0.5em;
                  color: $apple-dark-gray;
                }

                p {
                  margin: 0.5em 0;
                }

                code {
                  font-family: 'SF Mono', monospace;
                  background-color: rgba(0, 0, 0, 0.05);
                  padding: 2px 4px;
                  border-radius: 4px;
                  font-size: 0.9em;
                }

                pre {
                  background-color: rgba(0, 0, 0, 0.05);
                  border-radius: 6px;
                  padding: 12px;
                  overflow-x: auto;
                }
              }
            }
          }

        }
      }
    }

    // 右侧面板 - 题目列表
    .right-panel {
      flex: 1;

      .problem-list-card {
        @include apple-card;
        height: calc(100vh);
        display: flex;
        flex-direction: column;

        .list-header {
          @include apple-card-header;
          display: flex;
          justify-content: space-between;
          align-items: center;
          flex-shrink: 0;

          .header-left {
            .list-title {
              font-size: 15px;
              font-weight: 600;
              color: var(--el-color-primary);
              position: relative;
              padding-left: 10px;
              margin-bottom: 8px;
              display: block;

              &::before {
                content: '';
                position: absolute;
                left: 0;
                top: 50%;
                transform: translateY(-50%);
                width: 3px;
                height: 14px;
                background: var(--el-color-primary);
                border-radius: 3px;
              }
            }

            .list-stats {
              display: flex;
              gap: 12px;
              font-size: 12px;

              .current-page-info {
                color: var(--el-color-primary);
                font-weight: 600;
              }

              .total-problems {
                color: $apple-gray;
                font-weight: 500;
              }
            }
          }
        }

        // 分页控制器
        .pagination-controls {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 16px;
          padding: 4px 20px;
          border-bottom: 1px solid rgba(0, 0, 0, 0.04);
          background: linear-gradient(to right, rgba(var(--el-color-primary-rgb), 0.01), rgba(var(--el-color-primary-rgb), 0.005));
          flex-shrink: 0;

          .page-btn {
            font-size: 12px;
            padding: 8px 16px;
            border-radius: 8px;
            font-weight: 500;
            transition: all 0.3s ease;

            &:not(:disabled):hover {
              transform: translateY(-1px);
              box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.2);
            }

            &:disabled {
              opacity: 0.4;
              cursor: not-allowed;
            }
          }

          .page-indicators {
            display: flex;
            align-items: center;
            gap: 8px;

            .page-indicator {
              min-width: 32px;
              height: 32px;
              display: flex;
              align-items: center;
              justify-content: center;
              border-radius: 8px;
              font-size: 13px;
              font-weight: 500;
              cursor: pointer;
              transition: all 0.2s ease;
              color: $apple-gray;
              background: transparent;

              &:hover:not(.ellipsis):not(.active) {
                background: rgba(var(--el-color-primary-rgb), 0.1);
                color: var(--el-color-primary);
                transform: scale(1.05);
              }

              &.active {
                background: var(--el-color-primary);
                color: white;
                box-shadow: 0 2px 8px rgba(var(--el-color-primary-rgb), 0.3);
              }

              &.ellipsis {
                cursor: default;
                color: $apple-gray;
                opacity: 0.6;

                &:hover {
                  background: transparent;
                  transform: none;
                }
              }
            }
          }
        }

        // 题目列表内容
        .problem-list-content {
          flex: 1;
          overflow: hidden;
          display: flex;
          flex-direction: column;
          min-height: 0; // 确保flex子元素可以正确收缩

          .empty-state,
          .restricted-view {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 40px 20px;
          }

          .problem-grid {
            flex: 1;
            padding: 20px;
            overflow-y: auto;
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 14px;
            align-content: start;
            max-height: calc(100vh - 320px);

            // 自定义滚动条
            &::-webkit-scrollbar {
              width: 6px;
            }

            &::-webkit-scrollbar-track {
              background: rgba(0, 0, 0, 0.02);
              border-radius: 3px;
            }

            &::-webkit-scrollbar-thumb {
              background: rgba(var(--el-color-primary-rgb), 0.3);
              border-radius: 3px;
              transition: background 0.2s ease;

              &:hover {
                background: rgba(var(--el-color-primary-rgb), 0.5);
              }
            }

            .problem-card {
              background: $apple-white;
              border-radius: $border-radius-large;
              padding: 12px;
              cursor: pointer;
              transition: $transition-slow;
              position: relative;
              overflow: hidden;
              box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
              border: 1px solid rgba(0, 0, 0, 0.04);
              min-height: 110px;
              display: flex;
              flex-direction: column;

              &::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 3px;
                background: var(--el-color-primary);
                opacity: 0;
                transform: scaleX(0);
                transform-origin: left;
                transition: all 0.4s ease;
              }

              &:hover:not(.disabled) {
                transform: translateY(-4px);
                box-shadow: 0 12px 28px rgba(0, 0, 0, 0.08);

                &::before {
                  opacity: 1;
                  transform: scaleX(1);
                }

                .problem-title {
                  color: var(--el-color-primary);
                }
              }

              &.disabled {
                opacity: 0.7;
                cursor: not-allowed;

                &:hover {
                  transform: translateY(-2px);
                  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.06);
                }
              }

              .problem-header {
                display: flex;
                align-items: center;
                justify-content: space-between;
                margin-bottom: 12px;
                padding-bottom: 8px;
                border-bottom: 1px solid rgba(0, 0, 0, 0.04);

                .problem-number {
                  font-weight: 600;
                  color: var(--el-color-primary);
                  background: rgba(var(--el-color-primary-rgb), 0.1);
                  padding: 4px 8px;
                  border-radius: 8px;
                  font-size: 12px;
                  min-width: 32px;
                  text-align: center;
                }

                .problem-key {
                  font-weight: 500;
                  color: $apple-gray;
                  font-size: 12px;
                  opacity: 0.8;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
                  flex: 1;
                  margin: 0 8px;
                }

                .problem-status {
                  // 预留给状态图标
                  width: 20px;
                  height: 20px;
                }
              }

              .problem-content {
                flex: 1;
                display: flex;
                flex-direction: column;

                .problem-title {
                  font-size: 14px;
                  font-weight: 600;
                  margin-bottom: 10px;
                  color: $apple-dark-gray;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  -webkit-box-orient: vertical;
                  transition: color 0.3s ease;
                  line-height: 1.2;
                  flex: 1;
                }

                .problem-meta {
                  display: flex;
                  align-items: center;
                  justify-content: space-between;
                  gap: 8px;
                  margin-top: auto;

                  .problem-type-tag,
                  .difficulty-tag {
                    border-radius: $border-radius-small;
                    font-size: 11px;
                    padding: 3px 8px;
                    font-weight: 500;
                    border: none;
                    flex-shrink: 0;
                  }

                  .difficulty-tag {
                    font-weight: 600;
                  }
                }
              }

              // 禁用覆盖层
              .disabled-overlay {
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(255, 255, 255, 0.9);
                backdrop-filter: blur(4px);
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                color: $apple-gray;
                border-radius: $border-radius-large;

                .lock-icon {
                  font-size: 24px;
                  margin-bottom: 8px;
                  opacity: 0.6;
                }

                .disabled-text {
                  font-size: 13px;
                  font-weight: 500;
                  opacity: 0.8;
                }
              }
            }
          }
        }
      }
    }

  }

  // 响应式设计
  @media (max-width: 1200px) {
    .main-content-wrapper {
      flex-direction: column;
      gap: 16px;

      .left-panel {
        width: 100%;

        .topic-detail-card {
          max-height: 400px;
        }
      }

      .right-panel {
        .problem-list-card {
          height: calc(100vh - 500px);
          min-height: 600px;

          .problem-grid {
            grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
            gap: 12px;
            max-height: calc(100vh - 650px);
          }
        }
      }
    }
  }

  @media (max-width: 768px) {
    padding: 0;

    .top-navigation {
      padding: 12px 16px;

      .nav-content {
        .nav-left {
          gap: 12px;

          .topic-basic-info {
            .topic-title {
              font-size: 18px;
            }

            .topic-meta {
              gap: 8px;
              flex-wrap: wrap;

              .topic-type-tag,
              .problem-total {
                font-size: 11px;
                padding: 3px 8px;
              }
            }
          }
        }
      }
    }

    .main-content-wrapper {
      padding: 16px;
      gap: 12px;

      .left-panel {
        .topic-detail-card {
          max-height: 300px;

          .detail-content {
            max-height: calc(100vh - 400px);

            .time-section {
              padding: 12px;

              .time-display {
                flex-direction: column;
                gap: 8px;
                text-align: center;

                .time-separator {
                  transform: rotate(90deg);
                }
              }
            }

            .basic-info .info-row {
              flex-direction: column;
              align-items: flex-start;
              gap: 4px;

              .info-value {
                text-align: left;
              }
            }
          }
        }
      }

      .right-panel {
        .problem-list-card {
          height: calc(100vh - 500px);
          min-height: 400px;

          .list-header {
            padding: 12px 16px;

            .header-left .list-stats {
              flex-direction: column;
              gap: 4px;
            }
          }

          .pagination-controls {
            padding: 12px 16px;
            gap: 12px;

            .page-btn {
              font-size: 11px;
              padding: 6px 12px;
            }

            .page-indicators {
              gap: 4px;

              .page-indicator {
                min-width: 28px;
                height: 28px;
                font-size: 12px;
              }
            }
          }

          .problem-grid {
            padding: 16px;
            grid-template-columns: 1fr;
            gap: 12px;

            .problem-card {
              padding: 14px;
              min-height: 120px;

              .problem-header {
                margin-bottom: 10px;

                .problem-number {
                  font-size: 11px;
                  padding: 3px 6px;
                }

                .problem-key {
                  font-size: 11px;
                }
              }

              .problem-content {
                .problem-title {
                  font-size: 14px;
                  margin-bottom: 10px;
                }

                .problem-meta {
                  .problem-type-tag,
                  .difficulty-tag {
                    font-size: 10px;
                    padding: 2px 6px;
                  }
                }
              }
            }
          }
        }
      }
    }
  }

}

.ml-5 {
  margin-left: 5px;
}
</style>
