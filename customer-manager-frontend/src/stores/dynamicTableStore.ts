import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { DynamicTableMetadata } from '../models/DynamicTableMetadata';
import type { DynamicTableRecord } from '../models/DynamicTableRecord';
import type { PagingResult } from '../models/Result';

// 模拟API（开发环境使用）
const mockApi = {
  // 模拟元数据
  mockMetadata: [
    { id: 1, tableKey: 'customer', fieldName: 'name', fieldLabel: '姓名', fieldType: 'text', required: true, sortOrder: 1 },
    { id: 2, tableKey: 'customer', fieldName: 'phone', fieldLabel: '电话', fieldType: 'text', required: true, sortOrder: 2 },
    { id: 3, tableKey: 'customer', fieldName: 'address', fieldLabel: '地址', fieldType: 'text', required: false, sortOrder: 3 },
    { id: 4, tableKey: 'customer', fieldName: 'email', fieldLabel: '邮箱', fieldType: 'text', required: false, sortOrder: 4 },
    { id: 5, tableKey: 'customer', fieldName: 'birthday', fieldLabel: '生日', fieldType: 'date', required: false, sortOrder: 5 }
  ] as DynamicTableMetadata[],
  
  // 模拟记录数据
  mockRecords: [
    {
      id: 1,
      tableKey: 'customer',
      data: {
        name: '张三',
        phone: '13800138000',
        address: '北京市朝阳区建国路88号',
        email: 'zhangsan@example.com',
        birthday: '1990-01-01'
      } as Record<string, any>,
      createTime: '2024-01-01 10:00:00',
      updateTime: '2024-01-01 10:00:00'
    },
    {
      id: 2,
      tableKey: 'customer',
      data: {
        name: '李四',
        phone: '13900139000',
        address: '上海市浦东新区张江高科技园区',
        email: 'lisi@example.com',
        birthday: '1995-05-05'
      },
      createTime: '2024-01-02 11:00:00',
      updateTime: '2024-01-02 11:00:00'
    }
  ]
};

