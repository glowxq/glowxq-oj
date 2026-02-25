// 命令配置文件

/**
 * 组件类型枚举
 */
export enum ComponentType {
  PEN = 'pen',
  ARRAY = 'arr',
  GRID = 'grid',
  TREE = 'tree',
  LIST = 'list',
  COLORS = 'colors'
}

/**
 * 模板组类型枚举
 */
export enum TemplateGroupType {
  SYNTAX = 'Syntax',
  COMPONENTS = 'Components',
  PEN = 'Pen',
  ARRAY = 'Array',
  GRID = 'Grid',
  TREE = 'Tree',
  LIST = 'List'
}

/**
 * 基础项接口 - 具有标签和标识符
 */
export interface BaseItem {
  label: string;
  id?: string;
}

/**
 * 命令项接口 - 描述一个命令及其功能
 */
export interface CommandItem extends BaseItem {
  description: string;
  code: string;
}

/**
 * 代码模板接口 - 用于代码编辑器中插入的模板
 */
export interface CodeTemplate extends BaseItem {
  content: string;
  group: string;
}

/**
 * 颜色映射接口 - 定义颜色编码与颜色值的对应关系
 */
export interface ColorMap {
  code: number;
  color: string;
  name: string;
}

/**
 * 命令组接口 - 相关命令的分组
 */
export interface CommandGroup {
  title: string;
  commands: CommandItem[];
}

// ================ 颜色配置 ================
/**
 * 颜色映射表 - 从pen.ts提取
 */
export const COLOR_MAPS: ColorMap[] = [
  { code: 1, color: '#FF0000', name: '红色' },
  { code: 2, color: '#FFA500', name: '橙色' },
  { code: 3, color: '#FFFF00', name: '黄色' },
  { code: 4, color: '#00FF00', name: '绿色' },
  { code: 5, color: '#00FFFF', name: '青色' },
  { code: 6, color: '#0000FF', name: '蓝色' },
  { code: 7, color: '#800080', name: '紫色' },
  { code: 8, color: '#FFC0CB', name: '粉色' },
  { code: 9, color: '#FFB6C1', name: '浅粉色' },
  { code: 10, color: '#FFFF66', name: '浅黄色' },
  { code: 11, color: '#87CEEB', name: '天蓝色' },
  { code: 12, color: '#B0C4DE', name: '淡蓝色' },
  { code: 13, color: '#FFD700', name: '金色' },
  { code: 14, color: '#5C3317', name: '褐色' },
  { code: 15, color: '#808080', name: '灰色' },
  { code: 16, color: '#000000', name: '黑色' }
];

// ================ 命令配置 ================
/**
 * 命令配置命名空间
 */
export namespace Commands {
  /**
   * 画笔基本操作命令
   */
  export const PEN_BASIC: CommandItem[] = [
    { label: 'pen.fd(distance)', description: '向前移动指定距离', code: 'pen.fd(100);' },
    { label: 'pen.rt(angle)', description: '向右旋转指定角度', code: 'pen.rt(90);' },
    { label: 'pen.lt(angle)', description: '向左旋转指定角度', code: 'pen.lt(90);' },
    { label: 'pen.move(x, y)', description: '移动到指定坐标', code: 'pen.move(0, 0);' },
    { label: 'pen.moveFd(distance)', description: '向当前方向移动但不画线', code: 'pen.moveFd(100);' },
    { label: 'pen.sleep(ms)', description: '等待指定毫秒', code: 'pen.sleep(1000);' },
    { label: 'pen.finish()', description: '立即完成所有动画', code: 'pen.finish();' },
    { label: 'pen.clear()', description: '清空画布', code: 'pen.clear();' },
    { label: 'pen.getX()', description: '获取当前X坐标', code: 'pen.getX();' },
    { label: 'pen.getY()', description: '获取当前Y坐标', code: 'pen.getY();' }
  ];

