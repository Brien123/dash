package com.example.dash.common.Email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.from-address}")
    private String fromEmailAddress;

    /**
     * Internal generic method to send Mime messages to reduce code duplication.
     */
    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setText(htmlBody, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(fromEmailAddress);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email to " + to, e);
        }
    }

    public void sendWelcomeEmail(String to, String firstName) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        String body = templateEngine.process("mail/welcome", context);
        sendHtmlEmail(to, "Welcome to dash!", body);
    }

    public void sendOtpEmail(String to, String otp) {
        Context context = new Context();
        context.setVariable("otp", otp);
        String body = templateEngine.process("mail/otp", context);
        sendHtmlEmail(to, "Your Verification Code", body);
    }

    public void sendPasswordResetEmail(String to, String resetCode) {
        Context context = new Context();
        context.setVariable("resetCode", resetCode);
        String body = templateEngine.process("mail/password_reset", context);
        sendHtmlEmail(to, "dash Password Reset", body);
    }

    public void sendChangePasswordEmail(String to, String resetCode){
        Context context = new Context();
        context.setVariable("resetCode", resetCode);
        String body = templateEngine.process("mail/password_change", context);
        sendHtmlEmail(to, "dash Password Change", body);
    }

    /**
     * General purpose method for sending any template
     */
    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        String body = templateEngine.process(templateName, context);
        sendHtmlEmail(to, subject, body);
    }
}