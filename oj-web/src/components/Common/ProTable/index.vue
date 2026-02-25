<template>
  <!-- 查询表单 card -->
  <SearchForm
    v-show="isShowSearch"
    :search="_search"
    :reset="_reset"
    :columns="searchColumnsData"
    :search-param="searchParam"
    :search-col="searchCol"
  />

  <!-- 表格内容 card -->
  <div class="card table-main">
    <!-- 表格头部 操作按钮 -->
    <div class="table-header">
      <div class="header-button-lf">
        <slot name="tableHeader" :selected-list-ids="selectedListIds" :selected-list="selectedList" :is-selected="isSelected" />
      </div>
      <div v-if="toolButton" class="header-button-ri">
        <slot name="toolButton">
          <el-button v-if="showToolButton('refresh')" :icon="Refresh" circle @click="getTableList" />
          <el-button v-if="showToolButton('setting') && columns.length" :icon="Operation" circle @click="openColSetting" />
          <el-button
            v-if="showToolButton('search') && searchColumnsData.length"
            :icon="Search"
            circle
            @click="isShowSearch = !isShowSearch"
          />
        </slot>
      </div>
    </div>
    <!-- 表格主体 -->
    <el-table
      v-if="reload"
      :class="randomTableClass"
      ref="tableRef"
      v-bind="$attrs"
      :data="processTableData"
      :border="border"
      :row-key="rowKey"
      @selection-change="selectionChange"
      v-loading="loading"
    >
      <!-- 默认插槽 -->
      <slot />
      <template v-for="item in tableColumns" :key="item">
        <!-- selection || radio || index || expand || sort -->
        <el-table-column
          v-if="item.type && columnTypes.includes(item.type)"
          v-bind="item"
          :align="item.align ?? 'center'"
          :reserve-selection="item.type === 'selection'"
        >
          <template #default="scope">
            <!-- expand -->
            <template v-if="item.type == 'expand'">
              <component :is="item.render" v-bind="scope" v-if="item.render" />
              <slot v-else :name="item.type" v-bind="scope" />
            </template>
            <!-- radio -->
            <el-radio v-if="item.type == 'radio'" v-model="radio" :value="scope.row[rowKey]">
              <i />
            </el-radio>
            <!-- sort -->
            <el-tag v-if="item.type == 'sort'" type="primary" class="move">
              <el-icon>
                <DCaret />
              </el-icon>
            </el-tag>
          </template>
        </el-table-column>
        <!-- other -->
        <TableColumn v-if="!item.type && item.prop && item.isShow" :column="item">
          <template v-for="slot in Object.keys($slots)" #[slot]="scope">
            <slot :name="slot" v-bind="scope" />
          </template>
        </TableColumn>
      </template>
      <!-- 插入表格最后一行之后的插槽 -->
      <template #append>
        <slot name="append" />
      </template>
      <!-- 无数据 -->
      <template #empty>
        <div class="table-empty">
          <slot name="empty">
            <img src="../../../assets/images/notData.png" alt="notData" />
            <div>暂无数据</div>
          </slot>
        </div>
      </template>
    </el-table>
    <!-- 分页组件 -->
    <slot name="pagination">
      <Pagination
        v-if="pagination"
        :pageable="pageable"
        :handle-size-change="handleSizeChange"
        :handle-current-change="handleCurrentChange"
      />
    </slot>
  </div>
  <!-- 列设置 -->
  <ColSetting v-if="toolButton" ref="colRef" v-model:col-setting="colSetting" />
</template>

<script setup lang="ts">
import { ref, watch, computed, provide, onMounted, reactive, unref, nextTick } from 'vue';
import { useTable } from '@/hooks/useTable';
import { useSelection } from '@/hooks/useSelection';
import { Refresh, Operation, Search, DCaret } from '@element-plus/icons-vue';
import { generateUUID, handleProp } from '@/utils';
import SearchForm from '@/components/Common/SearchForm/index.vue';
import Pagination from './components/Pagination.vue';
import ColSetting from './components/ColSetting.vue';
import TableColumn from './components/TableColumn.vue';
import type { ColumnProps, ProTableProps, TypeProps } from '@/components/Common/ProTable/interface';
import Sortable from 'sortablejs';

defineOptions({
  name: 'ProTable',
  inheritAttrs: false
});

// 接受父组件参数，配置默认值
const props = withDefaults(defineProps<ProTableProps>(), {
  columns: () => [],
  searchColumns: () => [],
  requestAuto: true,
  pagination: true,
  initParam: {},
  border: true,
  toolButton: true,
  rowKey: 'id',
  searchCol: () => ({ xs: 1, sm: 2, md: 2, lg: 3, xl: 4 })
});

const randomTableClass = 'random-' + generateUUID();

// 是否显示搜索模块
const isShowSearch = ref(true);

// 表格 DOM 元素
const tableRef = ref();

// column 列类型
const columnTypes: TypeProps[] = ['selection', 'radio', 'index', 'expand', 'sort'];

// 控制 ToolButton 显示
const showToolButton = (key: 'refresh' | 'setting' | 'search') => {
  return Array.isArray(props.toolButton) ? props.toolButton.includes(key) : props.toolButton;
};

