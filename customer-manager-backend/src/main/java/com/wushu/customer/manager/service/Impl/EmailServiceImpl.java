package com.wushu.customer.manager.service.Impl;

import com.wushu.customer.manager.model.EmailRequest;
import com.wushu.customer.manager.model.User;
import com.wushu.customer.manager.service.EmailService;
import com.wushu.customer.manager.service.UserService;
import com.wushu.customer.manager.service.DynamicTableService;
import com.wushu.customer.manager.model.DynamicTableRecord;
import com.wushu.customer.manager.model.DynamicTableMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DynamicTableService dynamicTableService;

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
    
    @Override
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    public void checkBirthdaysAndSendEmails() {
        // 获取所有用户
        List<User> users = userService.getAllUsers();
        
        // 获取客户表的所有字段元数据
        List<DynamicTableMetadata> metadataList = dynamicTableService.getFieldMetadataByTableKey("customer");
        
        // 查找生日字段
        DynamicTableMetadata birthdayField = metadataList.stream()
                .filter(metadata -> "birthday".equals(metadata.getFieldName()))
                .findFirst()
                .orElse(null);
        
        if (birthdayField == null) {
            // 如果没有生日字段，直接返回
            return;
        }
        
        // 获取今天日期 (格式: MM-dd)
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd"));
        
        // 获取所有客户记录
        List<DynamicTableRecord> customerRecords = dynamicTableService.getRecordsByTableKey("customer");
        
        // 筛选出今天生日的客户
        List<DynamicTableRecord> birthdayCustomers = customerRecords.stream()
                .filter(record -> {
                    Map<String, Object> data = record.getData();
                    Object birthdayObj = data.get("birthday");
                    if (birthdayObj != null) {
                        String birthday = birthdayObj.toString();
                        // 支持两种日期格式: yyyy-MM-dd 和 MM-dd
                        if (birthday.length() >= 5) {
                            // 提取月日部分进行比较
                            String birthdayMMdd = birthday.length() == 10 ? 
                                birthday.substring(5) : birthday.substring(0, 5);
                            return today.equals(birthdayMMdd);
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
        
        // 如果有今天生日的客户，向每个用户发送提醒邮件
        if (!birthdayCustomers.isEmpty() && !users.isEmpty()) {
            // 构建邮件内容
            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append("您好！\n\n今天是以下客户的生日，请记得送上祝福：\n\n");
            
            for (DynamicTableRecord customer : birthdayCustomers) {
                Map<String, Object> data = customer.getData();
                contentBuilder.append("- ")
                        .append(data.getOrDefault("name", "未知客户"))
                        .append(" (电话: ")
                        .append(data.getOrDefault("phone", "无"))
                        .append(")\n");
            }
            
            contentBuilder.append("\n祝您工作顺利！\n客户管理系统");
            
            // 向每个用户发送邮件
            for (User user : users) {
                if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                    try {
                        sendSimpleEmail(
                                user.getEmail(),
                                "客户生日提醒 - " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")),
                                contentBuilder.toString()
                        );
                    } catch (Exception e) {
                        // 记录发送失败的日志，但不中断其他邮件的发送
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}