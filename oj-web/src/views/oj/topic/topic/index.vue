<template>
  <div class="topic-container">
    <!-- 操作按钮组 -->
    <div class="button-group">
      <el-button
        v-show="isAdminOrCommon"
        v-auth="'topic.create'"
        :icon="CirclePlus"
        class="cute-button create-button"
        type="primary"
        @click="openAddEdit('新增主题')"
      >
        <span class="button-text">新增</span>
        <span class="button-effect"></span>
      </el-button>

      <el-button
        v-show="isAdminOrCommon"
        :class="{ 'is-selected': isAllSelected }"
        :icon="Select"
        class="cute-button select-button"
        type="default"
        @click="toggleSelectAll"
      >
        <span class="button-text">{{ isAllSelected ? '取消全选' : '全选' }}</span>
      </el-button>

      <el-button
        v-show="isAdminOrCommon"
        v-auth="'topic.remove'"
        :disabled="!selectedTopics.length"
        :icon="Delete"
        class="cute-button delete-button"
        plain
        type="danger"
        @click="batchDelete(selectedTopics.map(t => t.id))"
      >
        <span class="button-text">批量删除</span>
        <span class="button-effect"></span>
      </el-button>
      <el-button
        v-show="isAdminOrCommon"
        v-auth="'topic.import'"
        :icon="Upload"
        class="cute-button import-button"
        plain
        type="primary"
        @click="importData"
      >
        <span class="button-text">导入</span>
        <span class="button-effect"></span>
      </el-button>
      <el-button
        v-show="isAdminOrCommon"
        v-auth="'topic.export'"
        :icon="Download"
        class="cute-button export-button"
        plain
        type="primary"
        @click="downloadFile"
      >
        <span class="button-text">导出</span>
        <span class="button-effect"></span>
      </el-button>

      <div class="search-toggle" @click="toggleSearch">
        <el-button :icon="Search" circle></el-button>
        <span class="toggle-text">{{ isSearchExpanded ? '收起筛选' : '展开筛选' }}</span>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div :class="{ expanded: isSearchExpanded }" class="search-container">
      <div class="search-header" @click="toggleSearch">
        <div class="search-title">
          <el-icon class="search-icon">
            <Search />
          </el-icon>
          <span>筛选条件</span>
        </div>
        <el-icon :class="{ 'is-rotate': isSearchExpanded }">
          <ArrowDown />
        </el-icon>
      </div>

      <div v-show="isSearchExpanded" class="search-content">
        <el-form :model="searchParam" label-width="80px" @keyup.enter="getTopicList">
          <el-row :gutter="30">
            <el-col :span="6">
              <el-form-item label="主题名">
                <el-input v-model="searchParam.name" clearable placeholder="请输入主题名称"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="主题代码">
                <el-input v-model="searchParam.code" clearable placeholder="请输入主题代码"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="所属班级">
                <GroupSelect v-model="searchParam.groupIds" multiple />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="负责人">
                <UserSelect v-model="searchParam.principalUserId" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="30">
            <el-col :span="6">
              <el-form-item label="时间策略">
                <EnumSelect v-model="searchParam.timeRangeType" :enum-data="TimeRangeType" type="select" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="主题类型">
                <EnumSelect v-model="searchParam.topicType" :enum-data="TopicType" :multiple="true" type="tab" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="判题模式">
                <EnumSelect v-model="searchParam.topicJudgeType" :enum-data="TopicJudgeType" type="tab" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="得分类型">
                <EnumSelect v-model="searchParam.oiScoreType" :enum-data="OiScoreType" type="tab" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="30">
            <el-col :span="6">
              <el-form-item label="公共主题">
                <BlockButtonSwitch
                  :model-value="searchParam.common === 'true'"
                  text="公共"
                  @update:model-value="val => (searchParam.common = val ? 'true' : 'false')"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="启用">
                <BlockButtonSwitch
                  :model-value="searchParam.enable === 'true'"
                  text="启用"
                  @update:model-value="val => (searchParam.enable = val ? 'true' : 'false')"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="标签">
                <TagSelect v-model="searchParam.tagIds" multiple />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="开始时间">
                <el-date-picker
                  v-model="searchParam.timeRange1"
                  end-placeholder="结束日期"
                  range-separator="至"
                  start-placeholder="开始日期"
                  style="width: 100%"
                  type="datetimerange"
                  value-format="YYYY-MM-DD HH:mm:ss"
                ></el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="截止时间">
                <el-date-picker
                  v-model="searchParam.timeRange2"
                  end-placeholder="结束日期"
                  range-separator="至"
                  start-placeholder="开始日期"
                  style="width: 100%"
                  type="datetimerange"
                  value-format="YYYY-MM-DD HH:mm:ss"
                ></el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="24" style="text-align: right">
              <el-button class="search-button" type="primary" @click="getTopicList">
                <el-icon>
                  <Search />
                </el-icon>
                搜索
              </el-button>
              <el-button class="reset-button" @click="resetSearch">
                <el-icon>
                  <Refresh />
                </el-icon>
                重置
              </el-button>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </div>

    <!-- 分页信息 -->
    <div v-if="total > 0" class="pagination-info">
      <span
        >共 <span class="total-count">{{ total }}</span> 条主题</span
      >
      <span v-if="selectedTopics.length" class="selected-count">
        已选择 <span class="count-num">{{ selectedTopics.length }}</span> 项
      </span>
    </div>

    <!-- 卡片视图 -->
    <div class="topic-card-container">
      <div v-if="topicList.length === 0" class="empty-data">
        <el-empty :image-size="200" description="暂无主题数据">
          <template #image>
            <img class="empty-icon" src="../../../../assets/icons/topic/topic.svg" />
          </template>
        </el-empty>
      </div>
      <el-row v-else :gutter="80">
        <el-col v-for="item in topicList" :key="item.id" :lg="6" :md="8" :sm="12" :xl="5" :xs="24">
          <TopicCard
            :is-selected="selectedTopics.some(t => t.id === item.id)"
            :topic="item"
            @copy="copyTopic"
            @delete="deleteInfo"
            @detail="toTopicDetail"
            @edit="openAddEdit('编辑主题', item, false)"
            @select="toggleTopicSelection"
            @do-topic="toDoTopic"
          />
        </el-col>
      </el-row>
    </div>

    <!-- 分页控件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :background="true"
        :page-sizes="[8, 16, 24, 32]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <TopicForm ref="topicRef" />
    <ImportExcel ref="ImportExcelRef" />
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { ArrowDown, CirclePlus, Delete, Download, Refresh, Search, Select, Upload } from '@element-plus/icons-vue';
import { useRole } from '@/hooks/useRole';
import {
  createTopicApi,
  exportTopicExcelApi,
  getTopicDetailApi,
  getTopicListApi,
  importTopicExcelApi,
  removeTopicApi,
  updateTopicApi
} from '@/api/modules/oj/topic/topic';
import { useHandleData } from '@/hooks/useHandleData';
import TopicForm from '@/views/oj/topic/topic/components/TopicForm.vue';
import TopicCard from '@/views/oj/topic/topic/components/TopicCard.vue';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { useDownload } from '@/hooks/useDownload';
import GroupSelect from '@/components/Oj/Group/GroupSelect.vue';
import UserSelect from '@/components/Common/User/UserSelect.vue';
import TagSelect from '@/components/Common/Meta/Tag/TagSelect.vue';
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue';
import BlockButtonSwitch from '@/components/Common/Base/BlockButtonSwitch.vue';
import { TimeRangeType } from '@/enums/oj/topic/TimeRangeType';
import { TopicType } from '@/enums/oj/topic/TopicType';
import { TopicJudgeType } from '@/enums/oj/topic/TopicJudgeType';
import { OiScoreType } from '@/enums/oj/topic/OiScoreType';
import { useRouter } from 'vue-router';
import { useAppStore } from '@/stores/modules/app';
import { storeToRefs } from 'pinia';
import { getDarkColor, getLightColor } from '@/utils/color';
import type { ITopic } from '@/api/interface/oj/topic/topic';
import { ElMessage } from 'element-plus';

