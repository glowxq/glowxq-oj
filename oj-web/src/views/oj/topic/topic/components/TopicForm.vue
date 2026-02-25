<template>
  <el-dialog
    v-model="visible"
    :title="`${paramsProps.title}`"
    :destroy-on-close="true"
    width="90%"
    fullscreen
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
      <!-- 主题类型选择（优先选择） -->
      <el-divider content-position="left">
        <div class="divider-content">
          <el-icon><InfoFilled /></el-icon>
          <span>主题类型选择</span>
        </div>
      </el-divider>

      <el-form-item :label="`主题类型`" prop="topicType" required>
        <enum-select
          v-model="paramsProps.row.topicType"
          :enum-data="TopicType"
          :disabled="isEdit"
          type="tab"
          placeholder="请选择主题类型"
          @change="handleTopicTypeChange"
        />
        <div class="form-tip" v-if="typeInfo">
          <el-icon><InfoFilled /></el-icon>
          <span>{{ typeInfo }}</span>
        </div>
      </el-form-item>

      <!-- 基本信息部分 -->
      <el-divider content-position="left">
        <div class="divider-content">
          <el-icon><InfoFilled /></el-icon>
          <span>基本信息</span>
        </div>
      </el-divider>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="topicNameLabel" prop="name">
        <el-input
          v-model="paramsProps.row.name"
              :placeholder="`请填写${topicNameLabel}`"
          clearable
        ></el-input>
      </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="`${typeTextShort}代码`" prop="code">
            <div class="input-group">
        <el-input
          v-model="paramsProps.row.code"
                :placeholder="`请填写${typeTextShort}代码`"
          clearable
        ></el-input>
              <el-button type="primary" plain @click="generateRandomCode">
                随机生成
              </el-button>
            </div>
      </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="负责人" prop="principalUserId">
            <user-select
              v-model="paramsProps.row.principalUserId"
              @change="handleUserChange"
            />
      </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="`${typeTextShort}密码`" prop="password">
            <div class="input-group">
        <el-input
          v-model="paramsProps.row.password"
                :placeholder="`可选，非空时需密码访问`"
          clearable
                type="password"
                show-password
        ></el-input>
              <el-button type="primary" plain @click="generateRandomPassword">
                随机生成
              </el-button>
            </div>
            <div class="form-tip">
              <el-icon><Lock /></el-icon>
              <span>{{ passwordTip }}</span>
            </div>
      </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="`${typeTextShort}颜色`" prop="color">
            <color-select
              v-model="paramsProps.row.color"
              v-model:text-color="paramsProps.row.textColor"
              :show-preview="true"
              :preview-text="paramsProps.row.name || typeTextShort"
            />
      </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="公共主题" prop="common">
            <block-button-switch
              v-model="paramsProps.row.common"
              :text="`公共${typeTextShort}`"
              icon="el-icon-share"
              @update:model-value="handleCommonChange"
            />
            <div class="form-tip">若设为公共{{ typeTextShort }}，则所有用户可见</div>
      </el-form-item>
        </el-col>
      </el-row>

      <el-form-item :label="`${typeTextShort}描述`" prop="description">
        <Markdown
          v-model="paramsProps.row.description"
          :height="300"
          :placeholder="`请填写${typeTextShort}描述信息`"
          preview-theme="github"
          code-theme="atom-one-light"
        />
      </el-form-item>

      <!-- 时间与设置部分 -->
      <el-divider content-position="left">
        <div class="divider-content">
          <el-icon><Timer /></el-icon>
          <span>时间与设置</span>
        </div>
      </el-divider>

      <el-row :gutter="20">
        <el-col :span="12">
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker clearable
          v-model="paramsProps.row.startTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
              placeholder="请选择开始时间"
              style="width: 100%"
            ></el-date-picker>
      </el-form-item>
        </el-col>
        <el-col :span="12">
      <el-form-item label="截止时间" prop="endTime">
        <el-date-picker clearable
          v-model="paramsProps.row.endTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
              placeholder="请选择截止时间"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="时间范围策略" prop="timeRangeType">
            <div class="strategy-container">
              <enum-select
                v-model="paramsProps.row.timeRangeType"
                :enum-data="TimeRangeType"
                type="select"
                placeholder="请选择时间范围策略"
                @change="handleTimeRangeTypeChange"
              />
            </div>
            <div class="form-tip" v-if="strategyInfo">
              <el-icon><Timer /></el-icon>
              <span>{{ strategyInfo }}</span>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="OI得分类型" prop="oiScoreType">
            <enum-select
              v-model="paramsProps.row.oiScoreType"
              :enum-data="OiScoreType"
              type="select"
              placeholder="请选择OI得分类型"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="判题模式" prop="topicJudgeType">
            <enum-select
              v-model="paramsProps.row.topicJudgeType"
              :enum-data="TopicJudgeType"
              type="tab"
              placeholder="请选择判题模式"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="启用" prop="enable">
            <block-button-switch
              v-model="paramsProps.row.enable"
              :text="`启用${typeTextShort}`"
              icon="el-icon-check"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="首页展示" prop="homeShow">
            <block-button-switch
              v-model="paramsProps.row.homeShow"
              :text="`首页展示${typeTextShort}`"
              icon="el-icon-star"
            />
            <div class="form-tip">开启后此{{ typeTextShort }}将在系统首页进行展示</div>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="公共主题" prop="common">
            <block-button-switch
              v-model="paramsProps.row.common"
              :text="`公共${typeTextShort}`"
              icon="el-icon-share"
              @update:model-value="handleCommonChange"
            />
            <div class="form-tip">若设为公共{{ typeTextShort }}，则所有用户可见</div>
      </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :xs="24" :sm="8" :md="8">
          <el-form-item label="提前封榜(时)" prop="sealedTime" class="number-item">
            <el-tooltip content="-1为全程封榜 0为不封榜" placement="top">
              <el-input-number v-model="paramsProps.row.sealedTime" :precision="0" :min="-1" :max="999" class="full-width" />
            </el-tooltip>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="8" :md="8">
          <el-form-item label="时间限制(分)" prop="timeLimit" class="number-item">
            <el-tooltip content="超时自动提交" placement="top">
              <el-input-number v-model="paramsProps.row.timeLimit" :precision="0" :min="0" :max="999" class="full-width" />
            </el-tooltip>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="8" :md="8">
          <el-form-item label="罚时(分)" prop="punishmentTime" class="number-item">
            <el-input-number v-model="paramsProps.row.punishmentTime" :precision="0" :min="0" :max="999" class="full-width" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 资源与设置部分 -->
      <el-divider content-position="left">
        <div class="divider-content">
          <el-icon><Setting /></el-icon>
          <span>资源与设置</span>
        </div>
      </el-divider>

      <el-form-item label="所属班级" prop="groupIds" :required="!paramsProps.row.common">
        <group-select
          v-model="paramsProps.row.groupIds"
          @change="handleGroupChange"
          multiple
        />
        <div class="form-tip">不选择班级则默认设置为公共{{ typeTextShort }}</div>
      </el-form-item>

      <el-form-item label="标签" prop="tagIds">
        <tag-select
          v-model="paramsProps.row.tagIds"
          multiple
        />
      </el-form-item>

      <el-form-item label="题目" prop="problemIds">
        <problem-select
          v-model="paramsProps.row.problemIds"
          multiple
          :preload-options="paramsProps.row.problemList"
        />
      </el-form-item>

      <!-- 承诺书部分 -->
      <el-divider content-position="left">
        <div class="divider-content">
          <el-icon><Document /></el-icon>
          <span>承诺书</span>
        </div>
      </el-divider>

      <el-form-item label="承诺书启用" prop="agreementEnabled">
        <block-button-switch
          v-model="paramsProps.row.agreementEnabled"
          text="启用承诺书"
          icon="el-icon-check"
        />
      </el-form-item>

      <el-form-item v-if="paramsProps.row.agreementEnabled" label="承诺书内容" prop="agreementContent">
        <Markdown
          v-model="paramsProps.row.agreementContent"
          :height="300"
          placeholder="请填写承诺书内容，学员首次进入时将需要同意此承诺书"
          preview-theme="github"
          code-theme="atom-one-light"
          :maxLength="2000"
          showWordCount
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false" class="cancel-button">取消</el-button>
        <el-button type="warning" @click="fillTestData" class="test-button" plain>🧪 一键测试</el-button>
        <el-button type="primary" @click="handleSubmit" class="confirm-button">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'