  /**
   * 画笔绘图操作命令
   */
  export const PEN_DRAWING: CommandItem[] = [
    { label: 'pen.circle(radius)', description: '绘制圆形轮廓', code: 'pen.circle(50);' },
    { label: 'pen.circleF(radius)', description: '绘制填充圆形', code: 'pen.circleF(50);' },
    { label: 'pen.ellipse(radiusX, radiusY)', description: '绘制椭圆轮廓', code: 'pen.ellipse(50, 30);' },
    { label: 'pen.ellipseF(radiusX, radiusY)', description: '绘制填充椭圆', code: 'pen.ellipseF(50, 30);' },
    { label: 'pen.square(size)', description: '绘制正方形轮廓', code: 'pen.square(100);' },
    { label: 'pen.squareF(size)', description: '绘制填充正方形', code: 'pen.squareF(100);' },
    { label: 'pen.color(color)', description: '设置画笔颜色', code: 'pen.color(1);' },
    { label: 'pen.background(color)', description: '设置背景颜色', code: 'pen.background(1);' }
  ];

  /**
   * 画笔颜色操作命令
   */
  export const PEN_COLOR: CommandItem[] = [
    { label: 'pen.color(1-16)', description: '使用颜色代码设置画笔颜色', code: 'pen.color(1);' },
    { label: 'pen.color("#RRGGBB")', description: '使用十六进制颜色值设置画笔颜色', code: 'pen.color("#FF0000");' },
    { label: 'pen.background(1-16)', description: '使用颜色代码设置背景颜色', code: 'pen.background(1);' },
    { label: 'pen.background("#RRGGBB")', description: '使用十六进制颜色值设置背景颜色', code: 'pen.background("#FFFFFF");' }
  ];

  /**
   * 一维数组操作命令
   */
  export const ARRAY: CommandItem[] = [
    { label: 'arr.build(size, initialValue)', description: '创建一个指定大小的数组', code: 'arr.build(5, 0);' },
    { label: 'arr.clear()', description: '清空数组', code: 'arr.clear();' },
    { label: 'arr.setValue(index, value)', description: '设置指定索引的值', code: 'arr.setValue(0, 100);' },
    { label: 'arr.getValue(index)', description: '获取指定索引的值', code: 'arr.getValue(0);' },
    { label: 'arr.bright(index)', description: '高亮显示指定索引的元素', code: 'arr.bright(0);' },
    { label: 'arr.begin()', description: '获取数组开始索引', code: 'arr.begin();' },
    { label: 'arr.end()', description: '获取数组结束索引', code: 'arr.end();' }
  ];

  /**
   * 二维数组操作命令
   */
  export const GRID: CommandItem[] = [
    { label: 'grid.build(rows, cols, initialValue)', description: '创建指定大小的二维数组', code: 'grid.build(5, 5, 0);' },
    { label: 'grid.clear()', description: '清空二维数组', code: 'grid.clear();' },
    { label: 'grid.setValueRC(row, col, value)', description: '按行列设置值', code: 'grid.setValueRC(0, 0, 100);' },
    { label: 'grid.getValueRC(row, col)', description: '按行列获取值', code: 'grid.getValueRC(0, 0);' },
    { label: 'grid.brightRC(row, col)', description: '按行列高亮显示元素', code: 'grid.brightRC(0, 0);' },
    { label: 'grid.setValueIdx(index, value)', description: '按索引设置值', code: 'grid.setValueIdx(0, 100);' },
    { label: 'grid.getValueIdx(index)', description: '按索引获取值', code: 'grid.getValueIdx(0);' },
    { label: 'grid.brightIdx(index)', description: '按索引高亮显示元素', code: 'grid.brightIdx(0);' },
    { label: 'grid.getRows()', description: '获取行数', code: 'grid.getRows();' },
    { label: 'grid.getCols()', description: '获取列数', code: 'grid.getCols();' },
    { label: 'grid.getRowStart(row)', description: '获取行起始索引', code: 'grid.getRowStart(0);' },
    { label: 'grid.getRowEnd(row)', description: '获取行结束索引', code: 'grid.getRowEnd(0);' }
  ];

