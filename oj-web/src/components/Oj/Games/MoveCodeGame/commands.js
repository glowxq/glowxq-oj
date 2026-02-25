// 编程指令信息数据
export const commandsData = [
  {
    category: '基础移动指令',
    icon: '🎮',
    description: '控制英雄在地图上移动的基本指令',
    commands: [
      {
        name: 'right()',
        description: '向右移动一格',
        syntax: 'right()',
        example: 'right() // 英雄向右移动一格',
        level: '基础',
        color: '#3498db',
        oldName: 'moveRight()'
      },
      {
        name: 'left()',
        description: '向左移动一格',
        syntax: 'left()',
        example: 'left() // 英雄向左移动一格',
        level: '基础',
        color: '#3498db',
        oldName: 'moveLeft()'
      },
      {
        name: 'up()',
        description: '向上移动一格',
        syntax: 'up()',
        example: 'up() // 英雄向上移动一格',
        level: '基础',
        color: '#3498db',
        oldName: 'moveUp()'
      },
      {
        name: 'down()',
        description: '向下移动一格',
        syntax: 'down()',
        example: 'down() // 英雄向下移动一格',
        level: '基础',
        color: '#3498db',
        oldName: 'moveDown()'
      },
      {
        name: 'printf()',
        description: '输出信息到控制台',
        syntax: 'printf(内容)',
        example: 'printf("Hello World")\nprintf("英雄位置:", x, y)',
        level: '基础',
        color: '#9b59b6',
        params: [
          { name: '内容', type: 'any', description: '要输出的内容，可以是文字、数字或变量' }
        ],
        returns: { type: 'void', description: '无返回值' },
        note: '用于输出信息和调试程序'
      }
    ]
  },
  {
    category: '高级移动指令',
    icon: '🚀',
    description: '更高效的移动控制指令',
    commands: [
      {
        name: 'dir()',
        description: '设置英雄面向方向',
        syntax: 'dir(方向)',
        example: 'dir(2) // 1=上, 2=右, 3=下, 4=左',
        level: '中级',
        color: '#9b59b6',
        params: [
          { name: '方向', type: 'number', description: '1=上, 2=右, 3=下, 4=左' }
        ],
        oldName: 'direction()'
      },
      {
        name: 'go()',
        description: '向前移动指定步数',
        syntax: 'go(步数)',
        example: 'go(3) // 向前移动3格',
        level: '中级',
        color: '#9b59b6',
        params: [
          { name: '步数', type: 'number', description: '要移动的格子数量，默认为1' }
        ],
        oldName: 'fd()'
      },
      {
        name: 'turn()',
        description: '向右转90度',
        syntax: 'turn()',
        example: 'turn() // 向右转向',
        level: '中级',
        color: '#9b59b6',
        oldName: 'rt()'
      }
    ]
  },
  {
    category: '循环控制',
    icon: '🔄',
    description: '重复执行代码块的控制结构',
    commands: [
      {
        name: 'for循环',
        description: '重复执行指定次数的代码',
        syntax: 'for(let i = 0; i < 次数; i++) { 代码块 }',
        example: 'for(let i = 0; i < 3; i++) {\n  moveRight()\n}',
        level: '中级',
        color: '#e67e22'
      },
      {
        name: 'while循环',
        description: '在条件为真时重复执行代码',
        syntax: 'while(条件) { 代码块 }',
        example: 'while(!hasTreasure(x, y)) {\n  moveRight()\n}',
        level: '中级',
        color: '#e67e22'
      }
    ]
  },
  {
    category: '条件判断',
    icon: '🤔',
    description: '根据条件执行不同的代码分支',
    commands: [
      {
        name: 'if-else',
        description: '条件判断语句',
        syntax: 'if(条件) { 代码块1 } else { 代码块2 }',
        example: 'if(hasObstacle(2, 3)) {\n  moveUp()\n} else {\n  moveRight()\n}',
        level: '中级',
        color: '#f39c12'
      }
    ]
  },
  {
    category: '位置获取指令',
    icon: '📍',
    description: '获取游戏世界中各种元素的位置信息',
    commands: [
      {
        name: 'posX()',
        description: '获取英雄当前横坐标',
        syntax: 'posX()',
        example: 'const heroX = posX()\nconsole.log("英雄横坐标:", heroX)',
        level: '中级',
        color: '#27ae60',
        returns: { type: 'number', description: '返回英雄的横坐标(x)' },
        oldName: 'getHeroPosition()'
      },
      {
        name: 'posY()',
        description: '获取英雄当前纵坐标',
        syntax: 'posY()',
        example: 'const heroY = posY()\nconsole.log("英雄纵坐标:", heroY)',
        level: '中级',
        color: '#27ae60',
        returns: { type: 'number', description: '返回英雄的纵坐标(y)' },
        oldName: 'getHeroPosition()'
      },
      {
        name: 'treasureX()',
        description: '获取宝箱横坐标（只有一个宝箱时可用）',
        syntax: 'treasureX()',
        example: 'const treasureX = treasureX()\nconsole.log("宝箱横坐标:", treasureX)',
        level: '中级',
        color: '#27ae60',
        returns: { type: 'number', description: '返回宝箱的横坐标(x)' },
        note: '地图上只有一个宝箱时才能使用，否则会报错'
      },
      {
        name: 'treasureY()',
        description: '获取宝箱纵坐标（只有一个宝箱时可用）',
        syntax: 'treasureY()',
        example: 'const treasureY = treasureY()\nconsole.log("宝箱纵坐标:", treasureY)',
        level: '中级',
        color: '#27ae60',
        returns: { type: 'number', description: '返回宝箱的纵坐标(y)' },
        note: '地图上只有一个宝箱时才能使用，否则会报错'
      },
      {
        name: 'weaponX()',
        description: '获取武器横坐标',
        syntax: 'weaponX(类型)',
        example: 'const swordX = weaponX(1) // 1=剑, 2=魔杖\nconst staffX = weaponX(2)',
        level: '中级',
        color: '#27ae60',
        params: [
          { name: '类型', type: 'number', description: '1=剑, 2=魔杖' }
        ],
        returns: { type: 'number', description: '返回武器的横坐标(x)' },
        note: '地图上只能同时存在一个同类型武器，否则会报错'
      },
      {
        name: 'weaponY()',
        description: '获取武器纵坐标',
        syntax: 'weaponY(类型)',
        example: 'const swordY = weaponY(1) // 1=剑, 2=魔杖\nconst staffY = weaponY(2)',
        level: '中级',
        color: '#27ae60',
        params: [
          { name: '类型', type: 'number', description: '1=剑, 2=魔杖' }
        ],
        returns: { type: 'number', description: '返回武器的纵坐标(y)' },
        note: '地图上只能同时存在一个同类型武器，否则会报错'
      },
      {
        name: 'weapon()',
        description: '检测当前持有的武器类型',
        syntax: 'weapon()',
        example: 'const myWeapon = weapon()\nif(myWeapon === 1) console.log("持有剑")',
        level: '中级',
        color: '#27ae60',
        returns: { type: 'number', description: '返回 1=剑, 2=魔杖, 0=无武器' }
      }
    ]
  },
  {
    category: '位置检测指令',
    icon: '🔍',
    description: '检查指定位置是否有特定元素',
    commands: [
      {
        name: 'hasObstacle()',
        description: '检查指定位置是否有障碍物',
        syntax: 'hasObstacle(x, y)',
        example: 'if(hasObstacle(3, 4)) {\n  console.log("有障碍物")\n}',
        level: '中级',
        color: '#27ae60',
        params: [
          { name: 'x', type: 'number', description: '横坐标' },
          { name: 'y', type: 'number', description: '纵坐标' }
        ],
        returns: { type: 'boolean', description: '有障碍物返回true，否则返回false' }
      },
      {
        name: 'hasTreasure()',
        description: '检查指定位置是否有宝箱',
        syntax: 'hasTreasure(x, y)',
        example: 'if(hasTreasure(5, 6)) {\n  console.log("找到宝箱！")\n}',
        level: '中级',
        color: '#27ae60',
        params: [
          { name: 'x', type: 'number', description: '横坐标' },
          { name: 'y', type: 'number', description: '纵坐标' }
        ],
        returns: { type: 'boolean', description: '有宝箱返回true，否则返回false' }
      }
    ]
  },
  {
    category: '方向检测指令',
    icon: '🧭',
    description: '检测指定方向是否有特定元素（不传方向则检测前方）',
    commands: [
      {
        name: 'see()',
        description: '检测前方是否有障碍物',
        syntax: 'see(方向)',
        example: 'if(see()) console.log("前方有障碍物")\nif(see(2)) console.log("右方有障碍物")',
        level: '中级',
        color: '#8e44ad',
        params: [
          { name: '方向', type: 'number', description: '可选，1=上, 2=右, 3=下, 4=左，不传则检测前方', optional: true }
        ],
        returns: { type: 'boolean', description: '有障碍物返回true' },
        oldName: 'isThereObstacles()'
      },
      {
        name: 'seeTreasure()',
        description: '检测指定方向是否有宝箱',
        syntax: 'seeTreasure(方向)',
        example: 'if(seeTreasure()) console.log("前方有宝箱")\nif(seeTreasure(1)) console.log("上方有宝箱")',
        level: '中级',
        color: '#8e44ad',
        params: [
          { name: '方向', type: 'number', description: '可选，1=上, 2=右, 3=下, 4=左，不传则检测前方', optional: true }
        ],
        returns: { type: 'boolean', description: '有宝箱返回true' }
      },
      {
        name: 'seeWeapon()',
        description: '检测指定方向是否有武器',
        syntax: 'seeWeapon(方向)',
        example: 'if(seeWeapon()) console.log("前方有武器")\nif(seeWeapon(3)) console.log("下方有武器")',
        level: '中级',
        color: '#8e44ad',
        params: [
          { name: '方向', type: 'number', description: '可选，1=上, 2=右, 3=下, 4=左，不传则检测前方', optional: true }
        ],
        returns: { type: 'boolean', description: '有武器返回true' }
      },
      {
        name: 'seeMonster()',
        description: '检测指定方向是否有怪物',
        syntax: 'seeMonster(方向)',
        example: 'if(seeMonster()) console.log("前方有怪物")\nif(seeMonster(4)) console.log("左方有怪物")',
        level: '中级',
        color: '#8e44ad',
        params: [
          { name: '方向', type: 'number', description: '可选，1=上, 2=右, 3=下, 4=左，不传则检测前方', optional: true }
        ],
        returns: { type: 'boolean', description: '有怪物返回true' }
      }
    ]
  },
  {
    category: '高级检测指令',
    icon: '🔍',
    description: '更复杂的环境检测功能',
    commands: [
      {
        name: 'thereObstacles()',
        description: '获取周围障碍物的方向信息',
        syntax: 'thereObstacles()',
        example: 'const obstacles = thereObstacles()\nconsole.log("障碍物方向:", obstacles)',
        level: '高级',
        color: '#8e44ad',
        returns: { type: 'array', description: '返回周围障碍物的方向数组' }
      },
      {
        name: 'reachBoundary()',
        description: '获取到达的边界信息',
        syntax: 'reachBoundary()',
        example: 'const boundaries = reachBoundary()\nconsole.log("到达边界:", boundaries)',
        level: '高级',
        color: '#8e44ad',
        returns: { type: 'array', description: '返回到达的边界方向数组' }
      },
      {
        name: 'isReachBoundary()',
        description: '检查指定方向是否到达边界',
        syntax: 'isReachBoundary(方向)',
        example: 'if(isReachBoundary(2)) {\n  console.log("右边到达边界")\n}',
        level: '高级',
        color: '#8e44ad',
        params: [
          { name: '方向', type: 'number', description: '1=上, 2=右, 3=下, 4=左' }
        ],
        returns: { type: 'boolean', description: '指定方向到达边界返回true' }
      }
    ]
  },
  {
    category: '战斗指令',
    icon: '⚔️',
    description: '战斗系统相关的指令',
    commands: [
      {
        name: 'hit()',
        description: '使用当前武器攻击前方怪物',
        syntax: 'hit()',
        example: 'hit() // 攻击前方的怪物',
        level: '中级',
        color: '#e74c3c',
        note: '需要先装备合适的武器：剑⚔️攻击物理系怪物👹，魔杖🪄攻击魔法系怪物🔮',
        oldName: 'attack()'
      }
    ]
  }
]

// 根据难度级别获取颜色
export const getLevelColor = (level) => {
  const colors = {
    '基础': '#3498db',
    '中级': '#f39c12', 
    '高级': '#e74c3c'
  }
  return colors[level] || '#95a5a6'
}

// 根据指令名称查找指令信息
export const findCommand = (commandName) => {
  for (const category of commandsData) {
    const command = category.commands.find(cmd => cmd.name === commandName)
    if (command) {
      return { ...command, category: category.category }
    }
  }
  return null
}

// 获取所有指令名称列表
export const getAllCommandNames = () => {
  const names = []
  commandsData.forEach(category => {
    category.commands.forEach(command => {
      names.push(command.name)
    })
  })
  return names
}

// 根据难度级别筛选指令
export const getCommandsByLevel = (level) => {
  const filtered = []
  commandsData.forEach(category => {
    const commands = category.commands.filter(cmd => cmd.level === level)
    if (commands.length > 0) {
      filtered.push({
        ...category,
        commands
      })
    }
  })
  return filtered
}
