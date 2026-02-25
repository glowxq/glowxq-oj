<template>
  <RenderTableColumn v-bind="column" />
</template>

<script setup lang="tsx">
import { filterEnum, formatValue, handleProp, handleRowAccordingToProp } from '@/utils';
import { inject, ref, useSlots } from 'vue';
import type { ColumnProps, RenderScope, HeaderRenderScope } from '@/components/Common/ProTable/interface';

defineOptions({
  name: 'TableColumn'
});

defineProps<{ column: ColumnProps }>();

const slots = useSlots();

const enumMap = inject('enumMap', ref(new Map()));

// 渲染表格数据
const renderCellData = (item: ColumnProps, scope: RenderScope<any>) => {
  // 处理布尔值，使用 el-tag 显示
  if (typeof handleRowAccordingToProp(scope.row, item.prop!) === 'boolean') {
    const value = handleRowAccordingToProp(scope.row, item.prop!);
    // 如果设置了 booleanTag 为 false 或者设置了 tag 为 true，则不使用标签展示
    if (item.booleanTag === false || item.tag) {
      return value ? 'Yes' : 'No';
    }
    return (
      <el-tag type={value ? 'success' : 'danger'} effect="plain" size="small">
        {value ? 'Yes' : 'No'}
      </el-tag>
    );
  }

  return enumMap.value.get(item.prop) && item.isFilterEnum
    ? filterEnum(handleRowAccordingToProp(scope.row, item.prop!), enumMap.value.get(item.prop), item.fieldNames)
    : formatValue(handleRowAccordingToProp(scope.row, item.prop!));
};

// 获取 tag 类型
const getTagType = (item: ColumnProps, scope: RenderScope<any>) => {
  return (
    filterEnum(handleRowAccordingToProp(scope.row, item.prop!), enumMap.value.get(item.prop), item.fieldNames, 'tag') || 'primary'
  );
};

const RenderTableColumn = (item: ColumnProps) => {
  return (
    <>
      {item.isShow && (
        <el-table-column
          {...item}
          align={item.align ?? 'center'}
          showOverflowTooltip={item.showOverflowTooltip ?? item.prop !== 'operation'}
        >
          {{
            default: (scope: RenderScope<any>) => {
              if (item._children) return item._children.map(child => RenderTableColumn(child));
              if (item.render) return item.render(scope);
              if (slots[handleProp(item.prop!)]) return slots[handleProp(item.prop!)]!(scope);
              if (item.tag) return <el-tag type={getTagType(item, scope)}>{renderCellData(item, scope)}</el-tag>;
              return renderCellData(item, scope);
            },
            header: (scope: HeaderRenderScope<any>) => {
              if (item.headerRender) return item.headerRender(scope);
              if (slots[`${handleProp(item.prop!)}Header`]) return slots[`${handleProp(item.prop!)}Header`]!(scope);
              return item.label;
            }
          }}
        </el-table-column>
      )}
    </>
  );
};
</script>