defineOptions({
  name: 'TopicView'
});

const { isAdminOrCommon } = useRole();

// 获取应用主题色
const appStore = useAppStore();
const { primary: themePrimary } = storeToRefs(appStore);

// 计算衍生颜色 - 所有颜色值都在这里生成，不在SCSS中处理
const primaryLighter = computed(() => getLightColor(themePrimary.value, 0.1));
const primaryDarker = computed(() => getDarkColor(themePrimary.value, 0.1));
const primaryLight = computed(() => getLightColor(themePrimary.value, 0.8));
const primaryGradient = computed(() => `linear-gradient(135deg, ${primaryLighter.value}, ${primaryDarker.value})`);
const bgGradient = computed(
  () => `linear-gradient(135deg, ${getLightColor(themePrimary.value, 0.95)}, ${getLightColor(themePrimary.value, 0.9)})`
);
const boxShadowColor = computed(() => 'rgba(0, 0, 0, 0.1)');
const boxShadowColorDarker = computed(() => 'rgba(0, 0, 0, 0.25)');
const borderGradient = computed(
  () => `linear-gradient(90deg, ${primaryLighter.value}, ${primaryDarker.value}, ${primaryLighter.value})`
);
const decorationGradient = computed(() => `linear-gradient(90deg, rgba(0, 0, 0, 0.1), rgba(0, 0, 0, 0.05))`);

