<template>
  <div class="course-container">
    <!-- 搜索区域 -->
    <div class="search-area" :class="{ 'is-collapsed': isSearchCollapsed }">
      <div class="search-header" @click="toggleSearch">
        <div class="search-title">
          <el-icon class="search-icon"><Search /></el-icon>
          <span>搜索课程</span>
        </div>
        <el-icon class="collapse-icon" :class="{ 'is-rotate': !isSearchCollapsed }">
          <arrow-down />
        </el-icon>
      </div>

      <div class="search-content" v-show="!isSearchCollapsed">
        <el-form :model="searchForm" ref="searchFormRef" :inline="true" class="search-form">
          <el-form-item label="课程名">
            <el-input v-model="searchForm.name" placeholder="请输入课程名" clearable class="cute-input" />
          </el-form-item>

          <el-form-item label="课程内容">
            <el-input v-model="searchForm.content" placeholder="请输入课程内容" clearable class="cute-input" />
          </el-form-item>

          <el-form-item label="课程链接">
            <el-input v-model="searchForm.url" placeholder="请输入课程链接" clearable class="cute-input" />
          </el-form-item>

          <el-form-item label="授课老师" class="special-form-item">
            <UserSelect
              v-model="searchForm.teacherUserId"
              placeholder="请选择授课老师"
              clearable
              @change="handleTeacherChange"
              class="cute-select teacher-select"
            />
          </el-form-item>

          <el-form-item label="班级" class="special-form-item">
            <GroupSelect
              v-model="searchForm.groupIds"
              multiple
              placeholder="请选择班级"
              clearable
              class="cute-select group-select"
            />
          </el-form-item>

          <el-form-item label="启用状态">
            <el-switch v-model="searchForm.enable" class="cute-switch" />
          </el-form-item>

          <el-form-item label="上课时间">
            <el-date-picker
              v-model="searchForm.startTime"
              type="datetimerange"
              value-format="YYYY-MM-DD HH:mm:ss"
              placeholder="选择上课时间范围"
              clearable
              class="cute-date-picker"
            />
          </el-form-item>

          <el-form-item label="下课时间">
            <el-date-picker
              v-model="searchForm.endTime"
              type="datetimerange"
              value-format="YYYY-MM-DD HH:mm:ss"
              placeholder="选择下课时间范围"
              clearable
              class="cute-date-picker"
            />
          </el-form-item>
        </el-form>

        <div class="search-buttons">
          <el-button type="primary" @click="searchCourses" :icon="Search">
            搜索
          </el-button>
          <el-button @click="resetSearch" :icon="RefreshRight">
            重置
          </el-button>
        </div>
      </div>
    </div>

    <!-- 操作按钮区域 -->
    <div class="operation-btns">
      <el-button type="primary"
        v-show="isAdminOrCommon"
        v-auth="'course.create'"
        :icon="CirclePlus"
        @click="openAddEdit('新增课程')"
        class="cute-button create-button"
      >
        <span class="button-text">新增课程</span>
        <span class="button-effect"></span>
      </el-button>

      <el-button
        v-show="isAdminOrCommon"
        v-auth="'course.remove'"
        type="danger"
        :icon="Delete"
        plain
        :disabled="selectedIds.length === 0"
        @click="batchDelete(selectedIds)"
        class="cute-button delete-button"
      >
        <span class="button-text">批量删除</span>
        <span class="button-effect"></span>
      </el-button>

      <el-button
        v-auth="'course.update'"
        type="success"
        :icon="VideoPlay"
        plain
        :disabled="selectedIds.length !== 1"
        @click="startCourse"
        class="cute-button start-button"
      >
        <span class="button-text">开始上课</span>
        <span class="button-effect"></span>
      </el-button>

      <el-button
        type="info"
        :icon="selectedAll ? CircleCheck : MoreFilled"
        plain
        @click="toggleSelectAll"
        class="cute-button select-button"
      >
        <span class="button-text">{{ selectedAll ? '取消全选' : '全选' }}</span>
        <span class="button-effect"></span>
      </el-button>

      <el-button
        v-show="isAdminOrCommon"
        v-auth="'course.import'"
        type="primary"
        :icon="Upload"
        plain
        @click="importData"
        class="cute-button import-button"
      >
        <span class="button-text">导入</span>
        <span class="button-effect"></span>
      </el-button>

      <el-button
        v-show="isAdminOrCommon"
        v-auth="'course.export'"
        type="primary"
        :icon="Download"
        plain
        @click="downloadFile"
        class="cute-button export-button"
      >
        <span class="button-text">导出</span>
        <span class="button-effect"></span>
      </el-button>
    </div>

    <!-- 分页信息 -->
    <div class="pagination-info">
      <span class="course-count">共 {{ total }} 个课程</span>
    </div>

    <!-- 课程卡片区域 -->
    <div class="course-cards">
      <div v-if="courseList.length === 0" class="empty-data">
        <el-empty description="暂无课程数据" :image-size="200">
          <template #image>
            <img src="../../../assets/icons/time.svg" class="empty-icon" />
          </template>
        </el-empty>
      </div>
      <el-row :gutter="80" v-else>
        <el-col
          v-for="course in courseList"
          :key="course.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
          :xl="5"
        >
          <CourseCard
            :course="course"
            @edit="openAddEdit('编辑课程', course, false)"
            @copy="handleCopyCourse"
            @delete="deleteInfo"
            @select="toggleSelection"
            @view-details="handleViewDetails"
            :is-selected="isSelected(course)"
          />
        </el-col>
      </el-row>
    </div>

    <!-- 分页控件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[8, 16, 24, 32]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="cute-pagination"
      />
    </div>

    <!-- 组件 -->
    <CourseForm ref="courseRef" />
    <ImportExcel ref="ImportExcelRef" />

    <!-- 添加课程详情弹窗 -->
    <CourseDetailModal
      v-model="showDetailModal"
      :course="selectedCourseForDetail"
      @start-course="getCourses"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watchEffect } from 'vue'
