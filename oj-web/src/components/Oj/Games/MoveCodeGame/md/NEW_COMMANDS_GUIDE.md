# 🎮 新增指令使用指南

## 📋 指令概述

MoveCodeGame新增了6个强大的指令，让编程更加灵活和智能：

1. **thereObstacles()** - 检查周围障碍物（返回数组）
2. **reachBoundary()** - 检查周围边界（返回数组）
3. **isThereObstacles(方向)** - 检查指定方向障碍物（返回布尔值）
4. **isReachBoundary(方向)** - 检查指定方向边界（返回布尔值）
5. **fd()** - 根据当前方向前进一格
6. **rt(次数)** - 顺时针旋转方向

## 🧭 方向编码系统

所有方向相关的指令都使用统一的编码系统：
- **1** = 上方 (↑)
- **2** = 右方 (→)
- **3** = 下方 (↓)
- **4** = 左方 (←)

## 📚 详细指令说明

### 1. thereObstacles() 🧱
**功能**：检查英雄周围四个方向是否有障碍物
**返回值**：`number[]` - 包含障碍物方向的数组
**示例**：
```javascript
const obstacles = thereObstacles()
console.log(obstacles)  // 可能输出: [1, 3] 表示上方和下方有障碍物

if (obstacles.length > 0) {
  console.log('周围有障碍物')
} else {
  console.log('周围没有障碍物')
}
```

### 2. reachBoundary() 🌐
**功能**：检查英雄是否接近游戏边界
**返回值**：`number[]` - 包含边界方向的数组
**示例**：
```javascript
const boundaries = reachBoundary()
console.log(boundaries)  // 可能输出: [2, 3] 表示接近右边界和下边界

if (boundaries.includes(2)) {
  console.log('接近右边界')
}
```

### 3. isThereObstacles(direction) 🔍
**功能**：检查指定方向是否有障碍物
**参数**：`direction: number` - 方向编码（1-4）
**返回值**：`boolean` - 有障碍物返回true
**示例**：
```javascript
if (isThereObstacles(1)) {
  console.log('上方有障碍物')
  rt(1)  // 向右转避开
} else {
  console.log('上方没有障碍物，可以前进')
}

// 检查所有方向
for (let dir = 1; dir <= 4; dir++) {
  if (isThereObstacles(dir)) {
    console.log(`方向${dir}有障碍物`)
  }
}
```

### 4. isReachBoundary(direction) 🚧
**功能**：检查指定方向是否到达边界
**参数**：`direction: number` - 方向编码（1-4）
**返回值**：`boolean` - 到达边界返回true
**示例**：
```javascript
if (isReachBoundary(2)) {
  console.log('右方是边界，不能继续前进')
  rt(1)  // 转向
} else {
  fd()   // 继续前进
}

// 边界检测循环
while (!isReachBoundary(2)) {
  fd()  // 一直向右前进直到边界
}
```

### 5. fd() 🚀
**功能**：根据英雄当前面向方向前进一格
**参数**：无
**返回值**：`Promise<void>` - 异步操作
**示例**：
```javascript
// 基础前进
fd()  // 向当前方向前进一格

// 循环前进
for (let i = 0; i < 5; i++) {
  fd()  // 前进5格
}

// 条件前进
if (!isThereObstacles(2)) {  // 如果右方没有障碍物
  rt(1)  // 转向右
  fd()   // 前进
}
```

### 6. rt(times) 🔄
**功能**：顺时针旋转英雄方向
**参数**：`times: number` - 旋转次数（默认为1）
**每次旋转**：90度
**示例**：
```javascript
// 基础旋转
rt()     // 顺时针转90度
rt(1)    // 顺时针转90度（同上）
rt(2)    // 顺时针转180度（掉头）
rt(3)    // 顺时针转270度（相当于逆时针90度）
rt(4)    // 顺时针转360度（回到原方向）

// 实用旋转模式
rt(1)    // 向右转
rt(2)    // 掉头
rt(3)    // 向左转
```

## 🎯 实战应用示例

