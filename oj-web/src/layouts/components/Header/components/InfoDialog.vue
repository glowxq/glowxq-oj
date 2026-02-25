<template>
  <el-dialog v-model="visible" title="个人信息" width="500px" draggable>
    <el-descriptions :column="2" border>
      <el-descriptions-item>
        <template #label> 用户名 </template>
        {{ info?.username }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label> 昵称 </template>
        {{ info?.nickname }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label> 手机号 </template>
        {{ info?.phone }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label> 身份证 </template>
        {{ info?.idCard }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label> 性别 </template>
        {{ showSex(info?.sex) }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label> 年龄 </template>
        {{ info?.age }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="visible = false">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { getUserinfo } from '@/api/modules/system/admin/user';
import type { IUser } from '@/api/interface/system/admin/user';

const visible = ref(false);
const openDialog = () => {
  visible.value = true;
  loadInfo();
};

const info = ref<IUser.Info>();
const loadInfo = () => {
  getUserinfo().then(res => {
    info.value = res.data;
  });
};

const showSex = (sex: number | undefined) => {
  switch (sex) {
    case 1:
      return '男';
    case 2:
      return '女';
    default:
      return '未知';
  }
};

defineExpose({ openDialog });
</script>
