import type { ProblemType } from '@/enums/oj/problem'
import type { JudgeSceneType } from '@/enums/oj/judge/JudgeSceneType'

// 题目选项接口
export interface ProblemOption {
  id?: string;
  optionKey?: string;
  optionContent?: string;
  answer?: boolean;
}

/**
 * 普通测评提交参数
 */
export interface GlobalNormalSubmitDTO {
  // 题目id
  problemId: number;

  // 代码语言选择
  language: string;

  // 提交的代码
  code: string;

  // 测评场景类型
  judgeSceneType: string;

  // 提交类型
  submitType: string;

  // 提交类型业务ID 比赛、训练、班级等等
  businessId: number;

  // 流程图URL
  flowImage?: string;

  // 是否为远程判题
  isRemote: boolean;

  // 题目类型
  problemType: string;

  // 客观题 答题内容
  replyOptions?: ProblemOption[];
}

export interface Judge {
  id: number;
  judgeKey: string;
  problemId: number;
  problemKey: string;
  userId: number;
  groupId: number;
  contestId: number;
  businessId: number;
  name: string;
  sceneType: string;
  problemType: string;
  submitTime: string;
  startTime: string;
  endTime: string;
  status: number;
  errorMessage: string;
  time: number;
  memory: number;
  score: number;
  length: number;
  flowImage: string;
  code: string;
  replyOptions: string;
  language: string;
  judgeServer: string;
  submitIp: string;
  version: number;
  oiRankScore: number;
  shareEnable: boolean;
  manualEvaluation: boolean;
  otherJudgeSubmitId: string;
  otherJudgeUsername: string;
  otherJudgePassword: string;
  createTime: string;
  updateTime: string;
  delFlag: string;
  createId: number;
  updateId: number;
}
