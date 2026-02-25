<template>
  <div class="submit-section">
    <div class="section-header">
      <div class="title">
        <el-icon><DataAnalysis /></el-icon>
        <span>{{ title }}</span>
      </div>
      <div class="actions">
        <button
          class="custom-button warning-button"
          @click="exportToExcel"
          :disabled="isExporting"
        >
          <div class="button-content">
            <el-icon class="button-icon"><Download /></el-icon>
            <span class="button-text">导出Excel</span>
            <div v-if="isExporting" class="loading-spinner"></div>
          </div>
        </button>

        <button
          class="custom-button success-button"
          @click="exportToImage"
          :disabled="isExportingImage"
        >
          <div class="button-content">
            <el-icon class="button-icon"><Picture /></el-icon>
            <span class="button-text">导出图片</span>
            <div v-if="isExportingImage" class="loading-spinner"></div>
          </div>
        </button>

        <button
          class="custom-button default-button"
          @click="isCollapsed = !isCollapsed"
        >
          <div class="button-content">
            <el-icon class="button-icon">
              <ArrowUp v-if="isCollapsed" />
              <ArrowDown v-else />
            </el-icon>
            <span class="button-text">{{ isCollapsed ? '展开' : '折叠' }}</span>
          </div>
        </button>
      </div>
    </div>

    <div class="submit-content" :class="{ 'collapsed': isCollapsed }">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>
      <el-empty
        v-else-if="!hasData"
        description="暂无做题数据"
        class="empty-data"
      />
      <div v-else class="submit-table-wrapper">
        <el-scrollbar>
          <el-table
            :data="tableData"
            border
            stripe
            size="default"
            class="submit-table"
            @cell-click="handleCellClick"
          >
            <!-- 用户信息列 -->
            <el-table-column prop="userName" label="姓名" min-width="80" width="120" fixed="left">
              <template #default="scope">
                <div class="user-info-cell">
                  <el-avatar
                    :size="32"
                    :src="scope.row.avatar"
                    class="user-avatar"
                  >
                    {{ getNameInitial(scope.row.userName || scope.row.nickName) }}
                  </el-avatar>
                  <div class="user-name-wrapper">
                    <span class="user-name">{{ scope.row.userName || '未知' }}</span>
                    <span class="user-nickname" v-if="scope.row.nickName">({{ scope.row.nickName }})</span>
                  </div>
                </div>
              </template>
            </el-table-column>

            <!-- 统计信息 -->
            <el-table-column label="统计信息">
              <el-table-column prop="totalScore" label="总分" width="80" align="center" sortable>
                <template #default="scope">
                  <div class="score-cell">{{ scope.row.totalScore || 0 }}</div>
                </template>
              </el-table-column>
              <el-table-column prop="acNum" label="通过数" width="80" align="center" sortable>
                <template #default="scope">
                  <div class="ac-num-cell">{{ scope.row.acNum || 0 }}</div>
                </template>
              </el-table-column>
            </el-table-column>

            <!-- 题目列 - 动态生成 -->
            <el-table-column
              v-for="problem in submitInfo.problems"
              :key="problem.problemId"
              :label="problem.problemKey"
              :min-width="160"
              align="center"
            >
              <template #header>
                <div class="problem-header-cell" @click="showProblemDetail(problem)">
                  <span class="problem-key">{{ problem.problemKey }}</span>
                  <el-tooltip
                    placement="top"
                    :content="problem.problemTitle"
                  >
                    <span class="problem-title">{{ ellipsis(problem.problemTitle, 20) }}</span>
                  </el-tooltip>
                </div>
              </template>
              <template #default="scope">
                <div
                  class="problem-cell"
                  :class="getProblemCellClass(scope.row.problems[problem.problemId])"
                  @click="showSubmitDetail(scope.row, problem)"
                >
                  <template v-if="scope.row.problems[problem.problemId]">
                    <div class="problem-status">
                      <JudgeStatusShow :code="scope.row.problems[problem.problemId].judgeStatus" />
                    </div>
                    <div class="problem-score">
                      {{ scope.row.problems[problem.problemId].score || 0 }}分
                    </div>
                    <div class="problem-time" v-if="scope.row.problems[problem.problemId].useTime">
                      {{ formatTime(scope.row.problems[problem.problemId].useTime) }}
                    </div>
                  </template>
                  <template v-else>
                    <div class="problem-status no-submit">未提交</div>
                  </template>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-scrollbar>
      </div>
    </div>

    <!-- 对话框 - 做题详情 -->
    <el-dialog
      v-model="submitDetailVisible"
      title="提交详情"
      width="600px"
      class="submit-detail-dialog"
      :append-to-body="true"
    >
      <template v-if="selectedSubmit">
        <div class="detail-item">
          <span class="detail-label">题目：</span>
          <span class="detail-value">{{ selectedProblem?.problemKey }} - {{ selectedProblem?.problemTitle }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">提交者：</span>
          <span class="detail-value">{{ selectedUser?.userName }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">判题状态：</span>
          <span class="detail-value">
            <JudgeStatusShow :code="selectedSubmit.judgeStatus" />
          </span>
        </div>
        <div class="detail-item" v-if="topicJudgeType === 1">
          <span class="detail-label">得分：</span>
          <span class="detail-value">{{ selectedSubmit.score }}</span>
        </div>
        <div class="detail-item" v-if="selectedSubmit.useTime">
          <span class="detail-label">用时：</span>
          <span class="detail-value">{{ formatTime(selectedSubmit.useTime) }}</span>
        </div>
        <div class="detail-item" v-if="selectedSubmit.submitNum">
          <span class="detail-label">提交次数：</span>
          <span class="detail-value">{{ selectedSubmit.submitNum }}</span>
        </div>
      </template>
    </el-dialog>

    <!-- 对话框 - 题目详情 -->
    <el-dialog
      v-model="problemDetailVisible"
      title="题目详情"
      width="600px"
      class="problem-detail-dialog"
      :append-to-body="true"
    >
      <template v-if="selectedProblem">
        <div class="detail-item">
          <span class="detail-label">题号：</span>
          <span class="detail-value">{{ selectedProblem.problemKey }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">标题：</span>
          <span class="detail-value">{{ selectedProblem.problemTitle }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">类型：</span>
          <span class="detail-value">{{ selectedProblem.problemType }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">满分：</span>
          <span class="detail-value">{{ selectedProblem.maxScore }}</span>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue';
import { DataAnalysis, Download, Picture, ArrowUp, ArrowDown } from '@element-plus/icons-vue';
import { getTopicSubmitStatusApi } from '@/api/modules/oj/topic/topicSubmit';
import type { ITopicSubmit } from '@/api/interface/oj/topic/topicSubmit';
import { ElMessage } from 'element-plus';
import JudgeStatusShow from '@/components/Oj/Judge/JudgeStatusShow.vue';
import { saveAs } from 'file-saver';
import html2canvas from 'html2canvas';

const props = defineProps({
  topicId: {
    type: Number,
    required: true
  },
  title: {
    type: String,
    default: '成绩单'
  },
  topicJudgeType: {
    type: Number,
    default: 0 // 默认为ACM判题模式
  },
  topicName: {
    type: String,
    default: ''
  },
  headerBgColor: {
    type: String,
    default: '#f9f9f9'
  },
  loading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:loading', 'error']);

// 数据和状态
const submitInfo = ref<ITopicSubmit.SubmitInfo>({
  users: [],
  problems: [],
  submissions: {}
});
const isExporting = ref(false);
const isExportingImage = ref(false);
const submitDetailVisible = ref(false);
const problemDetailVisible = ref(false);
const selectedSubmit = ref<any>(null);
const selectedProblem = ref<any>(null);
const selectedUser = ref<any>(null);
const isCollapsed = ref(false);

// 计算属性
const hasData = computed(() => {
  return submitInfo.value.users && submitInfo.value.users.length > 0 &&
         submitInfo.value.problems && submitInfo.value.problems.length > 0;
});

// 处理数据，转换为表格数据格式
const tableData = computed(() => {
  const result: any[] = [];

  if (!hasData.value) return result;

  submitInfo.value.users.forEach(user => {
    const userSubmissions = submitInfo.value.submissions[user.userId] || {};

    // 计算总分和AC题目数
    let totalScore = 0;
    let acNum = 0;
    const problemsData: Record<number, any> = {};

    submitInfo.value.problems.forEach(problem => {
      const submission = userSubmissions[problem.problemId];
      if (submission) {
        problemsData[problem.problemId] = submission;
        totalScore += submission.score || 0;
        if (submission.judgeStatus === 0) { // 只有状态为0(Accepted)才计入通过数
          acNum++;
        }
      }
    });

    result.push({
      userId: user.userId,
      userName: user.name,
      nickName: user.nickName,
      avatar: user.avatar,
      totalScore: user.totalScore || totalScore,
      acNum: user.totalAcNum || acNum,
      problems: problemsData
    });
  });

  // 按总分排序（降序）
  return result.sort((a, b) => b.totalScore - a.totalScore);
});

// 获取状态文本
const getStatusText = (status: number | undefined) => {
  if (status === undefined) return '未提交';

  const statusMap: Record<number, string> = {
    [-10]: '未提交',
    [-4]: '已取消',
    [-3]: '格式错误',
    [-2]: '编译错误',
    [-1]: '答案错误',
    0: '通过',
    1: '超时',
    2: '内存超限',
    3: '运行时错误',
    4: '系统错误',
    5: '等待中',
    6: '编译中',
    7: '评测中',
    8: '部分通过',
    9: '提交中',
    10: '提交失败',
    15: '无状态'
  };

  return statusMap[status] || '未知状态';
};

// 获取题目单元格的样式类
const getProblemCellClass = (submission: any) => {
  if (!submission) return 'no-submission';

  const statusClass: Record<number, string> = {
    0: 'accepted',
    1: 'time-limit',
    2: 'wrong-answer',
    3: 'compile-error',
    4: 'runtime-error',
    5: 'memory-limit',
    6: 'system-error',
    7: 'judging',
    8: 'waiting'
  };

  return statusClass[submission.judgeStatus] || 'unknown';
};

// 获取姓名首字母（用于头像显示）
const getNameInitial = (name?: string) => {
  if (!name) return '?';
  return name.charAt(0).toUpperCase();
};

// 格式化时间（分钟）
const formatTime = (minutes?: number) => {
  if (minutes === undefined || minutes === null) return '-';

  if (minutes < 60) {
    return `${minutes}分钟`;
  }

  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;

  if (mins === 0) {
    return `${hours}小时`;
  }

  return `${hours}小时${mins}分钟`;
};

// 截断字符串
const ellipsis = (str: string | undefined, maxLength: number) => {
  if (!str) return '';
  return str.length > maxLength ? str.substring(0, maxLength) + '...' : str;
};

// 单元格点击处理
const handleCellClick = (row: any, column: any) => {
  // 可以在这里添加额外的单元格点击处理逻辑
  console.log('单元格点击', row, column);
};

// 显示提交详情
const showSubmitDetail = (user: any, problem: any) => {
  if (!user || !problem || !user.problems[problem.problemId]) return;

  selectedSubmit.value = user.problems[problem.problemId];
  selectedProblem.value = problem;
  selectedUser.value = user;
  submitDetailVisible.value = true;
};

// 显示题目详情
const showProblemDetail = (problem: any) => {
  if (!problem) return;

  selectedProblem.value = problem;
  problemDetailVisible.value = true;
};

// 导出Excel
const exportToExcel = async () => {
  if (!hasData.value) {
    ElMessage.warning('暂无数据可导出');
    return;
  }

  isExporting.value = true;

  try {
    // 动态导入xlsx库，使用更兼容的方式
    let XLSX;
    try {
      XLSX = await import('xlsx');
      // 如果导入的是ES模块，可能需要访问default属性
      if (XLSX.default && typeof XLSX.default === 'object') {
        XLSX = XLSX.default;
      }
    } catch (importError) {
      console.error('动态导入xlsx失败，尝试使用全局变量', importError);
      // 如果动态导入失败，尝试使用全局变量（如果有的话）
      if (typeof window !== 'undefined' && (window as any).XLSX) {
        XLSX = (window as any).XLSX;
      } else {
        throw new Error('无法加载XLSX库，请检查依赖配置');
      }
    }

    // 创建工作表数据
    const wsData = [
      // 主标题
      [`${props.topicName || '主题'} - 成绩单统计表`],
      // 空行
      [''],
      // 表头
      ['序号', '用户名', '昵称', '总分', 'AC题数', '提交总数', ...submitInfo.value.problems.map(p => `${p.problemKey}(${p.maxScore}分)\n${p.problemTitle}`)]
    ];

    // 填充数据行
    tableData.value.forEach((row, index) => {
      const rowData = [
        index + 1, // 序号
        row.userName,
        row.nickName || '',
        row.totalScore,
        row.acNum,
        Object.keys(row.problems).length
      ];

      // 添加每个题目的得分和状态
      submitInfo.value.problems.forEach(problem => {
        const submission = row.problems[problem.problemId];
        if (submission) {
          const statusText = getStatusText(submission.judgeStatus);
          rowData.push(`${submission.score || 0}分\n${statusText}`);
        } else {
          rowData.push('未提交');
        }
      });

      wsData.push(rowData);
    });

    // 创建工作表
    const ws = XLSX.utils.aoa_to_sheet(wsData);

    // 设置单元格合并 - 标题行跨越所有列
    const range = {s: {r: 0, c: 0}, e: {r: 0, c: 6 + submitInfo.value.problems.length - 1}};
    if(!ws['!merges']) ws['!merges'] = [];
    ws['!merges'].push(range);

    // 设置列宽
    const wscols = [
      {wch: 6},  // 序号
      {wch: 15}, // 用户名
      {wch: 15}, // 昵称
      {wch: 8},  // 总分
      {wch: 8},  // AC题数
      {wch: 8},  // 提交总数
    ];

    // 添加题目列宽
    submitInfo.value.problems.forEach(() => {
      wscols.push({wch: 20}); // 题目列宽设置为20
    });

    ws['!cols'] = wscols;

    // 创建工作簿
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, '成绩单');

    // 生成Excel文件并下载
    const excelBuffer = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });
    const blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    saveAs(blob, `${props.topicName || '主题'}成绩单-${new Date().toISOString().split('T')[0]}.xlsx`);

    ElMessage.success('导出成功');
  } catch (error) {
    console.error('导出Excel失败', error);
    ElMessage.error('导出失败');
  } finally {
    isExporting.value = false;
  }
};

// 导出图片
const exportToImage = async () => {
  if (!hasData.value) {
    ElMessage.warning('暂无数据可导出');
    return;
  }

  isExportingImage.value = true;

  try {
    // 等待下一次DOM更新
    await nextTick();

    // 获取表格DOM元素
    const tableElement = document.querySelector('.submit-table-wrapper');

    if (!tableElement) {
      throw new Error('未找到表格元素');
    }

    // 创建标题元素
    const titleDiv = document.createElement('div');
    titleDiv.style.textAlign = 'center';
    titleDiv.style.fontSize = '20px';
    titleDiv.style.fontWeight = 'bold';
    titleDiv.style.padding = '20px 0';
    titleDiv.textContent = `${props.topicName || '主题'} - 成绩单统计表`;

    // 创建包装元素
    const wrapperDiv = document.createElement('div');
    wrapperDiv.style.backgroundColor = '#fff';
    wrapperDiv.style.padding = '20px';
    wrapperDiv.style.borderRadius = '5px';

    // 克隆表格元素
    const tableClone = tableElement.cloneNode(true) as HTMLElement;
    tableClone.style.maxHeight = 'none'; // 移除高度限制

    // 添加标题和表格到包装元素
    wrapperDiv.appendChild(titleDiv);
    wrapperDiv.appendChild(tableClone);

    // 将包装元素添加到文档中
    document.body.appendChild(wrapperDiv);

    // 使用html2canvas将元素转换为canvas
    const canvas = await html2canvas(wrapperDiv, {
      scale: 2, // 提高清晰度
      useCORS: true, // 允许跨域图片
      backgroundColor: '#fff',
      logging: false
    });

    // 从canvas获取图片数据URL
    const imageUrl = canvas.toDataURL('image/png');

    // 创建下载链接
    const downloadLink = document.createElement('a');
    downloadLink.href = imageUrl;
    downloadLink.download = `${props.topicName || '主题'}成绩单-${new Date().toISOString().split('T')[0]}.png`;

    // 触发下载
    document.body.appendChild(downloadLink);
    downloadLink.click();

    // 清理
    document.body.removeChild(downloadLink);
    document.body.removeChild(wrapperDiv);

    ElMessage.success('导出图片成功');
  } catch (error) {
    console.error('导出图片失败', error);
    ElMessage.error('导出图片失败');
  } finally {
    isExportingImage.value = false;
  }
};

// 获取做题数据
const fetchSubmitData = async () => {
  if (!props.topicId) return;

  emit('update:loading', true);

  try {
    const response = await getTopicSubmitStatusApi({ topicId: props.topicId });

    if (response.data) {
      // 转换数据结构以适应组件要求
      if (Array.isArray(response.data)) {
        console.log('收到数据数组:', response.data);

        // 从API返回的数组构建期望的数据结构
        const users: any[] = [];
        const problems: any[] = [];
        const submissions: Record<number, Record<number, any>> = {};
        const userMap = new Map();
        const problemMap = new Map();

        // 处理数据
        response.data.forEach(item => {
          const { userId, problemId, name, nickName, problemKey, problemTitle, problemType, judgeStatus, score, useTime } = item;

          // 添加用户
          if (userId && !userMap.has(userId)) {
            userMap.set(userId, true);
            users.push({
              userId,
              name,
              nickName,
              avatar: '', // API没有提供头像数据
              totalScore: 0,
              totalAcNum: 0,
              totalSubmitNum: 0
            });
          }

          // 添加题目
          if (problemId && !problemMap.has(problemId)) {
            problemMap.set(problemId, true);
            problems.push({
              problemId,
              problemKey,
              problemTitle,
              problemType,
              maxScore: 100 // 假设每道题满分为100
            });
          }

          // 添加提交记录
          if (userId && problemId) {
            if (!submissions[userId]) {
              submissions[userId] = {};
            }

            submissions[userId][problemId] = {
              judgeStatus,
              score,
              submitNum: 1, // 假设每个记录对应一次提交
              useTime,
              lastSubmitTime: item.createTime
            };

            // 更新用户总分和AC数
            const userIndex = users.findIndex(u => u.userId === userId);
            if (userIndex !== -1) {
              users[userIndex].totalScore = (users[userIndex].totalScore || 0) + (score || 0);
              if (judgeStatus === 0) { // 只有状态为0(Accepted)才计入通过数
                users[userIndex].totalAcNum = (users[userIndex].totalAcNum || 0) + 1;
              }
              users[userIndex].totalSubmitNum = (users[userIndex].totalSubmitNum || 0) + 1;
            }
          }
        });

        // 设置转换后的数据
        submitInfo.value = {
          users,
          problems,
          submissions
        };

        console.log('处理后的数据:', submitInfo.value);
      } else {
        // 如果API返回的已经是正确格式，直接使用
        submitInfo.value = response.data;
      }
    } else {
      submitInfo.value = { users: [], problems: [], submissions: {} };
    }
  } catch (error) {
    console.error('获取成绩单失败', error);
    emit('error', '获取成绩单失败，请稍后重试');
    submitInfo.value = { users: [], problems: [], submissions: {} };
  } finally {
    emit('update:loading', false);
  }
};

// 监听主题ID变化
watch(() => props.topicId, (newVal) => {
  if (newVal) {
    fetchSubmitData();
  }
}, { immediate: true });

// 监听判题类型变化
watch(() => props.topicJudgeType, () => {
  // 判题类型变化可能会影响展示，但不需要重新请求数据
});
</script>

<style scoped lang="scss">
.submit-section {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(25px);
  -webkit-backdrop-filter: blur(25px);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.05) 100%);
    pointer-events: none;
    z-index: 0;
  }

  &:hover {
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
  }

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 24px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.15);
    position: relative;
    background: linear-gradient(135deg, v-bind('headerBgColor') 0%, rgba(64, 158, 255, 0.8) 100%);
    color: white;
    z-index: 1;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(45deg, rgba(255, 255, 255, 0.08) 25%, transparent 25%, transparent 50%, rgba(255, 255, 255, 0.08) 50%, rgba(255, 255, 255, 0.08) 75%, transparent 75%, transparent);
      background-size: 12px 12px;
      opacity: 0.6;
      z-index: -1;
    }

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 1px;
      background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.3) 50%, transparent 100%);
      z-index: 1;
    }

    .title {
      display: flex;
      align-items: center;
      gap: 12px;
      font-size: 18px;
      font-weight: 700;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      position: relative;
      z-index: 2;

      .el-icon {
        font-size: 20px;
        filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1));
      }
    }

    .actions {
      display: flex;
      gap: 8px;
      position: relative;
      z-index: 1;

      .custom-button {
        padding: 8px 18px;
        border-radius: 12px;
        font-size: 14px;
        font-weight: 600;
        border: none;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        outline: none;
        min-width: 100px;
        position: relative;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        backdrop-filter: blur(10px);
        -webkit-backdrop-filter: blur(10px);

        &:hover {
          transform: translateY(-3px) scale(1.02);
          box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
        }

        &:active {
          transform: translateY(-1px) scale(0.98);
        }

        &:disabled {
          opacity: 0.6;
          cursor: not-allowed;
          transform: none;
          box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
        }

        .button-content {
          display: flex;
          align-items: center;
          justify-content: center;
          pointer-events: none; /* 防止点击事件被内部元素捕获 */
        }

        .button-icon {
          margin-right: 5px;
        }

        .button-text {
          white-space: nowrap;
        }

        .loading-spinner {
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          width: 16px;
          height: 16px;
          border: 2px solid rgba(255, 255, 255, 0.3);
          border-top: 2px solid white;
          border-radius: 50%;
          animation: spin 1s linear infinite;
        }
      }

      .warning-button {
        background: linear-gradient(135deg, #E6A23C 0%, #F7BA2A 100%);
        color: white;
        border: 1px solid rgba(255, 255, 255, 0.2);

        &:hover {
          background: linear-gradient(135deg, #ebb563 0%, #F9C74F 100%);
          box-shadow: 0 8px 20px rgba(230, 162, 60, 0.4);
        }
      }

      .success-button {
        background: linear-gradient(135deg, #67C23A 0%, #85CE61 100%);
        color: white;
        border: 1px solid rgba(255, 255, 255, 0.2);

        &:hover {
          background: linear-gradient(135deg, #85cf60 0%, #95D475 100%);
          box-shadow: 0 8px 20px rgba(103, 194, 58, 0.4);
        }
      }

      .default-button {
        background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.85) 100%);
        color: #409EFF;
        border: 1px solid rgba(64, 158, 255, 0.2);

        &:hover {
          background: linear-gradient(135deg, rgba(255, 255, 255, 1) 0%, rgba(248, 250, 255, 1) 100%);
          box-shadow: 0 8px 20px rgba(64, 158, 255, 0.3);
        }
      }
    }
  }

  .submit-content {
    padding: 0;
    max-height: 800px;
    overflow: hidden;
    transition: max-height 0.5s ease;

    &.collapsed {
      max-height: 0;
    }

    .loading-container {
      padding: 20px;
    }

    .empty-data {
      padding: 60px 0;
    }

    .submit-table-wrapper {
      max-height: 600px;

      .submit-table {
        width: 100%;

        :deep(.el-table__header) {
          th {
            background-color: #f5f7fa;
            padding: 8px 0;
          }
        }

        .user-info-cell {
          display: flex;
          align-items: center;
          gap: 10px;
          padding: 6px 0;

          .user-avatar {
            border: 2px solid white;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          }

          .user-name-wrapper {
            display: flex;
            flex-direction: column;

            .user-name {
              font-weight: 500;
              font-size: 14px;
            }

            .user-nickname {
              font-size: 12px;
              color: #909399;
            }
          }
        }

        .score-cell {
          font-weight: 600;
          font-size: 15px;
          color: #409EFF;
        }

        .ac-num-cell {
          color: #67C23A;
          font-weight: 500;
        }

        .problem-header-cell {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 4px;
          cursor: pointer;
          padding: 4px 8px;
          border-radius: 4px;
          transition: all 0.2s ease;
          width: 100%;

          &:hover {
            background-color: rgba(64, 158, 255, 0.1);
            transform: translateY(-2px);
          }

          .problem-key {
            font-weight: 600;
            font-size: 15px;
            margin-bottom: 4px;
          }

          .problem-title {
            font-size: 12px;
            color: #606266;
            max-width: 140px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            cursor: help;
          }
        }

        .problem-cell {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 4px;
          padding: 10px 4px;
          border-radius: 6px;
          transition: all 0.2s ease;
          cursor: pointer;
          min-height: 80px;
          justify-content: center;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
          }

          .problem-status {
            margin-bottom: 4px;
          }

          .problem-score {
            font-size: 14px;
            font-weight: 600;
            color: #E6A23C;
            margin-top: 4px;
          }

          .problem-time {
            font-size: 12px;
            color: #909399;
            margin-top: 2px;
          }

          &.accepted {
            background-color: rgba(103, 194, 58, 0.1);
          }

          &.wrong-answer {
            background-color: rgba(245, 108, 108, 0.1);
          }

          &.waiting, &.judging {
            background-color: rgba(144, 147, 153, 0.1);
          }

          &.compile-error, &.runtime-error, &.time-limit, &.memory-limit, &.system-error {
            background-color: rgba(230, 162, 60, 0.1);
          }

          &.no-submission {
            .problem-status.no-submit {
              color: #909399;
              font-size: 12px;
              font-style: italic;
            }
          }
        }
      }
    }
  }
}

