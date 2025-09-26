<template>
  <div class="login-container">
    <div class="login-form">
      <h2>会员管理 - BuildAdmin</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">用户名</label>
          <input 
            type="text" 
            id="username" 
            v-model="username" 
            placeholder="请输入用户名" 
            required
          >
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input 
            type="password" 
            id="password" 
            v-model="password" 
            placeholder="请输入密码" 
            required
          >
        </div>
        <button type="submit" class="login-button">登录</button>
      </form>
      <p class="error-message" v-if="errorMessage">{{ errorMessage }}</p>
    </div>

    <button @click="handleTest">Test</button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import UserApi from '../apis/userApi';

const username = ref('');
const password = ref('');
const errorMessage = ref('');
const router = useRouter();

// 测试函数
const handleTest = async () => {
  try {
    const response = await UserApi.test('test');
    console.log('test', response);
    //const tmpRes = await CustomerApi.getCustomerList();
  } catch (error) {
    console.error('test error:', error);
  }
}

// 登录函数
const handleLogin = async () => {
  try {
    errorMessage.value = '';
    const response = await UserApi.login(username.value, password.value);
    
    if (response.code === 200 && response.data) {
      // 登录成功，存储token并跳转到首页
      localStorage.setItem('token', response.data);
      router.push('/index');
    } else {
      errorMessage.value = response.msg || '登录失败，请检查用户名和密码';
      console.error('登录失败:', response.msg);
    }
  } catch (error) {
    console.error('登录错误:', error);
    errorMessage.value = '网络错误，请稍后重试';
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.login-form {
  width: 100%;
  max-width: 400px;
  padding: 2rem;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.login-form h2 {
  text-align: center;
  margin-bottom: 2rem;
  color: #333;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #666;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #4285f4;
}

.login-button {
  width: 100%;
  padding: 0.75rem;
  background-color: #4285f4;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-button:hover {
  background-color: #3367d6;
}

.error-message {
  color: #d32f2f;
  text-align: center;
  margin-top: 1rem;
}
</style>