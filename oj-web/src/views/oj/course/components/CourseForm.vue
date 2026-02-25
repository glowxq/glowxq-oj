<template>
  <el-dialog
    v-model="visible"
    :title="`${paramsProps.title}`"
    :destroy-on-close="true"
    width="780px"
    draggable
  >
    <el-form
      ref="ruleFormRef"
      label-width="140px"
      label-suffix=" :"
      :rules="rules"
      :model="paramsProps.row"
      @submit.enter.prevent="handleSubmit"
    >
      <el-form-item label="课程名" prop="name">
        <el-input
          v-model="paramsProps.row.name"
          placeholder="请填写课程名"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="课程内容" prop="content">
        <div class="markdown-wrapper">
          <Markdown
            v-model="paramsProps.row.content"
            :height="300"
            placeholder="请使用Markdown编写课程内容..."
          />
        </div>
      </el-form-item>
      <el-form-item label="课程连接" prop="url">
        <el-input
          v-model="paramsProps.row.url"
          placeholder="请填写课程连接"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="授课老师" prop="teacherUserId">
        <UserSelect
          v-model="paramsProps.row.teacherUserId"
          placeholder="请选择授课老师"
          clearable
          @change="(val, user) => handleTeacherChange(val, user)"
        />
      </el-form-item>
      <el-form-item label="课程状态">
        <div class="status-display">
          <el-tag
            :type="getCourseStatusType()"
            effect="dark"
            class="status-tag"
          >
            {{ getCourseStatusText() }}
          </el-tag>
          <span class="status-hint">（根据上课时间和下课时间自动计算）</span>
        </div>
      </el-form-item>
      <el-form-item label="启用状态" prop="enable">
        <el-switch v-model="paramsProps.row.enable" ></el-switch>
      </el-form-item>
      <el-form-item label="上课时间" prop="startTime">
        <el-date-picker clearable
          v-model="paramsProps.row.startTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="请选择上课时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="下课时间" prop="endTime">
        <el-date-picker clearable
          v-model="paramsProps.row.endTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="请选择下课时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="班级" prop="groupIds">
        <GroupSelect
          v-model="paramsProps.row.groupIds"
          multiple
          placeholder="请选择班级"
          clearable
        />
        <div v-if="paramsProps.row.groups && paramsProps.row.groups.length" class="selected-groups">
          <div class="selected-groups-title">已选班级：</div>
          <div class="selected-groups-tags">
            <el-tag
              v-for="group in paramsProps.row.groups"
              :key="group.id"
              :style="{
                backgroundColor: group.color || '#e9e9eb',
                color: group.textColor || '#606266',
                borderColor: group.color || '#e9e9eb'
              }"
              class="group-tag-form"
              size="small"
            >
              {{ group.code ? `${group.code} - ` : '' }}{{ group.name }}
              {{ group.principalName ? `(负责人: ${group.principalName})` : '' }}
            </el-tag>
          </div>
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false"> 取消</el-button>
      <el-button type="warning" @click="fillTestData" plain>🧪 一键测试</el-button>
      <el-button type="primary" @click="handleSubmit"> 确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'
import { useOptionsStore } from '@/stores/modules/options'
import GroupSelect from '@/components/Oj/Group/GroupSelect.vue'
import UserSelect from '@/components/Common/User/UserSelect.vue'
import Markdown from '@/components/Common/Markdown/index.vue'

defineOptions({
    name: 'CourseForm'
})

const optionsStore = useOptionsStore()
const rules = reactive({
  name: [{ required: true, message: '请填写课程名' }],
  content: [{ required: true, message: '请填写课程内容' }],
  url: [{ required: true, message: '请填写课程连接' }],
  teacherUserId: [{ required: true, message: '请选择授课老师' }],
  enable: [{ required: true, message: '请设置启用状态' }],
  startTime: [{ required: true, message: '请填写上课时间' }],
  endTime: [{ required: true, message: '请填写下课时间' }],
  groupIds: [{ required: true, message: '请选择班级', type: 'array' }],
  createTime: [{ required: true, message: '请填写创建时间' }],
  updateTime: [{ required: true, message: '请填写更新时间' }],
  delFlag: [{ required: true, message: '请填写是否删除' }],
})

// 处理教师选择变更
const handleTeacherChange = (val: number, user: any) => {
  if (user) {
    paramsProps.value.row.teacherName = user.name
    paramsProps.value.row.teacherPhone = user.phone
  } else {
    paramsProps.value.row.teacherName = undefined
    paramsProps.value.row.teacherPhone = undefined
  }
}

const visible = ref(false)
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
})

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  // 如果是编辑模式，且有groups数据，处理groups和groupIds的关系
  if (params.row && params.row.groups && Array.isArray(params.row.groups)) {
    // 确保groupIds存在并与groups对应
    if (!params.row.groupIds || !Array.isArray(params.row.groupIds)) {
      params.row.groupIds = params.row.groups.map(group => group.id).filter(Boolean);
    }
  }

  paramsProps.value = params
  visible.value = true
}

