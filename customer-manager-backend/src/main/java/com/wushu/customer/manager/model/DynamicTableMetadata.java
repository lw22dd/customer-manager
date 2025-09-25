package com.wushu.customer.manager.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicTableMetadata {
    private Long id;
    private String tableKey;        // 表唯一标识
    private String fieldName;       // 字段名
    private String fieldLabel;      // 显示标签
    private String fieldType;       // 字段类型(text/number/select/date等)
    private String placeholder;     // 占位提示
    private boolean required;       // 是否必填
    private String regex;           // 验证正则表达式
    private String options;         // 下拉框选项(JSON格式)
    private Integer sortOrder;      // 显示顺序
    private String defaultValue;    // 默认值
}