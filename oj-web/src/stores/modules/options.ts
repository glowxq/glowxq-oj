import { defineStore } from 'pinia';
import { getAllDict, getDictByCode } from '@/api/modules/system/admin/dict';
import piniaPersistConfig from '@/stores/helper/persist';
import type { IDict } from '@/api/interface/system/admin/dict';
import { ref } from 'vue';

export const useOptionsStore = defineStore(
  'options',
  () => {
    const isLoaded = ref(false);
    const dictOptions = ref<Record<string, IDict.DictCustom[]>>({});

    async function getAllDictList() {
      if (isLoaded.value) return;
      const { data } = await getAllDict();
      dictOptions.value = data;
      isLoaded.value = true;
    }

    function getDictOptions(type: string) {
      return dictOptions.value[type] || [];
    }

    function setReloadOptions() {
      isLoaded.value = false;
    }

    async function getDictByCodes(typeCodes: string[]) {
      const { data } = await getDictByCode({ typeCode: typeCodes });
      typeCodes.forEach(code => {
        dictOptions.value[code] = data[code];
      });
    }

    return {
      isLoaded,
      dictOptions,
      getAllDictList,
      getDictOptions,
      setReloadOptions,
      getDictByCodes
    };
  },
  {
    persist: piniaPersistConfig('options')
  }
);