import { InfoFilled, Setting, Timer, Document, Lock } from '@element-plus/icons-vue'
import GroupSelect from '@/components/Oj/Group/GroupSelect.vue'
import UserSelect from '@/components/Common/User/UserSelect.vue'
import TagSelect from '@/components/Common/Meta/Tag/TagSelect.vue'
import ProblemSelect from '@/components/Oj/Problem/ProblemSelect.vue'
import ColorSelect from '@/components/Common/Color/ColorSelect.vue'
import BlockButtonSwitch from '@/components/Common/Base/BlockButtonSwitch.vue'
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue'
import Markdown from '@/components/Common/Markdown/index.vue'
import { TimeRangeType } from '@/enums/oj/topic/TimeRangeType'
import { TopicType } from '@/enums/oj/topic/TopicType'
import { TopicJudgeType } from '@/enums/oj/topic/TopicJudgeType'
import { OiScoreType } from '@/enums/oj/topic/OiScoreType'

defineOptions({
    name: 'TopicForm'
})

// 当前选中的主题类型信息
const currentTypeInfo = ref<any>(null);
const currentTimeRangeInfo = ref<any>(null);

// 主题类型相关的计算属性
const typeText = computed(() => {
  // 如果存在选择的类型，则返回类型名称，否则返回"主题"
  return currentTypeInfo.value ? currentTypeInfo.value.name : '主题';
});

