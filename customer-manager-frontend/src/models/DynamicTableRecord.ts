// DynamicTableRecord.ts
export interface DynamicTableRecord {
  id?: number;
  tableKey: string;                    // 表唯一标识
  data: Record<string, any>;          // 动态字段数据，以JSON格式存储
  createTime?: string;                // 创建时间
  updateTime?: string;                // 更新时间
}
