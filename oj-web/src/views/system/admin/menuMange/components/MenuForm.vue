<template>
  <el-dialog v-model="visible" :title="`${paramsProps.title}`" :destroy-on-close="true" width="800px" draggable append-to-body>
    <el-form
      ref="ruleFormRef"
      label-width="100px"
      label-suffix=" :"
      :rules="rules"
      :model="paramsProps.row"
      @submit.enter.prevent="handleSubmit"
    >
      <el-form-item label="上级" prop="pid">
        <el-tree-select
          v-model="paramsProps.row.pid"
          :data="parentMenus"
          check-strictly
          placeholder="请选择上级"
          :render-after-expand="false"
          clearable
          :default-expand-all="true"
          :props="treeProps"
        />
      </el-form-item>

      <el-form-item label="类型" prop="menuTypeCd">
        <el-radio-group v-model="paramsProps.row.menuTypeCd">
          <el-radio-button :value="MENU_DIR">
            {{ menuLabel(MENU_DIR, '目录') }}
          </el-radio-button>
          <el-radio-button :value="MENU_PAGE">
            {{ menuLabel(MENU_DIR, '菜单') }}
          </el-radio-button>
          <el-radio-button :value="MENU_BTN">
            {{ menuLabel(MENU_DIR, '按钮') }}
          </el-radio-button>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="图标" prop="icon" v-if="paramsProps.row.menuTypeCd !== MENU_BTN">
        <IconChoose v-model="paramsProps.row.icon" placeholder="请填写图标" clearable />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="名称" prop="title">
            <el-input v-model="paramsProps.row.title" placeholder="请填写名称" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="排序" prop="sort">
            <el-input-number v-model="paramsProps.row.sort" :precision="0" :min="1" :max="999999" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="菜单类型" prop="type">
        <EnumSelect
          v-model="paramsProps.row.type"
          :enum-data="MenuType"
          type="tab"
          placeholder="请选择菜单类型"
        />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12" v-if="paramsProps.row.menuTypeCd !== MENU_BTN">
          <el-form-item label="是否外链" prop="isLink">
            <el-radio-group v-model="paramsProps.row.isLink">
              <el-radio value="T" border> 是 </el-radio>
              <el-radio value="F" border> 否 </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>

        <el-col :span="12" v-if="paramsProps.row.menuTypeCd !== MENU_BTN && paramsProps.row.isLink === 'T'">
          <el-form-item label="外链地址" prop="redirect">
            <el-input v-model="paramsProps.row.redirect" placeholder="请填写外链地址" clearable />
          </el-form-item>
        </el-col>

        <el-col :span="12" v-if="paramsProps.row.menuTypeCd !== MENU_BTN">
          <el-form-item label="路由名称" prop="name">
            <el-input v-model="paramsProps.row.name" placeholder="请填写路由名称" clearable />
          </el-form-item>
        </el-col>

        <el-col :span="12" v-if="paramsProps.row.menuTypeCd !== MENU_BTN">
          <el-form-item label="路由地址" prop="path">
            <el-input v-model="paramsProps.row.path" placeholder="请填写路由地址" clearable />
          </el-form-item>
        </el-col>

        <el-col :span="12" v-if="paramsProps.row.menuTypeCd === MENU_PAGE">
          <el-form-item label="组件路径" prop="component">
            <el-input v-model="paramsProps.row.component" placeholder="请填写组件路径" clearable />
          </el-form-item>
        </el-col>

        <el-col :span="12" v-if="paramsProps.row.menuTypeCd !== MENU_DIR">
          <el-form-item label="权限" prop="permissions">
            <el-input v-model="paramsProps.row.permissions" placeholder="请填写权限" clearable />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="显示状态" prop="isHidden">
            <el-radio-group v-model="paramsProps.row.isHidden">
              <el-radio value="F" border> 显示 </el-radio>
              <el-radio value="T" border> 隐藏 </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>

        <el-col :span="12" v-if="paramsProps.row.menuTypeCd === MENU_PAGE">
          <el-form-item label="是否全屏" prop="isFull">
            <el-radio-group v-model="paramsProps.row.isFull">
              <el-radio value="T" border> 是 </el-radio>
              <el-radio value="F" border> 否 </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>

        <el-col :span="12" v-if="paramsProps.row.menuTypeCd === MENU_PAGE">
          <el-form-item label="固定标签页" prop="isAffix">
            <el-radio-group v-model="paramsProps.row.isAffix">
              <el-radio value="T" border> 是 </el-radio>
              <el-radio value="F" border> 否 </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="paramsProps.row.menuTypeCd === MENU_PAGE">
          <el-form-item label="是否缓存" prop="isKeepAlive">
            <el-radio-group v-model="paramsProps.row.isKeepAlive">
              <el-radio value="T" border> 是 </el-radio>
              <el-radio value="F" border> 否 </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="visible = false"> 取消 </el-button>
      <el-button type="primary" @click="handleSubmit"> 确定 </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { MENU_BTN, MENU_DIR, MENU_PAGE } from '@/config/consts';
