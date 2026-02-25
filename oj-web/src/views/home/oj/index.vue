<template>
  <div class="home-container">
    <!-- 欢迎区域 -->
    <section class="welcome-section">
      <div class="welcome-content">
        <h1 class="welcome-title">欢迎使用 {{ tenantName || 'Glowxq OJ' }}</h1>
        <p class="welcome-subtitle">在线编程评测系统，助力编程技能提升</p>
      </div>
      <div class="user-info-card" v-if="userInfo">
        <div class="user-header">
          <div class="user-avatar">
            <el-avatar :size="64" :src="userInfo.logo || ''" v-if="userInfo.logo">
              {{ getUserInitials() }}
            </el-avatar>
            <el-avatar :size="64" v-else class="default-avatar">
              {{ getUserInitials() }}
            </el-avatar>
          </div>
          <div class="user-details">
            <h2>{{ userInfo.nickname || userInfo.name || userInfo.username }}</h2>
            <div class="user-badges">
              <span class="user-title" v-if="userInfo.title" :style="{ color: userInfo.color || '#F56C6C' }">
                <el-icon><Trophy /></el-icon> {{ userInfo.title }}
              </span>
            </div>
          </div>
        </div>
        <div class="user-stats">
          <div class="stat-item">
            <el-icon><Check /></el-icon>
            <span class="stat-value">{{ userInfo.acNum || 0 }}</span>
            <span class="stat-label">已通过</span>
          </div>
          <div class="stat-item">
            <el-icon><Star /></el-icon>
            <span class="stat-value">{{ userInfo.integral || 0 }}</span>
            <span class="stat-label">积分</span>
          </div>
          <div class="stat-item">
            <el-icon><Upload /></el-icon>
            <span class="stat-value">{{ userInfo.submitNum || 0 }}</span>
            <span class="stat-label">提交</span>
          </div>
          <div class="stat-item">
            <el-icon><Calendar /></el-icon>
            <span class="stat-value">{{ userInfo.continuousSignDay || 0 }}</span>
            <span class="stat-label">连续签到</span>
          </div>
        </div>
        <div class="sign-in-area">
          <el-button
            type="primary"
            class="sign-in-button"
            :disabled="checkinResult.todayChecked"
            @click="handleSignIn"
            :loading="isSigningIn">
            {{ checkinResult.todayChecked ? '今日已签到' : '每日签到' }}
          </el-button>
          <div class="checkin-info" v-if="checkinResult.todayChecked">
            <p>连续签到 {{ checkinResult.continuousSignDay }} 天</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 全局统计数据展示 -->
    <section class="global-stats-section">
      <div class="section-title">
        <h2>平台概览</h2>
      </div>
      <div class="global-stats-grid" v-loading="isGlobalDataLoading">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ formatNumber(globalData.userCount) }}</div>
            <div class="stat-label">注册用户</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Collection /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ formatNumber(globalData.problemCount) }}</div>
            <div class="stat-label">题目总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Check /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ formatNumber(globalData.acCount) }}</div>
            <div class="stat-label">通过提交</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Upload /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ formatNumber(globalData.judgeCount) }}</div>
            <div class="stat-label">总提交数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Connection /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ formatNumber(globalData.topicCount) }}</div>
            <div class="stat-label">作业/比赛/题集</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ formatNumber(globalData.courseCount) }}</div>
            <div class="stat-label">课程总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><UserFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ formatNumber(globalData.groupCount) }}</div>
            <div class="stat-label">班级数量</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 公告和精选活动区 - 双栏布局 -->
    <div class="info-topics-section">
      <el-row :gutter="24">
        <!-- 首页公告展示 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <section class="announcement-section" v-if="showAnnouncement">
            <div class="section-title">
              <h2>
                <el-icon><Bell /></el-icon>
                首页公告
              </h2>
              <div class="title-actions">
                <el-button
                  text
                  type="primary"
                  @click="showAnnouncementDialog"
                  class="action-button"
                  title="全屏查看"
                >
                  <el-icon><FullScreen /></el-icon>
                </el-button>
                <el-button
                  text
                  type="primary"
                  @click="toggleAnnouncement"
                  class="toggle-button"
                >
                  {{ isAnnouncementCollapsed ? '展开' : '收起' }}
                  <el-icon class="toggle-icon" :class="{ 'is-collapsed': isAnnouncementCollapsed }">
                    <ArrowRight />
                  </el-icon>
                </el-button>
              </div>
            </div>
            <el-collapse-transition>
              <div class="announcement-content" v-show="!isAnnouncementCollapsed">
                <TextShow text-key="HomePageAnnouncement" default-content="暂无公告信息" />
              </div>
            </el-collapse-transition>
          </section>
        </el-col>

        <!-- 精选活动展示 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <section class="featured-topics-section" v-if="homeTopics.length > 0 || isHomeTopicsLoading">
            <div class="section-title">
              <h2>
                <el-icon><Connection /></el-icon>
                精选活动
              </h2>
              <div class="title-actions">
                <el-button text type="primary" @click="goTo('/oj/topic/topic')" v-if="homeTopics.length > 0">
                  查看更多 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
                </el-button>
                <el-button
                  text
                  type="primary"
                  @click="toggleTopics"
                  class="toggle-button"
                >
                  {{ isTopicsCollapsed ? '展开' : '收起' }}
                  <el-icon class="toggle-icon" :class="{ 'is-collapsed': isTopicsCollapsed }">
                    <ArrowRight />
                  </el-icon>
                </el-button>
              </div>
            </div>
            <el-collapse-transition>
              <div class="featured-topics-preview" v-loading="isHomeTopicsLoading" v-show="!isTopicsCollapsed">
                <!-- 活动滚动容器 -->
                <div class="topics-scroll-container">
                  <div
                    v-for="topic in homeTopics"
                    :key="topic.id"
                    class="preview-topic-card"
                    @click="goToTopic(topic)"
                  >
                    <div class="topic-header-row">
                      <div class="topic-status" :class="getTopicStatusClass(topic)">
                        <span class="status-dot"></span>
                        <span>{{ getTopicStatusText(topic) }}</span>
                      </div>

                      <div class="topic-type" v-if="topic.topicType">
                        <EnumShow
                          :enum="TopicType"
                          :code="topic.topicType"
                          backgroundColor="rgba(var(--primary-color-rgb), 0.15)"
                          color="var(--primary-color)"
                          fontWeight="600"
                          fontSize="12px"
                          padding="2px 8px"
                          borderRadius="8px"
                          class="mini-type-enum"
                        />
                      </div>
                    </div>

                    <div class="topic-content">
                      <div class="topic-header">
                        <h3 class="topic-title">{{ topic.name }}</h3>
                      </div>

                      <div class="topic-description" v-if="topic.description">
                        {{ topic.description }}
                      </div>

                      <div class="topic-info-row">
                        <div class="topic-time" v-if="topic.startTime || topic.endTime">
                          <el-icon><Clock /></el-icon>
                          <span>
                            {{ topic.startTime ? formatTopicTime(topic.startTime) : '' }}
                            {{ topic.startTime && topic.endTime ? ' - ' : '' }}
                            {{ topic.endTime ? formatTopicTime(topic.endTime) : '' }}
                          </span>
                        </div>
                      </div>
                    </div>

                    <div class="action-btn-container">
                      <el-button
                        type="primary"
                        size="small"
                        class="action-btn"
                        @click.stop="goToTopic(topic)"
                      >
                        {{ getTopicActionText(topic) }}
                      </el-button>
                    </div>
                  </div>
                </div>

                <div class="view-more" @click="goTo('/oj/topic/topic')" v-if="homeTopics.length > 2">
                  <span>查看更多活动</span>
                  <el-icon><ArrowRight /></el-icon>
                </div>
              </div>
            </el-collapse-transition>
          </section>
        </el-col>
      </el-row>
    </div>

    <!-- 快捷功能区 - 以卡片方式展示 -->
    <section class="quick-actions-section">
      <div class="section-title">
        <h2>快捷功能</h2>
      </div>
      <div class="quick-actions-grid">
        <div class="quick-action-card" @click="goTo('/oj/problem/problem')">
          <div class="action-icon">
            <el-icon><List /></el-icon>
          </div>
          <div class="action-content">
            <h3>题目列表</h3>
            <p>浏览所有可用的编程题目</p>
          </div>
        </div>
        <div class="quick-action-card" @click="goTo('/oj/topic/topic')">
          <div class="action-icon">
            <el-icon><Connection /></el-icon>
          </div>
          <div class="action-content">
            <h3>主题列表</h3>
            <p>查看所有比赛、作业和练习</p>
          </div>
        </div>
        <div class="quick-action-card" @click="goTo('/oj/group/group')">
          <div class="action-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="action-content">
            <h3>班级列表</h3>
            <p>加入或管理您的班级</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 数据分析区域 - 并排展示题型分布和排行榜 -->
    <section class="data-analysis-section">
      <div class="data-analysis-container">
        <!-- 题型分布统计 -->
        <div class="problem-type-section">
          <div class="section-title">
            <h2>题型分布</h2>
          </div>
          <div class="problem-type-container" v-loading="isProblemTypeLoading">
            <div class="chart-container" ref="chartRef"></div>
            <div class="problem-type-cards">
              <div
                v-for="(item, index) in problemTypeData"
                :key="index"
                class="type-card"
                :style="{ backgroundColor: getTypeColor(index, 0.1), color: getTypeColor(index, 1) }"
              >
                <div class="type-name">
                  <EnumShow :enum="ProblemType" :code="item.problemType" />
                </div>
                <div class="type-count">{{ item.number }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 排行榜区域 -->
        <div class="ranking-section">
          <div class="section-title">
            <h2>解题排行榜</h2>
          </div>
          <div class="ranking-content" v-loading="isRankLoading">
            <div
              v-for="(user, index) in rankingList.slice(0, 5)"
              :key="user.id"
              class="rank-item"
              :class="{
                'rank-top1': index === 0,
                'rank-top2': index === 1,
                'rank-top3': index === 2
              }"
            >
              <div class="rank-position">
                <div class="rank-number" v-if="index < 3">
                  <el-icon v-if="index === 0"><Trophy /></el-icon>
                  <el-icon v-else-if="index === 1"><Trophy /></el-icon>
                  <el-icon v-else-if="index === 2"><Trophy /></el-icon>
                </div>
                <div class="rank-number" v-else>{{ index + 1 }}</div>
              </div>

              <div class="user-info">
                <div class="user-avatar">
                  <el-avatar :size="32" class="default-avatar">
                    {{ getNameInitial(user) }}
                  </el-avatar>
                </div>
                <div class="user-details">
                  <div class="user-name">{{ user.name || '用户' }}</div>
                  <div class="user-title" v-if="user.title" :style="{ color: user.color || '#F56C6C' }">
                    {{ user.title }}
                  </div>
                </div>
              </div>

              <div class="user-stats">
                <div class="stat-item ac-num">
                  <el-tooltip content="AC数量" placement="top">
                    <div class="stat-icon-box">
                      <el-icon><Check /></el-icon>
                      <span>{{ user.acNum }}</span>
                    </div>
                  </el-tooltip>
                </div>
                <div class="stat-item integral">
                  <el-tooltip content="积分" placement="top">
                    <div class="stat-icon-box">
                      <el-icon><Star /></el-icon>
                      <span>{{ user.integral }}</span>
                    </div>
                  </el-tooltip>
                </div>
              </div>
            </div>

            <el-empty description="暂无排名数据" v-if="rankingList.length === 0" />
          </div>
        </div>
      </div>
    </section>

    <!-- 公告全屏弹窗 -->
    <el-dialog
      v-model="announcementDialogVisible"
      title="公告详情"
      width="80%"
      :destroy-on-close="true"
      class="announcement-dialog"
    >
      <div class="announcement-dialog-content">
        <TextShow text-key="HomePageAnnouncement" default-content="暂无公告信息" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Trophy, Check, Upload, Star, Calendar, List, Connection, Document, ArrowRight,
  User, Collection, Reading, UserFilled, Bell, Clock, FullScreen, Expand } from '@element-plus/icons-vue';
