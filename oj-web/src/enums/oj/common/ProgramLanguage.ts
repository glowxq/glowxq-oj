/**
 * 编程语言枚举
 * 用于统一系统中对编程语言的描述和使用
 */
export enum ProgramLanguage {
  JAVASCRIPT = "JavaScript",
  TYPESCRIPT = "TypeScript",
  PYTHON = "Python",
  JAVA = "Java",
  CPP = "C++",
  GOLANG = "Golang",
  RUST = "Rust"
}

/**
 * 语言标识映射
 * 用于将各种写法映射到标准枚举
 */
const languageAliasMap: Record<string, ProgramLanguage> = {
  // JavaScript别名
  'javascript': ProgramLanguage.JAVASCRIPT,
  'js': ProgramLanguage.JAVASCRIPT,
  // TypeScript别名
  'typescript': ProgramLanguage.TYPESCRIPT,
  'ts': ProgramLanguage.TYPESCRIPT,
  // Python别名
  'python': ProgramLanguage.PYTHON,
  'python2': ProgramLanguage.PYTHON,
  'python3': ProgramLanguage.PYTHON,
  'py': ProgramLanguage.PYTHON,
  // Java别名
  'java': ProgramLanguage.JAVA,
  // C/C++别名
  'c': ProgramLanguage.CPP,
  'c++': ProgramLanguage.CPP,
  'cpp': ProgramLanguage.CPP,
  'c++ with o2': ProgramLanguage.CPP,
  'c with o2': ProgramLanguage.CPP,
  'g++': ProgramLanguage.CPP,
  'gcc': ProgramLanguage.CPP,
  // Golang别名
  'go': ProgramLanguage.GOLANG,
  'golang': ProgramLanguage.GOLANG,
  // Rust别名
  'rust': ProgramLanguage.RUST
}

/**
 * 将语言标识转换为标准枚举值
 * @param language 语言标识（可以是字符串或已有枚举值）
 * @returns 标准化的语言枚举值
 */
export function normalizeProgramLanguage(language: string): ProgramLanguage {
  if (!language) return ProgramLanguage.JAVASCRIPT;
  
  // 如果是大写形式且匹配枚举值，直接返回
  if (Object.values(ProgramLanguage).includes(language as ProgramLanguage)) {
    return language as ProgramLanguage;
  }
  
  // 转小写后在映射表中查找
  const lowerCaseKey = language.toLowerCase();
  
  // 尝试直接匹配
  if (languageAliasMap[lowerCaseKey]) {
    return languageAliasMap[lowerCaseKey];
  }
  
  // 处理带有额外后缀的语言名称（例如"C++ With O2"）
  for (const [key, value] of Object.entries(languageAliasMap)) {
    // 检查是否以某个已知语言键开头
    if (lowerCaseKey.startsWith(key + ' ') || lowerCaseKey === key) {
      return value;
    }
  }
  
  // 如果没有找到匹配，使用默认语言
  return ProgramLanguage.JAVASCRIPT;
}

/**
 * 获取语言对应的CodeEditor编辑器语言标识
 * @param language 标准化的语言枚举值
 */
export function getEditorLanguage(language: ProgramLanguage | string): string {
  // 确保传入的是标准枚举值
  const normalizedLang = typeof language === 'string' 
    ? normalizeProgramLanguage(language)
    : language;
  
  // 编辑器语言映射
  const editorLangMap: Record<ProgramLanguage, string> = {
    [ProgramLanguage.JAVASCRIPT]: 'javascript',
    [ProgramLanguage.TYPESCRIPT]: 'typescript',
    [ProgramLanguage.PYTHON]: 'python',
    [ProgramLanguage.JAVA]: 'java',
    [ProgramLanguage.CPP]: 'cpp',
    [ProgramLanguage.GOLANG]: 'go',
    [ProgramLanguage.RUST]: 'rust'
  };
  
  return editorLangMap[normalizedLang] || 'javascript';
}

/**
 * 获取支持的语言列表
 * @returns 语言列表，包含label和value
 */
export function getSupportedLanguages() {
  return [
    { label: "JavaScript", value: ProgramLanguage.JAVASCRIPT },
    { label: "TypeScript", value: ProgramLanguage.TYPESCRIPT },
    { label: "Python", value: ProgramLanguage.PYTHON },
    { label: "Java", value: ProgramLanguage.JAVA },
    { label: "C++", value: ProgramLanguage.CPP },
    { label: "Golang", value: ProgramLanguage.GOLANG },
    { label: "Rust", value: ProgramLanguage.RUST },
  ];
}

/**
 * 检查两种语言标识符是否兼容（是否表示同一种语言）
 * @param language1 第一个语言标识符
 * @param language2 第二个语言标识符
 * @returns 两种语言是否兼容
 */
export function areLanguagesCompatible(language1: string, language2: string): boolean {
  if (!language1 || !language2) return false;
  
  // 如果字符串完全相同，直接返回true
  if (language1.toLowerCase() === language2.toLowerCase()) return true;
  
  // 处理带有额外后缀的语言名称（例如"C++ With O2"）
  // 只考虑简单名称匹配，拒绝复杂的名称匹配
  const name1Parts = language1.toLowerCase().split(/\s+/);
  const name2Parts = language2.toLowerCase().split(/\s+/);
  
  // 如果任一名称有多个单词且总长度>12，严格匹配第一个单词
  if ((name1Parts.length > 1 && language1.length > 12) || 
      (name2Parts.length > 1 && language2.length > 12)) {
    // 只比较第一个单词是否匹配
    return name1Parts[0] === name2Parts[0];
  }
  
  // 简单情况下，使用标准化比较
  const normalizedLang1 = normalizeProgramLanguage(language1);
  const normalizedLang2 = normalizeProgramLanguage(language2);
  
  // 如果标准化后相同，返回true
  return normalizedLang1 === normalizedLang2;
}
