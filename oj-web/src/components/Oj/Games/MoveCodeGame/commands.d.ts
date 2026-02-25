/**
 * 指令数据类型定义
 */

export interface CommandParam {
  name: string
  type: string
  description: string
}

export interface CommandReturn {
  type: string
  description: string
}

export interface Command {
  name: string
  description: string
  syntax: string
  example: string
  level: '基础' | '中级' | '高级'
  color: string
  oldName?: string
  parameters?: string
  returns?: string | CommandReturn
  note?: string
  params?: CommandParam[]
}

export interface CommandCategory {
  category: string
  icon: string
  description: string
  commands: Command[]
}

export declare const commandsData: CommandCategory[]

export declare function getLevelColor(level: string): string

export declare function findCommand(commandName: string): (Command & { category: string }) | null

export declare function getAllCommandNames(): string[]

export declare function getCommandsByLevel(level: string): CommandCategory[]
