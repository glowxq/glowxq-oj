/**
 * 链表结构测试示例
 * 
 * 本示例展示了链表的基本操作:
 * - 创建单向和双向链表
 * - 添加、插入和删除节点
 * - 获取和修改节点值
 * - 链表的遍历
 */

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
let length = list.getLength();
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
list.reverseTraverse(); 