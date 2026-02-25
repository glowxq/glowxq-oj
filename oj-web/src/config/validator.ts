/**
 * 验证密码格式
 * @param rule
 * @param value
 * @param callback
 */
export const validatePasswordFormat = (rule: any, value: any, callback: (error?: string | Error) => void) => {
  // 验证密码格式
  const reg = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[^]{8,16}$/;
  // 密码要求：
  // 1、长度为8-16位
  // 2、必须包含大小写字母及数字
  if (!reg.test(value)) {
    // 密码不符合要求
    callback(new Error('必须包含大小写字母及数字且长度为8-16位'));
    return;
  }
  // 密码符合要求
  callback();
};
