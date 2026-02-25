/**
 * 测试模板集合
 * 包含所有可视化组件的测试代码模板
 */

/**
 * 测试模板接口定义
 */
export interface TestTemplates {
  pen: string;
  arr: string;
  grid: string;
  tree: string;
  list: string;
}

// 画笔测试模板
export const penTestTemplate = `// 画笔测试代码 - C++语法风格
pen.clear();
pen.background(2);

// 基本绘制操作 - for循环
pen.color(1);
pen.move(50, 50);

// 使用for循环绘制正方形
for (int i = 0; i < 4; i++) {
  pen.fd(80);
  pen.rt(90);
}

// 使用for循环绘制五角星
for (int i = 0; i < 5; i++) {
  pen.color(i);
  pen.circle(10);
  pen.triangleF(15);
  // Forward 100 pixels
  pen.fd(100);
  // Right turn by 144 degrees to form a star
  pen.rt(144);
}

// 使用if-else条件判断
pen.move(180, 50);
pen.color("blue");
for (int i = 0; i < 8; i++) {
  if (i % 2 == 0) {
    pen.color("blue");
  } else {
    pen.color("green");
  }
  pen.fd(30);
  pen.rt(45);
}

// 使用while循环
pen.move(300, 50);
pen.color("purple");
int j = 0;
while (j < 6) {
  pen.fd(35);
  pen.rt(60);
  j++;
}

// 使用do-while循环
pen.move(90, 200);
pen.color("orange");
int k = 0;
do {
  pen.circle(10 + k * 5);
  pen.move(90, 200 + k * 25);
  k++;
} while (k < 4);

// 嵌套循环 - 绘制图案
pen.move(220, 200);
pen.color("teal");
for (int row = 0; row < 3; row++) {
  for (int col = 0; col < 3; col++) {
    if ((row + col) % 2 == 0) {
      pen.color("teal");
      pen.circleF(10);
    } else {
      pen.color("magenta");
      pen.squareF(15);
    }
    pen.move(220 + col * 25, 200 + row * 25);
  }
}

// switch语句演示
pen.move(350, 200);
for (int i = 0; i < 4; i++) {
  switch (i) {
    case 0:
      pen.color("red");
      pen.fd(35);
      break;
    case 1:
      pen.color("blue");
      pen.fd(35);
      break;
    case 2:
      pen.color("green");
      pen.fd(35);
      break;
    default:
      pen.color("black");
      pen.fd(35);
      break;
  }
  pen.rt(90);
}

// 三元运算符
pen.move(130, 300);
for (int i = 0; i < 6; i++) {
  pen.color(i % 2 == 0 ? "darkblue" : "darkred");
  pen.fd(i % 2 == 0 ? 40 : 20);
  pen.rt(60);
}

// 复杂条件
pen.move(270, 350);
for (int i = 0; i < 8; i++) {
  if (i > 2 && i < 6) {
    pen.color("gold");
  } else if (i == 0 || i == 7) {
    pen.color("silver");
  } else {
    pen.color("bronze");
  }
  pen.fd(25);
  pen.rt(45);
}

// 画一个螺旋
pen.move(400, 350);
pen.color("deepskyblue");
for (int i = 0; i < 20; i++) {
  pen.fd(2 + i * 1.5);
  pen.rt(30);
}

pen.finish();`;

// 一维数组测试模板
export const arrTestTemplate = `// 一维数组测试代码 - C++语法风格
arr.clear();

// 创建一个大小为10的一维数组，随机初始化
arr.build(10);

// 访问元素
for (int i = 0; i < 10; i += 2) {
  // 高亮显示偶数位置的元素
  arr.bright(i);
}

// 等待一段时间以便观察
pen.sleep(1000);

// 设置元素值
for (int i = 0; i < 10; i += 3) {
  // 设置索引为3的倍数的元素值为99
  arr.setValue(i, 99);
}

// 等待一段时间以便观察
pen.sleep(1000);

// 获取特定元素值
int value = arr.getValue(5);
pen.sleep(500);

// 清除数组
arr.clear();

// 创建一个大小为15的一维数组，所有元素初始化为字符串"B"
arr.build(15, "B");

// 在数组中创建模式
for (int i = 0; i < 15; i++) {
  if (i % 2 == 0) {
    arr.setValue(i, "偶");
  } else {
    arr.setValue(i, "奇");
  }
}

// 高亮显示所有元素
for (int i = 0; i < 15; i++) {
  arr.bright(i);
}

// 完成演示
pen.sleep(1000);`;