import { useRole } from '@/hooks/useRole'
import {
  CirclePlus,
  Delete,
  EditPen,
  Upload,
  Download,
  DocumentCopy,
  Search,
  RefreshRight,
  ArrowDown,
  ArrowUp,
  VideoPlay,
  CircleCheck,
  MoreFilled
} from '@element-plus/icons-vue'
import {
  createCourseApi,
  removeCourseApi,
  updateCourseApi,
  getCourseListApi,
  getCourseDetailApi,
  importCourseExcelApi,
  exportCourseExcelApi,
} from '@/api/modules/oj/course/course';
import { useHandleData } from '@/hooks/useHandleData';
import CourseForm from '@/views/oj/course/components/CourseForm.vue';
import CourseCard from '@/views/oj/course/components/CourseCard.vue';
import type { ICourse } from '@/api/interface/oj/course/course';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox, ElMessage } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
import GroupSelect from '@/components/Oj/Group/GroupSelect.vue';
import UserSelect from '@/components/Common/User/UserSelect.vue';
import { useAppStore } from '@/stores/modules/app';
import { storeToRefs } from 'pinia';
import { getLightColor, getDarkColor } from '@/utils/color';
import GroupBlockShow from '@/components/Oj/Group/GroupBlockShow.vue';
import CourseDetailModal from './components/CourseDetailModal.vue';

defineOptions({
  name: 'CourseView'
})

const { isAdminOrCommon } = useRole()

// 获取应用主题色
const appStore = useAppStore();
const themePrimary = computed(() => appStore.primary);

// 计算衍生颜色 - 动态计算主题色
const primaryLighter = computed(() => getLightColor(themePrimary.value, 0.1));
const primaryDarker = computed(() => getDarkColor(themePrimary.value, 0.1));
const primaryGradient = computed(() => `linear-gradient(135deg, ${primaryLighter.value}, ${primaryDarker.value})`);
const bgGradient = computed(() => `linear-gradient(135deg, ${getLightColor(themePrimary.value, 0.95)}, white)`);
const boxShadowColor = computed(() => 'rgba(0, 0, 0, 0.1)');
const decorationGradient = computed(() => `linear-gradient(90deg, ${primaryLighter.value}, transparent)`);

// 搜索表单数据
const searchForm = reactive({
  name: '',
  content: '',
  url: '',
  teacherUserId: undefined,
  teacherName: undefined,
  teacherPhone: undefined,
  groupIds: undefined,
  enable: undefined,
  startTime: undefined,
  endTime: undefined
});

// 搜索区域折叠状态
const isSearchCollapsed = ref(true);
const searchFormRef = ref();

// 切换搜索区域显示
const toggleSearch = () => {
  isSearchCollapsed.value = !isSearchCollapsed.value;
};

// 课程列表数据
const courseList = ref<ICourse.Row[]>([]);
const currentPage = ref(1);
const pageSize = ref(8);
const total = ref(0);
const selectedIds = ref<number[]>([]);

// 全选功能
const selectedAll = ref(false);

const toggleSelectAll = () => {
  if (selectedAll.value) {
    // 取消全选
    selectedIds.value = [];
    selectedAll.value = false;
  } else {
    // 全选
    selectedIds.value = courseList.value.map(course => course.id!);
    selectedAll.value = true;
  }
};

