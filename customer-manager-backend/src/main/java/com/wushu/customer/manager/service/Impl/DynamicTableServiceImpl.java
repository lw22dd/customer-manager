package com.wushu.customer.manager.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wushu.customer.manager.dao.DynamicTableMetadataMapper;
import com.wushu.customer.manager.dao.DynamicTableRecordMapper;
import com.wushu.customer.manager.model.DynamicTableMetadata;
import com.wushu.customer.manager.model.DynamicTableRecord;
import com.wushu.customer.manager.model.PagingResult;
import com.wushu.customer.manager.service.DynamicTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DynamicTableServiceImpl implements DynamicTableService {
    
    @Autowired
    private DynamicTableMetadataMapper metadataMapper;
    
    @Autowired
    private DynamicTableRecordMapper recordMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean saveFieldMetadata(DynamicTableMetadata metadata) {
        if (metadata.getId() != null && metadata.getId() > 0) {
            // 更新操作
            return metadataMapper.update(metadata) > 0;
        } else {
            // 新增操作
            return metadataMapper.insert(metadata) > 0;
        }
    }
    
    @Override
    public List<DynamicTableMetadata> getFieldMetadataByTableKey(String tableKey) {
        return metadataMapper.findByTableKey(tableKey);
    }
    
    @Override
    public boolean deleteFieldMetadata(Long id) {
        return metadataMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean saveRecord(DynamicTableRecord record) {
        // 设置创建和更新时间
        if (record.getId() != null && record.getId() > 0) {
            // 更新操作
            record.setUpdateTime(new Date());
            return recordMapper.update(record) > 0;
        } else {
            // 新增操作
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            return recordMapper.insert(record) > 0;
        }
    }
    
    @Override
    public List<DynamicTableRecord> getRecordsByTableKey(String tableKey) {
        return recordMapper.findByTableKey(tableKey);
    }
    
    @Override
    public List<DynamicTableRecord> getRecordsByTableKeyOrderByName(String tableKey) {
        return recordMapper.findByTableKeyOrderByName(tableKey);
    }
    
    @Override
    public List<DynamicTableRecord> getRecordsByTableKeyOrderByCreateTime(String tableKey, String orderBy) {
        // 验证orderBy参数
        if (!"ASC".equalsIgnoreCase(orderBy) && !"DESC".equalsIgnoreCase(orderBy)) {
            orderBy = "DESC"; // 默认倒序
        }
        return recordMapper.findByTableKeyOrderByCreateTime(tableKey, orderBy.toUpperCase());
    }
    
    @Override
    public PagingResult<DynamicTableRecord> getRecordsByTableKeyWithPage(String tableKey, int page, int size) {
        int offset = (page - 1) * size;
        List<DynamicTableRecord> records = recordMapper.findByTableKeyWithPage(tableKey, offset, size);
        long totalCount = recordMapper.countByTableKey(tableKey);
        return new PagingResult<>(records, totalCount, page, size);
    }
    
    @Override
    public PagingResult<DynamicTableRecord> getRecordsByTableKeyWithPageOrderByName(String tableKey, int page, int size) {
        int offset = (page - 1) * size;
        List<DynamicTableRecord> records = recordMapper.findByTableKeyWithPageOrderByName(tableKey, offset, size);
        long totalCount = recordMapper.countByTableKey(tableKey);
        return new PagingResult<>(records, totalCount, page, size);
    }
    
    @Override
    public PagingResult<DynamicTableRecord> getRecordsByTableKeyWithPageOrderByCreateTime(String tableKey, int page, int size, String orderBy) {
        // 验证orderBy参数
        if (!"ASC".equalsIgnoreCase(orderBy) && !"DESC".equalsIgnoreCase(orderBy)) {
            orderBy = "DESC"; // 默认倒序
        }
        int offset = (page - 1) * size;
        List<DynamicTableRecord> records = recordMapper.findByTableKeyWithPageOrderByCreateTime(tableKey, offset, size, orderBy.toUpperCase());
        long totalCount = recordMapper.countByTableKey(tableKey);
        return new PagingResult<>(records, totalCount, page, size);
    }
    
    @Override
    public DynamicTableRecord getRecordById(Long id) {
        return recordMapper.findById(id);
    }
    
    @Override
    public boolean deleteRecord(Long id) {
        return recordMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean batchDeleteRecords(List<Long> ids) {
        return recordMapper.batchDeleteByIds(ids) > 0;
    }
    
    @Override
    public List<DynamicTableRecord> searchByKeyword(String tableKey, String keyword) {
        return recordMapper.searchByKeyword(tableKey, keyword);
    }
    
    @Override
    public List<DynamicTableRecord> searchByFieldAndKeyword(String tableKey, String fieldName, String keyword) {
        return recordMapper.searchByFieldAndKeyword(tableKey, fieldName, keyword);
    }
    
    @Override
    public boolean uploadAvatar(Long recordId, String avatarBase64) {
        // 获取现有记录
        DynamicTableRecord record = recordMapper.findById(recordId);
        if (record == null) {
            return false;
        }
        
        // 获取现有数据并添加头像字段
        Map<String, Object> data = record.getData();
        if (data == null) {
            data = new HashMap<>();
        }
        
        // 设置头像数据
        data.put("avatar", avatarBase64);
        record.setData(data);
        
        // 更新记录
        record.setUpdateTime(new Date());
        return recordMapper.update(record) > 0;
    }
}