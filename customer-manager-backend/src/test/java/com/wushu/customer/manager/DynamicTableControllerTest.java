package com.wushu.customer.manager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wushu.customer.manager.model.DynamicTableMetadata;
import com.wushu.customer.manager.model.DynamicTableRecord;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureWebMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DynamicTableControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private String getJwtToken() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String loginRequest = "{\"username\":\"user\",\"password\":\"123456\"}";

        String result = mockMvc.perform(MockMvcRequestBuilders.post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 解析JWT token
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(result);
        return jsonNode.get("data").asText();
    }

    @Test
    public void testSaveFieldMetadata() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String token = getJwtToken();

        // 创建字段元数据
        DynamicTableMetadata metadata = new DynamicTableMetadata();
        metadata.setTableKey("test_table");
        metadata.setFieldName("name");
        metadata.setFieldLabel("姓名");
        metadata.setFieldType("text");
        metadata.setRequired(true);
        metadata.setSortOrder(1);

        ObjectMapper objectMapper = new ObjectMapper();
        String metadataJson = objectMapper.writeValueAsString(metadata);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/dynamic-table/metadata")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(metadataJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("保存字段元数据响应: " + responseContent);

        // 验证响应内容
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        assertTrue(jsonNode.get("code").asInt() == 200);
        assertTrue(jsonNode.get("data").asBoolean());

        System.out.println("保存字段元数据测试完成！");
    }

    @Test
    public void testGetFieldMetadataByTableKey() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String token = getJwtToken();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dynamic-table/metadata/test_table")
                .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("获取字段元数据响应: " + responseContent);

        // 验证响应内容
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        assertTrue(jsonNode.get("code").asInt() == 200);

        System.out.println("获取字段元数据测试完成！");
    }

    @Test
    public void testSaveRecord() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String token = getJwtToken();

        // 创建记录
        DynamicTableRecord record = new DynamicTableRecord();
        record.setTableKey("test_table");

        Map<String, Object> data = new HashMap<>();
        data.put("name", "张三");
        record.setData(data);

        ObjectMapper objectMapper = new ObjectMapper();
        String recordJson = objectMapper.writeValueAsString(record);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/dynamic-table/record")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(recordJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("保存记录响应: " + responseContent);

        // 验证响应内容
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        assertTrue(jsonNode.get("code").asInt() == 200);
        assertTrue(jsonNode.get("data").asBoolean());

        System.out.println("保存记录测试完成！");
    }

    @Test
    public void testGetRecordsByTableKey() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String token = getJwtToken();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dynamic-table/record/test_table")
                .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("获取记录响应: " + responseContent);

        // 验证响应内容
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        assertTrue(jsonNode.get("code").asInt() == 200);

        System.out.println("获取记录测试完成！");
    }

    @Test
    public void testGetRecordsByTableKeyWithPage() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String token = getJwtToken();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dynamic-table/record/test_table/page")
                .header("Authorization", "Bearer " + token)
                .param("page", "1")
                .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("分页获取记录响应: " + responseContent);

        // 验证响应内容
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        assertTrue(jsonNode.get("code").asInt() == 200);

        System.out.println("分页获取记录测试完成！");
    }

    @Test
    public void testSearchByKeyword() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String token = getJwtToken();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dynamic-table/search/test_table")
                .header("Authorization", "Bearer " + token)
                .param("keyword", "张"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("关键字搜索响应: " + responseContent);

        // 验证响应内容
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        assertTrue(jsonNode.get("code").asInt() == 200);

        System.out.println("关键字搜索测试完成！");
    }

    @Test
    public void testDeleteRecord() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String token = getJwtToken();

        // 先获取一条记录的ID
        MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/dynamic-table/record/test_table")
                .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String getResponseContent = getResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(getResponseContent);
        
        long recordId = 0;
        if (jsonNode.get("code").asInt() == 200 && jsonNode.get("data").isArray() && jsonNode.get("data").size() > 0) {
            recordId = jsonNode.get("data").get(0).get("id").asLong();
        }

        if (recordId > 0) {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/dynamic-table/record/" + recordId)
                    .header("Authorization", "Bearer " + token))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(print())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            System.out.println("删除记录响应: " + responseContent);

            // 验证响应内容
            JsonNode deleteJsonNode = objectMapper.readTree(responseContent);
            assertTrue(deleteJsonNode.get("code").asInt() == 200);

            System.out.println("删除记录测试完成！");
        } else {
            System.out.println("没有找到可以删除的记录");
        }
    }

    @Test
    public void testDeleteFieldMetadata() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String token = getJwtToken();

        // 先获取字段元数据的ID
        MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/dynamic-table/metadata/test_table")
                .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String getResponseContent = getResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(getResponseContent);
        
        long metadataId = 0;
        if (jsonNode.get("code").asInt() == 200 && jsonNode.get("data").isArray() && jsonNode.get("data").size() > 0) {
            metadataId = jsonNode.get("data").get(0).get("id").asLong();
        }

        if (metadataId > 0) {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/dynamic-table/metadata/" + metadataId)
                    .header("Authorization", "Bearer " + token))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(print())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            System.out.println("删除字段元数据响应: " + responseContent);

            // 验证响应内容
            JsonNode deleteJsonNode = objectMapper.readTree(responseContent);
            assertTrue(deleteJsonNode.get("code").asInt() == 200);

            System.out.println("删除字段元数据测试完成！");
        } else {
            System.out.println("没有找到可以删除的字段元数据");
        }
    }
}