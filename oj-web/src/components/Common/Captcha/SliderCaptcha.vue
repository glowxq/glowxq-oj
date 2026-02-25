<template>
  <el-dialog
    v-model="dialogVisible"
    :style="{ width: slideData.bigWidth + 70 + 'px' }"
    :close-on-click-modal="false"
    @close="closeSlider"
    align-center
    destroy-on-close
    :close-on-press-escape="false"
  >
    <div class="sliderBox">
      <div class="sliderBox_content">
        <!-- 大图显示 -->
        <img v-show="slideData.big" :src="'data:image/png;base64,' + slideData.big" class="bigImg" alt="验证码主图" />
        <span class="sliderBox_refresh" @click="refreshSlider">
          <el-icon class="el-input__icon"><refresh /></el-icon>
        </span>
        <!-- 小图显示 -->
        <img
          v-show="slideData.small"
          :src="'data:image/png;base64,' + slideData.small"
          class="smallImg"
          ref="imgK"
          alt="验证码拼图"
        />
        <!-- 验证结果提示 -->
        <div v-if="overlayVisible" class="overlay" :class="{ success: slideData.isSuccess, failure: !slideData.isSuccess }">
          <span>{{ overlayMessage }}</span>
        </div>
      </div>
      <div class="btnBox" ref="wrap">
        <div class="sliderBox_text">向右滑动，完成验证</div>
        <!-- 滑块轨迹显示 -->
        <div class="sliderBox_track" :style="{ width: trackWidth + 'px' }" />
        <!-- 滑块按钮 -->
        <div class="sliderBox_btn" ref="slider">&gt;</div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onBeforeUnmount } from 'vue';
import { Refresh } from '@element-plus/icons-vue';
import { getImageCodeApi, verifyImageCodeApi } from '@/api/modules/system/admin/captcha';
import { aesEncrypt } from '@/utils';

// 定义组件选项
defineOptions({
  name: 'SliderCaptcha'
});

const dialogVisible = ref(false); // 控制对话框可见性

// 定义滑动验证数据结构
const slideData = reactive({
  big: '',
  small: '',
  requestId: '',
  posY: 0,
  isSuccess: false,
  bigWidth: 320,
  secretKey: ''
});

const slider = ref<HTMLDivElement | null>(null); // 滑块元素引用
const wrap = ref<HTMLDivElement | null>(null); // 滑动容器引用
const imgK = ref<HTMLImageElement | null>(null); // 小图元素引用
const trackWidth = ref(0); // 滑动轨迹宽度
const overlayMessage = ref(''); // 验证结果提示信息
const overlayVisible = ref(false); // 验证结果提示可见性
const isVerifying = ref(false); // 是否正在验证标志
let clearEventListeners: (() => void) | null = null; // 清理事件监听器的函数
const emit = defineEmits(['success']);

// 检测是否为移动端
const isMobile = 'ontouchstart' in window || navigator.maxTouchPoints > 0;

// 接收父组件的参数并初始化滑动验证
const acceptParams = () => {
  fetchSlideData();
  dialogVisible.value = true;
};

// 获取滑动验证数据
const fetchSlideData = async () => {
  const { data } = await getImageCodeApi();
  // 将API返回的数据赋值给滑动验证数据
  Object.assign(slideData, {
    big: data.bigImageBase64,
    small: data.smallImageBase64,
    posY: data.posY,
    requestId: data.requestId,
    bigWidth: data.bigWidth,
    isSuccess: false,
    secretKey: data.secretKey
  });
  overlayVisible.value = false;
  overlayMessage.value = '';
  await nextTick(initializeSlider); // 等待DOM更新后初始化滑块
};

// 刷新滑动验证
const refreshSlider = async () => {
  slideData.isSuccess = false;
  overlayVisible.value = false;
  overlayMessage.value = '';
  await fetchSlideData();
};

// 关闭滑动验证对话框
const closeSlider = () => {
  dialogVisible.value = false;
  clearEventListeners && clearEventListeners(); // 清理事件监听器
  resetSlider();
};

