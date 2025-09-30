<template>
  <div class="dynamic-table-manager">
    <div class="header">
      <h2>表头管理</h2>
      <button class="btn-add" @click="showAddModal = true">
        <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M8 2V14M2 8H14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
        新增字段
      </button>
    </div>
    
    <!-- 字段列表 -->
    <div class="metadata-list" v-if="!isLoading">
      <div v-if="store.sortedMetadata.length === 0" class="empty-state">
        暂无字段，请点击"新增字段"添加
      </div>
      <div class="metadata-item" v-for="item in store.sortedMetadata" :key="item.id">
        <div class="metadata-info">
          <div class="field-label">{{ item.fieldLabel }}</div>
          <div class="field-detail">
            <span class="field-name">{{ item.fieldName }}</span>
            <span class="field-type">{{ getFieldTypeName(item.fieldType) }}</span>
            <span v-if="item.required" class="required-tag">必填</span>
          </div>
        </div>
        <div class="metadata-actions">
          <button class="btn-edit" @click="editMetadata(item)">
            编辑
          </button>
          <button class="btn-delete" @click="showDeleteConfirm(item)">
            删除
          </button>
          <div class="sort-handle" @mousedown="startDrag($event, item)">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 6V8M12 6V8M4 10V12M12 10V12M4 2V4M12 2V4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div class="loading" v-if="isLoading">
      加载中...
    </div>
    
    <!-- 添加/编辑模态框 -->
    <div class="modal" v-if="showAddModal || showEditModal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ showEditModal ? '编辑字段' : '新增字段' }}</h3>
          <button class="modal-close" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveMetadata">
            <div class="form-group">
              <label>字段标签 *</label>
              <input 
                type="text" 
                v-model="formData.fieldLabel" 
                required 
                placeholder="例如：姓名"
              />
            </div>
            <div class="form-group">
              <label>字段名 *</label>
              <input 
                type="text" 
                v-model="formData.fieldName" 
                required 
                placeholder="例如：name"
                pattern="^[a-zA-Z_][a-zA-Z0-9_]*$"
                title="字段名只能包含字母、数字和下划线，且不能以数字开头"
              />
              <small>只能包含字母、数字和下划线，且不能以数字开头</small>
            </div>
            <div class="form-group">
              <label>字段类型 *</label>
              <select v-model="formData.fieldType" required>
                <option value="text">文本</option>
                <option value="number">数字</option>
                <option value="select">下拉选择</option>
                <option value="date">日期</option>
                <option value="textarea">多行文本</option>
                <option value="checkbox">复选框</option>
                <option value="radio">单选框</option>
              </select>
            </div>
            <div class="form-group" v-if="formData.fieldType === 'select' || formData.fieldType === 'checkbox' || formData.fieldType === 'radio'">
              <label>选项配置</label>
              <textarea 
                v-model="formData.options" 
                placeholder="格式示例: {label: '选项1', value: '1'}"
                rows="3"
              ></textarea>
              <small>JSON数组格式，每个对象包含label和value字段</small>
            </div>
            <div class="form-group">
              <label>占位提示</label>
              <input 
                type="text" 
                v-model="formData.placeholder" 
                placeholder="输入提示文本"
              />
            </div>
            <div class="form-group">
              <label>默认值</label>
              <input 
                type="text" 
                v-model="formData.defaultValue" 
                placeholder="设置默认值"
              />
            </div>
            <div class="form-group">
              <label class="checkbox-label">
                <input 
                  type="checkbox" 
                  v-model="formData.required"
                />
                必填字段
              </label>
            </div>
            <div class="form-group">
              <label>验证正则（可选）</label>
              <input 
                type="text" 
                v-model="formData.regex" 
                placeholder="例如：^1[3-9]\\d{9}$"
              />
              <small>用于字段内容的格式验证</small>
            </div>
            <div class="form-group">
              <label>显示顺序</label>
              <input 
                type="number" 
                v-model="formData.sortOrder" 
                min="1" 
                placeholder="数字越小越靠前"
              />
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
          <p>确定要删除字段 "{{ deleteMetadataItem?.fieldLabel }}" 吗？</p>
          <p class="warning">删除后，该字段的数据将无法恢复！</p>
          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="closeDeleteModal">取消</button>
            <button type="button" class="btn-delete-confirm" @click="confirmDelete">确认删除</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useDynamicTableStore } from '../stores/dynamicTableStore';
import type { DynamicTableMetadata } from '../models/DynamicTableMetadata';

const store = useDynamicTableStore();

// 状态
const showAddModal = ref(false);
const showEditModal = ref(false);
const showDeleteModal = ref(false);
const deleteMetadataItem = ref<DynamicTableMetadata | null>(null);
const isDragging = ref(false);
const draggedItem = ref<DynamicTableMetadata | null>(null);
const isLoading = ref(false);

// 表单数据
const formData = reactive<Partial<DynamicTableMetadata>>({
  fieldLabel: '',
  fieldName: '',
  fieldType: 'text',
  placeholder: '',
  required: false,
  regex: '',
  options: '',
  sortOrder: 999,
  defaultValue: ''
});

// 方法

/**
 * 获取字段类型的中文名称
 */