import { getUserDetailApi, rankGlobalApi, signApi, getGlobalDataCountApi, countProblemTypeDataApi, getHomeTopicApi } from '@/api/modules/oj/home/home';
import { useAppStore } from '@/stores/modules/app';
import { useTenantStore } from '@/stores/modules/tenant';
import { storeToRefs } from 'pinia';
import type { IHome } from '@/api/interface/oj/home/home';
import type { IUserInfo } from '@/api/interface/oj/user/userInfo';
// 引入echarts
import * as echarts from 'echarts/core';
import type { EChartsType } from 'echarts/core';
import { PieChart } from 'echarts/charts';
import { TooltipComponent, LegendComponent, TitleComponent } from 'echarts/components';
import { LabelLayout } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';
// 引入题型枚举
import { ProblemType } from '@/enums/oj/problem';
import { TopicType } from '@/enums/oj/topic/TopicType';
// 引入枚举显示组件
import EnumShow from '@/components/Common/Enum/EnumShow.vue';
// 引入文本显示组件
import TextShow from '@/components/Common/Text/TextShow.vue';

// 注册必须的组件
echarts.use([
  PieChart,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  LabelLayout,
  CanvasRenderer
]);

// 路由
const router = useRouter();

// 获取主题颜色
const appStore = useAppStore();
const tenantStore = useTenantStore();
const themePrimary = computed(() => appStore.primary);

