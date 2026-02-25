<template>
  <el-card class="user-card">
    <div class="user-content">
      <div class="user-avatar">
        <el-avatar :size="50" :src="user.logo || ''" v-if="user.logo">
          {{ user.nickname?.substring(0, 1) || user.username?.substring(0, 1) || user.name?.substring(0, 1) || '用' }}
        </el-avatar>
        <el-avatar :size="50" v-else class="default-avatar">
          {{ user.name?.substring(0, 1) || user.nickname?.substring(0, 1) || user.username?.substring(0, 1) || '用' }}
        </el-avatar>
      </div>
      <div class="user-info">
        <div class="user-name">{{ user.nickname || user.name || user.username }}</div>
        <div class="user-username">@{{ user.username }}</div>
        <div class="user-title" v-if="user.title" :style="{ color: user.color || '#F56C6C' }">
          <el-icon><Trophy /></el-icon> {{ user.title }}
        </div>
        <div class="user-stats">
          <div v-if="user.acNum !== undefined" class="stat-item ac-num">
            <el-tooltip content="AC数量" placement="top">
              <div class="stat-icon-box">
                <el-icon><Check /></el-icon>
                <span>{{ user.acNum }}</span>
              </div>
            </el-tooltip>
          </div>
          <div v-if="user.submitNum !== undefined" class="stat-item submit-num">
            <el-tooltip content="提交数量" placement="top">
              <div class="stat-icon-box">
                <el-icon><Upload /></el-icon>
                <span>{{ user.submitNum }}</span>
              </div>
            </el-tooltip>
          </div>
          <div v-if="user.integral !== undefined" class="stat-item integral">
            <el-tooltip content="积分" placement="top">
              <div class="stat-icon-box">
                <el-icon><Star /></el-icon>
                <span>{{ user.integral }}</span>
              </div>
            </el-tooltip>
          </div>
          <div v-if="user.continuousSignDay !== undefined" class="stat-item sign-day">
            <el-tooltip content="连续签到" placement="top">
              <div class="stat-icon-box">
                <el-icon><Calendar /></el-icon>
                <span>{{ user.continuousSignDay }}天</span>
              </div>
            </el-tooltip>
          </div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { Trophy, Check, Upload, Star, Calendar } from '@element-plus/icons-vue'

defineOptions({
  name: 'UserCart'
})

interface UserProps {
  id?: number;
  username?: string;
  name?: string;
  nickname?: string;
  logo?: string;
  title?: string;
  color?: string;
  acNum?: number;
  submitNum?: number;
  integral?: number;
  continuousSignDay?: number;
}

const props = defineProps<{
  user: UserProps
}>()
</script>

<style scoped lang="scss">
.user-card {
  padding: 12px;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  height: auto;
  width: 100%;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  background-color: #fafafa;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
  }
  
  .user-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    
    .user-avatar {
      margin-bottom: 12px;
      
      .el-avatar {
        border: 2px solid #fff;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
        
        &:hover {
          transform: scale(1.05);
        }
        
        &.default-avatar {
          background: linear-gradient(45deg, #409EFF, #67C23A);
          color: white;
          font-weight: bold;
        }
      }
    }
    
    .user-info {
      width: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      
      .user-name {
        font-weight: bold;
        font-size: 15px;
        margin-bottom: 5px;
        color: #303133;
        width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .user-username {
        color: #909399;
        font-size: 12px;
        margin-bottom: 5px;
        width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .user-title {
        color: #F56C6C;
        font-size: 13px;
        font-weight: 600;
        margin-bottom: 8px;
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
          padding: 5px;
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
            background-color: rgba(103, 194, 58, 0.1);
            color: #67C23A;
            border: 1px solid rgba(103, 194, 58, 0.2);
          }
          
          &.submit-num {
            background-color: rgba(230, 162, 60, 0.1);
            color: #E6A23C;
            border: 1px solid rgba(230, 162, 60, 0.2);
          }
          
          &.integral {
            background-color: rgba(64, 158, 255, 0.1);
            color: #409EFF;
            border: 1px solid rgba(64, 158, 255, 0.2);
          }
          
          &.sign-day {
            background-color: rgba(245, 108, 108, 0.1);
            color: #F56C6C;
            border: 1px solid rgba(245, 108, 108, 0.2);
          }
        }
      }
    }
  }
}
</style>