const getFieldTypeName = (type: string): string => {
  const typeMap: Record<string, string> = {
    text: '文本',
    number: '数字',
    select: '下拉选择',
    date: '日期',
    textarea: '多行文本',
    checkbox: '复选框',
    radio: '单选框'
  };
  return typeMap[type] || type;
};

/**
 * 打开新增模态框
 */
const openAddModal = () => {
  resetForm();
  showAddModal.value = true;
};

/**
 * 编辑元数据
 */
const editMetadata = (metadata: DynamicTableMetadata) => {
  // 复制数据到表单
  Object.assign(formData, { ...metadata });
  showEditModal.value = true;
};

/**
 * 显示删除确认
 */
const showDeleteConfirm = (metadata: DynamicTableMetadata) => {
  deleteMetadataItem.value = metadata;
  showDeleteModal.value = true;
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
  deleteMetadataItem.value = null;
};

/**
 * 重置表单
 */
const resetForm = () => {
  formData.fieldLabel = '';
  formData.fieldName = '';
  formData.fieldType = 'text';
  formData.placeholder = '';
  formData.required = false;
  formData.regex = '';
  formData.options = '';
  formData.sortOrder = 999;
  formData.defaultValue = '';
  delete formData.id;
};

/**
 * 保存元数据
 */
const saveMetadata = async () => {
  // 验证选项配置
  if (formData.options && (formData.fieldType === 'select' || formData.fieldType === 'checkbox' || formData.fieldType === 'radio')) {
    try {
      // 验证JSON格式
      JSON.parse(`[${formData.options}]`);
    } catch (error) {
      alert('选项配置格式错误，请输入有效的JSON数组格式');
      return;
    }
  }
  
  // 为formData添加tableKey字段
  formData.tableKey = "customer";
  // 保存
  const result = await store.saveMetadata(formData as DynamicTableMetadata);
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
  if (deleteMetadataItem.value?.id) {
    const result = await store.deleteMetadata(deleteMetadataItem.value.id);
    if (result) {
      closeDeleteModal();
    } else {
      alert('删除失败，请重试');
    }
  }
};

/**
 * 开始拖拽排序
 */
const startDrag = (event: MouseEvent, item: DynamicTableMetadata) => {
  isDragging.value = true;
  draggedItem.value = item;
  event.preventDefault();
  
  // 添加拖拽事件监听
  const handleMouseMove = (e: MouseEvent) => {
    if (!isDragging.value) return;
    
    // 这里可以添加拖拽视觉效果
    // 实际排序逻辑可以在mouseup时处理
  };
  
  const handleMouseUp = (e: MouseEvent) => {
    if (!isDragging.value || !draggedItem.value) return;
    
    isDragging.value = false;
    
    // 找到目标元素
    const targetElement = document.elementFromPoint(e.clientX, e.clientY);
    const metadataItem = targetElement?.closest('.metadata-item');
    
    if (metadataItem) {
      // 获取目标item的索引
      const index = Array.from(document.querySelectorAll('.metadata-item')).indexOf(metadataItem);
      
      if (index !== -1) {
        // 重新排序并保存
        const sortedMetadata = [...store.sortedMetadata];
        const currentIndex = sortedMetadata.findIndex(m => m.id === draggedItem.value?.id);
        
        if (currentIndex !== -1) {
          // 移除当前项
          const [removed] = sortedMetadata.splice(currentIndex, 1);
          // 插入到新位置
          sortedMetadata.splice(index, 0, removed);
          
          // 更新排序号
          sortedMetadata.forEach((item, idx) => {
            item.sortOrder = idx + 1;
          });
          
          // 批量保存（实际环境中可以考虑批量API）
          sortedMetadata.forEach(async (item) => {
            await store.saveMetadata(item);
          });
        }
      }
    }
    
    // 移除事件监听
    document.removeEventListener('mousemove', handleMouseMove);
    document.removeEventListener('mouseup', handleMouseUp);
  };
  
  document.addEventListener('mousemove', handleMouseMove);
  document.addEventListener('mouseup', handleMouseUp);
};

// 初始化时加载元数据
store.loadMetadata();
</script>

<style scoped>
.dynamic-table-manager {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.btn-add {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.btn-add:hover {
  background: #40a9ff;
}

.metadata-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
  background: #f5f5f5;
  border-radius: 4px;
}

.metadata-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fafafa;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  transition: all 0.3s;
}

.metadata-item:hover {
  border-color: #1890ff;
  background: #e6f7ff;
}

.metadata-info {
  flex: 1;
}

.field-label {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.field-detail {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #666;
}

.field-name {
  background: #e8f4ff;
  color: #1890ff;
  padding: 2px 6px;
  border-radius: 2px;
}

.field-type {
  background: #f0f0f0;
  color: #666;
  padding: 2px 6px;
  border-radius: 2px;
}

.required-tag {
  background: #ff4d4f;
  color: white;
  padding: 2px 6px;
  border-radius: 2px;
  font-size: 11px;
}

.metadata-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-edit, .btn-delete {
  padding: 4px 12px;
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

.sort-handle {
  cursor: grab;
  padding: 4px;
  color: #999;
}

.sort-handle:active {
  cursor: grabbing;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
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

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #1890ff;
}

.form-group small {
  display: block;
  margin-top: 4px;
  color: #999;
  font-size: 12px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
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