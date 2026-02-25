<template>
  <div class="choose-container">
    <el-input ref="refInput" v-model="internalValue" v-bind="$attrs" readonly @input="changeValue" @click="showPopover">
      <template #prepend>
        <el-icon size="30">
          <SvgIcon v-if="internalValue.startsWith('svg-')" :name="internalValue.substring(4)" />
          <component v-else-if="internalValue" :is="internalValue" />
        </el-icon>
      </template>
    </el-input>

    <div ref="chooseDialogRef" class="choose-wrap" :class="{ show: isShow }">
      <el-input v-model="search" class="w-50 m-2" placeholder="搜索图标" :prefix-icon="Search" />

      <el-tabs v-model="activeTab">
        <el-tab-pane label="Element" name="element">
          <el-scrollbar max-height="280">
            <div class="choose-box">
              <div class="choose-item" v-for="item in filteredElementIcons" :key="item">
                <div @click="chooseIcon(item)">
                  <el-icon size="30">
                    <component :is="item" />
                  </el-icon>
                  {{ item }}
                </div>
              </div>
            </div>
          </el-scrollbar>
        </el-tab-pane>
        <el-tab-pane label="Svg" name="svg">
          <el-scrollbar max-height="280">
            <div class="choose-box">
              <div class="choose-item" v-for="item in filteredSvgIcons" :key="item">
                <div @click="chooseIcon(item)">
                  <el-icon size="30">
                    <SvgIcon :name="item.substring(4)" />
                  </el-icon>
                  {{ item }}
                </div>
              </div>
            </div>
          </el-scrollbar>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
// element icons
import * as Icons from '@element-plus/icons-vue';
import { Search } from '@element-plus/icons-vue';
import SvgIcon from '@/components/Common/SvgIcon/index.vue';
import { onClickOutside } from '@vueuse/core';

defineOptions({
  name: 'IconChoose'
});
type Props = {
  modelValue: string;
};
const props = defineProps<Props>();
const emits = defineEmits<{
  'update:modelValue': [string];
}>();

const internalValue = ref(props.modelValue);

const changeValue = () => {
  emits('update:modelValue', internalValue.value);
};

const activeTab = ref('element');

// 图标搜索
const search = ref('');
const elementIcons = Object.keys(Icons).map(key => {
  return Icons[key as keyof typeof Icons].name;
});

const svgIcons: string[] = [];
const svgIconsFiles = import.meta.glob('@/assets/icons/**/*.svg');
for (const key in svgIconsFiles) {
  const matchArray = key.match(/\/assets\/icons\/([^/]+)\.svg$/);
  if (matchArray && matchArray.length >= 2) {
    svgIcons.push(`svg-${matchArray[1]}`);
  }
}

// 过滤图标
const filteredElementIcons = computed(() => {
  return elementIcons.filter(item => item.toLowerCase().includes(search.value.toLowerCase()));
});

const filteredSvgIcons = computed(() => {
  return svgIcons.filter(item => item.toLowerCase().includes(search.value.toLowerCase()));
});

/**
 * 弹窗
 */
const isShow = ref(false);

/**
 * 显示
 */
const showPopover = () => {
  isShow.value = true;
};

/**
 * 隐藏
 */
const hidePopover = () => {
  isShow.value = false;
};

// 弹窗外部触发
const chooseDialogRef = ref(null);
onClickOutside(chooseDialogRef, () => {
  if (isShow.value) {
    hidePopover();
  }
});

const chooseIcon = (name: string) => {
  hidePopover();
  internalValue.value = name;
  emits('update:modelValue', name);
};
</script>

<style scoped lang="scss">
.choose-container {
  position: relative;
  width: 100%;

  .choose-wrap {
    position: absolute;
    margin-top: 6px;
    padding: 10px;
    z-index: 100;
    border: 1px solid #e4e7ed;
    border-radius: 6px;
    box-shadow: 0 0 12px rgb(0 0 0 / 5%);
    background-color: #fff;
    width: 100%;
    height: 0;
    opacity: 0;
    overflow: hidden;
    transition:
      height 0.3s,
      opacity 0.3s;
    box-sizing: border-box;

    &.show {
      opacity: 1;
      height: 400px;
    }
  }
}

.choose-box {
  display: flex;
  flex-wrap: wrap;
  margin: 0 10px;

  .choose-item {
    width: 25%;
    padding: 5px;
    box-sizing: border-box;

    > div {
      display: flex;
      align-items: center;
      padding: 5px;
      border: 1px solid rgba(204, 204, 204, 0.5);
      border-radius: 4px;
      overflow: hidden;
      font-size: 12px;
    }

    &:hover {
      > div {
        border-color: rgba(0, 150, 136, 0.5);
      }
    }
  }
}

.close-box {
  position: absolute;
  top: -15px;
  right: -15px;
}
</style>