// 二维数组测试模板
export const gridTestTemplate = `// 二维数组测试代码 - C++语法风格
grid.clear();

// 创建一个5x5的二维数组(网格)
grid.build(5, 5);

// 按行列访问和设置元素
for (int i = 0; i < 5; i++) {
  // 设置对角线元素为特定值
  grid.setValueRC(i, i, i * 10);
  
  // 高亮显示该单元格
  grid.brightRC(i, i);
}

// 等待一段时间以便观察
pen.sleep(1000);

// 访问特定元素并输出
int value = grid.getValueRC(2, 2);
pen.sleep(500);

// 获取网格的行数和列数
int rows = grid.getRows();
int cols = grid.getCols();

// 按索引访问元素（索引 = 行 * 列数 + 列）
for (int i = 0; i < rows; i++) {
  // 获取每行的起始和结束索引
  int start = grid.getRowStart(i);
  int end = grid.getRowEnd(i);
  
  // 遍历这一行的所有元素
  for (int idx = start; idx < end; idx++) {
    // 设置行边界元素的值
    if (idx == start || idx == end - 1) {
      grid.setValueIdx(idx, 99);
      grid.brightIdx(idx);
    }
  }
}

// 等待一段时间以便观察
pen.sleep(1000);

// 清除网格
grid.clear();

// 创建一个8x8的二维数组，所有元素初始化为字符串"A"
grid.build(8, 8, "A");

// 在网格中绘制一个图案
for (int i = 0; i < 8; i++) {
  grid.setValueRC(i, i, "X");
  grid.setValueRC(i, 7-i, "O");
}

// 高亮显示
for (int i = 0; i < 8; i++) {
  grid.brightRC(i, i);
  grid.brightRC(i, 7-i);
}

// 完成演示
pen.sleep(1000);`;

// 树结构测试模板
export const treeTestTemplate = `// 树结构测试代码 - C++语法风格
// 初始化树，创建根节点
tree.init("Root");

// 获取根节点ID，用于后续添加子节点
int rootId = tree.getRootId();

// 添加第一层子节点
int node1 = tree.addChild(rootId, "Node 1");
int node2 = tree.addChild(rootId, "Node 2");
int node3 = tree.addChild(rootId, "Node 3");

pen.sleep(1000);

// 添加第二层子节点
int node11 = tree.addChild(node1, "Node 1.1");
int node12 = tree.addChild(node1, "Node 1.2");
int node21 = tree.addChild(node2, "Node 2.1");
int node31 = tree.addChild(node3, "Node 3.1");
int node32 = tree.addChild(node3, "Node 3.2");

pen.sleep(1000);

// 添加第三层子节点
tree.addChild(node11, "Node 1.1.1");
tree.addChild(node11, "Node 1.1.2");
tree.addChild(node32, "Node 3.2.1");

pen.sleep(1000);

// 高亮显示特定节点
tree.highlight(node2);
tree.highlight(node32);

// 修改节点的值
tree.setValue(node21, "Modified 2.1");
tree.setValue(node12, 42);

pen.sleep(1000);

// 执行树的遍历
tree.preOrder();  // 前序遍历
pen.sleep(500);

tree.postOrder(); // 后序遍历
pen.sleep(500);

tree.levelOrder(); // 层序遍历
pen.sleep(500);

// 删除一个节点及其子树
tree.removeNode(node3);
pen.sleep(1000);

// 执行前序遍历查看删除后的树结构
tree.preOrder();`;

// 链表测试模板
export const listTestTemplate = `// 链表测试代码 - C++语法风格
// 初始化链表（默认为单向链表）
list.init();

// 在尾部添加节点
list.addTail(10);
list.addTail(20);
list.addTail(30);

pen.sleep(1000);

// 在头部添加节点
list.addHead(5);
list.addHead(1);

pen.sleep(1000);

// 在指定位置插入节点
list.insertAt(3, 15);
list.insertAt(5, 25);

pen.sleep(1000);

// 获取链表长度
int length = list.getLength();
pen.sleep(500);

// 遍历链表
list.traverse();
pen.sleep(500);

// 修改节点的值
list.setValue(2, 12);
list.setValue(4, 22);

pen.sleep(1000);

// 删除指定位置的节点
list.removeAt(3);
pen.sleep(500);

// 根据值删除节点
list.removeByValue(22);
pen.sleep(500);

// 删除头尾节点
list.removeHead();
list.removeTail();

pen.sleep(1000);

// 遍历链表
list.traverse();
pen.sleep(1000);

// 清空链表
list.clear();
pen.sleep(500);

// 重新初始化为双向链表
list.init(true);

// 添加一些节点
list.addHead("A");
list.addTail("B");
list.addTail("C");
list.addTail("D");
list.addTail("E");

pen.sleep(1000);

// 遍历和反向遍历双向链表
list.traverse();
pen.sleep(500);
list.reverseTraverse();`;

// 导出一个统一的对象，包含所有测试模板
const testTemplates: TestTemplates = {
  pen: penTestTemplate,
  arr: arrTestTemplate,
  grid: gridTestTemplate,
  tree: treeTestTemplate,
  list: listTestTemplate
};

export default testTemplates; 