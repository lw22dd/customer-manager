package com.wushu.customer.manager.dao;

import com.wushu.customer.manager.model.DynamicTableMetadata;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DynamicTableMetadataMapper {
    
    /**
     * 根据表标识获取所有字段元数据
     * @param tableKey 表唯一标识
     * @return 字段元数据列表
     */
    List<DynamicTableMetadata> findByTableKey(@Param("tableKey") String tableKey);
    
    /**
     * 根据ID获取字段元数据
     * @param id 字段元数据ID
     * @return 字段元数据
     */
    DynamicTableMetadata findById(Long id);
    
    /**
     * 插入字段元数据
     * @param metadata 字段元数据
     * @return 影响行数
     */
    int insert(DynamicTableMetadata metadata);
    
    /**
     * 更新字段元数据
     * @param metadata 字段元数据
     * @return 影响行数
     */
    int update(DynamicTableMetadata metadata);
    
    /**
     * 删除字段元数据
     * @param id 字段元数据ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 根据表标识删除所有字段元数据
     * @param tableKey 表唯一标识
     * @return 影响行数
     */
    int deleteByTableKey(@Param("tableKey") String tableKey);
}