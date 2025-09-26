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

    @Test
public void testQueryAndPrintInitialRecords() throws JsonProcessingException {
    String tableKey = "customer";

    // 1. 获取客户表的字段元数据
    List<DynamicTableMetadata> metadataList = dynamicTableService.getFieldMetadataByTableKey(tableKey);
    System.out.println("=== 客户表字段元数据 ===");
    metadataList.forEach(metadata -> {
        System.out.println("字段名: " + metadata.getFieldName() +
                          ", 标签: " + metadata.getFieldLabel() +
                          ", 类型: " + metadata.getFieldType() +
                          ", 必填: " + (metadata.isRequired() ? "是" : "否"));
    });

    // 2. 获取客户表的所有记录
    List<DynamicTableRecord> records = dynamicTableService.getRecordsByTableKey(tableKey);
    System.out.println("\n=== 客户表记录数据 ===");
    System.out.println("总记录数: " + records.size());

    // 3. 打印每条记录的详细信息
    for (int i = 0; i < records.size(); i++) {
        DynamicTableRecord record = records.get(i);
        System.out.println("\n记录 #" + (i + 1) + ":");
        System.out.println("  ID: " + record.getId());
        System.out.println("  创建时间: " + record.getCreateTime());
        System.out.println("  更新时间: " + record.getUpdateTime());
        System.out.println("  数据详情:");

        Map<String, Object> data = record.getData();
        metadataList.stream()
            .sorted((m1, m2) -> Integer.compare(m1.getSortOrder(), m2.getSortOrder()))
            .forEach(metadata -> {
                String fieldName = metadata.getFieldName();
                Object value = data.get(fieldName);
                System.out.println("    " + metadata.getFieldLabel() + ": " +
                                 (value != null ? value.toString() : "无"));
            });
    }

    // 4. 验证是否有初始记录
    assertFalse(records.isEmpty(), "应该存在初始客户记录");
    System.out.println("\n=== 查询完成 ===");
}

}