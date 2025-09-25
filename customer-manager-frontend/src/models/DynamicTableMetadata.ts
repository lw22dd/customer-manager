// DynamicTableMetadata.ts
export interface DynamicTableMetadata {
  id?: number;
  tableKey: string;        // 表唯一标识
  fieldName: string;       // 字段名
  fieldLabel: string;      // 显示标签
  fieldType: string;       // 字段类型(text/number/select/date等)
  placeholder?: string;    // 占位提示
  required: boolean;       // 是否必填
  regex?: string;          // 验证正则表达式
  options?: string;        // 下拉框选项(JSON格式)
  sortOrder?: number;      // 显示顺序
  defaultValue?: string;   // 默认值
}