// 监听已选择课程数量变化，自动更新全选状态
watchEffect(() => {
  if (courseList.value.length > 0 && selectedIds.value.length === courseList.value.length) {
    selectedAll.value = true;
  } else {
    selectedAll.value = false;
  }
});

// 查询课程列表
const getCourses = async () => {
  const params: ICourse.Query = {
    ...searchForm,
    page: currentPage.value,
    limit: pageSize.value
  };

  const formattedParams = formatParams(params);

  try {
    const res = await getCourseListApi(formattedParams);
    courseList.value = res.data.rows || [];
    total.value = res.data.total || 0;
  } catch (error) {
    console.error('获取课程列表失败:', error);
  }
};

// 搜索课程
const searchCourses = () => {
  currentPage.value = 1;
  getCourses();
};

// 重置搜索条件
const resetSearch = () => {
  searchFormRef.value?.resetFields();
  // 重置自定义字段
  searchForm.teacherUserId = undefined;
  searchForm.teacherName = undefined;
  searchForm.teacherPhone = undefined;
  searchForm.groupIds = undefined;

  currentPage.value = 1;
  getCourses();
};

// 处理教师选择变更
const handleTeacherChange = (val: number, user: any) => {
  if (user) {
    searchForm.teacherName = user.name;
    searchForm.teacherPhone = user.phone;
  } else {
    searchForm.teacherName = undefined;
    searchForm.teacherPhone = undefined;
  }
};

// 格式化参数
const formatParams = (params: ICourse.Query) => {
  let newParams = JSON.parse(JSON.stringify(params));

  newParams.startTime && (newParams.startTimeStart = newParams.startTime[0]);
  newParams.startTime && (newParams.startTimeEnd = newParams.startTime[1]);
  delete newParams.startTime;

  newParams.endTime && (newParams.endTimeStart = newParams.endTime[0]);
  newParams.endTime && (newParams.endTimeEnd = newParams.endTime[1]);
  delete newParams.endTime;

  newParams.createTime && (newParams.createTimeStart = newParams.createTime[0]);
  newParams.createTime && (newParams.createTimeEnd = newParams.createTime[1]);
  delete newParams.createTime;

  newParams.updateTime && (newParams.updateTimeStart = newParams.updateTime[0]);
  newParams.updateTime && (newParams.updateTimeEnd = newParams.updateTime[1]);
  delete newParams.updateTime;

  return newParams;
};

// 选择处理
const isSelected = (course: ICourse.Row) => {
  return selectedIds.value.includes(course.id!);
};

const toggleSelection = (course: ICourse.Row) => {
  const index = selectedIds.value.indexOf(course.id!);
  if (index > -1) {
    selectedIds.value.splice(index, 1);
  } else {
    selectedIds.value.push(course.id!);
  }
};

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  getCourses();
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  getCourses();
};

// 打开表单
const courseRef = ref<InstanceType<typeof CourseForm>>();
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getCourseDetailApi({ id: row?.id });
    row = record?.data;
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createCourseApi : updateCourseApi,
    getTableList: getCourses
  };
  courseRef.value?.acceptParams(params);
};

// 处理课程复制
const handleCopyCourse = async (row: any) => {
  // 获取课程详情确保有完整数据
  const response = await getCourseDetailApi({ id: row?.id });
  const courseDetail = response?.data;

  // 创建一个新的课程对象，除id外复制所有数据
  const copiedCourse = { ...courseDetail };

  // 删除id，确保创建新记录而不是更新
  delete copiedCourse.id;

  // 复制课程名称添加"(复制)"后缀
  if (copiedCourse.name) {
    copiedCourse.name = `${copiedCourse.name} (复制)`;
  }

  // 打开新增表单，并填充复制的数据
  openAddEdit('新增课程', copiedCourse, true);
};

// 删除信息
const deleteInfo = async (params: ICourse.Row) => {
  await useHandleData(
    removeCourseApi,
    { ids: [params.id] },
    `删除【${params.name || params.id}】课程`
  );

  // 移除选中项
  const index = selectedIds.value.indexOf(params.id!);
  if (index > -1) {
    selectedIds.value.splice(index, 1);
  }

  getCourses();
};

// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeCourseApi, { ids }, '删除所选课程');
  selectedIds.value = [];
  getCourses();
};

// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>();
const importData = () => {
  const params = {
    title: '课程',
    templateName: '课程',
    tempApi: downloadTemplate,
    importApi: importCourseExcelApi,
    getTableList: getCourses
  };
  ImportExcelRef.value?.acceptParams(params);
};

