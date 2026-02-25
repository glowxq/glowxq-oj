import {isArray} from '@/utils/is';
import forge from 'node-forge';

/**
 * @description 获取localStorage
 * @param {String} key Storage名称
 * @returns {String}
 */
export function localGet(key: string) {
  const value = window.localStorage.getItem(key);
  try {
    return JSON.parse(window.localStorage.getItem(key) as string);
  } catch (error) {
    return value;
  }
}

/**
 * @description 存储localStorage
 * @param {String} key Storage名称
 * @param {*} value Storage值
 * @returns {void}
 */
export function localSet(key: string, value: any) {
  window.localStorage.setItem(key, JSON.stringify(value));
}

/**
 * @description 清除localStorage
 * @param {String} key Storage名称
 * @returns {void}
 */
export function localRemove(key: string) {
  window.localStorage.removeItem(key);
}

/**
 * @description 清除所有localStorage
 * @returns {void}
 */
export function localClear() {
  window.localStorage.clear();
}

/**
 * @description 判断数据类型
 * @param {*} val 需要判断类型的数据
 * @returns {String}
 */
export function isType(val: any) {
  if (val === null) {
    return 'null';
  }
  if (typeof val !== 'object') {
    return typeof val;
  }
  return Object.prototype.toString.call(val).slice(8, -1).toLocaleLowerCase();
}

/**
 * @description 生成唯一 uuid
 * @returns {String}
 */
export function generateUUID() {
  let uuid = '';
  for (let i = 0; i < 32; i++) {
    const random = (Math.random() * 16) | 0;
    if (i === 8 || i === 12 || i === 16 || i === 20) {
      uuid += '-';
    }
    uuid += (i === 12 ? 4 : i === 16 ? (random & 3) | 8 : random).toString(16);
  }
  return uuid;
}

/**
 * 判断两个对象是否相同
 * @param {Object} a 要比较的对象一
 * @param {Object} b 要比较的对象二
 * @returns {Boolean} 相同返回 true，反之 false
 */
export function isObjectValueEqual(a: { [key: string]: any }, b: { [key: string]: any }) {
  if (!a || !b) {
    return false;
  }
  const aProps = Object.getOwnPropertyNames(a);
  const bProps = Object.getOwnPropertyNames(b);
  if (aProps.length !== bProps.length) {
    return false;
  }
  for (let i = 0; i < aProps.length; i++) {
    const propName = aProps[i];
    const propA = a[propName];
    const propB = b[propName];
    if (!Object.prototype.hasOwnProperty.call(b, propName)) {
      return false;
    }
    if (propA instanceof Object) {
      if (!isObjectValueEqual(propA, propB)) {
        return false;
      }
    } else if (propA !== propB) {
      return false;
    }
  }
  return true;
}

/**
 * @description 生成随机数
 * @param {Number} min 最小值
 * @param {Number} max 最大值
 * @returns {Number}
 */
export function randomNum(min: number, max: number): number {
  return Math.floor(Math.random() * (min - max) + max);
}

/**
 * @description 获取当前时间对应的提示语
 * @returns {String}
 */
export function getTimeState() {
  const timeNow = new Date();
  const hours = timeNow.getHours();
  if (hours >= 6 && hours <= 10) {
    return `早上好 ⛅`;
  }
  if (hours >= 10 && hours <= 14) {
    return `中午好 🌞`;
  }
  if (hours >= 14 && hours <= 18) {
    return `下午好 🌞`;
  }
  if (hours >= 18 && hours <= 24) {
    return `晚上好 🌛`;
  }
  if (hours >= 0 && hours <= 6) {
    return `凌晨好 🌛`;
  }
}

/**
 * @description 获取浏览器默认语言
 * @returns {String}
 */
export function getBrowserLang(): string {
  const browserLang = navigator.language ? navigator.language : navigator.browserLanguage;
  let defaultBrowserLang;
  if (['cn', 'zh', 'zh-cn'].includes(browserLang.toLowerCase())) {
    defaultBrowserLang = 'zh';
  } else {
    defaultBrowserLang = 'en';
  }
  return defaultBrowserLang;
}

