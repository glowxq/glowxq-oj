<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="部门管理"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
      :pagination="false"
      :border="false"
      :default-expand-all="defaultExpandAllKey"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader>
        <el-button type="primary" v-auth="'sys.dept.create'" :icon="CirclePlus" @click="openAddEdit('新增部门')">
          新增
        </el-button>
        <el-button type="info" :icon="Sort" @click="changeExpand"> 展开/折叠 </el-button>
      </template>
      <!-- 自定义区域显示列 -->
      <template #regionName="{ row }">
        <span v-if="regionMap.get(row.regionId)">
          {{ regionMap.get(row.regionId)?.name }}
        </span>
        <span v-else class="text-gray-400">-</span>
      </template>
      <!-- 自定义联系方式显示 -->
      <template #contactInfo="{ row }">
        <div v-if="row.phone || row.email">
          <div v-if="row.phone" class="contact-item">
            <el-icon><Phone /></el-icon>
            <span>{{ row.phone }}</span>
          </div>
          <div v-if="row.email" class="contact-item">
            <el-icon><Message /></el-icon>
            <span>{{ row.email }}</span>
          </div>
        </div>
        <span v-else class="text-gray-400">-</span>
      </template>
      <template #operation="{ row }">
        <el-button v-auth="'sys.dept.create'" type="primary" link :icon="CirclePlus" @click="openAddEdit('新增部门', row)">
          新增
        </el-button>
        <el-button
          v-auth="'sys.dept.update'"
          type="primary"
          link
          v-if="row.isLock === 'F'"
          :icon="EditPen"
          @click="openAddEdit('编辑部门', row, false)"
        >
          编辑
        </el-button>
        <el-button
          v-auth="'sys.dept.remove'"
          type="primary"
          link
          v-if="row.isLock === 'F'"
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
      <template #leaderInfo="{ row }">
        <el-tag class="user-item" v-for="tag in formatLeaderInfo(row.leaderInfo)" :key="tag.id" type="info">
          {{ tag.name }}
        </el-tag>
      </template>
    </ProTable>
    <SysDeptForm ref="sysDeptRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, h } from 'vue';
import { CirclePlus, Delete, EditPen, Sort, Phone, Message } from '@element-plus/icons-vue';
// @ts-ignore
import ProTable from '@/components/Common/ProTable/index.vue';
import {
  createSysDeptApi,
  removeSysDeptApi,
  updateSysDeptApi,
  getSysDeptListApi,
  getSysDeptDetailApi
// @ts-ignore
} from '@/api/modules/system/admin/dept';
// @ts-ignore
import { getMetaRegionListAllApi } from '@/api/modules/system/meta/metaRegion';
// @ts-ignore
import { useHandleData } from '@/hooks/useHandleData';
// @ts-ignore
import SysDeptForm from '@/views/system/admin/deptManage/components/SysDeptForm.vue';
// @ts-ignore
import type { ColumnProps, ProTableInstance, SearchProps, SearchRenderScope } from '@/components/Common/ProTable/interface';
// @ts-ignore
import type { ISysDept } from '@/api/interface/system/admin/dept';
// @ts-ignore
import type { IMetaRegion } from '@/api/interface/system/meta/metaRegion';
// @ts-ignore
import RegionSelect from '@/components/Common/Meta/Region/RegionSelect.vue';
import { ElImage } from 'element-plus';

defineOptions({
  name: 'SysDeptView'
});

const proTableRef = ref<ProTableInstance>();

// 区域数据缓存
const regionMap = ref<Map<number, IMetaRegion.Row>>(new Map())
const regionOptions = ref<Array<{ label: string; value: number }>>([])

// 表格配置项
const columns: ColumnProps<ISysDept.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'name', label: '部门名称', align: 'left', width: 200 },
  { prop: 'deptNumber', label: '部门编号', width: 200 },
  { prop: 'numberPrefix', label: '编号前缀', width: 120 },
  { 
    prop: 'logo', 
    label: '部门Logo', 
    width: 100,
    render: ({ row }) => {
      return row.image ? h(ElImage, {
        src: row.image,
        style: {
          width: '60px',
          height: '60px',
          borderRadius: '4px'
        },
        previewSrcList: [row.image],
        previewTeleported: true,
        fit: 'cover',
        initialIndex: 0,
        appendToBody: true,
        hideOnClickModal: false,
        class: 'product-image'
      }) : h('div', { 
        class: 'no-image-placeholder',
        style: {
          width: '60px',
          height: '60px',
          borderRadius: '4px',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          backgroundColor: '#f5f7fa',
          color: '#909399',
          fontSize: '12px'
        }
      }, '无图片');
    }
  },
  { prop: 'content', label: '部门介绍', width: 200, align: 'center' },
  { prop: 'leaderInfo', label: '负责人', width: 150 },
  { prop: 'regionName', label: '所属区域', width: 120 },
  { prop: 'contactInfo', label: '联系方式', width: 150 },
  { prop: 'sort', label: '排序', width: 80, align: 'center' },
  { prop: 'enable', label: '启用', width: 80 },
  { prop: 'operation', label: '操作',  fixed: 'right' }
];

