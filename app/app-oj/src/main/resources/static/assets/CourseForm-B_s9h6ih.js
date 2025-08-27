import{d as S,bc as h,aB as Y,r as p,f as u,o as F,x as $,k as r,j as a,y as i,T as B,a as w,bo as N,t as A,W as g,_ as H}from"./index-DTkCE7MD.js";import{M as O}from"./index-E5zgXN01.js";import"./style-DrPmFrlE.js";import"./upload-BtJOZC1L.js";import"./FileSaver.min-BnsyPtRb.js";const P={class:"markdown-wrapper"},j={class:"status-display"},E=S({name:"CourseForm",__name:"CourseForm",setup(L,{expose:T}){h();const b=Y({name:[{required:!0,message:"请填写课程名"}],content:[{required:!0,message:"请填写课程内容"}],url:[{required:!0,message:"请填写课程连接"}],teacherUserId:[{required:!0,message:"请选择授课老师"}],enable:[{required:!0,message:"请设置启用状态"}],startTime:[{required:!0,message:"请填写上课时间"}],endTime:[{required:!0,message:"请填写下课时间"}],groupIds:[{required:!0,message:"请选择班级",type:"array"}],createTime:[{required:!0,message:"请填写创建时间"}],updateTime:[{required:!0,message:"请填写更新时间"}],delFlag:[{required:!0,message:"请填写是否删除"}]}),_=(l,e)=>{e?(t.value.row.teacherName=e.name,t.value.row.teacherPhone=e.phone):(t.value.row.teacherName=void 0,t.value.row.teacherPhone=void 0)},d=p(!1),t=p({title:"",row:{},api:void 0,getTableList:void 0}),V=l=>{l.row&&l.row.groups&&Array.isArray(l.row.groups)&&(!l.row.groupIds||!Array.isArray(l.row.groupIds))&&(l.row.groupIds=l.row.groups.map(e=>e.id).filter(Boolean)),t.value=l,d.value=!0},c=p(),f=()=>{c.value.validate(async l=>{if(l)try{await t.value.api(t.value.row),g.success({message:`${t.value.title}成功！`}),t.value.getTableList(),d.value=!1}catch(e){console.log(e)}})},y=()=>{const l=new Date,e=t.value.row.startTime?new Date(t.value.row.startTime):null,s=t.value.row.endTime?new Date(t.value.row.endTime):null;return!e||!s||l<e?"info":l>=e&&l<=s?"success":"danger"},D=()=>{const l=new Date,e=t.value.row.startTime?new Date(t.value.row.startTime):null,s=t.value.row.endTime?new Date(t.value.row.endTime):null;return!e||!s?"未设置时间":l<e?"未开始":l>=e&&l<=s?"进行中":"已结束"},k=()=>{const l=Math.floor(Math.random()*1e4),e=new Date,s=new Date(e.getTime()+24*60*60*1e3),n=new Date(s.getTime()+3*60*60*1e3);return{name:`测试课程${l}`,content:`# 测试课程${l}

## 课程介绍
这是一个自动生成的测试课程，包含以下内容：

### 学习目标
- 掌握基础概念
- 理解核心原理
- 实践操作技能

### 课程大纲
1. **第一章：基础知识**
   - 概念介绍
   - 基本原理
   - 实例分析

2. **第二章：进阶内容**
   - 深入理解
   - 高级技巧
   - 项目实战

3. **第三章：总结提升**
   - 知识整合
   - 经验分享
   - 课后练习

### 学习资源
- 📖 课程讲义
- 🎥 视频教程
- 💻 代码示例
- 📝 课后作业

> **注意**：这是测试数据，请在实际使用前修改内容。`,url:`https://example.com/course/${l}`,enable:!0,startTime:s.toISOString().slice(0,19).replace("T"," "),endTime:n.toISOString().slice(0,19).replace("T"," ")}},C=()=>{const l=k();Object.assign(t.value.row,l),g.success("测试数据已填充！")};return T({acceptParams:V}),(l,e)=>{const s=u("el-input"),n=u("el-form-item"),U=u("el-tag"),x=u("el-switch"),v=u("el-date-picker"),I=u("el-form"),m=u("el-button"),q=u("el-dialog");return F(),$(q,{modelValue:d.value,"onUpdate:modelValue":e[9]||(e[9]=o=>d.value=o),title:`${t.value.title}`,"destroy-on-close":!0,width:"780px",draggable:""},{footer:r(()=>[a(m,{onClick:e[8]||(e[8]=o=>d.value=!1)},{default:r(()=>e[11]||(e[11]=[i(" 取消")])),_:1}),a(m,{type:"warning",onClick:C,plain:""},{default:r(()=>e[12]||(e[12]=[i("🧪 一键测试")])),_:1}),a(m,{type:"primary",onClick:f},{default:r(()=>e[13]||(e[13]=[i(" 确定")])),_:1})]),default:r(()=>[a(I,{ref_key:"ruleFormRef",ref:c,"label-width":"140px","label-suffix":" :",rules:b,model:t.value.row,onSubmit:B(f,["prevent"])},{default:r(()=>[a(n,{label:"课程名",prop:"name"},{default:r(()=>[a(s,{modelValue:t.value.row.name,"onUpdate:modelValue":e[0]||(e[0]=o=>t.value.row.name=o),placeholder:"请填写课程名",clearable:""},null,8,["modelValue"])]),_:1}),a(n,{label:"课程内容",prop:"content"},{default:r(()=>[w("div",P,[a(O,{modelValue:t.value.row.content,"onUpdate:modelValue":e[1]||(e[1]=o=>t.value.row.content=o),height:300,placeholder:"请使用Markdown编写课程内容..."},null,8,["modelValue"])])]),_:1}),a(n,{label:"课程连接",prop:"url"},{default:r(()=>[a(s,{modelValue:t.value.row.url,"onUpdate:modelValue":e[2]||(e[2]=o=>t.value.row.url=o),placeholder:"请填写课程连接",clearable:""},null,8,["modelValue"])]),_:1}),a(n,{label:"授课老师",prop:"teacherUserId"},{default:r(()=>[a(N,{modelValue:t.value.row.teacherUserId,"onUpdate:modelValue":e[3]||(e[3]=o=>t.value.row.teacherUserId=o),placeholder:"请选择授课老师",clearable:"",onChange:e[4]||(e[4]=(o,M)=>_(o,M))},null,8,["modelValue"])]),_:1}),a(n,{label:"课程状态"},{default:r(()=>[w("div",j,[a(U,{type:y(),effect:"dark",class:"status-tag"},{default:r(()=>[i(A(D()),1)]),_:1},8,["type"]),e[10]||(e[10]=w("span",{class:"status-hint"},"（根据上课时间和下课时间自动计算）",-1))])]),_:1}),a(n,{label:"启用状态",prop:"enable"},{default:r(()=>[a(x,{modelValue:t.value.row.enable,"onUpdate:modelValue":e[5]||(e[5]=o=>t.value.row.enable=o)},null,8,["modelValue"])]),_:1}),a(n,{label:"上课时间",prop:"startTime"},{default:r(()=>[a(v,{clearable:"",modelValue:t.value.row.startTime,"onUpdate:modelValue":e[6]||(e[6]=o=>t.value.row.startTime=o),type:"datetime","value-format":"YYYY-MM-DD HH:mm:ss",placeholder:"请选择上课时间"},null,8,["modelValue"])]),_:1}),a(n,{label:"下课时间",prop:"endTime"},{default:r(()=>[a(v,{clearable:"",modelValue:t.value.row.endTime,"onUpdate:modelValue":e[7]||(e[7]=o=>t.value.row.endTime=o),type:"datetime","value-format":"YYYY-MM-DD HH:mm:ss",placeholder:"请选择下课时间"},null,8,["modelValue"])]),_:1})]),_:1},8,["rules","model"])]),_:1},8,["modelValue","title"])}}}),K=H(E,[["__scopeId","data-v-14e45c95"]]);export{K as default};