  /**
   * 树结构操作命令
   */
  export const TREE: CommandItem[] = [
    { label: 'tree.init(rootValue)', description: '初始化树并创建根节点', code: 'tree.init("Root");' },
    { label: 'tree.clear()', description: '清空树', code: 'tree.clear();' },
    { label: 'tree.addChild(parentId, value)', description: '添加子节点，返回节点ID', code: 'tree.addChild(rootId, "Child");' },
    { label: 'tree.removeNode(id)', description: '移除指定ID的节点', code: 'tree.removeNode(nodeId);' },
    { label: 'tree.highlight(id)', description: '高亮显示指定节点', code: 'tree.highlight(nodeId);' },
    { label: 'tree.getValue(id)', description: '获取节点值', code: 'tree.getValue(nodeId);' },
    { label: 'tree.setValue(id, value)', description: '设置节点值', code: 'tree.setValue(nodeId, "NewValue");' },
    { label: 'tree.getRootId()', description: '获取根节点ID', code: 'tree.getRootId();' },
    { label: 'tree.preOrder()', description: '前序遍历树', code: 'tree.preOrder();' },
    { label: 'tree.postOrder()', description: '后序遍历树', code: 'tree.postOrder();' },
    { label: 'tree.levelOrder()', description: '层序遍历树', code: 'tree.levelOrder();' }
  ];

  /**
   * 链表操作命令
   */
  export const LIST: CommandItem[] = [
    { label: 'list.init(isDouble)', description: '初始化链表，isDouble为true创建双向链表', code: 'list.init(false);' },
    { label: 'list.clear()', description: '清空链表', code: 'list.clear();' },
    { label: 'list.setType(isDouble)', description: '设置链表类型', code: 'list.setType(true);' },
    { label: 'list.addHead(value)', description: '在头部添加节点', code: 'list.addHead(100);' },
    { label: 'list.addTail(value)', description: '在尾部添加节点', code: 'list.addTail(100);' },
    { label: 'list.insertAt(index, value)', description: '在指定位置插入节点', code: 'list.insertAt(0, 100);' },
    { label: 'list.removeHead()', description: '删除头节点', code: 'list.removeHead();' },
    { label: 'list.removeTail()', description: '删除尾节点', code: 'list.removeTail();' },
    { label: 'list.removeAt(index)', description: '删除指定位置的节点', code: 'list.removeAt(0);' },
    { label: 'list.removeByValue(value)', description: '删除指定值的节点', code: 'list.removeByValue(100);' },
    { label: 'list.getValue(index)', description: '获取指定位置的节点值', code: 'list.getValue(0);' },
    { label: 'list.setValue(index, value)', description: '设置指定位置的节点值', code: 'list.setValue(0, 100);' },
    { label: 'list.highlight(index)', description: '高亮显示指定节点', code: 'list.highlight(0);' },
    { label: 'list.getLength()', description: '获取链表长度', code: 'list.getLength();' },
    { label: 'list.traverse()', description: '遍历链表', code: 'list.traverse();' },
    { label: 'list.reverseTraverse()', description: '反向遍历链表（双向链表）', code: 'list.reverseTraverse();' }
  ];
}

// ================ 模板配置 ================
/**
 * 模板配置命名空间
 */
