<template>
  <div class="group-detail-container">
    <!-- 班级基本信息卡片 -->
    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <span
            class="group-name"
            :style="{ color: groupInfo.color }"
          >
            <el-icon><School /></el-icon>
            {{ groupInfo.name }}
          </span>
          <div class="header-actions">
            <el-button
              v-auth="'group.update'"
              type="primary"
              :icon="EditPen"
              @click="openAddEdit('编辑班级', groupInfo)"
            >
              编辑
            </el-button>
            <el-button
              type="primary"
              :icon="Back"
              @click="goBack"
            >
              返回
            </el-button>
          </div>
        </div>
      </template>
      <div class="info-content">
        <div class="info-item">
          <span class="label">
            <el-icon><Ticket /></el-icon>
            班级代码:
          </span>
          <span class="value">{{ groupInfo.code || "暂无" }}</span>
        </div>
        <div class="info-item">
          <span class="label">
            <el-icon><Avatar /></el-icon>
            负责人:
          </span>
          <span class="value">{{ groupInfo.principalName || "暂无" }}</span>
        </div>
        <div class="info-item">
          <span class="label">
            <el-icon><ChatDotRound /></el-icon>
            描述:
          </span>
          <span class="value">{{ groupInfo.description || "暂无描述" }}</span>
        </div>
        <div class="info-item">
          <span class="label">
            <el-icon><Switch /></el-icon>
            状态:
          </span>
          <el-tag
            :type="
              groupInfo.enable?.toString() === 'true' || groupInfo.enable === 'T'
                ? 'success'
                : 'danger'
            "
          >
            {{
              groupInfo.enable?.toString() === "true" || groupInfo.enable === "T"
                ? "启用"
                : "禁用"
            }}
          </el-tag>
        </div>
      </div>
    </el-card>

    <!-- 数据统计卡片 -->
    <div class="stats-row">
      <el-card class="stats-card">
        <div class="stats-item">
          <div class="stats-icon user-icon">
            <el-icon><User /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-count">
              {{ groupInfo.userList?.length || 0 }}
            </div>
            <div class="stats-label">
              班级成员
            </div>
          </div>
        </div>
      </el-card>
      <el-card class="stats-card">
        <div class="stats-item">
          <div class="stats-icon tag-icon">
            <el-icon><PriceTag /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-count">
              {{ groupInfo.tagList?.length || 0 }}
            </div>
            <div class="stats-label">
              标签数量
            </div>
          </div>
        </div>
      </el-card>
      <el-card class="stats-card">
        <div class="stats-item">
          <div class="stats-icon work-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-count">
              {{ groupInfo.topicList?.length || 0 }}
            </div>
            <div class="stats-label">
              主题数量
            </div>
          </div>
        </div>
      </el-card>
      <el-card class="stats-card">
        <div class="stats-item">
          <div class="stats-icon course-icon">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-count">
              {{ groupInfo.courseList?.length || 0 }}
            </div>
            <div class="stats-label">
              课程数量
            </div>
          </div>
        </div>
      </el-card>
      <!-- <el-card class="stats-card" >
        <div class="stats-item">
          <div class="stats-icon problem-icon">
            <el-icon><QuestionFilled /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-count">{{ groupInfo.problemList?.length || 0 }}</div>
            <div class="stats-label">题目数量</div>
          </div>
        </div>
      </el-card> -->
      <el-card class="stats-card topic-stats-card">
        <div class="stats-item">
          <div class="stats-icon work-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-label">
              主题分布
            </div>
            <div class="topic-type-stats">
              <div
                class="topic-type-item"
                v-for="type in topicTypeStats"
                :key="type.code"
              >
                <enum-show
                  :enum="TopicType"
                  :code="type.code"
                  font-size="12px"
                  padding="2px 6px"
                  background-color="rgba(var(--el-color-primary-rgb), 0.08)"
                  color="var(--el-color-primary)"
                  border-radius="4px"
                />
                <span class="type-count">{{ type.count }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 选项卡区域 -->
    <el-card class="tabs-card">
      <el-tabs
        v-model="activeTab"
        class="fun-tabs"
      >
        <!-- 成员标签页 -->
        <el-tab-pane
          label="班级成员"
          name="users"
        >
          <div class="user-list">
            <el-empty
              v-if="!groupInfo.userList || groupInfo.userList.length === 0"
              description="暂无成员"
            />
            <div
              v-else
              class="user-grid"
            >
              <user-cart
                v-for="user in groupInfo.userList"
                :key="user.id"
                :user="user"
              />
            </div>
          </div>
        </el-tab-pane>

        <!-- AC排名标签页 -->
        <el-tab-pane
          label="解题排行榜"
          name="rank"
        >
          <div class="rank-list">
            <user-rank :group-id="groupId" />
          </div>
        </el-tab-pane>

        <!-- 标签标签页 -->
        <el-tab-pane
          label="班级标签"
          name="tags"
        >
          <div class="tag-list">
            <el-empty
              v-if="!groupInfo.tagList || groupInfo.tagList.length === 0"
              description="暂无标签"
            />
            <tag-show
              v-else
              :tag-list="groupInfo.tagList"
            />
          </div>
        </el-tab-pane>

        <!-- 主题标签页 -->
        <el-tab-pane
          label="班级主题"
          name="topics"
        >
          <div class="topic-list">
            <!-- 主题类型筛选区域 -->
            <div
              class="topic-filter-section"
              v-if="groupInfo.topicList && groupInfo.topicList.length > 0"
            >
              <div class="filter-header">
                <el-icon><Filter /></el-icon>
                <span>按主题类型筛选：</span>
              </div>
              <div class="filter-options">
                <div
                  class="filter-option"
                  :class="{ active: selectedTopicType === 'all' }"
                  @click="selectedTopicType = 'all'"
                >
                  全部
                </div>
                <div
                  v-for="type in Object.values(TopicType)"
                  :key="type.code"
                  class="filter-option"
                  :class="{ active: selectedTopicType === String(type.code) }"
                  @click="selectedTopicType = String(type.code)"
                >
                  <enum-show
                    :enum="TopicType"
                    :code="type.code"
                    font-size="12px"
                    padding="2px 6px"
                  />
                </div>
              </div>
            </div>

            <el-empty
              v-if="!groupInfo.topicList || groupInfo.topicList.length === 0"
              :image-size="200"
              description="暂无主题"
            >
              <template #description>
                <div class="empty-description">
                  <p>当前班级暂无主题</p>
                  <p class="empty-tip">
                    可以在主题管理中添加主题
                  </p>
                </div>
              </template>
            </el-empty>

            <el-empty
              v-else-if="filteredTopicList.length === 0"
              :image-size="150"
              description="没有符合条件的主题"
            >
              <template #description>
                <div class="empty-description">
                  <p>当前筛选条件下没有找到主题</p>
                  <p class="empty-tip">
                    尝试更换筛选条件或添加新的主题
                  </p>
                </div>
              </template>
            </el-empty>

            <div
              v-else
              class="topic-card-container"
            >
              <topic-card
                v-for="topic in filteredTopicList"
                :key="topic.id"
                :topic="topic"
                :is-selected="false"
                @do-topic="toDoTopic"
                class="topic-card-item"
              />
            </div>
          </div>
        </el-tab-pane>

        <!-- 课程标签页 -->
        <el-tab-pane
          label="班级课程"
          name="courses"
        >
          <div class="course-list">
            <el-empty
              v-if="!groupInfo.courseList || groupInfo.courseList.length === 0"
              :image-size="200"
              description="暂无课程"
            >
              <template #description>
                <div class="empty-description">
                  <p>当前班级暂无课程</p>
                  <p class="empty-tip">
                    可以在课程管理中添加课程
                  </p>
                </div>
              </template>
            </el-empty>

            <div
              v-else
              class="course-card-container"
            >
              <course-card
                v-for="course in groupInfo.courseList"
                :key="course.id"
                :course="course"
                :is-selected="false"
                @select="handleStartCourse"
                class="course-card-item"
              />
            </div>
          </div>
        </el-tab-pane>

        <!-- 代码监控标签页 -->
        <el-tab-pane
          label="代码监控"
          name="codeMonitor"
        >
          <code-monitor-list
            :group-id="groupId"
            @view-detail="handleViewDetail"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <GroupForm ref="groupRef" />
    <PushCoverdCode ref="pushCoverdCodeRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch, onUnmounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  EditPen,
  Back,
  User,
  PriceTag,
  Document,
  Reading,
  Upload,
  School,
  Avatar,
  ChatDotRound,
  Switch,
  Filter,
} from "@element-plus/icons-vue";
import { getGroupDetailApi, updateGroupApi } from "@/api/modules/oj/group/group";
import { getCodeMonitorListByGroupApi } from "@/api/modules/oj/code/codeMonitor";
import type { IGroup } from "@/api/interface/oj/group/group";
import type { ITopic } from "@/api/interface/oj/topic/topic";
import TagShow from "@/components/Common/Meta/Tag/TagShow.vue";
import GroupForm from "@/views/oj/group/group/components/GroupForm.vue";
import UserCart from "@/components/Oj/User/UserCard.vue";
import CodeViewButton from "@/components/Oj/Judge/common/CodeViewButton.vue";
import { CodeMode, CodeMonitorStatus } from "@/enums/oj/code";
import EnumSelect from "@/components/Common/Enum/EnumSelect.vue";
import UserRank from "@/components/Oj/User/UserRank.vue";
import PushCoverdCode from '@/components/Oj/Code/PushCoverdCode.vue'
import EnumShow from "@/components/Common/Enum/EnumShow.vue";
import { TopicType } from "@/enums/oj/topic";
import TopicCard from "@/views/oj/topic/topic/components/TopicCard.vue";
import CourseCard from "@/views/oj/course/components/CourseCard.vue";
import CodeMonitorList from "@/components/Oj/Code/CodeMonitorList.vue";

