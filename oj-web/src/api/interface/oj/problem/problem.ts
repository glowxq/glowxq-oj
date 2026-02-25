import type { IPageQuery } from '@/api/interface'
import type { BaseForm, BaseQuery, BaseRow } from '@/api/interface/base'

export namespace IProblem {

  // 查询条件
  export interface Query extends BaseQuery {
    id?: number;
    problemKey?: string;
    title?: string;
    programType?: number;
    problemType?: string;
    sourceType?: string;
    timeLimit?: number[];
    timeLimitStart?: number;
    timeLimitEnd?: number;
    memoryLimit?: number[];
    memoryLimitStart?: number;
    memoryLimitEnd?: number;
    stackLimit?: number;
    description?: string;
    input?: string;
    output?: string;
    examples?: string;
    difficulty?: number;
    hint?: string;
    auth?: number;
    ioScore?: number;
    score?: number;
    source?: string;
    tagIds?: number[];
    [key: string]: any;
  }

  // 编辑form表单
  export interface Form extends BaseForm {
    id?: number;
    problemKey?: string;
    title?: string;
    programType?: number;
    problemType?: string;
    sourceType?: string;
    timeLimit?: number;
    memoryLimit?: number;
    stackLimit?: number;
    description?: string;
    input?: string;
    output?: string;
    examples?: string;
    difficulty?: number;
    hint?: string;
    auth?: number;
    ioScore?: number;
    score?: number;
    source?: string;
    judgeMode?: string;
    judgeCaseMode?: string;
    userExtraFile?: string;
    judgeExtraFile?: string;
    spjCode?: string;
    spjLanguage?: string;
    remote?: boolean;
    codeShare?: boolean;
    removeEndBlank?: boolean;
    openCaseResult?: boolean;
    uploadCase?: boolean;
    groupProblem?: boolean;
    fileIo?: boolean;
    requireImage?: boolean;
    caseVersion?: string;
    modifiedUser?: string;
    applyPublicProgress?: number;
    ioReadFileName?: string;
    ioWriteFileName?: string;
    
    uploadTestcaseDir?: string;
    options?: any[];
    problemCaseDataList?: Array<{
      input?: string;
      output?: string;
      type?: string;
      score?: number;
      groupNum?: string;
    }>;
    changeModeCode?: boolean;
    changeJudgeCaseMode?: boolean;
    languageIds?: number[];
    tagIds?: string;
    tagList?: Array<{
      id?: number;
      name?: string;
      color?: string;
      textColor?: string;
      enable?: boolean;
      createTime?: string;
      updateTime?: string;
      createId?: number;
      updateId?: number;
    }>;
    codeTemplates?: Array<{
      languageId?: number;
      name?: string
      code?: string;
      isCustomized?: boolean;
    }>;
    [key: string]: any;
  }

  // list或detail返回结构
  export interface Row extends BaseRow {
    id?: number;
    problemKey?: string;
    title?: string;
    programType?: number;
    problemType?: string;
    sourceType?: string;
    timeLimit?: number;
    memoryLimit?: number;
    stackLimit?: number;
    description?: string;
    input?: string;
    output?: string;
    examples?: string;
    difficulty?: number;
    hint?: string;
    auth?: number;
    ioScore?: number;
    score?: number;
    source?: string;
    problemBo?: {
      id?: number;
      problemKey?: string;
      title?: string;
      programType?: number;
      problemType?: string;
      sourceType?: string;
      timeLimit?: number;
      memoryLimit?: number;
      stackLimit?: number;
      description?: string;
      input?: string;
      output?: string;
      examples?: string;
      difficulty?: number;
      hint?: string;
      auth?: number;
      ioScore?: number;
      score?: number;
      source?: string;
      judgeMode?: string;
      judgeCaseMode?: string;
      userExtraFile?: string;
      judgeExtraFile?: string;
      spjCode?: string;
      spjLanguage?: string;
      remote?: boolean;
      codeShare?: boolean;
      removeEndBlank?: boolean;
      openCaseResult?: boolean;
      uploadCase?: boolean;
      groupProblem?: boolean;
      fileIo?: boolean;
      requireImage?: boolean;
      caseVersion?: string;
      modifiedUser?: string;
      applyPublicProgress?: number;
      ioReadFileName?: string;
      ioWriteFileName?: string;
      [key: string]: any;
    };
    uploadTestcaseDir?: string;
    options?: any[];
    problemCaseDataList?: Array<{
      input?: string;
      output?: string;
      type?: string;
      score?: number;
      groupNum?: string;
    }>;
    changeModeCode?: boolean;
    changeJudgeCaseMode?: boolean;
    languageIds?: number[];
    tagIds?: string;
    tagList?: Array<{
      id?: number;
      name?: string;
      color?: string;
      textColor?: string;
      enable?: boolean;
      createTime?: string;
      updateTime?: string;
      createId?: number;
      updateId?: number;
    }>;
    codeTemplates?: ICodeTemplate.Row[];
    [key: string]: any;
  }

}

export namespace ICodeTemplate {
  export interface Row {
    id?: string;
    languageId?: number;
    code?: string;
    name?: string;
    description?: string;
    codeTemplate?: string;
    compileCommand?: string;
    contentType?: string;
    oj?: string;
    template?: string;
    spjEnable?: number;
    seq?: number;
    isCustomized?: boolean;
  }
}