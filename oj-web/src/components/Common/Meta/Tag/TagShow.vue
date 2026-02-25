<template>
  <div class="tag-show-container">
    <template v-if="tagList && tagList.length > 0">
      <el-tag
        v-for="(tag, index) in tagList"
        :key="tag.id || index"
        class="tag-item"
        :style="{
          backgroundColor: tag.backgroundColor || '#f5f5f7',
          color: tag.textColor || '#1d1d1f',
          borderColor: tag.backgroundColor || '#f5f5f7'
        }"
      >
        {{ tag.name }}
      </el-tag>
    </template>
    <div v-else class="empty-tags">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

defineOptions({
  name: 'TagShow'
});

interface TagItem {
  id?: number;
  name?: string;
  backgroundColor?: string;
  textColor?: string;
  enable?: boolean | string;
  [key: string]: any;
}

interface Props {
  tagList?: TagItem[];
}

const props = defineProps<Props>();
const tagList = computed(() => props.tagList || []);
</script>

<style scoped>
.tag-show-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 4px 0;
}

.tag-item {
  border-radius: 16px;
  font-size: 13px;
  padding: 0 12px;
  height: 28px;
  line-height: 28px;
  margin: 0;
  font-weight: 500;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  transition: all 0.2s cubic-bezier(0.25, 0.1, 0.25, 1.0);
}

.tag-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.empty-tags {
  font-size: 13px;
  color: #86868b;
  padding: 4px 0;
}
</style>
