import { defineStore } from 'pinia';
import { ref } from 'vue';
import piniaPersistConfig from '@/stores/helper/persist';
import type { ILogin } from '@/api/interface/system/admin/login';

export const useUserStore = defineStore(
  'user',
  () => {
    const token = ref('');
    const userInfo = ref<ILogin.UserInfo>({
      username: '',
      name: ''
    });
    function setToken(tokenStr: string) {
      token.value = tokenStr;
    }

    // Set setUserInfo
    function setUserInfo(info: ILogin.UserInfo) {
      userInfo.value = info;
    }

    function clear() {
      token.value = '';
      userInfo.value = {
        username: '',
        name: ''
      };
    }

    return {
      token,
      userInfo,
      setToken,
      setUserInfo,
      clear
    };
  },
  {
    persist: piniaPersistConfig('user')
  }
);