// 简短版本的类型文本（用于复合词中）
const typeTextShort = computed(() => {
  return typeText.value;
});

// 主题类型的描述信息
const typeInfo = computed(() => {
  return currentTypeInfo.value ? currentTypeInfo.value.tooltip : '请选择主题类型';
});

// 主题名称标签
const topicNameLabel = computed(() => {
  return `${typeText.value}名`;
});

// 密码提示信息
const passwordTip = computed(() => {
  if (typeText.value === '比赛') {
    return '设置比赛密码后，参与者需要输入密码才能访问比赛内容及提交解答';
  } else if (typeText.value === '作业') {
    return '设置密码后，学生需要输入密码才能访问作业内容及提交作业';
  } else {
    return '设置密码后，用户需要输入密码才能访问此内容';
  }
});

// 时间范围策略的说明
const strategyInfo = computed(() => {
  return currentTimeRangeInfo.value ? currentTimeRangeInfo.value.text : '请选择时间范围策略';
});

// 是否是编辑模式
const isEdit = computed(() => {
  return !!paramsProps.value.row.id;
});

// 处理主题类型变化
const handleTopicTypeChange = (selectedEnum: any) => {
  // 保存当前选中的主题类型信息，用于后续展示
  currentTypeInfo.value = selectedEnum;
};

// 处理时间范围类型变化
const handleTimeRangeTypeChange = (selectedEnum: any) => {
  // 保存当前选中的时间范围类型信息，用于后续展示
  currentTimeRangeInfo.value = selectedEnum;
};

// 主题类型变化事件处理
const onTypeChange = (value: string) => {
  if (!value) return;
  // 根据选择的代码查找完整的枚举信息
  const typeInfo = Object.values(TopicType).find(item => item.code === value);
  currentTypeInfo.value = typeInfo;
};

// 时间范围策略变化事件处理
const onTimeRangeStrategyChange = (value: string) => {
  if (!value) return;
  // 根据选择的代码查找完整的枚举信息
  const strategyInfo = Object.values(TimeRangeType).find(item => item.code === value);
  currentTimeRangeInfo.value = strategyInfo;
};

const rules = reactive({
  name: [{ required: true, message: '请填写名称' }],
  code: [{ required: true, message: '请填写代码' }],
  topicType: [{ required: true, message: '请选择主题类型' }],
  groupIds: [{
    required: false,
    validator: (rule: any, value: any, callback: any) => {
      if (!paramsProps.value.row.common && (!value || value.length === 0)) {
        callback(new Error('非公共主题必须选择班级'));
      } else {
        callback();
      }
    }
  }],
  principalUserId: [{ required: true, message: '请选择负责人' }],
  password: [{ required: false, message: '请填写密码' }],
  description: [{ required: false, message: '请填写描述' }],
  timeRangeType: [{ required: true, message: '请选择时间范围策略' }],
  topicJudgeType: [{ required: true, message: '请选择判题模式' }],
  oiScoreType: [{ required: true, message: '请选择OI得分类型' }],
  color: [{ required: false, message: '请填写颜色' }],
  startTime: [{ required: true, message: '请选择开始时间' }],
  endTime: [{ required: true, message: '请选择截止时间' }],
  agreementContent: [{
    required: true,
    message: '请填写承诺书内容',
    trigger: 'blur',
    validator: (rule: any, value: any, callback: any) => {
      if (paramsProps.value.row.agreementEnabled && (!value || value.trim() === '')) {
        callback(new Error('启用承诺书时，必须填写承诺书内容'))
      } else {
        callback()
      }
    }
  }],
})

