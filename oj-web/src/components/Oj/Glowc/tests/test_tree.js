/**
 * 树结构测试示例
 * 
 * 本示例展示了树的基本操作:
 * - 创建树
 * - 添加节点
 * - 修改和查询节点
 * - 树的遍历
 */

// 初始化树，创建根节点
tree.init("Root");

// 获取根节点ID，用于后续添加子节点
let rootId = tree.getRootId();

// 添加第一层子节点
let node1 = tree.addChild(rootId, "Node 1");
let node2 = tree.addChild(rootId, "Node 2");
let node3 = tree.addChild(rootId, "Node 3");

pen.sleep(1000);

// 添加第二层子节点
let node11 = tree.addChild(node1, "Node 1.1");
let node12 = tree.addChild(node1, "Node 1.2");
let node21 = tree.addChild(node2, "Node 2.1");
let node31 = tree.addChild(node3, "Node 3.1");
let node32 = tree.addChild(node3, "Node 3.2");

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
tree.preOrder(); 