// 使用computed确保搜索配置能响应式更新
const searchColumns = computed<SearchProps[]>(() => {
  console.log('搜索配置更新，当前区域选项数量:', regionOptions.value.length);
  console.log('区域选项数据:', regionOptions.value);
  
  return [
    { prop: 'name', label: '部门名称', el: 'input' },
    { prop: 'deptNumber', label: '部门编号', el: 'input' },
    { 
      prop: 'regionId', 
      label: '所属区域',
      render: (scope: SearchRenderScope) => {
        return h(RegionSelect, {
          placeholder: '请选择所属区域',
          clearable: true,
          modelValue: scope.searchParam.regionId,
          'onUpdate:modelValue': (val: any) => {
            scope.searchParam.regionId = val;
          }
        });
      }
    },
    { prop: 'phone', label: '联系电话', el: 'input' },
    { prop: 'email', label: '邮箱', el: 'input' },
    { prop: 'principal', label: '主管部门', el: 'input' },
  ];
});

const defaultExpandAllKey = ref(true);

// 获取区域数据
const fetchRegionData = async () => {
  try {
    console.log('开始获取区域数据...');
    const response = await getMetaRegionListAllApi()
    const regions = response.data || []
    console.log('获取到的区域数据:', regions);
    
    // 建立区域映射
    const map = new Map<number, IMetaRegion.Row>()
    const options: Array<{ label: string; value: number }> = []
    
    // 添加"全部"选项
    options.push({
      label: '全部区域',
      value: 0
    });
    
    regions.forEach(region => {
      if (region.id && region.name) {
        map.set(region.id, region)
        options.push({
          label: region.name,
          value: region.id
        })
      }
    })
    
    regionMap.value = map
    regionOptions.value = options
    
    console.log('处理后的区域选项:', options);
    console.log('区域映射:', map);
  } catch (error) {
    console.error('获取区域数据失败:', error)
  }
}

// 获取table列表
const getTableList = (params: ISysDept.Query) => {
  let newParams = formatParams(params);
  return getSysDeptListApi(newParams);
};

const formatParams = (params: ISysDept.Query) => {
  let newParams = JSON.parse(JSON.stringify(params));
  // 如果regionId为0（全部区域），则删除该参数
  if (newParams.regionId === 0) {
    delete newParams.regionId;
  }
  return newParams;
};

// 打开 drawer(新增、查看、编辑)
const sysDeptRef = ref<InstanceType<typeof SysDeptForm>>();
const openAddEdit = async (title: string, row: any = {}, isAdd = true) => {
  let formData = {};
  if (!isAdd) {
    const record = await getSysDeptDetailApi({ id: row?.id });
    formData = record?.data;
  } else {
    let pid = row.id || 0;
    const sort = presort(row, pid);
    formData = {
      pid: pid,
      sort: sort
    };
  }
  const params = {
    title,
    row: { ...formData },
    api: isAdd ? createSysDeptApi : updateSysDeptApi,
    getTableList: proTableRef.value?.getTableList
  };
  sysDeptRef.value?.acceptParams(params);
};

// sort的预计算
const presort = (row: any = {}, pid: number) => {
  let cnt;
  // 如果选择的是根节点
  if (pid == 0) {
    cnt = proTableRef.value?.tableData?.length || 0; // 根据部门列表的长度计算sort
  } else {
    cnt = row?.children?.length || 0; // 根据选中行的children长度计算sort
  }
  return (cnt + 1) * 100;
};

// 删除信息
const deleteInfo = async (params: ISysDept.Row) => {
  await useHandleData(
    removeSysDeptApi,
    { ids: [params.id] },
    `删除【${params.name}】及以下部门（此操作不可逆请谨慎操作！！！）`,
    'error'
  );
  proTableRef.value?.getTableList();
};

const changeExpand = () => {
  defaultExpandAllKey.value = !defaultExpandAllKey.value;
  proTableRef.value?.refresh();
};

const formatLeaderInfo = (deptInfo: string): { id: string; name: string }[] => {
  if (deptInfo === null) {
    return [];
  }
  let departments: { id: string; name: string }[] = [];

  // 使用逗号分割字符串
  let departmentArray = deptInfo.split(',');

  // 遍历每个部门的键值对
  departmentArray.forEach(function (department: string) {
    // 使用冒号分割键值对
    let keyValue = department.split(':');
    // 构造部门对象
    let departmentObj = {
      id: keyValue[0],
      name: keyValue[1]
    };
    // 添加到数组
    departments.push(departmentObj);
  });
  return departments;
};

// 初始化
onMounted(async () => {
  await fetchRegionData()
})
</script>

<style scoped lang="scss">
.user-item {
  margin: 5px;
}

.text-gray-400 {
  color: #9ca3af;
}

.contact-item {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  font-size: 12px;
  
  .el-icon {
    margin-right: 4px;
    color: #409eff;
  }
}
</style>