const visible = ref(false)
const paramsProps = ref<any>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
})

// 监视班级选择变化，如果班级为空则自动设置为公共主题
watch(() => paramsProps.value.row.groupIds, (newVal) => {
  if (!newVal || newVal.length === 0) {
    paramsProps.value.row.common = true;
  }
}, { deep: true })

// 处理公共主题状态变化
const handleCommonChange = (isCommon: boolean | null) => {
  if (isCommon) {
    // 如果设置为公共主题，清空班级选择
    paramsProps.value.row.groupIds = [];
  }
}

// 接收父组件传过来的参数
const acceptParams = (params: any) => {
  paramsProps.value = params

  // 确保字段初始化
  initFormData()

  visible.value = true
}

// 初始化表单数据
const initFormData = () => {
  // 从groupList提取ID填充到groupIds
  if (paramsProps.value.row.groupList && paramsProps.value.row.groupList.length > 0) {
    paramsProps.value.row.groupIds = paramsProps.value.row.groupList.map((item: any) => item.id)
  } else if (!paramsProps.value.row.groupIds) {
    paramsProps.value.row.groupIds = []
  }

  // 从tagList提取ID填充到tagIds
  if (paramsProps.value.row.tagList && paramsProps.value.row.tagList.length > 0) {
    paramsProps.value.row.tagIds = paramsProps.value.row.tagList.map((item: any) => item.id)
  } else if (!paramsProps.value.row.tagIds) {
    paramsProps.value.row.tagIds = []
  }

  // 从problemList提取ID填充到problemIds
  if (paramsProps.value.row.problemList && paramsProps.value.row.problemList.length > 0) {
    console.log('问题列表信息:', JSON.stringify(paramsProps.value.row.problemList))
    paramsProps.value.row.problemIds = paramsProps.value.row.problemList.map((item: any) => item.id)
  } else if (!paramsProps.value.row.problemIds) {
    paramsProps.value.row.problemIds = []
  }

  // 设置默认颜色
  if (!paramsProps.value.row.color) {
    paramsProps.value.row.color = '#1890FF'
  }
  if (!paramsProps.value.row.textColor) {
    paramsProps.value.row.textColor = '#FFFFFF'
  }

  // 设置开关默认值
  if (paramsProps.value.row.common === undefined) {
    paramsProps.value.row.common = false

    // 如果没有设置班级，默认为公共主题
    if (!paramsProps.value.row.groupIds || paramsProps.value.row.groupIds.length === 0) {
      paramsProps.value.row.common = true
    }
  }

  if (paramsProps.value.row.enable === undefined) {
    paramsProps.value.row.enable = true
  }
  if (paramsProps.value.row.agreementEnabled === undefined) {
    paramsProps.value.row.agreementEnabled = false
  }

  // 设置首页展示默认值
  if (paramsProps.value.row.homeShow === undefined) {
    paramsProps.value.row.homeShow = false
  }

  // 设置默认罚时和时间限制
  if (paramsProps.value.row.punishmentTime === undefined) {
    paramsProps.value.row.punishmentTime = 0
  }
  if (paramsProps.value.row.timeLimit === undefined) {
    paramsProps.value.row.timeLimit = 0
  }
  if (paramsProps.value.row.sealedTime === undefined) {
    paramsProps.value.row.sealedTime = 0
  }

  // 如果是新增，默认生成随机代码和密码
  if (!paramsProps.value.row.id) {
    if (!paramsProps.value.row.code) {
      generateRandomCode()
    }
    if (!paramsProps.value.row.agreementContent) {
      paramsProps.value.row.agreementContent = `我承诺遵守以下规则：
1. 不抄袭他人代码，独立完成所有题目
2. 不分享题目或题解给他人
3. 在规定时间内完成主题内容
4. 遵守学校和教师的相关规定

我已阅读以上内容，并同意遵守上述规则。`
    }
  }

  // 初始化主题类型信息
  if (paramsProps.value.row.topicType) {
    // 根据主题类型代码获取详细信息
    const typeInfo = Object.values(TopicType).find(item => item.code === paramsProps.value.row.topicType);
    if (typeInfo) {
      currentTypeInfo.value = typeInfo;
    }
  }

  // 初始化时间范围类型信息
  if (paramsProps.value.row.timeRangeType) {
    // 根据时间范围类型代码获取详细信息
    const timeRangeInfo = Object.values(TimeRangeType).find(item => item.code === paramsProps.value.row.timeRangeType);
    if (timeRangeInfo) {
      currentTimeRangeInfo.value = timeRangeInfo;
    }
  }
}

