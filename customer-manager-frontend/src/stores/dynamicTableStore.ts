import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { DynamicTableMetadata } from '../models/DynamicTableMetadata';
import type { DynamicTableRecord } from '../models/DynamicTableRecord';
import type { PagingResult } from '../models/Result';
import DynamicTableApi from '@/apis/dynamicTableApi';



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
      const response = await DynamicTableApi.getFieldMetadataByTableKey(key);
      console.log('获取表字段元数据结果:', response);
      if (response.code === 200 && response.data) {
        metadataList.value = response.data;
        console.log('表字段元数据:', metadataList.value);
      } else {
        console.warn('加载元数据失败:', response.msg);
        metadataList.value = []; // 失败时重置为空
      }
    } catch (error) {
      console.error('加载表字段元数据失败:', error);
      metadataList.value = [];
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
      const response = await DynamicTableApi.saveFieldMetadata(metadata);
      console.log('保存表字段元数据结果:', response);
      if (response.code === 200 && response.data) {
        await loadMetadata();
        return true;
      } else {
        console.warn('保存元数据失败:', response.msg);
        return false;
      }

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

      const response = await DynamicTableApi.deleteFieldMetadata(id);
      console.log('删除表字段元数据结果:', response);
      if (response.code === 200 && response.data) {
        await loadMetadata();
        return true;
      }
      else {
        console.warn('删除元数据失败:', response.msg);
        return false;
      }
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

      const response = await DynamicTableApi.getRecordsByTableKey(key);
      console.log('获取表所有记录结果:', response);
      if (response.code === 200 && response.data) {
        records.value = response.data;
        totalRecords.value = response.data.length;
      }
      else {
        console.warn('加载表记录失败:', response.msg);
        records.value = []; // 失败时重置为空
        totalRecords.value = 0;
      }
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
    
      const response = await DynamicTableApi.getRecordsByTableKeyWithPage(currentTableKey.value, p, s);
      console.log('分页加载表记录结果:', response.data);
      if (response.code === 200 && response.data) {
        pagedRecords.value = response.data;
        records.value = response.data.items || [];
        currentPage.value = response.data.current || 1;
        pageSize.value = response.data.size || p;
        totalRecords.value = response.data.total || s;

        console.log('加载完成：records长度=', records.value.length, '后端返回的records长度=', response.data.items.length);

      }
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
      const response = await DynamicTableApi.saveRecord(record);
      console.log('保存记录结果:', response);
      if (response.code === 200 && response.data) {

        const defaultPageSize = 10; // 默认每页显示10条数据
        pageSize.value = defaultPageSize;
        await loadRecordsWithPage(1, defaultPageSize);

        return true;
      }
      else {
        console.warn('保存记录失败:', response.msg);
        return false;
      }
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

      const response = await DynamicTableApi.deleteRecord(id);
      console.log('删除记录结果:', response);
      if (response.code === 200 && response.data) {
        await loadRecordsWithPage(currentPage.value, 10);
        return true;
      }

      else {
        console.warn('删除记录失败:', response.msg);
        return false;
      }
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

        const response = await DynamicTableApi.searchByFieldAndKeyword(
          currentTableKey.value, field, keyword
        );
        console.log('按字段搜索结果:', response);
        if (response.code === 200 && response.data) {
          records.value = response.data;
          totalRecords.value = response.data.length;
        }

      } else {
        // 搜索所有字段
        // 在实际环境中使用API
        const response = await DynamicTableApi.searchByKeyword(
          currentTableKey.value, keyword
        );
        console.log('全局搜索结果:', response);
        if (response.code === 200 && response.data) {
          records.value = response.data;
          totalRecords.value = response.data.length;
        }
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
      loadRecordsWithPage(1, 10)
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