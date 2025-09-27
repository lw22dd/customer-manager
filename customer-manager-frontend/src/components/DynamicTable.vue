<template>
  <div class="dynamic-table-container">
    <!-- 搜索面板 -->
    <div class="search-panel">
      <div class="search-input-group">
        <select v-model="searchField" class="search-field">
          <option value="">所有字段</option>
          <option v-for="field in sortedMetadata" :key="field.fieldName" >
            {{ field.fieldLabel }}
          </option>
        </select> 
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="请输入搜索关键词" 
          class="search-input"
          @keyup.enter="search"
        />
        <button class="search-btn" @click="search">搜索</button>
        <button v-if="searchKeyword" class="reset-btn" @click="resetSearch">重置</button>
      </div>
      <button class="add-btn" @click="showAddModal = true">新增记录</button>
    </div>
    
    <!-- 表格 -->
    <div class="table-wrapper">
      <table class="dynamic-table" v-if="!isLoading">
        <thead>
          <tr>
            <th class="checkbox-col">
              <input 
                type="checkbox" 
                v-model="isAllSelected"
                :disabled="records.length === 0"
              />
            </th>
            <th v-for="field in sortedMetadata" :key="field.fieldName">
              {{ field.fieldLabel }}
              <span v-if="field.required" class="required-mark">*</span>
            </th>
            <th class="action-col">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="records.length === 0" class="empty-row">
            <td :colspan="sortedMetadata.length + 2" class="empty-cell">
              {{ isSearching ? '暂无搜索结果' : '暂无数据' }}
            </td>
          </tr>
          <tr v-else v-for="record in records" :key="record.id">
            <td class="checkbox-col">
              <input 
                type="checkbox" 
                :value="record.id"
                v-model="selectedIds"
              />
            </td>
            <td v-for="field in sortedMetadata" :key="field.fieldName" class="data-cell">
              <div v-if="field.fieldType === 'date' && record.data[field.fieldName]">
                {{ formatDate(record.data[field.fieldName]) }}
              </div>
              <div v-else-if="field.fieldType === 'select' && record.data[field.fieldName] && field.options">
                {{ getOptionLabel(field.options, record.data[field.fieldName]) }}
              </div>
              <div v-else-if="field.fieldType === 'checkbox' && record.data[field.fieldName]">
                <input 
                  type="checkbox" 
                  :checked="record.data[field.fieldName] === true || record.data[field.fieldName] === 'true'"
                  disabled
                />
              </div>
              <div v-else-if="field.fieldType === 'radio' && record.data[field.fieldName] && field.options">
                {{ getOptionLabel(field.options, record.data[field.fieldName]) }}
              </div>
              <div v-else>
                {{ record.data[field.fieldName] ?? '-' }}
              </div>
            </td>
            <td class="action-col">
              <button class="btn-edit" @click="editRecord(record)">编辑</button>
              <button class="btn-delete" @click="showDeleteConfirm(record)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <!-- 加载状态 -->
      <div class="loading" v-if="isLoading">
        加载中...
      </div>
    </div>
    
    <!-- 分页 -->
    <div class="pagination" v-if="!isLoading && totalRecords > 0">
      <div class="pagination-info">
        共 {{ totalRecords }} 条记录，当前第 {{ currentPage }} / {{ totalPages }} 页
      </div>
      <div class="pagination-controls">
        <button 
          class="page-btn"
          :disabled="currentPage === 1"
          @click="changePage(currentPage - 1)"
        >
          上一页
        </button>
        <button 
          v-for="page in visiblePages" 
          :key="page"
          class="page-btn"
          :class="{ active: page === currentPage }"
          @click="changePage(page)"
        >
          {{ page }}
        </button>
        <button 
          class="page-btn"
          :disabled="currentPage === totalPages"
          @click="changePage(currentPage + 1)"
        >
          下一页
        </button>
      </div>
    </div>
    
    <!-- 批量操作 -->
    <div class="batch-actions" v-if="!isLoading && selectedIds.length > 0">
      <span>已选择 {{ selectedIds.length }} 项</span>
      <button class="batch-delete-btn" @click="showBatchDeleteConfirm">批量删除</button>
    </div>
    
    <!-- 新增/编辑记录模态框 -->
    <div class="modal" v-if="showAddModal || showEditModal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ showEditModal ? '编辑记录' : '新增记录' }}</h3>
          <button class="modal-close" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveRecord">
            <div v-for="field in sortedMetadata" :key="field.fieldName" class="form-group">
              <label :for="field.fieldName">
                {{ field.fieldLabel }}
                <span v-if="field.required" class="required-mark">*</span>
              </label>
              <div v-if="field.fieldType === 'text'">
                <input 
                  :id="field.fieldName"
                  type="text" 
                  v-model="formData[field.fieldName]"
                  :required="field.required"
                  :placeholder="field.placeholder || `请输入${field.fieldLabel}`"
                  :pattern="field.regex || undefined"
                  class="form-input"
                />
              </div>
              <div v-else-if="field.fieldType === 'number'">
                <input 
                  :id="field.fieldName"
                  type="number" 
                  v-model.number="formData[field.fieldName]"
                  :required="field.required"
                  :placeholder="field.placeholder || `请输入${field.fieldLabel}`"
                  class="form-input"
                />
              </div>
              <div v-else-if="field.fieldType === 'date'">
                <input 
                  :id="field.fieldName"
                  type="date" 
                  v-model="formData[field.fieldName]"
                  :required="field.required"
                  class="form-input"
                />
              </div>
              <div v-else-if="field.fieldType === 'textarea'">
                <textarea 
                  :id="field.fieldName"
                  v-model="formData[field.fieldName]"
                  :required="field.required"
                  :placeholder="field.placeholder || `请输入${field.fieldLabel}`"
                  rows="4"
                  class="form-textarea"
                ></textarea>
              </div>
              <div v-else-if="field.fieldType === 'select' && field.options">
                <select 
                  :id="field.fieldName"
                  v-model="formData[field.fieldName]"
                  :required="field.required"
                  class="form-select"
                >
                  <option value="">请选择</option>
                  <option 
                    v-for="option in parseOptions(field.options)" 
                    :key="option.value"
                    :value="option.value"
                  >
                    {{ option.label }}
                  </option>
                </select>
              </div>
              <div v-else-if="field.fieldType === 'checkbox'">
                <label class="checkbox-label">
                  <input 
                    :id="field.fieldName"
                    type="checkbox" 
                    v-model="formData[field.fieldName]"
                    :required="field.required"
                  />
                  {{ field.placeholder || field.fieldLabel }}
                </label>
              </div>
              <div v-else-if="field.fieldType === 'radio' && field.options">
                <div v-for="option in parseOptions(field.options)" :key="option.value" class="radio-group">
                  <label class="radio-label">
                    <input 
                      :id="`${field.fieldName}-${option.value}`"
                      type="radio" 
                      v-model="formData[field.fieldName]"
                      :value="option.value"
                      :required="field.required"
                    />
                    {{ option.label }}
                  </label>
                </div>
              </div>
            </div>
            <div class="form-actions">
              <button type="button" class="btn-cancel" @click="closeModal">取消</button>
              <button type="submit" class="btn-submit">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>
    
    <!-- 删除确认模态框 -->
    <div class="modal" v-if="showDeleteModal" @click.self="closeDeleteModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>确认删除</h3>
          <button class="modal-close" @click="closeDeleteModal">×</button>
        </div>
        <div class="modal-body">
          <p>确定要删除这条记录吗？</p>
          <p class="warning">删除后数据将无法恢复！</p>
          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="closeDeleteModal">取消</button>
            <button type="button" class="btn-delete-confirm" @click="confirmDelete">确认删除</button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 批量删除确认模态框 -->
    <div class="modal" v-if="showBatchDeleteModal" @click.self="closeBatchDeleteModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>批量删除</h3>
          <button class="modal-close" @click="closeBatchDeleteModal">×</button>
        </div>
        <div class="modal-body">
          <p>确定要删除选中的 {{ selectedIds.length }} 条记录吗？</p>
          <p class="warning">删除后数据将无法恢复！</p>
          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="closeBatchDeleteModal">取消</button>
            <button type="button" class="btn-delete-confirm" @click="confirmBatchDelete">确认删除</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useDynamicTableStore } from '../stores/dynamicTableStore';
