<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="菜单列表"
      row-key="id"
      :indent="20"
      :columns="columns"
      :request-api="getTableList"
      :search-columns="searchColumns"
      :pagination="false"
      :default-expand-all="defaultExpandAllKey"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader>
        <el-button v-auth="'sys.menu.create_btn'" type="primary" :icon="CirclePlus" @click="openAddEdit('新增菜单')">
          新增菜单
        </el-button>
        <el-button type="info" :icon="Sort" @click="changeExpand"> 展开/折叠 </el-button>
      </template>
      <!-- 图标 -->
      <template #icon="scope">
        <el-icon :size="18" v-if="scope.row.meta.icon">
          <SvgIcon v-if="scope.row.meta.icon.startsWith('svg-')" :name="scope.row.meta.icon.substring(4)" />
          <component v-else :is="scope.row.meta.icon" />
        </el-icon>
      </template>
      <template #useDataScope="scope">
        <el-switch
          v-if="scope.row.menuTypeCd == '1002002'"
          v-model="scope.row.meta.useDataScope"
          :active-value="'T'"
          :inactive-value="'F'"
          :loading="switchLoading"
          :before-change="() => changeDataScope(scope.row)"
        />
      </template>

      <!-- 菜单操作 -->
      <template #operation="{ row }">
        <el-button
          v-auth="'sys.menu.create_btn'"
          type="primary"
          v-if="row.menuTypeCd !== MENU_BTN"
          link
          :icon="CirclePlus"
          @click="openAddEdit('新增菜单', row)"
        >
          新增
        </el-button>
        <el-button
          v-auth="'sys.menu.update_btn'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑菜单', row, false)"
        >
          编辑
        </el-button>
        <el-button v-auth="'sys.menu.delete_btn'" type="primary" link :icon="Delete" @click="deleteInfo(row)"> 删除 </el-button>
        <el-button v-auth="'sys.menu.gen_btn'" type="primary" link :icon="SetUp" @click="genMenuButtons(row)"> 生成权限 </el-button>
        <el-button v-auth="'sys.menu.sql_btn'" type="primary" link :icon="SoldOut" @click="showSqlInfo(row)"> SQL </el-button>
      </template>
    </ProTable>
    <MenuForm ref="menuFormRef" />
    <el-dialog v-model="showSqlDialog" :title="sqlDialTitle" width="80%">
      <HighCode :code="sqlData" language="sql" title="菜单SQL" class="sql-box" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { Delete, EditPen, CirclePlus, SoldOut, Sort, SetUp } from '@element-plus/icons-vue';
import ProTable from '@/components/Common/ProTable/index.vue';
import { addMenu, deleteMenu, editMenu, exportMenuSql, getMenuInfo, getMenuList, chaneDataRole, genButtons } from '@/api/modules/system/admin/menu';
import MenuForm from '@/views/system/admin/menuMange/components/MenuForm.vue';
import { useHandleData } from '@/hooks/useHandleData';
import { MENU_BTN, MENU_DIR, MENU_PAGE } from '@/config/consts';
import { useOptionsStore } from '@/stores/modules/options';
import type { IMenu } from '@/api/interface/system/admin/menu';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import HighCode from '@/components/Common/HighCode/index.vue';
import { computed, ref, h } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import SvgIcon from '@/components/Common/SvgIcon/index.vue';
import { IS_PREVIEW } from '@/config';
import { MenuType } from '@/enums/oj/system/MenuType';
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue';

defineOptions({
  name: 'MenuManage'
});

const showSqlDialog = ref(false);
const sqlData = ref<string>('');
const rowSqlName = ref<any>({});
const optionsStore = useOptionsStore();
const proTableRef = ref<ProTableInstance>();

// 获取table列表
const getTableList = (params: IMenu.Query) => getMenuList(params);

// 搜索列配置
const searchColumns: SearchProps[] = [
  { prop: 'keywords', label: '关键字', el: 'input' },
  {
    prop: 'type',
    label: '菜单类型',
    el: 'select',
    props: {
      placeholder: '请选择菜单类型'
    },
    enum: Object.values(MenuType),
    fieldNames: { label: 'name', value: 'code' }
  }
];

const defaultExpandAllKey = ref(false);