// 获取租户名称
const tenantName = computed(() => tenantStore.tenantName);

// 用户详情
const userInfo = ref<any>({});
const isUserLoading = ref(false);

// 排名列表
const rankingList = ref<any[]>([]);
const isRankLoading = ref(false);

// 签到结果
const checkinResult = ref<any>({
  signDayIntegral: 0,
  totalIntegral: 0,
  continuousSignDay: 0,
  todayChecked: false
});
const isSigningIn = ref(false);

// 全局统计数据
const globalData = ref<any>({
  userCount: 0,
  topicCount: 0,
  problemCount: 0,
  acCount: 0,
  judgeCount: 0,
  groupCount: 0,
  courseCount: 0
});
const isGlobalDataLoading = ref(false);

// 题型统计数据
const problemTypeData = ref<any[]>([]);
const isProblemTypeLoading = ref(false);
const chartRef = ref<HTMLElement | null>(null);
let chart: ReturnType<typeof echarts.init> | null = null;

// 题型颜色
const typeColors = [
  '#1890ff', '#13c2c2', '#52c41a', '#faad14', '#f5222d',
  '#722ed1', '#eb2f96', '#fa8c16', '#a0d911', '#2f54eb'
];

// 首页比赛数据
const homeTopics = ref<any[]>([]);
const isHomeTopicsLoading = ref(false);

// 是否显示公告
const showAnnouncement = ref(true);
const isAnnouncementCollapsed = ref(false);

// 是否显示活动
const isTopicsCollapsed = ref(false);

// 公告弹窗控制
const announcementDialogVisible = ref(false);

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    isUserLoading.value = true;
    const res = await getUserDetailApi();
    if (res.data) {
      userInfo.value = res.data;

      // 更新签到信息
      if (userInfo.value.continuousSignDay) {
        checkinResult.value.continuousSignDay = userInfo.value.continuousSignDay;
      }
      if (userInfo.value.integral) {
        checkinResult.value.totalIntegral = userInfo.value.integral;
      }
    }
  } catch (error) {
    console.error('获取用户信息失败', error);
  } finally {
    isUserLoading.value = false;
  }
};

// 获取排名数据
const fetchRanking = async () => {
  try {
    isRankLoading.value = true;
    const res = await rankGlobalApi();
    if (res.data) {
      rankingList.value = res.data;
    }
  } catch (error) {
    console.error('获取排名数据失败', error);
  } finally {
    isRankLoading.value = false;
  }
};

// 获取全局统计数据
const fetchGlobalData = async () => {
  try {
    isGlobalDataLoading.value = true;
    const res = await getGlobalDataCountApi();
    if (res.data) {
      globalData.value = res.data;
    }
  } catch (error) {
    console.error('获取全局统计数据失败', error);
  } finally {
    isGlobalDataLoading.value = false;
  }
};

// 获取题型统计数据
const fetchProblemTypeData = async () => {
  try {
    isProblemTypeLoading.value = true;
    const res = await countProblemTypeDataApi();
    if (res.data) {
      problemTypeData.value = res.data;
      nextTick(() => {
        initChart();
      });
    }
  } catch (error) {
    console.error('获取题型统计数据失败', error);
  } finally {
    isProblemTypeLoading.value = false;
  }
};

// 获取首页比赛数据
const fetchHomeTopics = async () => {
  try {
    isHomeTopicsLoading.value = true;
    const res = await getHomeTopicApi(6); // 获取6个比赛
    if (res.data) {
      homeTopics.value = res.data;
    }
  } catch (error) {
    console.error('获取首页比赛数据失败', error);
  } finally {
    isHomeTopicsLoading.value = false;
  }
};

// 获取比赛状态类名
const getTopicStatusClass = (topic: any) => {
  const now = new Date();
  const startTime = topic.startTime ? new Date(topic.startTime) : null;
  const endTime = topic.endTime ? new Date(topic.endTime) : null;

  if (!topic.enable) return 'status-disabled';
  if (startTime && now < startTime) return 'status-upcoming';
  if (endTime && now > endTime) return 'status-ended';
  return 'status-ongoing';
};

// 获取比赛状态文本
const getTopicStatusText = (topic: any) => {
  const now = new Date();
  const startTime = topic.startTime ? new Date(topic.startTime) : null;
  const endTime = topic.endTime ? new Date(topic.endTime) : null;

  if (!topic.enable) return '已禁用';
  if (startTime && now < startTime) return '即将开始';
  if (endTime && now > endTime) return '已结束';
  return '进行中';
};

// 格式化比赛时间
const formatTopicTime = (timeStr: string) => {
  if (!timeStr) return '';
  const date = new Date(timeStr);
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  return `${month}月${day}日 ${hours}:${minutes}`;
};

// 跳转到比赛详情
const goToTopic = (topic: any) => {
  router.push(`/oj/topic/detail/${topic.id}`);
};

// 获取比赛状态（用于禁用检查）
const getTopicStatus = (topic: any) => {
  const now = new Date();
  const startTime = topic.startTime ? new Date(topic.startTime) : null;
  const endTime = topic.endTime ? new Date(topic.endTime) : null;

  if (!topic.enable) return 'disabled';
  if (startTime && now < startTime) return 'upcoming';
  if (endTime && now > endTime) return 'ended';
  return 'ongoing';
};

