# 🎮 MoveCodeGame - CodeCombat 风格少儿编程游戏

## 📖 概述

MoveCodeGame 是一个基于 Vue3 + TypeScript 开发的少儿编程学习游戏组件，灵感来源于 CodeCombat。通过游戏化的方式让孩子们学习编程基础概念，玩家需要编写代码来控制小英雄在游戏世界中移动，完成各种挑战任务。

## ✨ 特性

- 🎯 **渐进式学习**：从基础移动命令到循环语句的渐进式关卡设计
- 🎨 **精美界面**：采用 Apple 设计风格，毛玻璃效果和流畅动画
- 🔧 **独立组件**：完全独立的组件设计，易于集成和维护
- 📱 **响应式设计**：支持桌面端和移动端的完美适配
- 🎪 **事件系统**：完整的游戏事件系统，支持外部系统联动
- 🛡️ **安全执行**：安全的代码执行环境，防止恶意代码
- 🎬 **平滑动画**：参考pen.ts实现，支持平滑的移动动画效果
- 🖼️ **自定义图片**：支持为游戏元素设置自定义图片和样式
- ⚡ **性能优化**：使用requestAnimationFrame优化动画性能
- 🎮 **丰富交互**：支持闪烁效果、朝向变化等丰富的视觉反馈
- 🔄 **序列执行**：参考CodeRunner实现，正确处理for循环和连续命令
- 🛠️ **自动await**：自动为异步命令添加await，确保命令按顺序执行
- 💻 **专业编辑器**：集成CodeEditor组件，提供语法高亮和代码补全
- 🎛️ **可调节布局**：支持拖拽调整左右面板比例，适应不同屏幕
- 🎈 **悬浮交互**：使用悬浮球设计，关卡信息和设置可折叠显示
- 📱 **完全响应式**：支持桌面、平板、手机等各种设备的完美适配
- 💡 **智能答案系统**：为每个关卡提供标准答案和详细解析
- 🎓 **渐进式学习**：8个关卡从基础到高级，循序渐进教学
- 🎯 **关卡选择器**：直观的关卡选择界面，支持自由选择挑战关卡
- ⭐ **难度指示**：星级难度显示，帮助学习者选择合适的挑战
- 🏰 **通道式迷宫**：第5关后采用通道式设计，障碍物组成明确路线
- 🛤️ **路径引导**：玩家必须沿着规定路径前进，培养编程逻辑思维
- 🔀 **条件判断**：关卡9-10专门训练if-else条件判断
- ⚙️ **个性化设置**：完整的设置面板，支持自定义图标和移动速度
- 🧭 **智能指令**：6个新增指令支持方向控制和环境检测
- 🎯 **方向系统**：英雄具有方向状态，支持旋转和前进

## 🎮 游戏关卡

MoveCodeGame现在包含10个精心设计的关卡，从基础移动到条件判断的完整编程概念：

### 关卡 1：基础移动 🚶‍♂️
- **目标**：控制小英雄移动到金色宝箱位置
- **学习内容**：函数调用、基本移动命令
- **难度**：⭐

### 关卡 2：避障挑战 🧱
- **目标**：绕过红色障碍物到达宝箱
- **学习内容**：路径规划、问题分析
- **难度**：⭐⭐

### 关卡 3：循环优化 🔄
- **目标**：使用循环语句高效移动
- **学习内容**：for 循环、代码优化
- **难度**：⭐⭐

### 关卡 4：多目标收集 🎯
- **目标**：收集多个宝箱
- **学习内容**：顺序执行、路径优化
- **难度**：⭐⭐⭐

### 关卡 5：L形通道 🏰
- **目标**：沿着L形迷宫通道到达宝箱
- **学习内容**：路径跟踪、按规定路线移动
- **特色**：障碍物组成明确的通道路线
- **难度**：⭐⭐⭐⭐

### 关卡 6：S形通道 🐍
- **目标**：穿越S形蛇形通道
- **学习内容**：复杂路径跟踪、方向变化
- **特色**：多次方向变化的蛇形路径
- **难度**：⭐⭐⭐⭐