/**
 * @description 使用递归扁平化菜单，方便添加动态路由
 * @param {Array} menuList 菜单列表
 * @returns {Array}
 */
export function getFlatMenuList(menuList: Menu.MenuOptions[]): Menu.MenuOptions[] {
  const newMenuList: Menu.MenuOptions[] = JSON.parse(JSON.stringify(menuList));
  return newMenuList.flatMap(item => [item, ...(item.children ? getFlatMenuList(item.children) : [])]);
}

/**
 * @description 使用递归过滤出需要渲染在左侧菜单的列表 (需剔除 isHidden == true 的菜单)
 * @param {Array} menuList 菜单列表
 * @returns {Array}
 * */
export function getShowMenuList(menuList: Menu.MenuOptions[]): Menu.MenuOptions[] {
  const newMenuList: Menu.MenuOptions[] = JSON.parse(JSON.stringify(menuList));
  return newMenuList.filter(item => {
    item.children?.length && (item.children = getShowMenuList(item.children));
    return item.meta?.isHidden === 'F';
  });
}

/**
 * @description 使用递归找出所有面包屑存储到 pinia/vuex 中
 * @param {Array} menuList 菜单列表
 * @param {Array} parent 父级菜单
 * @param {Object} result 处理后的结果
 * @returns {Object}
 */
export const getAllBreadcrumbList = (
  menuList: Menu.MenuOptions[],
  parent: any[] = [],
  result: {
    [key: string]: any;
  } = {}
) => {
  for (const item of menuList) {
    result[item.path] = [...parent, item];
    if (item.children) {
      getAllBreadcrumbList(item.children, result[item.path], result);
    }
  }
  return result;
};

/**
 * @description 使用递归处理路由菜单 path，生成一维数组 (第一版本地路由鉴权会用到，该函数暂未使用)
 * @param {Array} menuList 所有菜单列表
 * @param {Array} menuPathArr 菜单地址的一维数组 ['**','**']
 * @returns {Array}
 */
export function getMenuListPath(menuList: Menu.MenuOptions[], menuPathArr: string[] = []): string[] {
  for (const item of menuList) {
    if (typeof item === 'object' && item.path) {
      menuPathArr.push(item.path);
    }
    if (item.children?.length) {
      getMenuListPath(item.children, menuPathArr);
    }
  }
  return menuPathArr;
}

/**
 * @description 递归查询当前 path 所对应的菜单对象 (该函数暂未使用)
 * @param {Array} menuList 菜单列表
 * @param {String} path 当前访问地址
 * @returns {Object | null}
 */
export function findMenuByPath(menuList: Menu.MenuOptions[], path: string): Menu.MenuOptions | null {
  for (const item of menuList) {
    if (item.path === path) return item;
    if (item.children) {
      const res = findMenuByPath(item.children, path);
      if (res) return res;
    }
  }
  return null;
}

/**
 * @description 使用递归过滤需要缓存的菜单 name (该函数暂未使用)
 * @param {Array} menuList 所有菜单列表
 * @param {Array} keepAliveNameArr 缓存的菜单 name ['**','**']
 * @returns {Array}
 * */
export function getKeepAliveRouterName(menuList: Menu.MenuOptions[], keepAliveNameArr: string[] = []): string[] {
  menuList.forEach(item => {
    item.meta.isKeepAlive === 'T' && item.name && keepAliveNameArr.push(item.name);
    item.children?.length && getKeepAliveRouterName(item.children, keepAliveNameArr);
  });
  return keepAliveNameArr;
}

/**
 * @description 格式化表格单元格默认值 (el-table-column)
 * @param {Number} row 行
 * @param {Number} col 列
 * @param {*} callValue 当前单元格值
 * @returns {String}
 * */
export function formatTableColumn(row: number, col: number, callValue: any) {
  // 如果当前值为数组，使用 / 拼接（根据需求自定义）
  if (isArray(callValue)) {
    return callValue.length ? callValue.join(' / ') : '--';
  }
  return callValue ?? '--';
}

