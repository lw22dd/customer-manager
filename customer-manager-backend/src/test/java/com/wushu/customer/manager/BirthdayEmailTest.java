package com.wushu.customer.manager;

import com.wushu.customer.manager.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BirthdayEmailTest {

    @Autowired
    private EmailService emailService;

    @Test
    public void testCheckBirthdaysAndSendEmails() {
        // 手动调用检查生日并发送邮件的方法进行测试
        emailService.checkBirthdaysAndSendEmails();
        System.out.println("生日检查和邮件发送测试完成");
    }
}