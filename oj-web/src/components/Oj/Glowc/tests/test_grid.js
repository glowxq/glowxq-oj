/**
 * 二维数组(网格)功能测试示例
 * 
 * 本示例展示了二维数组(网格)的基本操作:
 * - 创建二维数组
 * - 按行列访问和设置元素
 * - 按索引访问和设置元素
 * - 高亮显示操作
 */

// 创建一个5x5的二维数组(网格)
grid.build(5, 5);

// 按行列访问和设置元素
for (let i = 0; i < 5; i++) {
  // 设置对角线元素为特定值
  grid.setValueRC(i, i, i * 10);
  
  // 高亮显示该单元格
  grid.brightRC(i, i);
}

// 等待一段时间以便观察
pen.sleep(1000);

// 访问特定元素并输出
let value = grid.getValueRC(2, 2);
pen.sleep(500);

// 获取网格的行数和列数
let rows = grid.getRows();
let cols = grid.getCols();

// 按索引访问元素（索引 = 行 * 列数 + 列）
for (let i = 0; i < rows; i++) {
  // 获取每行的起始和结束索引
  let start = grid.getRowStart(i);
  let end = grid.getRowEnd(i);
  
  // 遍历这一行的所有元素
  for (let idx = start; idx < end; idx++) {
    // 设置行边界元素的值
    if (idx === start || idx === end - 1) {
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
for (let i = 0; i < 8; i++) {
  grid.setValueRC(i, i, "X");
  grid.setValueRC(i, 7-i, "O");
}

// 高亮显示
for (let i = 0; i < 8; i++) {
  grid.brightRC(i, i);
  grid.brightRC(i, 7-i);
}

// 完成演示
pen.sleep(1000); 