export const useDynamicTableStore = defineStore('dynamicTable', () => {
  // 状态定义
  const currentTableKey = ref<string>('customer'); // 当前表标识
  const metadataList = ref<DynamicTableMetadata[]>([]); // 表字段元数据
  const records = ref<DynamicTableRecord[]>([]); // 表记录
  const pagedRecords = ref<PagingResult<DynamicTableRecord> | null>(null); // 分页记录
  const currentRecord = ref<DynamicTableRecord | null>(null); // 当前操作的记录
  const selectedIds = ref<number[]>([]); // 选中的记录ID
  const isLoading = ref<boolean>(false); // 加载状态
  const searchKeyword = ref<string>(''); // 搜索关键词
  const searchField = ref<string>(''); // 搜索字段
  const currentPage = ref<number>(1); // 当前页码
  const pageSize = ref<number>(10); // 每页条数
  const totalRecords = ref<number>(0); // 总记录数
  
  // 计算属性
  const sortedMetadata = computed(() => {
    return metadataList.value && metadataList.value.length > 0 
      ? [...metadataList.value].sort((a, b) => 
          (a.sortOrder || 999) - (b.sortOrder || 999)
        )
      : [];
  });
  
  const isAllSelected = computed({
    get: () => 
      records.value.length > 0 && 
      selectedIds.value.length === records.value.length,
    set: (value) => {
      selectedIds.value = value 
        ? records.value.map(item => item.id || 0).filter(Boolean) 
        : [];
    }
  });
  
  // 方法定义
  
  /**
   * 设置当前表
   */
  const setCurrentTableKey = (tableKey: string) => {
    currentTableKey.value = tableKey;
    // 重置状态
    resetState();
  };
  
  /**
   * 重置状态
   */
  const resetState = () => {
    metadataList.value = [];
    records.value = [];
    pagedRecords.value = null;
    currentRecord.value = null;
    selectedIds.value = [];
    searchKeyword.value = '';
    searchField.value = '';
    currentPage.value = 1;
    totalRecords.value = 0;
  };
  
  /**
   * 加载表字段元数据
   */
  const loadMetadata = async (tableKey?: string) => {
    const key = tableKey || currentTableKey.value;
    isLoading.value = true;
    try {
      // 在实际环境中使用API，这里使用模拟数据
      // const response = await DynamicTableApi.getFieldMetadataByTableKey(key);
      // if (response.code === 200 && response.data) {
      //   metadataList.value = response.data;
      // }
      
      // 开发环境使用模拟数据
      metadataList.value = mockApi.mockMetadata.filter(item => item.tableKey === key);
    } catch (error) {
      console.error('加载表字段元数据失败:', error);
    } finally {
      isLoading.value = false;
    }
  };
  
  /**
   * 保存表字段元数据
   */
  const saveMetadata = async (metadata: DynamicTableMetadata) => {
    isLoading.value = true;
    try {
      // 在实际环境中使用API
      // const response = await DynamicTableApi.saveFieldMetadata(metadata);
      // if (response.code === 200 && response.data) {
      //   await loadMetadata();
      //   return true;
      // }
      
      // 开发环境使用模拟数据
      if (metadata.id) {
        // 更新
        const index = mockApi.mockMetadata.findIndex(item => item.id === metadata.id);
        if (index > -1) {
          mockApi.mockMetadata[index] = { ...metadata };
        }
      } else {
        // 开发环境使用模拟数据
        const newMetadata = {
          ...metadata,
          id: Date.now(),
          tableKey: currentTableKey.value,
          sortOrder: metadata.sortOrder || 999
        };
        mockApi.mockMetadata.push(newMetadata);
      }
      await loadMetadata();
      return true;
    } catch (error) {
      console.error('保存表字段元数据失败:', error);
    } finally {
      isLoading.value = false;
    }
    return false;
  };
  
  /**
   * 删除表字段元数据
   */
  const deleteMetadata = async (id: number) => {
    isLoading.value = true;
    try {
      // 在实际环境中使用API
      // const response = await DynamicTableApi.deleteFieldMetadata(id);
      // if (response.code === 200 && response.data) {
      //   await loadMetadata();
      //   return true;
      // }
      
      // 开发环境使用模拟数据
      mockApi.mockMetadata = mockApi.mockMetadata.filter(item => item.id !== id);
      await loadMetadata();
      return true;
    } catch (error) {
      console.error('删除表字段元数据失败:', error);
    } finally {
      isLoading.value = false;
    }
    return false;
  };
  
  /**
   * 加载表记录
   */
  const loadRecords = async (tableKey?: string) => {
    const key = tableKey || currentTableKey.value;
    isLoading.value = true;
    try {
      // 在实际环境中使用API
      // const response = await DynamicTableApi.getRecordsByTableKey(key);
      // if (response.code === 200 && response.data) {
      //   records.value = response.data;
      //   totalRecords.value = response.data.length;
      // }
      
      // 开发环境使用模拟数据
      records.value = mockApi.mockRecords.filter(item => item.tableKey === key);
      totalRecords.value = records.value.length;
      
      // 应用搜索
      applySearch();
    } catch (error) {
      console.error('加载表记录失败:', error);
    } finally {
      isLoading.value = false;
    }
  };
  
  /**
   * 分页加载表记录
   */
  const loadRecordsWithPage = async (page?: number, size?: number) => {
    const p = page || currentPage.value;
    const s = size || pageSize.value;
    isLoading.value = true;
    try {
      // 在实际环境中使用API
      // const response = await DynamicTableApi.getRecordsByTableKeyWithPage(
      //   currentTableKey.value, p, s
      // );
      // if (response.code === 200 && response.data) {
      //   pagedRecords.value = response.data;
      //   records.value = response.data.records;
      //   currentPage.value = response.data.current;
      //   pageSize.value = response.data.size;
      //   totalRecords.value = response.data.total;
      // }
      
      // 开发环境使用模拟数据
      const allRecords = mockApi.mockRecords.filter(item => item.tableKey === currentTableKey.value);
      const startIndex = (p - 1) * s;
      const endIndex = startIndex + s;
      
      records.value = allRecords.slice(startIndex, endIndex);
      totalRecords.value = allRecords.length;
      currentPage.value = p;
      pageSize.value = s;
      
      // 应用搜索 - 只有在有搜索关键词时才应用搜索
      if (searchKeyword.value.trim()) {
        applySearch();
      }
    } catch (error) {
      console.error('分页加载表记录失败:', error);
    } finally {
      isLoading.value = false;
    }
  };
  
  /**
   * 设置当前操作的记录
   */
  const setCurrentRecord = (record: DynamicTableRecord | null) => {
    currentRecord.value = record ? { ...record, data: { ...record.data } } : null;
  };
  
  /**
   * 保存记录（新增或更新）
   */
  const saveRecord = async (record: DynamicTableRecord) => {
    isLoading.value = true;
    try {
      // 在实际环境中使用API
      // const response = await DynamicTableApi.saveRecord(record);
      // if (response.code === 200 && response.data) {
      //   await loadRecordsWithPage();
      //   return true;
      // }
      
      // 开发环境使用模拟数据
      const newRecord = {
        ...record,
        tableKey: currentTableKey.value,
        createTime: record.createTime || new Date().toLocaleString('zh-CN'),
        updateTime: new Date().toLocaleString('zh-CN')
      };
      
      if (record.id) {
        // 更新
        const index = mockApi.mockRecords.findIndex(item => item.id === record.id);
        if (index > -1) {
          // 确保id有值
          mockApi.mockRecords[index] = { ...newRecord, id: record.id };
        }
      } else {
        // 添加
        newRecord.id = Date.now();
        mockApi.mockRecords.push(newRecord as any);
      }
      
      await loadRecordsWithPage();
      return true;
    } catch (error) {
      console.error('保存记录失败:', error);
    } finally {
      isLoading.value = false;
    }
    return false;
  };
  
  /**
   * 删除记录
   */
  const deleteRecord = async (id: number) => {
    isLoading.value = true;
    try {
      // 在实际环境中使用API
      // const response = await DynamicTableApi.deleteRecord(id);
      // if (response.code === 200 && response.data) {
      //   await loadRecordsWithPage();
      //   return true;
      // }
      
      // 开发环境使用模拟数据
      mockApi.mockRecords = mockApi.mockRecords.filter(item => item.id !== id);
      await loadRecordsWithPage();
      return true;
    } catch (error) {
      console.error('删除记录失败:', error);
    } finally {
      isLoading.value = false;
    }
    return false;
  };
  
  /**
   * 删除选中的记录
   */
  const deleteSelectedRecords = async () => {
    if (selectedIds.value.length === 0) return false;
    
    isLoading.value = true;
    try {
      // 依次删除（实际环境可以考虑批量删除API）
      for (const id of selectedIds.value) {
        await deleteRecord(id);
      }
      
      selectedIds.value = [];
      return true;
    } catch (error) {
      console.error('删除选中记录失败:', error);
    } finally {
      isLoading.value = false;
    }
    return false;
  };
  
  /**
   * 切换记录选择状态
   */
  const toggleSelect = (id: number) => {
    const index = selectedIds.value.indexOf(id);
    if (index > -1) {
      selectedIds.value.splice(index, 1);
    } else {
      selectedIds.value.push(id);
    }
  };
  
  /**
   * 设置搜索条件
   */
  const setSearchParams = (keyword: string, field: string = '') => {
    searchKeyword.value = keyword;
    searchField.value = field;
    currentPage.value = 1; // 重置到第一页
    applySearch();
  };
  
  /**
   * 应用搜索
   */
  const applySearch = async () => {
    if (!searchKeyword.value.trim()) {
      // 如果没有搜索关键词，加载所有记录
      await loadRecordsWithPage();
      return;
    }
    
    isLoading.value = true;
    try {
      const keyword = searchKeyword.value.trim();
      const field = searchField.value;
      
      if (field) {
        // 搜索指定字段
        // 在实际环境中使用API
        // const response = await DynamicTableApi.searchByFieldAndKeyword(
        //   currentTableKey.value, field, keyword
        // );
        // if (response.code === 200 && response.data) {
        //   records.value = response.data;
        //   totalRecords.value = response.data.length;
        // }
        
        // 开发环境使用模拟数据
        const allRecords = mockApi.mockRecords.filter(item => item.tableKey === currentTableKey.value);
        records.value = allRecords.filter(record => {
          const data = record.data as Record<string, any>;
          return data[field] && 
                 String(data[field]).toLowerCase().includes(keyword.toLowerCase());
        });
        totalRecords.value = records.value.length;
      } else {
        // 搜索所有字段
        // 在实际环境中使用API
        // const response = await DynamicTableApi.searchByKeyword(
        //   currentTableKey.value, keyword
        // );
        // if (response.code === 200 && response.data) {
        //   records.value = response.data;
        //   totalRecords.value = response.data.length;
        // }
        
        // 开发环境使用模拟数据
        const allRecords = mockApi.mockRecords.filter(item => item.tableKey === currentTableKey.value);
        records.value = allRecords.filter(record => {
          const values = Object.values(record.data);
          return values.some(value => 
            value && 
            String(value).toLowerCase().includes(keyword.toLowerCase())
          );
        });
        totalRecords.value = records.value.length;
      }
      
      // 重新分页
      const startIndex = 0;
      const endIndex = pageSize.value;
      records.value = records.value.slice(startIndex, endIndex);
    } catch (error) {
      console.error('搜索失败:', error);
    } finally {
      isLoading.value = false;
    }
  };
  
  /**
   * 初始化表数据
   */
  const initTable = async (tableKey: string) => {
    setCurrentTableKey(tableKey);
    await Promise.all([
      loadMetadata(),
      loadRecordsWithPage()
    ]);
  };
  
  // 暴露状态和方法
  return {
    // 状态
    currentTableKey,
    metadataList,
    sortedMetadata,
    records,
    pagedRecords,
    currentRecord,
    selectedIds,
    isAllSelected,
    isLoading,
    searchKeyword,
    searchField,
    currentPage,
    pageSize,
    totalRecords,
    
    // 方法
    setCurrentTableKey,
    resetState,
    loadMetadata,
    saveMetadata,
    deleteMetadata,
    loadRecords,
    loadRecordsWithPage,
    setCurrentRecord,
    saveRecord,
    deleteRecord,
    deleteSelectedRecords,
    toggleSelect,
    setSearchParams,
    applySearch,
    initTable
  };
});