package com.wushu.customer.manager.config;

import com.wushu.customer.manager.model.DynamicTableMetadata;
import com.wushu.customer.manager.service.DynamicTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TableInitializationConfig {

    @Autowired
    private DynamicTableService dynamicTableService;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initializeTableFields();
    }

    private void initializeTableFields() {
        String tableKey = "customer";
        
        // 检查是否已经初始化过
        List<DynamicTableMetadata> existingFields = dynamicTableService.getFieldMetadataByTableKey(tableKey);
        if (!existingFields.isEmpty()) {
            // 如果已经有字段定义，则不进行初始化
            return;
        }

        // 定义初始字段
        List<DynamicTableMetadata> initialFields = Arrays.asList(
                createField(tableKey, "name", "姓名", "text", true, 1),
                createField(tableKey, "phone", "电话", "text", true, 2),
                createField(tableKey, "address", "收件信息", "text", false, 3),
                createField(tableKey, "idCard", "身份证号", "text", false, 4),
                createField(tableKey, "birthday", "生日", "date", false, 5),
                createField(tableKey, "heightWeight", "身高体重", "text", false, 6),
                createField(tableKey, "clothingSize", "常用鞋服尺码", "text", false, 7),
                createField(tableKey, "athleteLevel", "运动员等级", "text", false, 8),
                createField(tableKey, "refereeLevel", "裁判员等级", "text", false, 9),
                createField(tableKey, "familyHeritage", "家族传承", "text", false, 10)
        );

        // 保存初始字段
        for (DynamicTableMetadata field : initialFields) {
            dynamicTableService.saveFieldMetadata(field);
        }
    }

    private DynamicTableMetadata createField(String tableKey, String fieldName, String fieldLabel, 
                                           String fieldType, boolean required, int sortOrder) {
        DynamicTableMetadata field = new DynamicTableMetadata();
        field.setTableKey(tableKey);
        field.setFieldName(fieldName);
        field.setFieldLabel(fieldLabel);
        field.setFieldType(fieldType);
        field.setRequired(required);
        field.setSortOrder(sortOrder);
        return field;
    }
}