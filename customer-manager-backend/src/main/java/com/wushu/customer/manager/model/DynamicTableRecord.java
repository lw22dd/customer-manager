package com.wushu.customer.manager.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicTableRecord {
    private Long id;
    private String tableKey;           // 表唯一标识
    private Map<String, Object> data;  // 动态字段数据，以JSON格式存储
    private Date createTime;           // 创建时间
    private Date updateTime;           // 更新时间
}