const router = useRouter();
const topicType = ref<string>(router.currentRoute.value.query.topicType as string);

const topicRef = ref<InstanceType<typeof TopicForm>>();
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>();

// 分页参数
const pageNum = ref<number>(1);
const pageSize = ref<number>(8);
const total = ref<number>(0);

// 搜索参数
const searchParam = reactive<any>({ topicType: topicType });

// 主题列表数据
const topicList = ref<ITopic.Row[]>([]);
const selectedTopics = ref<ITopic.Row[]>([]);

// 搜索展开收起控制
const isSearchExpanded = ref<boolean>(false);
const toggleSearch = () => {
  isSearchExpanded.value = !isSearchExpanded.value;
};

// 全选功能
const isAllSelected = computed(() => {
  return topicList.value.length > 0 && selectedTopics.value.length === topicList.value.length;
});

const toggleSelectAll = () => {
  if (isAllSelected.value) {
    // 取消全选
    selectedTopics.value = [];
  } else {
    // 全选 - 创建新的对象数组，避免直接引用原数组对象
    selectedTopics.value = topicList.value.map(item => ({ ...item }));
  }
};

// 获取主题列表
const getTopicList = async () => {
  try {
    // 构建分页参数
    const params: any = {
      page: pageNum.value,
      limit: pageSize.value,
      topicType: topicType.value
    };

    // 从searchParam复制有值的属性
    Object.entries(searchParam).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        params[key] = value;
      }
    });

    // 格式化日期范围
    if (searchParam.timeRange1 && Array.isArray(searchParam.timeRange1)) {
      params.startTimeStart = searchParam.timeRange1[0];
      params.startTimeEnd = searchParam.timeRange1[1];
    }

    if (searchParam.timeRange2 && Array.isArray(searchParam.timeRange2)) {
      params.endTimeStart = searchParam.timeRange2[0];
      params.endTimeEnd = searchParam.timeRange2[1];
    }

    // 调用API并处理结果
    const res = await getTopicListApi(params);
    console.log('API响应:', res);

    if (res.data) {
      // 尝试适配不同的数据结构
      if (Array.isArray(res.data)) {
        topicList.value = res.data;
        total.value = res.data.length;
      } else {
        // 使用断言来解决类型问题
        const data = res.data as any;

        if (data.rows && Array.isArray(data.rows)) {
          // API返回的数据结构是 data.rows
          topicList.value = data.rows;
          total.value = data.total || data.rows.length;
        } else if (data.records && Array.isArray(data.records)) {
          topicList.value = data.records;
          total.value = data.total || data.records.length;
        } else if (data.list && Array.isArray(data.list)) {
          topicList.value = data.list;
          total.value = data.total || data.list.length;
        } else {
          console.error('未能识别的数据结构:', data);
          topicList.value = [];
          total.value = 0;
        }
      }
    } else {
      console.error('API响应中没有data属性');
      topicList.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取主题列表失败', error);
    topicList.value = [];
    total.value = 0;
  }
};

