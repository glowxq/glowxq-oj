<template>
  <div class="user-rank-container">
    <el-card class="rank-card" :body-style="{ padding: '0px' }" v-loading="loading">
      <div class="rank-header">
        <div class="rank-title">
          <el-icon><Trophy /></el-icon>
          <span>解题排行榜</span>
        </div>
        <div class="rank-subtitle">Top {{ rankingList.length }} 解题高手</div>
      </div>

      <div class="rank-list">
        <div
          v-for="(user, index) in rankingList"
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
              <el-avatar :size="40" :src="user.logo || ''" v-if="user.logo">
                {{ user.nickname?.substring(0, 1) || user.username?.substring(0, 1) || user.name?.substring(0, 1) || '用' }}
              </el-avatar>
              <el-avatar :size="40" v-else class="default-avatar">
                {{ user.name?.substring(0, 1) || user.nickname?.substring(0, 1) || user.username?.substring(0, 1) || '用' }}
              </el-avatar>
            </div>
            <div class="user-details">
              <div class="user-name">{{ user.nickname || user.name || user.username }}</div>
              <div class="user-username" v-if="user.username">@{{ user.username }}</div>
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

        <div v-if="rankingList.length === 0" class="empty-rank">
          <el-empty description="暂无排名数据" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { Trophy, Check, Star } from '@element-plus/icons-vue'
import { getUserAcRankingListApi } from '@/api/modules/oj/user/userInfo'
import type { IUserInfo } from '@/api/interface/oj/user/userInfo'

defineOptions({
  name: 'UserRank'
})

// 扩展用户类型，添加缺少的字段
interface UserRankItem extends IUserInfo.Row {
  logo?: string;
  nickname?: string;
  username?: string;
}

const props = defineProps<{
  groupId?: number
}>()

const rankingList = ref<UserRankItem[]>([])
const loading = ref(false)
// 添加数据加载中的标记变量
let isLoadingRankData = false

// 获取排名数据
const fetchRankingData = async () => {
  // 如果已经在加载中，则不再重复请求
  if (isLoadingRankData) return

  try {
    loading.value = true
    // 设置加载标记为true
    isLoadingRankData = true

    const params: { groupId?: number } = {}

    if (props.groupId) {
      params.groupId = props.groupId
    }

    const res = await getUserAcRankingListApi(params)
    if (res.data) {
      rankingList.value = res.data as UserRankItem[]
    }
  } catch (error) {
    console.error('获取排名数据失败:', error)
  } finally {
    loading.value = false
    // 重置加载标记
    isLoadingRankData = false
  }
}

// 监听 groupId 变化，重新获取数据
watch(() => props.groupId, () => {
  fetchRankingData()
})

onMounted(() => {
  fetchRankingData()
})
</script>

<style scoped lang="scss">
.user-rank-container {
  width: 100%;

  .rank-card {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    background-color: #ffffff;
    transition: transform 0.3s ease, box-shadow 0.3s ease;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
    }

    .rank-header {
      background: linear-gradient(135deg, #0071e3, #42aaff);
      color: white;
      padding: 20px;

      .rank-title {
        display: flex;
        align-items: center;
        font-size: 18px;
        font-weight: 600;
        margin-bottom: 8px;

        .el-icon {
          margin-right: 8px;
          font-size: 22px;
        }
      }

      .rank-subtitle {
        font-size: 14px;
        opacity: 0.9;
      }
    }

    .rank-list {
      padding: 8px 0;

      .rank-item {
        display: flex;
        align-items: center;
        padding: 12px 20px;
        transition: background-color 0.2s ease;
        position: relative;

        &:hover {
          background-color: #f9f9f9;
        }

        &:not(:last-child)::after {
          content: '';
          position: absolute;
          left: 20px;
          right: 20px;
          bottom: 0;
          height: 1px;
          background-color: #f0f0f0;
        }

        &.rank-top1 {
          .rank-position .rank-number {
            color: #ff9500;
          }
        }

        &.rank-top2 {
          .rank-position .rank-number {
            color: #8e8e93;
          }
        }

        &.rank-top3 {
          .rank-position .rank-number {
            color: #c46e00;
          }
        }

        .rank-position {
          width: 40px;
          display: flex;
          justify-content: center;

          .rank-number {
            font-size: 18px;
            font-weight: 600;

            .el-icon {
              font-size: 24px;
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
              box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
              transition: transform 0.3s ease, box-shadow 0.3s ease;

              &:hover {
                transform: scale(1.05);
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
              }

              &.default-avatar {
                background: linear-gradient(135deg, #0071e3, #42aaff);
                color: white;
                font-weight: 600;
              }
            }
          }

          .user-details {
            .user-name {
              font-size: 15px;
              font-weight: 500;
              color: #1d1d1f;
              margin-bottom: 4px;
            }

            .user-username {
              font-size: 12px;
              font-weight: 500;
              color: #8e8e93;
            }

            .user-title {
              font-size: 12px;
              font-weight: 500;
            }
          }
        }

        .user-stats {
          display: flex;
          gap: 10px;

          .stat-item {
            min-width: 65px;
            border-radius: 8px;
            padding: 6px 10px;

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
              background-color: rgba(52, 199, 89, 0.1);
              color: #34c759;
            }

            &.integral {
              background-color: rgba(255, 149, 0, 0.1);
              color: #ff9500;
            }
          }
        }
      }

      .empty-rank {
        padding: 40px 0;
      }
    }
  }
}
</style>