// 提交数据（新增/编辑）
const ruleFormRef = ref<InstanceType<typeof ElForm>>()
const handleSubmit = () => {
  ruleFormRef.value!.validate(async (valid) => {
    if (!valid) return
    try {
      await paramsProps.value.api!(paramsProps.value.row)
      ElMessage.success({ message: `${paramsProps.value.title}成功！` })
      paramsProps.value.getTableList!()
      visible.value = false
    } catch (error) {
      console.log(error)
    }
  })
}

// 获取课程状态类型
const getCourseStatusType = () => {
  const now = new Date();
  const startTime = paramsProps.value.row.startTime ? new Date(paramsProps.value.row.startTime) : null;
  const endTime = paramsProps.value.row.endTime ? new Date(paramsProps.value.row.endTime) : null;

  if (!startTime || !endTime) return 'info';

  if (now < startTime) {
    return 'info';
  } else if (now >= startTime && now <= endTime) {
    return 'success';
  } else {
    return 'danger';
  }
};

// 获取课程状态文本
const getCourseStatusText = () => {
  const now = new Date();
  const startTime = paramsProps.value.row.startTime ? new Date(paramsProps.value.row.startTime) : null;
  const endTime = paramsProps.value.row.endTime ? new Date(paramsProps.value.row.endTime) : null;

  if (!startTime || !endTime) return '未设置时间';

  if (now < startTime) {
    return '未开始';
  } else if (now >= startTime && now <= endTime) {
    return '进行中';
  } else {
    return '已结束';
  }
};

// 生成随机测试数据
const generateTestData = () => {
  const randomId = Math.floor(Math.random() * 10000);
  const now = new Date();
  const startTime = new Date(now.getTime() + 24 * 60 * 60 * 1000); // 明天开始
  const endTime = new Date(startTime.getTime() + 3 * 60 * 60 * 1000); // 3小时后结束

  return {
    name: `测试课程${randomId}`,
    content: `# 测试课程${randomId}

## 课程介绍
这是一个自动生成的测试课程，包含以下内容：

### 学习目标
- 掌握基础概念
- 理解核心原理
- 实践操作技能

### 课程大纲
1. **第一章：基础知识**
   - 概念介绍
   - 基本原理
   - 实例分析

2. **第二章：进阶内容**
   - 深入理解
   - 高级技巧
   - 项目实战

3. **第三章：总结提升**
   - 知识整合
   - 经验分享
   - 课后练习

### 学习资源
- 📖 课程讲义
- 🎥 视频教程
- 💻 代码示例
- 📝 课后作业

> **注意**：这是测试数据，请在实际使用前修改内容。`,
    url: `https://example.com/course/${randomId}`,
    enable: true,
    startTime: startTime.toISOString().slice(0, 19).replace('T', ' '),
    endTime: endTime.toISOString().slice(0, 19).replace('T', ' '),
  };
};

// 一键填充测试数据
const fillTestData = () => {
  const testData = generateTestData();
  Object.assign(paramsProps.value.row, testData);
  ElMessage.success('测试数据已填充！');
};

defineExpose({
  acceptParams
})
</script>

<style scoped lang="scss">
.status-display {
  display: flex;
  align-items: center;

  .status-tag {
    padding: 6px 12px;
    font-weight: bold;
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .status-hint {
    margin-left: 10px;
    color: #909399;
    font-size: 13px;
    font-style: italic;
  }
}

.selected-groups {
  margin-top: 10px;

  .selected-groups-title {
    font-size: 12px;
    color: #606266;
    margin-bottom: 5px;
  }

  .selected-groups-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 5px;

    .group-tag-form {
      margin: 2px;
      font-size: 12px;
      padding: 4px 8px;
      border-radius: 4px;
    }
  }
}

.markdown-wrapper {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);

  :deep(.markdown-editor-container) {
    border: 1px solid var(--el-border-color-light);
    border-radius: 8px;
    font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', sans-serif;
  }
}

:deep(.el-dialog) {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1), 0 6px 12px rgba(0, 0, 0, 0.08);

  .el-dialog__header {
    padding: 20px 24px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
    margin-right: 0;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: #1d1d1f;
      font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', sans-serif;
    }
  }

  .el-dialog__body {
    padding: 24px;
  }

  .el-dialog__footer {
    padding: 16px 24px;
    border-top: 1px solid rgba(0, 0, 0, 0.05);
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  :deep(.el-dialog) {
    background-color: #1e1e1e;
    border: 1px solid rgba(255, 255, 255, 0.08);

    .el-dialog__header {
      border-bottom-color: rgba(255, 255, 255, 0.08);

      .el-dialog__title {
        color: #f5f5f7;
      }
    }

    .el-dialog__footer {
      border-top-color: rgba(255, 255, 255, 0.08);
    }
  }

  .markdown-wrapper {
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);

    :deep(.markdown-editor-container) {
      border-color: rgba(255, 255, 255, 0.08);
    }
  }
}

// 测试按钮样式
:deep(.el-button--warning.is-plain) {
  border-color: var(--el-color-warning);
  color: var(--el-color-warning);

  &:hover {
    background-color: var(--el-color-warning);
    border-color: var(--el-color-warning);
    color: white;
  }
}
</style>
