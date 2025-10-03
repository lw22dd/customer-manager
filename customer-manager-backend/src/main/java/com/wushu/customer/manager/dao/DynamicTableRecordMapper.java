package com.wushu.customer.manager.dao;

import com.wushu.customer.manager.model.DynamicTableRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DynamicTableRecordMapper {
    
    /**
     * 根据表标识获取所有记录
     * @param tableKey 表唯一标识
     * @return 记录列表
     */
    List<DynamicTableRecord> findByTableKey(@Param("tableKey") String tableKey);
    
    /**
     * 根据表标识获取所有记录，按姓名首字母排序
     * @param tableKey 表唯一标识
     * @return 记录列表
     */
    List<DynamicTableRecord> findByTableKeyOrderByName(@Param("tableKey") String tableKey);
    
    /**
     * 根据表标识获取所有记录，按创建时间排序
     * @param tableKey 表唯一标识
     * @param order 排序方式：ASC-正序，DESC-倒序
     * @return 记录列表
     */
    List<DynamicTableRecord> findByTableKeyOrderByCreateTime(@Param("tableKey") String tableKey, @Param("order") String order);
    
    /**
     * 分页获取记录
     * @param tableKey 表唯一标识
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 记录列表
     */
    List<DynamicTableRecord> findByTableKeyWithPage(@Param("tableKey") String tableKey, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 分页获取记录，按姓名首字母排序
     * @param tableKey 表唯一标识
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 记录列表
     */
    List<DynamicTableRecord> findByTableKeyWithPageOrderByName(@Param("tableKey") String tableKey, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 分页获取记录，按创建时间排序
     * @param tableKey 表唯一标识
     * @param offset 偏移量
     * @param limit 限制数量
     * @param order 排序方式：ASC-正序，DESC-倒序
     * @return 记录列表
     */
    List<DynamicTableRecord> findByTableKeyWithPageOrderByCreateTime(@Param("tableKey") String tableKey, @Param("offset") int offset, @Param("limit") int limit, @Param("order") String order);
    
    /**
     * 根据表标识统计记录总数
     * @param tableKey 表唯一标识
     * @return 记录总数
     */
    long countByTableKey(@Param("tableKey") String tableKey);
    
    /**
     * 根据ID获取记录
     * @param id 记录ID
     * @return 记录
     */
    DynamicTableRecord findById(Long id);
    
    /**
     * 根据表标识和关键词搜索记录（在所有字段中搜索）
     * @param tableKey 表唯一标识
     * @param keyword 关键词
     * @return 记录列表
     */
    List<DynamicTableRecord> searchByKeyword(@Param("tableKey") String tableKey, @Param("keyword") String keyword);
    
    /**
     * 根据表标识、字段名和关键词搜索记录（在指定字段中搜索）
     * @param tableKey 表唯一标识
     * @param fieldName 字段名
     * @param keyword 关键词
     * @return 记录列表
     */
    List<DynamicTableRecord> searchByFieldAndKeyword(@Param("tableKey") String tableKey, @Param("fieldName") String fieldName, @Param("keyword") String keyword);
    
    /**
     * 插入记录
     * @param record 记录
     * @return 影响行数
     */
    int insert(DynamicTableRecord record);
    
    /**
     * 更新记录
     * @param record 记录
     * @return 影响行数
     */
    int update(DynamicTableRecord record);
    
    /**
     * 删除记录
     * @param id 记录ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 批量删除记录
     * @param ids 记录ID列表
     * @return 影响行数
     */
    int batchDeleteByIds(@Param("ids") List<Long> ids);
    
    /**
     * 根据表标识删除所有记录
     * @param tableKey 表唯一标识
     * @return 影响行数
     */
    int deleteByTableKey(@Param("tableKey") String tableKey);
}