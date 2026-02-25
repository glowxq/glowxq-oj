<template>
  <div class="card filter">
    <h4 v-if="title" class="title sle">
      {{ title }}
      <el-tooltip effect="dark" content="数量仅展示直属部门即当前节点直接下级，不包含其后代" placement="top">
        <i :class="'iconfont icon-yiwen'" />
      </el-tooltip>
    </h4>
    <el-input v-model="filterText" placeholder="输入关键字进行过滤" clearable />
    <el-scrollbar :style="{ height: title ? `calc(100% - 95px)` : `calc(100% - 56px)` }">
      <el-tree
        v-if="reload"
        ref="treeRef"
        default-expand-all
        :node-key="id"
        :data="multiple ? treeData : treeAllData"
        :show-checkbox="multiple"
        :check-strictly="false"
        :current-node-key="!multiple ? selected : ''"
        :highlight-current="!multiple"
        :expand-on-click-node="false"
        :check-on-click-node="multiple"
        :props="defaultProps"
        :filter-node-method="filterNode"
        :default-checked-keys="multiple ? selected : []"
        @node-click="handleNodeClick"
        @check="handleCheckChange"
        v-loading="loading"
      >
        <template #default="scope">
          <span class="el-tree-node__label">
            <slot :row="scope">
              {{ scope.node.label }}
              <span class="el-tree-node__label info-count">
                <slot :row="scope"> （{{ scope.data.userTotal }}） </slot>
              </span>
            </slot>
          </span>
        </template>
      </el-tree>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onBeforeMount, nextTick } from 'vue';
import { ElTree } from 'element-plus';

defineOptions({
  name: 'DeptTree'
});

// 接收父组件参数并设置默认值
interface DeptTreeProps {
  requestApi?: (data?: any) => Promise<any>; // 请求分类数据的 api ==> 非必传
  data?: { [key: string]: any }[]; // 分类数据，如果有分类数据，则不会执行 api 请求 ==> 非必传
  title?: string; // DeptTree 标题 ==> 非必传
  id?: string; // 选择的id ==> 非必传，默认为 “id”
  label?: string; // 显示的label ==> 非必传，默认为 “label”
  multiple?: boolean; // 是否为多选 ==> 非必传，默认为 false
  defaultValue?: any; // 默认选中的值 ==> 非必传
}
const loading = ref(false);

const props = withDefaults(defineProps<DeptTreeProps>(), {
  id: 'id',
  label: 'label',
  multiple: false
});

const defaultProps = {
  children: 'children',
  label: props.label
};

const treeRef = ref<InstanceType<typeof ElTree>>();
const treeData = ref<{ [key: string]: any }[]>([]);
const treeAllData = ref<{ [key: string]: any }[]>([]);

const selected = ref();
const setSelected = () => {
  if (props.multiple) selected.value = Array.isArray(props.defaultValue) ? props.defaultValue : [props.defaultValue];
  else selected.value = props.defaultValue;
};

onBeforeMount(() => {
  getTreeApi();
});

const getTreeApi = async () => {
  loading.value = true;
  setSelected();
  if (props.requestApi) {
    await delayLoading(200);
    const { data } = await props.requestApi!();
    loading.value = false;
    if (typeof data === 'object' && Object.keys(data).length === 0) {
      treeData.value = [];
      treeAllData.value = [];
    } else {
      treeData.value = data;
      treeAllData.value = data;
    }
  }
};

// 使用 nextTick 防止打包后赋值不生效，开发环境是正常的
watch(
  () => props.defaultValue,
  () => nextTick(() => setSelected()),
  { deep: true, immediate: true }
);

watch(
  () => props.data,
  () => {
    if (props.data?.length) {
      treeData.value = props.data;
      treeAllData.value = props.data;
    }
  },
  { deep: true, immediate: true }
);

const filterText = ref('');
watch(filterText, val => {
  treeRef.value!.filter(val);
});

// 过滤
const filterNode = (value: string, data: { [key: string]: any }, node: any) => {
  if (!value) return true;
  let parentNode = node.parent,
    labels = [node.label],
    level = 1;
  while (level < node.level) {
    labels = [...labels, parentNode.label];
    parentNode = parentNode.parent;
    level++;
  }
  return labels.some(label => label.indexOf(value) !== -1);
};

// emit
const emit = defineEmits<{
  change: [value: any];
}>();

// 单选
const handleNodeClick = (data: { [key: string]: any }) => {
  if (props.multiple) return;
  emit('change', data[props.id]);
};

// 多选
const handleCheckChange = () => {
  emit('change', treeRef.value?.getCheckedKeys());
};
const reload = ref(true);
const refresh = () => {
  reload.value = false;
  nextTick(() => {
    getTreeApi();
    reload.value = true;
  });
};

const delayLoading = async (loadingTime: number | undefined) => {
  const defaultLoadingTime = 0; // 默认的 loading 延迟时间为 1 秒
  let actualLoadingTime = loadingTime;
  // 判断 loadingTime 是否为特殊参数
  if (typeof loadingTime === 'undefined' || loadingTime === null || loadingTime === -1) {
    actualLoadingTime = defaultLoadingTime;
  }
  // 等待 loading 延迟时间
  await new Promise(resolve => setTimeout(resolve, actualLoadingTime));
};

// 暴露给父组件使用
defineExpose({ treeData, treeAllData, treeRef, refresh });
</script>

<style scoped lang="scss">
.info-count {
  font-size: 12px; /* 调整字体大小 */
  color: #888; /* 设置字体颜色 */
}

.filter {
  box-sizing: border-box;
  width: 220px;
  height: 100%;
  padding: 18px;
  margin-right: 10px;
  .title {
    margin: 0 0 15px;
    font-size: 18px;
    font-weight: bold;
    color: var(--el-color-info-dark-2);
    letter-spacing: 0.5px;
  }
  .el-input {
    margin: 0 0 15px;
  }
  .el-scrollbar {
    :deep(.el-tree) {
      height: 80%;
      overflow: auto;
      .el-tree-node__content {
        height: 33px;
      }
    }
    :deep(.el-tree--highlight-current) {
      .el-tree-node.is-current > .el-tree-node__content {
        background-color: var(--el-color-primary);
        .el-tree-node__label,
        .el-tree-node__expand-icon {
          color: white;
        }
        .is-leaf {
          color: transparent;
        }
      }
    }
  }
}
</style>
