package com.wushu.customer.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wushu.customer.manager.model.DynamicTableMetadata;
import com.wushu.customer.manager.model.DynamicTableRecord;
import com.wushu.customer.manager.service.DynamicTableService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DynamicTableServiceTest {
    
    @Autowired
    private DynamicTableService dynamicTableService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    public void testDynamicTableOperations() throws JsonProcessingException {
        String tableKey = "test_table";
        
        // 1. 创建表字段元数据
        DynamicTableMetadata nameField = new DynamicTableMetadata();
        nameField.setTableKey(tableKey);
        nameField.setFieldName("name");
        nameField.setFieldLabel("姓名");
        nameField.setFieldType("text");
        nameField.setRequired(true);
        nameField.setSortOrder(1);
        
        DynamicTableMetadata ageField = new DynamicTableMetadata();
        ageField.setTableKey(tableKey);
        ageField.setFieldName("age");
        ageField.setFieldLabel("年龄");
        ageField.setFieldType("number");
        ageField.setRequired(false);
        ageField.setSortOrder(2);
        
        assertTrue(dynamicTableService.saveFieldMetadata(nameField));
        assertTrue(dynamicTableService.saveFieldMetadata(ageField));
        
        // 2. 获取表字段元数据
        List<DynamicTableMetadata> metadataList = dynamicTableService.getFieldMetadataByTableKey(tableKey);
        assertEquals(2, metadataList.size());
        
        // 3. 创建记录
        DynamicTableRecord record = new DynamicTableRecord();
        record.setTableKey(tableKey);
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "张三");
        data.put("age", 25);
        record.setData(data);
        
        assertTrue(dynamicTableService.saveRecord(record));
        
        // 4. 获取记录
        List<DynamicTableRecord> records = dynamicTableService.getRecordsByTableKey(tableKey);
        assertEquals(1, records.size());
        assertEquals("张三", records.get(0).getData().get("name"));
        assertEquals(25, records.get(0).getData().get("age"));
        
        // 5. 搜索记录
        List<DynamicTableRecord> searchResults = dynamicTableService.searchByKeyword(tableKey, "张");
        assertEquals(1, searchResults.size());
        
        searchResults = dynamicTableService.searchByFieldAndKeyword(tableKey, "name", "张");
        assertEquals(1, searchResults.size());
        
        // 6. 删除记录
        assertTrue(dynamicTableService.deleteRecord(records.get(0).getId()));
        
        // 7. 验证记录已删除
        List<DynamicTableRecord> remainingRecords = dynamicTableService.getRecordsByTableKey(tableKey);
        assertEquals(0, remainingRecords.size());
        
        // 8. 清理元数据
        for (DynamicTableMetadata metadata : metadataList) {
            assertTrue(dynamicTableService.deleteFieldMetadata(metadata.getId()));
        }
        
        // 9. 验证元数据已删除
        List<DynamicTableMetadata> remainingMetadata = dynamicTableService.getFieldMetadataByTableKey(tableKey);
        assertEquals(0, remainingMetadata.size());
    }
}