defineOptions({
  name: "GroupDetail",
});

// 路由
const route = useRoute();
const router = useRouter();

// 班级信息
const groupInfo = reactive<IGroup.Row>({});
const groupId = ref<number>();
const activeTab = ref("users");
const defaultAvatar =
  "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";

// 添加加载标记，防止重复请求
let isLoadingGroupDetail = false;

// 获取班级详情
const getGroupDetail = async () => {
  if (!groupId.value || isLoadingGroupDetail) return;

  // 设置加载标记为true，防止重复请求
  isLoadingGroupDetail = true;

  try {
    const res = await getGroupDetailApi({ id: groupId.value });
    if (res.data) {
      Object.assign(groupInfo, res.data);
    }
  } catch (error) {
    console.error('获取班级详情失败:', error);
  } finally {
    // 请求完成后重置加载标记
    isLoadingGroupDetail = false;
  }
};

// 打开编辑表单
const groupRef = ref<InstanceType<typeof GroupForm>>();
const openAddEdit = async (title: string, row: IGroup.Row) => {
  const params = {
    title,
    row: { ...row },
    api: updateGroupApi,
    getTableList: getGroupDetail,
  };
  groupRef.value?.acceptParams(params);
};

// 返回按钮
const goBack = () => {
  router.push("/oj/group/group");
};