// 表格配置项
const columns: ColumnProps<Menu.MenuOptions>[] = [
  { type: 'index', label: '#' },
  { prop: 'meta.title', width: 200, label: '名称', align: 'left' },
  {
    prop: 'menuTypeCd',
    label: '类型',
    width: 100,
    tag: true,
    enum: optionsStore.getDictOptions('menu_type'),
    fieldNames: { label: 'codeName', value: 'id', tagType: 'callbackShowStyle' }
  },
  {
    prop: 'type',
    label: '菜单类型',
    width: 120,
    // 直接显示对应的名称，而不是所有选项
    formatter: (row) => {
      // 使用类型断言解决类型问题
      const menuType = Object.values(MenuType).find(item => item.code === (row as any).type);
      return menuType?.name || '-';
    },
    // 添加标签样式
    tag: true,
    // 设置标签类型
    enum: Object.values(MenuType),
    // 设置标签字段映射
    fieldNames: {
      label: 'name',
      value: 'code',
      // 标签样式回调
      tagType: 'success'
    }
  },
  { prop: 'meta.icon', label: '图标', width: 100 },
  { prop: 'sort', label: '排序', width: 100 },
  { prop: 'name', label: '路由名称' },
  { prop: 'path', label: '路由地址' },
  { prop: 'component', label: '组件路径' },
  {
    prop: 'useDataScope',
    label: '数据权限支持',
    width: 100
  },
  { prop: 'permissions', label: '权限', tag: true, width: 200 },
  { prop: 'operation', label: '操作', width: 300, fixed: 'right' }
];
const switchLoading = ref(false);
// 打开 drawer(新增、查看、编辑)
const menuFormRef = ref<InstanceType<typeof MenuForm>>();
const openAddEdit = async (title: string, row: any = {}, isAdd = true) => {
  let orig = {};
  if (isAdd) {
    let pid = row.id || '0';
    const sort = presort(row, pid);
    orig = {
      pid: pid,
      icon: '',
      sort: sort,
      type: MenuType.ADMIN.code,
      menuTypeCd: row.menuTypeCd === MENU_DIR ? MENU_PAGE : row.menuTypeCd === MENU_PAGE ? MENU_BTN : MENU_DIR,
      isLink: 'F',
      isHidden: 'F',
      isFull: 'F',
      isAffix: 'F',
      isKeepAlive: 'F'
    };
  } else {
    const { data } = await getMenuInfo({ id: row.id });
    if (!data) {
      ElMessage.error({ message: `获取菜单详情失败！` });
      return;
    }
    orig = data;
  }
  const params = {
    title,
    row: { ...orig },
    api: isAdd ? addMenu : editMenu,
    getTableList: proTableRef.value?.getTableList
  };
  menuFormRef.value?.acceptParams(params);
};

const presort = (row: any = {}, pid: number) => {
  let cnt;
  if (pid == 0) {
    cnt = proTableRef.value?.tableData?.length || 0;
  } else {
    cnt = row?.children?.length || 0;
  }
  return (cnt + 1) * 100;
};
// 删除信息
const deleteInfo = async (params: Menu.MenuOptions) => {
  if (IS_PREVIEW) {
    return ElMessage.warning({ message: '预览环境，禁止删除菜单，请谅解！' });
  }
  await useHandleData(deleteMenu, { ids: [params.id] }, `删除【${params?.meta?.title}】菜单及以下菜单`);
  proTableRef.value?.getTableList();
};

const showSqlInfo = async (row: any = {}) => {
  rowSqlName.value = row;
  const { data } = await exportMenuSql({ ids: [row.id] });
  showSqlDialog.value = true;
  sqlData.value = data;
};

const genMenuButtons = async (row: any = {}) => {
  if (IS_PREVIEW) {
    return ElMessage.warning({ message: '预览环境，禁止生成权限按钮，请谅解！' });
  }
  try {
    // 弹出输入框，让用户输入code
    const { value: code } = await ElMessageBox.prompt(
      '请输入权限代码',
      '生成权限按钮',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        inputPattern: /^[a-zA-Z0-9_\.]+$/,
        inputErrorMessage: '权限代码格式不正确，只能包含字母、数字、下划线和点',
        inputPlaceholder: '例如：sys.menu.create_btn'
      }
    );
    
    if (!code) {
      return ElMessage.warning('权限代码不能为空');
    }
    
    await ElMessageBox.confirm(`确定要为【${row?.meta?.title}】生成权限按钮吗？`, '温馨提示', {
      type: 'warning'
    });
    
    const { data } = await genButtons({ 
      ids: [row.id],
      code
    });
    
    ElMessage.success('生成权限按钮成功');
    proTableRef.value?.getTableList();
  } catch (error: any) {
    // 用户取消操作不显示错误
    if (error === 'cancel' || error?.type === 'cancel') {
      return;
    }
    console.error('生成权限按钮失败:', error);
    ElMessage.error('生成权限按钮失败');
  }
};

const sqlDialTitle = computed(() => {
  return 'SQL [' + rowSqlName.value?.meta?.title + ' ]' || 'SQL []';
});

const changeExpand = () => {
  defaultExpandAllKey.value = !defaultExpandAllKey.value;
  proTableRef.value?.refresh();
};
const changeDataScope = (params: Menu.MenuOptions) => {
  switchLoading.value = true;
  const menuId = params.id;
  if (IS_PREVIEW && menuId == '85b54322630f43a39296488a5e76ba16') {
    switchLoading.value = false;
    ElMessage.warning({ message: '预览环境，禁止修改，请谅解！' });
    return false;
  }
  const handleSuccess = (resolve: (value: boolean | PromiseLike<boolean>) => void) => {
    setTimeout(() => {
      switchLoading.value = false;
      ElMessage.success('切换数据权限成功');
      resolve(true);
    }, 200);
  };

  const handleError = (reject: (reason?: any) => void) => {
    setTimeout(() => {
      switchLoading.value = false;
      reject();
    }, 200);
  };

  return new Promise((resolve, reject) => {
    if (params.meta.useDataScope === 'T') {
      ElMessageBox.confirm(
        `您确认要关闭菜单 [${params.meta.title}] 数据权限支持吗? 此操作有可能导致数据权限失效 ！！`,
        '温馨提示',
        { type: 'warning' }
      )
        .then(() => {
          chaneDataRole({ id: menuId })
            .then(() => handleSuccess(resolve))
            .catch(() => handleError(reject));
        })
        .catch(() => {
          switchLoading.value = false;
        });
    } else {
      chaneDataRole({ id: menuId })
        .then(() => handleSuccess(resolve))
        .catch(() => handleError(reject));
    }
  });
};
</script>
<style scoped lang="scss">
.sql-box {
  margin: 0 auto;
  width: 90%;
  max-height: 60vh;
  overflow: auto;
}
</style>