### 示例1：智能避障前进
```javascript
// 一直向前，遇到障碍物就右转
while (!isReachBoundary(2)) {  // 直到右边界
  if (isThereObstacles(2)) {   // 如果前方有障碍物
    rt(1)                      // 向右转
  } else {
    fd()                       // 前进
  }
}
```

### 示例2：沿墙行走
```javascript
// 右手法则：右手始终贴着墙走
for (let i = 0; i < 20; i++) {
  if (!isThereObstacles(2)) {  // 如果右方没有障碍物
    rt(1)                      // 向右转
    fd()                       // 前进
  } else if (!isThereObstacles(1)) {  // 如果前方没有障碍物
    fd()                       // 直接前进
  } else {
    rt(3)                      // 向左转
  }
}
```

### 示例3：螺旋搜索
```javascript
// 螺旋形搜索模式
let steps = 1
for (let i = 0; i < 4; i++) {
  for (let j = 0; j < 2; j++) {
    for (let k = 0; k < steps; k++) {
      if (!isThereObstacles(2) && !isReachBoundary(2)) {
        fd()
      }
    }
    rt(1)  // 转向
  }
  steps++  // 增加步数
}
```

### 示例4：边界巡逻
```javascript
// 沿着边界巡逻
// 先到达右边界
while (!isReachBoundary(2)) {
  fd()
}

// 沿边界行走
const directions = [3, 4, 1, 2]  // 下、左、上、右
for (let dir of directions) {
  rt(dir - 2)  // 调整到对应方向
  while (!isReachBoundary(dir)) {
    fd()
  }
}
```

## 🎨 方向指示器

英雄现在会显示一个方向箭头，指示当前面向的方向：
- **↑** - 面向上方
- **→** - 面向右方  
- **↓** - 面向下方
- **←** - 面向左方

## 🔧 技术实现细节

### 方向状态管理
```typescript
interface HeroElement {
  direction: 'up' | 'right' | 'down' | 'left'
  // ... 其他属性
}
```

### 指令实现示例
```typescript
const rt = (times: number = 1): void => {
  if (!gameWorld.hero) return
  
  const directions = ['up', 'right', 'down', 'left'] as const
  const currentIndex = directions.indexOf(gameWorld.hero.direction || 'right')
  const newIndex = (currentIndex + times) % 4
  gameWorld.hero.direction = directions[newIndex]
  
  renderGame()  // 立即更新显示
}
```

## 📊 指令对比表

| 指令 | 参数 | 返回值 | 用途 | 示例 |
|------|------|--------|------|------|
| thereObstacles() | 无 | number[] | 检查周围障碍物 | `[1,3]` |
| reachBoundary() | 无 | number[] | 检查周围边界 | `[2]` |
| isThereObstacles() | number | boolean | 检查指定方向障碍物 | `true` |
| isReachBoundary() | number | boolean | 检查指定方向边界 | `false` |
| fd() | 无 | Promise | 前进一格 | - |
| rt() | number | void | 旋转方向 | - |

## 🎓 学习建议

### 初学者
1. **先掌握基础**：从fd()和rt()开始
2. **理解方向**：熟悉1-4的方向编码
3. **简单应用**：用于简单的移动和转向

### 进阶学习
1. **条件判断**：结合if-else使用检测指令
2. **循环应用**：在循环中使用智能移动
3. **算法思维**：实现复杂的移动算法

### 高级应用
1. **路径规划**：设计智能的路径搜索算法
2. **状态机**：实现基于状态的行为控制
3. **优化策略**：寻找最优的移动策略

## 🚀 实际应用场景

### 游戏关卡
- **关卡9**：使用条件判断选择路径
- **关卡10**：复杂的多目标收集

### 编程教学
- **方向概念**：理解二维空间的方向
- **条件逻辑**：基于环境状态做决策
- **算法思维**：设计智能的移动策略

### 创意编程
- **艺术图案**：用移动指令绘制图案
- **游戏AI**：实现简单的游戏AI行为
- **数学应用**：用编程解决几何问题

这些新指令大大增强了MoveCodeGame的编程能力，让学习者能够实现更复杂、更智能的移动逻辑！🎉