export namespace Templates {
  /**
   * 语法结构模板
   */
  export const SYNTAX: CodeTemplate[] = [
    {
      label: 'if-else 条件语句',
      content: 'if (condition) {\n    // code block\n} else {\n    // code block\n}',
      group: TemplateGroupType.SYNTAX
    },
    {
      label: 'if-else if-else 多条件语句',
      content: 'if (condition1) {\n    // code block\n} else if (condition2) {\n    // code block\n} else {\n    // code block\n}',
      group: TemplateGroupType.SYNTAX
    },
    { 
      label: 'for 循环', 
      content: 'for (int i = 0; i < n; i++) {\n    // code block\n}', 
      group: TemplateGroupType.SYNTAX 
    },
    {
      label: 'for 循环（倒序）',
      content: 'for (int i = n - 1; i >= 0; i--) {\n    // code block\n}',
      group: TemplateGroupType.SYNTAX
    },
    { 
      label: 'while 循环', 
      content: 'while (condition) {\n    // code block\n}', 
      group: TemplateGroupType.SYNTAX 
    },
    { 
      label: 'do-while 循环', 
      content: 'do {\n    // code block\n} while (condition);', 
      group: TemplateGroupType.SYNTAX 
    },
    {
      label: 'switch 语句',
      content: 'switch (value) {\n    case 1:\n        // code block\n        break;\n    case 2:\n        // code block\n        break;\n    default:\n        // code block\n}',
      group: TemplateGroupType.SYNTAX
    },
    {
      label: '函数定义',
      content: 'returnType functionName(parameter1, parameter2) {\n    // code block\n    return value;\n}',
      group: TemplateGroupType.SYNTAX
    },
    {
      label: '类定义',
      content: 'class ClassName {\nprivate:\n    // private members\npublic:\n    // public members\nprotected:\n    // protected members\n};',
      group: TemplateGroupType.SYNTAX
    },
    {
      label: '结构体定义',
      content: 'struct StructName {\n    // members\n};',
      group: TemplateGroupType.SYNTAX
    },
    {
      label: '枚举定义',
      content: 'enum EnumName {\n    VALUE1,\n    VALUE2,\n    VALUE3\n};',
      group: TemplateGroupType.SYNTAX
    },
    {
      label: 'try-catch 异常处理',
      content: 'try {\n    // code block\n} catch (const std::exception& e) {\n    // handle exception\n}',
      group: TemplateGroupType.SYNTAX
    },
    {
      label: 'vector 容器',
      content: 'std::vector<type> vec;\nvec.push_back(value);\nvec.pop_back();\nvec.size();\nvec.empty();',
      group: TemplateGroupType.SYNTAX
    },
    {
      label: 'map 容器',
      content: 'std::map<keyType, valueType> map;\nmap[key] = value;\nmap.find(key);\nmap.erase(key);',
      group: TemplateGroupType.SYNTAX
    },
    {
      label: 'set 容器',
      content: 'std::set<type> set;\nset.insert(value);\nset.erase(value);\nset.find(value);',
      group: TemplateGroupType.SYNTAX
    },
    {
      label: 'string 操作',
      content: 'std::string str;\nstr.length();\nstr.substr(start, length);\nstr.find(substr);\nstr.replace(pos, len, newstr);',
      group: TemplateGroupType.SYNTAX
    },
    {
      label: '文件操作',
      content: 'std::ifstream inFile("filename.txt");\nstd::ofstream outFile("filename.txt");\nstd::string line;\nwhile (std::getline(inFile, line)) {\n    // process line\n}',
      group: TemplateGroupType.SYNTAX
    }
  ];

  /**
   * 基础组件模板
   */
  export const COMPONENTS: CodeTemplate[] = [
    { label: 'pen 画笔', content: 'pen', group: TemplateGroupType.COMPONENTS },
    { label: 'arr 数组', content: 'arr', group: TemplateGroupType.COMPONENTS },
    { label: 'grid 二维数组', content: 'grid', group: TemplateGroupType.COMPONENTS },
    { label: 'tree 树结构', content: 'tree', group: TemplateGroupType.COMPONENTS },
    { label: 'list 链表', content: 'list', group: TemplateGroupType.COMPONENTS }
  ];

  /**
   * 画笔操作模板
   */
  export const PEN: CodeTemplate[] = [
    { label: 'fd 前进', content: 'pen.fd(100);', group: TemplateGroupType.PEN },
    { label: 'rt 右转', content: 'pen.rt(90);', group: TemplateGroupType.PEN },
    { label: 'lt 左转', content: 'pen.lt(90);', group: TemplateGroupType.PEN },
    { label: 'move 坐标移动', content: 'pen.move(100, 100);', group: TemplateGroupType.PEN },
    { label: 'moveFd 向前移动', content: 'pen.moveFd(50);', group: TemplateGroupType.PEN },
    { label: 'sleep 等待毫秒', content: 'pen.sleep(1000);', group: TemplateGroupType.PEN },
    { label: 'finish 瞬间完成', content: 'pen.finish();', group: TemplateGroupType.PEN },
    { label: 'clear 清理画板', content: 'pen.clear();', group: TemplateGroupType.PEN },
    { label: 'getX 获取X坐标', content: 'pen.getX();', group: TemplateGroupType.PEN },
    { label: 'getY 获取Y坐标', content: 'pen.getY();', group: TemplateGroupType.PEN },
    { label: 'circle 画圆', content: 'pen.circle(30);', group: TemplateGroupType.PEN },
    { label: 'circleF 画圆', content: 'pen.circleF(30);', group: TemplateGroupType.PEN },
    { label: 'ellipse 椭圆', content: 'pen.ellipse(40, 20);', group: TemplateGroupType.PEN },
    { label: 'ellipseF 椭圆', content: 'pen.ellipseF(40, 20);', group: TemplateGroupType.PEN },
    { label: 'square 正方形', content: 'pen.square(40);', group: TemplateGroupType.PEN },
    { label: 'squareF 正方形', content: 'pen.squareF(40);', group: TemplateGroupType.PEN },
    { label: 'color 修改颜色', content: 'pen.color("blue");', group: TemplateGroupType.PEN },
    { label: 'background 修改背景颜色', content: 'pen.background("lightyellow");', group: TemplateGroupType.PEN }
  ];

