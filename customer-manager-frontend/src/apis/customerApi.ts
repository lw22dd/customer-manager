import type { PagingResult, Result } from "../models/Result";
import type { DynamicTableMetadata } from "../models/DynamicTableMetadata";
import type { DynamicTableRecord } from "../models/DynamicTableRecord";
import Axios from "../utils/axios";



export default class DynamicTableApi {
  // ==================== 表头管理 ====================
  
  /**
   * 创建或更新表字段元数据
   */
  public static async saveFieldMetadata(metadata: DynamicTableMetadata): Promise<Result<boolean>> {
    return await Axios.post('/dynamic-table/metadata', metadata);
  }
  
  /**
   * 获取表的所有字段元数据
   */
  public static async getFieldMetadataByTableKey(tableKey: string): Promise<Result<DynamicTableMetadata[]>> {
    return await Axios.get(`/dynamic-table/metadata/${tableKey}`);
  }
  
  /**
   * 删除字段元数据
   */
  public static async deleteFieldMetadata(id: number): Promise<Result<boolean>> {
    return await Axios.delete(`/dynamic-table/metadata/${id}`);
  }
  
  // ==================== 记录操作 ====================
  
  /**
   * 保存记录
   */
  public static async saveRecord(record: DynamicTableRecord): Promise<Result<boolean>> {
    return await Axios.post('/dynamic-table/record', record);
  }
  
  /**
   * 获取表的所有记录
   */
  public static async getRecordsByTableKey(tableKey: string): Promise<Result<DynamicTableRecord[]>> {
    return await Axios.get(`/dynamic-table/record/${tableKey}`);
  }
  
  /**
   * 分页获取表的记录
   */
  public static async getRecordsByTableKeyWithPage(
    tableKey: string,
    page: number = 1,
    size: number = 10
  ): Promise<Result<PagingResult<DynamicTableRecord>>> {
    return await Axios.get(`/dynamic-table/record/${tableKey}/page`, { 
      params: { page, size } 
    });
  }
  
  /**
   * 根据ID获取记录
   */
  public static async getRecordById(id: number): Promise<Result<DynamicTableRecord>> {
    return await Axios.get(`/dynamic-table/record/detail/${id}`);
  }
  
  /**
   * 删除记录
   */
  public static async deleteRecord(id: number): Promise<Result<boolean>> {
    return await Axios.delete(`/dynamic-table/record/${id}`);
  }
  
  // ==================== 搜索功能 ====================
  
  /**
   * 在所有字段中搜索关键词
   */
  public static async searchByKeyword(
    tableKey: string,
    keyword: string
  ): Promise<Result<DynamicTableRecord[]>> {
    return await Axios.get(`/dynamic-table/search/${tableKey}`, { 
      params: { keyword } 
    });
  }
  
  /**
   * 在指定字段中搜索关键词
   */
  public static async searchByFieldAndKeyword(
    tableKey: string,
    fieldName: string,
    keyword: string
  ): Promise<Result<DynamicTableRecord[]>> {
    return await Axios.get(`/dynamic-table/search/${tableKey}/${fieldName}`, { 
      params: { keyword } 
    });
  }
}