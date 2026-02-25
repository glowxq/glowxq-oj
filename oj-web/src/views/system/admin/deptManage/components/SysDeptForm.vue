<template>
  <el-dialog v-model="visible" :title="`${paramsProps.title}`" :destroy-on-close="true" width="900px" draggable append-to-body>
    <el-form
      ref="ruleFormRef"
      label-width="120px"
      label-suffix=" :"
      :rules="rules"
      :model="paramsProps.row"
      @submit.enter.prevent="handleSubmit"
    >
      <el-tabs tab-position="top">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息">
          <div class="section-header">
            <h3 class="section-title">基本信息</h3>
            <el-button 
              type="info" 
              size="small" 
              plain 
              @click="fillTestData"
              class="test-button"
            >
              <el-icon><MagicStick /></el-icon>
              一键测试
            </el-button>
          </div>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="上级部门" prop="pid">
                <el-tree-select
                  v-model="paramsProps.row.pid"
                  :data="parentMenus"
                  check-strictly
                  placeholder="请选择上级"
                  :render-after-expand="false"
                  clearable
                  :default-expand-all="true"
                  :props="treeProps"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="部门名称" prop="name">
                <el-input v-model="paramsProps.row.name" placeholder="请填写部门名称" clearable />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="部门编号" prop="deptNumber">
                <el-input v-model="paramsProps.row.deptNumber" placeholder="请填写部门编号" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="编号前缀" prop="numberPrefix">
                <el-input v-model="paramsProps.row.numberPrefix" placeholder="请填写编号前缀" clearable />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="所属区域" prop="regionId">
                <RegionSelect 
                  v-model="paramsProps.row.regionId" 
                  placeholder="请选择所属区域"
                  @change="handleRegionChange"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="排序" prop="sort">
                <el-input-number v-model="paramsProps.row.sort" :precision="0" :min="1" :max="999999" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-tab-pane>

        <!-- 联系信息 -->
        <el-tab-pane label="联系信息">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="部门负责人" prop="userId">
                <el-select 
                  v-model="paramsProps.row.userId" 
                  placeholder="请选择部门负责人"
                  filterable
                  clearable
                >
                  <el-option
                    v-for="leader in allLeaders"
                    :key="leader.id"
                    :label="leader.nickname"
                    :value="leader.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="paramsProps.row.phone" placeholder="请填写联系电话" clearable />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="paramsProps.row.email" placeholder="请填写邮箱" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="主管部门" prop="principal">
                <el-input v-model="paramsProps.row.principal" placeholder="请填写主管部门" clearable />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="主管部门编号" prop="principalNumber">
                <el-input v-model="paramsProps.row.principalNumber" placeholder="请填写主管部门编号" clearable />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="多人负责人" prop="leaderIds">
            <el-tag class="user-item" v-for="tag in leaders" :key="tag.id" type="info" closable @close="removeUser(tag.id)">
              {{ tag.nickname }}
            </el-tag>
            <el-button @click="chooseUser(paramsProps.row)"> 选择多人负责人 </el-button>
          </el-form-item>
        </el-tab-pane>

        <!-- 多媒体与其他 -->
        <el-tab-pane label="其他信息">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="部门Logo" prop="logo">
                <UploadImg 
                  :image-url="paramsProps.row.logo || ''"
                  height="120"
                  width="120"
                  :limit="1"
                  :use-oss="true"
                  dir="dept/logo"
                  :file-size="5"
                  @update:imageUrl="(url) => paramsProps.row.logo = url"
                >
                  <template #tip>
                    <div>建议上传120x120像素的图片，支持jpg、png格式，大小不超过5MB</div>
                  </template>
                </UploadImg>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="部门图片" prop="image">
                <UploadImg 
                  :image-url="paramsProps.row.image || ''"
                  height="120"
                  width="200"
                  :limit="1"
                  :use-oss="true"
                  dir="dept/image"
                  :file-size="10"
                  @update:imageUrl="(url) => paramsProps.row.image = url"
                >
                  <template #tip>
                    <div>建议上传16:9比例的图片，支持jpg、png格式，大小不超过10MB</div>
                  </template>
                </UploadImg>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="备注" prop="remark">
            <el-input 
              v-model="paramsProps.row.remark" 
              placeholder="请填写备注" 
              type="textarea" 
              :rows="3"
              clearable 
            />
          </el-form-item>

          <el-form-item label="部门介绍" prop="content">
            <MarkdownEditor
              v-model="paramsProps.row.content"
              :height="400"
              placeholder="请填写部门介绍，支持Markdown格式"
              :use-oss="true"
              img-dir="dept/content"
              @change="handleContentChange"
            />
          </el-form-item>
        </el-tab-pane>
      </el-tabs>
    </el-form>
    <template #footer>
      <el-button @click="visible = false"> 取消 </el-button>
      <el-button type="primary" @click="handleSubmit"> 确定 </el-button>
    </template>
  </el-dialog>

  <DeptLeaderTransfer ref="deptLeaderTransferRef" v-model="selectLeaderIds" />
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { type ElForm, ElMessage } from 'element-plus';
import { MagicStick } from '@element-plus/icons-vue';
import type { ISysDept } from '@/api/interface/system/admin/dept';
import { getMenuTree, getSysDeptLeaderApi } from '@/api/modules/system/admin/dept';
import DeptLeaderTransfer from '@/views/system/admin/deptManage/components/DeptLeaderTransfer.vue';
import RegionSelect from '@/components/Common/Meta/Region/RegionSelect.vue';
// @ts-ignore
import UploadImg from '@/components/Common/Upload/Img.vue';
// @ts-ignore
import MarkdownEditor from '@/components/Common/Markdown/index.vue';

