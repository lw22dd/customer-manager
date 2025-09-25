export interface User {
  id: number;
  name: string;
  phone: string;
  address: string|null;
  email: string|null;// 邮箱
  idCard: string|null; // 身份证号
  birthday: string|null; // 生日
  height: number|null; // 身高
  weight: number|null; // 体重
  shoeSize: string|null; // 鞋码
  clothesSize: string|null; // 服码
  athleteLevel: string|null; // 运动员等级
  refereeLevel: string|null; // 裁判员等级
  familyHeritage: string|null; // 家族传承

  customFields?: Record<string, string | number | null>; // 自定义字段
}
export interface CustomField {
  id?: number; 
  label: string; // 显示名称
  key: string; // 字段标识
  required: boolean; // 是否必填
  type: 'text' | 'number' | 'date' | 'select'; // 输入类型
  options?: { label: string; value: string }[]; // 下拉选项
  isSystem: boolean; // 是否系统默认字段
}
// 表单配置类型
export interface FormConfig {
  systemFields: CustomField[]; // 系统字段
  customFields: CustomField[]; // 用户自定义字段
}