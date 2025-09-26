package com.wushu.customer.manager.service.Impl;

import com.wushu.customer.manager.model.EmailRequest;
import com.wushu.customer.manager.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("1322168192@qq.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String html) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("1322168192@qq.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("发送HTML邮件失败", e);
        }
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String text, String attachmentPath) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("1322168192@qq.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            File file = new File(attachmentPath);
            if (file.exists()) {
                helper.addAttachment(file.getName(), file);
            }

            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("发送带附件邮件失败", e);
        }
    }

    @Override
    public void sendEmail(EmailRequest emailRequest) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("1322168192@qq.com");
            helper.setTo(emailRequest.getTo());
            
            if (emailRequest.getCc() != null && !emailRequest.getCc().isEmpty()) {
                helper.setCc(emailRequest.getCc().toArray(new String[0]));
            }
            
            if (emailRequest.getBcc() != null && !emailRequest.getBcc().isEmpty()) {
                helper.setBcc(emailRequest.getBcc().toArray(new String[0]));
            }
            
            helper.setSubject(emailRequest.getSubject());
            
            if (emailRequest.getHtml() != null && !emailRequest.getHtml().isEmpty()) {
                helper.setText(emailRequest.getHtml(), true);
            } else {
                helper.setText(emailRequest.getText());
            }
            
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("发送邮件失败", e);
        }
    }
}