  /**
   * 数组操作模板
   */
  export const ARRAY: CodeTemplate[] = [
    { label: 'clear 清理画板', content: 'arr.clear();', group: TemplateGroupType.ARRAY },
    { label: 'build 创建格子', content: 'arr.build(10);', group: TemplateGroupType.ARRAY },
    { label: 'begin 首个索引', content: 'arr.begin();', group: TemplateGroupType.ARRAY },
    { label: 'getValue 获取容器内容', content: 'arr.getValue(0);', group: TemplateGroupType.ARRAY },
    { label: 'end 尾部索引', content: 'arr.end();', group: TemplateGroupType.ARRAY },
    { label: 'bright 高亮格子', content: 'arr.bright(0);', group: TemplateGroupType.ARRAY },
    { label: 'setValue 格子设置内容', content: 'arr.setValue(0, 100);', group: TemplateGroupType.ARRAY }
  ];

  /**
   * A二维数组操作模板
   */
  export const GRID: CodeTemplate[] = [
    { label: 'clear 清理画板', content: 'grid.clear();', group: TemplateGroupType.GRID },
    { label: 'build 创建二维网格', content: 'grid.build(5, 5);', group: TemplateGroupType.GRID },
    { label: 'getRows 获取行数', content: 'grid.getRows();', group: TemplateGroupType.GRID },
    { label: 'getCols 获取列数', content: 'grid.getCols();', group: TemplateGroupType.GRID },
    { label: 'getValueRC 按行列获取值', content: 'grid.getValueRC(0, 0);', group: TemplateGroupType.GRID },
    { label: 'getValueIdx 按索引获取值', content: 'grid.getValueIdx(0);', group: TemplateGroupType.GRID },
    { label: 'setValueRC 按行列设置值', content: 'grid.setValueRC(0, 0, 100);', group: TemplateGroupType.GRID },
    { label: 'setValueIdx 按索引设置值', content: 'grid.setValueIdx(0, 100);', group: TemplateGroupType.GRID },
    { label: 'brightRC 按行列高亮', content: 'grid.brightRC(0, 0);', group: TemplateGroupType.GRID },
    { label: 'brightIdx 按索引高亮', content: 'grid.brightIdx(0);', group: TemplateGroupType.GRID },
    { label: 'getRowStart 获取行起始索引', content: 'grid.getRowStart(0);', group: TemplateGroupType.GRID },
    { label: 'getRowEnd 获取行结束索引', content: 'grid.getRowEnd(0);', group: TemplateGroupType.GRID }
  ];

  /**
   * 树结构操作模板
   */
  export const TREE: CodeTemplate[] = [
    { label: 'init 初始化树', content: 'tree.init("Root");', group: TemplateGroupType.TREE },
    { label: 'clear 清空树', content: 'tree.clear();', group: TemplateGroupType.TREE },
    { label: 'addChild 添加子节点', content: 'tree.addChild(parentId, "Node");', group: TemplateGroupType.TREE },
    { label: 'removeNode 移除节点', content: 'tree.removeNode(nodeId);', group: TemplateGroupType.TREE },
    { label: 'highlight 高亮节点', content: 'tree.highlight(nodeId);', group: TemplateGroupType.TREE },
    { label: 'getValue 获取节点值', content: 'tree.getValue(nodeId);', group: TemplateGroupType.TREE },
    { label: 'setValue 设置节点值', content: 'tree.setValue(nodeId, "NewValue");', group: TemplateGroupType.TREE },
    { label: 'preOrder 前序遍历', content: 'tree.preOrder();', group: TemplateGroupType.TREE },
    { label: 'postOrder 后序遍历', content: 'tree.postOrder();', group: TemplateGroupType.TREE },
    { label: 'levelOrder 层序遍历', content: 'tree.levelOrder();', group: TemplateGroupType.TREE },
    { label: 'getRootId 获取根节点ID', content: 'tree.getRootId();', group: TemplateGroupType.TREE }
  ];