// 重置搜索参数
const resetSearch = () => {
  Object.keys(searchParam).forEach(key => {
    delete searchParam[key];
  });
  pageNum.value = 1;
  getTopicList();
};

// 页码变化
const handleCurrentChange = (val: number) => {
  pageNum.value = val;
  getTopicList();
};

// 每页数量变化
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  pageNum.value = 1;
  getTopicList();
};

// 打开表单(新增、编辑)
const openAddEdit = async (title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getTopicDetailApi({ id: row?.id });
    row = record?.data;
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createTopicApi : updateTopicApi,
    getTableList: getTopicList
  };
  topicRef.value?.acceptParams(params);
};

// 删除主题
const deleteInfo = async (params: ITopic.Row) => {
  await useHandleData(removeTopicApi, { ids: [params.id] }, `删除【${params.name}】主题`);
  getTopicList();
};

// 批量删除
const batchDelete = async (ids: any[]) => {
  // 过滤掉可能的undefined值
  const validIds = ids.filter(id => id !== undefined) as (string | number)[];
  await useHandleData(removeTopicApi, { ids: validIds }, '删除所选主题');
  selectedTopics.value = [];
  getTopicList();
};

// 导入
const importData = () => {
  const params = {
    title: '主题',
    templateName: '主题',
    tempApi: downloadTemplate,
    importApi: importTopicExcelApi,
    getTableList: getTopicList
  };
  ImportExcelRef.value?.acceptParams(params);
};

// 导出
const downloadFile = async () => {
  // 构建导出参数
  const exportParams: any = {};

  // 只复制有值的属性
  for (const key in searchParam) {
    if (searchParam[key] !== undefined && searchParam[key] !== null && searchParam[key] !== '') {
      exportParams[key] = searchParam[key];
    }
  }

  // 处理日期范围
  if (searchParam.timeRange1 && Array.isArray(searchParam.timeRange1)) {
    exportParams.startTimeStart = searchParam.timeRange1[0];
    exportParams.startTimeEnd = searchParam.timeRange1[1];
  }

  if (searchParam.timeRange2 && Array.isArray(searchParam.timeRange2)) {
    exportParams.endTimeStart = searchParam.timeRange2[0];
    exportParams.endTimeEnd = searchParam.timeRange2[1];
  }

  useDownload(exportTopicExcelApi, '主题', exportParams);
};

// 处理进入主题
const toDoTopic = (row: ITopic.Row) => {
  // 检查时间策略和时间限制
  const isStrictTimeView = row.timeRangeType === TimeRangeType.STRICT_TIME_VIEW.code;
// 如果是严格时间查看模式，需要检查当前时间是否已达到开始时间
  if (isStrictTimeView && row.startTime) {
    const now = new Date();
    const startTime = new Date(row.startTime);

    if (now < startTime) {
      // 当前时间未达到开始时间，显示提示
      ElMessage({
        message: `该主题采用严格时间策略，将于 ${formatDate(row.startTime)} 开放查看`,
        type: 'warning',
        duration: 3000
      });
      return;
    }
  }

  router.push({
    path: `/oj/topic/doTopic/${row.id}`,
    query: {
      topicName: row.name,
      topicCode: row.code
    }
  });
};

