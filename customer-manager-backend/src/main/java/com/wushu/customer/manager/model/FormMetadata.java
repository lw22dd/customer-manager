package com.wushu.customer.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormMetadata {

    private Long id;

    private String formKey; // 表单唯一标识（如"user_profile"、"order_extra"）
    private String fieldName; // 字段名（如"nickname"）
    private String fieldLabel; // 显示标签（如"昵称"）
    private String fieldType; // 字段类型（text/number/select/date等）
    private String placeholder; // 占位提示
    private boolean required; // 是否必填
    private String regex; // 验证正则表达式（如手机号）
    private String options; // 下拉框选项（JSON格式：[{"label":"男","value":"M"},...]）
    private Integer sortOrder; // 显示顺序
    private String defaultValue; // 默认值

    // getter/setter
}