.submit-detail-dialog, .problem-detail-dialog {
  :deep(.el-dialog__header) {
    border-bottom: 1px solid #ebeef5;
    padding-bottom: 15px;
    margin-bottom: 20px;
  }

  .detail-item {
    margin-bottom: 15px;
    display: flex;

    .detail-label {
      font-weight: 500;
      color: #606266;
      width: 80px;
      flex-shrink: 0;
    }

    .detail-value {
      flex: 1;

      &.status-1 {
        color: #67C23A;
        font-weight: 500;
      }

      &.status-2 {
        color: #F56C6C;
      }

      &.status-3, &.status-4, &.status-5, &.status-6, &.status-7 {
        color: #E6A23C;
      }
    }
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .submit-section {
    background: rgba(30, 30, 32, 0.7);
    border-color: rgba(255, 255, 255, 0.05);

    .section-header {
      border-bottom-color: rgba(255, 255, 255, 0.05);
    }

    .submit-content {
      .submit-table-wrapper {
        .submit-table {
          background-color: transparent;
          color: #e5e5e5;

          :deep(.el-table__header) {
            th {
              background-color: rgba(40, 40, 42, 0.8);
              color: #e5e5e5;
              border-color: rgba(255, 255, 255, 0.1);
            }
          }

          :deep(.el-table__body) {
            tr {
              background-color: transparent;

              td {
                background-color: transparent;
                border-color: rgba(255, 255, 255, 0.05);
              }
            }

            .el-table__row--striped {
              td {
                background-color: rgba(255, 255, 255, 0.02);
              }
            }
          }

          .user-info-cell {
            .user-avatar {
              border-color: rgba(50, 50, 54, 1);
            }

            .user-name-wrapper {
              .user-name {
                color: #e5e5e5;
              }

              .user-nickname {
                color: #aaa;
              }
            }
          }

          .problem-header-cell {
            &:hover {
              background-color: rgba(64, 158, 255, 0.15);
            }

            .problem-title {
              color: #aaa;
            }
          }

          .problem-cell {
            &:hover {
              box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
            }

            &.accepted {
              background-color: rgba(103, 194, 58, 0.15);
            }

            &.wrong-answer {
              background-color: rgba(245, 108, 108, 0.15);
            }

            &.waiting, &.judging {
              background-color: rgba(144, 147, 153, 0.15);
            }

            &.compile-error, &.runtime-error, &.time-limit, &.memory-limit, &.system-error {
              background-color: rgba(230, 162, 60, 0.15);
            }
          }
        }
      }
    }
  }

  .submit-detail-dialog, .problem-detail-dialog {
    :deep(.el-dialog) {
      background-color: #1d1e1f;

      .el-dialog__header {
        border-bottom-color: #333;
      }

      .el-dialog__title {
        color: #e5e5e5;
      }
    }

    .detail-item {
      .detail-label {
        color: #aaa;
      }

      .detail-value {
        color: #e5e5e5;
      }
    }
  }
}

@keyframes spin {
  0% { transform: translate(-50%, -50%) rotate(0deg); }
  100% { transform: translate(-50%, -50%) rotate(360deg); }
}
</style>
