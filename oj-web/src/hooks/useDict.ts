import { onMounted } from 'vue';
import { useOptionsStore } from '@/stores/modules/options';

/**
 * 自定义 Hook，用于批量刷新字典数据
 * @param typeCode 字典类型数组
 */
export function useDict(typeCode: string[]) {
  const optionsStore = useOptionsStore();
  onMounted(async () => {
    await optionsStore.getDictByCodes(typeCode);
  });
}
