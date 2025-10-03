package com.wushu.customer.manager.service;

import com.wushu.customer.manager.model.DynamicTableMetadata;
import com.wushu.customer.manager.model.DynamicTableRecord;
import com.wushu.customer.manager.model.PagingResult;

import java.util.List;

public interface DynamicTableService {
    
    /**
     * 创建或更新表字段元数据
     * @param metadata 字段元数据
     * @return 是否成功
     */
    boolean saveFieldMetadata(DynamicTableMetadata metadata);
    
    /**
     * 获取表的所有字段元数据
     * @param tableKey 表唯一标识
     * @return 字段元数据列表
     */
    List<DynamicTableMetadata> getFieldMetadataByTableKey(String tableKey);
    
    /**
     * 删除字段元数据
     * @param id 字段元数据ID
     * @return 是否成功
     */
    boolean deleteFieldMetadata(Long id);
    
    /**
     * 保存记录
     * @param record 记录
     * @return 是否成功
     */
    boolean saveRecord(DynamicTableRecord record);
    
    /**
     * 获取表的所有记录
     * @param tableKey 表唯一标识
     * @return 记录列表
     */
    List<DynamicTableRecord> getRecordsByTableKey(String tableKey);
    
    /**
     * 获取表的所有记录，按姓名首字母排序
     * @param tableKey 表唯一标识
     * @return 记录列表
     */
    List<DynamicTableRecord> getRecordsByTableKeyOrderByName(String tableKey);
    
    /**
     * 获取表的所有记录，按创建时间排序
     * @param tableKey 表唯一标识
     * @param orderBy 排序方式：ASC-正序，DESC-倒序
     * @return 记录列表
     */
    List<DynamicTableRecord> getRecordsByTableKeyOrderByCreateTime(String tableKey, String orderBy);
    
    /**
     * 分页获取表的记录
     * @param tableKey 表唯一标识
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    PagingResult<DynamicTableRecord> getRecordsByTableKeyWithPage(String tableKey, int page, int size);
    
    /**
     * 分页获取表的记录，按姓名首字母排序
     * @param tableKey 表唯一标识
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    PagingResult<DynamicTableRecord> getRecordsByTableKeyWithPageOrderByName(String tableKey, int page, int size);
    
    /**
     * 分页获取表的记录，按创建时间排序
     * @param tableKey 表唯一标识
     * @param page 页码
     * @param size 每页大小
     * @param orderBy 排序方式：ASC-正序，DESC-倒序
     * @return 分页结果
     */
    PagingResult<DynamicTableRecord> getRecordsByTableKeyWithPageOrderByCreateTime(String tableKey, int page, int size, String orderBy);
    
    /**
     * 根据ID获取记录
     * @param id 记录ID
     * @return 记录
     */
    DynamicTableRecord getRecordById(Long id);
    
    /**
     * 删除记录
     * @param id 记录ID
     * @return 是否成功
     */
    boolean deleteRecord(Long id);
    
    /**
     * 批量删除记录
     * @param ids 记录ID列表
     * @return 是否成功
     */
    boolean batchDeleteRecords(List<Long> ids);
    
    /**
     * 在所有字段中搜索关键词
     * @param tableKey 表唯一标识
     * @param keyword 关键词
     * @return 记录列表
     */
    List<DynamicTableRecord> searchByKeyword(String tableKey, String keyword);
    
    /**
     * 在指定字段中搜索关键词
     * @param tableKey 表唯一标识
     * @param fieldName 字段名
     * @param keyword 关键词
     * @return 记录列表
     */
    List<DynamicTableRecord> searchByFieldAndKeyword(String tableKey, String fieldName, String keyword);
    
    /**
     * 上传客户头像
     * @param recordId 记录ID
     * @param avatarBase64 头像的Base64编码
     * @return 是否成功
     */
    boolean uploadAvatar(Long recordId, String avatarBase64);
}