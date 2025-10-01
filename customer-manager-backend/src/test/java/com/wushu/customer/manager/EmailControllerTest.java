package com.wushu.customer.manager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wushu.customer.manager.model.EmailRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

@SpringBootTest
@AutoConfigureWebMvc
public class EmailControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Value("${spring.mail.username}")
    private String testEmail;

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
    public void testSendSimpleEmailAPI() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String token = getJwtToken();
        
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTo(testEmail);
        emailRequest.setSubject("测试发送邮件的接口！");
        emailRequest.setText("您好！这是我发送的一封测试发送接口的邮件，看完请删除记录。");
        emailRequest.setCc(Collections.emptyList());
        emailRequest.setBcc(Collections.emptyList());
        
        ObjectMapper objectMapper = new ObjectMapper();
        String emailRequestJson = objectMapper.writeValueAsString(emailRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/email/simple")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(emailRequestJson))
                .andExpect(MockMvcResultMatchers.status().isOk());
                
        System.out.println("通过API发送简单邮件测试完成！");
    }

    @Test
    public void testSendTextEmailAPI() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String token = getJwtToken();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/email/text")
                .header("Authorization", "Bearer " + token)
                .param("to", testEmail)
                .param("subject", "测试发送邮件的接口！")
                .param("text", "您好！这是我发送的一封测试发送接口的邮件，看完请删除记录。"))
                .andExpect(MockMvcResultMatchers.status().isOk());
                
        System.out.println("通过API发送文本邮件测试完成！");
    }

    @Test
    public void testSendHtmlEmailAPI() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String token = getJwtToken();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/email/html")
                .header("Authorization", "Bearer " + token)
                .param("to", testEmail)
                .param("subject", "测试发送邮件的接口！")
                .param("html", "<h1>您好！</h1><p>这是我发送的一封<strong>测试发送接口</strong>的邮件，<br/>看完请删除记录。</p>"))
                .andExpect(MockMvcResultMatchers.status().isOk());
                
        System.out.println("通过API发送HTML邮件测试完成！");
    }
}