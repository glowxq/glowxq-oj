<template>
  <div class="animation-demo">
    <div class="demo-header">
      <h2>🎬 CodeGame 动画演示</h2>
      <p>展示平滑动画效果和自定义元素功能</p>
    </div>

    <div class="demo-controls">
      <div class="control-group">
        <label>动画速度：</label>
        <el-slider
          v-model="animationSpeed"
          :min="0.1"
          :max="5"
          :step="0.1"
          @input="updateAnimationSpeed"
          style="width: 200px"
        />
        <span>{{ animationSpeed }}x</span>
      </div>

      <div class="control-group">
        <el-button @click="runDemo1" type="primary">演示1: 基础移动</el-button>
        <el-button @click="runDemo2" type="success">演示2: 循环移动</el-button>
        <el-button @click="runDemo3" type="warning">演示3: 复杂路径</el-button>
      </div>
    </div>

    <div class="game-container">
      <MoveCodeGame
        ref="codeGameRef"
        :level-data="currentLevelData"
        @game-start="handleGameStart"
        @game-end="handleGameEnd"
        @level-complete="handleLevelComplete"
      />
    </div>

    <div class="demo-code">
      <h3>当前演示代码：</h3>
      <pre><code>{{ currentDemoCode }}</code></pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElSlider, ElButton } from 'element-plus'
import MoveCodeGame from '../MoveCodeGame.vue'

const codeGameRef = ref<InstanceType<typeof MoveCodeGame> | null>(null)
const animationSpeed = ref(1)
const currentDemoCode = ref('')

// 默认关卡数据
const currentLevelData = ref({
  id: 1,
  objective: '演示关卡',
  learningContent: '学习基础移动指令',
  difficulty: 1,
  availableCommands: ['right()', 'left()', 'up()', 'down()'],
  codePlaceholder: '// 在这里编写代码',
  successMessage: '演示完成！',
  maps: [{
    id: 1,
    name: '演示地图',
    heroStart: { x: 1, y: 1 },
    treasures: [{ x: 13, y: 8 }],
    obstacles: [] as Array<{x: number, y: number}>
  }],
  answer: 'right()\nright()'
})

// 更新动画速度
const updateAnimationSpeed = (speed: number | number[]) => {
  const actualSpeed = Array.isArray(speed) ? speed[0] : speed
  codeGameRef.value?.setAnimationSpeed(actualSpeed)
}

// 演示1: 基础移动
const runDemo1 = async () => {
  const code = `// 基础移动演示
moveRight()
moveRight()
moveRight()
moveDown()
moveDown()
moveLeft()
moveLeft()
moveLeft()`

  currentDemoCode.value = code

  if (codeGameRef.value) {
    await codeGameRef.value.resetGame()

    // 模拟用户输入代码并运行
    setTimeout(() => {
      codeGameRef.value?.runUserCode()
    }, 500)
  }
}

// 演示2: 循环移动
const runDemo2 = async () => {
  const code = `// 循环移动演示
for(let i = 0; i < 3; i++) {
  moveRight()
}

for(let i = 0; i < 2; i++) {
  moveDown()
}

for(let i = 0; i < 3; i++) {
  moveLeft()
}`

  currentDemoCode.value = code

  if (codeGameRef.value) {
    await codeGameRef.value.resetGame()

    setTimeout(() => {
      codeGameRef.value?.runUserCode()
    }, 500)
  }
}

// 演示3: 复杂路径
const runDemo3 = async () => {
  const code = `// 复杂路径演示
// 先向右移动
for(let i = 0; i < 4; i++) {
  moveRight()
}

// 向上绕过障碍
moveUp()
moveRight()
moveRight()
moveDown()

// 到达目标
for(let i = 0; i < 3; i++) {
  moveRight()
}`

  currentDemoCode.value = code

  if (codeGameRef.value) {
    // 更新关卡数据为有障碍物的地图
    currentLevelData.value.maps[0].obstacles.push(
      { x: 5, y: 1 }, { x: 6, y: 1 }, { x: 7, y: 1 }
    )
    await codeGameRef.value.resetGame()

    setTimeout(() => {
      codeGameRef.value?.runUserCode()
    }, 500)
  }
}

// 事件处理
const handleGameStart = () => {
  console.log('演示开始')
}

const handleGameEnd = (success: boolean) => {
  console.log(`演示结束, 成功: ${success}`)
}

const handleLevelComplete = () => {
  console.log('演示完成')
}

onMounted(() => {
  // 初始化时设置动画速度
  setTimeout(() => {
    updateAnimationSpeed(animationSpeed.value)
  }, 1000)
})
</script>

<style scoped>
.animation-demo {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.demo-header {
  text-align: center;
  margin-bottom: 30px;
}

.demo-header h2 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.demo-header p {
  color: #7f8c8d;
  font-size: 16px;
}

.demo-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 20px;
}

.control-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.control-group label {
  font-weight: 500;
  color: #2c3e50;
}

.game-container {
  margin-bottom: 30px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.demo-code {
  background: #2c3e50;
  color: #ecf0f1;
  padding: 20px;
  border-radius: 12px;
}

.demo-code h3 {
  margin: 0 0 15px 0;
  color: #3498db;
}

.demo-code pre {
  margin: 0;
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 14px;
  line-height: 1.5;
  overflow-x: auto;
}

.demo-code code {
  color: #e74c3c;
}

@media (max-width: 768px) {
  .demo-controls {
    flex-direction: column;
    align-items: stretch;
  }

  .control-group {
    justify-content: space-between;
  }
}
</style>