defineOptions({
  name: 'SysDeptForm'
});

const parentMenus = ref<ISysDept.Tree[]>([]);
const treeProps = {
  label: 'name',
  value: 'id'
};

const deptLeaderTransferRef = ref<InstanceType<typeof DeptLeaderTransfer>>();
const allLeaders = ref<ISysDept.Leader[]>([]);
const selectLeaderIds = ref<number[]>([]);
const leaders = computed(() => {
  return allLeaders.value.filter(item => selectLeaderIds.value.includes(item.id));
});

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params;
  selectLeaderIds.value = paramsProps.value.row?.leaders || [];
  visible.value = true;
  loadParentMenus();
  loadLeaderInfo();
  
  // 调试信息：打印接收到的regionId
  console.log('接收到的数据:', paramsProps.value.row);
  console.log('regionId:', paramsProps.value.row?.regionId, typeof paramsProps.value.row?.regionId);
};

const loadParentMenus = () => {
  getMenuTree({ excludeNodeId: paramsProps.value.row?.id }).then(res => {
    parentMenus.value = res.data;
  });
};

const loadLeaderInfo = () => {
  getSysDeptLeaderApi().then(res => {
    allLeaders.value = res.data.leaderInfoVOS;
  });
};

// 处理区域变化事件
const handleRegionChange = (value: string | number | null) => {
  console.log('区域选择变化:', value, typeof value);
  // 确保regionId正确设置
  if (value !== null && value !== undefined) {
    paramsProps.value.row.regionId = typeof value === 'string' ? Number(value) : Number(value);
  } else {
    paramsProps.value.row.regionId = undefined;
  }
  console.log('设置后的regionId:', paramsProps.value.row.regionId);
};

// 处理内容变化事件
const handleContentChange = (value: string) => {
  console.log('部门介绍内容变化:', value);
  paramsProps.value.row.content = value;
};

