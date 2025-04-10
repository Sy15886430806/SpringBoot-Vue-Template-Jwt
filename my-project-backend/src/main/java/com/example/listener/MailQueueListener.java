package com.example.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {

    @Resource
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    String username;

    @RabbitHandler
    public void sendMailMessage(Map<String, Object> data) {
        String email = (String) data.get("email").toString();
        Integer code = (Integer) data.get("code");
        String type = data.get("type").toString();
        SimpleMailMessage message = switch (type) {
            case "register" ->
                    createMessage("欢迎注册一粟的个人网站", "邮件验证码为：" + code + "有效时间三分钟，请勿向任何人泄漏验证码。", email);
            case "reset" ->
                    createMessage("你的密码重置邮件", "重置密码验证码为：" + code + "有效时间三分钟，请勿向任何人泄漏验证码。", email);
            default -> null;
        };
        if (message == null) return;
        sender.send(message);
    }

    private SimpleMailMessage createMessage(String title, String content, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }
}
