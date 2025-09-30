<template>
  <el-card class="dynamic-table-container" shadow="hover" :body-style="{ padding: '0 20px 20px' }">
    <!-- 搜索面板 -->
    <div class="search-panel py-6 border-b">
      <el-row :gutter="10" type="flex" align="middle">
        <el-col :lg="16" :xs="24">
          <el-row :gutter="10" type="flex" align="middle">
            <el-col :xs="24" :sm="8">
              <el-select v-model="searchField" placeholder="所有字段" class="search-field w-full">
                <el-option value="">所有字段</el-option>
                <el-option v-for="field in sortedMetadata" :key="field.fieldName" :value="field.fieldName">
                  {{ field.fieldLabel }}
                </el-option>
              </el-select>
            </el-col>
            <el-card class="search-panel" shadow="hover" :body-style="{ padding: '16px' }">
              <el-col :xs="24" :sm="16">
                <el-input v-model="searchKeyword" placeholder="请输入搜索关键词" class="search-input" @keyup.enter="search"
                  :suffix-icon="Search" />
              </el-col>
              <el-col :xs="12" :sm="6" class="mt-2 sm:mt-0">
                <el-button type="primary" @click="search" class="w-full*20% search-button">
                  搜索
                </el-button>
              </el-col>
              <el-col v-if="searchKeyword" :xs="12" :sm="6" class="mt-2 sm:mt-0">
                <el-button @click="resetSearch" class="w-full">
                  重置
                </el-button>
              </el-col>
            </el-card>

          </el-row>
        </el-col>
        <el-col :lg="8" :xs="24" class="mt-2 lg:mt-0">
          <el-button type="success" @click="showAddModal = true" class="w-full lg:w-auto lg:float-right">
            <el-icon>
              <Plus />
            </el-icon>
            新增记录
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 表格 -->
    <div class="table-wrapper py-4">
      <el-table v-if="!isLoading" :data="records" style="width: 100%" border v-loading="isLoading"
        element-loading-text="加载中..." :header-cell-style="{ 'white-space': 'nowrap' }">
        <el-table-column type="selection" width="55" />
        <!-- 姓名列 -->
        <el-table-column prop="data.name" label="姓名*" :show-overflow-tooltip="true" header-align="left" />
        <!-- 电话列 -->
        <el-table-column prop="data.phone" label="电话*" :show-overflow-tooltip="true" header-align="left" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button type="success" size="small" @click="viewRecord(scope.row)">
                查看
              </el-button>
              <el-button type="primary" size="small" @click="editRecord(scope.row)">
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="showDeleteConfirm(scope.row)">
                删除
              </el-button>
            </div>

          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="!isLoading && totalRecords > 0">
      <el-row type="flex" justify="space-between" align="middle">
        <el-col :xs="24" :sm="12" class="text-center sm:text-left">
          <span class="pagination-info">
            共 {{ totalRecords }} 条记录，当前第 {{ currentPage }} / {{ totalPages }} 页
          </span>
        </el-col>
        <el-col :xs="24" :sm="12" class="mt-2 sm:mt-0">
          <el-pagination background layout="prev, pager, next" :total="totalRecords" :page-size="pageSize"
            :current-page="currentPage" @current-change="changePage" style="float: right" />
        </el-col>
      </el-row>
    </div>

    <!-- 批量操作 -->
    <div class="batch-actions" v-if="!isLoading && selectedIds.length > 0">
      <el-row type="flex" align="middle">
        <el-col>
          <el-badge :value="selectedIds.length" type="primary" class="mr-2">
            <span>已选择</span>
          </el-badge>
        </el-col>
        <el-col>
          <el-button type="danger" @click="showBatchDeleteConfirm">
            <el-icon>
              <Delete />
            </el-icon>
            批量删除
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 新增/编辑记录模态框 -->
    <el-dialog v-model="showFormModal" :title="isViewMode ? '查看记录' : (showEditModal ? '编辑记录' : '新增记录')" width="70%"
      destroy-on-close :before-close="closeModal">
      <el-form @submit.prevent="saveRecord" :model="formData" label-width="120px" size="default" :disabled="isViewMode">
        <el-form-item v-for="field in sortedMetadata" :key="field.fieldName"
          :label="`${field.fieldLabel}${field.required ? '*' : ''}`" :prop="field.fieldName"
          :rules="!isViewMode && field.required ? [{ required: true, message: `请输入${field.fieldLabel}`, trigger: 'blur' }] : []"
          class="mb-4">
          <div v-if="field.fieldType === 'text'">
            <el-input v-model="formData[field.fieldName]" :placeholder="isViewMode && !formData[field.fieldName] ? '' : (field.placeholder || `请输入${field.fieldLabel}`)"
              :maxlength="field.maxLength || 200" show-word-limit />
          </div>
          <div v-else-if="field.fieldType === 'number'">
            <el-input-number v-model.number="formData[field.fieldName]"
              :placeholder="isViewMode && !formData[field.fieldName] ? '' : (field.placeholder || `请输入${field.fieldLabel}`)" :min="field.min || 0"
              :max="field.max || 99999999" :precision="field.decimalPlaces || 0" />
          </div>
          <div v-else-if="field.fieldType === 'date'">
            <el-date-picker v-model="formData[field.fieldName]" type="date" :placeholder="isViewMode && !formData[field.fieldName] ? '' : '选择日期'" format="YYYY-MM-DD"
              value-format="YYYY-MM-DD" />
          </div>
          <div v-else-if="field.fieldType === 'textarea'">
            <el-input v-model="formData[field.fieldName]" type="textarea"
              :placeholder="isViewMode && !formData[field.fieldName] ? '' : (field.placeholder || `请输入${field.fieldLabel}`)" :rows="4"
              :maxlength="field.maxLength || 1000" show-word-limit />
          </div>
          <div v-else-if="field.fieldType === 'select' && field.options">
            <el-select v-model="formData[field.fieldName]" :placeholder="isViewMode && !formData[field.fieldName] ? '' : '请选择'" clearable>
              <el-option v-for="option in parseOptions(field.options)" :key="option.value" :label="option.label"
                :value="option.value" />
            </el-select>
          </div>
          <div v-else-if="field.fieldType === 'checkbox'">
            <el-checkbox v-model="formData[field.fieldName]">
              {{ field.placeholder || field.fieldLabel }}
            </el-checkbox>
          </div>
          <div v-else-if="field.fieldType === 'radio' && field.options">
            <el-radio-group v-model="formData[field.fieldName]">
              <el-radio v-for="option in parseOptions(field.options)" :key="option.value" :label="option.value">
                {{ option.label }}
              </el-radio>
            </el-radio-group>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeModal">取消</el-button>
          <el-button v-if="!isViewMode" type="primary" @click="saveRecord">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认模态框 -->
    <el-dialog v-model="showDeleteModal" title="确认删除" width="500px" :before-close="closeDeleteModal" destroy-on-close>
      <div class="text-center py-4">
        <el-alert title="警告" type="warning" :closable="false" class="mb-4">
          <p>确定要删除这条记录吗？</p>
          <p class="text-danger">删除后数据将无法恢复！</p>
        </el-alert>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDeleteModal">取消</el-button>
          <el-button type="danger" @click="confirmDelete">确认删除</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量删除确认模态框 -->
    <el-dialog v-model="showBatchDeleteModal" title="批量删除" width="500px" :before-close="closeBatchDeleteModal"
      destroy-on-close>
      <div class="text-center py-4">
        <el-alert title="警告" type="warning" :closable="false" class="mb-4">
          <p>确定要删除选中的 {{ selectedIds.length }} 条记录吗？</p>
          <p class="text-danger">删除后数据将无法恢复！</p>
        </el-alert>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeBatchDeleteModal">取消</el-button>
          <el-button type="danger" @click="confirmBatchDelete">确认删除</el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { Search, Plus, Delete } from '@element-plus/icons-vue';
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
const isViewMode = ref(false);