import type { DynamicTableRecord } from '../models/DynamicTableRecord';

const store = useDynamicTableStore();

// 从store中解构出需要的状态
const records = computed(() => store.records);
const sortedMetadata = computed(() => store.sortedMetadata);
const isLoading = computed(() => store.isLoading);
const selectedIds = computed(() => store.selectedIds);
const currentPage = computed(() => store.currentPage);
const pageSize = computed(() => store.pageSize);
const totalRecords = computed(() => store.totalRecords);

// 状态
const showAddModal = ref(false);
const showEditModal = ref(false);
const showDeleteModal = ref(false);
const showBatchDeleteModal = ref(false);
const deleteRecordItem = ref<DynamicTableRecord | null>(null);
const searchKeyword = ref('');
const searchField = ref('');
const isSearching = ref(false);

// 表单数据
const formData = reactive<Record<string, any>>({});

// 计算属性
const isAllSelected = computed({
  get: () => store.isAllSelected,
  set: (value) => {
    store.isAllSelected = value;
  }
});

const totalPages = computed(() => {
  return Math.ceil(store.totalRecords / store.pageSize);
});

const visiblePages = computed(() => {
  const pages: number[] = [];
  const total = totalPages.value;
  const current = store.currentPage;
  
  // 简单分页逻辑，显示当前页前后2页
  let start = Math.max(1, current - 2);
  let end = Math.min(total, start + 4);
  
  // 确保至少显示5个页码
  if (end - start < 4) {
    start = Math.max(1, end - 4);
  }
  
  for (let i = start; i <= end; i++) {
    pages.push(i);
  }
  
  return pages;
});