### 关卡 7：螺旋通道 🌀
- **目标**：穿越螺旋迷宫通道
- **学习内容**：复杂路径规划、多方向移动
- **特色**：从外向内的螺旋移动模式
- **难度**：⭐⭐⭐⭐⭐

### 关卡 8：多宝箱分支迷宫 🎯
- **目标**：沿着分支通道收集所有宝箱
- **学习内容**：综合运用所有技能、分支路径处理
- **特色**：复杂的分支通道系统，需要收集多个宝箱
- **难度**：⭐⭐⭐⭐⭐

### 关卡 9：条件判断 - 路径选择 🔀
- **目标**：根据条件选择正确的路径
- **学习内容**：if-else条件判断、逻辑思维
- **特色**：Y形分叉路径，需要使用hasObstacle()函数判断
- **难度**：⭐⭐⭐⭐⭐

### 关卡 10：复杂条件 - 多重判断 🎯
- **目标**：使用复杂条件判断收集多个宝箱
- **学习内容**：复杂if-else、嵌套条件判断
- **特色**：十字路口设计，需要使用hasTreasure()函数
- **难度**：⭐⭐⭐⭐⭐

## 🚀 使用方法

### 基本使用

```vue
<template>
  <MoveCodeGame
    :initial-level="1"
    @game-start="handleGameStart"
    @game-end="handleGameEnd"
    @level-complete="handleLevelComplete"
  />
</template>

<script setup lang="ts">
import MoveCodeGame from '@/components/Oj/Games/MoveCodeGame/MoveCodeGame.vue'

const handleGameStart = (level: number) => {
  console.log(`游戏开始 - 关卡 ${level}`)
}

const handleGameEnd = (level: number, success: boolean) => {
  console.log(`游戏结束 - 关卡 ${level}, 成功: ${success}`)
}

const handleLevelComplete = (level: number) => {
  console.log(`关卡完成 - 关卡 ${level}`)
}
</script>
```

### 组件属性

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `initial-level` | `number` | `1` | 初始关卡 |

### 组件事件

| 事件名 | 参数 | 说明 |
|--------|------|------|
| `game-start` | `(level: number)` | 游戏开始时触发 |
| `game-end` | `(level: number, success: boolean)` | 游戏结束时触发 |
| `level-complete` | `(level: number)` | 关卡完成时触发 |

### 界面特性

#### 🎛️ 可调节布局
- **拖拽分隔条**：鼠标拖拽中间的分隔条可以调整左右面板比例
- **自适应画布**：游戏画布会根据面板大小自动调整尺寸
- **响应式设计**：在小屏幕设备上自动切换为上下布局

#### 🎈 悬浮球交互
- **关卡信息球**：点击右上角的信息图标查看关卡目标和可用命令
- **设置球**：点击设置图标调整动画速度等参数
- **智能折叠**：面板会自动折叠，避免遮挡游戏画面

#### 💻 专业代码编辑器
- **语法高亮**：JavaScript代码的完整语法高亮
- **代码补全**：智能代码提示和自动补全
- **多种主题**：支持明亮和暗黑主题切换
- **工具栏功能**：运行、重新运行、查看答案、清空等操作

#### 💡 智能答案系统
- **标准答案**：每个关卡都提供经过优化的标准解答
- **详细注释**：答案代码包含详细的解释说明
- **安全确认**：查看答案前会确认，防止意外覆盖用户代码
- **学习辅助**：帮助学习者理解解题思路和编程技巧

#### 🎯 关卡选择功能
- **直观选择**：网格布局的关卡选择器，一目了然
- **难度显示**：每个关卡显示对应的星级难度
- **即时切换**：点击关卡按钮立即切换，无需重新加载
- **信息展示**：显示关卡目标和当前进度状态

#### 🔀 条件判断功能
- **辅助函数**：提供hasObstacle()和hasTreasure()检查函数
- **逻辑训练**：专门的关卡训练if-else条件判断
- **复杂场景**：多重条件判断和嵌套逻辑处理
- **实战应用**：在实际游戏场景中学习条件判断

#### ⚙️ 个性化设置面板
- **移动速度**：0.1x-5.0x可调节的动画速度
- **英雄头像**：8种不同风格的英雄图标可选
- **宝箱图标**：8种精美的宝箱图标可选
- **障碍物图标**：8种不同类型的障碍物图标可选
- **一键重置**：快速恢复所有设置到默认值

