<template>
  <div class="welcome-container">
    <div class="header">
      <div class="header">
      <h1>会员管理 - BuildAdmin</h1>
      <div class="user-info">
        <span>Admin</span>
        <button @click="handleLogout" class="logout-button">退出登录</button>
      </div>
    </div>
      <h1>会员管理 - BuildAdmin</h1>
      <div class="user-info">
        <span>Admin</span>
        <button @click="handleLogout" class="logout-button">退出登录</button>
      </div>
    </div>
    
    <div class="toolbar">
      <button class="btn btn-primary">添加</button>
      <button class="btn btn-secondary">编辑</button>
      <button class="btn btn-danger">删除</button>
      <button class="btn btn-default">刷新</button>
    </div>
    
    <div class="table-container">
      <table class="user-table">
        <thead>
          <tr>
            <th>头像</th>
            <th>性别</th>
            <th>手机号</th>
            <th>最后登录IP</th>
            <th>最后登录时间</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td><img src="/vite.svg" alt="用户头像" class="avatar" /></td>
            <td><span class="gender-tag">女</span></td>
            <td>18888888889</td>
            <td>58.33.6.3</td>
            <td>2025-09-21 14:00:26</td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <div class="pagination">
      <button class="page-btn">上一页</button>
      <button class="page-btn">下一页</button>
      <span>前往</span>
      <input type="number" value="1" min="1" class="page-input">
      <span>页</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import UserApi from '../apis/userApi';

const router = useRouter();

// 退出登录函数
const handleLogout = async () => {
  try {
    await UserApi.logout();
    localStorage.removeItem('token');
    router.push('/login');
  } catch (error) {
    console.error('退出登录错误:', error);
    // 即使出错也清除token并跳转登录页
    localStorage.removeItem('token');
    router.push('/login');
  }
};
</script>

<style scoped>
.welcome-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h1 {
  font-size: 24px;
  color: #333;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logout-button {
  padding: 8px 16px;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-primary {
  background-color: #2196f3;
  color: white;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-default {
  background-color: #6c757d;
  color: white;
}

.table-container {
  margin-bottom: 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
}

.user-table th, .user-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.user-table th {
  background-color: #f8f9fa;
  font-weight: 600;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.gender-tag {
  padding: 4px 8px;
  background-color: #e3f2fd;
  color: #1976d2;
  border-radius: 12px;
  font-size: 12px;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: center;
}

.page-btn {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background-color: white;
  cursor: pointer;
  border-radius: 4px;
}

.page-input {
  width: 60px;
  padding: 6px;
  border: 1px solid #ddd;
  border-radius: 4px;
  text-align: center;
}
</style>