/**
 * @description 处理值无数据情况
 * @param {*} callValue 需要处理的值
 * @returns {String}
 * */
export function formatValue(callValue: any) {
  // 处理布尔值，以更直观的方式展示
  if (typeof callValue === 'boolean') {
    return callValue ? 'Yes' : 'No';
  }
  // 如果当前值为数组，使用 / 拼接（根据需求自定义）
  if (isArray(callValue)) return callValue.length ? callValue.join(' / ') : '--';
  return callValue ?? '--';
}

/**
 * @description 处理 prop 为多级嵌套的情况，返回的数据 (列如: prop: user.name)
 * @param {Object} row 当前行数据
 * @param {String} prop 当前 prop
 * @returns {*}
 * */
export function handleRowAccordingToProp(row: { [key: string]: any }, prop: string) {
  if (!prop.includes('.')) {
    return row[prop] ?? '--';
  }
  prop.split('.').forEach(item => (row = row[item] ?? '--'));
  return row;
}

/**
 * @description 处理 prop，当 prop 为多级嵌套时 ==> 返回最后一级 prop
 * @param {String} prop 当前 prop
 * @returns {String}
 * */
export function handleProp(prop: string) {
  const propArr = prop.toString().split('.');
  if (propArr.length === 1) {
    return prop;
  }
  return propArr[propArr.length - 1];
}

/**
 * @description 根据枚举列表查询当需要的数据（如果指定了 label 和 value 的 key值，会自动识别格式化）
 * @param {String} callValue 当前单元格值
 * @param {Array} enumData 字典列表
 * @param {Array} fieldNames label && value && children 的 key 值
 * @param {String} type 过滤类型（目前只有 tag）
 * @returns {String}
 * */
export function filterEnum(callValue: any, enumData?: any, fieldNames?: any, type?: 'tag') {
  const value = fieldNames?.value ?? 'value';
  const label = fieldNames?.label ?? 'label';
  const children = fieldNames?.children ?? 'children';
  const tagType = fieldNames?.tagType ?? 'tagType';
  let filterData: { [key: string]: any } = {};
  // 判断 enumData 是否为数组
  if (Array.isArray(enumData)) filterData = findItemNested(enumData, callValue, value, children);
  // 判断是否输出的结果为 tag 类型
  if (type === 'tag') {
    return filterData ? filterData[tagType] || '' : '';
  } else {
    return filterData ? filterData[label] : '--';
  }
}

/**
 * @description 递归查找 callValue 对应的 enum 值
 * */
export function findItemNested(enumData: any, callValue: any, value: string, children: string) {
  return enumData.reduce((accumulator: any, current: any) => {
    if (accumulator) return accumulator;
    if (current[value]?.toString() === callValue?.toString()) return current;
    if (current[children]) return findItemNested(current[children], callValue, value, children);
  }, null);
}

/**
 * 是否是local env
 */
export function isProdEnv() {
  return import.meta.env.MODE === 'production' || import.meta.env.MODE === 'prod';
}

/**
 * 使用 AES-GCM 模式加密消息
 * @param {string} message - 待加密的消息
 * @param {string} secretKey - 加密密钥（16 字节）
 * @returns {{ iv: string, encryptedData: string }} - 返回加密后的数据和 IV
 */
export function aesEncrypt(message: string, secretKey: string) {
  const iv = forge.random.getBytesSync(12); // 生成随机 IV (12 字节)
  const key = forge.util.createBuffer(secretKey, 'utf8').bytes();

  const cipher = forge.cipher.createCipher('AES-GCM', key);
  cipher.start({ iv: iv });
  cipher.update(forge.util.createBuffer(message, 'utf8'));
  cipher.finish();

  const encrypted = cipher.output.getBytes();
  const tag = cipher.mode.tag.getBytes();

  return {
    iv: forge.util.encode64(iv),
    encryptedData: forge.util.encode64(encrypted + tag)
  };
}