// 重置滑块数据
const resetSlider = () => {
  slideData.big = '';
  slideData.small = '';
  slideData.requestId = '';
  slideData.posY = 0;
  slideData.isSuccess = false;
  slideData.secretKey = '';
  slideData.bigWidth = 320;
};

// 初始化滑块事件
const initializeSlider = () => {
  if (!slider.value || !imgK.value || !wrap.value) return;

  slider.value.style.left = '0px';
  imgK.value.style.left = '0px';
  imgK.value.style.top = `${slideData.posY}px`;
  trackWidth.value = 0;
  const limit = wrap.value.offsetWidth - slider.value.offsetWidth;
  let offsetX = 0;
  let initX = 0;
  let startTime = 0;

  // 移动滑块
  const moveSlider = (clientX: number) => {
    offsetX = Math.max(0, Math.min(clientX - initX, limit));
    slider.value!.style.left = `${offsetX}px`;
    imgK.value!.style.left = `${offsetX}px`;
    trackWidth.value = offsetX;
  };

  // 滑块鼠标和触摸按下事件处理
  const handleStart = (e: MouseEvent | TouchEvent) => {
    initX = e instanceof MouseEvent ? e.clientX : e.touches[0].clientX;
    startTime = Date.now();

    const handleMove = (e: MouseEvent | TouchEvent) => {
      const clientX = e instanceof MouseEvent ? e.clientX : e.touches[0].clientX;
      moveSlider(clientX);
    };

    const handleEnd = async () => {
      document.removeEventListener('mousemove', handleMove);
      document.removeEventListener('mouseup', handleEnd);
      document.removeEventListener('touchmove', handleMove);
      document.removeEventListener('touchend', handleEnd);
      const { iv, encryptedData } = await aesEncrypt(offsetX + '', slideData.secretKey);
      await verifyImageCode({
        requestId: slideData.requestId,
        startTime,
        moveEncrypted: encryptedData,
        iv: iv
      });
    };

    if (isMobile) {
      document.addEventListener('touchmove', handleMove);
      document.addEventListener('touchend', handleEnd);
    } else {
      document.addEventListener('mousemove', handleMove);
      document.addEventListener('mouseup', handleEnd);
    }
  };

  // 根据设备类型添加滑块按下事件监听
  if (isMobile) {
    slider.value.addEventListener('touchstart', handleStart);
  } else {
    slider.value.addEventListener('mousedown', handleStart);
  }

  // 清理事件监听器
  clearEventListeners = () => {
    document.removeEventListener('mousemove', handleStart);
    document.removeEventListener('mouseup', handleStart);
    document.removeEventListener('touchmove', handleStart);
    document.removeEventListener('touchend', handleStart);
    slider.value?.removeEventListener('mousedown', handleStart);
    slider.value?.removeEventListener('touchstart', handleStart);
  };
};

// 验证滑动结果
const verifyImageCode = async (params: { requestId: string; startTime: number; moveEncrypted: string; iv: string }) => {
  if (isVerifying.value) return; // 防止重复验证
  isVerifying.value = true;

  try {
    const { code } = await verifyImageCodeApi(params);
    const duration = ((Date.now() - params.startTime) / 1000).toFixed(2);
    overlayVisible.value = true;

    if (code === '0000') {
      slideData.isSuccess = true;
      overlayMessage.value = `验证成功, 耗时${duration}s`;
      setTimeout(() => {
        emit('success');
        closeSlider();
      }, 2000); // 2秒后关闭
    } else {
      handleVerificationFailure();
    }
  } catch {
    handleVerificationFailure();
  } finally {
    isVerifying.value = false;
  }
};

// 处理验证失败
const handleVerificationFailure = () => {
  slideData.isSuccess = false;
  overlayMessage.value = '验证失败';
  overlayVisible.value = true;
  setTimeout(refreshSlider, 2000); // 2秒后刷新
};

// 组件卸载前清理事件监听器
onBeforeUnmount(() => {
  clearEventListeners && clearEventListeners();
});

// 公开的组件方法
defineExpose({
  acceptParams,
  dialogVisible
});
</script>

<style scoped lang="scss">
@use 'index';
</style>
