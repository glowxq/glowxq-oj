import { createPinia } from 'pinia';
import piniaPluginPersistedState from 'pinia-plugin-persistedstate';

// pinia persist
const pinia = createPinia();
pinia.use(piniaPluginPersistedState);

export default pinia;
