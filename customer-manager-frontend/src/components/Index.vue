<script setup lang="ts">
import { useUserStore } from '@/stores/userStore';
import { computed, onMounted, ref, toRef, toRefs } from 'vue' 

const store = useUserStore();

// 弹窗控制
const showAddModal = ref(false);
const showEditModal = ref(false);
const showDeleteModal = ref(false);
const showDetailModal = ref(false);

const isAllSelected = ref(false); // 全选状态

const users = computed(() => store.user);
const isLoading = computed(() => store.isLoading);
const currentUser = ref(store.currentUser);

//查看详情
const handleView = (user: any) => {
  store.setCurrentCustomer(user);
  currentUser.value = user;
  showDetailModal.value = true;
};
</script>

<template>
  <div class="min-h-screen bg-gray-100 p-4">
    <div class="flex flex-wrap gap-2 mb-4">
      <button 
        class="bg-green-500 text-white px-4 py-2 rounded-md shadow hover:bg-green-600 transition"
      >
        <i class="fa fa-plus mr-1"></i> 添加
      </button>
      
      <button 
        class="bg-blue-500 text-white px-4 py-2 rounded-md shadow hover:bg-blue-600 transition"
  
      >
        <i class="fa fa-edit mr-1"></i> 编辑
      </button>
      
      <button 
        class="bg-red-500 text-white px-4 py-2 rounded-md shadow hover:bg-red-600 transition"
      >
        <i class="fa fa-trash mr-1"></i> 删除
      </button>
    </div>
    <!--会员列表-->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-12">
                <input 
                  type="checkbox" 
                  v-model="isAllSelected"
                  class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                >
              </th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">姓名</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">电话</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">邮箱</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">生日</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="user in users" :key="user.id" class="hover:bg-gray-50">
              <td class="px-4 py-3 whitespace-nowrap">
                <input 
                  type="checkbox" 
            
                  class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                >
              </td>
              <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-500">{{ user.id }}</td>
              <td class="px-4 py-3 whitespace-nowrap text-sm font-medium text-gray-900">{{ user.name }}</td>
              <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-500">{{ user.phone }}</td>
              <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-500">{{ user.email }}</td>
              <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-500">{{ user.birthday }}</td>
              <td class="px-4 py-3 whitespace-nowrap text-sm font-medium">
                <button 
                 @click="handleView(user)"
                  class="text-blue-600 hover:text-blue-900 mr-3"
                >
                  查看
                </button>
                <button 
                  
                  class="text-indigo-600 hover:text-indigo-900"
                >
                  编辑
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
       <!-- 空状态 -->
      <div v-if="users.length === 0 && !isLoading" class="text-center py-10">
        <p class="text-gray-500">暂无会员数据，请点击"添加会员"按钮创建</p>
      </div>
      <!-- 加载状态 -->
      <div v-if="isLoading" class="text-center py-10">
        <i class="fa fa-spinner fa-spin text-gray-500"></i>
        <p class="text-gray-500 mt-2">加载中...</p>
      </div>

    </div>
  </div>
</template>
<style scoped>

</style>