// 方法

/**
 * 格式化日期
 */
const formatDate = (dateString: string): string => {
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN');
  } catch {
    return dateString;
  }
};

/**
 * 解析选项配置
 */
const parseOptions = (optionsString: string): Array<{label: string; value: string}> => {
  try {
    if (optionsString.startsWith('[')) {
      return JSON.parse(optionsString);
    } else {
      // 兼容没有中括号的格式
      return JSON.parse(`[${optionsString}]`);
    }
  } catch {
    return [];
  }
};

/**
 * 获取选项的显示文本
 */
const getOptionLabel = (optionsString: string, value: string): string => {
  const options = parseOptions(optionsString);
  const option = options.find(opt => opt.value === value);
  return option ? option.label : value;
};

/**
 * 搜索
 */
const search = () => {
  isSearching.value = true;
  store.setSearchParams(searchKeyword.value, searchField.value);
};

/**
 * 重置搜索
 */
const resetSearch = () => {
  searchKeyword.value = '';
  searchField.value = '';
  isSearching.value = false;
  store.setSearchParams('', '');
};

/**
 * 切换页码
 */
const changePage = (page: number) => {
  if (page < 1 || page > totalPages.value) return;
  store.loadRecordsWithPage(page);
};

/**
 * 编辑记录
 */
const editRecord = (record: DynamicTableRecord) => {
  // 复制数据到表单
  store.setCurrentRecord(record);
  // 初始化表单数据
  resetForm();
  // 填充现有数据
  if (record.data) {
    Object.assign(formData, { ...record.data });
  }
  showEditModal.value = true;
};

/**
 * 显示删除确认
 */
const showDeleteConfirm = (record: DynamicTableRecord) => {
  deleteRecordItem.value = record;
  showDeleteModal.value = true;
};

/**
 * 显示批量删除确认
 */
const showBatchDeleteConfirm = () => {
  showBatchDeleteModal.value = true;
};

/**
 * 关闭模态框
 */
const closeModal = () => {
  showAddModal.value = false;
  showEditModal.value = false;
  resetForm();
};

/**
 * 关闭删除模态框
 */
const closeDeleteModal = () => {
  showDeleteModal.value = false;
  deleteRecordItem.value = null;
};

/**
 * 关闭批量删除模态框
 */
const closeBatchDeleteModal = () => {
  showBatchDeleteModal.value = false;
};

/**
 * 重置表单
 */
const resetForm = () => {
  // 清空表单数据
  Object.keys(formData).forEach(key => {
    delete formData[key];
  });
  
  // 设置默认值
  store.sortedMetadata.forEach((field: any) => {
    if (field.defaultValue) {
      formData[field.fieldName] = field.defaultValue;
    }
  });
};

/**
 * 保存记录
 */
