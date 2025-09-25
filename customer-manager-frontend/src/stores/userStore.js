import { defineStore } from 'pinia';
import { ref } from 'vue';
export const useUserStore = defineStore('user', () => {
    const isLogin = ref(false);
    const userInfo = ref({});
    function setLogin(status) {
        isLogin.value = status;
    }
    function setUserInfo(info) {
        userInfo.value = info;
    }
    function logout() {
        isLogin.value = false;
        userInfo.value = {};
    }
    return { isLogin, userInfo, setLogin, setUserInfo, logout };
});
//# sourceMappingURL=userStore.js.map