#### 🧭 智能指令系统
- **环境检测**：thereObstacles()和reachBoundary()检查周围环境
- **方向判断**：isThereObstacles()和isReachBoundary()检查指定方向
- **智能移动**：fd()根据当前方向前进，rt()旋转方向
- **方向指示**：英雄显示当前面向方向的箭头
- **编程进阶**：支持更复杂的算法和逻辑实现

### 组件方法

通过 `ref` 可以调用以下方法：

```typescript
const codeGameRef = ref<InstanceType<typeof MoveCodeGame>>()

// 运行用户代码（自动开始游戏）
codeGameRef.value?.runUserCode()

// 重新运行（重置后运行）
codeGameRef.value?.resetAndRun()

// 重置游戏
codeGameRef.value?.resetGame()

// 下一关
codeGameRef.value?.nextLevel()

// 清空代码
codeGameRef.value?.clearCode()

// 查看当前关卡答案
codeGameRef.value?.showAnswer()

// 获取游戏状态
const status = codeGameRef.value?.getGameStatus()

// 获取当前关卡
const level = codeGameRef.value?.getCurrentLevel()

// 设置关卡
codeGameRef.value?.setLevel(2)

// 设置动画速度 (0.1-10倍速)
codeGameRef.value?.setAnimationSpeed(2)

// 界面控制
codeGameRef.value?.toggleLevelInfo() // 切换关卡信息显示
codeGameRef.value?.toggleSettings()  // 切换设置面板显示

// 布局控制
codeGameRef.value?.setLeftPanelWidth(50) // 设置左侧面板宽度百分比

// 添加自定义游戏元素
codeGameRef.value?.addCustomElement({
  id: 'custom-hero',
  type: 'hero',
  position: { x: 2, y: 3 },
  image: '/path/to/hero-image.png', // 自定义图片
  size: 0.8 // 80%大小
})

// 移除游戏元素
codeGameRef.value?.removeElement('custom-hero')
```

## 🎯 游戏API

玩家可以在代码编辑器中使用以下API：

### 移动命令

```javascript
// 向右移动一格
moveRight()

// 向左移动一格
moveLeft()

// 向上移动一格
moveUp()

// 向下移动一格
moveDown()
```

### 查询方法

```javascript
// 获取英雄当前位置
const position = getHeroPosition()
console.log(position) // { x: 1, y: 5 }

// 检查指定位置是否有障碍物
const hasObstacle = hasObstacle(5, 5)

// 检查指定位置是否有宝箱
const hasTreasure = hasTreasure(8, 5)

// 设置动画速度（影响移动动画的快慢）
setAnimationSpeed(2) // 2倍速
```

### 示例代码

#### 关卡 1 示例
```javascript
// 简单移动到宝箱
moveRight()
moveRight()
moveRight()
moveRight()
moveRight()
moveRight()
moveRight()
```

#### 关卡 2 示例
```javascript
// 绕过障碍物
moveRight()
moveRight()
moveRight()
moveRight()
moveUp()
moveRight()
moveRight()
moveDown()
moveRight()
moveRight()
moveRight()
```

#### 关卡 3 示例
```javascript
// 使用循环优化
for(let i = 0; i < 11; i++) {
  moveRight()
}
```

## 🏗️ 技术架构

### 核心技术栈
- **Vue 3** - 响应式框架
- **TypeScript** - 类型安全
- **Element Plus** - UI 组件库
- **Canvas API** - 游戏渲染

### 设计模式
- **组件化设计** - 独立可复用的游戏组件
- **事件驱动** - 完整的游戏事件系统
- **状态管理** - 响应式的游戏状态管理
- **安全执行** - 沙箱化的代码执行环境

