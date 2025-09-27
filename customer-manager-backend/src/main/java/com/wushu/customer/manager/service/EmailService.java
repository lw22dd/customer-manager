package com.wushu.customer.manager.service;

import com.wushu.customer.manager.model.EmailRequest;

public interface EmailService {
    /**
     * 发送简单文本邮件
     * @param to 收件人
     * @param subject 邮件主题
     * @param text 邮件内容
     */
    void sendSimpleEmail(String to, String subject, String text);

    /**
     * 发送HTML格式邮件
     * @param to 收件人
     * @param subject 邮件主题
     * @param html 邮件HTML内容
     */
    void sendHtmlEmail(String to, String subject, String html);

    /**
     * 发送带附件的邮件
     * @param to 收件人
     * @param subject 邮件主题
     * @param text 邮件内容
     * @param attachmentPath 附件路径
     */
    void sendEmailWithAttachment(String to, String subject, String text, String attachmentPath);

    /**
     * 根据EmailRequest对象发送邮件
     * @param emailRequest 邮件请求对象
     */
    void sendEmail(EmailRequest emailRequest);
    
    /**
     * 检查今日生日的客户并发送提醒邮件给用户
     */
    void checkBirthdaysAndSendEmails();
}