// 开始上课
const startCourse = async () => {
  if (selectedIds.value.length !== 1) {
    ElMessage.warning('请选择一个课程');
    return;
  }

  const courseId = selectedIds.value[0];
  // 查找选中的课程
  const selectedCourse = courseList.value.find(course => course.id === courseId);

  if (!selectedCourse) {
    ElMessage.error('未找到课程信息');
    return;
  }

  // 检查是否有课程链接
  if (!selectedCourse.url) {
    ElMessage.warning('该课程没有设置课程链接，无法开始上课');
    return;
  }

  // 询问用户是否打开课程链接
  ElMessageBox.confirm(
    `确定开始【${selectedCourse.name}】课程？将打开课程链接。`,
    '开始上课',
    {
      confirmButtonText: '开始上课',
      cancelButtonText: '取消',
      type: 'info',
    }
  )
    .then(() => {
      // 打开课程链接
      window.open(selectedCourse.url, '_blank');
      ElMessage.success(`已开始【${selectedCourse.name}】课程`);
    })
    .catch(() => {
      // 用户取消操作
    });
};

// 导出
const downloadFile = async () => {
  let params: ICourse.Query = {
    ...searchForm,
    page: 1,
    limit: 9999
  };

  const formattedParams = formatParams(params);
  useDownload(exportCourseExcelApi, "课程", formattedParams);
};

// 添加课程详情弹窗相关的状态
const showDetailModal = ref(false);
const selectedCourseForDetail = ref<any>(null);

// 查看课程详情
const handleViewDetails = (course: any) => {
  selectedCourseForDetail.value = course;
  showDetailModal.value = true;
};

// 组件挂载后获取数据
onMounted(() => {
  getCourses();
});
</script>

<style scoped lang="scss">
// 主题颜色变量 - 直接从JS计算结果绑定
$primary-color: v-bind(themePrimary);
$primary-lighter: v-bind(primaryLighter);
$primary-darker: v-bind(primaryDarker);
$primary-gradient: v-bind(primaryGradient);
$bg-gradient: v-bind(bgGradient);
$box-shadow-color: v-bind(boxShadowColor);
$decoration-gradient: v-bind(decorationGradient);

// 固定颜色变量
$success-color: #67C23A;
$warning-color: #E6A23C;
$danger-color: #F56C6C;
$info-color: #909399;
$white: #FFFFFF;

.course-container {
  position: relative;
  padding: 20px;
  min-height: 100%;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: 0 20px 80px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  z-index: 1;
  max-width: 1600px;
  margin: 0 auto;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: $primary-gradient;
    background-size: 200% 200%;
    animation: rainbow-border 8s ease infinite;
  }
}

// 搜索区域
.search-area {
  background-color: rgba(250, 250, 252, 0.8);
  border-radius: 20px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: all 0.5s cubic-bezier(0.22, 1, 0.36, 1);
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.05);

  &.is-collapsed {
    .search-header {
      border-bottom: none;
    }
  }

  .search-header {
    padding: 16px 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    cursor: pointer;
    transition: all 0.3s ease;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);

    &:hover {
      background-color: rgba(0, 0, 0, 0.02);
    }

    .search-title {
      font-size: 16px;
      font-weight: 600;
      color: #1d1d1f;
      font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
      display: flex;
      align-items: center;
      gap: 10px;

      .search-icon {
        font-size: 18px;
        color: $primary-color;
      }
    }

    .collapse-icon {
      font-size: 16px;
      transition: transform 0.3s ease;
      color: #666;

      &.is-rotate {
        transform: rotate(180deg);
      }
    }
  }

  .search-content {
    padding: 24px;

    :deep(.el-form-item__label) {
      font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
      font-weight: 500;
      color: #333;
    }

    :deep(.el-input__wrapper) {
      border-radius: 12px;
      box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1);
      padding: 4px 12px;
      transition: all 0.3s ease;

      &:hover, &.is-focus {
        box-shadow: 0 0 0 1px $primary-color;
      }
    }
  }
}

// 按钮组样式
.operation-btns {
  display: flex;
  gap: 16px;
  margin-bottom: 30px;
  align-items: center;
  flex-wrap: wrap;

  .cute-button {
    border-radius: 20px !important;
    padding: 12px 24px !important;
    font-size: 14px !important;
    font-weight: 500 !important;
    letter-spacing: 0.3px !important;
    transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1) !important;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08) !important;
    border: none !important;
    font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif !important;

    &:hover {
      transform: translateY(-2px) !important;
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12) !important;
    }

    &:active {
      transform: translateY(0) !important;
    }

    .button-text {
      padding-left: 4px !important;
    }

    &:disabled {
      cursor: not-allowed !important;
      opacity: 0.6 !important;
    }
  }
}