// 一键填充测试数据
const fillTestData = () => {
  const timestamp = Date.now();
  
  // 测试图片URL数组
  const testLogos = [
    'https://picsum.photos/120/120?random=1',
    'https://picsum.photos/120/120?random=2',
    'https://picsum.photos/120/120?random=3',
    'https://picsum.photos/120/120?random=4',
    'https://picsum.photos/120/120?random=5'
  ];
  
  const testImages = [
    'https://picsum.photos/320/180?random=10',
    'https://picsum.photos/320/180?random=11',
    'https://picsum.photos/320/180?random=12',
    'https://picsum.photos/320/180?random=13',
    'https://picsum.photos/320/180?random=14'
  ];
  
  // 随机选择测试图片
  const randomLogo = testLogos[Math.floor(Math.random() * testLogos.length)];
  const randomImage = testImages[Math.floor(Math.random() * testImages.length)];
  
  // 填充基本信息
  paramsProps.value.row.name = `测试部门-${timestamp}`;
  paramsProps.value.row.deptNumber = `DEPT-${timestamp}`;
  paramsProps.value.row.numberPrefix = 'TEST';
  paramsProps.value.row.sort = 1;
  
  // 填充联系信息
  paramsProps.value.row.phone = '13800138000';
  paramsProps.value.row.email = 'test@example.com';
  paramsProps.value.row.principal = '测试主管部门';
  paramsProps.value.row.principalNumber = `PRIN-${timestamp}`;
  
  // 填充图片信息
  paramsProps.value.row.logo = randomLogo;
  paramsProps.value.row.image = randomImage;
  
  // 填充其他信息
  paramsProps.value.row.remark = '这是一个测试部门，用于验证系统功能';
  paramsProps.value.row.content = `# 测试部门介绍

这是一个测试部门，主要用于系统功能验证。

![部门示意图](${randomImage})

## 主要职责
- 测试功能验证
- 系统稳定性检查
- 数据完整性校验
- 用户体验优化

## 部门特色
- 专业的测试团队
- 完善的测试流程
- 先进的测试工具
- 持续的质量改进

## 联系方式
- 📞 电话：13800138000
- 📧 邮箱：test@example.com
- 🏢 主管部门：测试主管部门`;
  
  ElMessage.success('测试数据填充完成（包含随机测试图片）');
};

const rules = reactive({
  name: [{ required: true, message: '请填写部门名称' }],
  pid: [{ required: true, message: '请选择上级部门' }],
  deptNumber: [{ required: true, message: '请填写部门编号' }],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
});

const visible = ref(false);
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
});

// 提交数据（新增/编辑）
const ruleFormRef = ref<InstanceType<typeof ElForm>>();
const handleSubmit = () => {
  ruleFormRef.value!.validate(async valid => {
    if (!valid) return;
    try {
      // 调试信息：提交前打印所有数据
      console.log('提交前的完整数据:', paramsProps.value.row);
      console.log('提交前的regionId:', paramsProps.value.row.regionId, typeof paramsProps.value.row.regionId);
      
      // 确保regionId和userId为数字类型
      if (paramsProps.value.row.regionId !== null && paramsProps.value.row.regionId !== undefined) {
        if (typeof paramsProps.value.row.regionId === 'string') {
          paramsProps.value.row.regionId = Number(paramsProps.value.row.regionId);
        }
      }
      if (paramsProps.value.row.userId && typeof paramsProps.value.row.userId === 'string') {
        paramsProps.value.row.userId = Number(paramsProps.value.row.userId);
      }
      
      // 设置多人负责人
      paramsProps.value.row.leaders = selectLeaderIds.value;
      
      // 调试信息：最终提交的数据
      console.log('最终提交的数据:', paramsProps.value.row);
      
      await paramsProps.value.api!(paramsProps.value.row);
      ElMessage.success({ message: `${paramsProps.value.title}成功！` });
      paramsProps.value.getTableList!();
      visible.value = false;
    } catch (error) {
      console.error('提交失败:', error);
    }
  });
};

// 选择人员
const chooseUser = (row = {}) => {
  const params = {
    title: '设置多人负责人',
    row: row,
    api: undefined,
    leaderList: allLeaders.value,
    selectIds: selectLeaderIds.value
  };
  deptLeaderTransferRef.value?.acceptParams(params);
};

// 移除人员
const removeUser = (id: number) => {
  const index = selectLeaderIds.value.findIndex(item => item === id);
  if (index !== -1) {
    selectLeaderIds.value.splice(index, 1);
  }
};

defineExpose({
  acceptParams
});
</script>

<style scoped lang="scss">
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  letter-spacing: -0.022em;
}

.test-button {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
}

.user-item {
  margin: 5px;
}

:deep(.el-dialog__body) {
  padding: 20px 20px 0 20px;
}

:deep(.el-tabs__content) {
  padding-top: 20px;
}

// 上传组件样式调整
:deep(.upload-box) {
  .el-upload__tip {
    margin-top: 8px;
    font-size: 12px;
    color: var(--el-text-color-regular);
    line-height: 1.5;
  }
}
</style>
