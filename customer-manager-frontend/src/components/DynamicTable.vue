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
          <el-button type="success" @click="showAddModal = true" class="w-full lg:w-auto lg:float-right mb-2 lg:mb-0 lg:mr-2">
            <el-icon>
              <Plus />
            </el-icon>
            新增记录
          </el-button>
          <el-dropdown class="w-full lg:w-auto lg:float-right">
            <el-button type="primary" class="w-full">
              <el-icon>
                <Download />
              </el-icon>
              导出
              <el-icon class="el-icon--right">
                <ArrowDown />
              </el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="exportCurrentPage">
                  导出当前页
                </el-dropdown-item>
                <el-dropdown-item @click="exportAll">
                  导出所有数据
                </el-dropdown-item>
                <el-dropdown-item v-if="selectedIds.length > 0" @click="exportSelected">
                  导出选中数据 ({{ selectedIds.length }})
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
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
        <!-- 头像上传 -->
        <el-form-item label="头像" class="mb-4">
          <div class="avatar-upload-container">
            <div class="avatar-preview" v-if="formData.avatar">
              <img :src="formData.avatar" alt="头像预览" class="avatar-image">
            </div>
            <el-upload
              v-if="!isViewMode"
              action=""
              :show-file-list="false"
              :on-change="handleAvatarSelect"
              :auto-upload="false"
              accept="image/*"
            >
              <el-button type="primary" icon="Upload" v-if="!formData.avatar">
                上传头像
              </el-button>
              <el-button v-else type="warning">
                更换头像
              </el-button>
            </el-upload>
          </div>
        </el-form-item>
        
        <el-form-item v-for="field in sortedMetadata" :key="field.fieldName"
          :label="`${field.fieldLabel}${field.required ? '*' : ''}`" :prop="field.fieldName"
          :rules="!isViewMode && field.required ? [{ required: true, message: `请输入${field.fieldLabel}`, trigger: 'blur' }] : []"
          class="mb-4">
          <div v-if="field.fieldType === 'text'">
            <el-autocomplete v-model="formData[field.fieldName]" 
              :fetch-suggestions="(query: string, callback: (results: { value: string }[]) => void) => handleTextSuggestions(field.fieldName, query, callback)"
              :placeholder="isViewMode && !formData[field.fieldName] ? '' : (field.placeholder ||
              `请输入${field.fieldLabel}`)"
              :maxlength="field.maxLength || 200"
              show-word-limit
              @select="(item: { value: string }) => formData[field.fieldName] = item.value"
              clearable
            />
          </div>
          <div v-else-if="field.fieldType === 'number'">
            <el-input-number v-model.number="formData[field.fieldName]"
              :placeholder="isViewMode && !formData[field.fieldName] ? '' : (field.placeholder || `请输入${field.fieldLabel}`)"
              :min="field.min || 0" :max="field.max || 99999999" :precision="field.decimalPlaces || 0" />
          </div>
          <div v-else-if="field.fieldType === 'date'">
            <el-date-picker v-model="formData[field.fieldName]" type="date"
              :placeholder="isViewMode && !formData[field.fieldName] ? '' : '选择日期'" format="YYYY-MM-DD"
              value-format="YYYY-MM-DD" />
          </div>
          <div v-else-if="field.fieldType === 'textarea'">
            <el-input v-model="formData[field.fieldName]" type="textarea"
              :placeholder="isViewMode && !formData[field.fieldName] ? '' : (field.placeholder || `请输入${field.fieldLabel}`)"
              :rows="4" :maxlength="field.maxLength || 1000" show-word-limit />
          </div>
          <div v-else-if="field.fieldType === 'select' && field.options">
            <el-select v-model="formData[field.fieldName]"
              :placeholder="isViewMode && !formData[field.fieldName] ? '' : '请选择'" clearable>
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
import { Search, Plus, Delete, Download, ArrowDown, Upload } from '@element-plus/icons-vue';
import { ref, reactive, computed, onMounted } from 'vue';
import { useDynamicTableStore } from '../stores/dynamicTableStore';
import type { DynamicTableRecord } from '../models/DynamicTableRecord';
import { ElAutocomplete } from 'element-plus';

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

/**
 * 导出当前页数据
 */
const exportCurrentPage = async () => {
  const result = await store.exportCurrentPageData();
  if (result) {
    // 可以添加成功提示
  } else {
    alert('导出当前页数据失败，请重试');
  }
};

/**
 * 导出所有数据
 */