  /**
   * 链表操作模板
   */
  export const LIST: CodeTemplate[] = [
    { label: 'init 初始化链表', content: 'list.init();', group: TemplateGroupType.LIST },
    { label: 'setType 设置链表类型', content: 'list.setType(true);', group: TemplateGroupType.LIST },
    { label: 'clear 清空链表', content: 'list.clear();', group: TemplateGroupType.LIST },
    { label: 'addHead 头部添加节点', content: 'list.addHead(10);', group: TemplateGroupType.LIST },
    { label: 'addTail 尾部添加节点', content: 'list.addTail(20);', group: TemplateGroupType.LIST },
    { label: 'insertAt 指定位置插入', content: 'list.insertAt(1, 15);', group: TemplateGroupType.LIST },
    { label: 'removeHead 删除头节点', content: 'list.removeHead();', group: TemplateGroupType.LIST },
    { label: 'removeTail 删除尾节点', content: 'list.removeTail();', group: TemplateGroupType.LIST },
    { label: 'removeAt 删除指定位置', content: 'list.removeAt(1);', group: TemplateGroupType.LIST },
    { label: 'removeByValue 按值删除', content: 'list.removeByValue(15);', group: TemplateGroupType.LIST },
    { label: 'getValue 获取节点值', content: 'list.getValue(0);', group: TemplateGroupType.LIST },
    { label: 'setValue 设置节点值', content: 'list.setValue(0, 100);', group: TemplateGroupType.LIST },
    { label: 'highlight 高亮节点', content: 'list.highlight(0);', group: TemplateGroupType.LIST },
    { label: 'getLength 获取链表长度', content: 'list.getLength();', group: TemplateGroupType.LIST },
    { label: 'traverse 遍历链表', content: 'list.traverse();', group: TemplateGroupType.LIST },
    { label: 'reverseTraverse 反向遍历', content: 'list.reverseTraverse();', group: TemplateGroupType.LIST }
  ];
}

// ================ 导出配置 ================

/**
 * 命令组配置 - 按组件类型组织命令
 */
export const COMMANDS_CONFIG: Record<string, CommandGroup[]> = {
  [ComponentType.PEN]: [
    { title: '画笔基本操作', commands: Commands.PEN_BASIC },
    { title: '画笔绘图操作', commands: Commands.PEN_DRAWING },
    { title: '画笔颜色操作', commands: Commands.PEN_COLOR }
  ],
  [ComponentType.ARRAY]: [
    { title: '一维数组操作', commands: Commands.ARRAY }
  ],
  [ComponentType.GRID]: [
    { title: '二维数组操作', commands: Commands.GRID }
  ],
  [ComponentType.TREE]: [
    { title: '树结构操作', commands: Commands.TREE }
  ],
  [ComponentType.LIST]: [
    { title: '链表操作', commands: Commands.LIST }
  ],
  [ComponentType.COLORS]: [
    { title: '颜色对照表', commands: [] } // 颜色对照表没有命令，只有颜色映射
  ]
};

/**
 * 合并所有代码模板
 */
export const CODE_TEMPLATES: CodeTemplate[] = [
  ...Templates.SYNTAX,
  ...Templates.COMPONENTS,
  ...Templates.PEN,
  ...Templates.ARRAY,
  ...Templates.GRID,
  ...Templates.TREE,
  ...Templates.LIST
];

/**
 * 从代码模板查找特定组的模板
 * @param group 模板分组
 * @returns 该分组的所有模板
 */
export function getTemplatesByGroup(group: string): CodeTemplate[] {
  return CODE_TEMPLATES.filter(template => template.group === group);
}

/**
 * 从组件类型获取命令组
 * @param componentType 组件类型
 * @returns 命令组数组
 */
export function getCommandGroups(componentType: string): CommandGroup[] {
  return COMMANDS_CONFIG[componentType] || [];
} 