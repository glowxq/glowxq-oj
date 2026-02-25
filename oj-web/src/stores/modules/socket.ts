import { useUserStore } from '@/stores/modules/user';
import { defineStore } from 'pinia';
import mittBus from '@/utils/mittBus';
import { CHANNEL_DEFAULT, CHANNEL_KICK_OFF, UPGRADE_CHANNEL } from '@/config/consts';
import { LOGIN_URL } from '@/config';
import router from '@/router';
import { ref } from 'vue';
import { ElMessageBox } from 'element-plus';
import { useAuthStore } from '@/stores/modules/auth';
import { matchCode, WebsocketBusinessType } from '@/enums/oj/websocket/BusinessType';

// 是否使用socket 当 import.meta.env.VITE_SOCKET_URL 不为空时，启用websocket
const useSocket = Object.hasOwn(import.meta.env, 'VITE_SOCKET_URL') && import.meta.env.VITE_SOCKET_URL;

// 获取WebSocket URL并确保协议正确
const getSocketUrl = () => {
  const configUrl = import.meta.env.VITE_SOCKET_URL;
  if (!configUrl) return '';

  // 如果配置的URL已经是正确的WebSocket协议，直接使用
  if (configUrl.startsWith('ws://') || configUrl.startsWith('wss://')) {
    return configUrl;
  }

  // 如果配置的是HTTP协议，转换为对应的WebSocket协议
  if (configUrl.startsWith('https://')) {
    return configUrl.replace('https://', 'wss://');
  }
  if (configUrl.startsWith('http://')) {
    return configUrl.replace('http://', 'ws://');
  }

  // 根据当前页面协议自动选择WebSocket协议
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
  return `${protocol}//${configUrl}`;
};

const socketUrl = getSocketUrl();
const MAX_RECONNECT_COUNT = 10;

/**
 * 使用socket
 * @param url
 * @returns {{init: (function(): void), socket: null}}
 */
export const useSocketStore = defineStore('socket', () => {
  /**
   * 定义socket变量
   *
   * @type {WebSocket|null}
   */
  const socket = ref<WebSocket | null>(null);

  const canReconnect = ref(true);

  const reconnectCount = ref(0);

  const _onOpen = () => {
    canReconnect.value = true;
    reconnectCount.value = 0;
  };

  /**
   * 处理不同频道的消息
   * @param res 消息响应数据
   * @param userStore 用户存储
   * @param authStore 认证存储
   */
  const handleChannelMessage = (res: any, userStore: any, authStore: any) => {
    switch (res.channel) {
      case CHANNEL_DEFAULT:
        break;
      case CHANNEL_KICK_OFF:
        close();
        // 1.清除 Token
        userStore.clear();
        authStore.clear();
        ElMessageBox.alert('您已经被强制踢下线了！', '温馨提示', {
          confirmButtonText: '确定',
          type: 'warning',
          callback: () => {
            // 2.重定向到登陆页
            router.replace(LOGIN_URL).then(r => r);
          }
        });
        break;
      case UPGRADE_CHANNEL:
        /*        close();
                        // 1.清除 Token
                        userStore.clear();
                        authStore.clear();

                        ElMessageBox.alert(res.data + '！', '温馨提示', {
                          confirmButtonText: '确定',
                          type: 'warning',
                          callback: () => {
                            // 2.重定向到登陆页
                            router.replace(LOGIN_URL);
                          }
                        });*/
        break;

      default:
        mittBus.emit(`socket.${res.channel}`, res.data);
    }
  };

  /**
   * 处理不同业务类型的消息
   * @param res 消息响应数据
   */
  const handleBusinessTypeMessage = (res: any) => {
    if (!res.businessType) return;

    switch (res.businessType) {
      case WebsocketBusinessType.UNKNOWN.code:
        console.log('收到未知业务类型消息:', res.data);
        break;
      default:
        // 尝试通过matchCode查找对应的业务类型
        const businessType = matchCode(res.businessType);
        if (businessType) {
          mittBus.emit(`socket.${businessType.code}`, res.data);
        } else {
          console.log('未处理的业务类型:', res.businessType);
        }
    }
  };

  const _onMessage = (event: MessageEvent) => {
    const { data } = event;
    const userStore = useUserStore();
    const authStore = useAuthStore();
    try {
      const res = JSON.parse(data);
      // 处理频道消息
      handleChannelMessage(res, userStore, authStore);

      // 处理业务类型消息
      handleBusinessTypeMessage(res);

      console.log('接收到的消息:', res);
    } catch (e) {
      /* empty */
    }
  };

  const _onError = (event: Event) => {
    mittBus.emit('socket.error', event);
  };

  const _onClose = () => {
    socket.value = null;
    if (canReconnect.value) {
      handleReconnect();
    }
  };

  const handleReconnect = () => {
    let timeout;
    if (reconnectCount.value < MAX_RECONNECT_COUNT) {
      timeout = Math.min(10000 * Math.pow(2, reconnectCount.value), 30000); // 指数退避算法
    } else {
      timeout = 60000; // 超过最大次数次后，每分钟重试一次
    }
    setTimeout(() => {
      reconnectCount.value++;
      open();
    }, timeout);
  };

  /**
   * 初始化开启socket
   */
  const open = () => {
    if (!useSocket) return;
    if (socket.value) return;
    const userStore = useUserStore();
    // 建立WebSocket连接
    const webSocket = new WebSocket(socketUrl, [userStore.token]);

    // 监听WebSocket事件
    webSocket.onopen = _onOpen;
    webSocket.onmessage = _onMessage;
    webSocket.onerror = _onError;
    webSocket.onclose = _onClose;

    // 连接时处理
    socket.value = webSocket;
  };

  /**
   * 关闭socket
   */
  const close = () => {
    if (!useSocket) return;
    if (!socket.value) return;
    canReconnect.value = false;
    reconnectCount.value = 0;
    socket.value.close();
    socket.value = null;
  };

  const send = (data: any, channel: string = CHANNEL_DEFAULT) => {
    if (!socket.value || socket.value.readyState !== 1) return;
    if (typeof data !== 'object') {
      data = { data };
    }
    const msgData = {
      channel,
      data
    };
    socket.value.send(JSON.stringify(msgData));
  };

  const sendMessage = (data: any) => {
    if (!socket.value || socket.value.readyState !== 1) return;
    socket.value.send(JSON.stringify(data));
  };

  return {
    open,
    send,
    sendMessage,
    close
  };
});