// 获取比赛操作按钮文本
const getTopicActionText = (topic: any) => {
  const status = getTopicStatus(topic);

  if (status === 'disabled') return '已禁用';
  if (status === 'upcoming') return '即将开始';
  if (status === 'ended') return '查看结果';
  return '进入比赛';
};

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return;

  // 销毁已存在的图表实例
  if (chart) {
    chart.dispose();
  }

  chart = echarts.init(chartRef.value);
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      show: false
    },
    series: [
      {
        name: '题型分布',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: problemTypeData.value.map((item: any, index: number) => ({
          value: item.number,
          name: getProblemTypeName(item.problemType),
          itemStyle: {
            color: getTypeColor(index, 1)
          }
        }))
      }
    ]
  };

  chart.setOption(option);

  // 窗口大小变化时重新调整图表大小
  window.addEventListener('resize', () => {
    chart?.resize();
  });
};

// 设置CSS变量
const setCssVariables = () => {
  if (themePrimary.value) {
    document.documentElement.style.setProperty('--primary-light-bg', `rgba(${hexToRgb(themePrimary.value)}, 0.1)`);
    document.documentElement.style.setProperty('--primary-color', themePrimary.value);
    document.documentElement.style.setProperty('--primary-color-rgb', hexToRgb(themePrimary.value));
  }
};

// 将hex颜色转为rgb
const hexToRgb = (hex: string) => {
  // 移除可能存在的#号
  hex = hex.replace('#', '');

  // 解析RGB值
  const r = parseInt(hex.substring(0, 2), 16);
  const g = parseInt(hex.substring(2, 4), 16);
  const b = parseInt(hex.substring(4, 6), 16);

  return `${r}, ${g}, ${b}`;
};

// 执行签到
const handleSignIn = async () => {
  if (checkinResult.value.todayChecked) {
    ElMessage.info('今日已签到');
    return;
  }

  try {
    isSigningIn.value = true;
    const res = await signApi();
    if (res.data) {
      checkinResult.value = res.data;

      // 更新用户信息中的签到数据
      userInfo.value.continuousSignDay = res.data.continuousSignDay;
      userInfo.value.integral = res.data.totalIntegral;

      ElMessage.success(`签到成功，获得 ${res.data.signDayIntegral} 积分`);
    }
  } catch (error) {
    console.error('签到失败', error);
  } finally {
    isSigningIn.value = false;
  }
};

// 跳转到指定页面
const goTo = (path: string) => {
  router.push(path);
};

// 获取用户名首字母
const getUserInitials = () => {
  if (userInfo.value.nickname) return userInfo.value.nickname.substring(0, 1);
  if (userInfo.value.name) return userInfo.value.name.substring(0, 1);
  if (userInfo.value.username) return userInfo.value.username.substring(0, 1);
  return '用';
};

// 获取排名用户首字母
const getNameInitial = (user: any) => {
  if (user.name) return user.name.substring(0, 1);
  return '用';
};

// 格式化数字（添加千位分隔符）
const formatNumber = (num: number) => {
  if (num === undefined || num === null) return 0;
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
};

// 获取题型颜色
const getTypeColor = (index: number, opacity: number) => {
  const colorIndex = index % typeColors.length;
  if (opacity === 1) {
    return typeColors[colorIndex];
  }
  // 将hex颜色转为rgba
  const hex = typeColors[colorIndex].replace('#', '');
  const r = parseInt(hex.substring(0, 2), 16);
  const g = parseInt(hex.substring(2, 4), 16);
  const b = parseInt(hex.substring(4, 6), 16);
  return `rgba(${r}, ${g}, ${b}, ${opacity})`;
};

// 获取题型名称
const getProblemTypeName = (type: string) => {
  const problemTypeEnum = Object.values(ProblemType).find((item: any) => item.code === type);
  return (problemTypeEnum && (problemTypeEnum as any).name) ? (problemTypeEnum as any).name : type;
};

// 切换公告折叠状态
const toggleAnnouncement = () => {
  isAnnouncementCollapsed.value = !isAnnouncementCollapsed.value;
};

// 切换活动折叠状态
const toggleTopics = () => {
  isTopicsCollapsed.value = !isTopicsCollapsed.value;
};

// 显示公告弹窗
const showAnnouncementDialog = () => {
  announcementDialogVisible.value = true;
};

onMounted(() => {
  fetchUserInfo();
  fetchRanking();
  fetchGlobalData();
  fetchProblemTypeData();
  fetchHomeTopics(); // 获取首页比赛数据
  setCssVariables();

  // 监听主题变化重新设置CSS变量
  watch(themePrimary, () => {
    setCssVariables();
  });
});
</script>