// 用于模态框的计算属性
const showFormModal = computed({
  get: () => showAddModal.value || showEditModal.value,
  set: (value) => {
    if (!value) {
      showAddModal.value = false;
      showEditModal.value = false;
    }
  }
});

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
const parseOptions = (optionsString: string): Array<{ label: string; value: string }> => {
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
 * 查看记录（只读）
 */
const viewRecord = (record: DynamicTableRecord) => {
  store.setCurrentRecord(record); // 标记当前操作的记录
  resetForm(); // 重置表单
  if (record.data) {
    Object.entries(record.data).forEach(([key, value]) => {
      if(value!==undefined && value!==null && value!==''){
        formData[key] = value;
      }
    }) // 填充记录数据
  }
  isViewMode.value = true; // 进入查看模式
  showEditModal.value = true; // 复用编辑模态框（通过 isViewMode 区分场景）
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
  isViewMode.value = false;
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
   if (!isViewMode.value) {
    store.sortedMetadata.forEach((field: any) => {
      if (field.defaultValue) {
        formData[field.fieldName] = field.defaultValue;
      }
    });
  }
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
  if (result.success) {
    // 成功保存，关闭模态框
    closeModal();
  } else {
    // 失败情况，根据原因显示不同提示
    if (result.reason === 'nameDuplicate') {
      alert('名称已存在');
    } else if (result.reason === 'phoneDuplicate') {
      alert('手机号已存在');
    } else {
      // 其他失败情况
      alert(result.message || '保存失败，请重试');
    }
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
/* 自定义样式 */
.search-panel {
  margin-bottom: 20px;
}

.table-wrapper {
  margin-bottom: 16px;
}

.pagination {
  margin-top: 20px;
}

.batch-actions {
  margin-top: 16px;
  padding: 12px;
  background: #f0f9ff;
  border: 1px solid #bae7ff;
  border-radius: 4px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .el-dialog {
    width: 95% !important;
  }
}

/* 确保搜索按钮在所有设备上都显示为蓝色 */
.search-button {
  --el-button-bg-color: var(--el-color-primary) !important;
  --el-button-border-color: var(--el-color-primary) !important;
}
.action-buttons {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  width: 100%;
}

.action-buttons .el-button {
  width: 70% ;
  padding: 6px 12px !important; 
  text-align: center !important; 
  box-sizing: border-box !important;
  margin: 0 !important; 
}
</style>