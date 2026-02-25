<template>
  <div class="array-settings">
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="config-section">
          <h3>一维数组操作设置</h3>
          <div class="config-item">
            <span class="config-label">运行速度：</span>
            <div class="speed-control-container">
              <el-select v-model="localSpeedValues.arr" style="width: 120px;" @change="handleSpeedChange('arr')">
                <el-option
                  v-for="option in speedOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
              <div class="speed-slider-container">
                <el-slider v-model="localSpeedValues.arr" :min="100" :max="1000" :step="10" @change="handleSliderChange('arr')" />
              </div>
              <span class="speed-value-text">{{ speedLabel }}</span>
              <el-button type="text" size="small" @click="resetSpeed('arr')" class="reset-button">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
            <div class="config-desc">控制数组操作的速度，值越大速度越快，1000为瞬间完成</div>
          </div>
          <div class="config-item">
            <span class="config-label">单元格大小：</span>
            <el-slider v-model="localConfigValues.arr.cellSize" :min="30" :max="80" show-input />
            <div class="config-desc">控制数组单元格的大小，范围30-80像素</div>
          </div>
          
          <div class="config-item">
            <span class="config-label">内边距：</span>
            <el-slider v-model="localConfigValues.arr.padding" :min="0" :max="20" show-input />
            <div class="config-desc">控制数组与画布边缘的内边距，范围0-20像素</div>
          </div>
          
          <div class="config-item">
            <span class="config-label">字体大小：</span>
            <el-slider v-model="localConfigValues.arr.fontSize" :min="10" :max="20" show-input />
            <div class="config-desc">控制数组元素内文字的大小，范围10-20像素</div>
          </div>
          
          <div class="config-item">
            <span class="config-label">高亮持续时间 (ms)：</span>
            <el-slider v-model="localConfigValues.arr.highlightDuration" :min="500" :max="5000" :step="100" show-input />
            <div class="config-desc">控制数组元素高亮显示的持续时间，范围500-5000毫秒</div>
          </div>
          
          <div class="config-item">
            <span class="config-label">设置值动画时间 (ms)：</span>
            <el-slider v-model="localConfigValues.arr.setDuration" :min="100" :max="3000" :step="100" show-input />
            <div class="config-desc">控制设置数组元素值时的动画持续时间，范围100-3000毫秒</div>
          </div>
        </div>
      </el-col>
      
      <el-col :span="12">
        <div class="config-section">
          <h3>数组颜色设置</h3>
          <div class="config-item">
            <span class="config-label">文本颜色：</span>
            <el-color-picker v-model="localConfigValues.arr.textColor" />
            <div class="config-desc">控制数组元素文本的颜色</div>
          </div>
          
          <div class="config-item">
            <span class="config-label">高亮颜色：</span>
            <el-color-picker v-model="localConfigValues.arr.highlightColor" />
            <div class="config-desc">控制数组元素被高亮时的背景颜色</div>
          </div>
          
          <div class="config-item">
            <span class="config-label">边框颜色：</span>
            <el-color-picker v-model="localConfigValues.arr.borderColor" />
            <div class="config-desc">控制数组元素边框的颜色</div>
          </div>
          
          <div class="config-item">
            <span class="config-label">背景颜色：</span>
            <el-color-picker v-model="localConfigValues.arr.backgroundColor" />
            <div class="config-desc">控制数组元素正常状态下的背景颜色</div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, defineProps, defineEmits, watch } from 'vue';
import { Refresh } from '@element-plus/icons-vue';

// 定义速度选项接口
interface SpeedOption {
  label: string;
  value: number;
}

const props = defineProps({
  // 速度值
  speedValues: {
    type: Object,
    required: true
  },
  
  // 配置值
  configValues: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['speed-change', 'slider-change', 'reset-speed']);

// 本地配置值，避免直接修改props
const localSpeedValues = ref({...props.speedValues});
const localConfigValues = ref({...props.configValues});

// 速度选项
const speedOptions = ref<SpeedOption[]>([
  { label: '极慢', value: 100 },
  { label: '慢速', value: 200 },
  { label: '中速', value: 500 },
  { label: '快速', value: 700 },
  { label: '极速', value: 900 },
  { label: '瞬间', value: 1000 }
]);

// 监听props变化
watch(() => props.speedValues, (newValues) => {
  localSpeedValues.value = {...newValues};
}, { deep: true });

watch(() => props.configValues, (newValues) => {
  localConfigValues.value = {...newValues};
}, { deep: true });

// 计算当前速度标签
const speedLabel = computed(() => {
  const speed = localSpeedValues.value.arr;
  if (speed >= 1000) return '瞬间';
  if (speed >= 900) return '极速';
  if (speed >= 700) return '快速';
  if (speed >= 500) return '中速';
  if (speed >= 200) return '慢速';
  return '极慢';
});

// 处理速度变化
const handleSpeedChange = (component: string) => {
  emit('speed-change', component, localSpeedValues.value[component]);
};

// 处理滑动条变化
const handleSliderChange = (component: string) => {
  emit('slider-change', component, localSpeedValues.value[component]);
};

// 重置速度
const resetSpeed = (component: string) => {
  emit('reset-speed', component);
};
</script>

<style scoped>
.config-section {
  margin-bottom: 20px;
  padding: 15px;
  border-radius: 4px;
  background-color: #f9f9f9;
  border: 1px solid #eee;
}

.config-section h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #409EFF;
  font-size: 16px;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.config-item {
  margin: 15px 0;
  display: flex;
  align-items: flex-start;
  flex-wrap: wrap;
}

.config-label {
  min-width: 130px;
  display: inline-block;
  margin-top: 8px;
}

.config-desc {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
  padding-left: 130px;
  width: 100%;
}

.speed-control-container {
  display: flex;
  align-items: center;
  flex: 1;
}

.speed-slider-container {
  flex: 1;
  margin-left: 10px;
  margin-right: 10px;
  max-width: 300px;
}

.speed-value-text {
  min-width: 40px;
  text-align: center;
  color: #409EFF;
  font-weight: bold;
}

.reset-button {
  margin-left: 5px;
  color: #909399;
}
</style> 