# Glowxq-OJ 图片资源目录

本目录包含项目文档中使用的所有图片资源。

## 📁 目录结构

### 🎨 Logo 和品牌资源
- `logo.svg` - 项目主Logo，采用绿蓝渐变配色，融入萤火虫元素设计
- `logo-icon.svg` - 项目图标
- `logo-preview.html` - Logo预览页面

### 📞 联系方式图片
- `contact-qr.jpg` - 联系客服微信二维码
- `contact-qr.svg` - 联系客服微信二维码(SVG版本)
- `group-wechat.png` - 技术交流群二维码

### 📸 系统截图

#### 🏠 主界面
- `homepage-main.png` - 系统主界面-首页
- `homepage-dark.png` - 系统主界面-暗黑模式
- `homepage-overview.png` - 系统主界面-功能概览

#### 📝 题目管理
- `problem-list-page.png` - 题目列表页面
- `problem-detail.png` - 题目详情页面
- `problem-edit.png` - 题目编辑页面
- `code-editor.png` - 代码编辑器界面
- `problem-submit.png` - 题目提交界面

#### 🏆 比赛与作业
- `contest-list.png` - 比赛列表页面
- `contest-detail.png` - 比赛详情页面
- `homework-management.png` - 作业管理页面
- `ranking-board.png` - 排行榜页面

#### ⚡ 测评结果
- `judge-result.png` - 测评结果页面
- `code-view.png` - 代码查看页面

#### 👥 用户管理
- `user-list.png` - 用户列表页面
- `user-detail.png` - 用户详情页面
- `user-permission.png` - 用户权限管理页面

#### 🎨 特色功能 - GlowC
- `glowc-editor.png` - GlowC代码编辑器
- `glowc-graphics.png` - GlowC图形输出界面
- `glowc-features.png` - GlowC功能展示页面

#### 🔐 权限管理
- `role-management.png` - 角色管理页面
- `permission-config.png` - 权限配置页面

#### ⚙️ 系统管理
- `system-config.png` - 系统配置页面
- `system-monitor.png` - 系统监控页面

#### 🏫 班级管理
- `class-management.png` - 班级管理页面
- `student-management.png` - 学生管理页面

#### 📡 代码监控推送
- `code-monitor-push.png` - 代码监控推送界面

### 🎬 动画演示 (../gif/)
- `../gif/glowc.gif` - GlowC功能演示动画
- `../gif/code-push.gif` - 代码监控推送演示动画

### 📊 其他截图
- `dashboard.png` - 系统仪表板
- `problem-list.png` - 题目列表页面

## 📝 使用说明

### 图片规范
- **尺寸**: 建议宽度400px-600px，保持16:9或4:3比例
- **格式**: PNG格式，保证清晰度
- **命名**: 使用有意义的英文名称，避免中文
- **大小**: 单个图片不超过500KB

### 在README.md中使用
所有图片都通过HTML表格布局实现两列显示，固定尺寸防止加载频闪：

```html
<div align="center">
<table>
<tr>
<td align="center" width="50%">
<img src="docs/images/img_example.png" width="400" alt="图片描述"/>
<br/>
<b>图片标题</b>
</td>
<td align="center" width="50%">
<img src="docs/images/img_example2.png" width="400" alt="图片描述"/>
<br/>
<b>图片标题</b>
</td>
</tr>
</table>
</div>
```

### GIF动画特殊处理
GIF动画单独显示，不使用表格布局：

```html
![动画描述](docs/gif/animation.gif)
```

## 🔧 维护说明

### 添加新图片
1. 截取系统相关界面的高清截图
2. 按照命名规范重命名文件
3. 将图片文件放入此目录
4. 更新README.md中的引用
5. 更新本文档的图片索引

### 图片优化
- 使用工具压缩图片大小
- 保持统一的截图风格
- 注意隐私信息保护

## 📊 统计信息
- 总图片数量: 35+
- Logo资源: 3个
- 联系方式图片: 3个
- 系统截图: 26个
- 动画演示: 2个
- 其他截图: 2个
