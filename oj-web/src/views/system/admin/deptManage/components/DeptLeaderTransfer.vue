<template>
  <el-dialog
    v-model="visible"
    :title="`${paramsProps.title}-${paramsProps.row?.name}`"
    :destroy-on-close="true"
    width="650px"
    draggable
    append-to-body
  >
    <el-transfer
      v-model="selectIds"
      filterable
      :filter-method="filterMethod"
      :titles="['未设置负责人', '已设置负责人']"
      filter-placeholder="筛选负责人"
      :props="transferProps"
      :data="leaderLists"
    />
    <template #footer>
      <el-button @click="visible = false"> 取消 </el-button>
      <el-button type="primary" @click="handleSubmit"> 确定 </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import type { ISysDept } from '@/api/interface/system/admin/dept';
import { ref } from 'vue';

defineOptions({
  name: 'DeptLeaderTransfer'
});

const transferProps = {
  key: 'id',
  label: 'nickname'
};

defineProps({
  modelValue: {
    type: Array as () => number[],
    required: true
  }
});

const leaderLists = ref<ISysDept.Leader[]>([]);
const selectIds = ref<number[]>([]);
const emits = defineEmits<{
  'update:modelValue': [number[]];
}>();

/**
 * 过滤角色
 * @param query
 * @param item
 * @returns {boolean}
 */
const filterMethod = (query: string, item: Record<string, any>) => {
  return item.nickname && item.nickname.toLowerCase().includes(query.toLowerCase());
};

const visible = ref(false);
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  leaderList: []
});

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params;
  leaderLists.value = params.leaderList;
  selectIds.value = params.selectIds;
  visible.value = true;
};

const handleSubmit = async () => {
  try {
    emits('update:modelValue', selectIds.value);
    visible.value = false;
  } catch (error) {
    console.log(error);
  }
};

defineExpose({
  acceptParams
});
</script>

<style scoped lang="scss"></style>