// 打开课程链接
const openCourseUrl = (url: string) => {
  if (url) {
    window.open(url, "_blank");
  }
};

// 添加新的函数
const formatTime = (time: string) => {
  if (!time) return "-";
  const date = new Date(time);
  return date.toLocaleString();
};

const pushCoverdCodeRef = ref<InstanceType<typeof PushCoverdCode>>()

// 处理查看详情
const handleViewDetail = (row: any) => {
  pushCoverdCodeRef.value?.open(row)
}

// 添加toDoTopic方法
const toDoTopic = (row: ITopic.Row) => {
  if (row.id) {
    router.push({
      path: `/oj/topic/doTopic/${row.id}`,
      query: {
        topicName: row.name,
        topicCode: row.code,
      },
    });
  }
};

// 计算各类型主题数量
const topicTypeStats = computed(() => {
  if (!groupInfo.topicList || groupInfo.topicList.length === 0) {
    return Object.values(TopicType).map(type => ({
      code: type.code,
      count: 0
    }));
  }

  // 初始化计数对象
  const counts: Record<string, number> = {};
  Object.values(TopicType).forEach(type => {
    counts[type.code] = 0;
  });

  // 统计各类型主题数量
  groupInfo.topicList.forEach(topic => {
    if (topic.topicType && counts[topic.topicType] !== undefined) {
      counts[topic.topicType]++;
    }
  });

  // 转换为数组形式，便于模板渲染
  return Object.entries(counts).map(([code, count]) => ({
    code,
    count
  }));
});

