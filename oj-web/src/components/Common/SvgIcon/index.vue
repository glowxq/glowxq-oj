<template>
  <svg :style="iconStyle" aria-hidden="true">
    <use :xlink:href="symbolId" />
  </svg>
</template>

<script setup lang="ts">
defineOptions({
  name: 'SvgIcon'
});
import { computed } from 'vue';
import type { CSSProperties } from 'vue';

interface SvgProps {
  name: string; // 图标的名称 ==> 必传
  prefix?: string; // 图标的前缀 ==> 非必传（默认为"icon"）
  iconStyle?: CSSProperties; // 图标的样式 ==> 非必传
  width?: number;
  height?: number;
}

const props = withDefaults(defineProps<SvgProps>(), {
  prefix: 'icon',
  width: 16,
  height: 16
});

const symbolId = computed(() => `#${props.prefix}-${props.name}`);

const iconStyle = computed(() => ({
  width: `${props.width}px`,
  height: `${props.height}px`,
  fill: 'var(--color)'
}));
</script>
