<template>
  <div class="pen-settings">
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="config-section">
          <h3>画笔基本设置</h3>
          <div class="config-item">
            <span class="config-label">运行速度：</span>
            <div class="speed-control-container">
              <el-select v-model="localSpeedValues.pen" style="width: 120px;" @change="handleSpeedChange('pen')">
                <el-option
                  v-for="option in speedOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
              <div class="speed-slider-container">
                <el-slider v-model="localSpeedValues.pen" :min="100" :max="1000" :step="10" @change="handleSliderChange('pen')" />
              </div>
              <span class="speed-value-text">{{ speedLabel }}</span>
              <el-button type="text" size="small" @click="resetSpeed('pen')" class="reset-button">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
            <div class="config-desc">控制画笔绘制的速度，值越大速度越快，1000为瞬间完成</div>
          </div>
          <div class="config-item">
            <span class="config-label">线条粗细：</span>
            <el-slider v-model="localLineWidth" :min="1" :max="10" show-input />
            <div class="config-desc">控制画笔绘制的线条粗细，范围1-10像素</div>
          </div>
          <div class="line-preview" :style="{ height: localLineWidth + 'px' }"></div>
          
          <div class="config-item">
            <span class="config-label">箭头长度：</span>
            <el-slider v-model="localConfigValues.pen.arrowSize" :min="10" :max="40" show-input />
            <div class="config-desc">控制方向指示箭头的长度，范围10-40像素</div>
          </div>
          
          <div class="config-item">
            <span class="config-label">箭头宽度：</span>
            <el-slider v-model="localConfigValues.pen.arrowWidth" :min="5" :max="20" show-input />
            <div class="config-desc">控制方向指示箭头的宽度，范围5-20像素</div>
          </div>

          <div class="config-item">
            <span class="config-label">箭头透明度：</span>
            <el-slider v-model="localConfigValues.pen.arrowOpacity" :min="0.1" :max="1" :step="0.1" show-input />
            <div class="config-desc">控制方向指示箭头的透明度，范围0.1-1.0</div>
          </div>
        </div>
      </el-col>
      
      <el-col :span="12">
        <div class="config-section">
          <h3>指示器颜色设置</h3>
          <div class="config-item">
            <span class="config-label">画笔落下颜色：</span>
            <el-color-picker v-model="localConfigValues.pen.arrowDownColor" />
            <div class="config-desc">画笔落下时指示器的颜色（默认红色）</div>
          </div>
          
          <div class="config-item">
            <span class="config-label">画笔抬起颜色：</span>
            <el-color-picker v-model="localConfigValues.pen.arrowUpColor" />
            <div class="config-desc">画笔抬起时指示器的颜色（默认蓝色）</div>
          </div>
        </div>

        <div class="color-map">
          <h3>颜色对照表 (pen.color(数字))</h3>
          <div class="color-grid-small">
            <div 
              v-for="color in colorMap" 
              :key="color.id" 
              class="color-item-small"
            >
              <div class="color-box-small" :style="{ backgroundColor: color.value }"></div>
              <div class="color-info-small">
                <span class="color-code-small">{{ color.id }}:</span>
                <span class="color-name-small">{{ color.name }}</span>
              </div>
            </div>
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

// 定义颜色映射项接口
interface ColorMapItem {
  id: number;
  name: string;
  value: string;
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
  },
  
  // 线宽值
  lineWidthValue: {
    type: Number,
    default: 2
  },
  
  // 颜色映射
  colorMap: {
    type: Array as () => ColorMapItem[],
    required: true
  }
});

// 添加新的emit事件用于同步线宽变化
const emit = defineEmits(['speed-change', 'slider-change', 'reset-speed', 'line-width-change', 'config-change']);

// 本地配置值，避免直接修改props
const localSpeedValues = ref({...props.speedValues});
const localConfigValues = ref({...props.configValues});
const localLineWidth = ref(props.lineWidthValue);

// 确保localLineWidth同步到localConfigValues.pen
watch(localLineWidth, (newValue) => {
  if (localConfigValues.value && localConfigValues.value.pen) {
    localConfigValues.value.pen.lineWidth = newValue;
    
    // 通过事件通知父组件线宽变化，不要直接修改props
    emit('line-width-change', newValue);
    
    // 同时通知父组件配置变化
    emit('config-change', 'pen', localConfigValues.value.pen);
  }
});

// 监听其他pen配置变化并通知父组件
watch(() => localConfigValues.value.pen, (newPenConfig) => {
  // 避免初始化时触发
  if (JSON.stringify(newPenConfig) !== JSON.stringify(props.configValues.pen)) {
    emit('config-change', 'pen', newPenConfig);
  }
}, { deep: true });

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

// 恢复这个watch
watch(() => props.lineWidthValue, (newValue) => {
  localLineWidth.value = newValue;
});

// 计算当前速度标签
const speedLabel = computed(() => {
  const speed = localSpeedValues.value.pen;
  if (speed >= 1000) return '瞬间';
  if (speed >= 900) return '极速';
  if (speed >= 700) return '快速';
  if (speed >= 500) return '中速';
  if (speed >= 200) return '慢速';
  return '极慢';
});

// 处理速度变化
const handleSpeedChange = (component: string) => {
  const value = localSpeedValues.value[component];
  emit('speed-change', component, value);
  console.log(`[PenSettings] 发送速度变化: ${component} = ${value}`);
};

// 处理滑动条变化
const handleSliderChange = (component: string) => {
  const value = localSpeedValues.value[component];
  emit('slider-change', component, value);
  console.log(`[PenSettings] 发送滑块变化: ${component} = ${value}`);
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

.line-preview {
  margin: 10px 0;
  background-color: #409EFF;
  width: 100%;
  border-radius: 4px;
}

.color-grid-small {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 8px;
  margin-top: 15px;
}

.color-item-small {
  display: flex;
  align-items: center;
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 5px;
  background-color: #f9f9f9;
}

.color-box-small {
  width: 20px;
  height: 20px;
  border-radius: 3px;
  margin-right: 8px;
  border: 1px solid #ddd;
}

.color-info-small {
  flex: 1;
  font-size: 12px;
}

.color-code-small {
  font-weight: bold;
  margin-right: 4px;
}

.color-name-small {
  color: #666;
}
</style> 