<style scoped lang="scss">
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--oj-space-6);
  font-family: var(--oj-font-family);

  // 欢迎部分
  .welcome-section {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 32px;
    gap: 24px;

    @media (max-width: 768px) {
      flex-direction: column;
    }

    .welcome-content {
      flex: 1;

      .welcome-title {
        font-size: var(--oj-text-3xl);
        font-weight: 700;
        color: v-bind('themePrimary');
        margin-bottom: var(--oj-space-3);
        letter-spacing: -0.5px;
        background: linear-gradient(90deg, v-bind('themePrimary'), #42aaff);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
      }

      .welcome-subtitle {
        font-size: var(--oj-text-md);
        color: var(--oj-text-secondary);
        line-height: 1.5;
      }
    }

    .user-info-card {
      flex: 1;
      background: var(--oj-glass-bg);
      backdrop-filter: var(--oj-glass-blur);
      border-radius: var(--oj-radius-lg);
      padding: var(--oj-space-6);
      box-shadow: var(--oj-shadow-md);
      max-width: 400px;
      transition: transform var(--oj-transition-normal), box-shadow var(--oj-transition-normal);
      border: 1px solid var(--oj-glass-border);

      &:hover {
        transform: translateY(-2px);
        box-shadow: var(--oj-shadow-hover);
      }

      .user-header {
        display: flex;
        align-items: center;
        margin-bottom: 20px;

        .user-avatar {
          margin-right: 16px;

          .el-avatar {
            border: 2px solid #fff;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

            &.default-avatar {
              background: v-bind('themePrimary');
              color: white;
              font-weight: 500;
            }
          }
        }

        .user-details {
          h2 {
            margin: 0 0 8px;
            font-size: 20px;
            font-weight: 600;
          }

          .user-badges {
            display: flex;
            gap: 8px;

            .user-title {
              display: flex;
              align-items: center;
              font-size: 13px;
              font-weight: 500;

              .el-icon {
                margin-right: 4px;
              }
            }
          }
        }
      }

      .user-stats {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16px;
        margin-bottom: 20px;

        .stat-item {
          background-color: rgba(0, 0, 0, 0.03);
          border-radius: 12px;
          padding: 12px;
          display: flex;
          flex-direction: column;
          align-items: center;
          transition: all 0.2s ease;

          &:hover {
            background-color: rgba(0, 0, 0, 0.05);
            transform: translateY(-2px);
          }

          .el-icon {
            font-size: 24px;
            margin-bottom: 8px;
            color: v-bind('themePrimary');
          }

          .stat-value {
            font-size: 20px;
            font-weight: 600;
            margin-bottom: 4px;
          }

          .stat-label {
            font-size: 12px;
            color: #666;
          }
        }
      }

      .sign-in-area {
        text-align: center;

        .sign-in-button {
          width: 100%;
          height: 44px;
          border-radius: 12px;
          font-size: 16px;
          font-weight: 500;
          background: v-bind('themePrimary');
          border: none;
          transition: all 0.3s ease;

          &:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            filter: brightness(1.05);
          }

          &:disabled {
            opacity: 0.8;
            cursor: not-allowed;
          }
        }

        .checkin-info {
          margin-top: 12px;
          font-size: 14px;
          color: #666;
        }
      }
    }
  }

  // 全局统计部分
  .global-stats-section {
    margin-bottom: 32px;

    .section-title {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 16px;

      h2 {
        font-size: 22px;
        font-weight: 600;
        margin: 0;
      }
    }

    .global-stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
      gap: 16px;

      .stat-card {
        background: var(--oj-glass-bg);
        backdrop-filter: var(--oj-glass-blur);
        border-radius: var(--oj-radius-lg);
        padding: var(--oj-space-5);
        display: flex;
        align-items: center;
        box-shadow: var(--oj-shadow-sm);
        transition: all var(--oj-transition-normal);
        border: 1px solid var(--oj-glass-border);

        &:hover {
          transform: translateY(-2px);
          box-shadow: var(--oj-shadow-hover);
        }

        .stat-icon {
          width: 48px;
          height: 48px;
          border-radius: var(--oj-radius-md);
          background-color: var(--primary-light-bg);
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16px;

          .el-icon {
            font-size: 24px;
            color: v-bind('themePrimary');
          }
        }

        .stat-info {
          flex: 1;

          .stat-value {
            font-size: 24px;
            font-weight: 700;
            margin-bottom: 4px;
            font-feature-settings: "tnum";
            font-variant-numeric: tabular-nums;
          }

          .stat-label {
            font-size: var(--oj-text-base);
            color: var(--oj-text-secondary);
          }
        }
      }
    }
  }

  // 数据分析区域
  .data-analysis-section {
    margin-bottom: 32px;

    .data-analysis-container {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 24px;

      @media (max-width: 992px) {
        grid-template-columns: 1fr;
      }
    }
  }

  // 题型分布部分 - 调整为适应新布局
  .problem-type-section {
    .section-title {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 16px;

      h2 {
        font-size: 22px;
        font-weight: 600;
        margin: 0;
      }
    }

    .problem-type-container {
      display: flex;
      flex-direction: column;
      background: var(--oj-glass-bg);
      backdrop-filter: var(--oj-glass-blur);
      border-radius: var(--oj-radius-lg);
      overflow: hidden;
      box-shadow: var(--oj-shadow-md);
      border: 1px solid var(--oj-glass-border);
      height: 100%;

      .chart-container {
        flex: 1;
        height: 220px;
        padding: 16px;
      }

      .problem-type-cards {
        padding: 0 16px 16px;
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        border-top: 1px solid rgba(0, 0, 0, 0.05);

        .type-card {
          padding: 6px 10px;
          border-radius: 6px;
          display: flex;
          justify-content: space-between;
          align-items: center;
          gap: 8px;
          transition: all 0.2s ease;
          flex: 1;
          min-width: calc(50% - 8px);

          &:hover {
            transform: translateY(-2px);
          }

          .type-name {
            font-size: 13px;
            font-weight: 500;
          }

          .type-count {
            font-size: 13px;
            font-weight: 600;
          }
        }
      }
    }
  }

  // 排行榜区域
  .ranking-section {
    .section-title {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 16px;

      h2 {
        font-size: 22px;
        font-weight: 600;
        margin: 0;
      }
    }

    .ranking-content {
      background: var(--oj-glass-bg);
      backdrop-filter: var(--oj-glass-blur);
      border-radius: var(--oj-radius-lg);
      overflow: hidden;
      box-shadow: var(--oj-shadow-md);
      border: 1px solid var(--oj-glass-border);
      height: 100%;
      display: flex;
      flex-direction: column;

      .rank-item {
        display: flex;
        align-items: center;
        padding: 16px 20px;
        transition: all 0.3s ease;
        flex: 1;
        animation: slideInLeft 0.6s ease-out;
        position: relative;
        overflow: hidden;

        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 0;
          height: 100%;
          width: 4px;
          background: transparent;
          transition: all 0.3s ease;
        }

        &:hover {
          background-color: rgba(0, 0, 0, 0.02);
          transform: translateX(8px);

          &::before {
            background: v-bind('themePrimary');
          }
        }

        &:not(:last-child) {
          border-bottom: 1px solid rgba(0, 0, 0, 0.05);
        }

        &.rank-top1 {
          background: linear-gradient(135deg, rgba(255, 215, 0, 0.1), rgba(255, 215, 0, 0.05));

          .rank-position .rank-number {
            color: #ff9500;
            font-size: 18px;
            font-weight: 700;
          }
        }

        &.rank-top2 {
          background: linear-gradient(135deg, rgba(192, 192, 192, 0.1), rgba(192, 192, 192, 0.05));

          .rank-position .rank-number {
            color: #8e8e93;
            font-size: 16px;
            font-weight: 600;
          }
        }

        &.rank-top3 {
          background: linear-gradient(135deg, rgba(205, 127, 50, 0.1), rgba(205, 127, 50, 0.05));

          .rank-position .rank-number {
            color: #c46e00;
            font-size: 16px;
            font-weight: 600;
          }
        }

        .rank-position {
          width: 32px;
          display: flex;
          justify-content: center;
          margin-right: 16px;

          .rank-number {
            font-size: 14px;
            font-weight: 600;
            font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;

            .el-icon {
              font-size: 20px;
              animation: bounce 2s infinite;
            }
          }
        }

        .user-info {
          flex: 1;
          display: flex;
          align-items: center;

          .user-avatar {
            margin-right: 12px;

            .el-avatar {
              border: 2px solid #fff;
              box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
              transition: all 0.3s ease;

              &:hover {
                transform: scale(1.1);
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
              }

              &.default-avatar {
                background: linear-gradient(135deg, v-bind('themePrimary'), rgba(66, 170, 255, 0.8));
                color: white;
                font-weight: 700;
              }
            }
          }

          .user-details {
            .user-name {
              font-size: var(--oj-text-base);
              font-weight: var(--oj-font-semibold);
              color: var(--oj-text-primary);
              margin-bottom: 2px;
              font-family: var(--oj-font-family);
            }

            .user-title {
              font-size: 11px;
              font-weight: 500;
              padding: 2px 6px;
              border-radius: 8px;
              background: rgba(245, 108, 108, 0.1);
              color: #F56C6C;
            }
          }
        }

        .user-stats {
          display: flex;
          gap: 8px;

          .stat-item {
            min-width: 60px;
            border-radius: 12px;
            padding: 6px 8px;
            transition: all 0.3s ease;

            .stat-icon-box {
              display: flex;
              align-items: center;
              justify-content: center;

              .el-icon {
                margin-right: 4px;
                font-size: 12px;
              }

              span {
                font-weight: 700;
                font-size: 12px;
                font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
              }
            }

            &.ac-num {
              background: linear-gradient(135deg, rgba(52, 199, 89, 0.15), rgba(52, 199, 89, 0.05));
              color: #34c759;
            }

            &.integral {
              background: linear-gradient(135deg, rgba(255, 149, 0, 0.15), rgba(255, 149, 0, 0.05));
              color: #ff9500;
            }

            &:hover {
              transform: translateY(-2px);
              filter: brightness(1.1);
            }
          }
        }
      }

      .el-empty {
        margin: auto;
        padding: 20px;
      }
    }
  }

  // 公告和精选活动双栏区域
  .info-topics-section {
    margin-bottom: 32px;

    .el-row {
      display: flex;
      height: 450px;

      @media (max-width: 768px) {
        flex-direction: column;
        height: auto;
      }
    }

    .el-col {
      display: flex;
      flex-direction: column;
      height: 100%;
    }
  }

  // 首页公告区域
  .announcement-section {
    margin-bottom: 32px;
    height: 100%;
    display: flex;
    flex-direction: column;
    flex: 1;

    .section-title {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 16px;

      h2 {
        font-size: 22px;
        font-weight: 600;
        margin: 0;
        display: flex;
        align-items: center;
        gap: 8px;

        .el-icon {
          color: #FF9500;
          font-size: 20px;
        }
      }

      .title-actions {
        display: flex;
        align-items: center;
        gap: 8px;

        .action-button {
          font-size: 16px;
          border-radius: 50%;
          padding: 8px;
          width: 36px;
          height: 36px;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: all 0.3s ease;

          &:hover {
            background: rgba(64, 158, 255, 0.1);
            transform: scale(1.1);
          }
        }

        .toggle-button {
          font-weight: 500;
          padding: 8px 16px;
          border-radius: 20px;
          background: rgba(255, 255, 255, 0.1);
          transition: all 0.3s ease;

          &:hover {
            background: rgba(255, 255, 255, 0.2);
            transform: translateY(-1px);
          }

          .toggle-icon {
            margin-left: 4px;
            transition: transform 0.3s ease;

            &.is-collapsed {
              transform: rotate(90deg);
            }
          }
        }
      }
    }

    .announcement-content {
      background: var(--oj-glass-bg);
      backdrop-filter: var(--oj-glass-blur);
      border-radius: var(--oj-radius-lg);
      padding: var(--oj-space-5) var(--oj-space-6);
      box-shadow: var(--oj-shadow-md);
      border: 1px solid var(--oj-glass-border);
      transition: all var(--oj-transition-normal);
      height: 350px;
      min-height: unset;
      max-height: unset;
      flex: 1;
      overflow-y: auto;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
      }

      /* 自定义滚动条样式 */
      &::-webkit-scrollbar {
        width: 4px;
      }

      &::-webkit-scrollbar-track {
        background: rgba(0, 0, 0, 0.02);
        border-radius: 2px;
      }

      &::-webkit-scrollbar-thumb {
        background-color: rgba(0, 0, 0, 0.1);
        border-radius: 2px;

        &:hover {
          background-color: rgba(0, 0, 0, 0.2);
        }
      }
    }
  }

  // 精选活动预览区域
  .featured-topics-section {
    margin-bottom: 32px;
    height: 100%;
    display: flex;
    flex-direction: column;
    flex: 1;

    .section-title {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 16px;

      h2 {
        font-size: 22px;
        font-weight: 600;
        margin: 0;
        display: flex;
        align-items: center;
        gap: 8px;

        .el-icon {
          color: #5856D6;
          font-size: 20px;
        }
      }

      .title-actions {
        display: flex;
        align-items: center;
        gap: 8px;
      }
    }

    .featured-topics-preview {
      background: var(--oj-glass-bg);
      backdrop-filter: var(--oj-glass-blur);
      border-radius: var(--oj-radius-lg);
      overflow: hidden;
      box-shadow: var(--oj-shadow-md);
      border: 1px solid var(--oj-glass-border);
      padding: 16px;
      position: relative;
      height: 350px;
      min-height: unset;
      max-height: unset;
      flex: 1;
      display: flex;
      flex-direction: column;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 4px;
        background: linear-gradient(90deg, v-bind('themePrimary'), rgba(66, 170, 255, 0.7));
      }

      .topics-scroll-container {
        flex: 1;
        overflow-y: auto;
        -webkit-overflow-scrolling: touch;
        padding-right: 4px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        /* 自定义滚动条样式 */
        &::-webkit-scrollbar {
          width: 4px;
        }

        &::-webkit-scrollbar-track {
          background: rgba(0, 0, 0, 0.02);
          border-radius: 2px;
        }

        &::-webkit-scrollbar-thumb {
          background-color: rgba(0, 0, 0, 0.1);
          border-radius: 2px;

          &:hover {
            background-color: rgba(0, 0, 0, 0.2);
          }
        }
      }

      .preview-topic-card {
        display: flex;
        flex-direction: column;
        padding: 16px;
        background: rgba(255, 255, 255, 0.8);
        border-radius: 14px;
        margin-bottom: 12px;
        cursor: pointer;
        transition: all 0.3s ease;
        border: 1px solid rgba(0, 0, 0, 0.03);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        backdrop-filter: blur(10px);
        height: calc(50% - 10px);

        &:only-child {
          height: calc(100% - 6px);
        }

        &:first-child:nth-last-child(2),
        &:last-child:nth-child(2) {
          height: calc(50% - 6px);
        }

        &:last-child {
          margin-bottom: 0;
        }

        &:hover {
          transform: translateY(-2px);
          box-shadow: var(--oj-shadow-hover);
          background: #fff;
        }

        .topic-header-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 8px;
        }

        .topic-status {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 12px;
          font-weight: 600;
          padding: 4px 8px;
          border-radius: 12px;

          .status-dot {
            width: 6px;
            height: 6px;
            border-radius: 50%;
          }

          &.status-upcoming {
            color: #E6A23C;
            background: rgba(230, 162, 60, 0.1);
            .status-dot {
              background: #E6A23C;
              animation: pulse 2s infinite;
            }
          }

          &.status-ongoing {
            color: #67C23A;
            background: rgba(103, 194, 58, 0.1);
            .status-dot {
              background: #67C23A;
              animation: pulse 2s infinite;
            }
          }

          &.status-ended, &.status-disabled {
            color: #909399;
            background: rgba(144, 147, 153, 0.1);
            .status-dot {
              background: #909399;
            }
          }
        }

        .topic-content {
          flex: 1;
          min-width: 0;
          display: flex;
          flex-direction: column;

          .topic-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 6px;

            .topic-title {
              margin: 0;
              font-size: 16px;
              font-weight: 600;
              color: #333;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
              max-width: 90%;
            }
          }

          .topic-description {
            font-size: 12px;
            color: #666;
            margin: 4px 0 8px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
            line-height: 1.5;
            flex: 1;
          }

          .topic-info-row {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-top: auto;
            padding-top: 6px;
            border-top: 1px solid rgba(0, 0, 0, 0.05);
          }

          .topic-time {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 12px;
            color: #666;

            .el-icon {
              color: v-bind('themePrimary');
            }
          }
        }

        .action-btn-container {
          display: flex;
          justify-content: center;
          margin-top: 10px;
        }
      }

      .view-more {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 4px;
        padding: 10px 0 0;
        font-size: 14px;
        color: v-bind('themePrimary');
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s ease;
        margin-top: auto;

        &:hover {
          opacity: 0.8;
          text-decoration: underline;
        }

        .el-icon {
          transition: transform 0.2s ease;
        }

        &:hover .el-icon {
          transform: translateX(3px);
        }
      }
    }
  }

  // 快捷功能区
  .quick-actions-section {
    margin-bottom: 32px;

    .section-title {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 16px;

      h2 {
        font-size: 22px;
        font-weight: 600;
        margin: 0;
      }
    }

    .quick-actions-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 20px;

      .quick-action-card {
        display: flex;
        align-items: center;
        padding: var(--oj-space-5);
        background: var(--oj-glass-bg);
        backdrop-filter: var(--oj-glass-blur);
        border-radius: var(--oj-radius-lg);
        box-shadow: var(--oj-shadow-sm);
        transition: all var(--oj-transition-normal);
        cursor: pointer;
        border: 1px solid var(--oj-glass-border);

        &:hover {
          transform: translateY(-2px);
          box-shadow: var(--oj-shadow-hover);
          background: rgba(255, 255, 255, 0.9);
        }

        .action-icon {
          width: 56px;
          height: 56px;
          border-radius: var(--oj-radius-lg);
          background-color: var(--primary-light-bg);
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 20px;

          .el-icon {
            font-size: 28px;
            color: v-bind('themePrimary');
          }
        }

        .action-content {
          flex: 1;

          h3 {
            margin: 0 0 6px;
            font-size: var(--oj-text-lg);
            font-weight: var(--oj-font-semibold);
            color: var(--oj-text-primary);
          }

          p {
            margin: 0;
            font-size: var(--oj-text-base);
            color: var(--oj-text-secondary);
            line-height: 1.4;
          }
        }
      }
    }
  }

  // CSS动画关键帧
  @keyframes slideInUp {
    from {
      opacity: 0;
      transform: translateY(30px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  @keyframes slideInLeft {
    from {
      opacity: 0;
      transform: translateX(-30px);
    }
    to {
      opacity: 1;
      transform: translateX(0);
    }
  }

  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  @keyframes fadeInRight {
    from {
      opacity: 0;
      transform: translateX(20px);
    }
    to {
      opacity: 1;
      transform: translateX(0);
    }
  }

  @keyframes bounce {
    0%, 20%, 53%, 80%, 100% {
      transform: translate3d(0,0,0);
    }
    40%, 43% {
      transform: translate3d(0, -8px, 0);
    }
    70% {
      transform: translate3d(0, -4px, 0);
    }
    90% {
      transform: translate3d(0, -2px, 0);
    }
  }

  @keyframes pulse {
    0% {
      opacity: 0.6;
      transform: scale(1);
    }
    50% {
      opacity: 1;
      transform: scale(1.2);
    }
    100% {
      opacity: 0.6;
      transform: scale(1);
    }
  }

  // 响应式设计
  @media (max-width: 768px) {
    .featured-topics-section {
      .featured-topics-preview {
        padding: 12px;

        .preview-topic-card {
          margin-bottom: 12px;
        }
      }
    }

    .info-topics-section {
      .el-row {
        grid-template-columns: 1fr;
      }
    }

    .data-analysis-section {
      .data-analysis-container {
        grid-template-columns: 1fr;
      }
    }
  }
}

// 深色模式适配 (使用 html.dark 而非 prefers-color-scheme，与应用主题切换一致)
:global(html.dark) .home-container {
    .welcome-section {
      .welcome-content {
        .welcome-subtitle {
          color: #a1a1a6;
        }
      }

      .user-info-card {
        background: rgba(30, 30, 32, 0.7);
        border-color: rgba(255, 255, 255, 0.05);

        .user-stats {
          .stat-item {
            background-color: rgba(255, 255, 255, 0.05);

            &:hover {
              background-color: rgba(255, 255, 255, 0.1);
            }

            .stat-label {
              color: #a1a1a6;
            }
          }
        }

        .sign-in-area {
          .checkin-info {
            color: #a1a1a6;
          }
        }
      }
    }

    .global-stats-section {
      .global-stats-grid {
        .stat-card {
          background: rgba(30, 30, 32, 0.7);
          border-color: rgba(255, 255, 255, 0.05);

          .stat-info {
            .stat-label {
              color: #a1a1a6;
            }
          }
        }
      }
    }

    .data-analysis-section {
      .problem-type-section {
        .problem-type-container {
          background: rgba(30, 30, 32, 0.7);
          border-color: rgba(255, 255, 255, 0.05);

          .problem-type-cards {
            border-top-color: rgba(255, 255, 255, 0.05);
          }
        }
      }

      .ranking-section {
        .ranking-content {
          background: rgba(30, 30, 32, 0.7);
          border-color: rgba(255, 255, 255, 0.05);

          .rank-item {
            &:not(:last-child) {
              border-bottom-color: rgba(255, 255, 255, 0.05);
            }

            &:hover {
              background-color: rgba(255, 255, 255, 0.05);
            }

            &.rank-top1 {
              background: linear-gradient(135deg, rgba(255, 215, 0, 0.15), rgba(255, 215, 0, 0.05));
            }

            &.rank-top2 {
              background: linear-gradient(135deg, rgba(192, 192, 192, 0.15), rgba(192, 192, 192, 0.05));
            }

            &.rank-top3 {
              background: linear-gradient(135deg, rgba(205, 127, 50, 0.15), rgba(205, 127, 50, 0.05));
            }

            .user-info {
              .user-details {
                .user-name {
                  color: #f5f5f7;
                }

                .user-title {
                  background: rgba(245, 108, 108, 0.2);
                  color: #ff6b6b;
                }
              }
            }

            .user-stats {
              .stat-item {
                &.ac-num {
                  background: linear-gradient(135deg, rgba(52, 199, 89, 0.2), rgba(52, 199, 89, 0.1));
                }

                &.integral {
                  background: linear-gradient(135deg, rgba(255, 149, 0, 0.2), rgba(255, 149, 0, 0.1));
                }
              }
            }
          }
        }
      }
    }

    .info-topics-section {
      .announcement-section {
        .announcement-content {
          background: rgba(30, 30, 32, 0.7);
          border-color: rgba(255, 255, 255, 0.05);

          &::-webkit-scrollbar-track {
            background: rgba(255, 255, 255, 0.02);
          }

          &::-webkit-scrollbar-thumb {
            background-color: rgba(255, 255, 255, 0.1);

            &:hover {
              background-color: rgba(255, 255, 255, 0.2);
            }
          }
        }

        .featured-topics-section {
          .featured-topics-preview {
            background: rgba(30, 30, 32, 0.7);
            border-color: rgba(255, 255, 255, 0.05);

            .topics-scroll-container {
              &::-webkit-scrollbar-track {
                background: rgba(255, 255, 255, 0.02);
              }

              &::-webkit-scrollbar-thumb {
                background-color: rgba(255, 255, 255, 0.1);

                &:hover {
                  background-color: rgba(255, 255, 255, 0.2);
                }
              }
            }

            .preview-topic-card {
              background: rgba(44, 44, 46, 0.7);
              border-color: rgba(255, 255, 255, 0.05);

              &:hover {
                background: rgba(58, 58, 60, 0.9);
              }

              .topic-content {
                .topic-header {
                  .topic-title {
                    color: #f5f5f7;
                  }
                }

                .topic-time {
                  color: #a1a1a6;
                }
              }
            }
          }
        }
      }

      .featured-topics-section {
        .featured-topics-preview {
          background: rgba(30, 30, 32, 0.7);
          border-color: rgba(255, 255, 255, 0.05);

          .topics-scroll-container {
            &::-webkit-scrollbar-track {
              background: rgba(255, 255, 255, 0.02);
            }

            &::-webkit-scrollbar-thumb {
              background-color: rgba(255, 255, 255, 0.1);

              &:hover {
                background-color: rgba(255, 255, 255, 0.2);
              }
            }
          }

          .preview-topic-card {
            background: rgba(44, 44, 46, 0.7);
            border-color: rgba(255, 255, 255, 0.05);

            &:hover {
              background: rgba(58, 58, 60, 0.9);
            }

            .topic-content {
              .topic-header {
                .topic-title {
                  color: #f5f5f7;
                }
              }

              .topic-time {
                color: #a1a1a6;
              }
            }
          }
        }
      }
    }

    .quick-actions-section {
      .quick-actions-grid {
        .quick-action-card {
          background: rgba(30, 30, 32, 0.7);
          border-color: rgba(255, 255, 255, 0.05);

          &:hover {
            background: rgba(44, 44, 46, 0.9);
          }

          .action-content {
            h3 {
              color: #f5f5f7;
            }

            p {
              color: #a1a1a6;
            }
          }
        }
      }
    }
}

// CourseCard 同样修复暗色模式
:deep(.mini-type-enum) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  white-space: nowrap;
  font-size: 11px;

  &::before {
    content: '';
    display: inline-block;
    width: 4px;
    height: 4px;
    background-color: var(--primary-color);
    border-radius: 50%;
    margin-right: 4px;
  }
}

// 公告弹窗样式
.announcement-dialog {
  .announcement-dialog-content {
    padding: 20px;
    min-height: 300px;
    font-size: 16px;
    line-height: 1.6;
  }
}

.action-btn {
  padding: 6px 16px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 16px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  background: linear-gradient(to right, v-bind('themePrimary'), rgba(66, 170, 255, 0.85));
  border: none;
  letter-spacing: 0.3px;
  width: 120px;
  text-align: center;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(to right, rgba(255, 255, 255, 0), rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0));
    transform: translateX(-100%);
    transition: transform 0.6s ease;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
    filter: brightness(1.05);

    &::before {
      transform: translateX(100%);
    }
  }

  &:active {
    transform: translateY(1px) scale(0.98);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
}
</style>