const exportAll = async () => {
  if (totalRecords.value > 100) {
    // 数据量较大时给出提示
    if (!confirm(`确定要导出全部 ${totalRecords.value} 条数据吗？这可能需要一些时间。`)) {
      return;
    }
  }
  
  const result = await store.exportAllData();
  if (result) {
    // 可以添加成功提示
  } else {
    alert('导出所有数据失败，请重试');
  }
};

/**
 * 导出选中数据
 */
const exportSelected = async () => {
  const result = await store.exportSelectedData();
  if (result) {
    // 可以添加成功提示
  } else {
    alert('导出选中数据失败，请重试');
  }
};

const totalPages = computed(() => {
  return Math.ceil(store.totalRecords / store.pageSize);
});

// 方法

/**
 * 文本字段联想
 */
const handleTextSuggestions = (
  fieldName: string,
  query: string,
  callback: (results: { value: string }[]) => void
) => {
  // 1. 从已有记录中提取当前字段的所有非空值
  const allValues = records.value
    .map(record => record.data?.[fieldName] || '') // 取该字段的值
    .filter(value => typeof value === 'string' && value.trim() !== ''); // 过滤空值和非字符串

  // 2. 过滤出包含查询词的结果，并去重（避免重复显示）
  const uniqueSuggestions = Array.from(new Set(
    allValues.filter(value => value.toLowerCase().includes(query.toLowerCase()))
  )).map(value => ({ value })); // 格式化为{ value: string }结构

  // 3. 调用回调返回联想结果（延迟100ms提升体验，避免输入过快）
  setTimeout(() => {
    callback(uniqueSuggestions);
  }, 100);
};/**
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
      if (value !== undefined && value !== null && value !== '') {
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
 * 处理头像选择逻辑
 */
const handleAvatarSelect = async (uploadFile: any) => {
  try {
    const file = uploadFile.raw;
    console.log('选择的文件：', file);
    
    // 文件类型校验
    const isImage = file.type.startsWith('image/');
    if (!isImage) {
      alert('请上传图片文件');
      return;
    }
    
    // 文件大小校验
    const isLt2M = file.size / 1024 / 1024 < 2;
    if (!isLt2M) {
      alert('上传头像图片大小不能超过 2MB!');
      return;
    }
    
    // 使用FileReader读取文件为Base64
    const reader = new FileReader();
    reader.readAsDataURL(file);
    
    reader.onload = async () => {
      const base64Image = reader.result as string;
      
      // 如果是编辑模式，调用上传头像API
      if (store.currentRecord?.id) {
        const result = await store.uploadAvatar(store.currentRecord.id.toString(), base64Image);
        console.log('上传结果：', result);
        if (result) {
          // 更新全局表单中的头像数据
          formData.avatar = base64Image;
        } else {
          alert('头像更新失败，请重试');
        }
      } else {
        // 新增模式，先保存到表单，待保存记录时一并提交
        formData.avatar = base64Image;
      }
    };
    
    reader.onerror = () => {
      alert('图片读取失败，请重试');
    };
  } catch (error) {
    console.error('处理头像失败:', error);
    alert('处理头像失败，请重试');
  }
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
    
    // 设置头像默认值
    formData.avatar = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==';
  }
};

/**
 * 保存记录
 */
const saveRecord = async () => {
  // 创建表单数据副本，确保包含头像信息
  const recordData = { ...formData };
  
  // 构建记录对象
  const record: DynamicTableRecord = {
    tableKey: store.currentTableKey,
    data: recordData
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
    width: 70%;
    padding: 6px 12px !important;
    text-align: center !important;
    box-sizing: border-box !important;
    margin: 0 !important;
  }
  
  /* 头像上传样式 */
  .avatar-upload-container {
    display: flex;
    align-items: center;
    gap: 15px;
    flex-wrap: wrap;
  }
  
  .avatar-preview {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    overflow: hidden;
    border: 2px solid #ddd;
    background-color: #f5f5f5;
  }
  
  .avatar-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  /* 生日选择器样式调整 */
  .el-date-editor {
    width: auto !important;
    max-width: 200px;
  }
  
  .el-date-editor .el-input__wrapper {
    width: 200px !important;
  }
  
  /* 调整模态框内表单元素的最大宽度 */
  .el-form-item .el-input,
  .el-form-item .el-select,
  .el-form-item .el-cascader,
  .el-form-item .el-date-editor {
    max-width: 300px;
  }
</style>