import { reactive, computed, toRefs } from 'vue';
import type { Table } from '@/hooks/interface';

/**
 * @description table 页面操作方法封装
 * @param {Function} api 获取表格数据 api 方法 (必传)
 * @param {Object} initParam 获取数据初始化参数 (非必传，默认为{})
 * @param {Boolean} isPageable 是否有分页 (非必传，默认为true)
 * @param {Function} dataCallBack 对后台返回的数据进行处理的方法 (非必传)
 * @param {Function} requestError 对请求数据错误处理的方法 (非必传)
 * @param loadingTime loading 延迟时间 (非必传，默认为 0 ms)
 * */
export const useTable = (
  api?: (params: any) => Promise<any>,
  initParam: object = {},
  isPageable: boolean = true,
  dataCallBack?: (data: any) => any,
  requestError?: (params: any) => void,
  loadingTime?: number
) => {
  const state = reactive<Table.StateProps>({
    // 表格数据
    tableData: [],
    // 分页数据
    pageable: {
      // 当前页数
      pageNum: 1,
      // 每页显示条数
      pageSize: 10,
      // 总条数
      total: 0
    },
    // 查询参数(只包括查询)
    searchParam: {},
    // 初始化默认的查询参数
    searchInitParam: {},
    // 总参数(包含分页和查询参数)
    totalParam: {},
    loading: false
  });

  /**
   * @description 分页查询参数(只包括分页和表格字段排序,其他排序方式可自行配置)
   * */
  const pageParam = computed({
    get: () => {
      return {
        page: state.pageable.pageNum,
        limit: state.pageable.pageSize
      };
    },
    set: newVal => {
      console.log('我是分页更新之后的值', newVal);
    }
  });

  /**
   * @description 获取表格数据
   * @return void
   * */
  const getTableList = async () => {
    if (!api) return;
    state.loading = true;
    try {
      await delayLoading(loadingTime);
      // 先把初始化参数和分页参数放到总参数里面
      Object.assign(state.totalParam, initParam, isPageable ? pageParam.value : {});
      let { data } = await api({ ...state.searchInitParam, ...state.totalParam });
      dataCallBack && (data = dataCallBack(data));
      if (typeof data === 'object' && Object.keys(data).length === 0) {
        state.tableData = isPageable ? data.rows : [];
      } else {
        state.tableData = isPageable ? data.rows : data;
      }
      state.loading = false;
      // 解构后台返回的分页数据 (如果有分页更新分页信息)
      if (isPageable) {
        const { current: pageNum, limit: pageSize, total } = data;
        updatePageable({ pageNum, pageSize, total });
      }
    } catch (error) {
      state.loading = false;
      requestError && requestError(error);
    }
  };

  /**
   * @description 更新查询参数
   * @return void
   * */
  const updatedTotalParam = () => {
    state.totalParam = {};
    // 处理查询参数，可以给查询参数加自定义前缀操作
    const nowSearchParam: Table.StateProps['searchParam'] = {};
    // 防止手动清空输入框携带参数（这里可以自定义查询参数前缀）
    for (const key in state.searchParam) {
      // * 某些情况下参数为 false/0 也应该携带参数
      if (state.searchParam[key] || state.searchParam[key] === false || state.searchParam[key] === 0) {
        nowSearchParam[key] = state.searchParam[key];
      }
    }
    Object.assign(state.totalParam, nowSearchParam, isPageable ? pageParam.value : {});
  };

  /**
   * @description 更新分页信息
   * @param {Object} pageable 后台返回的分页数据
   * @return void
   * */
  const updatePageable = (pageable: Table.Pageable) => {
    Object.assign(state.pageable, pageable);
  };

  /**
   * @description 表格数据查询
   * @return void
   * */
  const search = () => {
    state.pageable.pageNum = 1;
    updatedTotalParam();
    getTableList();
  };

  /**
   * @description 表格数据重置
   * @return void
   * */
  const reset = () => {
    state.pageable.pageNum = 1;
    state.searchParam = {};
    // 重置搜索表单的时，如果有默认搜索参数，则重置默认的搜索参数
    state.searchParam = { ...state.searchInitParam };
    updatedTotalParam();
    getTableList();
  };

  /**
   * @description 每页条数改变
   * @param {Number} val 当前条数
   * @return void
   * */
  const handleSizeChange = (val: number) => {
    state.pageable.pageNum = 1;
    state.pageable.pageSize = val;
    getTableList();
  };

  /**
   * @description 当前页改变
   * @param {Number} val 当前页
   * @return void
   * */
  const handleCurrentChange = (val: number) => {
    state.pageable.pageNum = val;
    getTableList();
  };

  const delayLoading = async (loadingTime: number | undefined) => {
    const defaultLoadingTime = 0; // 默认的 loading 延迟时间为0秒
    let actualLoadingTime = loadingTime;
    // 判断 loadingTime 是否为特殊参数
    if (typeof loadingTime === 'undefined' || loadingTime === null || loadingTime === -1) {
      actualLoadingTime = defaultLoadingTime;
    }
    // 等待 loading 延迟时间
    await new Promise(resolve => setTimeout(resolve, actualLoadingTime));
  };

  return {
    ...toRefs(state),
    getTableList,
    search,
    reset,
    handleSizeChange,
    handleCurrentChange,
    updatedTotalParam
  };
};