// 跳转到主题详情页面
const toTopicDetail = (row: ITopic.Row) => {
  router.push({
    path: `/oj/topic/detail/${row.id}`,
    query: {
      topicName: row.name,
      topicCode: row.code
    }
  });
};

// 格式化日期时间
const formatDate = (dateStr: string) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');

  return `${year}年${month}月${day}日 ${hours}:${minutes}`;
};

// 复制主题
const copyTopic = async (row: ITopic.Row) => {
  if (!row?.id) return;
  // 获取主题详情
  const recordCopy = await getTopicDetailApi({ id: row.id });
  // 创建新数据，移除ID以便创建新记录
  const newData = { ...recordCopy?.data };
  delete newData.id;
  // 修改名称，添加"(复制)"后缀
  newData.name = `${newData.name}(复制)`;

  // 打开新增表单并填入数据
  const params = {
    title: '新增主题',
    row: newData,
    api: createTopicApi,
    getTableList: getTopicList
  };
  topicRef.value?.acceptParams(params);
};

// 切换主题选中状态
const toggleTopicSelection = (topic: ITopic.Row) => {
  const index = selectedTopics.value.findIndex(t => t.id === topic.id);
  if (index > -1) {
    selectedTopics.value.splice(index, 1);
  } else {
    selectedTopics.value.push(topic);
  }
};

onMounted(() => {
  getTopicList();
});
</script>

<style lang="scss" scoped>
// 主题颜色变量 - 直接从JS计算结果绑定
$primary-color: v-bind('themePrimary');
$primary-lighter: v-bind('primaryLighter');
$primary-darker: v-bind('primaryDarker');
$primary-light: v-bind('primaryLight');
$primary-gradient: v-bind('primaryGradient');
$bg-gradient: v-bind('bgGradient');
$box-shadow-color: v-bind('boxShadowColor');
$box-shadow-color-darker: v-bind('boxShadowColorDarker');
$border-gradient: v-bind('borderGradient');
$decoration-gradient: v-bind('decorationGradient');

// 固定颜色变量 - 这些不从主题色衍生
$success-color: #67c23a;
$warning-color: #e6a23c;
$danger-color: #f56c6c;
$info-color: #909399;
$success-gradient: linear-gradient(135deg, #81c784, #4caf50);
$warning-gradient: linear-gradient(135deg, #ffd54f, #ffa726);
$danger-gradient: linear-gradient(135deg, #e57373, #ef5350);
$white: #ffffff;

.topic-container {
  position: relative;
  padding: 24px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  z-index: 1;
  max-width: 1600px;
  margin: 0 auto;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif;

  // 操作按钮组样式
  .button-group {
    display: flex;
    gap: 16px;
    margin-bottom: 32px;
    align-items: center;
    flex-wrap: wrap;

    .cute-button {
      border-radius: 12px;
      padding: 10px 20px;
      font-size: 14px;
      font-weight: 500;
      letter-spacing: 0.2px;
      transition: all 0.3s cubic-bezier(0.25, 1, 0.5, 1);
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
      border: none;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
      }

      &:active {
        transform: translateY(0);
      }

      .button-text {
        padding-left: 4px;
      }
    }

    .search-toggle {
      margin-left: auto;
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      transition: all 0.3s ease;

      .toggle-text {
        font-size: 14px;
        color: #6e6e73;
      }

      &:hover {
        color: var(--el-color-primary);

        .toggle-text {
          color: var(--el-color-primary);
        }
      }
    }
  }

  // 搜索区域样式
  .search-container {
    background-color: rgba(250, 250, 252, 0.8);
    border-radius: 16px;
    margin-bottom: 32px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
    transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1);
    overflow: hidden;
    border: 1px solid rgba(0, 0, 0, 0.04);

    &.expanded {
      .search-header {
        border-bottom: 1px solid rgba(0, 0, 0, 0.05);
      }
    }

    .search-header {
      padding: 16px 24px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        background-color: rgba(0, 0, 0, 0.02);
      }

      .search-title {
        font-size: 16px;
        font-weight: 600;
        color: #1d1d1f;
        display: flex;
        align-items: center;
        gap: 10px;

        .search-icon {
          font-size: 16px;
          color: var(--el-color-primary);
        }
      }

      .el-icon {
        font-size: 16px;
        transition: transform 0.3s ease;
        color: #6e6e73;

        &.is-rotate {
          transform: rotate(180deg);
        }
      }
    }

    .search-content {
      padding: 24px;

      :deep(.el-form-item__label) {
        font-weight: 500;
        color: #1d1d1f;
      }

      :deep(.el-input__wrapper) {
        border-radius: 10px;
        box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1);
        transition: all 0.2s ease;

        &:hover,
        &.is-focus {
          box-shadow: 0 0 0 1px var(--el-color-primary);
        }
      }

      .search-button,
      .reset-button {
        border-radius: 10px;
        transition: all 0.3s ease;
        padding: 9px 20px;
        font-weight: 500;
        letter-spacing: 0.2px;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        :deep(.el-icon) {
          margin-right: 6px;
        }
      }
    }
  }

  // 分页信息样式
  .pagination-info {
    margin: 10px 0 20px;
    font-size: 14px;
    color: #6e6e73;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .total-count,
    .count-num {
      font-weight: 600;
      color: var(--el-color-primary);
      font-size: 16px;
    }

    .selected-count {
      color: #6e6e73;
    }
  }

  // 分页容器样式
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 40px;
    padding-top: 20px;
    border-top: 1px solid rgba(0, 0, 0, 0.05);
  }

  // 卡片容器样式
  .topic-card-container {
    margin-top: 32px;
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
}

