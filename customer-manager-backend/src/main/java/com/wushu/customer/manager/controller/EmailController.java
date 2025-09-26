package com.wushu.customer.manager.controller;

import com.wushu.customer.manager.model.EmailRequest;
import com.wushu.customer.manager.model.Result;
import com.wushu.customer.manager.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    /**
     * 发送简单文本邮件
     */
    @PostMapping("/simple")
    public Result<String> sendSimpleEmail(@RequestBody EmailRequest emailRequest) {
        try {
            emailService.sendEmail(emailRequest);
            return Result.ok("邮件发送成功");
        } catch (Exception e) {
            return Result.fail("邮件发送失败: " + e.getMessage());
        }
    }

    /**
     * 发送HTML格式邮件
     */
    @PostMapping("/html")
    public Result<String> sendHtmlEmail(@RequestParam String to,
                                        @RequestParam String subject,
                                        @RequestParam String html) {
        try {
            emailService.sendHtmlEmail(to, subject, html);
            return Result.ok("HTML邮件发送成功");
        } catch (Exception e) {
            return Result.fail("HTML邮件发送失败: " + e.getMessage());
        }
    }

    /**
     * 发送简单文本邮件
     */
    @PostMapping("/text")
    public Result<String> sendTextEmail(@RequestParam String to,
                                        @RequestParam String subject,
                                        @RequestParam String text) {
        try {
            emailService.sendSimpleEmail(to, subject, text);
            return Result.ok("文本邮件发送成功");
        } catch (Exception e) {
            return Result.fail("文本邮件发送失败: " + e.getMessage());
        }
    }
}