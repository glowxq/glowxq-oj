// 题目表单所需类型定义

// Markdown加载状态
export interface MarkdownLoadedState {
  description: boolean;
  input: boolean;
  output: boolean;
  hint: boolean;
}

// 表单参数接口
export interface DefaultParams {
  title: string;
  row: Record<string, any>;
  api?: (params: any) => Promise<any>;
  getTableList?: () => void;
}

// 样例数据接口
export interface ProblemCaseBoData {
  input: string;
  output: string;
  type: string;
  score: number;
  groupNum: number;
}

// 代码模板接口
export interface ProblemCodeTemplate {
  id?: string;            // 语言ID
  languageId?: number;    // 传统的语言ID字段
  code?: string;          // 代码内容(兼容旧数据)
  name?: string;          // 语言名称
  description?: string;   // 语言描述
  codeTemplate?: string;  // 代码模板内容
  compileCommand?: string;// 编译指令
  contentType?: string;   // 语言类型
  oj?: string;            // OJ类型
  template?: string;      // 模板
  spjEnable?: number;     // 是否SPJ
  seq?: number;           // 排序
}

// 选项数据接口
export interface ProblemOption {
  optionKey: string;
  optionContent: string;
  score: number;
  answer: boolean;
}

// 题目基本信息接口
export interface ProblemBoData {
  id: number | null;
  problemKey: string;
  title: string;
  author: string;
  programType: number;
  problemType: string;
  sourceType: string;
  timeLimit: number;
  memoryLimit: number;
  stackLimit: number;
  description: string;
  input: string;
  output: string;
  examples: string;
  difficulty: number;
  hint: string;
  auth: number;
  ioScore: number;
  score: number;
  source: string;
  judgeMode: string;
  judgeCaseMode: string;
  userExtraFile: string;
  judgeExtraFile: string;
  spjCode: string;
  spjLanguage: string;
  remote: boolean;
  codeShare: boolean;
  removeEndBlank: boolean;
  openCaseResult: boolean;
  uploadCase: boolean;
  groupProblem: boolean;
  fileIo: boolean;
  requireImage: boolean;
  changeModeCode: boolean;
  changeJudgeCaseMode: boolean;
  caseVersion: string;
  modifiedUser: string;
  applyPublicProgress: number | null;
  ioReadFileName: string;
  ioWriteFileName: string;
  languageIds: number[];
  tagIds: number[];
  answer: boolean | null;
  referenceAnswer: string;
  gradingCriteria: string;
  [key: string]: any;
}

// 完整题目数据接口
export interface ProblemData {
  problemBo: ProblemBoData;
  problemCaseDataList: ProblemCaseBoData[];
  codeTemplates: ProblemCodeTemplate[];
  uploadTestcaseDir?: string; // 测试用例文件的存储目录路径
  options?: ProblemOption[]; // 客观题选项
  blanks?: ProblemOption[]; // 填空题答案
}
