<template>
  <div class="tuc-view">
    <Glowc :width="600" :height="600" class="tuc-component" />
  </div>
</template>

<script setup lang="ts">
import Glowc from '@/components/Oj/Glowc/index.vue';
</script>

<style scoped>
.tuc-view {
  width: 100%;
  height: 100%;
  padding: 0;
  margin: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.tuc-component {
  flex: 1;
  overflow: auto;
}
</style>