// 添加筛选类型状态
const selectedTopicType = ref('all');

// 过滤后的主题列表
const filteredTopicList = computed(() => {
  if (!groupInfo.topicList || groupInfo.topicList.length === 0) {
    return [];
  }

  if (selectedTopicType.value === 'all') {
    return groupInfo.topicList;
  }

  return groupInfo.topicList.filter(topic => topic.topicType === selectedTopicType.value);
});

// 处理上课
const handleStartCourse = (course: any) => {
  if (course.url) {
    openCourseUrl(course.url);
  }
};

onMounted(() => {
  const id = Number(route.params.id);
  if (!isNaN(id)) {
    groupId.value = id;
    getGroupDetail();
  }
});
</script>

<style scoped lang="scss">
.group-detail-container {
  padding: 24px;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue",
    sans-serif;
  letter-spacing: -0.01em;
  background-color: #f5f7fa;
  min-height: 100vh;

  // 卡片基础样式
  .card-base {
    border-radius: 16px;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
    overflow: hidden;
    border: none;
    background-color: #ffffff;
    transition: all 0.3s cubic-bezier(0.42, 0, 0.58, 1);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
    }
  }

  // 班级信息卡片
  .info-card {
    @extend .card-base;
    margin-bottom: 24px;

    :deep(.el-card__header) {
      padding: 0;
      border-bottom: none;
    }

    :deep(.el-card__body) {
      padding: 0;
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 20px;
      background: linear-gradient(
        to right,
        rgba(var(--el-color-primary-rgb), 0.03),
        rgba(var(--el-color-primary-rgb), 0.01)
      );
      border-bottom: 1px solid rgba(0, 0, 0, 0.03);

      .group-name {
        display: flex;
        align-items: center;
        font-size: 20px;
        font-weight: 600;
        letter-spacing: -0.02em;

        .el-icon {
          margin-right: 10px;
          font-size: 22px;
        }
      }

      .header-actions {
        display: flex;
        gap: 10px;

        .el-button {
          border-radius: 8px;
          font-weight: 500;
          letter-spacing: -0.01em;
          height: 36px;
          padding: 0 16px;

          &:hover {
            transform: translateY(-1px);
          }
        }
      }
    }

    .info-content {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 16px;
      padding: 20px;

      .info-item {
        display: flex;
        align-items: center;
        background-color: rgba(var(--el-color-primary-rgb), 0.02);
        padding: 14px 16px;
        border-radius: 12px;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.03);
        transition: all 0.3s cubic-bezier(0.42, 0, 0.58, 1);
        border: 1px solid rgba(var(--el-color-primary-rgb), 0.04);

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
          background-color: rgba(var(--el-color-primary-rgb), 0.03);
        }

        .label {
          display: flex;
          align-items: center;
          font-weight: 600;
          margin-right: 12px;
          min-width: 90px;
          color: var(--el-color-primary);
          font-size: 14px;

          .el-icon {
            margin-right: 8px;
            font-size: 18px;
            color: var(--el-color-primary);
          }
        }

        .value {
          color: #1d1d1f;
          flex: 1;
          word-break: break-word;
          font-size: 14px;
        }

        .el-tag {
          border-radius: 6px;
          padding: 0 10px;
          height: 26px;
          font-weight: 500;
          letter-spacing: -0.01em;
          box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
          border: none;
        }
      }
    }
  }

  // 数据统计卡片
  .stats-row {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(210px, 1fr));
    gap: 20px;
    margin-bottom: 24px;

    .stats-card {
      @extend .card-base;

      :deep(.el-card__body) {
        padding: 0;
      }

      .stats-item {
        display: flex;
        align-items: center;
        padding: 20px;

        .stats-icon {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 56px;
          height: 56px;
          border-radius: 16px;
          margin-right: 16px;
          transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
          position: relative;
          overflow: hidden;
          box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);

          &::after {
            content: "";
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(255, 255, 255, 0.2);
            transform: translateY(100%);
            transition: all 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
          }

          &:hover::after {
            transform: translateY(0);
          }

          .el-icon {
            font-size: 24px;
            color: white;
            position: relative;
            z-index: 2;
          }

          &.user-icon {
            background: linear-gradient(
              135deg,
              var(--el-color-primary),
              color-mix(in srgb, var(--el-color-primary) 70%, white)
            );
          }

          &.tag-icon {
            background: linear-gradient(135deg, #34c759, #7ee28f);
          }

          &.work-icon {
            background: linear-gradient(135deg, #ff9500, #ffbd52);
          }

          &.course-icon {
            background: linear-gradient(135deg, #5856d6, #7e7df5);
          }

          &.problem-icon {
            background: linear-gradient(135deg, #ff3b30, #ff7b74);
          }
        }

        .stats-info {
          .stats-count {
            font-size: 28px;
            font-weight: 600;
            line-height: 1;
            margin-bottom: 6px;
            color: #1d1d1f;
          }

          .stats-label {
            color: #86868b;
            font-size: 14px;
            font-weight: 500;
          }
        }
      }
    }
  }

  // 选项卡样式
  .tabs-card {
    @extend .card-base;

    :deep(.el-card__body) {
      padding: 0;
    }

    .fun-tabs {
      :deep(.el-tabs__header) {
        margin: 0;
        background: linear-gradient(
          to right,
          rgba(var(--el-color-primary-rgb), 0.03),
          rgba(var(--el-color-primary-rgb), 0.01)
        );
        padding: 0 16px;

        .el-tabs__nav-wrap::after {
          display: none;
        }

        .el-tabs__nav {
          border: none;
        }

        .el-tabs__item {
          height: 50px;
          line-height: 50px;
          font-size: 15px;
          font-weight: 600;
          color: #86868b;
          transition: all 0.3s cubic-bezier(0.42, 0, 0.58, 1);
          padding: 0 20px;

          &.is-active {
            color: var(--el-color-primary);
          }

          &:hover {
            color: var(--el-color-primary);
          }
        }

        .el-tabs__active-bar {
          height: 3px;
          border-radius: 3px;
          background-color: var(--el-color-primary);
        }
      }

      :deep(.el-tabs__content) {
        padding: 24px;
      }
    }

    // 用户网格
    .user-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
      gap: 20px;

      .user-card {
        padding: 16px;
        border-radius: 14px;
        overflow: hidden;
        transition: all 0.3s cubic-bezier(0.42, 0, 0.58, 1);
        height: auto;
        width: 100%;
        border: none;
        box-shadow: 0 1px 5px rgba(0, 0, 0, 0.05);
        background-color: #ffffff;

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
        }

        .user-content {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          text-align: center;

          .user-avatar {
            margin-bottom: 14px;

            .el-avatar {
              border: 2px solid #fff;
              box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
              transition: all 0.3s cubic-bezier(0.42, 0, 0.58, 1);

              &:hover {
                transform: scale(1.05);
                box-shadow: 0 6px 14px rgba(0, 0, 0, 0.15);
              }

              &.default-avatar {
                background: linear-gradient(
                  45deg,
                  var(--el-color-primary),
                  color-mix(in srgb, var(--el-color-primary) 70%, white)
                );
                color: white;
                font-weight: 600;
              }
            }
          }

          .user-info {
            width: 100%;
            display: flex;
            flex-direction: column;
            align-items: center;

            .user-name {
              font-weight: 600;
              font-size: 15px;
              margin-bottom: 4px;
              color: #1d1d1f;
              width: 100%;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }

            .user-username {
              color: #86868b;
              font-size: 13px;
              margin-bottom: 6px;
              width: 100%;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }

            .user-title {
              color: var(--el-color-primary);
              font-size: 13px;
              font-weight: 600;
              margin-bottom: 10px;
              width: 100%;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
              display: flex;
              align-items: center;
              justify-content: center;

              .el-icon {
                margin-right: 4px;
                font-size: 14px;
              }
            }

            .user-stats {
              display: flex;
              flex-wrap: wrap;
              justify-content: center;
              margin-top: 5px;
              width: 100%;
              gap: 8px;

              .stat-item {
                flex: 1 0 45%;
                border-radius: 8px;
                padding: 6px 4px;
                min-width: 65px;
                display: flex;
                align-items: center;
                justify-content: center;

                .stat-icon-box {
                  display: flex;
                  align-items: center;
                  justify-content: center;

                  .el-icon {
                    margin-right: 4px;
                    font-size: 14px;
                  }

                  span {
                    font-weight: 600;
                    font-size: 13px;
                  }
                }

                &.ac-num {
                  background-color: rgba(52, 199, 89, 0.08);
                  color: #34c759;
                  border: none;
                }

                &.submit-num {
                  background-color: rgba(255, 149, 0, 0.08);
                  color: #ff9500;
                  border: none;
                }

                &.integral {
                  background-color: rgba(var(--el-color-primary-rgb), 0.08);
                  color: var(--el-color-primary);
                  border: none;
                }

                &.sign-day {
                  background-color: rgba(255, 59, 48, 0.08);
                  color: #ff3b30;
                  border: none;
                }
              }
            }
          }
        }
      }
    }

    // 表格样式优化
    :deep(.el-table) {
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 1px 5px rgba(0, 0, 0, 0.03);

      th {
        background: linear-gradient(
          to right,
          rgba(var(--el-color-primary-rgb), 0.03),
          rgba(var(--el-color-primary-rgb), 0.01)
        );
        color: #1d1d1f;
        font-weight: 600;
        padding: 14px 0;
        font-size: 14px;
      }

      td {
        padding: 12px 0;
        font-size: 14px;
      }

      .el-button {
        border-radius: 6px;
        font-weight: 500;

        &.el-button--primary {
          color: var(--el-color-primary);
        }

        .el-icon {
          margin-right: 4px;
        }
      }
    }

    // 标签展示
    :deep(.tag-list) {
      .tag-show {
        .el-tag {
          margin: 0 8px 8px 0;
          border-radius: 6px;
          padding: 0 10px;
          height: 26px;
          font-weight: 500;
          letter-spacing: -0.01em;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
          transition: all 0.2s cubic-bezier(0.42, 0, 0.58, 1);
          border: none;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 3px 6px rgba(0, 0, 0, 0.1);
          }

          &.active {
            background-color: var(--el-color-primary);
            color: white;
            font-weight: 500;
            box-shadow: 0 2px 6px rgba(var(--el-color-primary-rgb), 0.3);

            :deep(.enum-show-value) {
              background-color: transparent !important;
              color: white !important;
            }
          }
        }
      }
    }

    // 时间范围显示
    :deep(.time-range) {
      display: flex;
      flex-direction: row;
      align-items: center;
      justify-content: center;
      font-size: 13px;

      .time-item {
        padding: 4px 10px;
        border-radius: 6px;
        display: flex;
        align-items: center;
        background-color: rgba(var(--el-color-primary-rgb), 0.05);
        border: none;

        i {
          margin-right: 4px;
          font-size: 14px;
        }
      }

      .start-time {
        color: var(--el-color-primary);
        border-left: 3px solid var(--el-color-primary);
      }

      .end-time {
        color: #34c759;
        border-left: 3px solid #34c759;
      }

      .time-separator {
        margin: 0 8px;
        color: #86868b;
        font-size: 14px;
        font-weight: 500;
      }
    }

    // 状态标签
    :deep(.time-status) {
      .el-tag {
        border-radius: 6px;
        font-weight: 500;
        letter-spacing: -0.01em;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
        border: none;
      }
    }
  }

  .course-list {
    .course-card-container {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;
      margin-top: 16px;

      .course-card-item {
        flex: 0 0 calc(25% - 15px);
        max-width: 320px;

        @media screen and (max-width: 1600px) {
          flex: 0 0 calc(33.33% - 14px);
        }

        @media screen and (max-width: 1200px) {
          flex: 0 0 calc(50% - 10px);
        }

        @media screen and (max-width: 768px) {
          flex: 0 0 100%;
        }
      }
    }

    /* 确保课程卡片样式符合Apple设计 */
    :deep(.course-card) {
      position: relative;
      height: 100%;
      width: 100%;
      transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
      border-radius: 16px;
      overflow: hidden;
      background-color: #ffffff;
      border: 1px solid rgba(0, 0, 0, 0.05);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
      display: flex;
      flex-direction: column;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
      }

      .card-header {
        padding: 16px 20px;
        border-bottom: 1px solid rgba(0, 0, 0, 0.05);

        .card-title {
          font-size: 18px;
          font-weight: 600;
          letter-spacing: -0.02em;
          color: #1d1d1f;
          font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'Helvetica Neue', sans-serif;
          max-width: 100%;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      .card-content {
        padding: 16px 20px;
        flex: 1;

        .course-description {
          font-size: 14px;
          color: #86868b;
          margin-bottom: 16px;
          line-height: 1.4;
          display: -webkit-box;
          -webkit-line-clamp: 3;
          -webkit-box-orient: vertical;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }

      .card-footer {
        padding: 12px 20px;
        border-top: 1px solid rgba(0, 0, 0, 0.05);
        display: flex;
        align-items: center;
        justify-content: space-between;

        .course-button {
          border-radius: 18px;
          padding: 6px 16px;
          font-size: 14px;
          font-weight: 500;
          border: none;
          background: var(--el-color-primary);
          color: white;
          box-shadow: 0 2px 6px rgba(var(--el-color-primary-rgb), 0.2);
          transition: all 0.2s ease;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 10px rgba(var(--el-color-primary-rgb), 0.3);
            filter: brightness(1.05);
          }
        }
      }
    }
  }

  // 主题列表样式
  .topic-list {
    .topic-filter-section {
      background-color: rgba(var(--el-color-primary-rgb), 0.03);
      border-radius: 10px;
      padding: 12px 16px;
      margin-bottom: 16px;

      .filter-header {
        display: flex;
        align-items: center;
        margin-bottom: 10px;
        font-weight: 600;
        color: #1d1d1f;

        .el-icon {
          margin-right: 6px;
          color: var(--el-color-primary);
        }
      }

      .filter-options {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;

        .filter-option {
          cursor: pointer;
          padding: 4px 10px;
          border-radius: 6px;
          transition: all 0.3s ease;
          background-color: white;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
          }

          &.active {
            background-color: var(--el-color-primary);
            color: white;
            font-weight: 500;
            box-shadow: 0 2px 6px rgba(var(--el-color-primary-rgb), 0.3);

            :deep(.enum-show-value) {
              background-color: transparent !important;
              color: white !important;
            }
          }
        }
      }
    }

    .topic-card-container {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;
      margin-top: 6px;

      .topic-card-item {
        flex: 0 0 calc(25% - 15px);
        max-width: 320px;

        @media screen and (max-width: 1600px) {
          flex: 0 0 calc(33.33% - 14px);
        }

        @media screen and (max-width: 1200px) {
          flex: 0 0 calc(50% - 10px);
        }

        @media screen and (max-width: 768px) {
          flex: 0 0 100%;
        }
      }
    }

    .empty-description {
      text-align: center;

      p {
        margin: 8px 0;
        color: #606266;
      }

      .empty-tip {
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .stats-card.topic-stats-card {
    .stats-info {
      .topic-type-stats {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        margin-top: 8px;

        .topic-type-item {
          display: flex;
          align-items: center;
          gap: 4px;

          .type-count {
            font-size: 12px;
            font-weight: 600;
            color: var(--el-color-primary);
            background-color: rgba(var(--el-color-primary-rgb), 0.05);
            border-radius: 50%;
            width: 20px;
            height: 20px;
            display: flex;
            align-items: center;
            justify-content: center;
          }
        }
      }
    }
  }

  .stats-card.topic-stats-card {
    grid-column: span 2;

    .stats-item {
      display: flex;
      flex-direction: column;
      align-items: flex-start;

      .stats-icon {
        margin-bottom: 12px;
        margin-right: 0;
      }

      .stats-info {
        width: 100%;

        .stats-label {
          font-size: 16px;
          font-weight: 600;
          margin-bottom: 12px;
          color: #1d1d1f;
        }

        .topic-type-stats {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
          gap: 12px;

          .topic-type-item {
            background-color: rgba(var(--el-color-primary-rgb), 0.03);
            padding: 8px;
            border-radius: 8px;
            display: flex;
            justify-content: space-between;
            transition: all 0.3s ease;

            &:hover {
              background-color: rgba(var(--el-color-primary-rgb), 0.06);
              transform: translateY(-2px);
              box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
            }

            .type-count {
              font-size: 14px;
              font-weight: 600;
              color: white;
              background-color: var(--el-color-primary);
              border-radius: 50%;
              width: 24px;
              height: 24px;
              display: flex;
              align-items: center;
              justify-content: center;
              box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            }
          }
        }
      }
    }
  }
}

// 媒体查询，确保在小屏幕上也有良好的响应式表现
@media screen and (max-width: 768px) {
  .group-detail-container {
    padding: 16px;

    .stats-row {
      grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
      gap: 16px;
    }

    .tabs-card .user-grid {
      grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    }

    .info-card .info-content {
      grid-template-columns: 1fr;
    }
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .group-detail-container {
    background-color: #1a1a1a;

    .card-base, .el-card {
      background-color: #242424;
      border-color: rgba(255, 255, 255, 0.05);

      .el-card__header {
        background: linear-gradient(
          to right,
          rgba(var(--el-color-primary-rgb), 0.1),
          rgba(var(--el-color-primary-rgb), 0.05)
        );
        border-color: rgba(255, 255, 255, 0.05);
      }
    }

    .info-content .info-item {
      background-color: rgba(255, 255, 255, 0.05);
      border-color: rgba(255, 255, 255, 0.05);

      .value {
        color: #e5e5e7;
      }
    }

    .course-list {
      :deep(.course-card) {
        background-color: #242424;
        border-color: rgba(255, 255, 255, 0.05);

        &:hover {
          box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
        }

        .card-header {
          border-color: rgba(255, 255, 255, 0.05);

          .card-title {
            color: #f5f5f7;
          }
        }

        .card-content {
          .course-description {
            color: #a1a1a6;
          }
        }

        .card-footer {
          border-color: rgba(255, 255, 255, 0.05);
        }
      }
    }

    .topic-list {
      .topic-filter-section {
        background-color: rgba(255, 255, 255, 0.05);

        .filter-header {
          color: #f5f5f7;
        }

        .filter-options {
          .filter-option {
            background-color: rgba(255, 255, 255, 0.05);
            color: #f5f5f7;

            &:hover {
              background-color: rgba(255, 255, 255, 0.1);
            }
          }
        }
      }
    }
  }
}
</style>
