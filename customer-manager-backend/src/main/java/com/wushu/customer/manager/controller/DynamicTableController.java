package com.wushu.customer.manager.controller;

import com.wushu.customer.manager.model.DynamicTableMetadata;
import com.wushu.customer.manager.model.DynamicTableRecord;
import com.wushu.customer.manager.model.PagingResult;
import com.wushu.customer.manager.model.Result;
import com.wushu.customer.manager.service.DynamicTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dynamic-table")
public class DynamicTableController {
    
    @Autowired
    private DynamicTableService dynamicTableService;
    
    // ==================== 表头管理 ====================
    
    /**
     * 创建或更新表字段元数据
     */
    @PostMapping("/metadata")
    public Result<Boolean> saveFieldMetadata(@RequestBody DynamicTableMetadata metadata) {
        try {
            boolean result = dynamicTableService.saveFieldMetadata(metadata);
            return Result.ok(result);
        } catch (Exception e) {
            return Result.fail("保存字段元数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取表的所有字段元数据
     */
    @GetMapping("/metadata/{tableKey}")
    public Result<List<DynamicTableMetadata>> getFieldMetadataByTableKey(@PathVariable String tableKey) {
        try {
            List<DynamicTableMetadata> metadataList = dynamicTableService.getFieldMetadataByTableKey(tableKey);
            return Result.ok(metadataList);
        } catch (Exception e) {
            return Result.fail("获取字段元数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除字段元数据
     */
    @DeleteMapping("/metadata/{id}")
    public Result<Boolean> deleteFieldMetadata(@PathVariable Long id) {
        try {
            boolean result = dynamicTableService.deleteFieldMetadata(id);
            return Result.ok(result);
        } catch (Exception e) {
            return Result.fail("删除字段元数据失败: " + e.getMessage());
        }
    }
    
    // ==================== 记录操作 ====================
    
    /**
     * 保存记录
     */
    @PostMapping("/record")
    public Result<Boolean> saveRecord(@RequestBody DynamicTableRecord record) {
        try {
            boolean result = dynamicTableService.saveRecord(record);
            return Result.ok(result);
        } catch (Exception e) {
            return Result.fail("保存记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取表的所有记录
     */
    @GetMapping("/record/{tableKey}")
    public Result<List<DynamicTableRecord>> getRecordsByTableKey(@PathVariable String tableKey) {
        try {
            List<DynamicTableRecord> records = dynamicTableService.getRecordsByTableKey(tableKey);
            return Result.ok(records);
        } catch (Exception e) {
            return Result.fail("获取记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页获取表的记录
     */
    @GetMapping("/record/{tableKey}/page")
    public Result<PagingResult<DynamicTableRecord>> getRecordsByTableKeyWithPage(
            @PathVariable String tableKey,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            PagingResult<DynamicTableRecord> records = dynamicTableService.getRecordsByTableKeyWithPage(tableKey, page, size);
            return Result.ok(records);
        } catch (Exception e) {
            return Result.fail("分页获取记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取记录
     */
    @GetMapping("/record/detail/{id}")
    public Result<DynamicTableRecord> getRecordById(@PathVariable Long id) {
        try {
            DynamicTableRecord record = dynamicTableService.getRecordById(id);
            return Result.ok(record);
        } catch (Exception e) {
            return Result.fail("获取记录详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除记录
     */
    @DeleteMapping("/record/{id}")
    public Result<Boolean> deleteRecord(@PathVariable Long id) {
        try {
            boolean result = dynamicTableService.deleteRecord(id);
            return Result.ok(result);
        } catch (Exception e) {
            return Result.fail("删除记录失败: " + e.getMessage());
        }
    }
    
    // ==================== 搜索功能 ====================
    
    /**
     * 在所有字段中搜索关键词
     */
    @GetMapping("/search/{tableKey}")
    public Result<List<DynamicTableRecord>> searchByKeyword(
            @PathVariable String tableKey,
            @RequestParam String keyword) {
        try {
            List<DynamicTableRecord> records = dynamicTableService.searchByKeyword(tableKey, keyword);
            return Result.ok(records);
        } catch (Exception e) {
            return Result.fail("搜索失败: " + e.getMessage());
        }
    }
    
    /**
     * 在指定字段中搜索关键词
     */
    @GetMapping("/search/{tableKey}/{fieldName}")
    public Result<List<DynamicTableRecord>> searchByFieldAndKeyword(
            @PathVariable String tableKey,
            @PathVariable String fieldName,
            @RequestParam String keyword) {
        try {
            List<DynamicTableRecord> records = dynamicTableService.searchByFieldAndKeyword(tableKey, fieldName, keyword);
            return Result.ok(records);
        } catch (Exception e) {
            return Result.fail("搜索失败: " + e.getMessage());
        }
    }
    
    // ==================== 头像上传功能 ====================
    
    /**
     * 上传客户头像
     */
    @PostMapping("/record/{id}/avatar")
    public Result<Boolean> uploadAvatar(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String avatarBase64 = payload.get("avatar");
            if (avatarBase64 == null || avatarBase64.isEmpty()) {
                return Result.fail("头像数据不能为空");
            }
            
            boolean result = dynamicTableService.uploadAvatar(id, avatarBase64);
            if (result) {
                return Result.ok(true);
            } else {
                return Result.fail("上传头像失败");
            }
        } catch (Exception e) {
            return Result.fail("上传头像失败: " + e.getMessage());
        }
    }
}