import { useOptionsStore } from '@/stores/modules/options';
import IconChoose from '@/components/Common/IconChoose/index.vue';
import { getBtnExits, getMenuTree } from '@/api/modules/system/admin/menu';
import type { IMenu } from '@/api/interface/system/admin/menu';
import type { FormItemRule } from 'element-plus/es/components/form/src/types';
import { ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { IS_PREVIEW } from '@/config';
import { MenuType } from '@/enums/oj/system/MenuType';
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue';

defineOptions({
  name: 'MenuForm'
});

const optionsStore = useOptionsStore();
const menuOptions = optionsStore.getDictOptions('menu_type');

const menuLabel = (type: any, defaultLabel = '') => {
  return menuOptions[type] || defaultLabel;
};

const rules = ref({});

const visible = ref(false);
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
});

watch(
  () => [paramsProps.value.row.menuTypeCd, paramsProps.value.row.isLink],
  newVal => {
    const [menuType, isLink] = newVal;
    let ruleData: Record<string, FormItemRule[]> = {
      pid: [{ required: true, message: '请选择上级' }],
      title: [{ required: true, message: '请填写名称' }],
      sort: [{ required: true, message: '请填写排序' }],
      type: [{ required: true, message: '请选择菜单类型' }]
    };
    switch (menuType) {
      case MENU_DIR:
        ruleData['icon'] = [{ required: true, message: '请填写图标' }];
        ruleData['name'] = [{ required: true, message: '请填写路由名称' }];
        ruleData['path'] = [{ required: true, message: '请填写路由地址' }];
        if (isLink === 'T') {
          ruleData['redirect'] = [{ required: true, message: '请填写外链地址' }];
        }
        break;
      case MENU_PAGE:
        ruleData['icon'] = [{ required: true, message: '请填写图标' }];
        if (isLink === 'T') {
          ruleData['redirect'] = [{ required: true, message: '请填写外链地址' }];
        } else {
          ruleData['name'] = [{ required: true, message: '请填写路由名称' }];
          ruleData['path'] = [{ required: true, message: '请填写路由地址' }];
          ruleData['component'] = [{ required: true, message: '请填写组件路径' }];
          ruleData['permissions'] = [{ validator: validatePermission, trigger: 'blur' }];
        }
        break;
      case MENU_BTN:
        ruleData['permissions'] = [{ validator: validatePermission, trigger: 'blur' }];
    }
    rules.value = ruleData;

    if (ruleFormRef.value) {
      ruleFormRef.value.clearValidate();
    }
  }
);

const validatePermission = (rule: any, value: any, callback: any) => {
  getBtnExits({ permissions: value, id: paramsProps.value.row?.id }).then(res => {
    if (res.data.permissionCount > 0) {
      callback(new Error('权限已存在!'));
    } else {
      callback();
    }
  });
};

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params;

  // 如果是新增，默认设置type为ADMIN类型
  if (!params.row.id && !params.row.type) {
    params.row.type = MenuType.ADMIN.code;
  }

  visible.value = true;
  loadParentMenus();
};

const parentMenus = ref<IMenu.Tree[]>([]);

const loadParentMenus = () => {
  getMenuTree({ nodeId: paramsProps.value.row?.id }).then(res => {
    parentMenus.value = res.data;
  });
};
const treeProps = {
  label: 'title',
  value: 'id'
};

// 提交数据（新增/编辑）
const ruleFormRef = ref();
const handleSubmit = () => {
  ruleFormRef.value!.validate(async (valid: boolean) => {
    if (!valid) return;
    if (IS_PREVIEW) {
      return ElMessage.warning({ message: '预览环境，禁止编辑菜单，请谅解！' });
    }
    try {
      // 确保type字段存在
      if (!paramsProps.value.row.type) {
        // 如果未设置，默认为与当前用户界面相同的菜单类型
        paramsProps.value.row.type = MenuType.ADMIN.code;
      }

      await paramsProps.value.api!(paramsProps.value.row);
      ElMessage.success({ message: `${paramsProps.value.title}成功！` });
      paramsProps.value.getTableList!();
      visible.value = false;
    } catch (error) {
      console.log(error);
    }
  });
};

// 获取新菜单或按钮的排序号
const presort = (row: any, pid: string) => {
  // 如果是顶级菜单，默认排序为0
  if (pid === '0') {
    return 0;
  }

  // 对于子级菜单，可返回一个合理的排序值，例如 100
  return 100;
};

const openAddEdit = async (title: string, row: any = {}, isAdd = true) => {
  let orig = {};
  if (isAdd) {
    let pid = row.id || '0';
    const sort = presort(row, pid);
    orig = {
      pid: pid,
      icon: '',
      sort: sort,
      type: MenuType.ADMIN.code, // 默认为管理端菜单
      menuTypeCd: row.menuTypeCd === MENU_DIR ? MENU_PAGE : row.menuTypeCd === MENU_PAGE ? MENU_BTN : MENU_DIR,
      isLink: 'F',
      isHidden: 'F',
      isFull: 'F',
      isAffix: 'F',
      isKeepAlive: 'F'
    };
  }
  // ... existing code ...
};

defineExpose({
  acceptParams
});
</script>

<style scoped lang="scss">
.form-item-tip {
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
  line-height: 1.4;

  i {
    margin-right: 4px;
  }
}
</style>
