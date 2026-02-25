# 简化指令系统演示

## 🎯 优化目标
1. **缩短指令长度** - 让小朋友更容易输入和记忆
2. **增强功能性** - 添加更多实用的检测和获取指令
3. **保持兼容性** - 旧指令仍然可用

## 📝 指令对比表

### 基础移动指令
| 新指令 | 旧指令 | 说明 |
|--------|--------|------|
| `right()` | `moveRight()` | 向右移动一格 |
| `left()` | `moveLeft()` | 向左移动一格 |
| `up()` | `moveUp()` | 向上移动一格 |
| `down()` | `moveDown()` | 向下移动一格 |

### 高级移动指令
| 新指令 | 旧指令 | 说明 |
|--------|--------|------|
| `go(步数)` | `fd(步数)` | 向前移动指定步数 |
| `dir(方向)` | `direction(方向)` | 设置朝向 |
| `turn()` | `rt()` | 向右转90度 |

### 战斗指令
| 新指令 | 旧指令 | 说明 |
|--------|--------|------|
| `hit()` | `attack()` | 攻击前方怪物 |

## 🆕 新增功能指令

### 位置获取指令
```javascript
// 获取英雄位置
const heroPos = pos()
console.log("英雄在:", heroPos.x, heroPos.y)

// 获取宝箱位置（只有一个宝箱时可用）
const treasure = treasurePos()
console.log("宝箱在:", treasure.x, treasure.y)

// 获取武器位置
const sword = weaponPos(1)    // 1=剑
const staff = weaponPos(2)    // 2=魔杖

// 检测当前持有的武器
const myWeapon = weapon()
if(myWeapon === 1) console.log("持有剑")
if(myWeapon === 2) console.log("持有魔杖")
if(myWeapon === 0) console.log("没有武器")
```

### 方向检测指令
```javascript
// 检测前方是否有障碍物
if(see()) {
  console.log("前方有障碍物")
}

// 检测指定方向是否有障碍物
if(see(2)) {  // 2=右方
  console.log("右方有障碍物")
}

// 检测前方是否有宝箱
if(seeTreasure()) {
  console.log("前方有宝箱")
}

// 检测前方是否有武器
if(seeWeapon()) {
  console.log("前方有武器")
}

// 检测前方是否有怪物
if(seeMonster()) {
  console.log("前方有怪物")
}
```

## 💡 实际应用示例

### 示例1：智能寻宝
```javascript
// 使用新指令的智能寻宝代码
const treasure = treasurePos()  // 获取宝箱位置
const hero = pos()              // 获取英雄位置

// 计算需要移动的距离
const dx = treasure.x - hero.x
const dy = treasure.y - hero.y

// 智能移动到宝箱
if(dx > 0) {
  dir(2)  // 朝右
  go(dx)  // 前进dx步
}
if(dy > 0) {
  dir(3)  // 朝下
  go(dy)  // 前进dy步
}
```

### 示例2：智能战斗
```javascript
// 检测当前武器类型
const myWeapon = weapon()

if(myWeapon === 0) {
  // 没有武器，先找武器
  const sword = weaponPos(1)
  // 移动到剑的位置...
} else {
  // 有武器，检测前方是否有怪物
  if(seeMonster()) {
    hit()  // 攻击怪物
  } else {
    go(1)  // 继续前进
  }
}
```

### 示例3：智能避障
```javascript
// 智能前进，遇到障碍物自动绕行
while(!seeTreasure()) {
  if(!see()) {
    go(1)  // 前方没有障碍物，前进
  } else {
    // 前方有障碍物，尝试绕行
    turn()  // 向右转
    if(!see()) {
      go(1)  // 右方没有障碍物，前进
    } else {
      turn()  // 继续转向
    }
  }
}
```

## 🔧 技术实现特点

1. **向后兼容** - 所有旧指令仍然可用
2. **智能检测** - 方向参数可选，不传则检测前方
3. **错误处理** - 对不合理的操作给出友好的错误提示
4. **类型安全** - 严格的参数类型检查

## 🎮 教学优势

1. **降低学习门槛** - 指令更短，更容易记忆
2. **增强交互性** - 更多的检测指令让游戏更智能
3. **培养编程思维** - 鼓励学生编写更通用的代码
4. **渐进式学习** - 从简单指令到复杂逻辑的平滑过渡

这个简化指令系统让小朋友能够更轻松地编写代码，同时提供了更强大的功能来创建智能的游戏角色！