// 不同按钮的样式
.create-button {
  background: $primary-gradient;
  border-color: transparent;
  color: white;

  &:hover {
    background: $primary-gradient;
    filter: brightness(1.05);
    border-color: transparent;
  }
}

.delete-button {
  background: rgba(245, 108, 108, 0.1);
  border-color: transparent;
  color: #f56c6c;

  &:hover {
    background: rgba(245, 108, 108, 0.2);
    border-color: transparent;
    color: #f56c6c;
  }

  &:disabled {
    background: #f5f5f5;
    color: #c0c4cc;
    box-shadow: none;

    &:hover {
      transform: none;
    }
  }
}

.import-button,
.export-button {
  background: rgba(103, 194, 58, 0.1);
  border-color: transparent;
  color: #67c23a;

  &:hover {
    background: rgba(103, 194, 58, 0.2);
    border-color: transparent;
    color: #67c23a;
  }
}

.select-button {
  background: rgba(64, 158, 255, 0.1);
  border-color: transparent;
  color: #409eff;

  &:hover {
    background: rgba(64, 158, 255, 0.2);
    border-color: transparent;
    color: #409eff;
  }

  &.is-selected {
    background: #409eff;
    color: white;

    &:hover {
      background: #409eff;
      filter: brightness(1.05);
    }
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .topic-container {
    background: rgba(30, 30, 32, 0.8);

    .search-container {
      background-color: rgba(40, 40, 42, 0.8);
      border-color: rgba(255, 255, 255, 0.05);

      .search-header {
        .search-title {
          color: #f5f5f7;
        }

        &:hover {
          background-color: rgba(255, 255, 255, 0.05);
        }
      }

      .search-content {
        :deep(.el-form-item__label) {
          color: #ccc;
        }

        :deep(.el-input__wrapper) {
          background-color: rgba(30, 30, 32, 0.8);
          box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.2);

          &:hover,
          &.is-focus {
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
    background: rgba(245, 108, 108, 0.15);
    color: #ff8585;

    &:hover {
      background: rgba(245, 108, 108, 0.25);
      color: #ff8585;
    }
  }

  .import-button,
  .export-button {
    background: rgba(103, 194, 58, 0.15);
    color: #95d475;

    &:hover {
      background: rgba(103, 194, 58, 0.25);
      color: #95d475;
    }
  }
}
</style>