// 生成随机密码，只包含字母和数字，全大写
const generateRandomPassword = () => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
  let password = ''
  for (let i = 0; i < 6; i++) {
    password += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  paramsProps.value.row.password = password
  ElMessage.success('已生成随机密码')
}

// 生成随机主题代码，只包含字母和数字，全大写
const generateRandomCode = () => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
  let code = 'TOPIC'
  for (let i = 0; i < 4; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  paramsProps.value.row.code = code
  ElMessage.success('已生成随机主题代码')
}

// 处理班级选择变化
const handleGroupChange = (value: number[]) => {
  if (!value || value.length === 0) {
    // 如果没有选择班级，自动设置为公共主题
    paramsProps.value.row.common = true;
  } else {
    // 如果选择了班级，则不是公共主题
    paramsProps.value.row.common = false;
  }
}

// 处理用户选择变化
const handleUserChange = (value: number, selectedUser: any) => {
  if (selectedUser && selectedUser.name) {
    paramsProps.value.row.principalName = selectedUser.name
  }
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

// 生成随机测试数据
const generateTestData = () => {
  const randomId = Math.floor(Math.random() * 10000);
  const now = new Date();
  const startTime = new Date(now.getTime() + 24 * 60 * 60 * 1000); // 明天开始
  const endTime = new Date(startTime.getTime() + 7 * 24 * 60 * 60 * 1000); // 一周后结束

  return {
    topicType: 'CONTEST', // 比赛类型
    name: `测试主题${randomId}`,
    code: `TOPIC${randomId}`,
    // principalUserId: 1, // 默认用户ID
    password: `PWD${randomId}`,
    color: '#1890FF',
    textColor: '#FFFFFF',
    common: false,
    description: `# 测试主题${randomId}\n\n这是一个自动生成的测试主题，包含以下内容：\n\n## 主题说明\n- 这是测试数据\n- 请在实际使用前修改\n- 包含基本的markdown格式\n\n## 注意事项\n1. 请注意时间设置\n2. 确认参与班级\n3. 检查题目配置`,
    startTime: startTime.toISOString().slice(0, 19).replace('T', ' '),
    endTime: endTime.toISOString().slice(0, 19).replace('T', ' '),
    // timeRangeType: 'DURING',
    // oiScoreType: 'LAST',
    // topicJudgeType: 'ACM',
    enable: true,
    homeShow: false,
    sealedTime: 0,
    timeLimit: 0,
    punishmentTime: 0,
    // groupIds: [1], // 默认班级ID
    // tagIds: [1, 2], // 默认标签ID
    // problemIds: [1, 2, 3], // 默认题目ID
    agreementEnabled: true,
    agreementContent: `我承诺遵守以下规则：
1. 不抄袭他人代码，独立完成所有题目
2. 不分享题目或题解给他人
3. 在规定时间内完成主题内容
4. 遵守学校和教师的相关规定

我已阅读以上内容，并同意遵守上述规则。`
  };
};

// 一键填充测试数据
const fillTestData = () => {
  const testData = generateTestData();
  Object.assign(paramsProps.value.row, testData);

  // 更新主题类型信息
  const typeInfo = Object.values(TopicType).find(item => item.code === testData.topicType);
  if (typeInfo) {
    currentTypeInfo.value = typeInfo;
  }

  ElMessage.success('测试数据已填充！');
};

defineExpose({
  acceptParams
})
</script>

<style scoped lang="scss">
.input-group {
  display: flex;
  align-items: center;
  width: 100%;

  .el-input {
    flex: 1;
    margin-right: 12px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding: 20px 0;

  .el-button + .el-button {
    margin-left: 16px;
  }

  .cancel-button {
    min-width: 80px;
    border-color: rgba(0, 0, 0, 0.08);
    color: #464646;
    font-weight: 500;

    &:hover {
      background-color: rgba(0, 0, 0, 0.03);
      border-color: rgba(0, 0, 0, 0.12);
    }
  }

  .test-button {
    min-width: 80px;
    font-weight: 500;
    border-color: var(--el-color-warning);
    color: var(--el-color-warning);

    &:hover {
      background-color: var(--el-color-warning);
      border-color: var(--el-color-warning);
      color: white;
    }
  }

  .confirm-button {
    min-width: 80px;
    font-weight: 500;

    &:hover {
      filter: brightness(1.05);
    }
  }
}

:deep(.el-dialog) {
  display: flex;
  flex-direction: column;
  margin: 0 !important;
  max-width: 100%;
  max-height: 100%;
  height: 100%;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif;

  .el-dialog__header {
    padding: 16px 24px;
    margin: 0;
    border-bottom: 1px solid #f0f0f0;
    background-color: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border-radius: 8px 8px 0 0;
    box-shadow: 0 1px 0 rgba(0, 0, 0, 0.05);

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: #1d1d1f;
    }
  }

  .el-dialog__body {
    flex: 1;
    overflow-y: auto;
    padding: 32px 40px;
    background-color: #ffffff;
  }

  .el-dialog__footer {
    padding: 16px 24px;
    margin: 0;
    border-top: 1px solid #f0f0f0;
    background-color: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border-radius: 0 0 8px 8px;
    box-shadow: 0 -1px 0 rgba(0, 0, 0, 0.05);

    .dialog-footer {
      margin: 0;
      padding: 0;
    }
  }
}

:deep(.el-divider) {
  margin: 36px 0 24px;
  border-top-color: rgba(0, 0, 0, 0.08);
}

:deep(.el-divider__text) {
  font-weight: 600;
  color: var(--el-color-primary);
  font-size: 16px;
  background-color: #ffffff;
  padding: 0 16px;
}

.divider-content {
  display: flex;
  align-items: center;
  gap: 8px;

  .el-icon {
    font-size: 16px;
  }
}

:deep(.el-form) {
  max-width: 1400px;
  margin: 0 auto;
}

:deep(.el-form-item) {
  margin-bottom: 24px;

  .el-form-item__label {
    font-weight: 500;
    color: #1d1d1f;
    font-size: 14px;
  }

  .el-input__wrapper,
  .el-select__wrapper {
    border-radius: 8px;
    box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1);

    &:hover {
      box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.2);
    }

    &.is-focus {
      box-shadow: 0 0 0 2px var(--el-color-primary);
    }
  }

  .el-button {
    border-radius: 8px;
    font-weight: 500;

    &.is-plain {
      border-color: rgba(0, 0, 0, 0.1);
    }
  }
}

:deep(.el-row) {
  margin-bottom: 16px;
}

.form-tip {
  font-size: 13px;
  color: #6e6e73;
  margin-top: 8px;
  line-height: 1.4;
  display: flex;
  align-items: flex-start;
  gap: 8px;

  .el-icon {
    font-size: 14px;
    color: var(--el-color-primary);
    margin-top: 2px;
  }
}

// 修复数字输入框显示问题
.number-item {
  margin-bottom: 20px;

  :deep(.el-form-item__label) {
    width: 140px !important;
    white-space: nowrap;
  }

  .full-width {
    width: 100%;
  }

  @media (max-width: 768px) {
    :deep(.el-form-item__label) {
      width: 100% !important;
      text-align: left;
      float: none;
      display: block;
      margin-bottom: 8px;
    }

    :deep(.el-form-item__content) {
      margin-left: 0 !important;
    }
  }
}

.strategy-container {
  display: flex;
  align-items: flex-start;
  width: 100%;
}
</style>
