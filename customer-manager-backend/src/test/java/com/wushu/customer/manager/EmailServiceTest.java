package com.wushu.customer.manager;

import com.wushu.customer.manager.model.EmailRequest;
import com.wushu.customer.manager.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;
    
    @Value("${spring.mail.username}")
    private String testEmail;

    @Test
    public void testSendSimpleEmail() {
        // 测试发送简单文本邮件
        try {
            // 确保邮箱地址不为空
            String toEmail = (testEmail != null && !testEmail.isEmpty()) ? testEmail : "test@example.com";
            emailService.sendSimpleEmail(
                toEmail,
                "测试发送邮件的接口！",
                "您好！这是我发送的一封测试发送接口的邮件，看完请删除记录。"
            );
            System.out.println("简单文本邮件发送成功！");
        } catch (Exception e) {
            System.err.println("简单文本邮件发送失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testSendHtmlEmail() {
        // 测试发送HTML邮件
        try {
            // 确保邮箱地址不为空
            String toEmail = (testEmail != null && !testEmail.isEmpty()) ? testEmail : "test@example.com";
            emailService.sendHtmlEmail(
                toEmail,
                "测试发送邮件的接口！",
                "<h1>您好！</h1><p>这是我发送的一封<strong>测试发送接口</strong>的邮件，<br/>看完请删除记录。</p>"
            );
            System.out.println("HTML邮件发送成功！");
        } catch (Exception e) {
            System.err.println("HTML邮件发送失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testSendEmailWithRequestObject() {
        // 测试通过EmailRequest对象发送邮件
        try {
            // 确保邮箱地址不为空
            String toEmail = (testEmail != null && !testEmail.isEmpty()) ? testEmail : "test@example.com";
            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setTo(toEmail);
            emailRequest.setSubject("测试发送邮件的接口！");
            emailRequest.setText("您好！这是我发送的一封测试发送接口的邮件，看完请删除记录。");
            emailRequest.setCc(Collections.emptyList());
            emailRequest.setBcc(Collections.emptyList());
            
            emailService.sendEmail(emailRequest);
            System.out.println("通过EmailRequest对象发送邮件成功！");
        } catch (Exception e) {
            System.err.println("通过EmailRequest对象发送邮件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}