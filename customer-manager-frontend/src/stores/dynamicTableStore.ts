import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { DynamicTableMetadata } from '../models/DynamicTableMetadata';
import type { DynamicTableRecord } from '../models/DynamicTableRecord';
import type { PagingResult } from '../models/Result';
import DynamicTableApi from '@/apis/dynamicTableApi';
import * as XLSX from 'xlsx';



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
    // 先更新当前页码，确保前端显示正确
    currentPage.value = p;
    isLoading.value = true;
    try {

      const response = await DynamicTableApi.getRecordsByTableKeyWithPage(currentTableKey.value, p, s);
      console.log('分页加载表记录结果:', response.data);
      if (response.code === 200 && response.data) {
        pagedRecords.value = response.data;
        records.value = response.data.items || [];
        pageSize.value = response.data.size || s;
        totalRecords.value = response.data.totalCount || records.value.length;

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
  const saveRecord = async (record: DynamicTableRecord): Promise<{ success: boolean; reason?: string; message?: string }> => {
    isLoading.value = true;
    try {
      // 检查姓名重复（排除当前编辑的记录）
      const searchResponseByName = await DynamicTableApi.searchByFieldAndKeyword(currentTableKey.value, 'name', record.data.name);
      console.log('名称搜索结果:', searchResponseByName);
      console.log('搜索结果长度:', searchResponseByName.data?.length);
      if (searchResponseByName.code === 200 && searchResponseByName.data && searchResponseByName.data.length > 0) {
        // 过滤掉当前编辑的记录
        const otherRecords = searchResponseByName.data.filter((r: DynamicTableRecord) => r.id !== record.id);
        console.log('其他匹配记录数量:', otherRecords.length);
        
        if (otherRecords.length > 0) {
          console.warn('名称已存在:', record.data.name);
          return { success: false, reason: 'nameDuplicate' };
        }
      }
      
      // 检查手机号重复（排除当前编辑的记录）
      const searchResponseByPhone = await DynamicTableApi.searchByFieldAndKeyword(currentTableKey.value, 'phone', record.data.phone);
      console.log('手机号搜索结果:', searchResponseByPhone);
      if (searchResponseByPhone.code === 200 && searchResponseByPhone.data && searchResponseByPhone.data.length > 0) {
        // 过滤掉当前编辑的记录
        const otherRecords = searchResponseByPhone.data.filter((r: DynamicTableRecord) => r.id !== record.id);
        
        if (otherRecords.length > 0) {
          console.warn('手机号已存在:', record.data.phone);
          return { success: false, reason: 'phoneDuplicate' };
        }
      }

      const response = await DynamicTableApi.saveRecord(record);
      console.log('保存记录结果:', response);
      if (response.code === 200 && response.data) {

        const defaultPageSize = 10; // 默认每页显示10条数据
        pageSize.value = defaultPageSize;
        await loadRecordsWithPage(1, defaultPageSize);

        return { success: true };
      }
      else {
        console.warn('保存记录失败:', response.msg);
        return { success: false, reason: 'saveFailed', message: response.msg };
      }
    } catch (error) {
      console.error('保存记录失败:', error);
      return { success: false, reason: 'systemError', message: error instanceof Error ? error.message : '系统错误' };
    } finally {
      isLoading.value = false;
    }
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
   * 上传头像
   */
  const uploadAvatar = async (id: string, avatar: string) => {
    isLoading.value = true;
    try {
      const result = await DynamicTableApi.uploadAvatar(id, avatar);
      if (result.code === 200 && result.data) {
        // 重新加载数据以更新头像
        await loadRecords();
        return true;
      } else {
        console.error('上传头像失败:', result.msg);
        return false;
      }
    } catch (error) {
      console.error('上传头像失败:', error);
      return false;
    } finally {
      isLoading.value = false;
    }
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

    currentPage.value = 1;
    if (!searchKeyword.value.trim()) {
      // 如果没有搜索关键词，加载所有记录
      console.log('空关键词搜索，加载全量数据');
      await loadRecordsWithPage(1, 10);
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

  /**
     * 导出当前页数据
     */
    const exportCurrentPageData = async () => {
      isLoading.value = true;
      try {
        const exportData = prepareExportData(records.value);
        const worksheet = XLSX.utils.json_to_sheet(exportData);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, currentTableKey.value);
        
        // 添加表头信息（元数据）
        const metadataWorksheet = XLSX.utils.json_to_sheet(
          metadataList.value.map(field => ({
            字段名称: field.fieldName,
            字段标签: field.fieldLabel,
            字段类型: field.fieldType,
            是否必填: field.required ? '是' : '否',
            默认值: field.defaultValue || '-',
            最大长度: field.maxLength || '-',
            排序: field.sortOrder || 999
          }))
        );
        XLSX.utils.book_append_sheet(workbook, metadataWorksheet, '表结构信息');
        
        // 导出文件
        XLSX.writeFile(workbook, `${currentTableKey.value}_数据_${new Date().toLocaleDateString('zh-CN').replace(/\//g, '-')}.xlsx`);
        return true;
      } catch (error) {
        console.error('导出当前页数据失败:', error);
        return false;
      } finally {
        isLoading.value = false;
      }
    };

    /**
     * 导出所有数据
     */
    const exportAllData = async () => {
      isLoading.value = true;
      try {
        let allRecords: DynamicTableRecord[] = [];
        let currentPageNum = 1;
        const pageSizeNum = 100; // 每页100条，防止一次性加载过多数据
        let hasMoreData = true;

        // 分页加载所有数据
        while (hasMoreData) {
          const response = await DynamicTableApi.getRecordsByTableKeyWithPage(
            currentTableKey.value,
            currentPageNum,
            pageSizeNum
          );
          
          if (response.code === 200 && response.data && response.data.items) {
            const pageRecords = response.data.items;
            allRecords = [...allRecords, ...pageRecords];
            
            // 检查是否还有更多数据
            hasMoreData = pageRecords.length === pageSizeNum;
            currentPageNum++;
          } else {
            hasMoreData = false;
          }
        }

        // 如果有搜索条件，应用搜索
        if (searchKeyword.value.trim()) {
          const searchResponse = searchField.value
            ? await DynamicTableApi.searchByFieldAndKeyword(
                currentTableKey.value,
                searchField.value,
                searchKeyword.value
              )
            : await DynamicTableApi.searchByKeyword(
                currentTableKey.value,
                searchKeyword.value
              );
          
          if (searchResponse.code === 200 && searchResponse.data) {
            allRecords = searchResponse.data;
          }
        }

        // 导出数据
        const exportData = prepareExportData(allRecords);
        const worksheet = XLSX.utils.json_to_sheet(exportData);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, currentTableKey.value);
        
        // 添加表头信息（元数据）
        const metadataWorksheet = XLSX.utils.json_to_sheet(
          metadataList.value.map(field => ({
            字段名称: field.fieldName,
            字段标签: field.fieldLabel,
            字段类型: field.fieldType,
            是否必填: field.required ? '是' : '否',
            默认值: field.defaultValue || '-',
            最大长度: field.maxLength || '-',
            排序: field.sortOrder || 999
          }))
        );
        XLSX.utils.book_append_sheet(workbook, metadataWorksheet, '表结构信息');
        
        // 导出文件
        XLSX.writeFile(workbook, `${currentTableKey.value}_全量数据_${new Date().toLocaleDateString('zh-CN').replace(/\//g, '-')}.xlsx`);
        return true;
      } catch (error) {
        console.error('导出所有数据失败:', error);
        return false;
      } finally {
        isLoading.value = false;
      }
    };

    /**
     * 导出选中的数据
     */
    const exportSelectedData = async () => {
      if (selectedIds.value.length === 0) return false;
      
      isLoading.value = true;
      try {
        // 从当前记录中过滤出选中的记录
        const selectedRecords = records.value.filter(
          record => record.id && selectedIds.value.includes(record.id)
        );
        
        // 导出数据
        const exportData = prepareExportData(selectedRecords);
        const worksheet = XLSX.utils.json_to_sheet(exportData);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, currentTableKey.value);
        
        // 导出文件
        XLSX.writeFile(workbook, `${currentTableKey.value}_选中数据_${new Date().toLocaleDateString('zh-CN').replace(/\//g, '-')}.xlsx`);
        return true;
      } catch (error) {
        console.error('导出选中数据失败:', error);
        return false;
      } finally {
        isLoading.value = false;
      }
    };

    /**
     * 准备导出数据
     */
    const prepareExportData = (records: DynamicTableRecord[]) => {
      // 创建字段名到字段标签的映射
      const fieldLabelMap: Record<string, string> = {};
      metadataList.value.forEach(field => {
        fieldLabelMap[field.fieldName] = field.fieldLabel;
      });

      // 处理导出数据格式
      return records.map(record => {
        const exportRow: Record<string, any> = {};
        
        // 添加ID字段
        if (record.id) {
          exportRow['ID'] = record.id;
        }
        
        // 添加其他字段，使用字段标签作为表头
        if (record.data) {
          Object.entries(record.data).forEach(([fieldName, value]) => {
            // 使用字段标签作为键名
            const label = fieldLabelMap[fieldName] || fieldName;
            
            // 处理不同类型的值
            if (value === null || value === undefined) {
              exportRow[label] = '';
            } else if (typeof value === 'object') {
              exportRow[label] = JSON.stringify(value);
            } else {
              exportRow[label] = value;
            }
          });
        }
        
        return exportRow;
      });
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
      uploadAvatar,
      loadRecords,
      loadRecordsWithPage,
      setCurrentRecord,
      saveRecord,
      deleteRecord,
      deleteSelectedRecords,
      toggleSelect,
      setSearchParams,
      applySearch,
      initTable,
      exportCurrentPageData,
      exportAllData,
      exportSelectedData

    };
});