const saveRecord = async () => {
  // 构建记录对象
  const record: DynamicTableRecord = {
    tableKey: store.currentTableKey,
    data: { ...formData }
  };
  
  // 如果是编辑模式，添加ID
  if (store.currentRecord?.id) {
    record.id = store.currentRecord.id;
  }
  
  // 保存
  const result = await store.saveRecord(record);
  if (result) {
    closeModal();
  } else {
    alert('保存失败，请重试');
  }
};

/**
 * 确认删除
 */
const confirmDelete = async () => {
  if (deleteRecordItem.value?.id) {
    const result = await store.deleteRecord(deleteRecordItem.value.id);
    if (result) {
      closeDeleteModal();
    } else {
      alert('删除失败，请重试');
    }
  }
};

/**
 * 确认批量删除
 */
const confirmBatchDelete = async () => {
  const result = await store.deleteSelectedRecords();
  if (result) {
    closeBatchDeleteModal();
  } else {
    alert('删除失败，请重试');
  }
};

// 初始化时加载数据
onMounted(() => {
  store.initTable('customer');
});
</script>

<style scoped>
.dynamic-table-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 搜索面板样式 */
.search-panel {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e8e8e8;
}

.search-input-group {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  max-width: 600px;
}

.search-field {
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  background: white;
}

.search-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.search-input:focus {
  outline: none;
  border-color: #1890ff;
}

.search-btn, .reset-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.search-btn {
  background: #1890ff;
  color: white;
}

.search-btn:hover {
  background: #40a9ff;
}

.reset-btn {
  background: #f5f5f5;
  color: #666;
  border: 1px solid #d9d9d9;
}

.reset-btn:hover {
  background: #e8e8e8;
}

.add-btn {
  padding: 8px 16px;
  background: #52c41a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.add-btn:hover {
  background: #73d13d;
}

/* 表格样式 */
.table-wrapper {
  overflow-x: auto;
}

.dynamic-table {
  width: 100%;
  border-collapse: collapse;
}

.dynamic-table th,
.dynamic-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e8e8e8;
}

.dynamic-table th {
  background: #fafafa;
  font-weight: 500;
  color: #333;
  position: sticky;
  top: 0;
  z-index: 10;
}

.checkbox-col {
  width: 40px;
  text-align: center;
}

.action-col {
  width: 120px;
  text-align: center;
}

.required-mark {
  color: #ff4d4f;
  margin-left: 4px;
}

.empty-row .empty-cell {
  text-align: center;
  color: #999;
  padding: 40px;
}

.data-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.btn-edit, .btn-delete {
  padding: 4px 12px;
  margin: 0 4px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
}

.btn-edit:hover {
  color: #1890ff;
  border-color: #1890ff;
}

.btn-delete {
  color: #ff4d4f;
  border-color: #ffccc7;
  background: #fff2f0;
}

.btn-delete:hover {
  background: #ffccc7;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e8e8e8;
}

.pagination-info {
  color: #666;
  font-size: 14px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-btn {
  padding: 6px 12px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  border-color: #1890ff;
  color: #1890ff;
}

.page-btn.active {
  background: #1890ff;
  color: white;
  border-color: #1890ff;
}

.page-btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

/* 批量操作样式 */
.batch-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 16px;
  padding: 12px;
  background: #f0f9ff;
  border: 1px solid #bae7ff;
  border-radius: 4px;
  color: #1890ff;
}

.batch-delete-btn {
  padding: 6px 16px;
  background: #ff4d4f;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.batch-delete-btn:hover {
  background: #ff7875;
}

/* 模态框样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e8e8e8;
}

.modal-header h3 {
  margin: 0;
  font-size: 16px;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.3s;
}

.modal-close:hover {
  background: #f5f5f5;
  color: #666;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 4px;
  font-weight: 500;
  color: #333;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #1890ff;
}

.checkbox-label,
.radio-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 0;
}

.radio-group {
  margin-bottom: 4px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e8e8e8;
}

.btn-cancel,
.btn-submit,
.btn-delete-confirm {
  padding: 8px 24px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-submit {
  background: #1890ff;
  color: white;
  border-color: #1890ff;
}

.btn-submit:hover {
  background: #40a9ff;
}

.btn-delete-confirm {
  background: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
}

.btn-delete-confirm:hover {
  background: #ff7875;
}

.warning {
  color: #ff4d4f;
  font-size: 12px;
}
</style>