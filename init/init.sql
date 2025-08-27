/*
 Navicat Premium Dump SQL

 Source Server         : Glowxq-old-42.192.87.184
 Source Server Type    : MySQL
 Source Server Version : 80027 (8.0.27)
 Source Host           : 42.192.87.184:3306
 Source Schema         : glowxq_oj

 Target Server Type    : MySQL
 Target Server Version : 80027 (8.0.27)
 File Encoding         : 65001

 Date: 26/08/2025 03:50:31
*/
create database if not exists glowxq_oj;
use glowxq_oj;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for applet_user
-- ----------------------------
DROP TABLE IF EXISTS `applet_user`;
CREATE TABLE `applet_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '小程序用户ID',
  `sys_user_id` int NOT NULL DEFAULT -1 COMMENT '关联的系统用户ID',
  `openid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '小程序用户的唯一标识',
  `unionid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公众号的唯一标识',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务代码',
  `bind_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '绑定代码',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '链接',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '地址',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `subscribe` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否订阅公众号（1是0否）',
  `enable` tinyint NOT NULL DEFAULT 1 COMMENT '状态',
  `sex` int NULL DEFAULT 0 COMMENT '性别，0-未知 1-男性，2-女性',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '删除标识',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `applet_user_username_uindex`(`username` ASC) USING BTREE,
  INDEX `index_openid`(`openid` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '小程序用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of applet_user
-- ----------------------------
INSERT INTO `applet_user` VALUES (1, 2, 'testestst', 'etsetset', 'setset', '', '', '', '姓名1', 'setest', 'stests', NULL, NULL, 'https://glowxq-system.oss-cn-guangzhou.aliyuncs.com/local/jpg/2025/04/26/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20250425001118.jpg', 1, 1, 1, 'F', '2025-04-26 03:48:58', '2025-06-07 19:27:50', 'GLOWXQ');

-- ----------------------------
-- Table structure for code_monitor
-- ----------------------------
DROP TABLE IF EXISTS `code_monitor`;
CREATE TABLE `code_monitor`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `monitor_user_id` bigint NOT NULL COMMENT '被用户id',
  `overlay_user_id` bigint NOT NULL DEFAULT (-(1)) COMMENT '覆盖用户id',
  `monitor_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '被监控人电话',
  `overlay_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '覆盖人电话',
  `monitor_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '被监控人',
  `overlay_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '覆盖人',
  `monitor_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '被监控代码',
  `overlay_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '覆盖代码',
  `code_mode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '代码模式',
  `monitor_status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Init' COMMENT '监控状态',
  `version` int NOT NULL DEFAULT 0 COMMENT '版本',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code_monitor_monitor_user_id_code_mode_uindex`(`monitor_user_id` ASC, `code_mode` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码监控' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of code_monitor
-- ----------------------------
INSERT INTO `code_monitor` VALUES (22, 1, 1, '13667700000', '13667700000', '管理员', '管理员', '#include<iostream>\nusing namespace std;\nint main() {\n\n     return 0;\n}', '#include<iostream>\nusing namespace std;\nint main() {\n\n     return 0;\n}', 'OJ', 'MonitorPush', 203, '2025-06-27 00:43:04', '2025-06-27 00:43:04', 'F', NULL, 1, 'GLOWXQ');
INSERT INTO `code_monitor` VALUES (23, 9, 0, '', '', '管理员', '', '#include<iostream>\nusing namespace std;\nint main() {\n\n     return 0;\n}', '', 'OJ', 'MonitorPush', 1, '2025-06-27 01:09:46', '2025-06-27 01:09:46', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `code_monitor` VALUES (24, 1, 0, '13667700000', '', '管理员', '', '// 请在此处编写代码\n', '', 'Glowc', 'MonitorPush', 123, '2025-06-28 01:44:12', '2025-06-28 01:44:12', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `code_monitor` VALUES (25, 9, 0, '', '', '管理员', '', '// 请在此处编写代码\n', '', 'Glowc', 'MonitorPush', 2, '2025-06-28 13:32:20', '2025-06-28 13:32:20', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `code_monitor` VALUES (26, 8, 0, '13667700001', '', '学生', '', '#include<iostream>\nusing namespace std;\nint main() {\n\n     return 0;\n}', '', 'OJ', 'MonitorPush', 1, '2025-06-28 23:29:21', '2025-06-28 23:29:21', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `code_monitor` VALUES (27, 10, 0, '13667700003', '', '老师', '', '// 请在此处编写代码\n', '', 'Glowc', 'MonitorPush', 2, '2025-06-28 23:32:53', '2025-06-28 23:32:53', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `code_monitor` VALUES (28, 54, 0, '13667700004', '', '系统管理员', '', '// 画笔测试代码 - C++语法风格\npen.clear();\npen.background(2);\n\n// 基本绘制操作 - for循环\npen.color(1);\npen.move(50, 50);\n\n// 使用for循环绘制正方形\nfor (int i = 0; i < 4; i++) {\n  pen.fd(80);\n  pen.rt(90);\n}\n\n// 使用for循环绘制五角星\nfor (int i = 0; i < 5; i++) {\n  pen.color(i);\n  pen.circle(10);\n  pen.triangleF(15);\n  // Forward 100 pixels\n  pen.fd(100);\n  // Right turn by 144 degrees to form a star\n  pen.rt(144);\n}\n\n// 使用if-else条件判断\npen.move(180, 50);\npen.color(\"blue\");\nfor (int i = 0; i < 8; i++) {\n  if (i % 2 == 0) {\n    pen.color(\"blue\");\n  } else {\n    pen.color(\"green\");\n  }\n  pen.fd(30);\n  pen.rt(45);\n}\n\n// 使用while循环\npen.move(300, 50);\npen.color(\"purple\");\nint j = 0;\nwhile (j < 6) {\n  pen.fd(35);\n  pen.rt(60);\n  j++;\n}\n\n// 使用do-while循环\npen.move(90, 200);\npen.color(\"orange\");\nint k = 0;\ndo {\n  pen.circle(10 + k * 5);\n  pen.move(90, 200 + k * 25);\n  k++;\n} while (k < 4);\n\n// 嵌套循环 - 绘制图案\npen.move(220, 200);\npen.color(\"teal\");\nfor (int row = 0; row < 3; row++) {\n  for (int col = 0; col < 3; col++) {\n    if ((row + col) % 2 == 0) {\n      pen.color(\"teal\");\n      pen.circleF(10);\n    } else {\n      pen.color(\"magenta\");\n      pen.squareF(15);\n    }\n    pen.move(220 + col * 25, 200 + row * 25);\n  }\n}\n\n// switch语句演示\npen.move(350, 200);\nfor (int i = 0; i < 4; i++) {\n  switch (i) {\n    case 0:\n      pen.color(\"red\");\n      pen.fd(35);\n      break;\n    case 1:\n      pen.color(\"blue\");\n      pen.fd(35);\n      break;\n    case 2:\n      pen.color(\"green\");\n      pen.fd(35);\n      break;\n    default:\n      pen.color(\"black\");\n      pen.fd(35);\n      break;\n  }\n  pen.rt(90);\n}\n\n// 三元运算符\npen.move(130, 300);\nfor (int i = 0; i < 6; i++) {\n  pen.color(i % 2 == 0 ? \"darkblue\" : \"darkred\");\n  pen.fd(i % 2 == 0 ? 40 : 20);\n  pen.rt(60);\n}\n\n// 复杂条件\npen.move(270, 350);\nfor (int i = 0; i < 8; i++) {\n  if (i > 2 && i < 6) {\n    pen.color(\"gold\");\n  } else if (i == 0 || i == 7) {\n    pen.color(\"silver\");\n  } else {\n    pen.color(\"bronze\");\n  }\n  pen.fd(25);\n  pen.rt(45);\n}\n\n// 画一个螺旋\npen.move(400, 350);\npen.color(\"deepskyblue\");\nfor (int i = 0; i < 20; i++) {\n  pen.fd(2 + i * 1.5);\n  pen.rt(30);\n}\n\npen.finish();', '', 'Glowc', 'MonitorPush', 3, '2025-06-29 17:24:26', '2025-06-29 17:24:26', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `code_monitor` VALUES (29, 67, 0, '19323030401', '', 'xiaoqiang', '', '// 请在此处编写代码\n', '', 'Glowc', 'MonitorPush', 2, '2025-06-30 00:18:42', '2025-06-30 00:18:42', 'F', NULL, NULL, 'GLOWXQ');

-- ----------------------------
-- Table structure for code_record
-- ----------------------------
DROP TABLE IF EXISTS `code_record`;
CREATE TABLE `code_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `code` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '代码',
  `code_mode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '代码模式',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `code_record_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  CONSTRAINT `code_record_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户代码' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of code_record
-- ----------------------------
INSERT INTO `code_record` VALUES (1, 1, '吴一一', 'admin', 'sssdfsdf', 'Tuc', '2025-04-07 00:57:28', '2025-06-07 19:27:50', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `code_record` VALUES (2, 1, '吴一一', 'admin', 'sssdfsdf', 'Tuc', '2025-04-07 00:57:31', '2025-06-07 19:27:50', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `code_record` VALUES (3, 1, '吴一一', 'admin', '', 'Tuc', '2025-04-07 23:12:43', '2025-06-07 19:27:50', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `code_record` VALUES (4, 1, '吴一一', 'admin', '', 'Tuc', '2025-04-09 02:22:10', '2025-06-07 19:27:50', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `code_record` VALUES (5, 1, 'aaa', 'admin', '// 请在此处编写代码\n', 'Tuc', '2025-04-13 15:54:27', '2025-06-07 19:27:50', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `code_record` VALUES (6, 1, 'aaa', 'admin', '// 请在此处编写代码\n看看', 'Tuc', '2025-04-21 18:26:37', '2025-06-07 19:27:50', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `code_record` VALUES (7, 1, 'aaa', 'admin', '// 画笔测试代码 - C++语法风格\npen.clear();\npen.background(2);\n\n// 基本绘制操作 - for循环\npen.color(1);\npen.move(50, 50);\n\n// 使用for循环绘制正方形\nfor (int i = 0; i < 4; i++) {\n  pen.fd(80);\n  pen.rt(90);\n}\n\n// 使用for循环绘制五角星\nfor (int i = 0; i < 5; i++) {\n  pen.color(i);\n  pen.circle(10);\n  pen.triangleF(15);\n  // Forward 100 pixels\n  pen.fd(100);\n  // Right turn by 144 degrees to form a star\n  pen.rt(144);\n}\n\n// 使用if-else条件判断\npen.move(180, 50);\npen.color(\"blue\");\nfor (int i = 0; i < 8; i++) {\n  if (i % 2 == 0) {\n    pen.color(\"blue\");\n  } else {\n    pen.color(\"green\");\n  }\n  pen.fd(30);\n  pen.rt(45);\n}\n\n// 使用while循环\npen.move(300, 50);\npen.color(\"purple\");\nint j = 0;\nwhile (j < 6) {\n  pen.fd(35);\n  pen.rt(60);\n  j++;\n}\n\n// 使用do-while循环\npen.move(90, 200);\npen.color(\"orange\");\nint k = 0;\ndo {\n  pen.circle(10 + k * 5);\n  pen.move(90, 200 + k * 25);\n  k++;\n} while (k < 4);\n\n// 嵌套循环 - 绘制图案\npen.move(220, 200);\npen.color(\"teal\");\nfor (int row = 0; row < 3; row++) {\n  for (int col = 0; col < 3; col++) {\n    if ((row + col) % 2 == 0) {\n      pen.color(\"teal\");\n      pen.circleF(10);\n    } else {\n      pen.color(\"magenta\");\n      pen.squareF(15);\n    }\n    pen.move(220 + col * 25, 200 + row * 25);\n  }\n}\n\n// switch语句演示\npen.move(350, 200);\nfor (int i = 0; i < 4; i++) {\n  switch (i) {\n    case 0:\n      pen.color(\"red\");\n      pen.fd(35);\n      break;\n    case 1:\n      pen.color(\"blue\");\n      pen.fd(35);\n      break;\n    case 2:\n      pen.color(\"green\");\n      pen.fd(35);\n      break;\n    default:\n      pen.color(\"black\");\n      pen.fd(35);\n      break;\n  }\n  pen.rt(90);\n}\n\n// 三元运算符\npen.move(130, 300);\nfor (int i = 0; i < 6; i++) {\n  pen.color(i % 2 == 0 ? \"darkblue\" : \"darkred\");\n  pen.fd(i % 2 == 0 ? 40 : 20);\n  pen.rt(60);\n}\n\n// 复杂条件\npen.move(270, 350);\nfor (int i = 0; i < 8; i++) {\n  if (i > 2 && i < 6) {\n    pen.color(\"gold\");\n  } else if (i == 0 || i == 7) {\n    pen.color(\"silver\");\n  } else {\n    pen.color(\"bronze\");\n  }\n  pen.fd(25);\n  pen.rt(45);\n}\n\n// 画一个螺旋\npen.move(400, 350);\npen.color(\"deepskyblue\");\nfor (int i = 0; i < 20; i++) {\n  pen.fd(2 + i * 1.5);\n  pen.rt(30);\n}\n\npen.finish();', 'Glowc', '2025-04-26 07:39:23', '2025-06-07 19:27:50', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `code_record` VALUES (8, 1, 'aaa', 'admin', '// 请在此处编写代码\n', 'Glowc', '2025-04-26 12:18:55', '2025-06-07 19:27:50', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `code_record` VALUES (10, 1, 'glowxq', 'admin', '#include<iostream>\nusing namespace std;\nint main() {\n\n     return 0;\n}', 'OJ', '2025-06-23 01:30:38', '2025-06-23 01:30:38', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `code_record` VALUES (11, 1, 'glowxq', 'admin', '#include <bits/stdc++.h>\nusing namespace std;\nconst int N = 2e6 + 5;\nvector<int>e[N];\nint n, m, dfn[N], low[N], cnt, tot, id[N];\nstack<int>stk;\nint book[N];\n\nvoid tarjan(int x) {\n	dfn[x] = low[x] = ++cnt;\n	stk.push(x);\n	book[x] = 1;\n	for (auto v : e[x]) {\n		if (!dfn[v]) {\n			tarjan(v);\n			low[x] = min(low[x], low[v]);\n		} else if (book[v]) {\n			low[x] = min(low[x], dfn[v]);\n		}\n	}\n	if (low[x] == dfn[x]) {\n		tot++;\n		int v;\n		do {\n			v = stk.top();\n			stk.pop();\n			book[v] = 0;\n			id[v] = tot;\n		} while (x != v);\n	}\n}\n\nint main() {\n	cin >> m >> n;\n	for (int i = 1; i <= m; i++) {\n		char aa, bb;\n		int a,b;\n		cin >> aa >> a >> bb >> b;\n		if (aa == \'+\' && bb == \'+\') {\n			//!a->b  !b->a\n			e[a + n].push_back(b);\n			e[b + n].push_back(a);\n		} else if (aa == \'+\' && bb == \'-\') {\n			//!a->!b   b->a\n			e[a + n].push_back(b + n);\n			e[b].push_back(a);\n		} else if (aa == \'-\' && bb == \'+\') {\n			//a->b    !b->!a\n			e[a].push_back(b);\n			e[b + n].push_back(a + n);\n		} else if (aa == \'-\' && bb == \'-\') {\n			//a->!b   b->!a\n			e[a].push_back(b + n);\n			e[b].push_back(a + n);\n		}\n	}\n	for (int i = 1; i <= 2 * n; i++) {\n		if (!dfn[i])\n			tarjan(i);\n	}\n	for (int i = 1; i <= n; i++) {\n		if (id[i] == id[i + n]) {\n			cout << \"IMPOSSIBLE\" << endl;\n			exit(0);\n		}\n	}\n	for (int i = 1; i <= n; i++) {\n		if(id[i]<id[i+n])cout<<\"+ \";\n		else cout<<\"- \";\n	}\n	return 0;\n}', 'OJ', '2025-06-26 00:39:22', '2025-06-26 00:39:22', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `code_record` VALUES (12, 1, '管理员', '13667700000', '#include<iostream>\nusing namespace std;\nint main() {\n\n     return 0;\n}', 'OJ', '2025-08-20 15:34:45', '2025-08-20 15:34:45', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '课程名',
  `content` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '课程内容',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '课程连接',
  `teacher_user_id` bigint NOT NULL COMMENT '老师用户id',
  `teacher_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '老师姓名',
  `teacher_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '老师电话',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NotIn' COMMENT '课程状态',
  `enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用状态',
  `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上课时间',
  `end_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '下课时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '课程' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (8, '测试课程6449', '# 测试课程6449\n\n## 课程介绍\n这是一个自动生成的测试课程，包含以下内容：\n\n### 学习目标\n- 掌握基础概念\n- 理解核心原理\n- 实践操作技能\n\n### 课程大纲\n1. **第一章：基础知识**\n   - 概念介绍\n   - 基本原理\n   - 实例分析\n\n2. **第二章：进阶内容**\n   - 深入理解\n   - 高级技巧\n   - 项目实战\n\n3. **第三章：总结提升**\n   - 知识整合\n   - 经验分享\n   - 课后练习\n\n### 学习资源\n- 📖 课程讲义\n- 🎥 视频教程\n- 💻 代码示例\n- 📝 课后作业\n\n> **注意**：这是测试数据，请在实际使用前修改内容。', 'https://example.com/course/6449', 1, 'glowxq', '13667700000', 'NotIn', 1, '2025-08-07 15:10:18', '2025-08-08 18:10:18', '2025-06-27 01:06:30', '2025-08-07 23:10:31', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `course` VALUES (9, '测试课程6805', '# 测试课程6805\n\n## 课程介绍\n这是一个自动生成的测试课程，包含以下内容：\n\n### 学习目标\n- 掌握基础概念\n- 理解核心原理\n- 实践操作技能\n\n### 课程大纲\n1. **第一章：基础知识**\n   - 概念介绍\n   - 基本原理\n   - 实例分析\n\n2. **第二章：进阶内容**\n   - 深入理解\n   - 高级技巧\n   - 项目实战\n\n3. **第三章：总结提升**\n   - 知识整合\n   - 经验分享\n   - 课后练习\n\n### 学习资源\n- 📖 课程讲义\n- 🎥 视频教程\n- 💻 代码示例\n- 📝 课后作业\n\n> **注意**：这是测试数据，请在实际使用前修改内容。', 'https://example.com/course/6805', 1, '管理员', '13667700000', 'NotIn', 1, '2025-06-30 09:22:19', '2025-06-30 12:22:19', '2025-06-29 17:22:34', '2025-06-29 17:22:34', 'F', 10, 10, 'GLOWXQ');
INSERT INTO `course` VALUES (10, '测试课程4677', '# 测试课程4677\n\n## 课程介绍\n这是一个自动生成的测试课程，包含以下内容：\n\n### 学习目标\n- 掌握基础概念\n- 理解核心原理\n- 实践操作技能\n\n### 课程大纲\n1. **第一章：基础知识**\n   - 概念介绍\n   - 基本原理\n   - 实例分析\n\n2. **第二章：进阶内容**\n   - 深入理解\n   - 高级技巧\n   - 项目实战\n\n3. **第三章：总结提升**\n   - 知识整合\n   - 经验分享\n   - 课后练习\n\n### 学习资源\n- 📖 课程讲义\n- 🎥 视频教程\n- 💻 代码示例\n- 📝 课后作业\n\n> **注意**：这是测试数据，请在实际使用前修改内容。', 'https://example.com/course/4677', 10, '老师', '13667700003', 'NotIn', 1, '2025-08-20 11:58:56', '2025-08-20 14:58:56', '2025-08-19 19:59:15', '2025-08-19 19:59:15', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for generator_table
-- ----------------------------
DROP TABLE IF EXISTS `generator_table`;
CREATE TABLE `generator_table`  (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表名称',
  `table_comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表描述',
  `class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '实体类名称',
  `camel_class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'camel实体类名称',
  `tpl_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '使用的模版',
  `package_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成作者名',
  `type` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成方式(0 zip压缩包；1 自定义路径)',
  `options` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其他参数',
  `parent_menu_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上级菜单id',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成路径',
  `path_api` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'api生成路径',
  `path_web` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'web生成路径',
  `menu_init_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否自动创建菜单路由（1 是）',
  `btn_permission_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否自动创建按钮权限 (1 是)',
  `has_import` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否支持导入(1 是)',
  `has_export` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否支持导出(1 是)',
  `generate_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'all' COMMENT '生成类型（全量：all，后端：server，接口：service）',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `is_autofill` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '是否自动填充(1 是)',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`table_id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 158 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Generator Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of generator_table
-- ----------------------------
INSERT INTO `generator_table` VALUES (123, 'user_info', '用户信息', 'UserInfo', 'userInfo', 'crud', 'com.glowxq.oj', 'user', 'userInfo', '用户信息', 'glowxq', '0', NULL, 'e7f8a72021b848f7805051260be19314', '/', '/Users/glowxq/Documents/code/glowxq-nexus/glowxq-api/business/business-oj', '/Users/glowxq/Documents/code/glowxq-nexus/glowxq-web', '0', '1', '1', '1', 'all', 1, 1, '2025-06-23 11:20:30', '2025-06-23 11:20:56', '1', 'GLOWXQ');
INSERT INTO `generator_table` VALUES (125, 'judge', '测评记录', 'Judge', 'judge', 'crud', 'com.glowxq.oj', 'judge', 'judge', '测评记录', 'glowxq', '0', NULL, '0', '/', '/judge/server/business/business-oj', '/judge/server', '1', '1', '1', '1', 'all', 1, 1, '2025-06-30 01:29:51', '2025-06-30 01:29:51', '1', 'GLOWXQ');

-- ----------------------------
-- Table structure for generator_table_column
-- ----------------------------
DROP TABLE IF EXISTS `generator_table_column`;
CREATE TABLE `generator_table_column`  (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint NULL DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
  `search_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '搜索类型',
  `ts_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ts类型',
  `java_type_package` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'java类型包名',
  `java_field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA字段名',
  `up_camel_field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'get开头的驼峰字段名',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否主键（1是）',
  `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否自增（1是）',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否必填（1是）',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否编辑字段（1是）',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否列表字段（1是）',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否查询字段（1是）',
  `is_import` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否导入字段(1 是)',
  `is_export` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否导出字段(1 是)',
  `is_autofill` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否自动填充(1 是)',
  `is_unique_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否进行唯一校验(1 是)',
  `autofill_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自动填充类型',
  `query_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '查询方式',
  `html_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示类型',
  `dict_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典类型',
  `is_logic_del` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否逻辑删除(1 是)',
  `options` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其他设置',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `dict_show_way` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '字典展示方式（0 唯一标识；1 别名）',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`column_id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2487 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Generator Table Column' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of generator_table_column
-- ----------------------------
INSERT INTO `generator_table_column` VALUES (768, 46, 'id', 'ID', 'bigint', 'Long', 'input', 'number', NULL, 'id', 'Id', '1', '1', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, 1, '2025-03-21 14:29:38', '2025-03-21 14:29:38', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (769, 46, 'problem_key', '题目的自定义ID 例如(HOJ-1000)', 'varchar(50)', 'String', 'input', 'string', NULL, 'problemKey', 'ProblemKey', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 2, 1, 1, '2025-03-21 14:29:38', '2025-03-21 14:29:38', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (770, 46, 'title', '题目', 'varchar(255)', 'String', 'input', 'string', NULL, 'title', 'Title', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 3, 1, 1, '2025-03-21 14:29:38', '2025-03-21 14:29:38', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (771, 46, 'author', '作者', 'varchar(255)', 'String', 'input', 'string', NULL, 'author', 'Author', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 4, 1, 1, '2025-03-21 14:29:39', '2025-03-21 14:29:39', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (772, 46, 'program_type', '0为ACM,1为OI', 'int', 'Integer', 'select', 'number', NULL, 'programType', 'ProgramType', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 5, 1, 1, '2025-03-21 14:29:39', '2025-03-21 14:29:39', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (773, 46, 'problem_type', '题目类型', 'varchar(255)', 'String', 'select', 'string', NULL, 'problemType', 'ProblemType', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 6, 1, 1, '2025-03-21 14:29:39', '2025-03-21 14:29:39', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (774, 46, 'source_type', '来源类型', 'varchar(255)', 'String', 'select', 'string', NULL, 'sourceType', 'SourceType', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 7, 1, 1, '2025-03-21 14:29:39', '2025-03-21 14:29:39', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (775, 46, 'time_limit', '单位ms', 'int', 'Integer', 'date-picker', 'number', NULL, 'timeLimit', 'TimeLimit', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 8, 1, 1, '2025-03-21 14:29:39', '2025-03-21 14:29:39', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (776, 46, 'memory_limit', '单位kb', 'int', 'Integer', 'input', 'number', NULL, 'memoryLimit', 'MemoryLimit', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 9, 1, 1, '2025-03-21 14:29:39', '2025-03-21 14:29:39', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (777, 46, 'stack_limit', '单位mb', 'int', 'Integer', 'input', 'number', NULL, 'stackLimit', 'StackLimit', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 10, 1, 1, '2025-03-21 14:29:40', '2025-03-21 14:29:40', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (778, 46, 'description', '描述', 'longtext', 'String', 'input', 'string', NULL, 'description', 'Description', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 11, 1, 1, '2025-03-21 14:29:40', '2025-03-21 14:29:40', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (779, 46, 'input', '输入描述', 'longtext', 'String', 'input', 'string', NULL, 'input', 'Input', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 12, 1, 1, '2025-03-21 14:29:40', '2025-03-21 14:29:40', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (780, 46, 'output', '输出描述', 'longtext', 'String', 'input', 'string', NULL, 'output', 'Output', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 13, 1, 1, '2025-03-21 14:29:40', '2025-03-21 14:29:40', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (781, 46, 'examples', '题面样例', 'longtext', 'String', 'input', 'string', NULL, 'examples', 'Examples', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 14, 1, 1, '2025-03-21 14:29:40', '2025-03-21 14:29:40', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (782, 46, 'difficulty', '题目难度,0简单|1中等|2困难', 'int', 'Integer', 'input', 'number', NULL, 'difficulty', 'Difficulty', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 15, 1, 1, '2025-03-21 14:29:40', '2025-03-21 14:29:40', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (783, 46, 'hint', '备注,提醒', 'longtext', 'String', 'input', 'string', NULL, 'hint', 'Hint', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 16, 1, 1, '2025-03-21 14:29:41', '2025-03-21 14:29:41', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (784, 46, 'auth', '默认为1公开|2为私有|3为比赛题目', 'int', 'Integer', 'input', 'number', NULL, 'auth', 'Auth', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 17, 1, 1, '2025-03-21 14:29:41', '2025-03-21 14:29:41', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (785, 46, 'io_score', '当该题目为io题目时的分数', 'int', 'Integer', 'input', 'number', NULL, 'ioScore', 'IoScore', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 18, 1, 1, '2025-03-21 14:29:41', '2025-03-21 14:29:41', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (786, 46, 'score', '非编程题目总分', 'int', 'Integer', 'input', 'number', NULL, 'score', 'Score', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 19, 1, 1, '2025-03-21 14:29:41', '2025-03-21 14:29:41', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (787, 46, 'source', '题目来源', 'text', 'String', 'input', 'string', NULL, 'source', 'Source', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 20, 1, 1, '2025-03-21 14:29:41', '2025-03-21 14:29:41', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (788, 46, 'judge_mode', '题目评测模式,default、spj、interactive', 'varchar(255)', 'String', 'input', 'string', NULL, 'judgeMode', 'JudgeMode', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 21, 1, 1, '2025-03-21 14:29:41', '2025-03-21 14:29:41', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (789, 46, 'judge_case_mode', '题目样例评测模式,default,subtask_lowest,subtask_average', 'varchar(255)', 'String', 'input', 'string', NULL, 'judgeCaseMode', 'JudgeCaseMode', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 22, 1, 1, '2025-03-21 14:29:41', '2025-03-21 14:29:41', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (790, 46, 'user_extra_file', '题目评测时用户程序的额外文件 json key:name value:content', 'mediumtext', 'String', 'input', 'string', NULL, 'userExtraFile', 'UserExtraFile', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'fileUpload', '', '0', NULL, 23, 1, 1, '2025-03-21 14:29:42', '2025-03-21 14:29:42', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (791, 46, 'judge_extra_file', '题目评测时交互或特殊程序的额外文件 json key:name value:content', 'mediumtext', 'String', 'input', 'string', NULL, 'judgeExtraFile', 'JudgeExtraFile', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'fileUpload', '', '0', NULL, 24, 1, 1, '2025-03-21 14:29:42', '2025-03-21 14:29:42', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (792, 46, 'spj_code', '特判程序或交互程序代码', 'longtext', 'String', 'input', 'string', NULL, 'spjCode', 'SpjCode', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 25, 1, 1, '2025-03-21 14:29:42', '2025-03-21 14:29:42', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (793, 46, 'spj_language', '特判程序或交互程序代码的语言', 'varchar(255)', 'String', 'input', 'string', NULL, 'spjLanguage', 'SpjLanguage', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 26, 1, 1, '2025-03-21 14:29:42', '2025-03-21 14:29:42', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (794, 46, 'remote', '是否为远程vj判题', 'tinyint(1)', 'Boolean', 'input', 'string', NULL, 'remote', 'Remote', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'switch', '', '0', NULL, 27, 1, 1, '2025-03-21 14:29:42', '2025-03-21 14:29:42', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (795, 46, 'code_share', '该题目对应的相关提交代码|用户是否可用分享', 'tinyint(1)', 'Boolean', 'input', 'string', NULL, 'codeShare', 'CodeShare', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'switch', '', '0', NULL, 28, 1, 1, '2025-03-21 14:29:42', '2025-03-21 14:29:42', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (796, 46, 'remove_end_blank', '是否默认去除用户代码的文末空格', 'tinyint(1)', 'Boolean', 'input', 'string', NULL, 'removeEndBlank', 'RemoveEndBlank', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'switch', '', '0', NULL, 29, 1, 1, '2025-03-21 14:29:42', '2025-03-21 14:29:42', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (797, 46, 'open_case_result', '是否默认开启该题目的测试样例结果查看', 'tinyint(1)', 'Boolean', 'input', 'string', NULL, 'openCaseResult', 'OpenCaseResult', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'switch', '', '0', NULL, 30, 1, 1, '2025-03-21 14:29:43', '2025-03-21 14:29:43', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (798, 46, 'upload_case', '题目测试数据是否是上传文件的', 'tinyint(1)', 'Boolean', 'input', 'string', NULL, 'uploadCase', 'UploadCase', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'switch', '', '0', NULL, 31, 1, 1, '2025-03-21 14:29:43', '2025-03-21 14:29:43', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (799, 46, 'group_problem', '在队伍内', 'tinyint(1)', 'Boolean', 'input', 'string', NULL, 'groupProblem', 'GroupProblem', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'switch', '', '0', NULL, 32, 1, 1, '2025-03-21 14:29:43', '2025-03-21 14:29:43', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (800, 46, 'file_io', '是否是file io自定义输入输出文件模式', 'tinyint(1)', 'Boolean', 'input', 'string', NULL, 'fileIo', 'FileIo', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'fileUpload', '', '0', NULL, 33, 1, 1, '2025-03-21 14:29:43', '2025-03-21 14:29:43', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (801, 46, 'require_image', '必须上传图片', 'tinyint(1)', 'Boolean', 'input', 'string', NULL, 'requireImage', 'RequireImage', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'imageUpload', '', '0', NULL, 34, 1, 1, '2025-03-21 14:29:43', '2025-03-21 14:29:43', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (802, 46, 'case_version', '题目测试数据的版本号', 'varchar(40)', 'String', 'input', 'string', NULL, 'caseVersion', 'CaseVersion', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 35, 1, 1, '2025-03-21 14:29:43', '2025-03-21 14:29:43', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (803, 46, 'modified_user', '修改题目的管理员用户名', 'varchar(255)', 'String', 'input', 'string', NULL, 'modifiedUser', 'ModifiedUser', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 36, 1, 1, '2025-03-21 14:29:43', '2025-03-21 14:29:43', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (804, 46, 'apply_public_progress', '申请公开的进度：null为未申请|1为申请中|2为申请通过|3为申请拒绝', 'int', 'Integer', 'input', 'number', NULL, 'applyPublicProgress', 'ApplyPublicProgress', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 37, 1, 1, '2025-03-21 14:29:44', '2025-03-21 14:29:44', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (805, 46, 'io_read_file_name', '题目指定的file io输入文件的名称', 'varchar(255)', 'String', 'input', 'string', NULL, 'ioReadFileName', 'IoReadFileName', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'fileUpload', '', '0', NULL, 38, 1, 1, '2025-03-21 14:29:44', '2025-03-21 14:29:44', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (806, 46, 'io_write_file_name', '题目指定的file io输出文件的名称', 'varchar(255)', 'String', 'input', 'string', NULL, 'ioWriteFileName', 'IoWriteFileName', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'fileUpload', '', '0', NULL, 39, 1, 1, '2025-03-21 14:29:44', '2025-03-21 14:29:44', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (807, 46, 'create_time', '创建时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '1', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'BETWEEN', 'datetime', '', '0', NULL, 40, 1, 1, '2025-03-21 14:29:44', '2025-03-21 14:29:44', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (808, 46, 'update_time', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '1', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'BETWEEN', 'datetime', '', '0', NULL, 41, 1, 1, '2025-03-21 14:29:44', '2025-03-21 14:29:44', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (809, 46, 'del_flag', '是否删除', 'enum(\'T\',\'F\')', 'String', 'input', 'string', NULL, 'delFlag', 'DelFlag', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', NULL, 'EQ', '', '', '1', NULL, 42, 1, 1, '2025-03-21 14:29:44', '2025-03-21 14:29:44', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (810, 46, 'create_id', '创建人ID', 'bigint', 'Long', 'input', 'number', NULL, 'createId', 'CreateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'input-number', '', '0', NULL, 43, 1, 1, '2025-03-21 14:29:45', '2025-03-21 14:29:45', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (811, 46, 'update_id', '更新人ID', 'bigint', 'Long', 'input', 'number', NULL, 'updateId', 'UpdateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'input-number', '', '0', NULL, 44, 1, 1, '2025-03-21 14:29:45', '2025-03-21 14:29:45', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (1994, 123, 'id', 'ID', 'bigint', 'Long', 'input', 'number', NULL, 'id', 'Id', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, 1, '2025-06-23 11:20:30', '2025-06-23 11:20:56', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (1995, 123, 'user_id', '用户ID', 'bigint', 'Long', 'input', 'number', NULL, 'userId', 'UserId', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 2, 1, 1, '2025-06-23 11:20:30', '2025-06-23 11:20:56', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (1996, 123, 'dept_id', '部门ID', 'bigint', 'Long', 'input', 'number', NULL, 'deptId', 'DeptId', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 3, 1, 1, '2025-06-23 11:20:30', '2025-06-23 11:20:56', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (1997, 123, 'name', '姓名', 'varchar(20)', 'String', 'input', 'string', NULL, 'name', 'Name', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'input', '', '0', NULL, 4, 1, 1, '2025-06-23 11:20:31', '2025-06-23 11:20:57', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (1998, 123, 'nick_name', '昵称', 'varchar(20)', 'String', 'input', 'string', NULL, 'nickName', 'NickName', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'input', '', '0', NULL, 5, 1, 1, '2025-06-23 11:20:31', '2025-06-23 11:20:57', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (1999, 123, 'phone', '手机号', 'varchar(20)', 'String', 'input', 'string', NULL, 'phone', 'Phone', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 6, 1, 1, '2025-06-23 11:20:31', '2025-06-23 11:20:57', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2000, 123, 'email', '邮箱', 'varchar(20)', 'String', 'input', 'string', NULL, 'email', 'Email', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 7, 1, 1, '2025-06-23 11:20:32', '2025-06-23 11:20:57', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2001, 123, 'avatar', '头像信息', 'varchar(255)', 'String', 'input', 'string', NULL, 'avatar', 'Avatar', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 8, 1, 1, '2025-06-23 11:20:32', '2025-06-23 11:20:58', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2002, 123, 'sex', '性别', 'varchar(10)', 'String', 'select', 'string', NULL, 'sex', 'Sex', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 9, 1, 1, '2025-06-23 11:20:32', '2025-06-23 11:20:58', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2003, 123, 'birthday', '生日', 'date', 'LocalDate', 'input', 'string', 'java.time.LocalDate', 'birthday', 'Birthday', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'date', '', '0', NULL, 10, 1, 1, '2025-06-23 11:20:32', '2025-06-23 11:20:58', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2004, 123, 'image', '相关图片', 'varchar(2000)', 'String', 'input', 'string', NULL, 'image', 'Image', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'imageUpload', '', '0', NULL, 11, 1, 1, '2025-06-23 11:20:33', '2025-06-23 11:20:58', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2005, 123, 'ac_num', 'ac数量', 'int', 'Integer', 'input', 'number', NULL, 'acNum', 'AcNum', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 12, 1, 1, '2025-06-23 11:20:33', '2025-06-23 11:20:58', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2006, 123, 'integral', '积分', 'int', 'Integer', 'input', 'number', NULL, 'integral', 'Integral', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 13, 1, 1, '2025-06-23 11:20:33', '2025-06-23 11:20:59', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2007, 123, 'continuous_sign_day', '连续签到时间', 'int', 'Integer', 'input', 'number', NULL, 'continuousSignDay', 'ContinuousSignDay', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 14, 1, 1, '2025-06-23 11:20:34', '2025-06-23 11:20:59', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2008, 123, 'submit_num', '提交题目数量', 'int', 'Integer', 'input', 'number', NULL, 'submitNum', 'SubmitNum', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 15, 1, 1, '2025-06-23 11:20:34', '2025-06-23 11:21:00', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2009, 123, 'title', '称号', 'varchar(20)', 'String', 'input', 'string', NULL, 'title', 'Title', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 16, 1, 1, '2025-06-23 11:20:34', '2025-06-23 11:21:00', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2010, 123, 'color', '颜色', 'varchar(50)', 'String', 'input', 'string', NULL, 'color', 'Color', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 17, 1, 1, '2025-06-23 11:20:35', '2025-06-23 11:21:00', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2011, 123, 'remark', '备注', 'varchar(255)', 'String', 'input', 'string', NULL, 'remark', 'Remark', '0', '0', '1', '1', '1', '1', '0', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 18, 1, 1, '2025-06-23 11:20:35', '2025-06-23 11:21:00', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2012, 123, 'expiration_time', '帐号过期时间', 'date', 'LocalDate', 'date-picker', 'string', 'java.time.LocalDate', 'expirationTime', 'ExpirationTime', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'date', '', '0', NULL, 19, 1, 1, '2025-06-23 11:20:35', '2025-06-23 11:21:00', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2013, 123, 'last_login_time', '最后登录时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'lastLoginTime', 'LastLoginTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 20, 1, 1, '2025-06-23 11:20:35', '2025-06-23 11:21:01', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2014, 123, 'last_login_ip', '最后登录ip', 'varchar(255)', 'String', 'input', 'string', NULL, 'lastLoginIp', 'LastLoginIp', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 21, 1, 1, '2025-06-23 11:20:36', '2025-06-23 11:21:01', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2015, 123, 'create_time', '创建时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '1', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'BETWEEN', 'datetime', '', '0', NULL, 22, 1, 1, '2025-06-23 11:20:36', '2025-06-23 11:21:01', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2016, 123, 'update_time', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '1', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'BETWEEN', 'datetime', '', '0', NULL, 23, 1, 1, '2025-06-23 11:20:36', '2025-06-23 11:21:01', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2017, 123, 'del_flag', '是否删除', 'enum(\'T\',\'F\')', 'String', 'input', 'string', NULL, 'delFlag', 'DelFlag', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', NULL, 'EQ', '', '', '1', NULL, 24, 1, 1, '2025-06-23 11:20:36', '2025-06-23 11:21:01', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2018, 123, 'create_id', '创建人ID', 'bigint', 'Long', 'input', 'number', NULL, 'createId', 'CreateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'input-number', '', '0', NULL, 25, 1, 1, '2025-06-23 11:20:37', '2025-06-23 11:21:02', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2019, 123, 'update_id', '更新人ID', 'bigint', 'Long', 'input', 'number', NULL, 'updateId', 'UpdateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'input-number', '', '0', NULL, 26, 1, 1, '2025-06-23 11:20:37', '2025-06-23 11:21:02', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2020, 123, 'tenant_id', '租户ID', 'varchar(64)', 'String', 'input', 'string', NULL, 'tenantId', 'TenantId', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 27, 1, 1, '2025-06-23 11:20:37', '2025-06-23 11:21:02', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2066, 125, 'id', 'ID', 'bigint', 'Long', 'input', 'number', NULL, 'id', 'Id', '1', '1', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, 1, '2025-06-30 01:29:52', '2025-06-30 01:29:52', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2067, 125, 'judge_key', '评测Key', 'varchar(50)', 'String', 'input', 'string', NULL, 'judgeKey', 'JudgeKey', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 2, 1, 1, '2025-06-30 01:29:52', '2025-06-30 01:29:52', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2068, 125, 'problem_id', '题目ID', 'bigint', 'Long', 'input', 'number', NULL, 'problemId', 'ProblemId', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 3, 1, 1, '2025-06-30 01:29:53', '2025-06-30 01:29:53', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2069, 125, 'problem_key', '题目自定义ID', 'varchar(50)', 'String', 'input', 'string', NULL, 'problemKey', 'ProblemKey', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 4, 1, 1, '2025-06-30 01:29:53', '2025-06-30 01:29:53', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2070, 125, 'problem_title', '题目标题', 'varchar(255)', 'String', 'input', 'string', NULL, 'problemTitle', 'ProblemTitle', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 5, 1, 1, '2025-06-30 01:29:54', '2025-06-30 01:29:54', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2071, 125, 'user_id', '测评用户id', 'bigint', 'Long', 'input', 'number', NULL, 'userId', 'UserId', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 6, 1, 1, '2025-06-30 01:29:54', '2025-06-30 01:29:54', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2072, 125, 'name', '测评人', 'varchar(32)', 'String', 'input', 'string', NULL, 'name', 'Name', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'input', '', '0', NULL, 7, 1, 1, '2025-06-30 01:29:55', '2025-06-30 01:29:55', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2073, 125, 'group_id', '团队id|不为团队内提交则为0', 'bigint', 'Long', 'input', 'number', NULL, 'groupId', 'GroupId', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 8, 1, 1, '2025-06-30 01:29:55', '2025-06-30 01:29:55', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2074, 125, 'contest_id', '比赛id|非比赛题目默认为0', 'bigint', 'Long', 'input', 'number', NULL, 'contestId', 'ContestId', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 9, 1, 1, '2025-06-30 01:29:56', '2025-06-30 01:29:56', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2075, 125, 'contest_problem_id', '比赛中题目排序id|非比赛题目默认为0', 'bigint', 'Long', 'input', 'number', NULL, 'contestProblemId', 'ContestProblemId', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 10, 1, 1, '2025-06-30 01:29:56', '2025-06-30 01:29:56', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2076, 125, 'business_id', '关联业务表ID', 'bigint', 'Long', 'input', 'number', NULL, 'businessId', 'BusinessId', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 11, 1, 1, '2025-06-30 01:29:57', '2025-06-30 01:29:57', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2077, 125, 'scene_type', '测评类型', 'varchar(50)', 'String', 'select', 'string', NULL, 'sceneType', 'SceneType', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 12, 1, 1, '2025-06-30 01:29:58', '2025-06-30 01:29:58', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2078, 125, 'submit_type', '提交类型', 'varchar(20)', 'String', 'select', 'string', NULL, 'submitType', 'SubmitType', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 13, 1, 1, '2025-06-30 01:29:58', '2025-06-30 01:29:58', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2079, 125, 'problem_type', '题目类型', 'varchar(255)', 'String', 'select', 'string', NULL, 'problemType', 'ProblemType', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 14, 1, 1, '2025-06-30 01:29:59', '2025-06-30 01:29:59', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2080, 125, 'submit_time', '提交测评的时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'submitTime', 'SubmitTime', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 15, 1, 1, '2025-06-30 01:29:59', '2025-06-30 01:29:59', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2081, 125, 'start_time', '开始测评的时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'startTime', 'StartTime', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 16, 1, 1, '2025-06-30 01:30:00', '2025-06-30 01:30:00', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2082, 125, 'end_time', '结束测评的时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'endTime', 'EndTime', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 17, 1, 1, '2025-06-30 01:30:00', '2025-06-30 01:30:00', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2083, 125, 'use_time', '使用时间', 'int', 'Integer', 'date-picker', 'number', NULL, 'useTime', 'UseTime', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 18, 1, 1, '2025-06-30 01:30:01', '2025-06-30 01:30:01', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2084, 125, 'auto_submit', '自动提交', 'tinyint', 'Boolean', 'input', 'string', NULL, 'autoSubmit', 'AutoSubmit', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'switch', '', '0', NULL, 19, 1, 1, '2025-06-30 01:30:01', '2025-06-30 01:30:01', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2085, 125, 'status', '结果码具体参考文档', 'int', 'Integer', 'select', 'number', NULL, 'status', 'Status', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'radio', '', '0', NULL, 20, 1, 1, '2025-06-30 01:30:02', '2025-06-30 01:30:02', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2086, 125, 'error_message', '错误提醒(编译错误|或者vj提醒)', 'mediumtext', 'String', 'input', 'string', NULL, 'errorMessage', 'ErrorMessage', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 21, 1, 1, '2025-06-30 01:30:02', '2025-06-30 01:30:02', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2087, 125, 'time', '运行时间(ms)', 'int', 'Integer', 'date-picker', 'number', NULL, 'time', 'Time', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 22, 1, 1, '2025-06-30 01:30:03', '2025-06-30 01:30:03', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2088, 125, 'memory', '运行内存(kb)', 'int', 'Integer', 'input', 'number', NULL, 'memory', 'Memory', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 23, 1, 1, '2025-06-30 01:30:03', '2025-06-30 01:30:03', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2089, 125, 'score', 'IO判题则不为空', 'int', 'Integer', 'input', 'number', NULL, 'score', 'Score', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 24, 1, 1, '2025-06-30 01:30:04', '2025-06-30 01:30:04', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2090, 125, 'length', '代码长度', 'int', 'Integer', 'input', 'number', NULL, 'length', 'Length', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 25, 1, 1, '2025-06-30 01:30:05', '2025-06-30 01:30:05', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2091, 125, 'flow_image', '流程图URL', 'varchar(500)', 'String', 'input', 'string', NULL, 'flowImage', 'FlowImage', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'imageUpload', '', '0', NULL, 26, 1, 1, '2025-06-30 01:30:05', '2025-06-30 01:30:05', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2092, 125, 'code', '代码', 'longtext', 'String', 'input', 'string', NULL, 'code', 'Code', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 27, 1, 1, '2025-06-30 01:30:06', '2025-06-30 01:30:06', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2093, 125, 'reply_options', '非编程题作答内容', 'varchar(3000)', 'String', 'input', 'string', NULL, 'replyOptions', 'ReplyOptions', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'textarea', '', '0', NULL, 28, 1, 1, '2025-06-30 01:30:06', '2025-06-30 01:30:06', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2094, 125, 'language', '代码语言', 'varchar(255)', 'String', 'input', 'string', NULL, 'language', 'Language', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 29, 1, 1, '2025-06-30 01:30:07', '2025-06-30 01:30:07', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2095, 125, 'judge_server', '判题机', 'varchar(20)', 'String', 'input', 'string', NULL, 'judgeServer', 'JudgeServer', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 30, 1, 1, '2025-06-30 01:30:07', '2025-06-30 01:30:07', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2096, 125, 'submit_ip', '提交者所在ip', 'varchar(20)', 'String', 'input', 'string', NULL, 'submitIp', 'SubmitIp', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 31, 1, 1, '2025-06-30 01:30:08', '2025-06-30 01:30:08', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2097, 125, 'version', '乐观锁', 'int', 'Integer', 'input', 'number', NULL, 'version', 'Version', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 32, 1, 1, '2025-06-30 01:30:08', '2025-06-30 01:30:08', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2098, 125, 'oi_rank_score', 'oi排行榜得分', 'int', 'Integer', 'input', 'number', NULL, 'oiRankScore', 'OiRankScore', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 33, 1, 1, '2025-06-30 01:30:09', '2025-06-30 01:30:09', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2099, 125, 'share_enable', '0为仅自己可见|1为全部人可见。', 'tinyint(1)', 'Boolean', 'input', 'string', NULL, 'shareEnable', 'ShareEnable', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'switch', '', '0', NULL, 34, 1, 1, '2025-06-30 01:30:09', '2025-06-30 01:30:09', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2100, 125, 'manual_evaluation', '是否为人工评测', 'tinyint(1)', 'Boolean', 'input', 'string', NULL, 'manualEvaluation', 'ManualEvaluation', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'switch', '', '0', NULL, 35, 1, 1, '2025-06-30 01:30:10', '2025-06-30 01:30:10', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2101, 125, 'other_judge_submit_id', 'vjudge判题在其它oj的提交id', 'bigint', 'Long', 'input', 'number', NULL, 'otherJudgeSubmitId', 'OtherJudgeSubmitId', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 36, 1, 1, '2025-06-30 01:30:10', '2025-06-30 01:30:10', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2102, 125, 'other_judge_username', 'vjudge判题在其它oj的提交用户名', 'varchar(255)', 'String', 'input', 'string', NULL, 'otherJudgeUsername', 'OtherJudgeUsername', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'input', '', '0', NULL, 37, 1, 1, '2025-06-30 01:30:11', '2025-06-30 01:30:11', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2103, 125, 'other_judge_password', 'vjudge判题在其它oj的提交账号密码', 'varchar(255)', 'String', 'input', 'string', NULL, 'otherJudgePassword', 'OtherJudgePassword', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 38, 1, 1, '2025-06-30 01:30:12', '2025-06-30 01:30:12', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2104, 125, 'exception_stack_trace', '异常堆栈信息', 'text', 'String', 'input', 'string', NULL, 'exceptionStackTrace', 'ExceptionStackTrace', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 39, 1, 1, '2025-06-30 01:30:12', '2025-06-30 01:30:12', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2105, 125, 'create_time', '创建时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '1', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'BETWEEN', 'datetime', '', '0', NULL, 40, 1, 1, '2025-06-30 01:30:13', '2025-06-30 01:30:13', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2106, 125, 'update_time', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '1', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'BETWEEN', 'datetime', '', '0', NULL, 41, 1, 1, '2025-06-30 01:30:13', '2025-06-30 01:30:13', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2107, 125, 'del_flag', '是否删除', 'enum(\'T\',\'F\')', 'String', 'input', 'string', NULL, 'delFlag', 'DelFlag', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', NULL, 'EQ', '', '', '1', NULL, 42, 1, 1, '2025-06-30 01:30:14', '2025-06-30 01:30:14', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2108, 125, 'create_id', '创建人ID', 'bigint', 'Long', 'input', 'number', NULL, 'createId', 'CreateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'input-number', '', '0', NULL, 43, 1, 1, '2025-06-30 01:30:14', '2025-06-30 01:30:14', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2109, 125, 'update_id', '更新人ID', 'bigint', 'Long', 'input', 'number', NULL, 'updateId', 'UpdateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'input-number', '', '0', NULL, 44, 1, 1, '2025-06-30 01:30:15', '2025-06-30 01:30:15', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2110, 125, 'tenant_id', '租户ID', 'varchar(64)', 'String', 'input', 'string', NULL, 'tenantId', 'TenantId', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 45, 1, 1, '2025-06-30 01:30:15', '2025-06-30 01:30:15', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2111, 126, 'AUDIT_ID', '主键', 'bigint', 'Long', 'input', 'number', NULL, 'auditId', 'AuditId', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, 1, '2025-07-07 11:12:05', '2025-07-07 11:12:05', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2112, 126, 'AUDIT_CODE', '复核代码', 'varchar(20)', 'String', 'input', 'string', NULL, 'auditCode', 'AuditCode', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 2, 1, 1, '2025-07-07 11:12:05', '2025-07-07 11:12:05', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2113, 126, 'AUDIT_TYPE', '复核类型 1新增、2修改、3删除、4导入、5启用、6禁用', 'bigint', 'Long', 'select', 'number', NULL, 'auditType', 'AuditType', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 3, 1, 1, '2025-07-07 11:12:06', '2025-07-07 11:12:06', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2114, 126, 'AUDIT_STATUS', '复核状态 0已发起 1已通过 2已拒绝 3已撤销', 'bigint', 'Long', 'select', 'number', NULL, 'auditStatus', 'AuditStatus', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'radio', '', '0', NULL, 4, 1, 1, '2025-07-07 11:12:06', '2025-07-07 11:12:06', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2115, 126, 'EXT_INFO', '扩展信息', 'text', 'String', 'input', 'string', NULL, 'extInfo', 'ExtInfo', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 5, 1, 1, '2025-07-07 11:12:07', '2025-07-07 11:12:07', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2116, 126, 'CREATE_TIME', '创建时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 6, 1, 1, '2025-07-07 11:12:07', '2025-07-07 11:12:07', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2117, 126, 'UPDATE_TIME', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 7, 1, 1, '2025-07-07 11:12:08', '2025-07-07 11:12:08', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2118, 126, 'CREATE_BY', '创建人', 'varchar(255)', 'String', 'input', 'string', NULL, 'createBy', 'CreateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 8, 1, 1, '2025-07-07 11:12:08', '2025-07-07 11:12:08', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2119, 126, 'UPDATE_BY', '更新人', 'varchar(255)', 'String', 'input', 'string', NULL, 'updateBy', 'UpdateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 9, 1, 1, '2025-07-07 11:12:08', '2025-07-07 11:12:08', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2120, 126, 'CREATE_ORG', '创建机构', 'varchar(255)', 'String', 'input', 'string', NULL, 'createOrg', 'CreateOrg', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 10, 1, 1, '2025-07-07 11:12:09', '2025-07-07 11:12:09', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2121, 126, 'UPDATE_ORG', '更新机构', 'varchar(255)', 'String', 'input', 'string', NULL, 'updateOrg', 'UpdateOrg', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 11, 1, 1, '2025-07-07 11:12:09', '2025-07-07 11:12:09', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2122, 127, 'ID_', '主键', 'bigint', 'Long', 'input', 'number', NULL, 'id', 'Id', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, 1, '2025-07-07 11:12:10', '2025-07-07 11:12:10', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2123, 127, 'TYPE', '按钮类型', 'varchar(60)', 'String', 'select', 'string', NULL, 'TYPE', 'Type', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 2, 1, 1, '2025-07-07 11:12:10', '2025-07-07 11:12:10', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2124, 127, 'NAME', '名称', 'varchar(50)', 'String', 'input', 'string', NULL, 'NAME', 'Name', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'input', '', '0', NULL, 3, 1, 1, '2025-07-07 11:12:11', '2025-07-07 11:12:11', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2125, 127, 'CODE', 'code', 'varchar(100)', 'String', 'input', 'string', NULL, 'CODE', 'Code', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 4, 1, 1, '2025-07-07 11:12:11', '2025-07-07 11:12:11', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2126, 128, 'CACHE_ID', '缓存主键', 'bigint', 'Long', 'input', 'number', NULL, 'cacheId', 'CacheId', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, 1, '2025-07-07 11:12:12', '2025-07-07 11:12:12', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2127, 128, 'UUID', '触发免疫名单策略相应流水号', 'varchar(64)', 'String', 'input', 'string', NULL, 'UUID', 'Uuid', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 2, 1, 1, '2025-07-07 11:12:12', '2025-07-07 11:12:12', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2128, 128, 'DURATION', '免疫过期毫秒数', 'bigint', 'Long', 'input', 'number', NULL, 'DURATION', 'Duration', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 3, 1, 1, '2025-07-07 11:12:12', '2025-07-07 11:12:12', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2129, 128, 'NAMELIST_TYPE', '名单类型', 'varchar(150)', 'String', 'select', 'string', NULL, 'namelistType', 'NamelistType', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 4, 1, 1, '2025-07-07 11:12:13', '2025-07-07 11:12:13', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2130, 128, 'NAMELIST_DIM', '名单维度,维度CODE', 'varchar(60)', 'String', 'input', 'string', NULL, 'namelistDim', 'NamelistDim', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 5, 1, 1, '2025-07-07 11:12:13', '2025-07-07 11:12:13', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2131, 128, 'NAMELIST_VALUE', '维度值', 'varchar(300)', 'String', 'input', 'string', NULL, 'namelistValue', 'NamelistValue', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 6, 1, 1, '2025-07-07 11:12:14', '2025-07-07 11:12:14', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2132, 128, 'NAMELIST_SUB_DIM', '子维度', 'varchar(60)', 'String', 'input', 'string', NULL, 'namelistSubDim', 'NamelistSubDim', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 7, 1, 1, '2025-07-07 11:12:14', '2025-07-07 11:12:14', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2133, 128, 'NAMELIST_SUB_DIM_VALUE', '子维度值', 'varchar(300)', 'String', 'input', 'string', NULL, 'namelistSubDimValue', 'NamelistSubDimValue', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 8, 1, 1, '2025-07-07 11:12:15', '2025-07-07 11:12:15', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2134, 128, 'NAMELIST_STATUS', '状态 1-启用， 0-禁用', 'int', 'Integer', 'select', 'number', NULL, 'namelistStatus', 'NamelistStatus', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'radio', '', '0', NULL, 9, 1, 1, '2025-07-07 11:12:15', '2025-07-07 11:12:15', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2135, 128, 'IMMUNE_RULE', '免疫规则代码', 'varchar(300)', 'String', 'input', 'string', NULL, 'immuneRule', 'ImmuneRule', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 10, 1, 1, '2025-07-07 11:12:16', '2025-07-07 11:12:16', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2136, 128, 'EXPIRE_TIME', '过期时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'expireTime', 'ExpireTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 11, 1, 1, '2025-07-07 11:12:16', '2025-07-07 11:12:16', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2137, 128, 'NAMELIST_REMARK', '名单备注', 'varchar(166)', 'String', 'input', 'string', NULL, 'namelistRemark', 'NamelistRemark', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 12, 1, 1, '2025-07-07 11:12:16', '2025-07-07 11:12:16', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2138, 128, 'CREATE_TIME', '创建时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 13, 1, 1, '2025-07-07 11:12:17', '2025-07-07 11:12:17', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2139, 128, 'UPDATE_TIME', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 14, 1, 1, '2025-07-07 11:12:17', '2025-07-07 11:12:17', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2140, 128, 'CREATE_BY', '创建人', 'varchar(255)', 'String', 'input', 'string', NULL, 'createBy', 'CreateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 15, 1, 1, '2025-07-07 11:12:18', '2025-07-07 11:12:18', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2141, 128, 'UPDATE_BY', '更新人', 'varchar(255)', 'String', 'input', 'string', NULL, 'updateBy', 'UpdateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 16, 1, 1, '2025-07-07 11:12:18', '2025-07-07 11:12:18', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2142, 128, 'SOURCE_TYPE', '来源，默认是rule', 'varchar(10)', 'String', 'select', 'string', NULL, 'sourceType', 'SourceType', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 17, 1, 1, '2025-07-07 11:12:19', '2025-07-07 11:12:19', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2143, 129, 'DIM_ID', '主键', 'bigint', 'Long', 'input', 'number', NULL, 'dimId', 'DimId', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, 1, '2025-07-07 11:12:19', '2025-07-07 11:12:19', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2144, 129, 'DIM_NAME', '维度名称', 'varchar(60)', 'String', 'input', 'string', NULL, 'dimName', 'DimName', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'input', '', '0', NULL, 2, 1, 1, '2025-07-07 11:12:19', '2025-07-07 11:12:19', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2145, 129, 'DIM_FIELD', '名单维度', 'varchar(300)', 'String', 'input', 'string', NULL, 'dimField', 'DimField', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 3, 1, 1, '2025-07-07 11:12:20', '2025-07-07 11:12:20', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2146, 129, 'CREATE_TIME', '创建时间', 'datetime(6)', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 4, 1, 1, '2025-07-07 11:12:20', '2025-07-07 11:12:20', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2147, 129, 'UPDATE_TIME', '更新时间', 'datetime(6)', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 5, 1, 1, '2025-07-07 11:12:21', '2025-07-07 11:12:21', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2148, 129, 'CREATE_BY', '创建人', 'varchar(255)', 'String', 'input', 'string', NULL, 'createBy', 'CreateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 6, 1, 1, '2025-07-07 11:12:21', '2025-07-07 11:12:21', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2149, 129, 'UPDATE_BY', '更新人', 'varchar(255)', 'String', 'input', 'string', NULL, 'updateBy', 'UpdateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 7, 1, 1, '2025-07-07 11:12:22', '2025-07-07 11:12:22', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2150, 129, 'DATA_MASKING_TYPE', '规则类型（0-不脱敏，1-全部脱敏，2-前后脱敏，3-中间脱敏）', 'tinyint', 'Boolean', 'select', 'string', NULL, 'dataMaskingType', 'DataMaskingType', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 8, 1, 1, '2025-07-07 11:12:22', '2025-07-07 11:12:22', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2151, 129, 'DATA_MASKING_RULE', '数据脱敏规则', 'varchar(10)', 'String', 'input', 'string', NULL, 'dataMaskingRule', 'DataMaskingRule', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 9, 1, 1, '2025-07-07 11:12:23', '2025-07-07 11:12:23', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2152, 130, 'FILE_ID', '主键', 'bigint', 'Long', 'input', 'number', NULL, 'fileId', 'FileId', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'fileUpload', '', '0', NULL, 1, 1, 1, '2025-07-07 11:12:23', '2025-07-07 11:12:23', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2153, 130, 'TYPE_CODE', '名单code', 'varchar(20)', 'String', 'select', 'string', NULL, 'typeCode', 'TypeCode', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 2, 1, 1, '2025-07-07 11:12:23', '2025-07-07 11:12:23', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2154, 130, 'IMP_DATA', '导入文件二进制文件', 'blob', 'String', 'input', 'string', NULL, 'impData', 'ImpData', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', '', '', '0', NULL, 3, 1, 1, '2025-07-07 11:12:24', '2025-07-07 11:12:24', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2155, 130, 'ERROR_DATA', '错误信息json格式', 'text', 'String', 'input', 'string', NULL, 'errorData', 'ErrorData', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 4, 1, 1, '2025-07-07 11:12:24', '2025-07-07 11:12:24', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2156, 130, 'CREATE_TIME', '创建时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 5, 1, 1, '2025-07-07 11:12:25', '2025-07-07 11:12:25', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2157, 130, 'UPDATE_TIME', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 6, 1, 1, '2025-07-07 11:12:25', '2025-07-07 11:12:25', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2158, 130, 'CREATE_BY', '创建人', 'varchar(255)', 'String', 'input', 'string', NULL, 'createBy', 'CreateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 7, 1, 1, '2025-07-07 11:12:26', '2025-07-07 11:12:26', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2159, 130, 'UPDATE_BY', '更新人', 'varchar(255)', 'String', 'input', 'string', NULL, 'updateBy', 'UpdateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 8, 1, 1, '2025-07-07 11:12:26', '2025-07-07 11:12:26', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2160, 130, 'CREATE_ORG', '创建机构', 'varchar(255)', 'String', 'input', 'string', NULL, 'createOrg', 'CreateOrg', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 9, 1, 1, '2025-07-07 11:12:27', '2025-07-07 11:12:27', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2161, 130, 'UPDATE_ORG', '更新机构', 'varchar(255)', 'String', 'input', 'string', NULL, 'updateOrg', 'UpdateOrg', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 10, 1, 1, '2025-07-07 11:12:27', '2025-07-07 11:12:27', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2162, 131, 'GROUP_ID', '主键', 'bigint', 'Long', 'input', 'number', NULL, 'groupId', 'GroupId', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, 1, '2025-07-07 11:12:27', '2025-07-07 11:12:27', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2163, 131, 'GROUP_CODE', '分组代码', 'bigint', 'Long', 'input', 'number', NULL, 'groupCode', 'GroupCode', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 2, 1, 1, '2025-07-07 11:12:28', '2025-07-07 11:12:28', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2164, 131, 'GROUP_NAME', '分组名称', 'varchar(255)', 'String', 'input', 'string', NULL, 'groupName', 'GroupName', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'input', '', '0', NULL, 3, 1, 1, '2025-07-07 11:12:28', '2025-07-07 11:12:28', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2165, 131, 'REPEAT_CHECK', '重复校验 1打开 0关闭', 'decimal(1,0)', 'Integer', 'input', 'number', NULL, 'repeatCheck', 'RepeatCheck', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 4, 1, 1, '2025-07-07 11:12:29', '2025-07-07 11:12:29', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2166, 131, 'GROUP_PARENT_ID', '主键', 'bigint', 'Long', 'input', 'number', NULL, 'groupParentId', 'GroupParentId', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 5, 1, 1, '2025-07-07 11:12:29', '2025-07-07 11:12:29', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2167, 131, 'GROUP_LEVEL', '分组层级;1,2,3,4', 'tinyint', 'Boolean', 'input', 'string', NULL, 'groupLevel', 'GroupLevel', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'switch', '', '0', NULL, 6, 1, 1, '2025-07-07 11:12:30', '2025-07-07 11:12:30', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2168, 131, 'CREATE_TIME', '创建时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 7, 1, 1, '2025-07-07 11:12:30', '2025-07-07 11:12:30', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2169, 131, 'UPDATE_TIME', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 8, 1, 1, '2025-07-07 11:12:30', '2025-07-07 11:12:30', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2170, 131, 'CREATE_BY', '创建人', 'varchar(255)', 'String', 'input', 'string', NULL, 'createBy', 'CreateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 9, 1, 1, '2025-07-07 11:12:31', '2025-07-07 11:12:31', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2171, 131, 'UPDATE_BY', '更新人', 'varchar(255)', 'String', 'input', 'string', NULL, 'updateBy', 'UpdateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 10, 1, 1, '2025-07-07 11:12:31', '2025-07-07 11:12:31', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2172, 131, 'TYPE_ORG', '所属机构', 'varchar(20)', 'String', 'select', 'string', NULL, 'typeOrg', 'TypeOrg', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 11, 1, 1, '2025-07-07 11:12:32', '2025-07-07 11:12:32', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2173, 132, 'AUDIT_NL_ID', '主键', 'bigint', 'Long', 'input', 'number', NULL, 'auditNlId', 'AuditNlId', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, 1, '2025-07-07 11:12:32', '2025-07-07 11:12:32', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2174, 132, 'NAMELIST_TYPE', '名单类型', 'varchar(150)', 'String', 'select', 'string', NULL, 'namelistType', 'NamelistType', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 2, 1, 1, '2025-07-07 11:12:33', '2025-07-07 11:12:33', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2175, 132, 'NAMELIST_DIM', '名单维度,维度CODE', 'varchar(60)', 'String', 'input', 'string', NULL, 'namelistDim', 'NamelistDim', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 3, 1, 1, '2025-07-07 11:12:33', '2025-07-07 11:12:33', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2176, 132, 'NAMELIST_VALUE', '维度值', 'text', 'String', 'input', 'string', NULL, 'namelistValue', 'NamelistValue', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 4, 1, 1, '2025-07-07 11:12:34', '2025-07-07 11:12:34', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2177, 132, 'NAMELIST_SUB_DIM', '名单维度,维度CODE', 'varchar(60)', 'String', 'input', 'string', NULL, 'namelistSubDim', 'NamelistSubDim', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 5, 1, 1, '2025-07-07 11:12:34', '2025-07-07 11:12:34', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2178, 132, 'NAMELIST_SUB_DIM_VALUE', '维度值', 'varchar(300)', 'String', 'input', 'string', NULL, 'namelistSubDimValue', 'NamelistSubDimValue', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 6, 1, 1, '2025-07-07 11:12:34', '2025-07-07 11:12:34', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2179, 132, 'NAMELIST_STATUS', '状态 1-启用， 0-禁用', 'bigint', 'Long', 'select', 'number', NULL, 'namelistStatus', 'NamelistStatus', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'radio', '', '0', NULL, 7, 1, 1, '2025-07-07 11:12:35', '2025-07-07 11:12:35', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2180, 132, 'IMMUNE_RULE', '白名单规则ID', 'text', 'String', 'input', 'string', NULL, 'immuneRule', 'ImmuneRule', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 8, 1, 1, '2025-07-07 11:12:35', '2025-07-07 11:12:35', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2181, 132, 'EXPIRE_TIME', '过期时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'expireTime', 'ExpireTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 9, 1, 1, '2025-07-07 11:12:36', '2025-07-07 11:12:36', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2182, 132, 'TAG_INFO', '标签信息,JSON字符串', 'text', 'String', 'input', 'string', NULL, 'tagInfo', 'TagInfo', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 10, 1, 1, '2025-07-07 11:12:36', '2025-07-07 11:12:36', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2183, 132, 'CREATE_TIME', '创建时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 11, 1, 1, '2025-07-07 11:12:37', '2025-07-07 11:12:37', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2184, 132, 'UPDATE_TIME', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 12, 1, 1, '2025-07-07 11:12:37', '2025-07-07 11:12:37', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2185, 132, 'CREATE_ORG', '创建机构', 'varchar(255)', 'String', 'input', 'string', NULL, 'createOrg', 'CreateOrg', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 13, 1, 1, '2025-07-07 11:12:38', '2025-07-07 11:12:38', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2186, 132, 'UPDATE_ORG', '更新机构', 'varchar(255)', 'String', 'input', 'string', NULL, 'updateOrg', 'UpdateOrg', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 14, 1, 1, '2025-07-07 11:12:38', '2025-07-07 11:12:38', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2187, 132, 'NAMELIST_SOURCE', '名单来源  1-规则触发、2-人工添加、3-系统同步', 'bigint', 'Long', 'input', 'number', NULL, 'namelistSource', 'NamelistSource', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 15, 1, 1, '2025-07-07 11:12:38', '2025-07-07 11:12:38', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2188, 132, 'CREATE_BY', '创建人', 'varchar(255)', 'String', 'input', 'string', NULL, 'createBy', 'CreateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 16, 1, 1, '2025-07-07 11:12:39', '2025-07-07 11:12:39', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2189, 132, 'UPDATE_BY', '更新人', 'varchar(255)', 'String', 'input', 'string', NULL, 'updateBy', 'UpdateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 17, 1, 1, '2025-07-07 11:12:39', '2025-07-07 11:12:39', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2190, 132, 'NAMELIST_REMARK', '名单备注', 'varchar(600)', 'String', 'input', 'string', NULL, 'namelistRemark', 'NamelistRemark', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'textarea', '', '0', NULL, 18, 1, 1, '2025-07-07 11:12:40', '2025-07-07 11:12:40', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2191, 132, 'NAMELIST_ID', '名单记录id', 'bigint', 'Long', 'input', 'number', NULL, 'namelistId', 'NamelistId', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 19, 1, 1, '2025-07-07 11:12:40', '2025-07-07 11:12:40', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2192, 132, 'UPDATE_FLAG', '更新状态 2修改前 1修改后 0初始状态', 'bigint', 'Long', 'input', 'number', NULL, 'updateFlag', 'UpdateFlag', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 20, 1, 1, '2025-07-07 11:12:41', '2025-07-07 11:12:41', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2193, 132, 'AUDIT_CODE', '复核表code', 'varchar(20)', 'String', 'input', 'string', NULL, 'auditCode', 'AuditCode', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 21, 1, 1, '2025-07-07 11:12:41', '2025-07-07 11:12:41', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2194, 132, 'GROUP_NAMES', '分组名称', 'varchar(255)', 'String', 'input', 'string', NULL, 'groupNames', 'GroupNames', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 22, 1, 1, '2025-07-07 11:12:41', '2025-07-07 11:12:41', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2195, 133, 'ID_NO', '主键', 'bigint', 'Long', 'input', 'number', NULL, 'idNo', 'IdNo', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, 1, '2025-07-07 11:12:42', '2025-07-07 11:12:42', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2196, 133, 'INDEX_CODE', '索引', 'varchar(50)', 'String', 'input', 'string', NULL, 'indexCode', 'IndexCode', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 2, 1, 1, '2025-07-07 11:12:42', '2025-07-07 11:12:42', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2197, 133, 'INDEX_NAME', '索引名称', 'varchar(600)', 'String', 'input', 'string', NULL, 'indexName', 'IndexName', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'textarea', '', '0', NULL, 3, 1, 1, '2025-07-07 11:12:43', '2025-07-07 11:12:43', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2198, 133, 'NAMELIST_CODE', '索引关联名单库', 'varchar(50)', 'String', 'input', 'string', NULL, 'namelistCode', 'NamelistCode', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 4, 1, 1, '2025-07-07 11:12:43', '2025-07-07 11:12:43', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2199, 133, 'NAMELIST_NAME', '名单库名称', 'varchar(50)', 'String', 'input', 'string', NULL, 'namelistName', 'NamelistName', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'input', '', '0', NULL, 5, 1, 1, '2025-07-07 11:12:44', '2025-07-07 11:12:44', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2200, 133, 'CREATE_TIME', '创建时间', 'datetime(3)', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 6, 1, 1, '2025-07-07 11:12:44', '2025-07-07 11:12:44', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2201, 133, 'UPDATE_TIME', '更新时间', 'datetime(3)', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 7, 1, 1, '2025-07-07 11:12:45', '2025-07-07 11:12:45', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2202, 133, 'CREATE_BY', '创建人', 'varchar(20)', 'String', 'input', 'string', NULL, 'createBy', 'CreateBy', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 8, 1, 1, '2025-07-07 11:12:45', '2025-07-07 11:12:45', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2203, 133, 'UPDATE_BY', '更新人', 'varchar(20)', 'String', 'input', 'string', NULL, 'updateBy', 'UpdateBy', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 9, 1, 1, '2025-07-07 11:12:45', '2025-07-07 11:12:45', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2204, 134, 'ID_NO', '主键', 'bigint', 'Long', 'input', 'number', NULL, 'idNo', 'IdNo', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, 1, '2025-07-07 11:12:46', '2025-07-07 11:12:46', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2205, 134, 'FIELD_CODE', '字段', 'varchar(50)', 'String', 'input', 'string', NULL, 'fieldCode', 'FieldCode', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 2, 1, 1, '2025-07-07 11:12:46', '2025-07-07 11:12:46', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2206, 134, 'FIELD_NAME', '字段名', 'varchar(50)', 'String', 'input', 'string', NULL, 'fieldName', 'FieldName', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'input', '', '0', NULL, 3, 1, 1, '2025-07-07 11:12:47', '2025-07-07 11:12:47', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2207, 134, 'FIELD_TYPE', '主维度：dim 子维度：sub_dim 附加属性：tag', 'varchar(16)', 'String', 'select', 'string', NULL, 'fieldType', 'FieldType', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 4, 1, 1, '2025-07-07 11:12:47', '2025-07-07 11:12:47', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2208, 134, 'FIELD_JAVA_TYPE', 'java字段类型', 'varchar(32)', 'String', 'select', 'string', NULL, 'fieldJavaType', 'FieldJavaType', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 5, 1, 1, '2025-07-07 11:12:48', '2025-07-07 11:12:48', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2209, 134, 'FIELD_INDEX_TYPE', '索引字段类型', 'varchar(32)', 'String', 'select', 'string', NULL, 'fieldIndexType', 'FieldIndexType', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 6, 1, 1, '2025-07-07 11:12:48', '2025-07-07 11:12:48', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2210, 134, 'MATCH_METHOD', '匹配方式 1：模糊匹配  ；2-其他', 'tinyint', 'Boolean', 'input', 'string', NULL, 'matchMethod', 'MatchMethod', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'switch', '', '0', NULL, 7, 1, 1, '2025-07-07 11:12:49', '2025-07-07 11:12:49', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2211, 134, 'MULT_VAL', '是否支持多值 0-不支持  1-支持', 'tinyint', 'Boolean', 'input', 'string', NULL, 'multVal', 'MultVal', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'switch', '', '0', NULL, 8, 1, 1, '2025-07-07 11:12:49', '2025-07-07 11:12:49', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2212, 134, 'NAMELIST_CODE', '索引关联名单库', 'varchar(50)', 'String', 'input', 'string', NULL, 'namelistCode', 'NamelistCode', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 9, 1, 1, '2025-07-07 11:12:49', '2025-07-07 11:12:49', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2213, 134, 'INDEX_CODE', '索引编码', 'varchar(50)', 'String', 'input', 'string', NULL, 'indexCode', 'IndexCode', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 10, 1, 1, '2025-07-07 11:12:50', '2025-07-07 11:12:50', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2214, 134, 'UNION_FIELD', '组合字段', 'varchar(255)', 'String', 'input', 'string', NULL, 'unionField', 'UnionField', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 11, 1, 1, '2025-07-07 11:12:50', '2025-07-07 11:12:50', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2215, 134, 'SYSTEM_FIELD', '是否系统字段(0-否，1-是)', 'tinyint', 'Boolean', 'input', 'string', NULL, 'systemField', 'SystemField', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'switch', '', '0', NULL, 12, 1, 1, '2025-07-07 11:12:51', '2025-07-07 11:12:51', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2216, 134, 'CREATE_TIME', '创建时间', 'datetime(3)', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 13, 1, 1, '2025-07-07 11:12:51', '2025-07-07 11:12:51', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2217, 134, 'CREATE_BY', '创建人', 'varchar(20)', 'String', 'input', 'string', NULL, 'createBy', 'CreateBy', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 14, 1, 1, '2025-07-07 11:12:52', '2025-07-07 11:12:52', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2218, 135, 'LOCK_ID', '主键', 'bigint', 'Long', 'input', 'number', NULL, 'lockId', 'LockId', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, 1, '2025-07-07 11:12:52', '2025-07-07 11:12:52', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2219, 135, 'LOCK_NAME', '锁名称，需唯一', 'varchar(255)', 'String', 'input', 'string', NULL, 'lockName', 'LockName', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'input', '', '0', NULL, 2, 1, 1, '2025-07-07 11:12:52', '2025-07-07 11:12:52', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2220, 135, 'VERSION', '版本', 'int', 'Integer', 'input', 'number', NULL, 'VERSION', 'Version', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 3, 1, 1, '2025-07-07 11:12:53', '2025-07-07 11:12:53', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2221, 135, 'LOCKED_BY', '上锁', 'varchar(255)', 'String', 'input', 'string', NULL, 'lockedBy', 'LockedBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 4, 1, 1, '2025-07-07 11:12:53', '2025-07-07 11:12:53', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2222, 135, 'LOCKED_TIME', '上锁时间', 'datetime(6)', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'lockedTime', 'LockedTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 5, 1, 1, '2025-07-07 11:12:54', '2025-07-07 11:12:54', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2223, 135, 'EXPIRE_TIME', '锁过期时间', 'datetime(6)', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'expireTime', 'ExpireTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 6, 1, 1, '2025-07-07 11:12:54', '2025-07-07 11:12:54', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2224, 136, 'SNAPSHOT_ID', '主键', 'bigint', 'Long', 'input', 'number', NULL, 'snapshotId', 'SnapshotId', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, 1, '2025-07-07 11:12:55', '2025-07-07 11:12:55', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2225, 136, 'audit_nl_id', '复核名单表id', 'bigint', 'Long', 'input', 'number', NULL, 'auditNlId', 'AuditNlId', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 2, 1, 1, '2025-07-07 11:12:55', '2025-07-07 11:12:55', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2226, 136, 'SNAPSHOT_TYPE', '快照类型 1重复校验、2上限校验', 'bigint', 'Long', 'select', 'number', NULL, 'snapshotType', 'SnapshotType', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 3, 1, 1, '2025-07-07 11:12:56', '2025-07-07 11:12:56', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2227, 136, 'DATA_INFO', '数据信息 重复或上限校验要失效或合并的名单记录json格式', 'text', 'String', 'input', 'string', NULL, 'dataInfo', 'DataInfo', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 4, 1, 1, '2025-07-07 11:12:56', '2025-07-07 11:12:56', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2228, 136, 'CREATE_TIME', '创建时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 5, 1, 1, '2025-07-07 11:12:56', '2025-07-07 11:12:56', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2229, 136, 'UPDATE_TIME', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 6, 1, 1, '2025-07-07 11:12:57', '2025-07-07 11:12:57', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2230, 136, 'CREATE_BY', '创建人', 'varchar(255)', 'String', 'input', 'string', NULL, 'createBy', 'CreateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 7, 1, 1, '2025-07-07 11:12:57', '2025-07-07 11:12:57', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2231, 136, 'UPDATE_BY', '更新人', 'varchar(255)', 'String', 'input', 'string', NULL, 'updateBy', 'UpdateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 8, 1, 1, '2025-07-07 11:12:58', '2025-07-07 11:12:58', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2232, 136, 'CREATE_ORG', '创建机构', 'varchar(255)', 'String', 'input', 'string', NULL, 'createOrg', 'CreateOrg', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 9, 1, 1, '2025-07-07 11:12:58', '2025-07-07 11:12:58', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2233, 136, 'UPDATE_ORG', '更新机构', 'varchar(255)', 'String', 'input', 'string', NULL, 'updateOrg', 'UpdateOrg', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 10, 1, 1, '2025-07-07 11:12:59', '2025-07-07 11:12:59', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2234, 137, 'ID_NO', '主键', 'bigint', 'Long', 'input', 'number', NULL, 'idNo', 'IdNo', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, 1, '2025-07-07 11:12:59', '2025-07-07 11:12:59', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2235, 137, 'TYPE_CODE', '名单类型代码', 'varchar(60)', 'String', 'select', 'string', NULL, 'typeCode', 'TypeCode', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 2, 1, 1, '2025-07-07 11:13:00', '2025-07-07 11:13:00', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2236, 137, 'DATE_TIME', '当前记录对应的时间,如2023年9月20日 09:10:29的记录会被统计到2023年9月20日', 'datetime(6)', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'dateTime', 'DateTime', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 3, 1, 1, '2025-07-07 11:13:00', '2025-07-07 11:13:00', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2237, 137, 'ADD_NUM', '新增名单记录数量', 'bigint', 'Long', 'input', 'number', NULL, 'addNum', 'AddNum', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 4, 1, 1, '2025-07-07 11:13:00', '2025-07-07 11:13:00', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2238, 137, 'EDIT_NUM', '编辑名单记录数量', 'bigint', 'Long', 'input', 'number', NULL, 'editNum', 'EditNum', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 5, 1, 1, '2025-07-07 11:13:01', '2025-07-07 11:13:01', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2239, 137, 'DEL_NUM', '删除名单记录数量', 'bigint', 'Long', 'input', 'number', NULL, 'delNum', 'DelNum', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 6, 1, 1, '2025-07-07 11:13:01', '2025-07-07 11:13:01', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2240, 138, 'TAG_ID', '主键', 'bigint', 'Long', 'input', 'number', NULL, 'tagId', 'TagId', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, 1, '2025-07-07 11:13:02', '2025-07-07 11:13:02', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2241, 138, 'TAG_CODE', '标签代码', 'varchar(150)', 'String', 'input', 'string', NULL, 'tagCode', 'TagCode', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 2, 1, 1, '2025-07-07 11:13:02', '2025-07-07 11:13:02', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2242, 138, 'TAG_NAME', '标签名称', 'varchar(255)', 'String', 'input', 'string', NULL, 'tagName', 'TagName', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'input', '', '0', NULL, 3, 1, 1, '2025-07-07 11:13:03', '2025-07-07 11:13:03', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2243, 138, 'RELATE_FIELD', '关联的字段，TAG1,TAG2...', 'varchar(150)', 'String', 'input', 'string', NULL, 'relateField', 'RelateField', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 4, 1, 1, '2025-07-07 11:13:03', '2025-07-07 11:13:03', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2244, 138, 'MUST_INPUTED', '是否为必输项', 'varchar(16)', 'String', 'input', 'string', NULL, 'mustInputed', 'MustInputed', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 5, 1, 1, '2025-07-07 11:13:03', '2025-07-07 11:13:03', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2245, 138, 'CREATE_TIME', '创建时间', 'datetime(6)', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 6, 1, 1, '2025-07-07 11:13:04', '2025-07-07 11:13:04', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2246, 138, 'UPDATE_TIME', '更新时间', 'datetime(6)', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 7, 1, 1, '2025-07-07 11:13:04', '2025-07-07 11:13:04', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2247, 138, 'CREATE_BY', '创建人', 'varchar(255)', 'String', 'input', 'string', NULL, 'createBy', 'CreateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 8, 1, 1, '2025-07-07 11:13:05', '2025-07-07 11:13:05', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2248, 138, 'UPDATE_BY', '更新人', 'varchar(255)', 'String', 'input', 'string', NULL, 'updateBy', 'UpdateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 9, 1, 1, '2025-07-07 11:13:05', '2025-07-07 11:13:05', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2249, 138, 'TAG_INPUT_TYPE', '输入类型 dropDownRadio-下拉单选 dropDownMulti-下拉复选 singleText-单行文本', 'varchar(255)', 'String', 'select', 'string', NULL, 'tagInputType', 'TagInputType', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 10, 1, 1, '2025-07-07 11:13:06', '2025-07-07 11:13:06', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2250, 138, 'MULTIPLE_VALUE', '是否多值，默认0-不是多值，1-多值', 'tinyint(1)', 'Boolean', 'input', 'string', NULL, 'multipleValue', 'MultipleValue', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'switch', '', '0', NULL, 11, 1, 1, '2025-07-07 11:13:06', '2025-07-07 11:13:06', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2251, 138, 'TAG_TYPE', '附加属性类型，0-基础附加属性，1-组合附加属性,2-组合附加属性的子维度', 'tinyint', 'Boolean', 'select', 'string', NULL, 'tagType', 'TagType', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 12, 1, 1, '2025-07-07 11:13:07', '2025-07-07 11:13:07', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2252, 138, 'DATA_MASKING_TYPE', '规则类型（0-不脱敏，1-全部脱敏，2-前后脱敏，3-中间脱敏）', 'tinyint', 'Boolean', 'select', 'string', NULL, 'dataMaskingType', 'DataMaskingType', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 13, 1, 1, '2025-07-07 11:13:07', '2025-07-07 11:13:07', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2253, 138, 'DATA_MASKING_RULE', '数据脱敏规则', 'varchar(10)', 'String', 'input', 'string', NULL, 'dataMaskingRule', 'DataMaskingRule', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 14, 1, 1, '2025-07-07 11:13:07', '2025-07-07 11:13:07', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2254, 138, 'TAG_PARENT_ID', '父级TAG_ID,子属性的父级', 'bigint', 'Long', 'input', 'number', NULL, 'tagParentId', 'TagParentId', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 15, 1, 1, '2025-07-07 11:13:08', '2025-07-07 11:13:08', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2255, 139, 'ENUM_ID', '枚举ID', 'bigint', 'Long', 'input', 'number', NULL, 'enumId', 'EnumId', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, 1, '2025-07-07 11:13:08', '2025-07-07 11:13:08', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2256, 139, 'ENUM_CODE', '枚举代码', 'varchar(255)', 'String', 'input', 'string', NULL, 'enumCode', 'EnumCode', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 2, 1, 1, '2025-07-07 11:13:09', '2025-07-07 11:13:09', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2257, 139, 'ENUM_NAME', '枚举名称', 'varchar(255)', 'String', 'input', 'string', NULL, 'enumName', 'EnumName', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'input', '', '0', NULL, 3, 1, 1, '2025-07-07 11:13:09', '2025-07-07 11:13:09', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2258, 139, 'TAG_CODE', '附加属性code', 'varchar(150)', 'String', 'input', 'string', NULL, 'tagCode', 'TagCode', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 4, 1, 1, '2025-07-07 11:13:10', '2025-07-07 11:13:10', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2259, 139, 'CREATE_TIME', '创建时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 5, 1, 1, '2025-07-07 11:13:10', '2025-07-07 11:13:10', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2260, 139, 'UPDATE_TIME', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 6, 1, 1, '2025-07-07 11:13:11', '2025-07-07 11:13:11', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2261, 139, 'CREATE_BY', '创建人', 'varchar(255)', 'String', 'input', 'string', NULL, 'createBy', 'CreateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 7, 1, 1, '2025-07-07 11:13:11', '2025-07-07 11:13:11', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2262, 139, 'UPDATE_BY', '更新人', 'varchar(255)', 'String', 'input', 'string', NULL, 'updateBy', 'UpdateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 8, 1, 1, '2025-07-07 11:13:11', '2025-07-07 11:13:11', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2263, 140, 'TYPE_ID', '主键', 'bigint', 'Long', 'select', 'number', NULL, 'typeId', 'TypeId', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 1, 1, 1, '2025-07-07 11:13:12', '2025-07-07 11:13:12', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2264, 140, 'TYPE_CODE', '名单类型代码', 'varchar(60)', 'String', 'select', 'string', NULL, 'typeCode', 'TypeCode', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 2, 1, 1, '2025-07-07 11:13:12', '2025-07-07 11:13:12', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2265, 140, 'TYPE_NAME', '名单类型名字', 'varchar(600)', 'String', 'select', 'string', NULL, 'typeName', 'TypeName', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'select', '', '0', NULL, 3, 1, 1, '2025-07-07 11:13:13', '2025-07-07 11:13:13', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2266, 140, 'TYPE_PARENT_ID', '名单类型父ID', 'bigint', 'Long', 'select', 'number', NULL, 'typeParentId', 'TypeParentId', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 4, 1, 1, '2025-07-07 11:13:13', '2025-07-07 11:13:13', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2267, 140, 'TYPE_WAY', '名单类型方式， 1-普通名单， 2-白名单类型', 'int', 'Integer', 'select', 'number', NULL, 'typeWay', 'TypeWay', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 5, 1, 1, '2025-07-07 11:13:14', '2025-07-07 11:13:14', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2268, 140, 'BUTTON_PERMISSION', '按钮权限列表，逗号隔开', 'varchar(500)', 'String', 'input', 'string', NULL, 'buttonPermission', 'ButtonPermission', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'textarea', '', '0', NULL, 6, 1, 1, '2025-07-07 11:13:14', '2025-07-07 11:13:14', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2269, 140, 'CREATE_TIME', '创建时间', 'datetime(6)', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 7, 1, 1, '2025-07-07 11:13:14', '2025-07-07 11:13:14', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2270, 140, 'UPDATE_TIME', '更新时间', 'datetime(6)', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'datetime', '', '0', NULL, 8, 1, 1, '2025-07-07 11:13:15', '2025-07-07 11:13:15', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2271, 140, 'CREATE_BY', '创建人', 'varchar(255)', 'String', 'input', 'string', NULL, 'createBy', 'CreateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 9, 1, 1, '2025-07-07 11:13:15', '2025-07-07 11:13:15', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2272, 140, 'UPDATE_BY', '更新人', 'varchar(255)', 'String', 'input', 'string', NULL, 'updateBy', 'UpdateBy', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 10, 1, 1, '2025-07-07 11:13:16', '2025-07-07 11:13:16', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2273, 140, 'TYPE_ORG', '所属机构', 'varchar(20)', 'String', 'select', 'string', NULL, 'typeOrg', 'TypeOrg', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 11, 1, 1, '2025-07-07 11:13:16', '2025-07-07 11:13:16', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2274, 140, 'TYPE_COUNT', '名单库下的名单数量', 'bigint', 'Long', 'select', 'number', NULL, 'typeCount', 'TypeCount', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 12, 1, 1, '2025-07-07 11:13:17', '2025-07-07 11:13:17', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2275, 140, 'TYPE_COUNT_TIME', '名单库下的名单数量统计的时间', 'datetime', 'LocalDateTime', 'select', 'string', 'java.time.LocalDateTime', 'typeCountTime', 'TypeCountTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'BETWEEN', 'select', '', '0', NULL, 13, 1, 1, '2025-07-07 11:13:17', '2025-07-07 11:13:17', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2276, 140, 'TYPE_ORG_PATH', '所属机构路径', 'varchar(500)', 'String', 'select', 'string', NULL, 'typeOrgPath', 'TypeOrgPath', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 14, 1, 1, '2025-07-07 11:13:18', '2025-07-07 11:13:18', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2277, 140, 'RULE_TYPE', '适用规则 1- 非可信画像规则 2-可信画像规则', 'bigint', 'Long', 'select', 'number', NULL, 'ruleType', 'RuleType', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 15, 1, 1, '2025-07-07 11:13:18', '2025-07-07 11:13:18', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2278, 140, 'INDEX_SWITCH', '名单模板指标开关 1允许使用 0禁止使用', 'bigint', 'Long', 'input', 'number', NULL, 'indexSwitch', 'IndexSwitch', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 16, 1, 1, '2025-07-07 11:13:18', '2025-07-07 11:13:18', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2279, 140, 'MAX_VALID_INTERVAL', '最大有效期', 'bigint', 'Long', 'input', 'number', NULL, 'maxValidInterval', 'MaxValidInterval', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 17, 1, 1, '2025-07-07 11:13:19', '2025-07-07 11:13:19', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2280, 140, 'MAX_VALID_UNIT', '最大有效期单位', 'varchar(2)', 'String', 'input', 'string', NULL, 'maxValidUnit', 'MaxValidUnit', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 18, 1, 1, '2025-07-07 11:13:19', '2025-07-07 11:13:19', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2281, 140, 'EXPIRE_WARN_INTERVAL', '到期告警时间跨度(天)', 'bigint', 'Long', 'input', 'number', NULL, 'expireWarnInterval', 'ExpireWarnInterval', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 19, 1, 1, '2025-07-07 11:13:20', '2025-07-07 11:13:20', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2282, 140, 'FOREIGN_SWITCH', '对外接口开关 0关闭 1打开', 'bigint', 'Long', 'input', 'number', NULL, 'foreignSwitch', 'ForeignSwitch', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 20, 1, 1, '2025-07-07 11:13:20', '2025-07-07 11:13:20', '0', 'GLOWXQ');
INSERT INTO `generator_table_column` VALUES (2283, 140, 'WITHIN_AUTH', '辖内维护开关 0关闭 1打开', 'bigint', 'Long', 'input', 'number', NULL, 'withinAuth', 'WithinAuth', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 21, 1, 1, '2025-07-07 11:13:21', '2025-07-07 11:13:21', '0', 'GLOWXQ');

-- ----------------------------
-- Table structure for group
-- ----------------------------
DROP TABLE IF EXISTS `group`;
CREATE TABLE `group`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '班级名',
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '班级代码',
  `principal_user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '负责人ID',
  `principal_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '负责人姓名',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '班级描述',
  `color` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '#009688' COMMENT '班级颜色',
  `text_color` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '#FFFFFF' COMMENT '字体颜色',
  `enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '班级表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of group
-- ----------------------------
INSERT INTO `group` VALUES (2, '超级测试班', '123456', '1', '管理员', '123', '#009688FF', '#FFFFFFFF', 1, '2025-03-27 23:00:22', '2025-06-07 19:27:51', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group` VALUES (7, '真的很一班', 'O2T7YR', '10', 'teacher', '冲刺', '#0071e3', '#FFFFFF', 1, '2025-06-12 23:14:24', '2025-06-12 23:14:24', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `group` VALUES (8, '测试班级2490', 'F7PSMD', '1', '张三', '这是一个自动生成的测试班级2490，用于测试系统功能。', '#FF6B6B', '#FFFFFF', 1, '2025-06-27 00:50:01', '2025-06-27 00:50:01', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group` VALUES (9, '测试班级6696', 'PREBZM', '1', '张三', '这是一个自动生成的测试班级6696，用于测试系统功能。', '#FF6B6B', '#FFFFFF', 1, '2025-06-27 00:50:47', '2025-06-27 00:50:47', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group` VALUES (10, '测试班级2329', 'EYTND7', '1', '张三', '这是一个自动生成的测试班级2329，用于测试系统功能。', '#FF6B6B', '#FFFFFF', 1, '2025-06-27 00:54:17', '2025-06-27 00:54:17', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group` VALUES (11, '文档书写班级', 'YKRMQ2', '1', 'glowxq', '写文档用', '#0071e3', '#FFFFFF', 1, '2025-06-30 23:44:45', '2025-06-30 23:44:45', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for group_bind
-- ----------------------------
DROP TABLE IF EXISTS `group_bind`;
CREATE TABLE `group_bind`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` bigint NOT NULL COMMENT '班级ID',
  `business_id` bigint NOT NULL COMMENT '班级绑定的业务ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '绑定类型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `group_user_group_id`(`group_id` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  CONSTRAINT `group_user_group_id` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 257 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '班级绑定数据' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of group_bind
-- ----------------------------
INSERT INTO `group_bind` VALUES (3, 2, 5, 'User', '2025-03-28 13:20:19', '2025-06-07 19:27:51', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (5, 2, 2, 'User', '2025-03-28 15:46:57', '2025-06-07 19:27:51', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (29, 2, 3, 'User', '2025-03-30 04:49:33', '2025-06-07 19:27:51', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (31, 2, 4, 'User', '2025-03-30 04:49:33', '2025-06-07 19:27:51', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (69, 2, 7, 'Work', '2025-04-03 00:21:51', '2025-06-07 19:27:51', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (71, 2, 7, 'Work', '2025-04-03 01:44:10', '2025-06-07 19:27:51', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (79, 2, 8, 'Work', '2025-04-04 04:38:29', '2025-06-07 19:27:51', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (88, 2, 9, 'Work', '2025-04-04 15:00:00', '2025-06-07 19:27:51', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (97, 2, 6, 'Work', '2025-04-04 17:17:29', '2025-06-07 19:27:51', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (116, 2, 3, 'Topic', '2025-04-12 19:30:16', '2025-06-07 19:27:51', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (128, 2, 4, 'Topic', '2025-04-13 00:57:47', '2025-06-07 19:27:51', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (159, 2, 11, 'User', '2025-05-27 04:18:45', '2025-06-07 19:27:51', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (169, 2, 5, 'Topic', '2025-05-27 23:50:15', '2025-06-07 19:27:51', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (184, 2, 4, 'Course', '2025-06-06 02:32:51', '2025-06-07 19:27:51', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (201, 2, 39, 'User', '2025-06-21 12:25:11', '2025-06-21 12:25:11', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (203, 2, 44, 'User', '2025-06-21 13:08:15', '2025-06-21 13:08:15', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (217, 7, 46, 'User', '2025-06-21 13:37:09', '2025-06-21 13:37:09', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (227, 2, 55, 'User', '2025-06-24 16:25:14', '2025-06-24 16:25:14', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (233, 7, 7, 'Course', '2025-06-27 01:05:48', '2025-06-27 01:05:48', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (243, 2, 8, 'User', '2025-06-28 23:02:04', '2025-06-28 23:02:04', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (245, 2, 54, 'User', '2025-06-28 23:02:17', '2025-06-28 23:02:17', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (247, 2, 1, 'User', '2025-06-29 00:51:44', '2025-06-29 00:51:44', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (250, 10, 10, 'User', '2025-06-29 16:39:05', '2025-06-29 16:39:05', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (251, 10, 9, 'Course', '2025-06-29 17:22:34', '2025-06-29 17:22:34', 'F', 10, 10, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (253, 11, 13, 'Topic', '2025-06-30 23:52:02', '2025-06-30 23:52:02', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (254, 2, 1, 'Course', '2025-08-07 23:09:32', '2025-08-07 23:09:32', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (255, 10, 8, 'Course', '2025-08-07 23:10:31', '2025-08-07 23:10:31', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `group_bind` VALUES (256, 9, 10, 'Course', '2025-08-19 19:59:15', '2025-08-19 19:59:15', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for judge
-- ----------------------------
DROP TABLE IF EXISTS `judge`;
CREATE TABLE `judge`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `judge_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评测Key',
  `problem_id` bigint UNSIGNED NOT NULL COMMENT '题目ID',
  `problem_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目自定义ID',
  `problem_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '题目标题',
  `user_id` bigint NOT NULL COMMENT '测评用户id',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '测评人',
  `group_id` bigint UNSIGNED NULL DEFAULT 0 COMMENT '团队id|不为团队内提交则为0',
  `contest_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '比赛id|非比赛题目默认为0',
  `contest_problem_id` bigint UNSIGNED NULL DEFAULT 0 COMMENT '比赛中题目排序id|非比赛题目默认为0',
  `business_id` bigint NOT NULL DEFAULT -1 COMMENT '关联业务表ID',
  `scene_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Normal' COMMENT '测评类型',
  `submit_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NormalJudge' COMMENT '提交类型',
  `problem_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Program' COMMENT '题目类型',
  `submit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交测评的时间',
  `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始测评的时间',
  `end_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '结束测评的时间',
  `use_time` int NOT NULL DEFAULT -1 COMMENT '使用时间',
  `auto_submit` tinyint NOT NULL DEFAULT -1 COMMENT '自动提交',
  `status` int NULL DEFAULT NULL COMMENT '结果码具体参考文档',
  `error_message` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误提醒(编译错误|或者vj提醒)',
  `time` int NULL DEFAULT NULL COMMENT '运行时间(ms)',
  `memory` int NULL DEFAULT NULL COMMENT '运行内存(kb)',
  `score` int NULL DEFAULT 0 COMMENT 'IO判题则不为空',
  `length` int NULL DEFAULT NULL COMMENT '代码长度',
  `flow_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程图URL',
  `code` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '代码',
  `reply_options` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[]' COMMENT '非编程题作答内容',
  `language` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '代码语言',
  `judge_server` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '判题机',
  `submit_ip` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '提交者所在ip',
  `version` int NOT NULL DEFAULT 0 COMMENT '乐观锁',
  `oi_rank_score` int NULL DEFAULT 0 COMMENT 'oi排行榜得分',
  `share_enable` tinyint(1) NULL DEFAULT 0 COMMENT '0为仅自己可见|1为全部人可见。',
  `manual_evaluation` tinyint(1) NULL DEFAULT 0 COMMENT '是否为人工评测',
  `other_judge_submit_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT 'vjudge判题在其它oj的提交id',
  `other_judge_username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'vjudge判题在其它oj的提交用户名',
  `other_judge_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'vjudge判题在其它oj的提交账号密码',
  `exception_stack_trace` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常堆栈信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `judge_unindex`(`judge_key` ASC) USING BTREE,
  UNIQUE INDEX `judge_problem_key_uindex`(`problem_key` ASC, `judge_key` ASC) USING BTREE,
  INDEX `problem_id_index`(`problem_id` ASC) USING BTREE,
  INDEX `user_id_index`(`user_id` ASC) USING BTREE,
  INDEX `username_index`(`name` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  CONSTRAINT `judge_problem_id_fk` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `judge_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 422 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '测评记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of judge
-- ----------------------------
INSERT INTO `judge` VALUES (420, 'Normal-Q1031-7JOI5P', 331, 'Q1031', '[GESP202406 三级]  寻找倍数', 1, '13667700000', 331, 331, 0, 331, 'Normal', 'NormalJudge', 'Programmer', '2025-08-20 15:32:43', '2025-08-20 15:32:43', '2025-08-20 15:32:43', -1, 1, -1, NULL, 2, 256, 0, 70, '', '#include<iostream>\nusing namespace std;\nint main() {\n\n     return 0;\n}', '[]', 'C++', '', '27.19.42.158', 0, 0, 0, 0, NULL, NULL, NULL, '', '2025-08-20 15:32:43', '2025-08-20 15:32:43', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `judge` VALUES (421, 'Normal-Q1031-9WSZEF', 331, 'Q1031', '[GESP202406 三级]  寻找倍数', 1, '13667700000', 331, 331, 0, 331, 'Normal', 'NormalJudge', 'Programmer', '2025-08-20 15:33:10', '2025-08-20 15:33:10', '2025-08-20 15:33:10', -1, 1, -1, NULL, 1, 264, 0, 70, '', '#include<iostream>\nusing namespace std;\nint main() {\n\n     return 0;\n}', '[]', 'C++', '', '27.19.42.158', 0, 0, 0, 0, NULL, NULL, NULL, '', '2025-08-20 15:33:10', '2025-08-20 15:33:10', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for judge_case
-- ----------------------------
DROP TABLE IF EXISTS `judge_case`;
CREATE TABLE `judge_case`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `judge_id` bigint UNSIGNED NOT NULL COMMENT '提交判题id',
  `judge_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自定义提交ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `problem_id` bigint UNSIGNED NOT NULL COMMENT '题目ID',
  `problem_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '题目自定义ID',
  `case_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '测试样例id',
  `status` int NULL DEFAULT NULL COMMENT '具体看结果码',
  `time` int NULL DEFAULT NULL COMMENT '测试该样例所用时间ms',
  `memory` int NULL DEFAULT NULL COMMENT '测试该样例所用空间KB',
  `score` int NULL DEFAULT NULL COMMENT 'IO得分',
  `group_num` int NULL DEFAULT NULL COMMENT 'subtask分组的组号',
  `seq` int NULL DEFAULT NULL COMMENT '排序',
  `mode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'default' COMMENT 'default,subtask_lowest,subtask_average',
  `input_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '样例输入|比赛不可看',
  `output_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '样例输出|比赛不可看',
  `user_output` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户样例输出|比赛不可看',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `judge_case_unindex`(`judge_id` ASC, `user_id` ASC, `problem_id` ASC, `case_id` ASC) USING BTREE,
  INDEX `judge_case_case_index`(`case_id` ASC) USING BTREE,
  INDEX `judge_case_problem_id_index`(`problem_id` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  CONSTRAINT `judge_case_judge_id` FOREIGN KEY (`judge_id`) REFERENCES `judge` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `judge_case_problem_id_fx` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 581 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '测评用例情况' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of judge_case
-- ----------------------------
INSERT INTO `judge_case` VALUES (561, 420, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 1, 'default', '01.in', '01.out', NULL, '2025-08-20 15:32:44', '2025-08-20 15:32:44', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (562, 420, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 2, 'default', '02.in', '02.out', NULL, '2025-08-20 15:32:44', '2025-08-20 15:32:44', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (563, 420, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 3, 'default', '03.in', '03.out', NULL, '2025-08-20 15:32:44', '2025-08-20 15:32:44', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (564, 420, NULL, 1, 331, NULL, NULL, -1, 2, 256, 0, 1, 4, 'default', '04.in', '04.out', NULL, '2025-08-20 15:32:44', '2025-08-20 15:32:44', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (565, 420, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 5, 'default', '05.in', '05.out', NULL, '2025-08-20 15:32:44', '2025-08-20 15:32:44', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (566, 420, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 6, 'default', '06.in', '06.out', NULL, '2025-08-20 15:32:44', '2025-08-20 15:32:44', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (567, 420, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 7, 'default', '07.in', '07.out', NULL, '2025-08-20 15:32:44', '2025-08-20 15:32:44', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (568, 420, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 8, 'default', '08.in', '08.out', NULL, '2025-08-20 15:32:44', '2025-08-20 15:32:44', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (569, 420, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 9, 'default', '09.in', '09.out', NULL, '2025-08-20 15:32:44', '2025-08-20 15:32:44', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (570, 420, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 10, 'default', '10.in', '10.out', NULL, '2025-08-20 15:32:44', '2025-08-20 15:32:44', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (571, 421, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 1, 'default', '01.in', '01.out', NULL, '2025-08-20 15:33:10', '2025-08-20 15:33:10', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (572, 421, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 2, 'default', '02.in', '02.out', NULL, '2025-08-20 15:33:10', '2025-08-20 15:33:10', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (573, 421, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 3, 'default', '03.in', '03.out', NULL, '2025-08-20 15:33:10', '2025-08-20 15:33:10', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (574, 421, NULL, 1, 331, NULL, NULL, -1, 1, 264, 0, 1, 4, 'default', '04.in', '04.out', NULL, '2025-08-20 15:33:10', '2025-08-20 15:33:10', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (575, 421, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 5, 'default', '05.in', '05.out', NULL, '2025-08-20 15:33:10', '2025-08-20 15:33:10', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (576, 421, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 6, 'default', '06.in', '06.out', NULL, '2025-08-20 15:33:10', '2025-08-20 15:33:10', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (577, 421, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 7, 'default', '07.in', '07.out', NULL, '2025-08-20 15:33:10', '2025-08-20 15:33:10', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (578, 421, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 8, 'default', '08.in', '08.out', NULL, '2025-08-20 15:33:10', '2025-08-20 15:33:10', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (579, 421, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 9, 'default', '09.in', '09.out', NULL, '2025-08-20 15:33:10', '2025-08-20 15:33:10', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `judge_case` VALUES (580, 421, NULL, 1, 331, NULL, NULL, -1, 1, 256, 0, 1, 10, 'default', '10.in', '10.out', NULL, '2025-08-20 15:33:10', '2025-08-20 15:33:10', 'F', NULL, NULL, 'GLOWXQ');

-- ----------------------------
-- Table structure for judge_server
-- ----------------------------
DROP TABLE IF EXISTS `judge_server`;
CREATE TABLE `judge_server`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '判题服务名字',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '判题机ip',
  `port` int NOT NULL COMMENT '判题机端口号',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip:port',
  `cpu_core` int NULL DEFAULT 0 COMMENT '判题机所在服务器cpu核心数',
  `free_memory` int NULL DEFAULT 0 COMMENT '判题机所在服务器空闲内存',
  `task_number` int NOT NULL DEFAULT 0 COMMENT '当前判题数',
  `max_task_number` int NOT NULL COMMENT '判题并发最大数',
  `enable` tinyint(1) NULL DEFAULT 0 COMMENT '0禁用|1启用',
  `remote_enable` tinyint(1) NULL DEFAULT NULL COMMENT '是否开启远程判题vj',
  `cf_submittable_enable` tinyint(1) NULL DEFAULT 1 COMMENT '是否可提交CF',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_judge_url`(`url` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '测评服务' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of judge_server
-- ----------------------------

-- ----------------------------
-- Table structure for meta_category
-- ----------------------------
DROP TABLE IF EXISTS `meta_category`;
CREATE TABLE `meta_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `pid` bigint NOT NULL COMMENT '父级ID',
  `deep` bigint NULL DEFAULT NULL COMMENT '层级',
  `sort` bigint NULL DEFAULT NULL COMMENT '排序',
  `enable` tinyint NOT NULL DEFAULT 1 COMMENT '启用',
  `has_children` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否有子级',
  `lock` tinyint NOT NULL DEFAULT 0 COMMENT '是否锁定',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除标识',
  `remark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of meta_category
-- ----------------------------
INSERT INTO `meta_category` VALUES (1, '电子产品', 0, 1, 1, 1, 1, 0, 'F', '顶级分类', 1001, 1001, '2025-04-25 11:55:07', '2025-04-25 11:55:07', 'GLOWXQ');
INSERT INTO `meta_category` VALUES (2, '手机', 1, 2, 1, 1, 1, 0, 'F', '二级分类', 1001, 1001, '2025-04-25 11:55:07', '2025-04-25 11:55:07', 'GLOWXQ');
INSERT INTO `meta_category` VALUES (3, '智能家居', 1, 2, 2, 1, 0, 0, 'F', '二级分类', 1001, 1001, '2025-04-25 11:55:07', '2025-04-25 11:55:07', 'GLOWXQ');
INSERT INTO `meta_category` VALUES (4, '旗舰机型', 2, 3, 1, 1, 0, 0, 'F', '三级分类', 1001, 1001, '2025-04-25 11:55:07', '2025-04-25 11:55:07', 'GLOWXQ');

-- ----------------------------
-- Table structure for meta_category_closure
-- ----------------------------
DROP TABLE IF EXISTS `meta_category_closure`;
CREATE TABLE `meta_category_closure`  (
  `ancestor_id` bigint NOT NULL COMMENT '祖先节点ID',
  `descendant_id` bigint NOT NULL COMMENT '后代节点ID',
  `depth` int NOT NULL COMMENT '祖先节点到后代节点的距离',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分类祖籍关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of meta_category_closure
-- ----------------------------
INSERT INTO `meta_category_closure` VALUES (1, 1, 0, 'GLOWXQ');
INSERT INTO `meta_category_closure` VALUES (1, 2, 1, 'GLOWXQ');
INSERT INTO `meta_category_closure` VALUES (1, 4, 2, 'GLOWXQ');
INSERT INTO `meta_category_closure` VALUES (2, 2, 0, 'GLOWXQ');
INSERT INTO `meta_category_closure` VALUES (2, 4, 1, 'GLOWXQ');
INSERT INTO `meta_category_closure` VALUES (3, 3, 0, 'GLOWXQ');
INSERT INTO `meta_category_closure` VALUES (4, 4, 0, 'GLOWXQ');

-- ----------------------------
-- Table structure for meta_image
-- ----------------------------
DROP TABLE IF EXISTS `meta_image`;
CREATE TABLE `meta_image`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '图片名字',
  `image_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '图片key',
  `business_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务类型',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '图片URL',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '图片介绍',
  `skip_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '跳转链接',
  `sort` int NOT NULL DEFAULT 1 COMMENT '排序(降序)',
  `enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `meta_image_image_key_uindex`(`image_key` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '图片' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of meta_image
-- ----------------------------
INSERT INTO `meta_image` VALUES (3, '头像1', 'img_96717014_0979a70d', 'Avatar', 'https://glowxq-oj-api.oss-cn-guangzhou.aliyuncs.com/local/png/2025/06/30/Snipaste_2025-06-23_22-09-01.png', '', '', 100, 1, '2025-06-30 23:18:41', '2025-06-30 23:18:41', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for meta_language
-- ----------------------------
DROP TABLE IF EXISTS `meta_language`;
CREATE TABLE `meta_language`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '语言类型',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '语言描述',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '语言名字',
  `compile_command` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '编译指令',
  `template` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '模板',
  `code_template` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '语言默认代码模板',
  `spj_enable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可作为特殊判题的一种语言',
  `oj` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '该语言属于哪个oj，自身oj用ME',
  `seq` int NOT NULL DEFAULT 0 COMMENT '语言排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 291 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '语言' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of meta_language
-- ----------------------------
INSERT INTO `meta_language` VALUES (1, 'text/x-csrc', 'GCC 9.4.0', 'C', '/usr/bin/gcc -DONLINE_JUDGE -w -fmax-errors=3 -std=c11 {src_path} -lm -o {exe_path}', '#include <stdio.h>\r\nint main() {\r\n    int a,b;\r\n    scanf(\"%d %d\",&a,&b);\r\n    printf(\"%d\",a+b);\r\n    return 0;\r\n}', '//PREPEND BEGIN\r\n#include <stdio.h>\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  printf(\"%d\", add(1, 2));\r\n  return 0;\r\n}\r\n//APPEND END', 1, 'GlowOJ', 0, '2020-12-12 23:11:44', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (2, 'text/x-csrc', 'GCC 9.4.0', 'C With O2', '/usr/bin/gcc -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c11 {src_path} -lm -o {exe_path}', '#include <stdio.h>\r\nint main() {\r\n    int a,b;\r\n    scanf(\"%d %d\",&a,&b);\r\n    printf(\"%d\",a+b);\r\n    return 0;\r\n}', '//PREPEND BEGIN\r\n#include <stdio.h>\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  printf(\"%d\", add(1, 2));\r\n  return 0;\r\n}\r\n//APPEND END', 0, 'GlowOJ', 0, '2021-06-14 21:05:57', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (3, 'text/x-c++src', 'G++ 9.4.0', 'C++', '/usr/bin/g++ -DONLINE_JUDGE -w -fmax-errors=3 -std=c++14 {src_path} -lm -o {exe_path}', '#include<iostream>\r\nusing namespace std;\r\nint main()\r\n{\r\n    int a,b;\r\n    cin >> a >> b;\r\n    cout << a + b;\r\n    return 0;\r\n}', '//PREPEND BEGIN\r\n#include <iostream>\r\nusing namespace std;\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  cout << add(1, 2);\r\n  return 0;\r\n}\r\n//APPEND END', 1, 'GlowOJ', 0, '2020-12-12 23:12:44', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (4, 'text/x-c++src', 'G++ 9.4.0', 'C++ With O2', '/usr/bin/g++ -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c++14 {src_path} -lm -o {exe_path}', '#include<iostream>\r\nusing namespace std;\r\nint main()\r\n{\r\n    int a,b;\r\n    cin >> a >> b;\r\n    cout << a + b;\r\n    return 0;\r\n}', '//PREPEND BEGIN\r\n#include <iostream>\r\nusing namespace std;\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  cout << add(1, 2);\r\n  return 0;\r\n}\r\n//APPEND END', 0, 'GlowOJ', 0, '2021-06-14 21:09:35', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (5, 'text/x-c++src', 'G++ 9.4.0', 'C++ 17', '/usr/bin/g++ -DONLINE_JUDGE -w -fmax-errors=3 -std=c++17 {src_path} -lm -o {exe_path}', '#include<iostream>\r\nusing namespace std;\r\nint main()\r\n{\r\n    int a,b;\r\n    cin >> a >> b;\r\n    cout << a + b;\r\n    return 0;\r\n}', '//PREPEND BEGIN\r\n#include <iostream>\r\nusing namespace std;\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  cout << add(1, 2);\r\n  return 0;\r\n}\r\n//APPEND END', 0, 'GlowOJ', 0, '2020-12-12 23:12:44', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (6, 'text/x-c++src', 'G++ 9.4.0', 'C++ 17 With O2', '/usr/bin/g++ -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c++17 {src_path} -lm -o {exe_path}', '#include<iostream>\r\nusing namespace std;\r\nint main()\r\n{\r\n    int a,b;\r\n    cin >> a >> b;\r\n    cout << a + b;\r\n    return 0;\r\n}', '//PREPEND BEGIN\r\n#include <iostream>\r\nusing namespace std;\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  cout << add(1, 2);\r\n  return 0;\r\n}\r\n//APPEND END', 0, 'GlowOJ', 0, '2021-06-14 21:09:35', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (7, 'text/x-c++src', 'G++ 9.4.0', 'C++ 20', '/usr/bin/g++ -DONLINE_JUDGE -w -fmax-errors=3 -std=c++20 {src_path} -lm -o {exe_path}', '#include<iostream>\r\nusing namespace std;\r\nint main()\r\n{\r\n    int a,b;\r\n    cin >> a >> b;\r\n    cout << a + b;\r\n    return 0;\r\n}', '//PREPEND BEGIN\r\n#include <iostream>\r\nusing namespace std;\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  cout << add(1, 2);\r\n  return 0;\r\n}\r\n//APPEND END', 0, 'GlowOJ', 0, '2020-12-12 23:12:44', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (8, 'text/x-c++src', 'G++ 9.4.0', 'C++ 20 With O2', '/usr/bin/g++ -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c++20 {src_path} -lm -o {exe_path}', '#include<iostream>\r\nusing namespace std;\r\nint main()\r\n{\r\n    int a,b;\r\n    cin >> a >> b;\r\n    cout << a + b;\r\n    return 0;\r\n}', '//PREPEND BEGIN\r\n#include <iostream>\r\nusing namespace std;\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  cout << add(1, 2);\r\n  return 0;\r\n}\r\n//APPEND END', 0, 'GlowOJ', 0, '2021-06-14 21:09:35', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (9, 'text/x-java', 'OpenJDK 1.8', 'Java', '/usr/bin/javac {src_path} -d {exe_dir} -encoding UTF8', 'import java.util.Scanner;\r\npublic class Main{\r\n    public static void main(String[] args){\r\n        Scanner in=new Scanner(System.in);\r\n        int a=in.nextInt();\r\n        int b=in.nextInt();\r\n        System.out.println((a+b));\r\n    }\r\n}', '//PREPEND BEGIN\r\nimport java.util.Scanner;\r\n//PREPEND END\r\n\r\npublic class Main{\r\n    //TEMPLATE BEGIN\r\n    public static Integer add(int a,int b){\r\n        return _______;\r\n    }\r\n    //TEMPLATE END\r\n\r\n    //APPEND BEGIN\r\n    public static void main(String[] args){\r\n        System.out.println(add(a,b));\r\n    }\r\n    //APPEND END\r\n}\r\n', 0, 'GlowOJ', 0, '2020-12-12 23:12:51', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (10, 'text/x-python', 'Python 3.7.5', 'Python3', '/usr/bin/python3 -m py_compile {src_path}', 'a, b = map(int, input().split())\r\nprint(a + b)', '//PREPEND BEGIN\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\ndef add(a, b):\r\n    return a + b\r\n//TEMPLATE END\r\n\r\n\r\nif __name__ == \'__main__\':\r\n    //APPEND BEGIN\r\n    a, b = 1, 1\r\n    print(add(a, b))\r\n    //APPEND END', 0, 'GlowOJ', 0, '2020-12-12 23:14:23', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (11, 'text/x-python', 'Python 2.7.17', 'Python2', '/usr/bin/python -m py_compile {src_path}', 'a, b = map(int, raw_input().split())\r\nprint a+b', '//PREPEND BEGIN\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\ndef add(a, b):\r\n    return a + b\r\n//TEMPLATE END\r\n\r\n\r\nif __name__ == \'__main__\':\r\n    //APPEND BEGIN\r\n    a, b = 1, 1\r\n    print add(a, b)\r\n    //APPEND END', 0, 'GlowOJ', 0, '2021-01-26 11:11:44', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (12, 'text/x-go', 'Golang 1.19', 'Golang', '/usr/bin/go build -o {exe_path} {src_path}', 'package main\r\nimport \"fmt\"\r\n\r\nfunc main(){\r\n    var x int\r\n    var y int\r\n    fmt.Scanln(&x,&y)\r\n    fmt.Printf(\"%d\",x+y)\r\n}\r\n', '\r\npackage main\r\n\r\n//PREPEND BEGIN\r\nimport \"fmt\"\r\n//PREPEND END\r\n\r\n\r\n//TEMPLATE BEGIN\r\nfunc add(a,b int)int{\r\n    return ______\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nfunc main(){\r\n    var x int\r\n    var y int\r\n    fmt.Printf(\"%d\",add(x,y))\r\n}\r\n//APPEND END\r\n', 0, 'GlowOJ', 0, '2021-03-28 23:15:54', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (13, 'text/x-csharp', 'C# Mono 4.6.2', 'C#', '/usr/bin/mcs -optimize+ -out:{exe_path} {src_path}', 'using System;\r\nusing System.Linq;\r\n\r\nclass Program {\r\n    public static void Main(string[] args) {\r\n        Console.WriteLine(Console.ReadLine().Split().Select(int.Parse).Sum());\r\n    }\r\n}', '//PREPEND BEGIN\r\nusing System;\r\nusing System.Collections.Generic;\r\nusing System.Text;\r\n//PREPEND END\r\n\r\nclass Solution\r\n{\r\n    //TEMPLATE BEGIN\r\n    static int add(int a,int b){\r\n        return _______;\r\n    }\r\n    //TEMPLATE END\r\n\r\n    //APPEND BEGIN\r\n    static void Main(string[] args)\r\n    {\r\n        int a ;\r\n        int b ;\r\n        Console.WriteLine(add(a,b));\r\n    }\r\n    //APPEND END\r\n}', 0, 'GlowOJ', 0, '2021-04-13 16:10:03', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (14, 'text/x-php', 'PHP 7.2.24', 'PHP', '/usr/bin/php {src_path}', '<?=array_sum(fscanf(STDIN, \"%d %d\"));', NULL, 0, 'GlowOJ', 0, '2022-02-25 20:55:30', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (15, 'text/x-python', 'PyPy 2.7.18 (7.3.8)', 'PyPy2', '/usr/bin/pypy -m py_compile {src_path}', 'print sum(int(x) for x in raw_input().split(\' \'))', '//PREPEND BEGIN\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\ndef add(a, b):\r\n    return a + b\r\n//TEMPLATE END\r\n\r\n\r\nif __name__ == \'__main__\':\r\n    //APPEND BEGIN\r\n    a, b = 1, 1\r\n    print add(a, b)\r\n    //APPEND END', 0, 'GlowOJ', 0, '2022-02-25 20:55:30', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (16, 'text/x-python', 'PyPy 3.9.17 (7.3.12)', 'PyPy3', '/usr/bin/pypy3 -m py_compile {src_path}', 'print(sum(int(x) for x in input().split(\' \')))', '//PREPEND BEGIN\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\ndef add(a, b):\r\n    return a + b\r\n//TEMPLATE END\r\n\r\n\r\nif __name__ == \'__main__\':\r\n    //APPEND BEGIN\r\n    a, b = 1, 1\r\n    print(add(a, b))\r\n    //APPEND END', 0, 'GlowOJ', 0, '2022-02-25 20:55:30', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (17, 'text/javascript', 'Node.js 14.19.0', 'JavaScript Node', '/usr/bin/node {src_path}', 'var readline = require(\'readline\');\r\nconst rl = readline.createInterface({\r\n        input: process.stdin,\r\n        output: process.stdout\r\n});\r\nrl.on(\'line\', function(line){\r\n   var tokens = line.split(\' \');\r\n    console.log(parseInt(tokens[0]) + parseInt(tokens[1]));\r\n});', NULL, 0, 'GlowOJ', 0, '2022-02-25 20:55:30', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (18, 'text/javascript', 'JavaScript V8 8.4.109', 'JavaScript V8', '/usr/bin/jsv8/d8 {src_path}', 'const [a, b] = readline().split(\' \').map(n => parseInt(n, 10));\r\nprint((a + b).toString());', NULL, 0, 'GlowOJ', 0, '2022-02-25 20:55:30', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (19, 'text/x-csrc', 'GCC', 'GCC', NULL, NULL, NULL, 0, 'HDU', 0, '2021-02-18 21:32:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (20, 'text/x-c++src', 'G++', 'G++', NULL, NULL, NULL, 0, 'HDU', 0, '2021-02-18 21:32:58', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (21, 'text/x-c++src', 'C++', 'C++', NULL, NULL, NULL, 0, 'HDU', 0, '2021-02-18 21:33:11', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (22, 'text/x-csrc', 'C', 'C', NULL, NULL, NULL, 0, 'HDU', 0, '2021-02-18 21:33:41', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (23, 'text/x-pascal', 'Pascal', 'Pascal', NULL, NULL, NULL, 0, 'HDU', 0, '2021-02-18 21:34:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (24, 'text/x-java', 'Java', 'Java', NULL, NULL, NULL, 0, 'HDU', 0, '2022-09-20 21:25:00', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (25, 'text/x-csharp', 'C#', 'C#', NULL, NULL, NULL, 0, 'HDU', 0, '2022-09-20 21:25:00', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (26, 'text/x-csrc', 'GNU GCC C11 5.1.0', 'GNU GCC C11 5.1.0', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (27, 'text/x-c++src', 'Clang++17 Diagnostics', 'Clang++17 Diagnostics', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (28, 'text/x-c++src', 'GNU G++14 6.4.0', 'GNU G++14 6.4.0', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (29, 'text/x-c++src', 'GNU G++17 7.3.0', 'GNU G++17 7.3.0', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (30, 'text/x-c++src', 'GNU G++20 11.2.0 (64 bit, winlibs)', 'GNU G++20 11.2.0 (64 bit, winlibs)', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (31, 'text/x-c++src', 'Microsoft Visual C++ 2017', 'Microsoft Visual C++ 2017', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (32, 'text/x-csharp', 'C# Mono 6.8', 'C# Mono 6.8', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (33, 'text/x-d', 'D DMD32 v2.091.0', 'D DMD32 v2.091.0', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (34, 'text/x-go', 'Go 1.15.6', 'Go 1.15.6', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (35, 'text/x-haskell', 'Haskell GHC 8.10.1', 'Haskell GHC 8.10.1', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (36, 'text/x-java', 'Java 1.8.0_241', 'Java 1.8.0_241', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (37, 'text/x-java', 'Kotlin 1.4.0', 'Kotlin 1.4.0', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (38, 'text/x-ocaml', 'OCaml 4.02.1', 'OCaml 4.02.1', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (39, 'text/x-pascal', 'Delphi 7', 'Delphi 7', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (40, 'text/x-pascal', 'Free Pascal 3.0.2', 'Free Pascal 3.0.2', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (41, 'text/x-pascal', 'PascalABC.NET 3.4.2', 'PascalABC.NET 3.4.2', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (42, 'text/x-perl', 'Perl 5.20.1', 'Perl 5.20.1', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (43, 'text/x-php', 'PHP 7.2.13', 'PHP 7.2.13', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (44, 'text/x-python', 'Python 2.7.18', 'Python 2.7.18', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (45, 'text/x-python', 'Python 3.9.1', 'Python 3.9.1', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (46, 'text/x-python', 'PyPy 2.7 (7.3.0)', 'PyPy 2.7 (7.3.0)', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (47, 'text/x-python', 'PyPy 3.7 (7.3.0)', 'PyPy 3.7 (7.3.0)', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (48, 'text/x-ruby', 'Ruby 3.0.0', 'Ruby 3.0.0', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (49, 'text/x-rustsrc', 'Rust 1.49.0', 'Rust 1.49.0', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (50, 'text/x-scala', 'Scala 2.12.8', 'Scala 2.12.8', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (51, 'text/javascript', 'JavaScript V8 4.8.0', 'JavaScript V8 4.8.0', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (52, 'text/javascript', 'Node.js 12.6.3', 'Node.js 12.6.3', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-03 19:46:24', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (53, 'text/x-csharp', 'C# 8, .NET Core 3.1', 'C# 8, .NET Core 3.1', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-25 21:17:39', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (54, 'text/x-java', 'Java 11.0.6', 'Java 11.0.6', NULL, NULL, NULL, 0, 'CF', 0, '2021-03-25 21:20:03', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (55, 'text/x-c++src', 'G++', 'G++', NULL, NULL, NULL, 0, 'POJ', 0, '2021-06-24 22:50:50', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (56, 'text/x-csrc', 'GCC', 'GCC', NULL, NULL, NULL, 0, 'POJ', 0, '2021-06-24 22:51:04', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (57, 'text/x-java', 'Java', 'Java', NULL, NULL, NULL, 0, 'POJ', 0, '2021-06-24 22:51:29', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (58, 'text/x-pascal', 'Pascal', 'Pascal', NULL, NULL, NULL, 0, 'POJ', 0, '2021-06-24 22:51:50', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (59, 'text/x-c++src', 'C++', 'C++', NULL, NULL, NULL, 0, 'POJ', 0, '2021-06-24 22:52:15', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (60, 'text/x-csrc', 'C', 'C', NULL, NULL, NULL, 0, 'POJ', 0, '2021-06-24 22:52:38', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (61, 'text/x-fortran', 'Fortran', 'Fortran', NULL, NULL, NULL, 0, 'POJ', 0, '2021-06-24 22:55:15', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (62, 'text/x-c++src', 'C++14 (gcc 8.3)', 'C++14 (gcc 8.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (63, 'text/x-csrc', 'Assembler 32 (gcc 8.3)', 'Assembler 32 (gcc 8.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (64, NULL, 'Sed (sed 4.7)', 'Sed (sed 4.7)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (65, NULL, 'Kotlin (kotlin 1.3.21)', 'Kotlin (kotlin 1.3.21)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (66, NULL, 'Dart (dart 2.3.0)', 'Dart (dart 2.3.0)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (67, 'text/x-csrc', 'BC (bc 1.07.1)', 'BC (bc 1.07.1)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (68, 'text/x-csrc', 'Clojure (clojure 1.10.0)', 'Clojure (clojure 1.10.0)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (69, 'text/x-csrc', 'JavaScript (SMonkey 60.2.3)', 'JavaScript (SMonkey 60.2.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (70, 'text/x-go', 'Go (go 1.12.1)', 'Go (go 1.12.1)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (71, NULL, 'Unlambda (unlambda 0.1.4.2)', 'Unlambda (unlambda 0.1.4.2)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (72, 'text/x-python', 'Python 3 (python  3.7.3)', 'Python 3 (python  3.7.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (73, NULL, 'R (R 3.5.2)', 'R (R 3.5.2)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (74, 'text/x-csrc', 'Cobol (gnucobol 2.2.0)', 'Cobol (gnucobol 2.2.0)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (75, 'text/x-csrc', 'CoffeeScript (coffee 2.4.1)', 'CoffeeScript (coffee 2.4.1)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (76, NULL, 'Fantom (fantom 1.0.72)', 'Fantom (fantom 1.0.72)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (77, 'text/x-rustsrc', 'Rust (rust 1.33.0)', 'Rust (rust 1.33.0)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (78, 'text/x-csrc', 'Pico Lisp (pico 18.12.27)', 'Pico Lisp (pico 18.12.27)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (79, NULL, 'VB.net (mono 4.7)', 'VB.net (mono 4.7)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (80, 'text/x-csrc', 'Racket (racket 7.0)', 'Racket (racket 7.0)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (81, NULL, 'Elixir (elixir 1.8.2)', 'Elixir (elixir 1.8.2)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (82, 'text/x-csrc', 'Scheme (chicken 4.13)', 'Scheme (chicken 4.13)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (83, 'text/x-go', 'Gosu (gosu 1.14.9)', 'Gosu (gosu 1.14.9)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (84, 'text/x-java', 'Java (HotSpot 12)', 'Java (HotSpot 12)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (85, 'text/x-perl', 'Perl (perl 2018.12)', 'Perl (perl 2018.12)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (86, 'text/x-csrc', 'C (gcc 8.3)', 'C (gcc 8.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (87, 'text/x-python', 'Python (PyPy 2.7.13)', 'Python (PyPy 2.7.13)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (88, NULL, 'Brainf**k (bff 1.0.6)', 'Brainf**k (bff 1.0.6)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (89, 'text/javascript', 'Node.js (node 11.12.0)', 'Node.js (node 11.12.0)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (90, NULL, 'Assembler 32 (nasm 2.14)', 'Assembler 32 (nasm 2.14)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (91, 'text/x-csrc', 'Clips (clips 6.30)', 'Clips (clips 6.30)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (92, NULL, 'Prolog (swi 7.6.4)', 'Prolog (swi 7.6.4)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (93, 'text/x-csrc', 'Icon (iconc 9.5.1)', 'Icon (iconc 9.5.1)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (94, 'text/x-ruby', 'Ruby (ruby 2.5.5)', 'Ruby (ruby 2.5.5)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (95, 'text/x-csrc', 'Scheme (stalin 0.11)', 'Scheme (stalin 0.11)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (96, NULL, 'Pike (pike 8.0)', 'Pike (pike 8.0)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (97, NULL, 'Groovy (groovy 2.5.6)', 'Groovy (groovy 2.5.6)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (98, 'text/x-c++src', 'C++ (gcc 8.3)', 'C++ (gcc 8.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (99, NULL, 'Nim (nim 0.19.4)', 'Nim (nim 0.19.4)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (100, 'text/x-csrc', 'Pascal (gpc 20070904)', 'Pascal (gpc 20070904)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (101, NULL, 'F# (mono 4.1)', 'F# (mono 4.1)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (102, 'text/x-perl', 'Perl (perl 5.28.1)', 'Perl (perl 5.28.1)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (103, 'text/x-csrc', 'Python (cpython 2.7.16)', 'Python (cpython 2.7.16)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (104, 'text/x-fortran', 'Fortran (gfortran 8.3)', 'Fortran (gfortran 8.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (105, 'text/x-csrc', 'Python 3 nbc (python 3.7.3)', 'Python 3 nbc (python 3.7.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (106, 'text/x-csrc', 'Octave (octave 4.4.1)', 'Octave (octave 4.4.1)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (107, 'text/x-csrc', 'Whitespace (wspace 0.3)', 'Whitespace (wspace 0.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (108, NULL, 'Ada95 (gnat 8.3)', 'Ada95 (gnat 8.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (109, 'text/x-csrc', 'Ocaml (ocamlopt 4.05.0)', 'Ocaml (ocamlopt 4.05.0)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (110, 'text/x-csrc', 'Intercal (ick 0.3)', 'Intercal (ick 0.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (111, NULL, 'Text (plain text)', 'Text (plain text)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (112, 'text/x-csrc', 'D (gdc 8.3)', 'D (gdc 8.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (113, 'text/x-csrc', 'Haskell (ghc 8.4.4)', 'Haskell (ghc 8.4.4)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (114, 'text/x-csrc', 'Pascal (fpc 3.0.4)', 'Pascal (fpc 3.0.4)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (115, NULL, 'Smalltalk (gst 3.2.5)', 'Smalltalk (gst 3.2.5)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (116, 'text/x-java', 'JAR (JavaSE 6)', 'JAR (JavaSE 6)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (117, 'text/x-csrc', 'Nice (nicec 0.9.13)', 'Nice (nicec 0.9.13)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (118, 'text/x-csrc', 'Lua (luac 5.3.3)', 'Lua (luac 5.3.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (119, 'text/x-csharp', 'C# (gmcs 5.20.1)', 'C# (gmcs 5.20.1)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (120, NULL, 'Bash (bash 5.0.3)', 'Bash (bash 5.0.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:27', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (121, 'text/x-php', 'PHP (php 7.3.5)', 'PHP (php 7.3.5)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (122, 'text/x-csrc', 'Nemerle (ncc 1.2.547)', 'Nemerle (ncc 1.2.547)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (123, 'text/x-csrc', 'Common Lisp (sbcl 1.4.16)', 'Common Lisp (sbcl 1.4.16)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (124, 'text/x-csrc', 'Common Lisp (clisp 2.49.92)', 'Common Lisp (clisp 2.49.92)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (125, 'text/x-csrc', 'Scheme (guile 2.2.4)', 'Scheme (guile 2.2.4)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (126, 'text/x-csrc', 'C99 (gcc 8.3)', 'C99 (gcc 8.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (127, 'text/x-csrc', 'JavaScript (rhino 1.7.9)', 'JavaScript (rhino 1.7.9)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (128, NULL, 'Erlang (erl 21.3.8)', 'Erlang (erl 21.3.8)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (129, 'text/x-csrc', 'TCL (tcl 8.6)', 'TCL (tcl 8.6)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (130, 'text/x-csrc', 'Scala (scala 2.12.8)', 'Scala (scala 2.12.8)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (131, NULL, 'D (dmd 2.085.0)', 'D (dmd 2.085.0)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (132, NULL, 'AWK (gawk 4.2.1)', 'AWK (gawk 4.2.1)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (133, NULL, 'AWK (mawk 1.3.3)', 'AWK (mawk 1.3.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (134, NULL, 'Forth (gforth 0.7.3)', 'Forth (gforth 0.7.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (135, 'text/x-csrc', 'C (clang 8.0)', 'C (clang 8.0)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (136, NULL, 'Prolog (gprolog 1.4.5)', 'Prolog (gprolog 1.4.5)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (137, 'text/x-c++src', 'C++14 (clang 8.0)', 'C++14 (clang 8.0)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (138, 'text/x-csrc', 'Objective-C (clang 8.0)', 'Objective-C (clang 8.0)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (139, 'text/x-csrc', 'D (ldc 1.12.0)', 'D (ldc 1.12.0)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (140, NULL, 'SQLite (sqlite 3.27.2)', 'SQLite (sqlite 3.27.2)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (141, 'text/x-c++src', 'C++ (g++ 4.3.2)', 'C++ (g++ 4.3.2)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (142, NULL, 'Swift (swift 4.2.2)', 'Swift (swift 4.2.2)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (143, NULL, 'Assembler 64 (nasm 2.14)', 'Assembler 64 (nasm 2.14)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (144, 'text/x-csrc', 'Objective-C (gcc 8.3)', 'Objective-C (gcc 8.3)', NULL, NULL, NULL, 0, 'SPOJ', 0, '2025-01-15 14:57:28', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (145, 'text/plain', 'jq (jq 1.6)', 'jq (jq 1.6)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (146, 'text/x-lisp', 'Emacs Lisp (No Compile) (GNU Emacs 28.2)', 'Emacs Lisp (No Compile) (GNU Emacs 28.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (147, 'text/x-c++src', 'C++ 17 (gcc 12.2)', 'C++ 17 (gcc 12.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (148, 'text/x-ocaml', 'OCaml (ocamlopt 5.0.0)', 'OCaml (ocamlopt 5.0.0)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (149, 'text/x-d', 'D (DMD 2.104.0)', 'D (DMD 2.104.0)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (150, 'text/plain', 'Factor (Factor 0.98)', 'Factor (Factor 0.98)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (151, 'text/x-d', 'D (GDC 12.2)', 'D (GDC 12.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (152, 'text/plain', 'Unison (Unison M5b)', 'Unison (Unison M5b)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (153, 'text/x-sh', 'Bash (bash 5.2.2)', 'Bash (bash 5.2.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (154, 'text/plain', 'dc (dc 1.07.1)', 'dc (dc 1.07.1)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (155, 'text/x-ruby', 'Ruby (ruby 3.2.2)', 'Ruby (ruby 3.2.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (156, 'text/x-lua', 'Lua (LuaJIT 2.1.0-beta3)', 'Lua (LuaJIT 2.1.0-beta3)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (157, 'text/x-fsharp', 'F# 7.0 (.NET 7.0.7)', 'F# 7.0 (.NET 7.0.7)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (158, 'text/x-clojure', 'Clojure (babashka 1.3.181)', 'Clojure (babashka 1.3.181)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (159, 'text/x-python', 'Python (CPython 3.11.4)', 'Python (CPython 3.11.4)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (160, 'text/plain', 'ECLiPSe (ECLiPSe 7.1_13)', 'ECLiPSe (ECLiPSe 7.1_13)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (161, 'text/javascript', 'JavaScript (Deno 1.35.1)', 'JavaScript (Deno 1.35.1)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (162, 'text/x-php', 'PHP (php 8.2.8)', 'PHP (php 8.2.8)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (163, 'text/plain', 'プロデル (mono版プロデル 1.9.1182)', 'プロデル (mono版プロデル 1.9.1182)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (164, 'text/x-fortran', 'Fortran (gfortran 12.2)', 'Fortran (gfortran 12.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (165, 'text/x-rustsrc', 'Rust (rustc 1.70.0)', 'Rust (rustc 1.70.0)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (166, 'text/x-clojure', 'Carp (Carp 0.5.5)', 'Carp (Carp 0.5.5)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (167, 'text/x-python', 'Python (Cython 0.29.34)', 'Python (Cython 0.29.34)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (168, 'text/x-sh', 'Sed (GNU sed 4.8)', 'Sed (GNU sed 4.8)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (169, 'text/x-python', 'Python (PyPy 3.10-v7.3.12)', 'Python (PyPy 3.10-v7.3.12)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (170, 'text/x-ada', 'Ada (GNAT 12.2)', 'Ada (GNAT 12.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (171, 'text/x-lisp', 'Emacs Lisp (Native Compile) (GNU Emacs 28.2)', 'Emacs Lisp (Native Compile) (GNU Emacs 28.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (172, 'text/javascript', 'JavaScript (Node.js 18.16.1)', 'JavaScript (Node.js 18.16.1)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (173, 'text/plain', 'Vim (vim 9.0.0242)', 'Vim (vim 9.0.0242)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (174, 'text/x-java', 'Java (OpenJDK 17)', 'Java (OpenJDK 17)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (175, 'text/x-cobol', 'COBOL (GnuCOBOL(Fixed) 3.1.2)', 'COBOL (GnuCOBOL(Fixed) 3.1.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (176, 'text/plain', 'Zig (Zig 0.10.1)', 'Zig (Zig 0.10.1)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (177, 'text/x-scheme', 'Scheme (Gauche 0.9.12)', 'Scheme (Gauche 0.9.12)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (178, 'text/x-clojure', 'Clojure (clojure 1.11.1)', 'Clojure (clojure 1.11.1)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (179, 'text/golang', 'Go (go 1.20.6)', 'Go (go 1.20.6)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (180, 'text/x-lua', 'Lua (Lua 5.4.6)', 'Lua (Lua 5.4.6)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (181, 'text/x-cobol', 'COBOL (Free) (GnuCOBOL 3.1.2)', 'COBOL (Free) (GnuCOBOL 3.1.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (182, 'text/typescript', 'TypeScript 5.1 (Node.js 18.16.1)', 'TypeScript 5.1 (Node.js 18.16.1)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (183, 'text/x-sh', 'Zsh (Zsh 5.9)', 'Zsh (Zsh 5.9)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (184, 'text/x-erlang', 'Erlang (Erlang 26.0.2)', 'Erlang (Erlang 26.0.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (185, 'text/x-c++src', 'C++ 20 (gcc 12.2)', 'C++ 20 (gcc 12.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (186, 'text/x-pascal', 'Pascal (fpc 3.2.2)', 'Pascal (fpc 3.2.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (187, 'text/x-sh', 'AWK (GNU Awk 5.0.1)', 'AWK (GNU Awk 5.0.1)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (188, 'text/plain', 'Forth (gforth 0.7.3)', 'Forth (gforth 0.7.3)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (189, 'text/plain', 'bc (bc 1.07.1)', 'bc (bc 1.07.1)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (190, 'text/x-perl', 'Perl (perl  5.34)', 'Perl (perl  5.34)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (191, 'text/plain', 'Koka (koka 2.4.0)', 'Koka (koka 2.4.0)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (192, 'text/plain', 'Mercury (Mercury 22.01.6)', 'Mercury (Mercury 22.01.6)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (193, 'text/x-csharp', 'C# 11.0 (.NET 7.0.7)', 'C# 11.0 (.NET 7.0.7)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (194, 'text/x-python', 'SageMath (SageMath 9.5)', 'SageMath (SageMath 9.5)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (195, 'text/x-kotlin', 'Kotlin (Kotlin/JVM 1.8.20)', 'Kotlin (Kotlin/JVM 1.8.20)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (196, 'text/x-c++src', 'C++ 20 (Clang 16.0.6)', 'C++ 20 (Clang 16.0.6)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (197, 'text/x-lisp', 'Emacs Lisp (Byte Compile) (GNU Emacs 28.2)', 'Emacs Lisp (Byte Compile) (GNU Emacs 28.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (198, 'text/x-haxe', 'Haxe (JVM) (Haxe 4.3.1)', 'Haxe (JVM) (Haxe 4.3.1)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (199, 'text/x-ocaml', 'ReasonML (reason 3.9.0)', 'ReasonML (reason 3.9.0)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (200, 'text/x-sh', 'PowerShell (PowerShell 7.3.1)', 'PowerShell (PowerShell 7.3.1)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (201, 'text/x-swift', 'Swift (swift 5.8.1)', 'Swift (swift 5.8.1)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (202, 'text/x-vbscript', 'Visual Basic 16.9 (.NET 7.0.7)', 'Visual Basic 16.9 (.NET 7.0.7)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (203, 'text/x-haskell', 'Haskell (GHC 9.4.5)', 'Haskell (GHC 9.4.5)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (204, 'text/plain', 'Assembly x64 (NASM 2.15.05)', 'Assembly x64 (NASM 2.15.05)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (205, 'text/x-prolog', 'Prolog (SWI-Prolog 9.0.4)', 'Prolog (SWI-Prolog 9.0.4)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (206, 'text/typescript', 'TypeScript 5.1 (Deno 1.35.1)', 'TypeScript 5.1 (Deno 1.35.1)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (207, 'text/x-csharp', 'C# 11.0 AOT (.NET 7.0.7)', 'C# 11.0 AOT (.NET 7.0.7)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (208, 'text/plain', 'LLVM IR (Clang 16.0.6)', 'LLVM IR (Clang 16.0.6)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (209, 'text/plain', 'なでしこ (cnako3 3.4.20)', 'なでしこ (cnako3 3.4.20)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (210, 'text/x-c++src', 'C++ 23 (Clang 16.0.6)', 'C++ 23 (Clang 16.0.6)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (211, 'text/x-elixir', 'Elixir (Elixir 1.15.2)', 'Elixir (Elixir 1.15.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (212, 'text/x-c++src', 'C (gcc 12.2.0)', 'C (gcc 12.2.0)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (213, 'text/plain', 'Nibbles (literate form) (nibbles 1.01)', 'Nibbles (literate form) (nibbles 1.01)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (214, 'text/x-r', 'R (GNU R 4.2.1)', 'R (GNU R 4.2.1)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (215, 'text/x-scala', 'Scala (Dotty 3.3.0)', 'Scala (Dotty 3.3.0)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (216, 'text/plain', 'V (V 0.4)', 'V (V 0.4)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (217, 'text/plain', 'Cyber (Cyber v0.2-Latest)', 'Cyber (Cyber v0.2-Latest)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (218, 'text/plain', 'Text (cat 8.32)', 'Text (cat 8.32)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (219, 'text/plain', 'Seed7 (Seed7 3.2.1)', 'Seed7 (Seed7 3.2.1)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (220, 'text/x-d', 'D (LDC 1.32.2)', 'D (LDC 1.32.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (221, 'text/x-python', 'Python (Mambaforge / CPython 3.10.10)', 'Python (Mambaforge / CPython 3.10.10)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (222, 'text/x-scala', 'Scala 3.3.0 (Scala Native 0.4.14)', 'Scala 3.3.0 (Scala Native 0.4.14)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (223, 'text/x-crystal', 'Crystal (Crystal 1.9.1)', 'Crystal (Crystal 1.9.1)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (224, 'text/x-julia', 'Julia (Julia 1.9.2)', 'Julia (Julia 1.9.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (225, 'text/x-raku', 'Raku (Rakudo 2023.06)', 'Raku (Rakudo 2023.06)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (226, 'text/x-c++src', 'C++ 17 (Clang 16.0.6)', 'C++ 17 (Clang 16.0.6)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (227, 'application/dart', 'Dart (Dart 3.0.5)', 'Dart (Dart 3.0.5)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (228, 'text/x-c++src', 'C++ 23 (gcc 12.2)', 'C++ 23 (gcc 12.2)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (229, 'text/plain', 'Whitespace (whitespacers 1.0.0)', 'Whitespace (whitespacers 1.0.0)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (230, 'text/x-matlab', 'Octave (GNU Octave 8.2.0)', 'Octave (GNU Octave 8.2.0)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (231, 'text/plain', 'Brainfuck (bf 20041219)', 'Brainfuck (bf 20041219)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (232, 'text/x-lisp', 'Common Lisp (SBCL 2.3.6)', 'Common Lisp (SBCL 2.3.6)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (233, 'text/plain', '><> (fishr 0.1.0)', '><> (fishr 0.1.0)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (234, 'text/x-nim', 'Nim (Nim 1.6.14)', 'Nim (Nim 1.6.14)', NULL, NULL, NULL, 0, 'AC', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (235, 'text/x-ruby', 'Ruby 2.5.1', 'Ruby', '/usr/bin/ruby {src_path}', 'a, b = gets.split.map(&:to_i)\r\nputs(a + b)', NULL, 0, 'ME', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (236, 'text/x-rustsrc', 'Rust 1.65.0', 'Rust', '/usr/bin/rustc -O -o {exe_path} {src_path}', 'use std::io;\r\n\r\nfn main() {\r\n    let mut line = String::new();\r\n    io::stdin().read_line(&mut line).expect(\"stdin\");\r\n\r\n    let sum: i32 = line.split_whitespace()\r\n                       .map(|x| x.parse::<i32>().expect(\"integer\"))\r\n                       .sum();\r\n    println!(\"{}\", sum);\r\n}', NULL, 0, 'ME', 0, '2025-01-15 14:57:32', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (237, 'text/x-c++src', 'C++ 11 (G++)', 'C++ 11 (G++)', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (238, 'text/x-c++src', 'C++ 17 (G++)', 'C++ 17 (G++)', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (239, 'text/x-c++src', 'C++ 11 (Clang++) ', 'C++ 11 (Clang++) ', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (240, 'text/x-c++src', 'C++ 17 (Clang++)', 'C++ 17 (Clang++)', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (241, 'text/x-c++src', 'C++ 11 O2(G++)', 'C++ 11 O2(G++)', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (242, 'text/x-c++src', 'C++ 17 O2(G++)', 'C++ 17 O2(G++)', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (243, 'text/x-c++src', 'C++ 11 O2(Clang++) ', 'C++ 11 O2(Clang++)', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (244, 'text/x-c++src', 'C++ 17 O2(Clang++)', 'C++ 17 O2(Clang++)', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (245, 'text/x-csrc', 'C 11 (GCC)', 'C 11 (GCC)', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (246, 'text/x-csrc', 'C 17 (GCC)', 'C 17 (GCC)', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (247, 'text/x-csrc', 'C 11 (Clang)', 'C 11 (Clang)', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (248, 'text/x-csrc', 'C 17 (Clang)', 'C 17 (Clang)', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (249, 'text/x-java', 'Java', 'Java', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (250, 'text/x-java', 'Kotlin 1.8 (JVM)', 'Kotlin 1.8 (JVM)', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (251, 'text/x-pascal', 'Pascal', 'Pascal', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (252, 'text/x-python', 'Python 3.10', 'Python 3.10', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (253, 'text/x-python', 'Python 3.9', 'Python 3.9', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (254, 'text/x-python', 'Python 2.7', 'Python 2.7', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (255, 'text/x-rustsrc', 'Rust 2021', 'Rust 2021', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (256, 'text/x-rustsrc', 'Rust 2018', 'Rust 2018', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (257, 'text/x-rustsrc', 'Rust 2015', 'Rust 2015', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (258, 'go', 'Go 1.x', 'Go 1.x', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (259, 'text/x-csharp', 'C# 9', 'C# 9', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (260, 'text/x-csharp', 'C# 7.3', 'C# 7.3', NULL, NULL, NULL, 0, 'LIBRE', 0, '2025-01-15 14:57:33', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (261, 'text/x-csrc', 'GNU GCC C11 5.1.0', 'GNU GCC C11 5.1.0', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (262, 'text/x-c++src', 'Clang++17 Diagnostics', 'Clang++17 Diagnostics', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (263, 'text/x-c++src', 'GNU G++11 5.1.0', 'GNU G++11 5.1.0', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (264, 'text/x-c++src', 'GNU G++14 6.4.0', 'GNU G++14 6.4.0', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (265, 'text/x-c++src', 'GNU G++17 7.3.0', 'GNU G++17 7.3.0', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (266, 'text/x-c++src', 'Microsoft Visual C++ 2010', 'Microsoft Visual C++ 2010', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (267, 'text/x-c++src', 'Microsoft Visual C++ 2017', 'Microsoft Visual C++ 2017', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (268, 'text/x-csharp', 'C# Mono 6.8', 'C# Mono 6.8', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (269, 'text/x-d', 'D DMD32 v2.091.0', 'D DMD32 v2.091.0', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (270, 'text/x-go', 'Go 1.15.6', 'Go 1.15.6', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (271, 'text/x-haskell', 'Haskell GHC 8.10.1', 'Haskell GHC 8.10.1', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (272, 'text/x-java', 'Java 1.8.0_241', 'Java 1.8.0_241', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (273, 'text/x-java', 'Kotlin 1.4.0', 'Kotlin 1.4.0', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (274, 'text/x-ocaml', 'OCaml 4.02.1', 'OCaml 4.02.1', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (275, 'text/x-pascal', 'Delphi 7', 'Delphi 7', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (276, 'text/x-pascal', 'Free Pascal 3.0.2', 'Free Pascal 3.0.2', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (277, 'text/x-pascal', 'PascalABC.NET 3.4.2', 'PascalABC.NET 3.4.2', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (278, 'text/x-perl', 'Perl 5.20.1', 'Perl 5.20.1', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (279, 'text/x-php', 'PHP 7.2.13', 'PHP 7.2.13', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (280, 'text/x-python', 'Python 2.7.18', 'Python 2.7.18', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (281, 'text/x-python', 'Python 3.9.1', 'Python 3.9.1', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (282, 'text/x-python', 'PyPy 2.7 (7.3.0)', 'PyPy 2.7 (7.3.0)', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (283, 'text/x-python', 'PyPy 3.7 (7.3.0)', 'PyPy 3.7 (7.3.0)', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (284, 'text/x-ruby', 'Ruby 3.0.0', 'Ruby 3.0.0', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (285, 'text/x-rustsrc', 'Rust 1.49.0', 'Rust 1.49.0', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (286, 'text/x-scala', 'Scala 2.12.8', 'Scala 2.12.8', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (287, 'text/javascript', 'JavaScript V8 4.8.0', 'JavaScript V8 4.8.0', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (288, 'text/javascript', 'Node.js 12.6.3', 'Node.js 12.6.3', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (289, 'text/x-csharp', 'C# 8, .NET Core 3.1', 'C# 8, .NET Core 3.1', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_language` VALUES (290, 'text/x-java', 'Java 11.0.6', 'Java 11.0.6', NULL, NULL, NULL, 0, 'CF', 0, '2025-02-18 21:45:34', '2025-06-07 19:27:52', 'F', NULL, NULL, 'GLOWXQ');

-- ----------------------------
-- Table structure for meta_menu
-- ----------------------------
DROP TABLE IF EXISTS `meta_menu`;
CREATE TABLE `meta_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '底部菜单ID',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '底部菜单名称',
  `active_icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '选中图标',
  `inactive_icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '未选中图标',
  `icon_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '图标类型',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '菜单类型',
  `path` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单路径',
  `enable` tinyint(1) NULL DEFAULT NULL COMMENT '层级',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `has_children` tinyint NOT NULL DEFAULT 0 COMMENT '是否有子级',
  `lock` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否锁定',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除标识',
  `remark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of meta_menu
-- ----------------------------
INSERT INTO `meta_menu` VALUES (1, '首页', 'https://picsum.photos/50/50', 'https://picsum.photos/50/50?grayscale', 'Icon', 'BottomMenu', '/home', 1, 1, 0, 0, 'F', '主菜单', 1001, 1001, NULL, NULL, 'GLOWXQ');
INSERT INTO `meta_menu` VALUES (2, '分类', 'https://picsum.photos/50/50', 'https://picsum.photos/50/50?grayscale', 'Image', 'BottomMenu', '/category', 1, 2, 1, 0, 'F', '分类菜单', 1001, 1001, NULL, NULL, 'GLOWXQ');

-- ----------------------------
-- Table structure for meta_region
-- ----------------------------
DROP TABLE IF EXISTS `meta_region`;
CREATE TABLE `meta_region`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '地址编码',
  `parent_id` bigint UNSIGNED NOT NULL COMMENT '父编码',
  `ancestors` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '祖级列表',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '地址名',
  `pinyin` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '地址拼音',
  `pinyin_prefix` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '拼音前缀',
  `level` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '地址等级',
  `enable` tinyint NOT NULL COMMENT '启用',
  `del_flag` enum('T','F') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'F' COMMENT '逻辑删除',
  `create_id` bigint NOT NULL DEFAULT -1 COMMENT '创建人',
  `update_id` bigint NOT NULL DEFAULT -1 COMMENT '更新人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '区域地址' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of meta_region
-- ----------------------------

-- ----------------------------
-- Table structure for meta_tag
-- ----------------------------
DROP TABLE IF EXISTS `meta_tag`;
CREATE TABLE `meta_tag`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `category_id` bigint NOT NULL DEFAULT -1 COMMENT '分类ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标签名字',
  `background_color` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '#009688' COMMENT '背景',
  `text_color` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '#FFFFFF' COMMENT '字体颜色',
  `plain` tinyint(1) NOT NULL DEFAULT 0 COMMENT '镂空样式',
  `enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '标签' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of meta_tag
-- ----------------------------
INSERT INTO `meta_tag` VALUES (16, -1, 'GESP', '#009688', '#FFFFFF', 0, 1, '2025-04-30 00:10:41', '2025-06-07 19:27:52', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (17, -1, '数组', '#009688', '#FFFFFF', 0, 1, '2025-04-30 01:52:26', '2025-06-07 19:27:52', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (18, -1, '基础算法-回溯深搜', '#009688', '#FFFFFF', 0, 1, '2025-04-30 01:52:38', '2025-06-07 19:27:52', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (19, -1, 'sfsdfsdf', '#009688', '#FFFFFF', 0, 1, '2025-04-30 01:52:38', '2025-06-07 19:27:52', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (20, -1, '入门题-模拟', '#009688', '#FFFFFF', 0, 1, '2025-04-30 02:30:58', '2025-06-07 19:27:52', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (21, -1, '动态规划', '#009688', '#FFFFFF', 0, 1, '2025-05-27 22:31:28', '2025-06-07 19:27:52', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (22, 1, '123', '#009688', '#FFFFFF', 1, 1, '2025-06-10 01:00:11', '2025-06-10 01:00:11', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (23, -1, 'PY语法基础', '#009688', '#FFFFFF', 0, 1, '2025-06-11 23:23:15', '2025-06-11 23:23:15', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (24, -1, '一本通在线评测', '#009688', '#FFFFFF', 0, 1, '2025-06-12 00:12:50', '2025-06-12 00:12:50', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (25, -1, 'GESP真题', '#009688', '#FFFFFF', 0, 1, '2025-06-23 01:25:51', '2025-06-23 01:25:51', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (26, -1, '图论', '#000000', '#ffffff', 0, 1, '2025-06-25 14:24:32', '2025-06-25 14:24:32', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (27, -1, 'CSES', '#000000', '#ffffff', 0, 1, '2025-06-25 14:24:32', '2025-06-25 14:24:32', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (28, -1, '基础语法', '#009688', '#FFFFFF', 0, 1, '2025-06-25 23:20:10', '2025-06-25 23:20:10', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (29, -1, '分支选择结构', '#009688', '#FFFFFF', 0, 1, '2025-06-25 23:20:10', '2025-06-25 23:20:10', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (30, 1, '是大多数', '#009688', '#FFFFFF', 1, 1, '2025-06-30 23:06:27', '2025-06-30 23:06:27', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (31, -1, '一维数组', '#009688', '#FFFFFF', 0, 1, '2025-07-28 23:29:32', '2025-07-28 23:29:32', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (32, -1, '循环结构', '#009688', '#FFFFFF', 0, 1, '2025-07-28 23:29:32', '2025-07-28 23:29:32', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (33, -1, '算法设计', '#009688', '#FFFFFF', 0, 1, '2025-07-28 23:29:34', '2025-07-28 23:29:34', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (34, -1, '一本通启蒙篇', '#009688', '#FFFFFF', 0, 1, '2025-07-28 23:29:34', '2025-07-28 23:29:34', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (35, -1, '数的存储与组织', '#009688', '#FFFFFF', 0, 1, '2025-07-28 23:29:35', '2025-07-28 23:29:35', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (36, -1, '函数', '#009688', '#FFFFFF', 0, 1, '2025-07-28 23:29:40', '2025-07-28 23:29:40', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (37, -1, '2025', '#009688', '#FFFFFF', 0, 1, '2025-07-31 18:41:35', '2025-07-31 18:41:35', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (38, -1, 'CSP-J复赛模拟', '#009688', '#FFFFFF', 0, 1, '2025-07-31 18:41:38', '2025-07-31 18:41:38', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (39, -1, 'CSP-J月赛', '#009688', '#FFFFFF', 0, 1, '2025-07-31 18:41:38', '2025-07-31 18:41:38', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (40, -1, '语法周赛', '#009688', '#FFFFFF', 0, 1, '2025-07-31 18:41:49', '2025-07-31 18:41:49', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (41, -1, 'GESP模拟', '#009688', '#FFFFFF', 0, 1, '2025-07-31 18:41:51', '2025-07-31 18:41:51', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (42, -1, '二级', '#009688', '#FFFFFF', 0, 1, '2025-07-31 18:41:51', '2025-07-31 18:41:51', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (43, -1, '数学', '#009688', '#FFFFFF', 0, 1, '2025-07-31 18:41:56', '2025-07-31 18:41:56', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (44, -1, '高精度', '#009688', '#FFFFFF', 0, 1, '2025-07-31 18:41:56', '2025-07-31 18:41:56', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (45, -1, '算法基础', '#009688', '#FFFFFF', 0, 1, '2025-07-31 18:41:56', '2025-07-31 18:41:56', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (46, -1, '模拟', '#009688', '#FFFFFF', 0, 1, '2025-07-31 18:41:56', '2025-07-31 18:41:56', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (47, -1, '二分', '#009688', '#FFFFFF', 0, 1, '2025-07-31 18:42:04', '2025-07-31 18:42:04', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (48, -1, '四级', '#009688', '#FFFFFF', 0, 1, '2025-07-31 18:42:18', '2025-07-31 18:42:18', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag` VALUES (49, -1, '一级', '#009688', '#FFFFFF', 0, 1, '2025-07-31 18:42:44', '2025-07-31 18:42:44', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for meta_tag_bind
-- ----------------------------
DROP TABLE IF EXISTS `meta_tag_bind`;
CREATE TABLE `meta_tag_bind`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tag_id` bigint UNSIGNED NOT NULL COMMENT '标签ID',
  `business_id` bigint NOT NULL COMMENT '标签绑定的业务ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'problem' COMMENT '绑定类型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `meta_tag_bind_tag_id`(`tag_id` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  CONSTRAINT `meta_tag_bind_tag_id` FOREIGN KEY (`tag_id`) REFERENCES `meta_tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 339 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '绑定标签权限' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of meta_tag_bind
-- ----------------------------
INSERT INTO `meta_tag_bind` VALUES (137, 16, 39, 'User', '2025-06-21 12:25:10', '2025-06-21 12:25:10', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `meta_tag_bind` VALUES (138, 17, 39, 'User', '2025-06-21 12:25:11', '2025-06-21 12:25:11', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `meta_tag_bind` VALUES (142, 18, 45, 'User', '2025-06-21 13:09:44', '2025-06-21 13:09:44', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `meta_tag_bind` VALUES (143, 19, 45, 'User', '2025-06-21 13:09:44', '2025-06-21 13:09:44', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `meta_tag_bind` VALUES (144, 20, 45, 'User', '2025-06-21 13:09:45', '2025-06-21 13:09:45', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `meta_tag_bind` VALUES (288, 22, 11, 'Group', '2025-06-30 23:44:45', '2025-06-30 23:44:45', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag_bind` VALUES (316, 17, 331, 'Problem', '2025-07-31 18:40:58', '2025-07-31 18:40:58', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `meta_tag_bind` VALUES (317, 16, 331, 'Problem', '2025-07-31 18:40:58', '2025-07-31 18:40:58', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for meta_tag_category
-- ----------------------------
DROP TABLE IF EXISTS `meta_tag_category`;
CREATE TABLE `meta_tag_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `enable` tinyint NOT NULL DEFAULT 1 COMMENT '启用',
  `remark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除标识',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '标签分类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of meta_tag_category
-- ----------------------------
INSERT INTO `meta_tag_category` VALUES (1, '分类1', 1, NULL, 'F', 1, 1, '2025-06-10 00:51:28', '2025-06-10 00:51:28', 'GLOWXQ');
INSERT INTO `meta_tag_category` VALUES (2, '标签分类2', 1, NULL, 'F', 1, 1, '2025-06-10 00:51:35', '2025-06-10 00:51:35', 'GLOWXQ');

-- ----------------------------
-- Table structure for meta_text
-- ----------------------------
DROP TABLE IF EXISTS `meta_text`;
CREATE TABLE `meta_text`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'text名字',
  `text_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文本key',
  `text_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文本类型',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '图标',
  `business_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务类型',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文本标题',
  `skip_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '跳转URL',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文本内容',
  `sort` int NOT NULL DEFAULT 1 COMMENT '排序(降序)',
  `enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `meta_text_text_key_uindex`(`text_key` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文本' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of meta_text
-- ----------------------------
INSERT INTO `meta_text` VALUES (4, '首页公告', 'HomePageAnnouncement', 'Markdown', '', 'Announcement', '系统公告', '/', '# 🚧 Online Judge 系统维护公告\n\n**2025年10月1日** 由 OJ 技术团队发布\n\n---\n\n## 📅 维护时间\n北京时间 **2023年10月5日 02:00 - 06:00**  \n预计影响时长：4小时（视升级进度可能提前完成）\n\n---\n\n## 📢 更新内容\n### ✨ 新增功能\n1. **支持 Python 3.11**：更新语言编译器版本\n2. **题单收藏功能**：用户可创建自定义刷题清单\n3. **比赛回放模式**：结束的比赛支持提交代码测试（不计入排名）\n\n### ⚙️ 优化修复\n- 提交队列优先级算法升级（VIP用户优先判题）\n- 修复 C++ 特殊判题题（SPJ）内存计算误差\n- 移动端代码编辑器支持自动缩进\n- 题目难度标签体系重构（原⭐→★显示）\n\n---\n\n## ⚠️ 注意事项\n1. 维护期间 **所有提交将被清空队列**，请避免在此期间提交代码\n2. 比赛中的题目将在维护前30分钟进入 **只读模式**\n3. 建议提前保存草稿代码（路径：`/user/code_drafts`）\n\n---\n\n## 📮 问题反馈\n如有异常请联络：  \n📧 support@ojsystem.com  \n或通过个人中心提交反馈工单：[反馈入口](https://oj.com/feedback)\n\n---\n\n*本公告由系统自动生成，请勿直接回复*  \n🔧 技术团队将持续为您提供稳定的判题服务', 100, 1, '2025-05-29 15:07:18', '2025-06-07 19:27:53', 'F', 1, 9, 'GLOWXQ');

-- ----------------------------
-- Table structure for mini_user
-- ----------------------------
DROP TABLE IF EXISTS `mini_user`;
CREATE TABLE `mini_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '小程序用户ID',
  `sys_user_id` int NULL DEFAULT NULL COMMENT '关联的系统用户ID',
  `openid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '小程序用户的唯一标识',
  `unionid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公众号的唯一标识',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像URL',
  `subscribe` tinyint NULL DEFAULT NULL COMMENT '是否订阅公众号（1是0否）',
  `sex` tinyint NULL DEFAULT NULL COMMENT '性别，0-未知 1-男性，2-女性',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '删除标识',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_openid`(`openid` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '小程序用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mini_user
-- ----------------------------

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名称',
  `ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'ip',
  `tenant_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '租户id',
  `method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '方法',
  `url` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '访问URL',
  `content` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '描述',
  `module` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '模块名称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1933181095161503745 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户操作日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES (1932130874720276480, 'Authorization', '127.0.0.1', 'GLOWXQ', 'update', '/api/problem', '修改题目', 'question', '2025-06-10 01:41:25', '2025-06-12 22:24:52', 9, 9);
INSERT INTO `operation_log` VALUES (1933179938523455488, 'Authorization', '111.0.233.64', 'GLOWXQ', 'update', '/api/problem', '修改题目', 'question', '2025-06-12 23:09:49', '2025-06-12 23:09:49', 9, 9);
INSERT INTO `operation_log` VALUES (1933181095161503744, 'Authorization', '111.0.233.64', 'GLOWXQ', 'create', '/api/group', '新增班级', 'class', '2025-06-12 23:14:25', '2025-06-12 23:14:25', 9, 9);

-- ----------------------------
-- Table structure for problem
-- ----------------------------
DROP TABLE IF EXISTS `problem`;
CREATE TABLE `problem`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `problem_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目的自定义ID 例如(HOJ-1000)',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目',
  `author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '未知' COMMENT '作者',
  `program_type` int NOT NULL DEFAULT 0 COMMENT '0为ACM,1为OI',
  `problem_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Program' COMMENT '题目类型',
  `source_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'HOJ' COMMENT '来源类型',
  `link_video` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '视频链接',
  `time_limit` int NULL DEFAULT 1000 COMMENT '单位ms',
  `memory_limit` int NULL DEFAULT 65535 COMMENT '单位kb',
  `stack_limit` int NULL DEFAULT 128 COMMENT '单位mb',
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '描述',
  `input` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '输入描述',
  `output` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '输出描述',
  `examples` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '题面样例',
  `difficulty` int NULL DEFAULT 0 COMMENT '题目难度,0简单|1中等|2困难',
  `hint` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注,提醒',
  `auth` int NOT NULL DEFAULT 1 COMMENT '默认为1公开|2为私有|3为比赛题目',
  `io_score` int NULL DEFAULT 100 COMMENT '当该题目为io题目时的分数',
  `score` int NOT NULL DEFAULT 0 COMMENT '非编程题目总分',
  `source` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '题目来源',
  `judge_mode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'default' COMMENT '题目评测模式,default、spj、interactive',
  `judge_case_mode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'default' COMMENT '题目样例评测模式,default,subtask_lowest,subtask_average',
  `user_extra_file` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '题目评测时用户程序的额外文件 json key:name value:content',
  `judge_extra_file` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '题目评测时交互或特殊程序的额外文件 json key:name value:content',
  `spj_code` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '特判程序或交互程序代码',
  `spj_language` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '特判程序或交互程序代码的语言',
  `remote` tinyint(1) NULL DEFAULT 0 COMMENT '是否为远程vj判题',
  `code_share` tinyint(1) NULL DEFAULT 1 COMMENT '该题目对应的相关提交代码|用户是否可用分享',
  `remove_end_blank` tinyint(1) NULL DEFAULT 1 COMMENT '是否默认去除用户代码的文末空格',
  `open_case_result` tinyint(1) NULL DEFAULT 1 COMMENT '是否默认开启该题目的测试样例结果查看',
  `upload_case` tinyint(1) NULL DEFAULT 1 COMMENT '题目测试数据是否是上传文件的',
  `group_problem` tinyint(1) NULL DEFAULT 0 COMMENT '在队伍内',
  `file_io` tinyint(1) NULL DEFAULT 0 COMMENT '是否是file io自定义输入输出文件模式',
  `require_image` tinyint(1) NOT NULL DEFAULT 0 COMMENT '必须上传图片',
  `case_version` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '题目测试数据的版本号',
  `modified_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改题目的管理员用户名',
  `apply_public_progress` int NULL DEFAULT NULL COMMENT '申请公开的进度：null为未申请|1为申请中|2为申请通过|3为申请拒绝',
  `io_read_file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '题目指定的file io输入文件的名称',
  `io_write_file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '题目指定的file io输出文件的名称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `problem_unindex`(`problem_key` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 373 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '题目' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of problem
-- ----------------------------
INSERT INTO `problem` VALUES (331, 'Q1031', '[GESP202406 三级]  寻找倍数', '未知', 1, 'Programmer', 'Hoj', '', 1000, 256, 128, '小杨有一个包含 $n$ 个正整数的序列 $A=[a_1,a_2,\\dots,a_n]$，他想知道是否存在 $i(1\\leq i\\leq n)$ 使得 $a_i$ 是序列 $A$ 中所有数的倍数。', '第一行包含一个正整数 $t$，代表测试用例组数。\n\n接下来是 $t$ 组测试用例。对于每组测试用例，一共两行。\n\n其中，第一行包含一个正整数 $n$；第二行包含 $n$ 个正整数，代表序列 $A$。', '对于每组测试用例，如果存在 $i(1\\leq i\\leq n)$ ，满足对于所有 $k(1\\leq k\\leq n)$ $a_i$ 是 $a_k$ 的倍数，输出 `YES`，否则输出 `NO`。\n', '**用例 1**\n\n**输入：**\n```\n2\n3\n1 2 4\n5\n1 2 3 4 5\n```\n\n**输出：**\n```\nYES\nNO\n```\n', 1, '**【样例解释】**\n\n对于第⼀组数据，对于 $a_3=4$，满足 $a_3$ 是 $a_1$ 和 $a_2$ 的倍数。\n\n**【数据范围】**\n\n对于全部数据，保证有 $1\\leq t\\leq 10$，$1\\leq n\\leq 10^5$，$1\\leq a_i\\leq 10^9$。', 1, 100, 100, '', 'default', 'default', NULL, NULL, NULL, NULL, 0, 0, 1, 1, 1, 0, 0, 0, '1753958457816', '18861825150', 0, NULL, NULL, '2025-07-31 18:40:58', '2025-07-31 18:40:58', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for problem_black
-- ----------------------------
DROP TABLE IF EXISTS `problem_black`;
CREATE TABLE `problem_black`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `problem_id` bigint UNSIGNED NOT NULL COMMENT '题目ID',
  `business_id` bigint NOT NULL COMMENT '拉黑对象ID',
  `business_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '拉黑对象名',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '拉黑对象类型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `problem_black_contest_id`(`problem_id` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  CONSTRAINT `problem_black_contest_id` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '题目黑名单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of problem_black
-- ----------------------------

-- ----------------------------
-- Table structure for problem_case
-- ----------------------------
DROP TABLE IF EXISTS `problem_case`;
CREATE TABLE `problem_case`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `problem_id` bigint UNSIGNED NOT NULL COMMENT '题目ID',
  `problem_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目自定义ID',
  `input` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '测试样例的输入',
  `output` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '测试样例的输出',
  `input_url` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '在线测试样例的输入',
  `output_url` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '在线测试样例的输出',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'LocalFile' COMMENT '用例类型(LocalFile-本地文件|OssFile-Oss文件|DataText-数据库存储)',
  `score` int NULL DEFAULT NULL COMMENT '该测试样例的IO得分',
  `enable` tinyint(1) NULL DEFAULT 0 COMMENT '0禁用|1启用',
  `group_num` int NULL DEFAULT 1 COMMENT 'subtask分组的编号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `problem_case_problem_id`(`problem_id` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  CONSTRAINT `problem_case_problem_id_fk` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 2734 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '测试用例' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of problem_case
-- ----------------------------
INSERT INTO `problem_case` VALUES (2344, 331, 'Q1031', '01.in', '01.out', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/01.in', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/01.out', 'FileUpload', 10, NULL, NULL, '2025-07-31 18:41:00', '2025-07-31 18:41:00', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `problem_case` VALUES (2345, 331, 'Q1031', '02.in', '02.out', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/02.in', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/02.out', 'FileUpload', 10, NULL, NULL, '2025-07-31 18:41:00', '2025-07-31 18:41:00', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `problem_case` VALUES (2346, 331, 'Q1031', '03.in', '03.out', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/03.in', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/03.out', 'FileUpload', 10, NULL, NULL, '2025-07-31 18:41:00', '2025-07-31 18:41:00', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `problem_case` VALUES (2347, 331, 'Q1031', '04.in', '04.out', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/04.in', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/04.out', 'FileUpload', 10, NULL, NULL, '2025-07-31 18:41:00', '2025-07-31 18:41:00', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `problem_case` VALUES (2348, 331, 'Q1031', '05.in', '05.out', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/05.in', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/05.out', 'FileUpload', 10, NULL, NULL, '2025-07-31 18:41:00', '2025-07-31 18:41:00', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `problem_case` VALUES (2349, 331, 'Q1031', '06.in', '06.out', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/06.in', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/06.out', 'FileUpload', 10, NULL, NULL, '2025-07-31 18:41:00', '2025-07-31 18:41:00', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `problem_case` VALUES (2350, 331, 'Q1031', '07.in', '07.out', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/07.in', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/07.out', 'FileUpload', 10, NULL, NULL, '2025-07-31 18:41:00', '2025-07-31 18:41:00', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `problem_case` VALUES (2351, 331, 'Q1031', '08.in', '08.out', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/08.in', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/08.out', 'FileUpload', 10, NULL, NULL, '2025-07-31 18:41:00', '2025-07-31 18:41:00', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `problem_case` VALUES (2352, 331, 'Q1031', '09.in', '09.out', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/09.in', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/09.out', 'FileUpload', 10, NULL, NULL, '2025-07-31 18:41:00', '2025-07-31 18:41:00', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `problem_case` VALUES (2353, 331, 'Q1031', '10.in', '10.out', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/10.in', 'https://oj-api.oss-cn-guangzhou.aliyuncs.com/GLOWXQ/goj/testcase/problem_331/10.out', 'FileUpload', 10, NULL, NULL, '2025-07-31 18:41:00', '2025-07-31 18:41:00', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for problem_code_template
-- ----------------------------
DROP TABLE IF EXISTS `problem_code_template`;
CREATE TABLE `problem_code_template`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `problem_id` bigint UNSIGNED NOT NULL COMMENT '题目ID',
  `language_id` bigint UNSIGNED NOT NULL COMMENT '语言ID',
  `code` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模版代码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '语言',
  `enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `problem_code_template_language_id`(`language_id` ASC) USING BTREE,
  INDEX `problem_code_template_problem_id`(`problem_id` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  CONSTRAINT `problem_code_template_problem_id_fk` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 272 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '题目代码模版' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of problem_code_template
-- ----------------------------
INSERT INTO `problem_code_template` VALUES (271, 331, 3, '#include<iostream>\rusing namespace std;\rint main() {\r\r     return 0;\r}', 'C++', 1, '2025-07-31 18:40:58', '2025-07-31 18:40:58', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for problem_language
-- ----------------------------
DROP TABLE IF EXISTS `problem_language`;
CREATE TABLE `problem_language`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `problem_id` bigint UNSIGNED NOT NULL COMMENT '题目ID',
  `problem_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目自定义ID',
  `problem_language` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目语言',
  `language_id` bigint UNSIGNED NOT NULL COMMENT '语言ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `problem_language_language_id`(`language_id` ASC) USING BTREE,
  INDEX `problem_language_problem_id`(`problem_id` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  CONSTRAINT `problem_language_problem_id_fk` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 2036 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '题目语言' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of problem_language
-- ----------------------------
INSERT INTO `problem_language` VALUES (1364, 331, '', '', 3, '2025-07-31 18:40:58', '2025-07-31 18:40:58', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `problem_language` VALUES (1365, 331, '', '', 9, '2025-07-31 18:40:58', '2025-07-31 18:40:58', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `problem_language` VALUES (1366, 331, '', '', 10, '2025-07-31 18:40:58', '2025-07-31 18:40:58', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for problem_option
-- ----------------------------
DROP TABLE IF EXISTS `problem_option`;
CREATE TABLE `problem_option`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `problem_id` bigint UNSIGNED NOT NULL COMMENT '题目ID',
  `problem_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目自定义ID',
  `option_key` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '选项标识(如A/B/C/D)',
  `problem_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目类型(单选/多选/判断/填空)',
  `score` int NOT NULL DEFAULT 0 COMMENT '选项得分',
  `option_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '富文本选项内容',
  `answer` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否正确答案(0-否|1-是)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_question_key`(`problem_id` ASC, `option_key` ASC) USING BTREE COMMENT '题目选项唯一索引',
  INDEX `idx_question_id`(`problem_id` ASC) USING BTREE COMMENT '题目查询索引',
  INDEX `idx_question_type`(`problem_type` ASC) USING BTREE COMMENT '题型查询索引',
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  CONSTRAINT `problem_option_problem_id_fk` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '题目选项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of problem_option
-- ----------------------------

-- ----------------------------
-- Table structure for sys_client
-- ----------------------------
DROP TABLE IF EXISTS `sys_client`;
CREATE TABLE `sys_client`  (
  `client_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '客户端id',
  `client_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '客户端key',
  `client_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '客户端秘钥',
  `grant_type_cd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'password' COMMENT '授权类型',
  `device_type_cd` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '设备类型',
  `active_timeout` int NULL DEFAULT 1800 COMMENT 'token活跃超时时间',
  `timeout` int NULL DEFAULT 604800 COMMENT 'token固定超时',
  `client_status_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT '状态（正常 禁用）',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'F' COMMENT '删除标志',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `is_lock` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'F' COMMENT '是否锁定',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`client_id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统授权表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_client
-- ----------------------------
INSERT INTO `sys_client` VALUES ('195da9fcce574852b850068771cde034', 'sz-admin', '839ce050d3814949af9b2e1f815bc620', 'password', '1004001', 86400, 604800, '1003001', 'F', NULL, 1, '2024-01-22 13:43:51', 1, '2024-04-12 16:06:49', '演示client，禁止删除', 'T', 'GLOWXQ');
INSERT INTO `sys_client` VALUES ('e45e83eb2fff4aea941c5bc2b44a124b', '陪跑', 'b8cc18442d1d4abba9830f7169cf3c86', 'applet', '1004002', 86400, 604800, '1003001', 'F', NULL, 1, '2025-04-25 15:26:18', 1, '2025-04-25 15:31:30', 'test', 'F', 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '参数配置ID',
  `config_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '参数名',
  `config_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '参数key',
  `config_value` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '参数value',
  `is_lock` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '是否锁定',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '主体名称', 'sys.dept.entityName', 'xx公司', 'T', 1, '2024-03-22 10:42:46', 1, '2024-05-10 19:55:41', '公司主体名称', 'GLOWXQ');
INSERT INTO `sys_config` VALUES (2, '系统账户-初始密码', 'sys.user.initPwd', '123456', 'T', 1, '2024-04-10 09:56:58', 1, '2025-03-17 15:32:52', '', 'GLOWXQ');
INSERT INTO `sys_config` VALUES (3, '密码错误尝试次数限制', 'sys.pwd.errCnt', '5', 'T', 1, '2024-06-05 20:40:21', 1, '2024-06-05 20:50:11', '一段时间内的密码最大错误次数', 'GLOWXQ');
INSERT INTO `sys_config` VALUES (4, '密码错误冻结时间（分）', 'sys_pwd.lockTime', '30', 'T', 1, '2024-06-05 20:42:22', 1, '2024-06-05 20:43:30', '时间到期后自动解冻', 'GLOWXQ');
INSERT INTO `sys_config` VALUES (5, '业务字典起始号段', 'sys.dict.startNo', '2000', 'T', 1, '2024-07-08 17:29:16', NULL, NULL, '业务字典起始号段。1000作为默认的系统字典号段。', 'GLOWXQ');
INSERT INTO `sys_config` VALUES (6, '是否启用验证码', 'sys.captcha.state', 'false', 'T', 1, '2024-11-07 15:39:50', 1, '2025-03-17 15:33:03', '', 'GLOWXQ');
INSERT INTO `sys_config` VALUES (7, '验证码有效时间（秒）', 'sys.captcha.expire', '120', 'T', 1, '2025-01-08 22:06:40', NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_config` VALUES (8, '验证码请求次数限制', 'sys.captcha.requestLimit', '0', 'T', 1, '2025-01-08 22:09:28', 1, '2025-01-09 09:37:10', '一段时间内的验证码请求次数上限（0为不限制）', 'GLOWXQ');
INSERT INTO `sys_config` VALUES (9, '验证码计数周期（分）', 'sys.captcha.requestCycle', '1440', 'T', 1, '2025-01-08 22:13:09', 1, '2025-01-09 09:38:10', '默认一天', 'GLOWXQ');
INSERT INTO `sys_config` VALUES (10, '验证码水印', 'sys.captcha.waterText', 'Glowxq', 'T', 1, '2025-01-08 22:15:00', 1, '2025-03-17 15:47:10', '验证码右下角水印图案', 'GLOWXQ');
INSERT INTO `sys_config` VALUES (11, '是否启用验证码水印', 'sys.captcha.waterEnable', 'true', 'T', 1, '2025-01-08 22:18:10', 1, '2025-01-09 09:39:36', '', 'GLOWXQ');
INSERT INTO `sys_config` VALUES (12, '水印字体', 'sys.captcha.waterFont', 'Arial', 'T', 1, '2025-01-09 08:58:33', NULL, NULL, '请确认服务器是否支持该字体，并注意在商业用途中需确保字体版权合法使用', 'GLOWXQ');
INSERT INTO `sys_config` VALUES (13, '测试参数', '1', '1', 'F', 1, '2025-06-30 00:55:15', 1, '2025-06-30 00:55:15', NULL, 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_data_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_data_role`;
CREATE TABLE `sys_data_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '数据角色ID',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `data_scope_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据权限，data_scope字典',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '简介',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除与否',
  `is_lock` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '是否锁定',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统数据角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_data_role
-- ----------------------------
INSERT INTO `sys_data_role` VALUES (1, '教师统计-本部门及以下', '1006002', '', 'F', 'T', '2024-07-15 15:35:05', '2024-07-15 16:57:19', 1, 1, 'GLOWXQ');
INSERT INTO `sys_data_role` VALUES (2, '教师统计-仅本部门', '1006003', '', 'F', 'T', '2024-07-15 15:36:03', NULL, 1, NULL, 'GLOWXQ');
INSERT INTO `sys_data_role` VALUES (3, '教师统计-仅本人', '1006004', '', 'F', 'T', '2024-07-15 15:36:46', NULL, 1, NULL, 'GLOWXQ');
INSERT INTO `sys_data_role` VALUES (4, '教师统计-自定义', '1006005', '', 'F', 'T', '2024-07-15 15:37:27', NULL, 1, NULL, 'GLOWXQ');
INSERT INTO `sys_data_role` VALUES (5, 'aa', '1006005', '', 'T', 'F', '2025-05-26 15:10:20', '2025-05-26 15:10:20', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_data_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_data_role_menu`;
CREATE TABLE `sys_data_role_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` bigint NULL DEFAULT NULL COMMENT 'sys_data_role_id （数据角色表ID）',
  `menu_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'sys_menu_id （菜单表）',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 74 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统数据角色-菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_data_role_menu
-- ----------------------------
INSERT INTO `sys_data_role_menu` VALUES (69, 2, '85b54322630f43a39296488a5e76ba16', 'GLOWXQ');
INSERT INTO `sys_data_role_menu` VALUES (70, 3, '85b54322630f43a39296488a5e76ba16', 'GLOWXQ');
INSERT INTO `sys_data_role_menu` VALUES (71, 4, '85b54322630f43a39296488a5e76ba16', 'GLOWXQ');
INSERT INTO `sys_data_role_menu` VALUES (72, 1, '85b54322630f43a39296488a5e76ba16', 'GLOWXQ');
INSERT INTO `sys_data_role_menu` VALUES (73, 5, '6977359ec55c40aa80b908fdd323148f', 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_data_role_relation
-- ----------------------------
DROP TABLE IF EXISTS `sys_data_role_relation`;
CREATE TABLE `sys_data_role_relation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` bigint NULL DEFAULT NULL COMMENT 'sys_data_role_id （数据角色表ID）',
  `relation_type_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联类型，data_scope_relation_type',
  `relation_id` bigint NULL DEFAULT NULL COMMENT '关联表id，联动relation_type_cd（部门ID或个人ID）',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 80 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统数据角色-关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_data_role_relation
-- ----------------------------
INSERT INTO `sys_data_role_relation` VALUES (69, 2, '1007001', 15, 'GLOWXQ');
INSERT INTO `sys_data_role_relation` VALUES (70, 3, '1007001', 15, 'GLOWXQ');
INSERT INTO `sys_data_role_relation` VALUES (71, 4, '1007002', 5, 'GLOWXQ');
INSERT INTO `sys_data_role_relation` VALUES (72, 4, '1007002', 3, 'GLOWXQ');
INSERT INTO `sys_data_role_relation` VALUES (73, 1, '1007001', 4, 'GLOWXQ');
INSERT INTO `sys_data_role_relation` VALUES (74, 5, '1007001', 11, 'GLOWXQ');
INSERT INTO `sys_data_role_relation` VALUES (75, 5, '1007001', 12, 'GLOWXQ');
INSERT INTO `sys_data_role_relation` VALUES (76, 5, '1007001', 13, 'GLOWXQ');
INSERT INTO `sys_data_role_relation` VALUES (77, 5, '1007001', 14, 'GLOWXQ');
INSERT INTO `sys_data_role_relation` VALUES (78, 5, '1007001', 20, 'GLOWXQ');
INSERT INTO `sys_data_role_relation` VALUES (79, 5, '1007002', 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `pid` bigint NOT NULL COMMENT '父级ID',
  `deep` int NULL DEFAULT NULL COMMENT '层级',
  `user_id` bigint NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '部门名称',
  `principal` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '主管部门',
  `principal_number` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '主管部门编号',
  `region_id` bigint NOT NULL DEFAULT (-(1)) COMMENT '所属地址区域',
  `number_prefix` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编号前缀',
  `dept_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '部门编号',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '部门logo',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '部门图片',
  `content` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '部门介绍',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `has_children` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '是否有子级',
  `is_lock` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否锁定',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除标识',
  `remark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (1, 0, 1, NULL, NULL, NULL, '测试部门-1750427173144', '', '', 120000000000, 'TEST', 'DEPT-1750427173144', 'https://picsum.photos/120/120?random=5', 'https://picsum.photos/320/180?random=10', '# 测试部门介绍\n\n这是一个测试部门，主要用于系统功能验证。\n\n![部门示意图](https://picsum.photos/320/180?random=10)\n\n## 主要职责\n- 测试功能验证\n- 系统稳定性检查\n- 数据完整性校验\n- 用户体验优化\n\n## 部门特色\n- 专业的测试团队\n- 完善的测试流程\n- 先进的测试工具\n- 持续的质量改进\n\n## 联系方式\n- 📞 电话：13800138000\n- 📧 邮箱：test@example.com\n- 🏢 主管部门：测试主管部门', 1, 'T', 'F', 'F', '这是一个测试部门，用于验证系统功能', 1, 1, '2025-06-20 21:46:38', '2025-06-20 21:46:38', 'GLOWXQ');
INSERT INTO `sys_dept` VALUES (2, 1, 2, NULL, NULL, NULL, '测试部门-1750428144993', '', '', -1, 'TEST', 'DEPT-1750428144993', 'https://picsum.photos/120/120?random=3', 'https://picsum.photos/320/180?random=14', '# 测试部门介绍\n\n这是一个测试部门，主要用于系统功能验证。\n\n![部门示意图](https://picsum.photos/320/180?random=14)\n\n## 主要职责\n- 测试功能验证\n- 系统稳定性检查\n- 数据完整性校验\n- 用户体验优化\n\n## 部门特色\n- 专业的测试团队\n- 完善的测试流程\n- 先进的测试工具\n- 持续的质量改进\n\n## 联系方式\n- 📞 电话：13800138000\n- 📧 邮箱：test@example.com\n- 🏢 主管部门：测试主管部门', 1, 'F', 'F', 'F', '这是一个测试部门，用于验证系统功能', 1, 1, '2025-06-20 22:02:27', '2025-06-20 22:02:27', 'GLOWXQ');
INSERT INTO `sys_dept` VALUES (3, 0, 1, NULL, NULL, NULL, '测试部门-1750428173332', '', '', -1, 'TEST', 'DEPT-1750428173332', 'https://picsum.photos/120/120?random=2', 'https://picsum.photos/320/180?random=10', '# 测试部门介绍\n\n这是一个测试部门，主要用于系统功能验证。\n\n![部门示意图](https://picsum.photos/320/180?random=10)\n\n## 主要职责\n- 测试功能验证\n- 系统稳定性检查\n- 数据完整性校验\n- 用户体验优化\n\n## 部门特色\n- 专业的测试团队\n- 完善的测试流程\n- 先进的测试工具\n- 持续的质量改进\n\n## 联系方式\n- 📞 电话：13800138000\n- 📧 邮箱：test@example.com\n- 🏢 主管部门：测试主管部门', 1, 'F', 'F', 'F', '这是一个测试部门，用于验证系统功能', 1, 1, '2025-06-20 22:02:55', '2025-06-20 22:02:55', 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_dept_closure
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept_closure`;
CREATE TABLE `sys_dept_closure`  (
  `ancestor_id` bigint NOT NULL COMMENT '祖先节点ID',
  `descendant_id` bigint NOT NULL COMMENT '后代节点ID',
  `depth` int NOT NULL COMMENT '祖先节点到后代节点的距离',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门祖籍关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept_closure
-- ----------------------------
INSERT INTO `sys_dept_closure` VALUES (1, 1, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 1, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (2, 2, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 2, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (3, 3, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 3, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 4, 2, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (4, 4, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (1, 4, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 5, 2, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (5, 5, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (1, 5, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 6, 2, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (6, 6, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (1, 6, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 7, 2, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (7, 7, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (2, 7, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 8, 2, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (8, 8, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (2, 8, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 9, 2, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (9, 9, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (3, 9, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 10, 2, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (10, 10, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (3, 10, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (11, 11, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 11, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (12, 12, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 12, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (13, 13, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 13, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (14, 14, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 14, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 15, 3, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (1, 15, 2, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (15, 15, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (4, 15, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 16, 3, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (1, 16, 2, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (16, 16, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (4, 16, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 17, 3, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (1, 17, 2, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (17, 17, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (4, 17, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 18, 3, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (1, 18, 2, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (18, 18, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (4, 18, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 19, 3, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (1, 19, 2, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (19, 19, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (4, 19, 1, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (20, 20, 0, 'GLOWXQ');
INSERT INTO `sys_dept_closure` VALUES (0, 20, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_dept_leader
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept_leader`;
CREATE TABLE `sys_dept_leader`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门领导人ID',
  `dept_id` int NULL DEFAULT NULL,
  `leader_id` bigint NOT NULL COMMENT '领导人ID（sys_user_id）',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门领导人表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept_leader
-- ----------------------------
INSERT INTO `sys_dept_leader` VALUES (2, 4, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` bigint NOT NULL COMMENT '字典ID(规则)',
  `sys_dict_type_id` bigint NOT NULL COMMENT '关联sys_dict_type ID',
  `code_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典名称',
  `alias` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字典（Key）别名，某些情况下如果不想使用id作为key',
  `sort` int NOT NULL COMMENT '排序(正序)',
  `callback_show_style` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回显样式',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `is_lock` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否锁定',
  `is_show` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'T' COMMENT '是否展示',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `delete_id` bigint NULL DEFAULT NULL COMMENT '删除人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (1000001, 1000, '正常', '', 1, 'success', '', 'F', 'T', 'F', '2023-08-20 16:30:23', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1000002, 1000, '禁用', '', 2, 'info', '', 'F', 'T', 'F', '2023-08-20 16:33:45', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1000003, 1000, '禁言', '', 3, 'info', '', 'F', 'T', 'F', '2023-08-20 16:33:54', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1001001, 1001, '测试用户', '', 0, 'info', '', 'T', 'T', 'F', '2023-08-20 16:38:58', '2025-03-17 12:00:27', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1001002, 1001, '超级管理员', '', 0, 'info', '', 'T', 'T', 'F', '2023-08-20 16:39:05', '2025-03-17 12:00:27', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1001003, 1001, '普通用户', '', 0, 'info', '', 'T', 'T', 'F', '2023-08-20 16:39:11', '2025-03-17 12:00:27', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1002001, 1002, '目录', '', 1, 'warning', '', 'T', 'T', 'F', '2023-08-21 11:23:05', '2025-03-17 12:00:27', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1002002, 1002, '菜单', '', 2, 'success', '', 'T', 'T', 'F', '2023-08-21 11:23:17', '2025-03-17 12:00:27', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1002003, 1002, '按钮', '', 3, 'danger', '', 'T', 'T', 'F', '2023-08-21 11:23:22', '2025-03-17 12:00:27', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1003001, 1003, '正常', '', 1, 'success', '', 'F', 'T', 'F', '2024-01-22 09:44:52', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1003002, 1003, '禁用', '', 2, 'info', '', 'F', 'T', 'F', '2024-01-22 09:45:16', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1004001, 1004, 'PC', '', 1, 'success', 'pc端', 'F', 'T', 'F', '2024-01-22 10:03:19', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1004002, 1004, '小程序', '', 2, 'success', '小程序端', 'F', 'T', 'F', '2024-01-22 10:03:47', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1004003, 1004, 'Androd', '', 3, 'success', '', 'F', 'T', 'F', '2024-01-22 10:04:35', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1004004, 1004, 'IOS', '', 4, 'success', '', 'F', 'T', 'F', '2024-01-22 10:04:42', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1005001, 1005, '密码认证', 'password', 100, 'success', '', 'T', 'T', 'F', '2024-01-22 10:20:32', '2025-03-17 12:00:27', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1005002, 1005, '小程序认证', 'applet', 300, 'success', '', 'F', 'T', 'F', '2024-01-22 10:20:40', '2024-04-12 16:51:58', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1005003, 1005, '三方认证', 'third', 400, 'success', '', 'F', 'T', 'F', '2024-01-22 10:20:51', '2024-04-12 16:51:49', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1005004, 1005, '短信认证', 'sms', 200, 'success', '', 'F', 'T', 'F', '2024-01-22 10:20:57', '2024-04-12 16:51:41', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1006001, 1006, '全部', '', 1, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:55:48', '2024-06-25 19:11:28', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1006002, 1006, '本部门及以下', '', 2, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:56:57', '2024-06-25 19:11:29', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1006003, 1006, '仅本部门', '', 3, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:57:22', '2024-06-25 19:11:32', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1006004, 1006, '仅本人', '', 4, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:57:57', '2024-06-25 19:11:34', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1006005, 1006, '自定义', '', 5, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:58:11', '2024-06-25 19:11:36', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1007001, 1007, '部门权限', '', 1, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:59:00', '2024-06-25 19:11:38', NULL, NULL, NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_dict` VALUES (1007002, 1007, '个人权限', '', 2, 'primary', '个人权限高优先级', 'T', 'T', 'F', '2024-06-25 18:59:27', '2024-06-25 19:11:41', NULL, NULL, NULL, NULL, 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典类型ID',
  `type_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字典类型名(中文)',
  `type_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典类型码(英文)',
  `is_lock` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '是否锁定，锁定的属性无法在页面进行修改',
  `is_show` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'T' COMMENT '显示与否',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '删除与否',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '描述',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `delete_id` bigint NULL DEFAULT NULL COMMENT '删除人ID',
  `type` enum('system','business') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'business' COMMENT '字典类型: system 系统, business 业务',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1008 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1000, '账户状态', 'account_status', 'T', 'T', 'F', '', '2023-08-20 11:09:46', '2025-03-17 12:00:28', NULL, NULL, NULL, NULL, 'system', 'GLOWXQ');
INSERT INTO `sys_dict_type` VALUES (1001, '用户标签', 'user_tag', 'T', 'T', 'F', '', '2023-08-20 14:22:40', '2025-03-17 12:00:28', NULL, NULL, NULL, NULL, 'system', 'GLOWXQ');
INSERT INTO `sys_dict_type` VALUES (1002, '菜单类型', 'menu_type', 'T', 'T', 'F', '', '2023-08-21 11:20:47', '2025-03-17 12:00:28', NULL, NULL, NULL, NULL, 'system', 'GLOWXQ');
INSERT INTO `sys_dict_type` VALUES (1003, '授权状态', 'sys_client_status', 'T', 'T', 'F', 'client授权状态', '2023-08-22 09:44:27', '2025-03-17 12:00:28', NULL, NULL, NULL, NULL, 'system', 'GLOWXQ');
INSERT INTO `sys_dict_type` VALUES (1004, '设备类型', 'device_type', 'T', 'T', 'F', '', '2023-08-22 10:02:11', '2025-03-17 12:00:28', NULL, NULL, NULL, NULL, 'system', 'GLOWXQ');
INSERT INTO `sys_dict_type` VALUES (1005, '授权类型', 'grant_type', 'T', 'T', 'F', '', '2023-08-22 10:15:58', '2025-03-17 12:00:28', NULL, NULL, NULL, NULL, 'system', 'GLOWXQ');
INSERT INTO `sys_dict_type` VALUES (1006, '数据权限', 'data_scope', 'T', 'T', 'F', '', '2024-06-25 18:54:21', '2024-06-25 19:12:46', NULL, NULL, NULL, NULL, 'system', 'GLOWXQ');
INSERT INTO `sys_dict_type` VALUES (1007, '数据权限关联类型', 'data_scope_relation_type', 'T', 'T', 'F', '自定义数据权限的关联类型', '2024-06-25 18:55:37', '2024-06-25 19:12:48', NULL, NULL, NULL, NULL, 'system', 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_export_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_export_info`;
CREATE TABLE `sys_export_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `file_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '导出的文件名称',
  `export_status_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '导出状态,关联字典表export_status',
  `create_id` int NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '导出信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_export_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件名',
  `dir_tag` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '目录标识',
  `size` bigint NULL DEFAULT NULL COMMENT '文件大小',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '文件域名',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `object_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '对象名（唯一）',
  `context_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件类型',
  `e_tag` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'eTag标识',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 107 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file
-- ----------------------------
INSERT INTO `sys_file` VALUES (97, '教师统计 (43) (203252.669).xlsx', 'tmp', 9866, 'https://minioapi.szadmin.cn/test/tmp/20241216/教师统计 (43) (203252.669).xlsx', '2024-12-16 20:32:53', 'tmp/20241216/教师统计 (43) (203252.669).xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '8bba8015aa748013cc8295a13637fb3a', 1, 'GLOWXQ');
INSERT INTO `sys_file` VALUES (98, '教师统计 (203323.951).xlsx', 'tmp', 9866, 'https://minioapi.szadmin.cn/test/tmp/20241216/教师统计 (203323.951).xlsx', '2024-12-16 20:33:24', 'tmp/20241216/教师统计 (203323.951).xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '8bba8015aa748013cc8295a13637fb3a', 1, 'GLOWXQ');
INSERT INTO `sys_file` VALUES (99, '微信图片_20240420160033.jpg', 'user', 20276, 'https://minioapi.szadmin.cn/test/user/20241216/微信图片_20240420160033.jpg', '2024-12-16 20:39:57', 'user/20241216/微信图片_20240420160033.jpg', 'image/jpeg', '322e08e6b47cd85dec6a7b8dc9e88476', 1, 'GLOWXQ');
INSERT INTO `sys_file` VALUES (100, 'problem_export_1743095444952.zip', 'local', 1198360, 'httpsglowxq-oj.oss-cn-guangzhou.aliyuncs.com/local/zip/2025/04/07/local/zip/2025/04/07//20250407/problem_export_1743095444952.zip', '2025-04-07 17:32:20', 'local/zip/2025/04/07/local/zip/2025/04/07//20250407/problem_export_1743095444952.zip', 'application/zip', '9DF6F7F8BA54EAAF5D7D2AB5B9823E14', NULL, 'GLOWXQ');
INSERT INTO `sys_file` VALUES (101, 'problem_export_1743095444952.zip', 'local', 1198360, 'httpsglowxq-oj.oss-cn-guangzhou.aliyuncs.com/local/zip/2025/04/07/local/zip/2025/04/07//20250407/problem_export_1743095444952.zip', '2025-04-07 17:37:21', 'local/zip/2025/04/07/local/zip/2025/04/07//20250407/problem_export_1743095444952.zip', 'application/zip', '9DF6F7F8BA54EAAF5D7D2AB5B9823E14', NULL, 'GLOWXQ');
INSERT INTO `sys_file` VALUES (102, 'problem_export_1743095444952.zip', 'local', 1198360, 'httpsglowxq-oj.oss-cn-guangzhou.aliyuncs.com/local/zip/2025/04/07/local/zip/2025/04/07//20250407/problem_export_1743095444952.zip', '2025-04-07 17:38:07', 'local/zip/2025/04/07/local/zip/2025/04/07//20250407/problem_export_1743095444952.zip', 'application/zip', '9DF6F7F8BA54EAAF5D7D2AB5B9823E14', NULL, 'GLOWXQ');
INSERT INTO `sys_file` VALUES (103, 'problem_export_1743095444952.zip', 'local', 1198360, 'httpsglowxq-oj.oss-cn-guangzhou.aliyuncs.com/local/zip/2025/04/07/local/zip/2025/04/07//20250407/problem_export_1743095444952.zip', '2025-04-07 17:43:43', 'local/zip/2025/04/07/local/zip/2025/04/07//20250407/problem_export_1743095444952.zip', 'application/zip', '9DF6F7F8BA54EAAF5D7D2AB5B9823E14', NULL, 'GLOWXQ');
INSERT INTO `sys_file` VALUES (104, 'problem_export_1743095444952.zip', 'local', 1198360, 'httpsglowxq-oj.oss-cn-guangzhou.aliyuncs.com/local/zip/2025/04/07/local/zip/2025/04/07//20250407/problem_export_1743095444952.zip', '2025-04-07 17:44:42', 'local/zip/2025/04/07/local/zip/2025/04/07//20250407/problem_export_1743095444952.zip', 'application/zip', '9DF6F7F8BA54EAAF5D7D2AB5B9823E14', NULL, 'GLOWXQ');
INSERT INTO `sys_file` VALUES (105, 'problem_export_1743095444952.zip', 'local', 1198360, 'httpsglowxq-oj.oss-cn-guangzhou.aliyuncs.com/local/zip/2025/04/07/local/zip/2025/04/07//20250407/problem_export_1743095444952.zip', '2025-04-07 17:46:00', 'local/zip/2025/04/07/local/zip/2025/04/07//20250407/problem_export_1743095444952.zip', 'application/zip', '9DF6F7F8BA54EAAF5D7D2AB5B9823E14', NULL, 'GLOWXQ');
INSERT INTO `sys_file` VALUES (106, 'problem_export_1743095444952.zip', 'local', 1198360, 'https://glowxq-oj.oss-cn-guangzhou.aliyuncs.com/local/zip/2025/04/07/local/zip/2025/04/07//20250407/problem_export_1743095444952.zip', '2025-04-07 17:46:55', 'local/zip/2025/04/07/local/zip/2025/04/07//20250407/problem_export_1743095444952.zip', 'application/zip', '9DF6F7F8BA54EAAF5D7D2AB5B9823E14', NULL, 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单表id',
  `pid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '父级id',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '路径',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由名称',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Admin' COMMENT '菜单类型',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'icon图标',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '组件路径',
  `redirect` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '外链地址',
  `sort` int NOT NULL COMMENT '排序',
  `deep` int NOT NULL COMMENT '层级',
  `menu_type_cd` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '菜单类型 （字典表menu_type）',
  `permissions` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '按钮权限',
  `is_hidden` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '是否隐藏',
  `has_children` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否有子级',
  `is_link` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '路由外链时填写的访问地址',
  `is_full` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '菜单是否全屏',
  `is_affix` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '菜单是否固定在标签页',
  `is_keep_alive` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '当前路由是否缓存',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `delete_id` bigint NULL DEFAULT NULL COMMENT '删除人ID',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  `use_data_scope` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '菜单是否开启数据权限',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('006bdbacd71a481f88b6acf895529acd', '8354d626cc65487594a7c38e98de1bad', '', '', '修改', 'Admin', '', '', '', 200, 3, '1002003', 'sys.dept.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-19 15:42:48', '2025-03-17 12:00:35', NULL, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('02e67d318684498c8719906dade80cd7', '2eb3e960a3b7490794f5ecab6da92577', '/system/meta/metaText', 'MetaTextView', '文本', 'Admin', 'Coin', '/system/meta/metaText/index', '', 600, 2, '1002002', 'meta.text.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-04-25 14:20:38', NULL, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('0444cd2c01584f0687264b6205536691', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/admin/dataRoleManage', 'SysDataRoleView', '数据权限', 'Admin', 'svg-scope', '/system/admin/dataRoleManage/index', '', 800, 2, '1002002', 'sys.data.role.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-07-09 19:19:45', '2025-03-17 12:00:35', NULL, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('049b16c3d77544bdaab9fa11d1f7f731', '92a7fe55d18d49888974e6b1909332a0', '', '', '删除', 'Admin', '', '', '', 300, 2, '1002003', 'problem.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:07', '2025-05-27 02:59:07', NULL, NULL, 'F', NULL, '2025-05-27 02:59:07', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('05194ef5fa7a4a308a44f6f5c6791c3a', '99c2ee7b882749e597bcd62385f368fb', '', '', '编辑菜单', 'Admin', '', '', '', 200, 3, '1002003', 'sys.menu.update_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-08-31 15:31:35', '2024-05-10 20:41:15', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('078abe736ceb4c1384e048db7ba6d03c', 'e328544f4cdb4354a189062b7831bf62', '', '', '修改', 'Admin', '', '', '', 200, 4, '1002003', 'meta.tag.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('0933b165ffc14d558e8de43ccb6687f6', 'c6dd479d5b304731be403d7551c60d70', '', '', '编辑角色', 'Admin', '', '', '', 200, 3, '1002003', 'sys.role.update_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:30:43', '2024-05-10 20:41:01', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('0caef49a81694985b8a8ce238dcf00f8', '61a13f97f0264bf88b095edfe01e8ff6', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'meta.category.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('0e529e8a9dbf450898b695e051c36d48', 'da1b46db642f42978f83ed5eb34870ce', '/toolbox/generator', 'generator', '代码生成', 'Admin', 'Brush', '/toolbox/generator/index', '', 100, 2, '1002002', 'generator.list', 'F', 'T', 'F', 'F', 'F', 'F', '2023-12-08 13:50:39', '2025-03-17 12:00:33', 1, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('0f23ebe1a9914e769b75e6a797ea006f', '392a723082bc4a2082ff4f76de40e748', '', '', '修改', 'Admin', '', '', '', 200, 3, '1002003', 'topic.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('0f98b89c67e54cb0bcff2b56aa98832f', '140c9ed43ef54542bbcdde8a5d928400', '', '', '新增账号', 'Admin', '', '', '', 100, 3, '1002003', 'sys.user.create_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:31:55', '2024-05-10 20:40:31', 1, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('140c9ed43ef54542bbcdde8a5d928400', 'e7f8a72021b848f7805051260be19314', '/oj/user/account', 'OjUserAccount', '账号管理', 'Admin', 'UserFilled', '/oj/user/account/index', '', 100, 2, '1002002', 'sys.user.query_table', 'F', 'T', 'F', 'F', 'F', 'T', '2023-08-29 13:53:49', '2025-06-23 11:00:19', 8, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('153c1c93519d480eb838f0199038b0e3', 'dc45662ca9594624acc22b2838ba22ba', '', '', '导出', 'Admin', '', '', '', 900, 3, '1002003', 'meta.category.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('1545ff4141254fba81c19b45d43bcc62', '92a7fe55d18d49888974e6b1909332a0', '', '', '导出', 'Admin', '', '', '', 300, 2, '1002003', 'problem.export', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:07', '2025-05-27 02:59:07', NULL, NULL, 'F', NULL, '2025-05-27 02:59:07', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('15466ed7598c4808b9bd108c2a98b6ba', 'c62a45cc9d3c4fa69c240fde6ae05b70', '', '', '导入', 'Admin', '', '', '', 300, 2, '1002003', 'topic.import', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:19', '2025-05-27 02:59:19', NULL, NULL, 'F', NULL, '2025-05-27 02:59:19', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('15a92b2ac53240f0abfc07ddacc7eccd', '5e145005903947fbbfa70c0c24d23211', '', '', '导出', 'Admin', '', '', '', 900, 2, '1002003', 'meta.category.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('164b6db594614ff8a4bb86969be296b7', '4624f50464c046068238bc82d2274700', '', '', '新增', 'Admin', '', '', '', 100, 3, '1002003', 'tenant.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('19f5e5ad4ebd421eb24a7a1158c68594', '0', '/oj/topic', 'oj-topic', 'OJ数据', 'Admin', 'Expand', '', '', 500, 1, '1002001', '', 'T', 'T', 'F', 'F', 'F', 'F', NULL, '2025-06-28 13:27:31', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('1a23d7802570408583ab7c5f41a72e06', 'b7ff08a48e6343c3be6696f3512c15e9', '', '', '导入', 'Admin', '', '', '', 300, 2, '1002003', 'judge.import', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 03:00:01', '2025-05-27 03:00:01', NULL, NULL, 'F', NULL, '2025-05-27 03:00:01', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('1a86a9d2b3ca49439277fff9f499c7cd', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '删除字典', 'Admin', '', '', '', 600, 3, '1002003', 'sys.dict.delete_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-04 13:58:26', '2024-05-10 20:41:45', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('1d291d738b2b45fb95afd6715db84c47', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/tenant/tenant', 'TenantView', '租户', 'Admin', 'Odometer', '/system/tenant/tenant/index', '', 1700, 2, '1002002', 'tenant.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-06-06 16:05:48', NULL, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('1d4447bd66044716b8c107f3ecb91348', '392a723082bc4a2082ff4f76de40e748', '', '', '导入', 'Admin', '', '', '', 700, 3, '1002003', 'topic.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('1e2a9a6364d4445b80ad1c1603e64cde', '6600c804453d4437a263f218014b5619', '', '', '导出', 'Admin', '', '', '', 900, 3, '1002003', 'meta.image.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('1e618d46a0d94128bfdb83814a3c2b74', '2c4749c308084dfd8ce3906c5cdeda23', '', '', '导出', 'Admin', '', '', '', 300, 2, '1002003', 'export', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:47:16', '2025-05-27 02:47:16', NULL, NULL, 'T', NULL, '2025-05-27 02:47:16', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('1f2caf3426aa400f92d3673d359b258d', '99c2ee7b882749e597bcd62385f368fb', '', '', '生成权限按钮', 'Admin', '', '', '', 500, 3, '1002003', 'sys.menu.gen_btn', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, 1, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('1f5e65f2020f40fe9be4e5c2b4a9965f', '5f2ad3bf794e4f998cb3671ed879dd2a', '', '', '导入', 'Admin', '', '', '', 700, 2, '1002003', 'applet.user.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('1fdc9d0ab6da4591921f75655a05c9bb', '02e67d318684498c8719906dade80cd7', '', '', '导出', 'Admin', '', '', '', 900, 3, '1002003', 'meta.text.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('20b760c3f93e4cee991b33003d704ceb', 'a229c8fada174090a4ef906ace30bf6f', '', '', '导出', 'Admin', '', '', '', 900, 3, '1002003', 'meta.category.closure.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('20fa91e12d2c4e0386b89a8b1692cd2e', '41b54cf19c584995bfdc08acf411e18d', '', '', '修改', 'Admin', '', '', '', 300, 3, '1002003', 'code.record.update', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:40', '2025-05-27 02:59:40', NULL, NULL, 'F', NULL, '2025-05-27 02:59:40', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('211faa0ac79d4dfeb4391f579310605e', '58d47947a01342e5b5e62447d1b9feb1', '', '', '删除', 'Admin', '', '', '', 300, 2, '1002003', 'course.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:13', '2025-05-27 02:59:13', NULL, NULL, 'F', NULL, '2025-05-27 02:59:13', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('220ca44050a745678b378733443ed794', 'a3c34b69f7be4e3299a2dfce6bf98bf4', '', '', '导出', 'Admin', '', '', '', 300, 2, '1002003', 'topic.export', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:58:58', '2025-05-27 02:58:58', NULL, NULL, 'F', NULL, '2025-05-27 02:58:58', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('22aa1a83a5ae453d86f31d466db4f2ed', '63f26741a1c24977bf4428161816f0a5', '', '', '导出', 'Admin', '', '', '', 900, 3, '1002003', 'meta.menu.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('2868079355ce4b6c985b1b746dbb0952', 'c4896e8735a745bda9b47ecaf50f46f2', '', '', '新增', 'Admin', '', '', '', 100, 3, '1002003', 'sys.file.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('29d33eba6b73420287d8f7e64aea62b3', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/admin/configManage', 'configManage', '参数管理', 'Admin', 'Key', '/system/admin/configManage/index', '', 500, 2, '1002002', 'sys.config.query_table', 'F', 'T', 'F', 'F', 'F', 'T', '2023-11-24 09:54:25', '2024-05-10 20:39:43', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('2c4749c308084dfd8ce3906c5cdeda23', '0', '/oj/group/group', 'oj-group', '班级', 'Common', 'Grape', '/oj/group/group/index', '', 700, 1, '1002002', 'group.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-05-27 02:58:09', 1, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('2d38167ef90f4992aca71aae2daa0f06', '2c4749c308084dfd8ce3906c5cdeda23', '', '', '新增', 'Admin', '', '', '', 300, 2, '1002003', 'group.create', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:58:31', '2025-05-27 02:58:31', NULL, NULL, 'T', NULL, '2025-05-27 02:58:31', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('2d3d67779f084b84a55b4ddbb342ac92', 'c62a45cc9d3c4fa69c240fde6ae05b70', '', '', '修改', 'Admin', '', '', '', 300, 2, '1002003', 'topic.update', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:19', '2025-05-27 02:59:19', NULL, NULL, 'F', NULL, '2025-05-27 02:59:19', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('2eb3e960a3b7490794f5ecab6da92577', '0', '/system/meta', 'system-meta', '基本配置', 'Admin', 'Cloudy', '', '', 500, 1, '1002001', '', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-04-26 07:31:06', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('2fd407cdb0b84bf6ac42337170c11605', '5f2ad3bf794e4f998cb3671ed879dd2a', '', '', '修改', 'Admin', '', '', '', 200, 2, '1002003', 'applet.user.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('308146bd8adb4999bc2cc78eb1e91262', 'a229c8fada174090a4ef906ace30bf6f', '', '', '修改', 'Admin', '', '', '', 200, 3, '1002003', 'meta.category.closure.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('30942929802f41cc850722c78db089e7', '140c9ed43ef54542bbcdde8a5d928400', '', '', '设置数据角色', 'Admin', '', '', '', 800, 3, '1002003', 'sys.user.data_role_set_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-07-02 19:22:37', '2024-07-11 20:27:03', 1, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('316f75fe8d4b44a0b114ce570c76a419', 'aa6df607a69f4100abbfb77016dc7a9a', '', '', '导出', 'Admin', '', '', '', 900, 2, '1002003', 'user.info.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('31c2707fad8c4aaea032ae25b06fee66', '5f2ad3bf794e4f998cb3671ed879dd2a', '', '', '导出', 'Admin', '', '', '', 900, 2, '1002003', 'applet.user.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('330a1a0a857c4ad1a95327db5134e420', '140c9ed43ef54542bbcdde8a5d928400', '', '', '解锁', 'Admin', '', '', '', 500, 3, '1002003', 'sys.user.unlock_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-29 14:06:49', '2024-05-10 20:40:49', 1, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('336771de588d47349f62be3533f46839', 'a3c34b69f7be4e3299a2dfce6bf98bf4', '', '', '删除', 'Admin', '', '', '', 300, 2, '1002003', 'topic.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:58:58', '2025-05-27 02:58:58', NULL, NULL, 'F', NULL, '2025-05-27 02:58:58', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('33c673d555c54b83ad4673a6603f427e', '2c4749c308084dfd8ce3906c5cdeda23', '', '', '删除', 'Admin', '', '', '', 300, 2, '1002003', '删除', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:44:59', '2025-05-27 02:44:59', NULL, NULL, 'T', NULL, '2025-05-27 02:44:59', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('392a723082bc4a2082ff4f76de40e748', '19f5e5ad4ebd421eb24a7a1158c68594', '/oj/topic/topic', 'TopicView', '主题', 'Admin', 'Bowl', '/oj/topic/topic/index', '', 1500, 2, '1002002', 'topic.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-04-26 13:58:55', NULL, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('3a54d488132b4331bf3cd5e6d86ffcf4', '29d33eba6b73420287d8f7e64aea62b3', '', '', '修改参数', 'Admin', '', '', '', 200, 3, '1002003', 'sys.config.update_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-11-24 09:57:38', '2024-05-10 20:42:50', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('3ba9407560a1490583fefa10b22bc74f', '8354d626cc65487594a7c38e98de1bad', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'sys.dept.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-19 15:42:48', '2025-03-17 12:00:35', NULL, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('3c06236312f1486a90e3f513619445e8', 'dc45662ca9594624acc22b2838ba22ba', '', '', '新增', 'Admin', '', '', '', 100, 3, '1002003', 'meta.category.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('3d26ccc3458948218bc0c605e77e024d', 'ecab878018034cfda49767bc4a66b388', '', '', '新增', 'Admin', '', '', '', 100, 4, '1002003', 'meta.tag.bind.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('3d3bbe793e8d4179b4b489d2adc2accc', '63f26741a1c24977bf4428161816f0a5', '', '', '导入', 'Admin', '', '', '', 700, 3, '1002003', 'meta.menu.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('3e6d2f54012542e08e39f05cb80ee8c6', '1d291d738b2b45fb95afd6715db84c47', '', '', '新增', 'Admin', '', '', '', 100, 3, '1002003', 'tenant.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('3ed14c58c4d641f18c6575473d982054', '4624f50464c046068238bc82d2274700', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'tenant.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('3f555e4a01174a1d9b29be439668e32f', '0444cd2c01584f0687264b6205536691', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'sys.data.role.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-07-09 19:19:11', '2025-03-17 12:00:35', NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('3fcb96a6a9b44a5e8cf64ffa6f3e8b8f', 'ecab878018034cfda49767bc4a66b388', '', '', '导出', 'Admin', '', '', '', 900, 4, '1002003', 'meta.tag.bind.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('410574ff78234277b3b5aaafcc3f9177', '2c4749c308084dfd8ce3906c5cdeda23', '', '', '导出', 'Admin', '', '', '', 300, 2, '1002003', 'group.export', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:58:31', '2025-05-27 02:58:31', NULL, NULL, 'T', NULL, '2025-05-27 02:58:31', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('41b54cf19c584995bfdc08acf411e18d', 'e0291f59e82a42ffb7899e4b6174cf94', '/oj/code/codeRecord', '/oj/code/codeRecord', '代码记录', 'Common', 'Coffee', '/oj/code/codeRecord/index', '', 100, 2, '1002002', 'code.record.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-05-28 08:51:02', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('428ed8f76fce4450ab73595d6814df31', '02e67d318684498c8719906dade80cd7', '', '', '新增', 'Admin', '', '', '', 100, 3, '1002003', 'meta.text.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('445b73dda9a34ad681d2705a7abcf2f6', 'c6dd479d5b304731be403d7551c60d70', '', '', '删除角色', 'Admin', '', '', '', 300, 3, '1002003', 'sys.role.delete_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:31:05', '2024-05-10 20:41:04', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('44fc6b1822fa46e5bbf530b1e15086c4', '61a13f97f0264bf88b095edfe01e8ff6', '', '', '修改', 'Admin', '', '', '', 200, 3, '1002003', 'meta.category.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('45e19955f92849a190ab7707c98e78c4', '2c4749c308084dfd8ce3906c5cdeda23', '', '', '导入', 'Admin', '', '', '', 300, 2, '1002003', 'import', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:47:16', '2025-05-27 02:47:16', NULL, NULL, 'T', NULL, '2025-05-27 02:47:16', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('4624f50464c046068238bc82d2274700', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/oj/system/tenant', 'TenantView', '租户', 'Admin', '', '/oj/system/tenant/index', '', 1100, 2, '1002002', 'tenant.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('47b58c5b38514711946d509a8ab0e8a3', 'a229c8fada174090a4ef906ace30bf6f', '', '', '导入', 'Admin', '', '', '', 700, 3, '1002003', 'meta.category.closure.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('47dd1145fb50415cb88920ba8140ce7c', '700e6977068d46bd94052d526ba93a3a', '', '', '新增', 'Admin', '', '', '', 100, 4, '1002003', 'meta.tag.category.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('49c75878b4d445f8be5f69e21e18b70d', 'c4896e8735a745bda9b47ecaf50f46f2', '', '', '修改', 'Admin', '', '', '', 200, 3, '1002003', 'sys.file.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('4a92e91a83b04a768f714759deaad7e4', '6600c804453d4437a263f218014b5619', '', '', '新增', 'Admin', '', '', '', 100, 3, '1002003', 'meta.image.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('4c02eb570b734d05ac31d9658f9f893a', '0f1555af4c6a4b789dbb9f4e774b8d82', '', '', '修改', 'Admin', '', '', '', 200, 3, '1002003', 'meta.tag.category.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('4c5256d0d11d41399fabace89776387f', '63f26741a1c24977bf4428161816f0a5', '', '', '修改', 'Admin', '', '', '', 200, 3, '1002003', 'meta.menu.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('4d2a08f8c7f440a3b28c32d28a4db98a', '8d89a35c15f74f9dbec6397098b15c05', '', '', '修改', 'Admin', '', '', '', 300, 3, '1002003', 'code.monitor.update', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:52', '2025-05-27 02:59:52', NULL, NULL, 'F', NULL, '2025-05-27 02:59:52', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('4e5b3ddd0bd94814a40197f2edd53535', 'a229c8fada174090a4ef906ace30bf6f', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'meta.category.closure.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('4f008f8705ea439c96505f96d20214ec', '5f2ad3bf794e4f998cb3671ed879dd2a', '', '', '新增', 'Admin', '', '', '', 100, 2, '1002003', 'applet.user.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('4f39ef0fd2f748f6ab7d6d20d98bc4af', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '新增字典类型', 'Admin', '', '', '', 100, 3, '1002003', 'sys.dict.add_type_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-08-31 15:52:38', '2024-05-10 20:41:28', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('500cc8b7ccfe4185a21069ecfafc329a', '92a7fe55d18d49888974e6b1909332a0', '', '', '修改', 'Admin', '', '', '', 300, 2, '1002003', 'problem.update', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:07', '2025-05-27 02:59:07', NULL, NULL, 'F', NULL, '2025-05-27 02:59:07', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('51ec495814b94820ba770504b4bd0a90', 'b7ff08a48e6343c3be6696f3512c15e9', '', '', '删除', 'Admin', '', '', '', 300, 2, '1002003', 'judge.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 03:00:01', '2025-05-27 03:00:01', NULL, NULL, 'F', NULL, '2025-05-27 03:00:01', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('5208f4e0838e4ff695f69b15b8d6de54', '0f1555af4c6a4b789dbb9f4e774b8d82', '', '', '新增', 'Admin', '', '', '', 100, 3, '1002003', 'meta.tag.category.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('52fbcc1960f0429f89382b3df41ba353', 'a229c8fada174090a4ef906ace30bf6f', '', '', '新增', 'Admin', '', '', '', 100, 3, '1002003', 'meta.category.closure.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('53cd98d9eac24efa96f399465805ab47', '700e6977068d46bd94052d526ba93a3a', '', '', '导出', 'Admin', '', '', '', 900, 4, '1002003', 'meta.tag.category.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('543f0dfa90374a62b8326f138f5e6b4f', '1d291d738b2b45fb95afd6715db84c47', '', '', '修改', 'Admin', '', '', '', 200, 3, '1002003', 'tenant.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('545e1725a8254c7e941ae4b843058dc5', '2c4749c308084dfd8ce3906c5cdeda23', '', '', '修改', 'Admin', '', '', '', 300, 2, '1002003', 'update', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:47:16', '2025-05-27 02:47:16', NULL, NULL, 'T', NULL, '2025-05-27 02:47:16', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('5633340a91634134871169f632b6c9c1', '4624f50464c046068238bc82d2274700', '', '', '修改', 'Admin', '', '', '', 200, 3, '1002003', 'tenant.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('57cc50a4261f4190a60a199b6ad4428b', '6600c804453d4437a263f218014b5619', '', '', '导入', 'Admin', '', '', '', 700, 3, '1002003', 'meta.image.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('58195d36dc684a4a924adc8a3131111f', '2c4749c308084dfd8ce3906c5cdeda23', '', '', '删除', 'Admin', '', '', '', 300, 2, '1002003', 'remove', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:47:16', '2025-05-27 02:47:16', NULL, NULL, 'T', NULL, '2025-05-27 02:47:16', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('58d47947a01342e5b5e62447d1b9feb1', '0', '/oj/course', 'oj-course', '课程', 'Common', 'Collection', '/oj/course/index', '', 1000, 1, '1002002', 'course.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, NULL, 1, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('5acaae87bac84de3aace33dd1d35455e', '41b54cf19c584995bfdc08acf411e18d', '', '', '新增', 'Admin', '', '', '', 300, 3, '1002003', 'code.record.create', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:40', '2025-05-27 02:59:40', NULL, NULL, 'F', NULL, '2025-05-27 02:59:40', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('5b33ac3d630543d09d1388fae4d13fc0', '9e731ff422184fc1be2022c5c985735e', '', '', '修改', 'Admin', '', '', '', 200, 3, '1002003', 'sys.client.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-22 10:52:09', '2025-03-17 12:00:35', NULL, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('5b5fb3748c6a4ed5a4dda3877508c3a7', 'c6dd479d5b304731be403d7551c60d70', '', '', '设置权限', 'Admin', '', '', '', 400, 3, '1002003', 'sys.role.setting_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:31:35', '2024-05-10 20:41:07', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('5c47eb146767429b9451ac4529fd8531', 'aa6df607a69f4100abbfb77016dc7a9a', '', '', '导入', 'Admin', '', '', '', 700, 2, '1002003', 'user.info.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('5c7115233db740e49cde3c9b0afea1a5', '5f2ad3bf794e4f998cb3671ed879dd2a', '', '', '删除', 'Admin', '', '', '', 300, 2, '1002003', 'applet.user.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('5d20685cbb2d4661aaa44ecce085ab4f', '5e145005903947fbbfa70c0c24d23211', '', '', '删除', 'Admin', '', '', '', 300, 2, '1002003', 'meta.category.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('5e145005903947fbbfa70c0c24d23211', '0', '/system/meta/metaCategory', 'MetaCategoryView', '分类表', 'Admin', '', '/system/meta/metaCategory/index', '', 3500, 1, '1002002', 'meta.category.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('5e8b043d1f5943d7af2553680f3c912a', 'b7ff08a48e6343c3be6696f3512c15e9', '', '', '新增', 'Admin', '', '', '', 300, 2, '1002003', 'judge.create', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 03:00:01', '2025-05-27 03:00:01', NULL, NULL, 'F', NULL, '2025-05-27 03:00:01', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('5f2ad3bf794e4f998cb3671ed879dd2a', '0', '/system/applet/appletUser', 'AppletUserView', '小程序用户', 'Admin', '', '/system/applet/appletUser/index', '', 600, 1, '1002002', 'applet.user.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('61172ce2061b401f8933c1b7ca5aa0c2', '0f1555af4c6a4b789dbb9f4e774b8d82', '', '', '导入', 'Admin', '', '', '', 700, 3, '1002003', 'meta.tag.category.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('61a13f97f0264bf88b095edfe01e8ff6', '2eb3e960a3b7490794f5ecab6da92577', '/system/meta/metaCategory', 'MetaCategoryView', '分类表', 'Admin', '', '/system/meta/metaCategory/index', '', 100, 2, '1002002', 'meta.category.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('62284b72c4f444a2b10269f7db58d736', 'c62a45cc9d3c4fa69c240fde6ae05b70', '', '', '删除', 'Admin', '', '', '', 300, 2, '1002003', 'topic.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:19', '2025-05-27 02:59:19', NULL, NULL, 'F', NULL, '2025-05-27 02:59:19', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('63f26741a1c24977bf4428161816f0a5', '2eb3e960a3b7490794f5ecab6da92577', '/system/meta/metaMenu', 'MetaMenuView', '菜单', 'Admin', 'Coin', '/system/meta/metaMenu/index', '', 300, 2, '1002002', 'meta.menu.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-04-25 14:20:15', NULL, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('651182e3348b4a108cdd6eb27f973fc6', '63f26741a1c24977bf4428161816f0a5', '', '', '新增', 'Admin', '', '', '', 100, 3, '1002003', 'meta.menu.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('6600c804453d4437a263f218014b5619', '2eb3e960a3b7490794f5ecab6da92577', '/system/meta/metaImage', 'MetaImageView', '图片', 'Admin', 'Coin', '/system/meta/metaImage/index', '', 800, 2, '1002002', 'meta.image.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-04-25 14:20:46', NULL, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('6633afcb31124727ae7e0b0f2671eb30', '4624f50464c046068238bc82d2274700', '', '', '导出', 'Admin', '', '', '', 900, 3, '1002003', 'tenant.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('67717296123d48008e2a842778917446', 'e328544f4cdb4354a189062b7831bf62', '', '', '导入', 'Admin', '', '', '', 700, 4, '1002003', 'meta.tag.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('67fe93780f8441c5859ac21f85145622', 'e328544f4cdb4354a189062b7831bf62', '', '', '导出', 'Admin', '', '', '', 900, 4, '1002003', 'meta.tag.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('686a5522b0334d4da51aa15b3fd1a303', '140c9ed43ef54542bbcdde8a5d928400', '', '', '设置部门', 'Admin', '', '', '', 700, 3, '1002003', 'sys.user.dept_set_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-12 14:33:21', '2025-03-17 12:00:35', 1, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('686c9a5af6c949789c10a88c86659600', '2eb3e960a3b7490794f5ecab6da92577', '/tag', 'tag', '标签管理', 'Admin', 'Coin', '', '', 800, 2, '1002001', '', 'F', 'T', 'F', 'F', 'F', 'F', NULL, NULL, 1, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('6894c7069f4649b2b55783a1ec0e9032', '2c4749c308084dfd8ce3906c5cdeda23', '', '', '导入', 'Admin', '', '', '', 300, 2, '1002003', 'group.import', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:58:31', '2025-05-27 02:58:31', NULL, NULL, 'T', NULL, '2025-05-27 02:58:31', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('6977359ec55c40aa80b908fdd323148f', '0', '/oj/glowc/glowc', 'oj-GlowC', 'GlowC', 'Common', 'BrushFilled', '/oj/glowc/glowc/index', '', 600, 1, '1002002', '', 'F', 'F', 'F', 'F', 'F', 'F', NULL, '2025-05-28 08:33:24', 1, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('6a74a4452db24b7193f06f8aff14a1f6', '8d89a35c15f74f9dbec6397098b15c05', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'code.monitor.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:52', '2025-05-27 02:59:52', NULL, NULL, 'F', NULL, '2025-05-27 02:59:52', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('6b91dcd5272a4467a49421482b12f9c1', '02e67d318684498c8719906dade80cd7', '', '', '修改', 'Admin', '', '', '', 200, 3, '1002003', 'meta.text.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('6b9c7b394ebf43f59398dd1588090f3d', 'e7f8a72021b848f7805051260be19314', '/oj/user/userInfo', 'UserInfoView', '用户信息', 'Admin', 'UserFilled', '/oj/user/userInfo/index', '', 400, 2, '1002002', 'user.info.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-06-23 11:11:36', NULL, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('6c46fd01faf042fc9dd4a9c9b9ef2c5a', '9e731ff422184fc1be2022c5c985735e', '', '', '新增', 'Admin', '', '', '', 100, 3, '1002003', 'sys.client.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-22 10:52:09', '2025-03-17 12:00:35', NULL, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('6e25a716c1a646009a9be90b16f0a682', '140c9ed43ef54542bbcdde8a5d928400', '', '', '设置角色', 'Admin', '', '', '', 400, 3, '1002003', 'sys.user.role_set_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:33:53', '2024-05-10 20:40:46', 1, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('700e6977068d46bd94052d526ba93a3a', '686c9a5af6c949789c10a88c86659600', '/system/meta/metaTagCategory', 'MetaTagCategoryView', '标签分类', 'Admin', 'Coin', '/system/meta/metaTagCategory/index', '', 1000, 3, '1002002', 'meta.tag.category.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-06-10 00:49:26', NULL, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('71b3bba9eb8a46ec8ad8ef121ba717bc', '5e145005903947fbbfa70c0c24d23211', '', '', '修改', 'Admin', '', '', '', 200, 2, '1002003', 'meta.category.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('79f853ebf2b34983b72e27f1ba6e38fb', 'ecab878018034cfda49767bc4a66b388', '', '', '导入', 'Admin', '', '', '', 700, 4, '1002003', 'meta.tag.bind.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('7a4544831af34e69aa73148bf84b9924', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', 'SQL按钮', 'Admin', '', '', '', 700, 3, '1002003', 'sys.dict.sql_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-08-11 16:10:09', '2025-03-17 12:00:35', 1, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('7b7adca3a6224e8da36711e3ea00df71', 'e328544f4cdb4354a189062b7831bf62', '', '', '新增', 'Admin', '', '', '', 100, 4, '1002003', 'meta.tag.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('7c89c40f32234b11b6b42f94358674c1', '5e145005903947fbbfa70c0c24d23211', '', '', '新增', 'Admin', '', '', '', 100, 2, '1002003', 'meta.category.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('7eda4f3cad474e23a5314b546b48e8d5', 'e328544f4cdb4354a189062b7831bf62', '', '', '删除', 'Admin', '', '', '', 300, 4, '1002003', 'meta.tag.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('81647226a2d047e8ab0b70472350ee69', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '新增字典', 'Admin', '', '', '', 400, 3, '1002003', 'sys.dict.add_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-04 13:54:55', '2024-05-10 20:41:37', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('818cc6e1889d46579525ad8ab921eeb8', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '编辑字典类型', 'Admin', '', '', '', 200, 3, '1002003', 'sys.dict.update_type_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:39:29', '2024-05-10 20:41:31', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('8231a369712e4f8f8ac09fce232cd034', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/admin/sysTempFile', 'SysTempFileView', '模版文件管理', 'Admin', 'DocumentCopy', '/system/admin/sysTempFile/index', '', 1000, 2, '1002002', 'sys.temp.file.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('8255bac5eae748a0a8500167963b3e00', '140c9ed43ef54542bbcdde8a5d928400', '', '', '编辑账号', 'Admin', '', '', '', 200, 3, '1002003', 'sys.user.update_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:32:15', '2024-05-10 20:40:35', 1, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('8354d626cc65487594a7c38e98de1bad', 'e7f8a72021b848f7805051260be19314', '/system/admin/deptManage', 'SysDeptView', '部门管理', 'Admin', 'svg-org', '/system/admin/deptManage/index', '', 700, 2, '1002002', 'sys.dept.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-03-19 15:42:48', '2025-06-23 11:00:40', NULL, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('84915c51fcf546f88dd946fbd14b74bd', '02e67d318684498c8719906dade80cd7', '', '', '导入', 'Admin', '', '', '', 700, 3, '1002003', 'meta.text.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('85b54322630f43a39296488a5e76ba16', '0', '/teacher/teacherStatistics', 'TeacherStatisticsView', '教师统计', 'Admin', 'svg-org', '/teacher/teacherStatistics/index', '', 300, 1, '1002002', 'teacher.statistics.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-02-17 10:19:32', '2025-03-17 12:00:35', NULL, 1, 'T', NULL, NULL, 'T', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('87a26b76daad47c2a12c470605563c4a', '8231a369712e4f8f8ac09fce232cd034', '', '', '修改', 'Admin', '', '', '', 200, 3, '1002003', 'sys.temp.file.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('88b2e5def2ff474fa8bf3537d4a2fe5b', '0', '/system/admin', 'system', '系统管理', 'Admin', 'Tools', '', '', 100, 1, '1002001', '', 'F', 'T', 'F', 'F', 'F', 'T', '2023-08-29 13:52:10', '2024-05-10 20:38:55', 8, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('892bf6bacb244f97aaf3bccb25243bdb', '6b9c7b394ebf43f59398dd1588090f3d', '', '', '导入', 'Admin', '', '', '', 700, 3, '1002003', 'user.info.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('8cbed332abed419bb718a454f9352d0a', '58d47947a01342e5b5e62447d1b9feb1', '', '', '新增', 'Admin', '', '', '', 300, 2, '1002003', 'course.create', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:13', '2025-05-27 02:59:13', NULL, NULL, 'F', NULL, '2025-05-27 02:59:13', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('8d0b8b57a58e41a5a5e840cc2b3703f4', '9e731ff422184fc1be2022c5c985735e', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'sys.client.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-22 10:52:09', '2025-03-17 12:00:35', NULL, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('8d1733a9550943fcbabb3862d44e5317', '6600c804453d4437a263f218014b5619', '', '', '修改', 'Admin', '', '', '', 200, 3, '1002003', 'meta.image.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('8d34a411bb004d6ba0af89cd72ef72a3', '2c4749c308084dfd8ce3906c5cdeda23', '', '', '导入', 'Admin', '', '', '', 300, 2, '1002003', '导入', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:44:59', '2025-05-27 02:44:59', NULL, NULL, 'T', NULL, '2025-05-27 02:44:59', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('8d35a4ee75ef4379b0206d14b170ae79', '6600c804453d4437a263f218014b5619', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'meta.image.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('8d718a96f719413aae242608cf02ab83', 'b7ff08a48e6343c3be6696f3512c15e9', '', '', '导出', 'Admin', '', '', '', 300, 2, '1002003', 'judge.export', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 03:00:01', '2025-05-27 03:00:01', NULL, NULL, 'F', NULL, '2025-05-27 03:00:01', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('8d89a35c15f74f9dbec6397098b15c05', 'e0291f59e82a42ffb7899e4b6174cf94', '/oj/code/codeMonitor', '/oj/code/codeMonitor', '代码监控', 'Common', 'Coffee', '/oj/code/codeMonitor/index', '', 200, 2, '1002002', 'code.monitor.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-05-28 08:51:19', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('8d92cf6f2f3248569d5dd6cb6b958d7c', '0444cd2c01584f0687264b6205536691', '', '', '新增', 'Admin', '', '', '', 100, 3, '1002003', 'sys.data.role.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-07-09 19:19:11', '2025-03-17 12:00:35', NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('8ece44129e8047e7b492ab42d32aff8e', '0', '/home/oj/index', 'oj-home', '首页', 'Admin', 'HomeFilled', '/home/oj/index', '', 1, 1, '1002002', '', 'T', 'F', 'F', 'F', 'F', 'F', NULL, '2025-04-26 16:41:53', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('8f0978943f73423d92c1d75329b0b647', '2c4749c308084dfd8ce3906c5cdeda23', '', '', '新增', 'Admin', '', '', '', 300, 2, '1002003', 'create', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:47:16', '2025-05-27 02:47:16', NULL, NULL, 'T', NULL, '2025-05-27 02:47:16', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('8fd6721941494fd5bbe16bec82b235be', '8354d626cc65487594a7c38e98de1bad', '', '', '新增', 'Admin', '', '', '', 100, 3, '1002003', 'sys.dept.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-19 15:42:48', '2025-03-17 12:00:35', NULL, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('906a445fdf3f4518bd80dd99812c2d26', '6b9c7b394ebf43f59398dd1588090f3d', '', '', '修改', 'Admin', '', '', '', 200, 3, '1002003', 'user.info.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('90ba1d50e22b41a5b712484718e4b1ab', '0f1555af4c6a4b789dbb9f4e774b8d82', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'meta.tag.category.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('91a965a53fe74a149cfc4280c1d113cb', '61a13f97f0264bf88b095edfe01e8ff6', '', '', '导出', 'Admin', '', '', '', 900, 3, '1002003', 'meta.category.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('91b61d600a45429690da6d187f60bf2c', 'a3c34b69f7be4e3299a2dfce6bf98bf4', '', '', '导入', 'Admin', '', '', '', 300, 2, '1002003', 'topic.import', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:58:58', '2025-05-27 02:58:58', NULL, NULL, 'F', NULL, '2025-05-27 02:58:58', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('92a7fe55d18d49888974e6b1909332a0', '0', '/oj/problem/problem', 'oj-problem', '题目', 'Common', 'QuestionFilled', '/oj/problem/problem/index', '', 900, 1, '1002002', 'problem.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-04-26 07:22:42', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('9338bf2f57984825bc227bb618f9db81', '99c2ee7b882749e597bcd62385f368fb', '', '', '新增菜单', 'Admin', '', '', '', 100, 3, '1002003', 'sys.menu.create_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-08-31 14:27:50', '2024-05-10 20:41:13', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('9488df8c01074b339332e9a97526bc0d', '41b54cf19c584995bfdc08acf411e18d', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'code.record.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:40', '2025-05-27 02:59:40', NULL, NULL, 'F', NULL, '2025-05-27 02:59:40', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('97f11d74c98047ba80f011a3da9d882c', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '编辑字典', 'Admin', '', '', '', 500, 3, '1002003', 'sys.dict.update_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-04 13:55:13', '2024-05-10 20:41:42', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('9830d86487184961b90fc527c9604720', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '删除字典类型', 'Admin', '', '', '', 300, 3, '1002003', 'sys.dict.delete_type_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-04 13:57:33', '2024-05-10 20:41:34', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('99c2ee7b882749e597bcd62385f368fb', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/admin/menuMange', 'menuMange', '菜单管理', 'Admin', 'Menu', '/system/admin/menuMange/index', '', 300, 2, '1002002', 'sys.menu.query_table', 'F', 'T', 'F', 'F', 'F', 'T', '2023-08-29 13:55:30', '2024-05-10 20:39:34', 8, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('9c7e4011cee74468a317afdd9b9d16cd', '700e6977068d46bd94052d526ba93a3a', '', '', '导入', 'Admin', '', '', '', 700, 4, '1002003', 'meta.tag.category.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('9d3f218f8ecb429e8e1c82f905258aa5', '700e6977068d46bd94052d526ba93a3a', '', '', '删除', 'Admin', '', '', '', 300, 4, '1002003', 'meta.tag.category.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('9e731ff422184fc1be2022c5c985735e', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/admin/clientManage', 'ClientManageView', '客户端管理', 'Admin', 'Operation', '/system/admin/clientManage/index', '', 600, 2, '1002002', 'sys.client.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-01-22 10:52:09', '2025-03-17 12:00:35', NULL, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('a0e2770f2adc4c97a409917543d1cf9b', '2c4749c308084dfd8ce3906c5cdeda23', '', '', '新增', 'Admin', '', '', '', 300, 2, '1002003', '新增', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:44:59', '2025-05-27 02:44:59', NULL, NULL, 'T', NULL, '2025-05-27 02:44:59', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('a1e034e75ba64bf5915007107ba4d302', 'd095463d52ac4d2fa8f142cbb18f10df', '11222', '2222', '123', 'Common', 'svg-zip', '22222', '', 100, 2, '1002002', '', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, 1, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('a229c8fada174090a4ef906ace30bf6f', '2eb3e960a3b7490794f5ecab6da92577', '/system/meta/metaCategoryClosure', 'MetaCategoryClosureView', '分类祖籍关系表', 'Admin', '', '/system/meta/metaCategoryClosure/index', '', 700, 2, '1002002', 'meta.category.closure.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('a299997c3af6404bb316b01010752164', '6b9c7b394ebf43f59398dd1588090f3d', '', '', '新增', 'Admin', '', '', '', 100, 3, '1002003', 'user.info.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('a2f178794f734d7f85f5080c6fea4769', '5e145005903947fbbfa70c0c24d23211', '', '', '导入', 'Admin', '', '', '', 700, 2, '1002003', 'meta.category.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('a309b954fd4d44a480663fe59901f699', 'dc45662ca9594624acc22b2838ba22ba', '', '', '导入', 'Admin', '', '', '', 700, 3, '1002003', 'meta.category.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('a3c34b69f7be4e3299a2dfce6bf98bf4', '0', '/oj/topic/contest?topicType=Contest', 'oj-topic-contest', '比赛', 'Common', 'GoldMedal', '/oj/contest/topic/index', '', 800, 1, '1002002', 'topic.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-04-26 07:28:21', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('a56587ece10847c09590ab216b963426', '8d89a35c15f74f9dbec6397098b15c05', '', '', '导出', 'Admin', '', '', '', 300, 3, '1002003', 'code.monitor.export', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:52', '2025-05-27 02:59:52', NULL, NULL, 'F', NULL, '2025-05-27 02:59:52', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('a58f8d66c888428fbb015858abc6df3c', '392a723082bc4a2082ff4f76de40e748', '', '', '导出', 'Admin', '', '', '', 900, 3, '1002003', 'topic.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('a6880f36ed29443da9e4f416bdd721c8', '0f1555af4c6a4b789dbb9f4e774b8d82', '', '', '导出', 'Admin', '', '', '', 900, 3, '1002003', 'meta.tag.category.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('a6c6199be67d4feeb8f980b04d1255ab', '41b54cf19c584995bfdc08acf411e18d', '', '', '导入', 'Admin', '', '', '', 300, 3, '1002003', 'code.record.import', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:40', '2025-05-27 02:59:40', NULL, NULL, 'F', NULL, '2025-05-27 02:59:40', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('a6f9ae721440401c891abfa73922683a', '2c4749c308084dfd8ce3906c5cdeda23', '', '', '修改', 'Admin', '', '', '', 300, 2, '1002003', '修改', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:44:59', '2025-05-27 02:44:59', NULL, NULL, 'T', NULL, '2025-05-27 02:44:59', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('aa6df607a69f4100abbfb77016dc7a9a', '0', '/oj/user/userInfo', 'UserInfoView', '用户信息', 'Admin', '', '/oj/user/userInfo/index', '', 1800, 1, '1002002', 'user.info.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('aef79557604b432fb8520cbb1a969bed', 'a3c34b69f7be4e3299a2dfce6bf98bf4', '', '', '新增', 'Admin', '', '', '', 300, 2, '1002003', 'topic.create', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:58:58', '2025-05-27 02:58:58', NULL, NULL, 'F', NULL, '2025-05-27 02:58:58', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('afe01b660aad4660b30ace1fa3c2c1d2', 'dc45662ca9594624acc22b2838ba22ba', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'meta.category.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('b2518c77e20e46e7a4d71ddb6a7c090c', '8d89a35c15f74f9dbec6397098b15c05', '', '', '导入', 'Admin', '', '', '', 300, 3, '1002003', 'code.monitor.import', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:52', '2025-05-27 02:59:52', NULL, NULL, 'F', NULL, '2025-05-27 02:59:52', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('b2d24780894c40238b9d6ef7778ae0e1', 'aa6df607a69f4100abbfb77016dc7a9a', '', '', '新增', 'Admin', '', '', '', 100, 2, '1002003', 'user.info.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('b428eba3f9a34025a46c394df5390b88', '29d33eba6b73420287d8f7e64aea62b3', '', '', '删除参数', 'Admin', '', '', '', 300, 3, '1002003', 'sys.config.delete_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-11-24 09:58:06', '2024-05-10 20:53:43', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('b5e7aa0270d94764816b29ed05fd99ee', '02e67d318684498c8719906dade80cd7', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'meta.text.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('b7ff08a48e6343c3be6696f3512c15e9', '0', '/oj/judge/judge', 'oj-judge', '测评', 'Common', 'Aim', '/oj/judge/judge/index', '', 600, 1, '1002002', 'judge.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-04-26 12:19:53', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('badc7ff787e74a988d0954c9f70fc619', 'c62a45cc9d3c4fa69c240fde6ae05b70', '', '', '导出', 'Admin', '', '', '', 300, 2, '1002003', 'topic.export', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:19', '2025-05-27 02:59:19', NULL, NULL, 'F', NULL, '2025-05-27 02:59:19', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('bb3e190bf9a6485899881ea67d27f2b6', '2c4749c308084dfd8ce3906c5cdeda23', '', '', '修改', 'Admin', '', '', '', 300, 2, '1002003', 'group.update', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:58:31', '2025-05-27 02:58:31', NULL, NULL, 'T', NULL, '2025-05-27 02:58:31', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('bbd755c332e7435bb8167c6ee007fcaa', '6b9c7b394ebf43f59398dd1588090f3d', '', '', '导出', 'Admin', '', '', '', 900, 3, '1002003', 'user.info.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('bc0bac502da2489488d3ef79214b9d8b', '700e6977068d46bd94052d526ba93a3a', '', '', '修改', 'Admin', '', '', '', 200, 4, '1002003', 'meta.tag.category.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('c1d30a0076684590aa9f5fde1beb53a4', 'aa6df607a69f4100abbfb77016dc7a9a', '', '', '删除', 'Admin', '', '', '', 300, 2, '1002003', 'user.info.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('c1f1ef5e0145488b9023e929987c1e01', '63f26741a1c24977bf4428161816f0a5', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'meta.menu.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('c4896e8735a745bda9b47ecaf50f46f2', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/admin/fileManage', 'SysFileView', '文件管理', 'Admin', 'Files', '/system/admin/fileManage/index', '', 900, 2, '1002002', 'sys.file.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('c55de3135b864579bda79c279f4129a9', 'c4896e8735a745bda9b47ecaf50f46f2', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'sys.file.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('c5acb6a80c9e4f998d838a11075022c7', 'dc45662ca9594624acc22b2838ba22ba', '', '', '修改', 'Admin', '', '', '', 200, 3, '1002003', 'meta.category.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('c62a45cc9d3c4fa69c240fde6ae05b70', '0', '/oj/topic/other?topicType=Other', 'oj-topic-other', '作业-练习', 'Common', 'Document', '/oj/topic/topic/index', '', 1200, 1, '1002002', 'topic.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-04-26 12:34:59', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('c6dd479d5b304731be403d7551c60d70', 'e7f8a72021b848f7805051260be19314', '/system/admin/roleManage', 'roleManage', '角色管理', 'Admin', 'User', '/system/admin/roleManage/index', '', 200, 2, '1002002', 'sys.role.query_table', 'F', 'T', 'F', 'F', 'F', 'T', '2023-08-29 13:54:36', '2025-06-23 11:00:30', 8, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('c987327b5c9d4bffa583dc8d993e639a', 'b7ff08a48e6343c3be6696f3512c15e9', '', '', '修改', 'Admin', '', '', '', 300, 2, '1002003', 'judge.update', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 03:00:01', '2025-05-27 03:00:01', NULL, NULL, 'F', NULL, '2025-05-27 03:00:01', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('cd1537bd1e2347b8ac73596231b13566', '2c4749c308084dfd8ce3906c5cdeda23', '', '', '导出', 'Admin', '', '', '', 300, 2, '1002003', '导出', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:44:59', '2025-05-27 02:44:59', NULL, NULL, 'T', NULL, '2025-05-27 02:44:59', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('ce6677739e0c4db797f63066cd87087b', '4624f50464c046068238bc82d2274700', '', '', '导入', 'Admin', '', '', '', 700, 3, '1002003', 'tenant.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('cea01dcde9b24b5a8686bdc33c438cd7', '140c9ed43ef54542bbcdde8a5d928400', '', '', '删除账号', 'Admin', '', '', '', 300, 3, '1002003', 'sys.user.delete_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:33:27', '2024-05-10 20:40:41', 1, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('d095463d52ac4d2fa8f142cbb18f10df', '0', '/oj/glowc/test', 'test123', 'test', 'Common', 'svg-scope', '/oj/glowc/test/index', '', 1500, 1, '1002002', '', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-08-04 00:36:28', 1, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('d14d69764b0149b5a6b43c8cb5400dbe', 'ecab878018034cfda49767bc4a66b388', '', '', '删除', 'Admin', '', '', '', 300, 4, '1002003', 'meta.tag.bind.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('d7b3e978b364421f916b5ac470121ef9', '92a7fe55d18d49888974e6b1909332a0', '', '', '导入', 'Admin', '', '', '', 300, 2, '1002003', 'problem.import', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:07', '2025-05-27 02:59:07', NULL, NULL, 'F', NULL, '2025-05-27 02:59:07', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('d938e91a766a45fda8cae24aaf0cc3f4', '1d291d738b2b45fb95afd6715db84c47', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'tenant.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('da1b46db642f42978f83ed5eb34870ce', '0', '/toolbox', 'toolbox', '工具箱', 'Admin', 'Briefcase', '', '', 200, 1, '1002001', '', 'F', 'T', 'F', 'F', 'F', 'F', '2023-12-08 13:46:08', '2024-05-10 20:39:17', 1, 1, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('dc45662ca9594624acc22b2838ba22ba', '2eb3e960a3b7490794f5ecab6da92577', '/system/meta/metaCategory', 'MetaCategoryView', '分类', 'Admin', 'Coin', '/system/meta/metaCategory/index', '', 200, 2, '1002002', 'meta.category.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-04-25 14:20:07', NULL, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('dcb6aabcd910469ebf3efbc7e43282d4', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/admin/dictManage', 'dictManage', '字典管理', 'Admin', 'Reading', '/system/admin/dictManage/index', '', 400, 2, '1002002', 'sys.dict.query_table', 'F', 'T', 'F', 'F', 'F', 'T', '2023-08-29 13:57:12', '2024-05-10 20:39:39', 8, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('dee6ca209d9d4178833bb9abd814f1eb', '392a723082bc4a2082ff4f76de40e748', '', '', '新增', 'Admin', '', '', '', 100, 3, '1002003', 'topic.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('df2894b4c06e47cab84142d81edc494d', 'c6dd479d5b304731be403d7551c60d70', '', '', '新增角色', 'Admin', '', '', '', 100, 3, '1002003', 'sys.role.create_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:30:18', '2024-05-10 20:40:57', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('e0215e3497854df3a5a182992b7d60d8', '41b54cf19c584995bfdc08acf411e18d', '', '', '导出', 'Admin', '', '', '', 300, 3, '1002003', 'code.record.export', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:40', '2025-05-27 02:59:40', NULL, NULL, 'F', NULL, '2025-05-27 02:59:40', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('e0291f59e82a42ffb7899e4b6174cf94', '0', '/oj/code', 'oj-code', '代码', 'Common', 'Coffee', '', '', 1300, 1, '1002001', '', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-04-26 12:34:09', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('e17720533d7a447dadc7fcff9b6f5e7e', '6b9c7b394ebf43f59398dd1588090f3d', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'user.info.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('e27c299ec9e84ed9b028c5d6e40e2cf5', '92a7fe55d18d49888974e6b1909332a0', '', '', '新增', 'Admin', '', '', '', 300, 2, '1002003', 'problem.create', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:07', '2025-05-27 02:59:07', NULL, NULL, 'F', NULL, '2025-05-27 02:59:07', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('e328544f4cdb4354a189062b7831bf62', '686c9a5af6c949789c10a88c86659600', '/system/meta/metaTag', 'MetaTagView', '标签', 'Admin', 'Coin', '/system/meta/metaTag/index', '', 400, 3, '1002002', 'meta.tag.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-06-10 00:49:35', NULL, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('e59d99537cce410087f3ea631e5941eb', '8d89a35c15f74f9dbec6397098b15c05', '', '', '新增', 'Admin', '', '', '', 300, 3, '1002003', 'code.monitor.create', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:52', '2025-05-27 02:59:52', NULL, NULL, 'F', NULL, '2025-05-27 02:59:52', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('e7f8a72021b848f7805051260be19314', '0', 'user', 'user', '组织管理', 'Admin', 'UserFilled', '', '', 200, 1, '1002001', '', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-06-23 11:03:56', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('e80a9ddc25d54fcd88f9ac3bbfaf0ccd', '2c4749c308084dfd8ce3906c5cdeda23', '', '', '删除', 'Admin', '', '', '', 300, 2, '1002003', 'group.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:58:31', '2025-05-27 02:58:31', NULL, NULL, 'T', NULL, '2025-05-27 02:58:31', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('e88e9dcd26b44a5ea2bb87081b066cd0', 'a3c34b69f7be4e3299a2dfce6bf98bf4', '', '', '修改', 'Admin', '', '', '', 300, 2, '1002003', 'topic.update', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:58:58', '2025-05-27 02:58:58', NULL, NULL, 'F', NULL, '2025-05-27 02:58:58', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('e91eeaea8f1546d3921839469fe247b6', '140c9ed43ef54542bbcdde8a5d928400', '', '', '重置密码', 'Admin', '', '', '', 600, 3, '1002003', 'sys.user_resetPwd', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-10 10:16:08', '2025-03-17 12:00:35', 1, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('e931c84b8bc945a7b6ba2d58c8a93afc', '8231a369712e4f8f8ac09fce232cd034', '', '', '新增', 'Admin', '', '', '', 100, 3, '1002003', 'sys.temp.file.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('ecab878018034cfda49767bc4a66b388', '686c9a5af6c949789c10a88c86659600', '/system/meta/metaTagBind', 'MetaTagBindView', '绑定标签', 'Admin', 'Coin', '/system/meta/metaTagBind/index', '', 500, 3, '1002002', 'meta.tag.bind.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, '2025-06-10 00:49:43', NULL, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('ede76f5e60b640aa9de2ba7216b90ceb', '29d33eba6b73420287d8f7e64aea62b3', '', '', '新增参数', 'Admin', '', '', '', 100, 3, '1002003', 'sys.config.add_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-11-24 09:57:19', '2024-05-10 20:42:46', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('ee36ad68586e42fa8a896215c544cb76', '99c2ee7b882749e597bcd62385f368fb', '', '', 'SQL按钮', 'Admin', '', '', '', 400, 3, '1002003', 'sys.menu.sql_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-11 09:41:47', '2024-05-10 20:41:21', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('f1ef824156c0402c90189d58afb1613e', '8231a369712e4f8f8ac09fce232cd034', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'sys.temp.file.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('f3cefcd649ad457597634e7ff08e49fc', '58d47947a01342e5b5e62447d1b9feb1', '', '', '导入', 'Admin', '', '', '', 300, 2, '1002003', 'course.import', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:13', '2025-05-27 02:59:13', NULL, NULL, 'F', NULL, '2025-05-27 02:59:13', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('f42b249ccfd44fdcbc2dba48a308c1f6', '0444cd2c01584f0687264b6205536691', '', '', '修改', 'Admin', '', '', '', 200, 3, '1002003', 'sys.data.role.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-07-09 19:19:11', '2025-03-17 12:00:35', NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('f55b445c96c44fe89f9389308a29c8f4', 'ecab878018034cfda49767bc4a66b388', '', '', '修改', 'Admin', '', '', '', 200, 4, '1002003', 'meta.tag.bind.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('f636184afaf14d05932b30f3efb62510', '61a13f97f0264bf88b095edfe01e8ff6', '', '', '导入', 'Admin', '', '', '', 700, 3, '1002003', 'meta.category.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('f67e9b892ef043c9adaa615913c2e134', 'aa6df607a69f4100abbfb77016dc7a9a', '', '', '修改', 'Admin', '', '', '', 200, 2, '1002003', 'user.info.update', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('f760bd16193b48b89ec2b4f2429aa6df', '58d47947a01342e5b5e62447d1b9feb1', '', '', '导出', 'Admin', '', '', '', 300, 2, '1002003', 'course.export', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:13', '2025-05-27 02:59:13', NULL, NULL, 'F', NULL, '2025-05-27 02:59:13', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('f83cd3e0e78d4cab9dbdd7e167eec07a', '61a13f97f0264bf88b095edfe01e8ff6', '', '', '新增', 'Admin', '', '', '', 100, 3, '1002003', 'meta.category.create', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('fa0c65ad783d4bf9b919a6db02ef1428', '99c2ee7b882749e597bcd62385f368fb', '', '', '删除菜单', 'Admin', '', '', '', 300, 3, '1002003', 'sys.menu.delete_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-08-31 16:28:57', '2024-05-10 20:41:18', 1, 1, 'F', NULL, NULL, 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('fe471e2409a7440da90cfc8acd86a1a5', '58d47947a01342e5b5e62447d1b9feb1', '', '', '修改', 'Admin', '', '', '', 300, 2, '1002003', 'course.update', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:13', '2025-05-27 02:59:13', NULL, NULL, 'F', NULL, '2025-05-27 02:59:13', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('ff1c9f60bb9548d6b9251d166c644158', 'c62a45cc9d3c4fa69c240fde6ae05b70', '', '', '新增', 'Admin', '', '', '', 300, 2, '1002003', 'topic.create', 'F', 'F', 'F', 'F', 'F', 'F', '2025-05-27 02:59:19', '2025-05-27 02:59:19', NULL, NULL, 'F', NULL, '2025-05-27 02:59:19', 'F', 'GLOWXQ');
INSERT INTO `sys_menu` VALUES ('ffe360deec60413b9b9a7df456bcf665', '392a723082bc4a2082ff4f76de40e748', '', '', '删除', 'Admin', '', '', '', 300, 3, '1002003', 'topic.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F', 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '链路追踪ID',
  `span_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '业务模块跟踪ID',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户名称',
  `ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'ip',
  `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '方法',
  `uri` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '接口',
  `header` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求头',
  `module` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '模块名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '操作描述',
  `param` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '参数',
  `request` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '请求参数json',
  `response` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '响应参数json',
  `error` tinyint NOT NULL DEFAULT -1 COMMENT '存在错误',
  `business_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '操作类型',
  `error_message` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '错误信息',
  `cost_time` bigint NOT NULL DEFAULT -1 COMMENT '操作耗时（ms）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户id',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1252 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '简介',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除与否',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `is_lock` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否锁定',
  `permissions` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '标识，唯一',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', '', 'F', '2024-05-10 21:28:31', '2025-03-17 12:00:34', NULL, NULL, 'T', 'admin', 'GLOWXQ');
INSERT INTO `sys_role` VALUES (2, '字典管理', '', 'T', '2024-05-10 21:52:39', '2025-03-17 12:00:34', NULL, NULL, 'F', 'dict_menu', 'GLOWXQ');
INSERT INTO `sys_role` VALUES (3, '教师统计', '', 'T', '2024-05-10 21:53:15', '2025-03-17 12:00:34', NULL, NULL, 'F', 'teacher_statics_menu', 'GLOWXQ');
INSERT INTO `sys_role` VALUES (4, '学生', '', 'F', '2025-03-30 04:22:30', '2025-03-30 04:22:52', 1, 1, 'F', 'student', 'GLOWXQ');
INSERT INTO `sys_role` VALUES (6, '教师', '', 'F', '2025-04-18 15:15:14', '2025-04-18 15:15:14', 1, 1, 'F', 'teacher', 'GLOWXQ');
INSERT INTO `sys_role` VALUES (7, '测试', '', 'T', '2025-05-11 00:02:29', '2025-05-11 00:02:29', 1, 1, 'F', 'test', 'GLOWXQ');
INSERT INTO `sys_role` VALUES (8, '校长', '', 'F', '2025-05-27 01:18:39', '2025-06-28 13:42:13', 1, 1, 'F', 'oj_admin', 'GLOWXQ');
INSERT INTO `sys_role` VALUES (9, '系统管理员', '', 'F', '2025-06-06 15:13:13', '2025-08-04 14:33:33', 1, 1, 'F', 'busines_admin', 'GLOWXQ');
INSERT INTO `sys_role` VALUES (10, '测试数据权限', '', 'T', '2025-06-22 15:42:31', '2025-06-24 16:24:55', 9, 1, 'F', 'test', 'GLOWXQ');
INSERT INTO `sys_role` VALUES (11, '教育局长', '114514', 'T', '2025-07-14 22:48:38', '2025-07-14 22:56:23', 1, 1, 'F', '领导', 'GLOWXQ');
INSERT INTO `sys_role` VALUES (12, 's阿德撒旦', '12', 'T', '2025-07-14 23:04:11', '2025-07-14 23:04:11', 1, 1, 'F', '1212', 'GLOWXQ');
INSERT INTO `sys_role` VALUES (13, '21额2', '', 'T', '2025-07-14 23:04:17', '2025-07-14 23:04:17', 1, 1, 'F', ' 的撒旦', 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '系统角色-菜单ID',
  `menu_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'sys_menu_id （菜单表）',
  `role_id` bigint NULL DEFAULT NULL COMMENT 'sys_role_id （角色表）',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2879 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统角色-菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (53, 'dcb6aabcd910469ebf3efbc7e43282d4', 2, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (54, '4f39ef0fd2f748f6ab7d6d20d98bc4af', 2, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (55, '818cc6e1889d46579525ad8ab921eeb8', 2, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (56, '9830d86487184961b90fc527c9604720', 2, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (57, '81647226a2d047e8ab0b70472350ee69', 2, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (58, '97f11d74c98047ba80f011a3da9d882c', 2, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (59, '1a86a9d2b3ca49439277fff9f499c7cd', 2, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (60, '88b2e5def2ff474fa8bf3537d4a2fe5b', 2, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (61, '85b54322630f43a39296488a5e76ba16', 3, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (62, 'cb3500315dba4c2d83e4d92edf36dff7', 3, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (63, '7391f12ad51049c2b86d231d39708c71', 3, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (64, '73d312f4fa8949ddba3d9807c0c56f00', 3, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (65, '91ccb13b5c174583803a4c492a5dfdb6', 3, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (66, '8061d8e79be744bf91b7b438f8e8e887', 3, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1015, '0f98b89c67e54cb0bcff2b56aa98832f', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1016, '8255bac5eae748a0a8500167963b3e00', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1017, '330a1a0a857c4ad1a95327db5134e420', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1018, 'e91eeaea8f1546d3921839469fe247b6', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1019, '686a5522b0334d4da51aa15b3fd1a303', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1020, '30942929802f41cc850722c78db089e7', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1021, '5b5fb3748c6a4ed5a4dda3877508c3a7', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1022, '4f39ef0fd2f748f6ab7d6d20d98bc4af', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1023, '7a4544831af34e69aa73148bf84b9924', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1024, 'b428eba3f9a34025a46c394df5390b88', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1025, '8fd6721941494fd5bbe16bec82b235be', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1026, '006bdbacd71a481f88b6acf895529acd', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1027, '19f5e5ad4ebd421eb24a7a1158c68594', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1028, '392a723082bc4a2082ff4f76de40e748', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1029, 'dee6ca209d9d4178833bb9abd814f1eb', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1030, '0f23ebe1a9914e769b75e6a797ea006f', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1031, 'ffe360deec60413b9b9a7df456bcf665', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1032, '1d4447bd66044716b8c107f3ecb91348', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1033, 'a58f8d66c888428fbb015858abc6df3c', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1034, '2eb3e960a3b7490794f5ecab6da92577', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1035, 'dc45662ca9594624acc22b2838ba22ba', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1036, '3c06236312f1486a90e3f513619445e8', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1037, 'c5acb6a80c9e4f998d838a11075022c7', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1038, 'afe01b660aad4660b30ace1fa3c2c1d2', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1039, 'a309b954fd4d44a480663fe59901f699', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1040, '153c1c93519d480eb838f0199038b0e3', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1041, '63f26741a1c24977bf4428161816f0a5', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1042, '651182e3348b4a108cdd6eb27f973fc6', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1043, '4c5256d0d11d41399fabace89776387f', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1044, 'c1f1ef5e0145488b9023e929987c1e01', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1045, '3d3bbe793e8d4179b4b489d2adc2accc', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1046, '22aa1a83a5ae453d86f31d466db4f2ed', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1047, 'e328544f4cdb4354a189062b7831bf62', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1048, '7b7adca3a6224e8da36711e3ea00df71', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1049, '078abe736ceb4c1384e048db7ba6d03c', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1050, '7eda4f3cad474e23a5314b546b48e8d5', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1051, '67717296123d48008e2a842778917446', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1052, '67fe93780f8441c5859ac21f85145622', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1053, 'ecab878018034cfda49767bc4a66b388', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1054, '3d26ccc3458948218bc0c605e77e024d', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1055, 'f55b445c96c44fe89f9389308a29c8f4', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1056, 'd14d69764b0149b5a6b43c8cb5400dbe', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1057, '79f853ebf2b34983b72e27f1ba6e38fb', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1058, '3fcb96a6a9b44a5e8cf64ffa6f3e8b8f', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1059, '02e67d318684498c8719906dade80cd7', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1060, '428ed8f76fce4450ab73595d6814df31', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1061, '6b91dcd5272a4467a49421482b12f9c1', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1062, 'b5e7aa0270d94764816b29ed05fd99ee', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1063, '84915c51fcf546f88dd946fbd14b74bd', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1064, '1fdc9d0ab6da4591921f75655a05c9bb', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1065, '6600c804453d4437a263f218014b5619', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1066, '4a92e91a83b04a768f714759deaad7e4', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1067, '8d1733a9550943fcbabb3862d44e5317', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1068, '8d35a4ee75ef4379b0206d14b170ae79', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1069, '57cc50a4261f4190a60a199b6ad4428b', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1070, '1e2a9a6364d4445b80ad1c1603e64cde', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1071, '6977359ec55c40aa80b908fdd323148f', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1072, 'b7ff08a48e6343c3be6696f3512c15e9', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1073, '2c4749c308084dfd8ce3906c5cdeda23', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1074, 'a3c34b69f7be4e3299a2dfce6bf98bf4', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1075, '92a7fe55d18d49888974e6b1909332a0', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1076, '58d47947a01342e5b5e62447d1b9feb1', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1077, 'c62a45cc9d3c4fa69c240fde6ae05b70', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1078, 'e0291f59e82a42ffb7899e4b6174cf94', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1079, '41b54cf19c584995bfdc08acf411e18d', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1080, '8d89a35c15f74f9dbec6397098b15c05', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1081, '88b2e5def2ff474fa8bf3537d4a2fe5b', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1082, '140c9ed43ef54542bbcdde8a5d928400', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1083, 'c6dd479d5b304731be403d7551c60d70', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1084, 'dcb6aabcd910469ebf3efbc7e43282d4', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1085, '29d33eba6b73420287d8f7e64aea62b3', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1086, '8354d626cc65487594a7c38e98de1bad', 7, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1673, '8ece44129e8047e7b492ab42d32aff8e', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1674, '88b2e5def2ff474fa8bf3537d4a2fe5b', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1675, '99c2ee7b882749e597bcd62385f368fb', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1676, '9338bf2f57984825bc227bb618f9db81', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1677, '05194ef5fa7a4a308a44f6f5c6791c3a', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1678, 'fa0c65ad783d4bf9b919a6db02ef1428', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1679, 'ee36ad68586e42fa8a896215c544cb76', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1680, '1f2caf3426aa400f92d3673d359b258d', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1681, 'dcb6aabcd910469ebf3efbc7e43282d4', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1682, '4f39ef0fd2f748f6ab7d6d20d98bc4af', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1683, '818cc6e1889d46579525ad8ab921eeb8', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1684, '9830d86487184961b90fc527c9604720', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1685, '81647226a2d047e8ab0b70472350ee69', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1686, '97f11d74c98047ba80f011a3da9d882c', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1687, '1a86a9d2b3ca49439277fff9f499c7cd', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1688, '7a4544831af34e69aa73148bf84b9924', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1689, '29d33eba6b73420287d8f7e64aea62b3', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1690, 'ede76f5e60b640aa9de2ba7216b90ceb', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1691, '3a54d488132b4331bf3cd5e6d86ffcf4', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1692, 'b428eba3f9a34025a46c394df5390b88', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1693, '9e731ff422184fc1be2022c5c985735e', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1694, '6c46fd01faf042fc9dd4a9c9b9ef2c5a', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1695, '5b33ac3d630543d09d1388fae4d13fc0', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1696, '8d0b8b57a58e41a5a5e840cc2b3703f4', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1697, '0444cd2c01584f0687264b6205536691', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1698, '8d92cf6f2f3248569d5dd6cb6b958d7c', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1699, 'f42b249ccfd44fdcbc2dba48a308c1f6', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1700, '3f555e4a01174a1d9b29be439668e32f', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1701, 'c4896e8735a745bda9b47ecaf50f46f2', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1702, '2868079355ce4b6c985b1b746dbb0952', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1703, '49c75878b4d445f8be5f69e21e18b70d', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1704, 'c55de3135b864579bda79c279f4129a9', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1705, '8231a369712e4f8f8ac09fce232cd034', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1706, 'e931c84b8bc945a7b6ba2d58c8a93afc', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1707, '87a26b76daad47c2a12c470605563c4a', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1708, 'f1ef824156c0402c90189d58afb1613e', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1709, '1d291d738b2b45fb95afd6715db84c47', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1710, '3e6d2f54012542e08e39f05cb80ee8c6', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1711, '543f0dfa90374a62b8326f138f5e6b4f', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1712, 'd938e91a766a45fda8cae24aaf0cc3f4', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1713, 'da1b46db642f42978f83ed5eb34870ce', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1714, '0e529e8a9dbf450898b695e051c36d48', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1715, 'e7f8a72021b848f7805051260be19314', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1716, 'c6dd479d5b304731be403d7551c60d70', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1717, 'df2894b4c06e47cab84142d81edc494d', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1718, '0933b165ffc14d558e8de43ccb6687f6', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1719, '445b73dda9a34ad681d2705a7abcf2f6', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1720, '5b5fb3748c6a4ed5a4dda3877508c3a7', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1721, '6b9c7b394ebf43f59398dd1588090f3d', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1722, 'a299997c3af6404bb316b01010752164', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1723, '906a445fdf3f4518bd80dd99812c2d26', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1724, 'e17720533d7a447dadc7fcff9b6f5e7e', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1725, '892bf6bacb244f97aaf3bccb25243bdb', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1726, 'bbd755c332e7435bb8167c6ee007fcaa', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1727, '8354d626cc65487594a7c38e98de1bad', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1728, '8fd6721941494fd5bbe16bec82b235be', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1729, '006bdbacd71a481f88b6acf895529acd', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1730, '3ba9407560a1490583fefa10b22bc74f', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1731, '19f5e5ad4ebd421eb24a7a1158c68594', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1732, '392a723082bc4a2082ff4f76de40e748', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1733, 'dee6ca209d9d4178833bb9abd814f1eb', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1734, '0f23ebe1a9914e769b75e6a797ea006f', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1735, 'ffe360deec60413b9b9a7df456bcf665', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1736, '1d4447bd66044716b8c107f3ecb91348', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1737, 'a58f8d66c888428fbb015858abc6df3c', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1738, '2eb3e960a3b7490794f5ecab6da92577', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1739, 'dc45662ca9594624acc22b2838ba22ba', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1740, '3c06236312f1486a90e3f513619445e8', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1741, 'c5acb6a80c9e4f998d838a11075022c7', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1742, 'afe01b660aad4660b30ace1fa3c2c1d2', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1743, 'a309b954fd4d44a480663fe59901f699', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1744, '153c1c93519d480eb838f0199038b0e3', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1745, '63f26741a1c24977bf4428161816f0a5', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1746, '651182e3348b4a108cdd6eb27f973fc6', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1747, '4c5256d0d11d41399fabace89776387f', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1748, 'c1f1ef5e0145488b9023e929987c1e01', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1749, '3d3bbe793e8d4179b4b489d2adc2accc', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1750, '22aa1a83a5ae453d86f31d466db4f2ed', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1751, '02e67d318684498c8719906dade80cd7', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1752, '428ed8f76fce4450ab73595d6814df31', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1753, '6b91dcd5272a4467a49421482b12f9c1', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1754, 'b5e7aa0270d94764816b29ed05fd99ee', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1755, '84915c51fcf546f88dd946fbd14b74bd', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1756, '1fdc9d0ab6da4591921f75655a05c9bb', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1757, '6600c804453d4437a263f218014b5619', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1758, '4a92e91a83b04a768f714759deaad7e4', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1759, '8d1733a9550943fcbabb3862d44e5317', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1760, '8d35a4ee75ef4379b0206d14b170ae79', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1761, '57cc50a4261f4190a60a199b6ad4428b', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1762, '1e2a9a6364d4445b80ad1c1603e64cde', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1763, '686c9a5af6c949789c10a88c86659600', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1764, 'e328544f4cdb4354a189062b7831bf62', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1765, '7b7adca3a6224e8da36711e3ea00df71', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1766, '078abe736ceb4c1384e048db7ba6d03c', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1767, '7eda4f3cad474e23a5314b546b48e8d5', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1768, '67717296123d48008e2a842778917446', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1769, '67fe93780f8441c5859ac21f85145622', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1770, 'ecab878018034cfda49767bc4a66b388', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1771, '3d26ccc3458948218bc0c605e77e024d', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1772, 'f55b445c96c44fe89f9389308a29c8f4', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1773, 'd14d69764b0149b5a6b43c8cb5400dbe', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1774, '79f853ebf2b34983b72e27f1ba6e38fb', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1775, '3fcb96a6a9b44a5e8cf64ffa6f3e8b8f', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1776, '700e6977068d46bd94052d526ba93a3a', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1777, '47dd1145fb50415cb88920ba8140ce7c', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1778, 'bc0bac502da2489488d3ef79214b9d8b', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1779, '9d3f218f8ecb429e8e1c82f905258aa5', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1780, '9c7e4011cee74468a317afdd9b9d16cd', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1781, '53cd98d9eac24efa96f399465805ab47', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1782, '6977359ec55c40aa80b908fdd323148f', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1783, 'b7ff08a48e6343c3be6696f3512c15e9', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1784, '1a23d7802570408583ab7c5f41a72e06', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1785, '51ec495814b94820ba770504b4bd0a90', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1786, '5e8b043d1f5943d7af2553680f3c912a', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1787, '8d718a96f719413aae242608cf02ab83', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1788, 'c987327b5c9d4bffa583dc8d993e639a', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1789, '2c4749c308084dfd8ce3906c5cdeda23', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1790, '2d38167ef90f4992aca71aae2daa0f06', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1791, '410574ff78234277b3b5aaafcc3f9177', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1792, '6894c7069f4649b2b55783a1ec0e9032', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1793, 'bb3e190bf9a6485899881ea67d27f2b6', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1794, 'e80a9ddc25d54fcd88f9ac3bbfaf0ccd', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1795, 'a3c34b69f7be4e3299a2dfce6bf98bf4', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1796, '220ca44050a745678b378733443ed794', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1797, '336771de588d47349f62be3533f46839', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1798, '91b61d600a45429690da6d187f60bf2c', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1799, 'aef79557604b432fb8520cbb1a969bed', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1800, 'e88e9dcd26b44a5ea2bb87081b066cd0', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1801, '92a7fe55d18d49888974e6b1909332a0', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1802, '049b16c3d77544bdaab9fa11d1f7f731', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1803, '1545ff4141254fba81c19b45d43bcc62', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1804, '500cc8b7ccfe4185a21069ecfafc329a', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1805, 'd7b3e978b364421f916b5ac470121ef9', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1806, 'e27c299ec9e84ed9b028c5d6e40e2cf5', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1807, '58d47947a01342e5b5e62447d1b9feb1', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1808, '211faa0ac79d4dfeb4391f579310605e', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1809, '8cbed332abed419bb718a454f9352d0a', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1810, 'f3cefcd649ad457597634e7ff08e49fc', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1811, 'f760bd16193b48b89ec2b4f2429aa6df', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1812, 'fe471e2409a7440da90cfc8acd86a1a5', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1813, 'c62a45cc9d3c4fa69c240fde6ae05b70', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1814, '15466ed7598c4808b9bd108c2a98b6ba', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1815, '2d3d67779f084b84a55b4ddbb342ac92', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1816, '62284b72c4f444a2b10269f7db58d736', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1817, 'badc7ff787e74a988d0954c9f70fc619', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1818, 'ff1c9f60bb9548d6b9251d166c644158', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1819, 'e0291f59e82a42ffb7899e4b6174cf94', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1820, '41b54cf19c584995bfdc08acf411e18d', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1821, '20fa91e12d2c4e0386b89a8b1692cd2e', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1822, '5acaae87bac84de3aace33dd1d35455e', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1823, '9488df8c01074b339332e9a97526bc0d', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1824, 'a6c6199be67d4feeb8f980b04d1255ab', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1825, 'e0215e3497854df3a5a182992b7d60d8', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1826, '8d89a35c15f74f9dbec6397098b15c05', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1827, '4d2a08f8c7f440a3b28c32d28a4db98a', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1828, '6a74a4452db24b7193f06f8aff14a1f6', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1829, 'a56587ece10847c09590ab216b963426', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1830, 'b2518c77e20e46e7a4d71ddb6a7c090c', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (1831, 'e59d99537cce410087f3ea631e5941eb', 10, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2244, '5b5fb3748c6a4ed5a4dda3877508c3a7', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2245, 'a299997c3af6404bb316b01010752164', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2246, '906a445fdf3f4518bd80dd99812c2d26', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2247, '3c06236312f1486a90e3f513619445e8', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2248, 'c5acb6a80c9e4f998d838a11075022c7', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2249, 'afe01b660aad4660b30ace1fa3c2c1d2', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2250, '651182e3348b4a108cdd6eb27f973fc6', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2251, '4c5256d0d11d41399fabace89776387f', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2252, 'c1f1ef5e0145488b9023e929987c1e01', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2253, '428ed8f76fce4450ab73595d6814df31', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2254, '6b91dcd5272a4467a49421482b12f9c1', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2255, 'b5e7aa0270d94764816b29ed05fd99ee', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2256, '4a92e91a83b04a768f714759deaad7e4', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2257, '8d1733a9550943fcbabb3862d44e5317', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2258, '8d35a4ee75ef4379b0206d14b170ae79', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2259, '7b7adca3a6224e8da36711e3ea00df71', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2260, '078abe736ceb4c1384e048db7ba6d03c', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2261, '7eda4f3cad474e23a5314b546b48e8d5', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2262, '3d26ccc3458948218bc0c605e77e024d', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2263, 'f55b445c96c44fe89f9389308a29c8f4', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2264, 'd14d69764b0149b5a6b43c8cb5400dbe', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2265, '6977359ec55c40aa80b908fdd323148f', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2266, '51ec495814b94820ba770504b4bd0a90', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2267, '5e8b043d1f5943d7af2553680f3c912a', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2268, 'c987327b5c9d4bffa583dc8d993e639a', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2269, '2d38167ef90f4992aca71aae2daa0f06', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2270, 'bb3e190bf9a6485899881ea67d27f2b6', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2271, 'e80a9ddc25d54fcd88f9ac3bbfaf0ccd', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2272, '336771de588d47349f62be3533f46839', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2273, 'aef79557604b432fb8520cbb1a969bed', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2274, 'e88e9dcd26b44a5ea2bb87081b066cd0', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2275, '049b16c3d77544bdaab9fa11d1f7f731', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2276, '500cc8b7ccfe4185a21069ecfafc329a', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2277, 'e27c299ec9e84ed9b028c5d6e40e2cf5', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2278, '211faa0ac79d4dfeb4391f579310605e', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2279, '8cbed332abed419bb718a454f9352d0a', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2280, 'fe471e2409a7440da90cfc8acd86a1a5', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2281, '2d3d67779f084b84a55b4ddbb342ac92', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2282, '62284b72c4f444a2b10269f7db58d736', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2283, 'ff1c9f60bb9548d6b9251d166c644158', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2284, '20fa91e12d2c4e0386b89a8b1692cd2e', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2285, '5acaae87bac84de3aace33dd1d35455e', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2286, '9488df8c01074b339332e9a97526bc0d', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2287, '4d2a08f8c7f440a3b28c32d28a4db98a', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2288, '6a74a4452db24b7193f06f8aff14a1f6', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2289, 'e59d99537cce410087f3ea631e5941eb', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2290, 'e7f8a72021b848f7805051260be19314', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2291, 'c6dd479d5b304731be403d7551c60d70', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2292, '6b9c7b394ebf43f59398dd1588090f3d', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2293, '2eb3e960a3b7490794f5ecab6da92577', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2294, 'dc45662ca9594624acc22b2838ba22ba', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2295, '63f26741a1c24977bf4428161816f0a5', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2296, '02e67d318684498c8719906dade80cd7', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2297, '6600c804453d4437a263f218014b5619', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2298, '686c9a5af6c949789c10a88c86659600', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2299, 'e328544f4cdb4354a189062b7831bf62', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2300, 'ecab878018034cfda49767bc4a66b388', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2301, 'b7ff08a48e6343c3be6696f3512c15e9', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2302, '2c4749c308084dfd8ce3906c5cdeda23', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2303, 'a3c34b69f7be4e3299a2dfce6bf98bf4', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2304, '92a7fe55d18d49888974e6b1909332a0', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2305, '58d47947a01342e5b5e62447d1b9feb1', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2306, 'c62a45cc9d3c4fa69c240fde6ae05b70', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2307, 'e0291f59e82a42ffb7899e4b6174cf94', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2308, '41b54cf19c584995bfdc08acf411e18d', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2309, '8d89a35c15f74f9dbec6397098b15c05', 8, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2401, '8ece44129e8047e7b492ab42d32aff8e', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2402, '7b7adca3a6224e8da36711e3ea00df71', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2403, '6977359ec55c40aa80b908fdd323148f', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2404, 'c987327b5c9d4bffa583dc8d993e639a', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2405, '2d38167ef90f4992aca71aae2daa0f06', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2406, 'bb3e190bf9a6485899881ea67d27f2b6', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2407, 'aef79557604b432fb8520cbb1a969bed', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2408, 'e88e9dcd26b44a5ea2bb87081b066cd0', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2409, '500cc8b7ccfe4185a21069ecfafc329a', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2410, 'e27c299ec9e84ed9b028c5d6e40e2cf5', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2411, '8cbed332abed419bb718a454f9352d0a', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2412, 'fe471e2409a7440da90cfc8acd86a1a5', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2413, '2d3d67779f084b84a55b4ddbb342ac92', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2414, '62284b72c4f444a2b10269f7db58d736', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2415, 'ff1c9f60bb9548d6b9251d166c644158', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2416, 'e0291f59e82a42ffb7899e4b6174cf94', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2417, '41b54cf19c584995bfdc08acf411e18d', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2418, '8d89a35c15f74f9dbec6397098b15c05', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2419, '2eb3e960a3b7490794f5ecab6da92577', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2420, '686c9a5af6c949789c10a88c86659600', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2421, 'e328544f4cdb4354a189062b7831bf62', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2422, 'b7ff08a48e6343c3be6696f3512c15e9', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2423, '2c4749c308084dfd8ce3906c5cdeda23', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2424, 'a3c34b69f7be4e3299a2dfce6bf98bf4', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2425, '92a7fe55d18d49888974e6b1909332a0', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2426, '58d47947a01342e5b5e62447d1b9feb1', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2427, 'c62a45cc9d3c4fa69c240fde6ae05b70', 6, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2444, '8ece44129e8047e7b492ab42d32aff8e', 4, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2445, '6977359ec55c40aa80b908fdd323148f', 4, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2446, 'b7ff08a48e6343c3be6696f3512c15e9', 4, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2447, '2c4749c308084dfd8ce3906c5cdeda23', 4, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2448, 'a3c34b69f7be4e3299a2dfce6bf98bf4', 4, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2449, '92a7fe55d18d49888974e6b1909332a0', 4, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2450, '58d47947a01342e5b5e62447d1b9feb1', 4, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2451, 'c62a45cc9d3c4fa69c240fde6ae05b70', 4, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2630, '88b2e5def2ff474fa8bf3537d4a2fe5b', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2631, '99c2ee7b882749e597bcd62385f368fb', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2632, '9338bf2f57984825bc227bb618f9db81', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2633, '05194ef5fa7a4a308a44f6f5c6791c3a', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2634, 'fa0c65ad783d4bf9b919a6db02ef1428', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2635, 'ee36ad68586e42fa8a896215c544cb76', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2636, '1f2caf3426aa400f92d3673d359b258d', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2637, '29d33eba6b73420287d8f7e64aea62b3', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2638, 'ede76f5e60b640aa9de2ba7216b90ceb', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2639, '3a54d488132b4331bf3cd5e6d86ffcf4', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2640, 'b428eba3f9a34025a46c394df5390b88', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2641, '9e731ff422184fc1be2022c5c985735e', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2642, '6c46fd01faf042fc9dd4a9c9b9ef2c5a', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2643, '5b33ac3d630543d09d1388fae4d13fc0', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2644, '8d0b8b57a58e41a5a5e840cc2b3703f4', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2645, 'c4896e8735a745bda9b47ecaf50f46f2', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2646, '1d291d738b2b45fb95afd6715db84c47', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2647, 'df2894b4c06e47cab84142d81edc494d', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2648, '0933b165ffc14d558e8de43ccb6687f6', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2649, '6b9c7b394ebf43f59398dd1588090f3d', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2650, 'a299997c3af6404bb316b01010752164', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2651, '906a445fdf3f4518bd80dd99812c2d26', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2652, '2eb3e960a3b7490794f5ecab6da92577', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2653, 'dc45662ca9594624acc22b2838ba22ba', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2654, '3c06236312f1486a90e3f513619445e8', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2655, 'c5acb6a80c9e4f998d838a11075022c7', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2656, 'afe01b660aad4660b30ace1fa3c2c1d2', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2657, '63f26741a1c24977bf4428161816f0a5', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2658, '651182e3348b4a108cdd6eb27f973fc6', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2659, '4c5256d0d11d41399fabace89776387f', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2660, 'c1f1ef5e0145488b9023e929987c1e01', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2661, '02e67d318684498c8719906dade80cd7', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2662, '428ed8f76fce4450ab73595d6814df31', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2663, '6b91dcd5272a4467a49421482b12f9c1', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2664, 'b5e7aa0270d94764816b29ed05fd99ee', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2665, '6600c804453d4437a263f218014b5619', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2666, '4a92e91a83b04a768f714759deaad7e4', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2667, '8d1733a9550943fcbabb3862d44e5317', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2668, '8d35a4ee75ef4379b0206d14b170ae79', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2669, '686c9a5af6c949789c10a88c86659600', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2670, 'e328544f4cdb4354a189062b7831bf62', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2671, '7b7adca3a6224e8da36711e3ea00df71', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2672, '078abe736ceb4c1384e048db7ba6d03c', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2673, '7eda4f3cad474e23a5314b546b48e8d5', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2674, 'ecab878018034cfda49767bc4a66b388', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2675, '3d26ccc3458948218bc0c605e77e024d', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2676, 'f55b445c96c44fe89f9389308a29c8f4', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2677, 'd14d69764b0149b5a6b43c8cb5400dbe', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2678, '700e6977068d46bd94052d526ba93a3a', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2679, '47dd1145fb50415cb88920ba8140ce7c', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2680, 'bc0bac502da2489488d3ef79214b9d8b', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2681, '9d3f218f8ecb429e8e1c82f905258aa5', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2682, '6977359ec55c40aa80b908fdd323148f', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2683, 'b7ff08a48e6343c3be6696f3512c15e9', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2684, '51ec495814b94820ba770504b4bd0a90', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2685, '5e8b043d1f5943d7af2553680f3c912a', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2686, 'c987327b5c9d4bffa583dc8d993e639a', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2687, '2c4749c308084dfd8ce3906c5cdeda23', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2688, '2d38167ef90f4992aca71aae2daa0f06', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2689, 'bb3e190bf9a6485899881ea67d27f2b6', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2690, 'e80a9ddc25d54fcd88f9ac3bbfaf0ccd', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2691, 'a3c34b69f7be4e3299a2dfce6bf98bf4', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2692, '336771de588d47349f62be3533f46839', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2693, 'aef79557604b432fb8520cbb1a969bed', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2694, 'e88e9dcd26b44a5ea2bb87081b066cd0', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2695, '92a7fe55d18d49888974e6b1909332a0', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2696, '049b16c3d77544bdaab9fa11d1f7f731', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2697, '500cc8b7ccfe4185a21069ecfafc329a', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2698, 'e27c299ec9e84ed9b028c5d6e40e2cf5', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2699, '58d47947a01342e5b5e62447d1b9feb1', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2700, '211faa0ac79d4dfeb4391f579310605e', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2701, '8cbed332abed419bb718a454f9352d0a', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2702, 'fe471e2409a7440da90cfc8acd86a1a5', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2703, 'c62a45cc9d3c4fa69c240fde6ae05b70', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2704, '2d3d67779f084b84a55b4ddbb342ac92', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2705, '62284b72c4f444a2b10269f7db58d736', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2706, 'ff1c9f60bb9548d6b9251d166c644158', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2707, 'e0291f59e82a42ffb7899e4b6174cf94', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2708, '41b54cf19c584995bfdc08acf411e18d', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2709, '20fa91e12d2c4e0386b89a8b1692cd2e', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2710, '5acaae87bac84de3aace33dd1d35455e', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2711, '9488df8c01074b339332e9a97526bc0d', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2712, '8d89a35c15f74f9dbec6397098b15c05', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2713, '4d2a08f8c7f440a3b28c32d28a4db98a', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2714, '6a74a4452db24b7193f06f8aff14a1f6', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2715, 'e59d99537cce410087f3ea631e5941eb', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2716, 'd095463d52ac4d2fa8f142cbb18f10df', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2717, 'e7f8a72021b848f7805051260be19314', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2718, 'c6dd479d5b304731be403d7551c60d70', 9, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2719, '8ece44129e8047e7b492ab42d32aff8e', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2720, '88b2e5def2ff474fa8bf3537d4a2fe5b', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2721, '99c2ee7b882749e597bcd62385f368fb', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2722, '9338bf2f57984825bc227bb618f9db81', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2723, '05194ef5fa7a4a308a44f6f5c6791c3a', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2724, 'fa0c65ad783d4bf9b919a6db02ef1428', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2725, 'ee36ad68586e42fa8a896215c544cb76', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2726, '1f2caf3426aa400f92d3673d359b258d', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2727, 'dcb6aabcd910469ebf3efbc7e43282d4', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2728, '4f39ef0fd2f748f6ab7d6d20d98bc4af', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2729, '818cc6e1889d46579525ad8ab921eeb8', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2730, '9830d86487184961b90fc527c9604720', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2731, '81647226a2d047e8ab0b70472350ee69', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2732, '97f11d74c98047ba80f011a3da9d882c', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2733, '1a86a9d2b3ca49439277fff9f499c7cd', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2734, '7a4544831af34e69aa73148bf84b9924', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2735, '29d33eba6b73420287d8f7e64aea62b3', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2736, 'ede76f5e60b640aa9de2ba7216b90ceb', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2737, '3a54d488132b4331bf3cd5e6d86ffcf4', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2738, 'b428eba3f9a34025a46c394df5390b88', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2739, '9e731ff422184fc1be2022c5c985735e', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2740, '6c46fd01faf042fc9dd4a9c9b9ef2c5a', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2741, '5b33ac3d630543d09d1388fae4d13fc0', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2742, '8d0b8b57a58e41a5a5e840cc2b3703f4', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2743, '0444cd2c01584f0687264b6205536691', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2744, '8d92cf6f2f3248569d5dd6cb6b958d7c', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2745, 'f42b249ccfd44fdcbc2dba48a308c1f6', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2746, '3f555e4a01174a1d9b29be439668e32f', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2747, 'c4896e8735a745bda9b47ecaf50f46f2', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2748, '2868079355ce4b6c985b1b746dbb0952', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2749, '49c75878b4d445f8be5f69e21e18b70d', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2750, 'c55de3135b864579bda79c279f4129a9', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2751, '8231a369712e4f8f8ac09fce232cd034', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2752, 'e931c84b8bc945a7b6ba2d58c8a93afc', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2753, '87a26b76daad47c2a12c470605563c4a', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2754, 'f1ef824156c0402c90189d58afb1613e', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2755, '1d291d738b2b45fb95afd6715db84c47', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2756, '3e6d2f54012542e08e39f05cb80ee8c6', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2757, '543f0dfa90374a62b8326f138f5e6b4f', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2758, 'd938e91a766a45fda8cae24aaf0cc3f4', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2759, 'da1b46db642f42978f83ed5eb34870ce', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2760, '0e529e8a9dbf450898b695e051c36d48', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2761, 'e7f8a72021b848f7805051260be19314', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2762, 'c6dd479d5b304731be403d7551c60d70', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2763, 'df2894b4c06e47cab84142d81edc494d', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2764, '0933b165ffc14d558e8de43ccb6687f6', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2765, '445b73dda9a34ad681d2705a7abcf2f6', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2766, '5b5fb3748c6a4ed5a4dda3877508c3a7', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2767, '6b9c7b394ebf43f59398dd1588090f3d', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2768, 'a299997c3af6404bb316b01010752164', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2769, '906a445fdf3f4518bd80dd99812c2d26', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2770, 'e17720533d7a447dadc7fcff9b6f5e7e', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2771, '892bf6bacb244f97aaf3bccb25243bdb', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2772, 'bbd755c332e7435bb8167c6ee007fcaa', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2773, '8354d626cc65487594a7c38e98de1bad', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2774, '8fd6721941494fd5bbe16bec82b235be', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2775, '006bdbacd71a481f88b6acf895529acd', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2776, '3ba9407560a1490583fefa10b22bc74f', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2777, '19f5e5ad4ebd421eb24a7a1158c68594', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2778, '392a723082bc4a2082ff4f76de40e748', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2779, 'dee6ca209d9d4178833bb9abd814f1eb', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2780, '0f23ebe1a9914e769b75e6a797ea006f', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2781, 'ffe360deec60413b9b9a7df456bcf665', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2782, '1d4447bd66044716b8c107f3ecb91348', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2783, 'a58f8d66c888428fbb015858abc6df3c', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2784, '2eb3e960a3b7490794f5ecab6da92577', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2785, 'dc45662ca9594624acc22b2838ba22ba', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2786, '3c06236312f1486a90e3f513619445e8', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2787, 'c5acb6a80c9e4f998d838a11075022c7', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2788, 'afe01b660aad4660b30ace1fa3c2c1d2', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2789, 'a309b954fd4d44a480663fe59901f699', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2790, '153c1c93519d480eb838f0199038b0e3', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2791, '63f26741a1c24977bf4428161816f0a5', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2792, '651182e3348b4a108cdd6eb27f973fc6', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2793, '4c5256d0d11d41399fabace89776387f', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2794, 'c1f1ef5e0145488b9023e929987c1e01', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2795, '3d3bbe793e8d4179b4b489d2adc2accc', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2796, '22aa1a83a5ae453d86f31d466db4f2ed', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2797, '02e67d318684498c8719906dade80cd7', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2798, '428ed8f76fce4450ab73595d6814df31', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2799, '6b91dcd5272a4467a49421482b12f9c1', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2800, 'b5e7aa0270d94764816b29ed05fd99ee', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2801, '84915c51fcf546f88dd946fbd14b74bd', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2802, '1fdc9d0ab6da4591921f75655a05c9bb', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2803, '6600c804453d4437a263f218014b5619', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2804, '4a92e91a83b04a768f714759deaad7e4', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2805, '8d1733a9550943fcbabb3862d44e5317', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2806, '8d35a4ee75ef4379b0206d14b170ae79', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2807, '57cc50a4261f4190a60a199b6ad4428b', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2808, '1e2a9a6364d4445b80ad1c1603e64cde', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2809, '686c9a5af6c949789c10a88c86659600', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2810, 'e328544f4cdb4354a189062b7831bf62', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2811, '7b7adca3a6224e8da36711e3ea00df71', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2812, '078abe736ceb4c1384e048db7ba6d03c', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2813, '7eda4f3cad474e23a5314b546b48e8d5', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2814, '67717296123d48008e2a842778917446', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2815, '67fe93780f8441c5859ac21f85145622', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2816, 'ecab878018034cfda49767bc4a66b388', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2817, '3d26ccc3458948218bc0c605e77e024d', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2818, 'f55b445c96c44fe89f9389308a29c8f4', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2819, 'd14d69764b0149b5a6b43c8cb5400dbe', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2820, '79f853ebf2b34983b72e27f1ba6e38fb', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2821, '3fcb96a6a9b44a5e8cf64ffa6f3e8b8f', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2822, '700e6977068d46bd94052d526ba93a3a', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2823, '47dd1145fb50415cb88920ba8140ce7c', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2824, 'bc0bac502da2489488d3ef79214b9d8b', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2825, '9d3f218f8ecb429e8e1c82f905258aa5', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2826, '9c7e4011cee74468a317afdd9b9d16cd', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2827, '53cd98d9eac24efa96f399465805ab47', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2828, '6977359ec55c40aa80b908fdd323148f', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2829, 'b7ff08a48e6343c3be6696f3512c15e9', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2830, '1a23d7802570408583ab7c5f41a72e06', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2831, '51ec495814b94820ba770504b4bd0a90', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2832, '5e8b043d1f5943d7af2553680f3c912a', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2833, '8d718a96f719413aae242608cf02ab83', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2834, 'c987327b5c9d4bffa583dc8d993e639a', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2835, '2c4749c308084dfd8ce3906c5cdeda23', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2836, '2d38167ef90f4992aca71aae2daa0f06', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2837, '410574ff78234277b3b5aaafcc3f9177', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2838, '6894c7069f4649b2b55783a1ec0e9032', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2839, 'bb3e190bf9a6485899881ea67d27f2b6', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2840, 'e80a9ddc25d54fcd88f9ac3bbfaf0ccd', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2841, 'a3c34b69f7be4e3299a2dfce6bf98bf4', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2842, '220ca44050a745678b378733443ed794', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2843, '336771de588d47349f62be3533f46839', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2844, '91b61d600a45429690da6d187f60bf2c', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2845, 'aef79557604b432fb8520cbb1a969bed', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2846, 'e88e9dcd26b44a5ea2bb87081b066cd0', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2847, '92a7fe55d18d49888974e6b1909332a0', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2848, '049b16c3d77544bdaab9fa11d1f7f731', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2849, '1545ff4141254fba81c19b45d43bcc62', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2850, '500cc8b7ccfe4185a21069ecfafc329a', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2851, 'd7b3e978b364421f916b5ac470121ef9', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2852, 'e27c299ec9e84ed9b028c5d6e40e2cf5', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2853, '58d47947a01342e5b5e62447d1b9feb1', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2854, '211faa0ac79d4dfeb4391f579310605e', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2855, '8cbed332abed419bb718a454f9352d0a', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2856, 'f3cefcd649ad457597634e7ff08e49fc', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2857, 'f760bd16193b48b89ec2b4f2429aa6df', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2858, 'fe471e2409a7440da90cfc8acd86a1a5', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2859, 'c62a45cc9d3c4fa69c240fde6ae05b70', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2860, '15466ed7598c4808b9bd108c2a98b6ba', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2861, '2d3d67779f084b84a55b4ddbb342ac92', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2862, '62284b72c4f444a2b10269f7db58d736', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2863, 'badc7ff787e74a988d0954c9f70fc619', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2864, 'ff1c9f60bb9548d6b9251d166c644158', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2865, 'e0291f59e82a42ffb7899e4b6174cf94', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2866, '41b54cf19c584995bfdc08acf411e18d', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2867, '20fa91e12d2c4e0386b89a8b1692cd2e', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2868, '5acaae87bac84de3aace33dd1d35455e', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2869, '9488df8c01074b339332e9a97526bc0d', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2870, 'a6c6199be67d4feeb8f980b04d1255ab', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2871, 'e0215e3497854df3a5a182992b7d60d8', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2872, '8d89a35c15f74f9dbec6397098b15c05', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2873, '4d2a08f8c7f440a3b28c32d28a4db98a', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2874, '6a74a4452db24b7193f06f8aff14a1f6', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2875, 'a56587ece10847c09590ab216b963426', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2876, 'b2518c77e20e46e7a4d71ddb6a7c090c', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2877, 'e59d99537cce410087f3ea631e5941eb', 1, 'GLOWXQ');
INSERT INTO `sys_role_menu` VALUES (2878, 'd095463d52ac4d2fa8f142cbb18f10df', 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_temp_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_temp_file`;
CREATE TABLE `sys_temp_file`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sys_file_id` bigint NULL DEFAULT NULL COMMENT '文件ID',
  `temp_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '模版名',
  `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '地址',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '逻辑删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模版文件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_temp_file
-- ----------------------------
INSERT INTO `sys_temp_file` VALUES (1, 98, '教师统计模板.xlsx', 'https://minioapi.szadmin.cn/test/tmp/20241216/教师统计 (203323.951).xlsx', '', 'F', 1, '2024-12-16 20:33:12', 1, '2024-12-16 20:33:36', 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_temp_file_history
-- ----------------------------
DROP TABLE IF EXISTS `sys_temp_file_history`;
CREATE TABLE `sys_temp_file_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sys_temp_file_id` bigint NULL DEFAULT NULL COMMENT '模版文件ID',
  `sys_file_id` bigint NULL DEFAULT NULL COMMENT '文件ID',
  `temp_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '模版名',
  `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '地址',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模版文件历史表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_temp_file_history
-- ----------------------------
INSERT INTO `sys_temp_file_history` VALUES (1, 1, 97, '教师统计模板.xlsx', 'https://minioapi.szadmin.cn/test/tmp/20241216/教师统计 (43) (203252.669).xlsx', '', 1, '2024-12-16 20:33:12', NULL, NULL, 'GLOWXQ');
INSERT INTO `sys_temp_file_history` VALUES (2, 1, 98, '教师统计模板.xlsx', 'https://minioapi.szadmin.cn/test/tmp/20241216/教师统计 (203323.951).xlsx', '', 1, '2024-12-16 20:33:36', NULL, NULL, 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `pwd` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '真实姓名',
  `nickname` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `openid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '微信openid',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '地址',
  `sex` tinyint(1) NULL DEFAULT NULL COMMENT '性别(0 未知 1 男 2 女)',
  `birthday` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生日',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  `age` int NULL DEFAULT NULL COMMENT '年龄，--废弃，以生日为主',
  `id_card` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '身份证',
  `data_scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'All' COMMENT '数据权限',
  `email` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱地址',
  `account_status_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户状态 (如 冻结；禁言；正常。 关联字典表account_status)',
  `user_tag_cd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '标签（自定义关联到字典表）',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最近一次登录时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `sys_user_username_uindex`(`username` ASC) USING BTREE,
  INDEX `create_time_index`(`create_time` DESC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 74 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, '13667700000', '$2a$10$qH7b6EDAHbAqwz7ygZwG3OeMytp/Q4xzj3IIar24iQTyVo56VN62y', '13667700000', '管理员', 'glowxq', '', '', 0, '2025-06-23', 'https://minioapi.szadmin.cn/test/user/20241216/微信图片_20240420160033.jpg', 0, '', '', 'glowxq@qq.com', '1000001', '1001002', '2024-02-02 13:36:04', '2023-08-18 11:15:10', '2025-06-29 00:51:43', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `sys_user` VALUES (8, '13667700001', '$2a$10$b2wr/PvZNn0Ectx75LTReuT5TRQqviH7X3pbbrmGk4Re5601yi1w2', '13667700001', '学生', '学生', '', '', 0, '2025-06-23', 'https://glowxq-oj-api.oss-cn-guangzhou.aliyuncs.com/local/png/2025/06/28/Snipaste_2025-06-25_20-42-39.png', 0, '', '', 'kuangshanshan@qq.com', '1000001', '1001003', NULL, '2025-03-30 16:27:39', '2025-06-28 23:02:03', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `sys_user` VALUES (9, '13667700002', '$2a$10$ipQxcX4ULbSA1lFkCufOT./vf3WMQUxFhgaUxvaIpikMCQYa0VvqG', '13667700002', '校长', '校长', '', '', 0, '2025-06-23', 'https://glowxq-oj-api.oss-cn-guangzhou.aliyuncs.com/local/png/2025/06/28/Snipaste_2025-06-25_20-42-39.png', 0, '', '', 'glwoxq@qq.com', '1000001', '1001003', NULL, '2025-05-27 01:18:06', '2025-06-28 23:02:07', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `sys_user` VALUES (10, '13667700003', '$2a$10$qIpdb/6bwwehM4j1jIoatuG.fa84O/0AYgDR/1qBmcBJEovSoVrWW', '13667700003', '老师', '老师', '', '', 0, '2025-06-23', 'https://glowxq-oj-api.oss-cn-guangzhou.aliyuncs.com/local/png/2025/06/28/Snipaste_2025-06-25_20-42-39.png', 0, '', '', 'glowxq@qq.com', '1000001', '1001003', NULL, '2025-05-27 01:21:28', '2025-06-29 16:39:04', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `sys_user` VALUES (54, '13667700004', '$2a$10$6j3ugmjhWqvVdq3sWn2OJeSeWOjONYsSzGZFfWgK./Oy/Wnvva.Bu', '13667700004', '系统管理员', '系统管理员', '', '', 0, '1996-01-22', 'https://glowxq-oj-api.oss-cn-guangzhou.aliyuncs.com/local/png/2025/06/28/Snipaste_2025-06-25_20-42-39.png', 0, '', '', '孙八616@mock.io', '1000001', '1001003', NULL, '2025-06-23 16:31:41', '2025-06-28 23:02:16', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_user_data_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_data_role`;
CREATE TABLE `sys_user_data_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户-数据角色关联ID',
  `role_id` bigint NULL DEFAULT NULL COMMENT '数据角色ID (sys_data_role_id)',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID(sys_user_id)',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `sys_user_data_role_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `sys_user_data_role_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户-数据角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_data_role
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_dept`;
CREATE TABLE `sys_user_dept`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户-部门关系ID',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID (sys_dept_id)',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID(sys_user_id)',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `sys_user_dept_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `sys_user_dept_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 61 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户-部门关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_dept
-- ----------------------------
INSERT INTO `sys_user_dept` VALUES (42, -1, 8, 'GLOWXQ');
INSERT INTO `sys_user_dept` VALUES (43, -1, 9, 'GLOWXQ');
INSERT INTO `sys_user_dept` VALUES (45, 7, 54, 'GLOWXQ');
INSERT INTO `sys_user_dept` VALUES (46, -1, 1, 'GLOWXQ');
INSERT INTO `sys_user_dept` VALUES (48, -1, 10, 'GLOWXQ');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户-角色关联ID',
  `role_id` bigint NULL DEFAULT NULL COMMENT '角色ID (sys_role_id)',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID(sys_user_id)',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `sys_user_role_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `sys_user_role_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户-角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (54, 4, 8, 'GLOWXQ');
INSERT INTO `sys_user_role` VALUES (55, 8, 9, 'GLOWXQ');
INSERT INTO `sys_user_role` VALUES (57, 9, 54, 'GLOWXQ');
INSERT INTO `sys_user_role` VALUES (58, 1, 1, 'GLOWXQ');
INSERT INTO `sys_user_role` VALUES (61, 6, 10, 'GLOWXQ');

-- ----------------------------
-- Table structure for t_db_version
-- ----------------------------
DROP TABLE IF EXISTS `t_db_version`;
CREATE TABLE `t_db_version`  (
  `installed_rank` int NOT NULL,
  `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `script` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `checksum` int NULL DEFAULT NULL,
  `installed_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`installed_rank`) USING BTREE,
  INDEX `t_db_version_s_idx`(`success` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_db_version
-- ----------------------------
INSERT INTO `t_db_version` VALUES (1, '1.1', '20230509 Init DDL', 'SQL', 'V1.1__20230509_Init_DDL.sql', 829744681, 'root', '2025-03-17 12:00:23', 15099, 1, 'GLOWXQ');
INSERT INTO `t_db_version` VALUES (2, '1.2', '20240511 Update DDL', 'SQL', 'V1.2__20240511_Update_DDL.sql', 372977814, 'root', '2025-03-17 12:00:24', 222, 1, 'GLOWXQ');
INSERT INTO `t_db_version` VALUES (3, '1.3', '20240530 Update DDL', 'SQL', 'V1.3__20240530_Update_DDL.sql', 1784154531, 'root', '2025-03-17 12:00:25', 263, 1, 'GLOWXQ');
INSERT INTO `t_db_version` VALUES (4, '1.4', '20240603 Update DDL', 'SQL', 'V1.4__20240603_Update_DDL.sql', 1997095868, 'root', '2025-03-17 12:00:26', 161, 1, 'GLOWXQ');
INSERT INTO `t_db_version` VALUES (5, '1.5', '20240605 Update DDL', 'SQL', 'V1.5__20240605_Update_DDL.sql', 1391658357, 'root', '2025-03-17 12:00:27', 187, 1, 'GLOWXQ');
INSERT INTO `t_db_version` VALUES (6, '1.6', '20240611 Update DDL', 'SQL', 'V1.6__20240611_Update_DDL.sql', 75627697, 'root', '2025-03-17 12:00:27', 215, 1, 'GLOWXQ');
INSERT INTO `t_db_version` VALUES (7, '1.7', '20240624 Update DDL', 'SQL', 'V1.7__20240624_Update_DDL.sql', -404704373, 'root', '2025-03-17 12:00:31', 3689, 1, 'GLOWXQ');
INSERT INTO `t_db_version` VALUES (8, '1.8', '20240726 Update DDL', 'SQL', 'V1.8__20240726_Update_DDL.sql', 2016440390, 'root', '2025-03-17 12:00:32', 227, 1, 'GLOWXQ');
INSERT INTO `t_db_version` VALUES (9, '1.9', '20240811 Update DDL', 'SQL', 'V1.9__20240811_Update_DDL.sql', -1874884231, 'root', '2025-03-17 12:00:33', 276, 1, 'GLOWXQ');
INSERT INTO `t_db_version` VALUES (10, '2.0', '20240813 Update DDL', 'SQL', 'V2.0__20240813_Update_DDL.sql', 1280585246, 'root', '2025-03-17 12:00:34', 436, 1, 'GLOWXQ');
INSERT INTO `t_db_version` VALUES (11, '2.1', '20241014 Update DDL', 'SQL', 'V2.1__20241014_Update_DDL.sql', -2022036290, 'root', '2025-03-17 12:00:36', 1397, 1, 'GLOWXQ');
INSERT INTO `t_db_version` VALUES (12, '2.2', '20241119 Update DDL', 'SQL', 'V2.2__20241119_Update_DDL.sql', 223266239, 'root', '2025-03-17 12:00:36', 123, 1, 'GLOWXQ');
INSERT INTO `t_db_version` VALUES (13, '2.3', '20241125 Update DDL', 'SQL', 'V2.3__20241125_Update_DDL.sql', 243924568, 'root', '2025-03-17 12:00:37', 562, 1, 'GLOWXQ');
INSERT INTO `t_db_version` VALUES (14, '2.4', '20241203 Update DDL', 'SQL', 'V2.4__20241203_Update_DDL.sql', 990805520, 'root', '2025-03-17 12:00:39', 777, 1, 'GLOWXQ');
INSERT INTO `t_db_version` VALUES (15, '2.5', '20250107 Update DDL', 'SQL', 'V2.5__20250107_Update_DDL.sql', -1141433116, 'root', '2025-03-17 12:00:40', 382, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for t_db_version_business
-- ----------------------------
DROP TABLE IF EXISTS `t_db_version_business`;
CREATE TABLE `t_db_version_business`  (
  `installed_rank` int NOT NULL,
  `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `script` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `checksum` int NULL DEFAULT NULL,
  `installed_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`installed_rank`) USING BTREE,
  INDEX `t_db_version_business_s_idx`(`success` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_db_version_business
-- ----------------------------
INSERT INTO `t_db_version_business` VALUES (1, '1', '<< Flyway Baseline >>', 'BASELINE', '<< Flyway Baseline >>', NULL, 'root', '2025-03-17 12:00:41', 0, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for teacher_statistics
-- ----------------------------
DROP TABLE IF EXISTS `teacher_statistics`;
CREATE TABLE `teacher_statistics`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `year` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '统计年限',
  `month` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '统计月份',
  `during_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '统计年月',
  `teacher_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '教师id',
  `teacher_common_type` int NOT NULL COMMENT '讲师区分类型',
  `total_teaching` int NULL DEFAULT NULL COMMENT '授课总数',
  `total_class_count` int NULL DEFAULT NULL COMMENT '服务班次数',
  `total_hours` decimal(10, 2) NULL DEFAULT NULL COMMENT '课时总数',
  `check_status` int NOT NULL DEFAULT 0 COMMENT '核对状态',
  `check_time` datetime NULL DEFAULT NULL COMMENT '核对时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '生成时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `last_sync_time` datetime NULL DEFAULT NULL COMMENT '最近一次同步时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `dept_scope` json NULL COMMENT '部门范围',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '教师统计总览表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of teacher_statistics
-- ----------------------------
INSERT INTO `teacher_statistics` VALUES (22, '2018', '12', '03', '1503', 1000001, 12, 22, 15.00, 1000001, '2024-07-08 10:39:56', '2024-07-08 10:40:16', NULL, '2024-07-08 10:39:57', 'test1 创建记录', 3, NULL, '[4]', 'GLOWXQ');
INSERT INTO `teacher_statistics` VALUES (23, '2019', '12', '03', '111', 1000001, 1, 2, 3.00, 1000001, '2024-07-08 10:41:09', '2024-07-08 10:41:18', NULL, '2024-07-08 10:41:11', 'test1 创建记录', 3, NULL, '[4]', 'GLOWXQ');
INSERT INTO `teacher_statistics` VALUES (24, '2020', '12', '2020-12', '023', 1000001, 1, 1, 1.00, 1000001, '2024-07-08 13:06:55', '2024-07-08 13:07:07', NULL, '2024-07-08 13:06:57', 'test1 创建记录', 3, NULL, '[4]', 'GLOWXQ');
INSERT INTO `teacher_statistics` VALUES (25, '2021', '12', '2021-12', '123', 1000001, 1, 1, 1.00, 1000001, '2024-07-08 13:13:56', '2024-07-08 13:13:59', NULL, NULL, 'test2 创建记录', 4, NULL, '[15]', 'GLOWXQ');
INSERT INTO `teacher_statistics` VALUES (26, '2022', '12', '2022-12', '13123', 1000001, 1, 1, 1.00, 1000001, '2024-07-08 13:15:36', '2024-07-08 13:15:37', '2024-07-08 13:15:46', NULL, 'test3 创建记录', 5, 5, '[15]', 'GLOWXQ');
INSERT INTO `teacher_statistics` VALUES (27, '2099', '12', '12', '123123', 1000001, 1, 1, 1.00, 1000001, NULL, '2024-07-08 13:20:29', NULL, NULL, '管理员创建', 1, NULL, '[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]', 'GLOWXQ');

-- ----------------------------
-- Table structure for tenant
-- ----------------------------
DROP TABLE IF EXISTS `tenant`;
CREATE TABLE `tenant`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户id',
  `tenant_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户编码，唯一标识',
  `tenant_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户名称',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系人手机号',
  `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '联系人邮箱',
  `show` tinyint(1) NOT NULL DEFAULT 0 COMMENT '显示',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '租户秘密',
  `enable` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `expired_time` datetime NOT NULL COMMENT '到期时间',
  `max_user_num` int NOT NULL DEFAULT 100 COMMENT '最大用户数',
  `current_user_num` int NOT NULL DEFAULT 0 COMMENT '当前用户数',
  `logo_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Logo地址',
  `text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '租户文本',
  `system_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '系统名称',
  `home_image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '首页图片地址',
  `theme_color` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '#1890ff' COMMENT '主题色',
  `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '配置信息',
  `custom_domain` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '自定义域名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_code`(`tenant_code` ASC) USING BTREE,
  UNIQUE INDEX `uk_tenant_id`(`tenant_id` ASC) USING BTREE,
  UNIQUE INDEX `tenant_contact_phone_uindex`(`contact_phone` ASC) USING BTREE,
  UNIQUE INDEX `uk_custom_domain`(`custom_domain` ASC) USING BTREE,
  INDEX `idx_created_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_expired_time`(`expired_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '租户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tenant
-- ----------------------------
INSERT INTO `tenant` VALUES (1, 'GLOWXQ', 'GLOWXQ', 'Glowxq-OJ', '吴一一', '19323030408', 'glowxq@qq.com', 1, '', 1, '2099-06-06 16:36:35', 100000, 24, NULL, '', NULL, NULL, '#009688FF', NULL, NULL, '2025-06-06 16:37:02', '2025-07-29 01:03:45', 'F', NULL, NULL);
INSERT INTO `tenant` VALUES (32, 'TENKTFQYRZ', 'TEST123', '测试租户', 'xiaoqiang', '19323030401', 'glowxq@qq.com', 1, '', 1, '2026-06-29 16:14:57', 200, 2, NULL, '', NULL, NULL, '#009688', NULL, NULL, '2025-06-30 00:16:45', '2025-06-30 00:16:45', 'F', NULL, NULL);

-- ----------------------------
-- Table structure for topic
-- ----------------------------
DROP TABLE IF EXISTS `topic`;
CREATE TABLE `topic`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主题名',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主题代码',
  `principal_user_id` bigint NOT NULL COMMENT '负责人ID',
  `principal_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '负责人姓名',
  `password` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主题密码(密码不为空,则打开主题时必须输入正确密码)',
  `description` varchar(2046) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主题描述',
  `time_range_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '时间范围策略',
  `topic_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主题类型',
  `topic_judge_type` int NOT NULL COMMENT '主题测评类型 ACM/OI',
  `sealed_time` int NOT NULL COMMENT '提前封榜(时) -1为全程封榜 0为不封榜',
  `time_limit` int NOT NULL DEFAULT 0 COMMENT '时间限制(分) 超时自动提交',
  `deduction_rate` decimal(10, 0) NOT NULL DEFAULT 0 COMMENT '扣分比例或',
  `punishment_time` int NOT NULL DEFAULT 0 COMMENT '罚时(分)',
  `oi_score_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'OI得分类型 最近得分/最高得分',
  `color` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '#000000' COMMENT '主题颜色',
  `common` tinyint NOT NULL DEFAULT 1 COMMENT '公共主题',
  `home_show` tinyint NOT NULL DEFAULT 0 COMMENT '是否显示',
  `enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '截止时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '主题' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of topic
-- ----------------------------
INSERT INTO `topic` VALUES (1, 'test', 'TOPICR0IT', 2, '姓名1', 'D5WTLH', 'test', 'StrictTimeSubmit', 'Exercise', 0, 3, 2, 0, 2, 'Recent', '#1890FF', 1, 1, 1, '2025-04-16 16:20:32', '2025-04-30 16:20:36', '2025-04-12 16:21:40', '2025-06-07 19:33:53', 'F', 1, 9, 'GLOWXQ');
INSERT INTO `topic` VALUES (3, 'test(复制)(复制)', 'TOPICIB41', 2, '姓名1', 'D5WTLH', 'test', 'StrictTimeView', 'Homework', 0, 3, 2, 0, 2, 'Recent', '#1890FF', 0, 0, 1, '2025-04-12 16:20:32', '2025-04-30 16:20:36', '2025-04-12 19:30:16', '2025-06-07 19:33:53', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic` VALUES (4, 'test(复制)', 'TOPICR0IT', 2, '姓名1', 'D5WTLH', 'test', 'StrictTimeSubmit', 'Exercise', 0, 3, 2, 0, 2, 'Recent', '#1890FF', 0, 0, 1, '2025-04-01 16:20:32', '2025-04-12 16:20:36', '2025-04-13 00:57:47', '2025-06-07 19:33:53', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic` VALUES (5, 'test(复制)(复制)', 'TOPICIB41', 2, '姓名1', 'D5WTLH', '* adfjf\n# asdf ', 'StrictTimeView', 'Homework', 0, 3, 200, 0, 2, 'Recent', '#1890FF', 0, 0, 1, '2025-04-12 16:20:32', '2026-04-24 16:20:36', '2025-05-27 23:50:15', '2025-06-07 19:33:53', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `topic` VALUES (12, '测试比赛', 'TOPIC6215', 1, 'glowxq', 'PWD6215', '# 测试主题6215\n\n这是一个自动生成的测试主题，包含以下内容：\n\n## 主题说明\n- 这是测试数据\n- 请在实际使用前修改\n- 包含基本的markdown格式\n\n## 注意事项\n1. 请注意时间设置\n2. 确认参与班级\n3. 检查题目配置', 'StrictTimeSubmit', 'Contest', 0, 0, 0, 0, 0, 'Recent', '#1890FF', 1, 0, 1, '2025-06-27 17:02:32', '2025-08-31 00:27:21', '2025-06-27 01:03:15', '2025-08-04 00:27:31', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic` VALUES (13, '课后练习', 'TOPICDU6O', 1, '管理员', '123', '111', 'Unrestricted', 'Exercise', 1, 0, 0, 0, 0, 'Highest', '#1890FF', 0, 0, 0, '2025-07-01 00:00:00', '2025-07-16 00:00:00', '2025-06-30 23:51:19', '2025-06-30 23:52:01', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for topic_info
-- ----------------------------
DROP TABLE IF EXISTS `topic_info`;
CREATE TABLE `topic_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `topic_id` bigint NOT NULL COMMENT '主题ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '头像',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '姓名',
  `nick_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '昵称',
  `score` int NOT NULL DEFAULT 0 COMMENT '总得分',
  `ac_num` int NOT NULL DEFAULT 0 COMMENT '总AC数量',
  `submit_num` int NOT NULL DEFAULT 0 COMMENT '提交次数',
  `use_time` int NOT NULL DEFAULT 0 COMMENT '总做题用时(分)',
  `punishment_time` int NOT NULL DEFAULT 0 COMMENT '总罚时(分)',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开始做题',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `auto_submit` tinyint NOT NULL DEFAULT 0 COMMENT '自动提交',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `topic_info_topic_id`(`topic_id` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  CONSTRAINT `topic_info_topic_id` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '主题数据' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of topic_info
-- ----------------------------
INSERT INTO `topic_info` VALUES (2, 1, 1, 'https://minioapi.szadmin.cn/test/user/20241216/微信图片_20240420160033.jpg', 'aaa', '管理员', 0, 0, 0, 0, 0, 'NotStarted', '2025-04-18 15:15:36', NULL, 0, '2025-04-18 15:15:36', '2025-06-07 19:33:54', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_info` VALUES (3, 4, 1, 'https://minioapi.szadmin.cn/test/user/20241216/微信图片_20240420160033.jpg', 'aaa', '管理员', 0, 0, 0, 0, 0, 'NotStarted', '2025-04-18 23:44:46', NULL, 0, '2025-04-18 23:44:46', '2025-06-07 19:33:54', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_info` VALUES (4, 3, 1, 'https://minioapi.szadmin.cn/test/user/20241216/微信图片_20240420160033.jpg', 'aaa', '管理员', 0, 0, 0, 0, 0, 'NotStarted', '2025-04-21 18:53:18', NULL, 0, '2025-04-21 18:53:18', '2025-06-07 19:33:54', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_info` VALUES (5, 4, 5, '', '姓名4', '昵称 3', 0, 0, 0, 0, 0, 'NotStarted', '2025-05-11 00:07:48', NULL, 0, '2025-05-11 00:07:48', '2025-06-07 19:33:54', 'F', 5, 5, 'GLOWXQ');
INSERT INTO `topic_info` VALUES (6, 1, 5, '', '姓名4', '昵称 3', 0, 0, 0, 0, 0, 'NotStarted', '2025-05-11 16:56:00', NULL, 0, '2025-05-11 16:56:00', '2025-06-07 19:33:54', 'F', 5, 5, 'GLOWXQ');
INSERT INTO `topic_info` VALUES (8, 3, 11, '', '学生', '', 0, 0, 0, 0, 0, 'NotStarted', '2025-05-27 05:09:06', NULL, 0, '2025-05-27 05:09:06', '2025-06-07 19:33:54', 'F', 11, 11, 'GLOWXQ');
INSERT INTO `topic_info` VALUES (10, 1, 9, '', '管理员', '', 0, 0, 0, 0, 0, 'NotStarted', '2025-05-27 23:52:27', NULL, 0, '2025-05-27 23:52:27', '2025-06-07 19:33:54', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `topic_info` VALUES (11, 3, 9, '', '管理员', '', 0, 0, 0, 0, 0, 'NotStarted', '2025-05-28 08:06:04', NULL, 0, '2025-05-28 08:06:04', '2025-06-07 19:33:54', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `topic_info` VALUES (21, 4, 9, '', '管理员', '', 0, 0, 0, 0, 0, 'NotStarted', '2025-06-06 01:54:10', NULL, 0, '2025-06-06 01:54:10', '2025-06-07 19:33:54', 'F', 9, 9, 'GLOWXQ');
INSERT INTO `topic_info` VALUES (31, 3, 10, 'https://glowxq-oj-api.oss-cn-guangzhou.aliyuncs.com/local/png/2025/06/28/Snipaste_2025-06-25_20-42-39.png', '老师', '老师', 0, 0, 0, 0, 0, 'NotStarted', '2025-06-28 23:39:54', NULL, 0, '2025-06-28 23:39:54', '2025-06-28 23:39:54', 'F', 10, 10, 'GLOWXQ');
INSERT INTO `topic_info` VALUES (32, 13, 1, 'https://minioapi.szadmin.cn/test/user/20241216/微信图片_20240420160033.jpg', '管理员', 'glowxq', 0, 0, 0, 0, 0, 'NotStarted', '2025-06-30 23:51:29', NULL, 0, '2025-06-30 23:51:29', '2025-06-30 23:51:29', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_info` VALUES (35, 12, 1, '#009688', '管理员', 'glowxq', 0, 0, 3, 0, 0, 'InProgress', '2025-08-04 00:26:10', '2025-08-09 15:10:25', 0, '2025-08-09 15:10:25', '2025-08-09 15:10:25', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_info` VALUES (36, 5, 1, 'https://minioapi.szadmin.cn/test/user/20241216/微信图片_20240420160033.jpg', '管理员', 'glowxq', 0, 0, 0, 0, 0, 'NotStarted', '2025-08-08 12:18:25', NULL, 0, '2025-08-08 12:18:25', '2025-08-08 12:18:25', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for topic_problem
-- ----------------------------
DROP TABLE IF EXISTS `topic_problem`;
CREATE TABLE `topic_problem`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `topic_id` bigint NOT NULL COMMENT '主题ID',
  `problem_id` bigint NOT NULL COMMENT '题目ID',
  `required` tinyint NOT NULL DEFAULT 1 COMMENT '必填',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `topic_problem_topic_id`(`topic_id` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  CONSTRAINT `topic_problem_topic_id` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 76 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '主题题目' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of topic_problem
-- ----------------------------
INSERT INTO `topic_problem` VALUES (4, 3, 52, 1, '2025-04-12 19:30:16', '2025-06-07 19:33:54', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_problem` VALUES (5, 3, 54, 1, '2025-04-12 19:30:16', '2025-06-07 19:33:54', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_problem` VALUES (6, 3, 56, 1, '2025-04-12 19:30:16', '2025-06-07 19:33:54', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_problem` VALUES (14, 4, 54, 1, '2025-04-13 00:57:48', '2025-06-07 19:33:54', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_problem` VALUES (15, 4, 56, 1, '2025-04-13 00:57:48', '2025-06-07 19:33:54', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_problem` VALUES (37, 13, 309, 1, '2025-06-30 23:52:03', '2025-06-30 23:52:03', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_problem` VALUES (38, 13, 311, 1, '2025-06-30 23:52:03', '2025-06-30 23:52:03', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_problem` VALUES (39, 13, 312, 1, '2025-06-30 23:52:04', '2025-06-30 23:52:04', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_problem` VALUES (68, 12, 362, 1, '2025-08-04 00:27:31', '2025-08-04 00:27:31', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_problem` VALUES (69, 12, 363, 1, '2025-08-04 00:27:31', '2025-08-04 00:27:31', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_problem` VALUES (70, 12, 364, 1, '2025-08-04 00:27:31', '2025-08-04 00:27:31', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_problem` VALUES (71, 12, 365, 1, '2025-08-04 00:27:31', '2025-08-04 00:27:31', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_problem` VALUES (72, 12, 366, 1, '2025-08-04 00:27:31', '2025-08-04 00:27:31', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_problem` VALUES (73, 12, 367, 1, '2025-08-04 00:27:31', '2025-08-04 00:27:31', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_problem` VALUES (74, 12, 368, 1, '2025-08-04 00:27:31', '2025-08-04 00:27:31', 'F', 1, 1, 'GLOWXQ');
INSERT INTO `topic_problem` VALUES (75, 12, 369, 1, '2025-08-04 00:27:31', '2025-08-04 00:27:31', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for topic_submit
-- ----------------------------
DROP TABLE IF EXISTS `topic_submit`;
CREATE TABLE `topic_submit`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `topic_id` bigint NOT NULL COMMENT '主题ID',
  `problem_id` bigint NOT NULL COMMENT '题目ID',
  `problem_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目Key',
  `problem_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目标题ID',
  `problem_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目类型',
  `topic_judge_type` int NOT NULL COMMENT '主题测评类型 ACM/OI',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
  `nick_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '昵称',
  `judge_status` int NOT NULL DEFAULT 0 COMMENT '测评状态',
  `score` int NOT NULL DEFAULT 0 COMMENT '主题分数',
  `use_time` int NOT NULL DEFAULT 0 COMMENT '做题用时(分)',
  `punishment_time` int NOT NULL DEFAULT 0 COMMENT '罚时(分)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `topic_submit_topic_id`(`topic_id` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  CONSTRAINT `topic_submit_topic_id` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '主题测评记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of topic_submit
-- ----------------------------
INSERT INTO `topic_submit` VALUES (21, 12, 365, 'GESP1058', '[GESP202409五级] 挑战怪物', 'Programmer', 0, 1, '管理员', 'glowxq', -1, 0, 0, 0, '2025-08-09 15:10:08', '2025-08-09 15:10:08', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `topic_submit` VALUES (22, 12, 366, 'HOL058', '螺旋矩阵', 'Programmer', 0, 1, '管理员', 'glowxq', -1, 0, 0, 0, '2025-08-09 15:10:12', '2025-08-09 15:10:12', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `topic_submit` VALUES (23, 12, 367, 'GESP1165', '[GESP一级模拟] 小杨去考试', 'Programmer', 0, 1, '管理员', 'glowxq', -1, 0, 0, 0, '2025-08-09 15:10:25', '2025-08-09 15:10:25', 'F', NULL, NULL, 'GLOWXQ');

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `dept_id` bigint NOT NULL DEFAULT -1 COMMENT '部门ID',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
  `nick_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '昵称',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机号',
  `email` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像信息',
  `sex` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '性别',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `image` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '相关图片',
  `ac_num` int NOT NULL DEFAULT 0 COMMENT 'ac数量',
  `integral` int NOT NULL DEFAULT 0 COMMENT '积分',
  `continuous_sign_day` int NOT NULL DEFAULT 0 COMMENT '连续签到时间',
  `submit_num` int NOT NULL DEFAULT 0 COMMENT '提交题目数量',
  `title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '称号',
  `color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '#009688' COMMENT '颜色',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `expiration_time` date NOT NULL DEFAULT '2099-12-31' COMMENT '帐号过期时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后登录ip',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_info_user_id_uindex`(`user_id` ASC) USING BTREE,
  CONSTRAINT `user_info_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES (1, 1, -1, '管理员', 'glowxq', '13667700000', 'glowxq@qq.com', 'https://minioapi.szadmin.cn/test/user/20241216/微信图片_20240420160033.jpg', 'Male', '2025-06-23', '', 0, 419, 1, 3, '', '#009688', '', '2099-12-31', '2025-08-26 02:22:10', '', '2025-06-23 12:32:32', '2025-06-29 00:51:44', 'F', NULL, 1, 'GLOWXQ');
INSERT INTO `user_info` VALUES (8, 8, -1, '学生', '学生', '13667700001', 'kuangshanshan@qq.com', 'https://glowxq-oj-api.oss-cn-guangzhou.aliyuncs.com/local/png/2025/06/28/Snipaste_2025-06-25_20-42-39.png', 'Male', '2025-06-23', '', 0, 16, 1, 0, '', '#009688', '', '2099-12-31', '2025-08-01 00:17:49', '', '2025-06-23 12:32:32', '2025-06-28 23:02:03', 'F', NULL, 1, 'GLOWXQ');
INSERT INTO `user_info` VALUES (9, 9, -1, '校长', '校长', '13667700002', 'glwoxq@qq.com', 'https://glowxq-oj-api.oss-cn-guangzhou.aliyuncs.com/local/png/2025/06/28/Snipaste_2025-06-25_20-42-39.png', 'Male', '2025-06-23', '', 0, 49, 2, 0, '', '#009688', '', '2099-12-31', '2025-06-29 00:42:13', '', '2025-06-23 12:32:32', '2025-06-28 23:02:08', 'F', NULL, 1, 'GLOWXQ');
INSERT INTO `user_info` VALUES (10, 10, -1, '老师', '老师', '13667700003', 'glowxq@qq.com', 'https://glowxq-oj-api.oss-cn-guangzhou.aliyuncs.com/local/png/2025/06/28/Snipaste_2025-06-25_20-42-39.png', 'Male', '2025-06-23', '', 0, 33, 2, 0, '', '#009688', '', '2099-12-31', '2025-06-29 17:20:46', '', '2025-06-23 12:32:32', '2025-06-29 16:39:04', 'F', NULL, 1, 'GLOWXQ');
INSERT INTO `user_info` VALUES (54, 54, 7, '系统管理员', '系统管理员', '13667700004', '孙八616@mock.io', 'https://glowxq-oj-api.oss-cn-guangzhou.aliyuncs.com/local/png/2025/06/28/Snipaste_2025-06-25_20-42-39.png', 'Male', '1996-01-22', 'https://picsum.photos/seed/0.08986787861217171/300/200', 492, 2854, 1, 575, '代码艺术家', '#FFCC00', '这是孙八的测试账号，生成时间：2025/6/23 16:31:37', '2029-07-22', '2025-06-29 17:24:18', '', '2025-06-23 16:31:42', '2025-06-28 23:02:16', 'F', 1, 1, 'GLOWXQ');

-- ----------------------------
-- Table structure for user_problem
-- ----------------------------
DROP TABLE IF EXISTS `user_problem`;
CREATE TABLE `user_problem`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `problem_id` bigint NOT NULL COMMENT '题目ID',
  `problem_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目Key',
  `problem_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目标题ID',
  `judge_id` bigint NOT NULL COMMENT '测评ID',
  `judge_status` int NOT NULL DEFAULT 0 COMMENT '测评状态',
  `score` int NOT NULL DEFAULT 0 COMMENT '作业分数',
  `code` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'AC的代码',
  `options` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '非编程题作答内容',
  `flow_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程图URL',
  `problem_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目类型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_problem_user_id_problem_id_judge_status_uindex`(`user_id` ASC, `problem_id` ASC, `judge_status` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  CONSTRAINT `user_problem_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户题目数据' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_problem
-- ----------------------------
INSERT INTO `user_problem` VALUES (1, 1, 54, 'Q1031', '[GESP202406 三级]  寻找倍数', 222, 4, 0, '对于每组测试用例，如果存在 $i(1\\leq i\\leq n)$ ，满足对于所有 $k(1\\leq k\\leq n)$ $a_i$ 是 $a_k$ 的倍数，输出 `Yes`，否则输出 `No`。\n', '', '', 'Programmer', '2025-04-18 17:53:42', '2025-06-07 19:33:54', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `user_problem` VALUES (2, 1, 56, 'Q1078', '[GESP202406 五级] 小杨的幸运数字', 227, 4, 0, '对于每组测试用例，如果存在 $i(1\\leq i\\leq n)$ ，满足对于所有 $k(1\\leq k\\leq n)$ $a_i$ 是 $a_k$ 的倍数，输出 `Yes`，否则输出 `No`。\n对于每组测试用例，如果存在 $i(1\\leq i\\leq n)$ ，满足对于所有 $k(1\\leq k\\leq n)$ $a_i$ 是 $a_k$ 的倍数，输出 `Yes`，否则输出 `No`。\n对于每组测试用例，如果存在 $i(1\\leq i\\leq n)$ ，满足对于所有 $k(1\\leq k\\leq n)$ $a_i$ 是 $a_k$ 的倍数，输出 `Yes`，否则输出 `No`。\n对于每组测试用例，如果存在 $i(1\\leq i\\leq n)$ ，满足对于所有 $k(1\\leq k\\leq n)$ $a_i$ 是 $a_k$ 的倍数，输出 `Yes`，否则输出 `No`。\n', '', '', 'Programmer', '2025-04-18 20:45:54', '2025-06-07 19:33:54', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `user_problem` VALUES (3, 1, 139, 'Q1079', '[GESP202406 五级] 黑白格', 242, 4, 0, '#include <bits/stdc++.h>\nusing namespace std;\nint main() {\n\n    return 0;\n}', '', '', 'Programmer', '2025-06-10 02:19:48', '2025-06-10 02:19:48', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `user_problem` VALUES (4, 1, 138, 'Q1027', '[GESP202406 一级]  休息时间', 250, 4, 0, '#include<iostream>\nusing namespace std;\nint main() {\n\n     return 0;\n}', '', '', 'Programmer', '2025-06-10 02:29:07', '2025-06-10 02:29:07', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `user_problem` VALUES (5, 1, 365, 'GESP1058', '[GESP202409五级] 挑战怪物', 416, -1, 0, '#include<iostream>\nusing namespace std;\nint main() {\n\n     return 0;\n}', '', '', 'Programmer', '2025-08-09 15:10:08', '2025-08-09 15:10:08', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `user_problem` VALUES (6, 1, 366, 'HOL058', '螺旋矩阵', 417, -1, 0, '#include<iostream>\nusing namespace std;\nint main() {\n\n     return 0;\n}', '', '', 'Programmer', '2025-08-09 15:10:12', '2025-08-09 15:10:12', 'F', NULL, NULL, 'GLOWXQ');
INSERT INTO `user_problem` VALUES (7, 1, 367, 'GESP1165', '[GESP一级模拟] 小杨去考试', 418, -1, 0, '#include<iostream>\nusing namespace std;\nint main() {\n\n     return 0;\n}', '', '', 'Programmer', '2025-08-09 15:10:25', '2025-08-09 15:10:25', 'F', NULL, NULL, 'GLOWXQ');

-- ----------------------------
-- Table structure for user_sign
-- ----------------------------
DROP TABLE IF EXISTS `user_sign`;
CREATE TABLE `user_sign`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `sign_day` date NOT NULL COMMENT '签到日期',
  `integral` int NOT NULL COMMENT '增加积分量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GLOWXQ' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_sign_user_id_sign_day_uindex`(`user_id` ASC, `sign_day` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  CONSTRAINT `user_sign_record_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_sign_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户签到记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_sign
-- ----------------------------

-- ----------------------------
-- Procedure structure for AddTenantIdToTables
-- ----------------------------
DROP PROCEDURE IF EXISTS `AddTenantIdToTables`;
delimiter ;;
CREATE PROCEDURE `AddTenantIdToTables`(IN db_name VARCHAR(64),
    IN ignore_tables TEXT,
    IN field_def TEXT,
    IN field_name VARCHAR(64))
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE table_name_var VARCHAR(64);
    DECLARE sql_stmt TEXT;

    -- 声明游标
    DECLARE table_cursor CURSOR FOR
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema = db_name
          AND table_type = 'BASE TABLE'
          AND FIND_IN_SET(table_name, ignore_tables) = 0
          AND table_name NOT IN (
            SELECT table_name
            FROM information_schema.columns
            WHERE table_schema = db_name
              AND column_name = field_name
          );

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    -- 开启游标
    OPEN table_cursor;

    -- 循环处理每个表
    read_loop: LOOP
        FETCH table_cursor INTO table_name_var;
        IF done THEN
            LEAVE read_loop;
        END IF;

        -- 添加字段
        SET sql_stmt = CONCAT('ALTER TABLE ', table_name_var, ' ADD COLUMN ', field_def);
        SET @sql = sql_stmt;
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        -- 添加索引
        SET sql_stmt = CONCAT('ALTER TABLE ', table_name_var, ' ADD INDEX idx_', field_name, ' (', field_name, ')');
        SET @sql = sql_stmt;
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SELECT CONCAT('已处理表: ', table_name_var) AS status;

    END LOOP;

    CLOSE table_cursor;
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for UpdateTenantIdDefault
-- ----------------------------
DROP PROCEDURE IF EXISTS `UpdateTenantIdDefault`;
delimiter ;;
CREATE PROCEDURE `UpdateTenantIdDefault`(IN db_name VARCHAR(64),
    IN column_name VARCHAR(64))
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE tbl_name VARCHAR(64);
    DECLARE alter_cmd TEXT;
    DECLARE update_cmd TEXT;

    -- 游标获取所有包含指定字段的表名
    DECLARE cur CURSOR FOR
        SELECT TABLE_NAME
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = db_name
          AND COLUMN_NAME = column_name;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SHOW ERRORS;
        ROLLBACK;
    END;

    START TRANSACTION;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO tbl_name;
        IF done THEN
            LEAVE read_loop;
        END IF;

        -- 生成修改默认值的SQL（处理保留字）
        SET @alter_cmd = CONCAT(
            'ALTER TABLE `', db_name, '`.`', tbl_name, '` ',
            'ALTER COLUMN `', column_name, '` SET DEFAULT ''GLOWXQ'''
        );

        -- 生成更新数据的SQL（处理保留字）
        SET @update_cmd = CONCAT(
            'UPDATE `', db_name, '`.`', tbl_name, '` ',
            'SET `', column_name, '` = ''GLOWXQ'' ',
            'WHERE `', column_name, '` IS NOT NULL'
        );

        -- 执行动态SQL
        PREPARE stmt1 FROM @alter_cmd;
        EXECUTE stmt1;
        DEALLOCATE PREPARE stmt1;

        PREPARE stmt2 FROM @update_cmd;
        EXECUTE stmt2;
        DEALLOCATE PREPARE stmt2;

    END LOOP;

    CLOSE cur;
    COMMIT;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
