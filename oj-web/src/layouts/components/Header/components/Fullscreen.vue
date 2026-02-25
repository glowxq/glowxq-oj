<template>
  <div class="fullscreen">
    <i :class="['iconfont', isFullscreen ? 'icon-suoxiao' : 'icon-fangda']" class="toolBar-icon" @click="handleFullScreen" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import screenfull from 'screenfull';
import { ElMessage } from 'element-plus';

const isFullscreen = ref(screenfull.isFullscreen);

onMounted(() => {
  screenfull.on('change', () => {
    isFullscreen.value = screenfull.isFullscreen;
  });
});

const handleFullScreen = () => {
  if (!screenfull.isEnabled) ElMessage.warning('当前您的浏览器不支持全屏 ❌');
  screenfull.toggle();
};
</script>