### 文件结构
```
src/components/Oj/Games/MoveCodeGame/
├── MoveCodeGame.vue          # 主游戏组件
├── README.md                # 组件文档
├── IMPROVEMENTS.md          # 改进说明
├── REFACTOR_SUMMARY.md      # 重构总结
├── BUG_FIXES.md            # 问题修复说明
├── LEVELS_GUIDE.md         # 关卡指南
├── ANSWER_FEATURE.md       # 答案功能说明
├── LEVEL_SELECTOR_FEATURE.md # 关卡选择功能说明
├── MAZE_DESIGN.md          # 迷宫设计说明
├── NEW_FEATURES_LEVELS_9_10.md # 关卡9-10和设置面板功能说明
├── NEW_COMMANDS_GUIDE.md   # 新增指令使用指南
├── ANSWER_VERIFICATION.md  # 答案验证文档
├── test-cases.md           # 测试用例
└── examples/               # 示例和测试
    └── AnimationDemo.vue   # 动画演示
```

## 🎨 设计理念

### Apple 风格设计
- **毛玻璃效果**：使用 `backdrop-filter` 实现现代化的毛玻璃背景
- **圆角设计**：统一的 16px 圆角设计语言
- **渐变背景**：优雅的渐变色彩搭配
- **微交互**：按钮悬停效果和平滑过渡动画

### 用户体验
- **直观操作**：简单明了的操作界面
- **即时反馈**：实时的游戏状态反馈
- **渐进学习**：由浅入深的关卡设计
- **错误友好**：友好的错误提示和处理

## 🎬 动画系统

### 动画特性

- **平滑移动**：参考pen.ts的实现，使用requestAnimationFrame和缓动函数实现平滑移动
- **可调速度**：支持0.1-10倍速的动画速度调节
- **视觉反馈**：英雄移动时会根据方向改变朝向
- **闪烁效果**：宝箱支持闪烁动画效果

### 动画原理

```typescript
// 缓动函数 - 提供平滑的动画效果
const easeInOutQuad = (t: number): number => {
  return t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t
}

// 线性插值
const lerp = (start: number, end: number, t: number): number => {
  return start + (end - start) * t
}

// 动画循环
const animateMove = () => {
  const elapsed = Date.now() - startTime
  const progress = Math.min(elapsed / duration, 1)
  const easedProgress = easeInOutQuad(progress)

  // 计算当前位置
  currentPos = {
    x: lerp(startPos.x, endPos.x, easedProgress),
    y: lerp(startPos.y, endPos.y, easedProgress)
  }

  if (progress < 1) {
    requestAnimationFrame(animateMove)
  }
}
```

## 🖼️ 自定义元素

### 元素类型

```typescript
interface GameElement {
  id: string
  type: 'hero' | 'treasure' | 'obstacle'
  position: Position
  symbol?: string // 默认符号
  image?: string // 自定义图片URL
  color?: string // 自定义颜色
  size?: number // 大小比例 (0-1)
}
```

### 使用自定义图片

```typescript
// 添加自定义英雄
const customHero = {
  id: 'my-hero',
  type: 'hero',
  position: { x: 1, y: 1 },
  image: '/images/knight.png',
  size: 0.9
}

codeGameRef.value?.addCustomElement(customHero)
```

### 图片加载机制

- 支持异步图片加载
- 内置图片缓存系统
- 加载失败时自动回退到默认符号
- 支持常见图片格式（PNG、JPG、SVG等）

## 🔧 扩展开发

### 添加新关卡

在 `levels` 数组中添加新的关卡配置：

```typescript
{
  id: 4,
  objective: '收集多个宝箱',
  learningContent: '条件判断、while循环',
  availableCommands: ['moveRight()', 'moveLeft()', 'moveUp()', 'moveDown()', 'while循环', 'if条件'],
  codePlaceholder: '// 使用条件判断和循环收集所有宝箱',
  successMessage: '恭喜！你学会了条件判断！',
  heroStart: { x: 1, y: 5 },
  treasures: [{ x: 8, y: 5 }, { x: 12, y: 3 }],
  obstacles: [{ x: 5, y: 5 }, { x: 10, y: 4 }]
}
```

### 添加新的游戏元素

可以扩展 `GameElement` 接口来支持更多游戏元素：

```typescript
interface GameElement {
  position: Position
  type: 'hero' | 'treasure' | 'obstacle' | 'enemy' | 'powerup'
  id: string
  properties?: Record<string, any>
}
```

## 📝 测试

访问测试页面查看完整的游戏演示：
```
/src/views/oj/glowc/test/index.vue
```

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进这个组件！

## 📄 许可证

MIT License
