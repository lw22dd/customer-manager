<template>
  <div class="float-right p-4 flex justify-between items-center mb-6 fixed top-0 right-0">
    <div class="user-info">
      <span>Admin</span>
      <button @click="handleLogout"
        class="ml-2 bg-red-500 text-white px-4 py-2 rounded-md shadow hover:bg-red-600 transition">退出登录</button>
    </div>
  </div>
  <div class="dynamic-table-app min-h-screen bg-gray-100 p-4">
    <div class="max-w-7xl mx-auto">
      <header class="mb-8">
        <h1 class="text-2xl font-bold text-gray-800 mb-2">客户管理系统</h1>
        <p class="text-gray-600">基于动态表设计的客户信息管理平台</p>
      </header>

      <!-- 导航选项卡 -->
      <div class="bg-white rounded-lg shadow mb-6">
        <div class="flex border-b">
          <button class="px-6 py-3 font-medium text-sm"
            :class="{ 'border-b-2 border-blue-500 text-blue-500': activeTab === 'data' }" @click="activeTab = 'data'">
            数据管理
          </button>
          <button class="px-6 py-3 font-medium text-sm"
            :class="{ 'border-b-2 border-blue-500 text-blue-500': activeTab === 'metadata' }"
            @click="activeTab = 'metadata'">
            表头管理
          </button>
        </div>

        <!-- 内容区域 -->
        <div class="p-4">
          <!-- 数据管理视图 -->
          <div v-if="activeTab === 'data'">
            <DynamicTable />
          </div>

          <!-- 表头管理视图 -->
          <div v-if="activeTab === 'metadata'">
            <DynamicTableManager />
          </div>
        </div>
      </div>

      <!-- 系统说明 -->
      <div class="bg-white rounded-lg shadow p-6">
        <h2 class="text-lg font-semibold mb-4">系统说明</h2>
        <div class="space-y-3 text-sm text-gray-600">
          <p>• 本系统采用元数据驱动的动态表设计，可以自由增删改表头字段，无需修改数据库结构。</p>
          <p>• 表头管理：可添加、编辑、删除和排序表字段，支持多种字段类型（文本、数字、日期、选择框等）。</p>
          <p>• 数据管理：可对客户数据进行增删改查，支持搜索、分页和批量操作功能。</p>
          <p>• 数据存储：表内容以JSON格式存储，确保表头变更时兼容新旧数据的查询和展示。</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import DynamicTable from '../components/DynamicTable.vue';
import DynamicTableManager from '../components/DynamicTableManager.vue';
import { useRouter } from 'vue-router';
import UserApi from '../apis/userApi';
// 当前激活的选项卡
const activeTab = ref('data');
const router = useRouter();
const userName = ref('Admin');


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
.dynamic-table-app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}
</style>