// 不同按钮的样式
.create-button {
  background: $primary-gradient !important;
  border-color: transparent !important;
  color: white !important;

  &:hover {
    background: $primary-gradient !important;
    filter: brightness(1.05) !important;
    border-color: transparent !important;
  }
}

.delete-button {
  background: rgba(245, 108, 108, 0.1) !important;
  border-color: transparent !important;
  color: #F56C6C !important;

  &:hover {
    background: rgba(245, 108, 108, 0.2) !important;
    border-color: transparent !important;
    color: #F56C6C !important;
  }

  &:disabled {
    background: #f5f5f5 !important;
    color: #c0c4cc !important;
    box-shadow: none !important;

    &:hover {
      transform: none !important;
    }
  }
}

.start-button {
  background: rgba(103, 194, 58, 0.1) !important;
  border-color: transparent !important;
  color: #67C23A !important;

  &:hover {
    background: rgba(103, 194, 58, 0.2) !important;
    border-color: transparent !important;
    color: #67C23A !important;
  }

  &:disabled {
    background: #f5f5f5 !important;
    color: #c0c4cc !important;
    box-shadow: none !important;

    &:hover {
      transform: none !important;
    }
  }
}

.select-button {
  background: rgba(64, 158, 255, 0.1) !important;
  border-color: transparent !important;
  color: #409EFF !important;

  &:hover {
    background: rgba(64, 158, 255, 0.2) !important;
    border-color: transparent !important;
    color: #409EFF !important;
  }

  &.is-selected {
    background: #409EFF !important;
    color: white !important;

    &:hover {
      background: #409EFF !important;
      filter: brightness(1.05) !important;
    }
  }
}

.import-button, .export-button {
  background: rgba(103, 194, 58, 0.1) !important;
  border-color: transparent !important;
  color: #67C23A !important;

  &:hover {
    background: rgba(103, 194, 58, 0.2) !important;
    border-color: transparent !important;
    color: #67C23A !important;
  }
}

// 分页信息
.pagination-info {
  margin: 10px 0;
  font-size: 14px;
  color: #666;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .course-count {
    font-weight: 600;
    color: $primary-color;
    font-size: 16px;
  }
}

// 课程卡片网格
.course-cards {
  margin-top: 30px;
  min-height: 400px;

  .empty-data {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 400px;

    .empty-icon {
      width: 120px;
      height: 120px;
      opacity: 0.3;
    }
  }

  :deep(.el-row) {
    row-gap: 36px;
  }
}

// 分页控件
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

// 搜索表单样式
.search-form {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 15px;

  .el-form-item {
    margin-bottom: 15px;
  }

  .special-form-item {
    grid-column: span 2;
  }
}

.search-buttons {
  text-align: right;

  .el-button {
    border-radius: 16px;
    transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
    padding: 10px 24px;
    font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
    font-weight: 500;
    letter-spacing: 0.3px;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    :deep(.el-icon) {
      margin-right: 6px;
    }
  }
}

// 动画效果
@keyframes rainbow-border {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .course-container {
    background: rgba(30, 30, 32, 0.8);

    .search-area {
      background-color: rgba(40, 40, 42, 0.8);
      border-color: rgba(255, 255, 255, 0.05);

      .search-header {
        .search-title {
          color: #f5f5f7;
        }

        &:hover {
          background-color: rgba(255, 255, 255, 0.05);
        }

        .collapse-icon {
          color: #aaa;
        }
      }

      .search-content {
        :deep(.el-form-item__label) {
          color: #ccc;
        }

        :deep(.el-input__wrapper) {
          background-color: rgba(30, 30, 32, 0.8);
          box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.2);

          &:hover, &.is-focus {
            box-shadow: 0 0 0 1px $primary-color;
          }
        }
      }
    }

    .pagination-info {
      color: #ccc;
    }

    .pagination-container {
      border-top-color: rgba(255, 255, 255, 0.1);
    }
  }

  .delete-button {
    background: rgba(245, 108, 108, 0.15) !important;
    color: #ff8585 !important;

    &:hover {
      background: rgba(245, 108, 108, 0.25) !important;
      color: #ff8585 !important;
    }
  }

  .start-button, .import-button, .export-button {
    background: rgba(103, 194, 58, 0.15) !important;
    color: #95d475 !important;

    &:hover {
      background: rgba(103, 194, 58, 0.25) !important;
      color: #95d475 !important;
    }
  }
}
</style>