// 单选值
const radio = ref('');

// 表格多选 Hooks
const { selectionChange, selectedList, selectedListIds, isSelected } = useSelection(props.rowKey);

const reload = ref(true);
const refresh = () => {
  reload.value = false;
  nextTick(() => (reload.value = true));
};

// 表格操作 Hooks
const {
  tableData,
  pageable,
  searchParam,
  searchInitParam,
  getTableList,
  search,
  reset,
  handleSizeChange,
  handleCurrentChange,
  loading
} = useTable(props.requestApi, props.initParam, props.pagination, props.dataCallback, props.requestError, props.loadingTime);

// 清空选中数据列表
const clearSelection = () => tableRef.value?.clearSelection();

// 初始化表格数据 && 拖拽排序
onMounted(() => {
  dragSort();
  props.requestAuto && getTableList();
  props.data && (pageable.value.total = props.data.length);
});

// 处理表格数据
const processTableData = computed(() => {
  if (!props.data) return tableData.value;
  if (!props.pagination) return props.data;
  return props.data.slice(
    (pageable.value.pageNum - 1) * pageable.value.pageSize,
    pageable.value.pageSize * pageable.value.pageNum
  );
});

// 监听页面 initParam 改化，重新获取表格数据
watch(() => props.initParam, getTableList, { deep: true });

// 接收 columns 并设置为响应式
const tableColumns = reactive<ColumnProps[]>(props.columns);

// 定义 enumMap 存储 enum 值（避免异步请求无法格式化单元格内容 || 无法填充搜索下拉选择）
const enumMap = ref(new Map<string, { [key: string]: any }[]>());
const setEnumMap = async (col: ColumnProps) => {
  if (!col.enum) return;
  // 如果当前 enumMap 存在相同的值 return
  if (enumMap.value.has(col.prop!) && (typeof col.enum === 'function' || enumMap.value.get(col.prop!) === col.enum)) return;
  // 当前 enum 为静态数据，则直接存储到 enumMap
  if (typeof col.enum !== 'function') return enumMap.value.set(col.prop!, unref(col.enum!));

  // 为了防止接口执行慢，而存储慢，导致重复请求，所以预先存储为[]，接口返回后再二次存储
  enumMap.value.set(col.prop!, []);

  // 当前 enum 为后台数据需要请求数据，则调用该请求接口，并存储到 enumMap
  const { data } = await col.enum();
  enumMap.value.set(col.prop!, data);
};

provide('enumMap', enumMap);

// 扁平化 columns
const flatColumnsFunc = (columns: ColumnProps[], flatArr: ColumnProps[] = []) => {
  columns.forEach(async col => {
    if (col._children?.length) flatArr.push(...flatColumnsFunc(col._children));
    flatArr.push(col);

    // 给每一项 column 添加 isShow && isFilterEnum 默认属性
    col.isShow = col.isShow ?? true;
    col.isFilterEnum = col.isFilterEnum ?? true;

    // 设置 enumMap
    setEnumMap(col);
  });
  return flatArr.filter(item => !item._children?.length);
};
flatColumnsFunc(tableColumns);

// 过滤需要搜索的配置项
const searchColumnsData = props.searchColumns;

// 设置搜索表单排序默认值 && 设置搜索表单项的默认值
searchColumnsData.forEach((column, index) => {
  column.order = column.order ?? index + 2;
  const key = column.key ?? handleProp(column.prop!);
  const defaultValue = column.defaultValue;
  if (defaultValue !== undefined && defaultValue !== null) {
    searchInitParam.value[key] = defaultValue;
    searchParam.value[key] = defaultValue;
  }
});

// 列设置 ==> 过滤掉不需要设置的列
const colRef = ref();
const colSetting = tableColumns.filter(item => !columnTypes.includes(item.type!) && item.prop !== 'operation' && item.isShow);
const openColSetting = () => colRef.value.openColSetting();

// 定义 emit 事件
const emit = defineEmits<{
  search: [];
  reset: [];
  dargSort: [{ newIndex?: number; oldIndex?: number }];
}>();

const _search = () => {
  search();
  emit('search');
};

const _reset = () => {
  reset();
  emit('reset');
};

// 拖拽排序
const dragSort = () => {
  const selector = `.${randomTableClass} .el-table__body-wrapper tbody`;
  const tbody = document.querySelector(selector) as HTMLElement;
  Sortable.create(tbody, {
    handle: '.move',
    animation: 300,
    onEnd({ newIndex, oldIndex }) {
      const [removedItem] = processTableData.value.splice(oldIndex!, 1);
      processTableData.value.splice(newIndex!, 0, removedItem);
      emit('dargSort', { newIndex, oldIndex });
    }
  });
};

// 暴露给父组件的参数和方法(外部需要什么，都可以从这里暴露出去)
defineExpose({
  element: tableRef,
  tableData: processTableData,
  radio,
  pageable,
  searchParam,
  searchInitParam,
  getTableList,
  search,
  reset,
  handleSizeChange,
  handleCurrentChange,
  clearSelection,
  enumMap,
  isSelected,
  selectedList,
  selectedListIds,
  refresh
});
</script>
