// 测试绘制五角星的代码
// 这是用户提供的代码，用于测试五角星绘制是否正确
// 在每个顶点处绘制一个圆和一个三角形

// 绘制五角星
const drawStar = async (pen) => {
  // 先清屏，并移动到画布中心
  await pen.clear();
  
  // 设置初始角度
  await pen.rt(90);
  
  // 设置五角星大小和颜色
  const starSize = 100;
  await pen.color(4); // 绿色
  
  // 向上移动一些距离，从那里开始绘制
  await pen.penUp();
  await pen.fd(starSize);
  await pen.penDown();
  
  // 五角星需要五个点，每个点之间旋转144度
  for (let i = 0; i < 5; i++) {
    // 绘制到下一个点
    await pen.fd(starSize * 2 * Math.sin(Math.PI / 5));
    // 右转144度准备绘制下一条边
    await pen.rt(144);
  }
  
  // 在每个顶点添加装饰
  await pen.penUp();
  await pen.rt(36);
  
  for (let i = 0; i < 5; i++) {
    await pen.color(i + 1);
    await pen.fd(starSize);
    await pen.penDown();
    await pen.circle(10);
    await pen.triangleF(15);
    await pen.penUp();
    await pen.moveFd(-starSize);
    await pen.rt(72);
  }
};

// 导出绘制函数
export default drawStar; 