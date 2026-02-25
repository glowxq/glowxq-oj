import type {BaseEnum} from '@/enums/base';

// 评测场景类型枚举接口
interface JudgeSceneTypeEnum extends BaseEnum {
    tooltip?: string;
    text?: string;
}

/**
 * 评测场景类型枚举
 * 用于标识不同的评测场景
 */
export const JudgeSceneType: { [key: string]: JudgeSceneTypeEnum } = {
    Normal: {
        code: 'Normal',
        name: '普通测评',
        tooltip: '普通评测场景',
        text: '常规代码评测场景'
    },
    Exercise: {
        code: 'Exercise',
        name: '练习',
        tooltip: '练习评测场景',
        text: '用于练习的评测场景'
    },
    Homework: {
        code: 'Homework',
        name: '作业',
        tooltip: '训练作业评测场景',
        text: '用于日常作业训练的评测场景'
    },
    Contest: {
        code: 'Contest',
        name: '比赛',
        tooltip: '比赛评测场景',
        text: '用于比赛的评测场景'
    }

};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): JudgeSceneTypeEnum | null {
    return Object.values(JudgeSceneType).find(item => item.code === code) || null;
}
