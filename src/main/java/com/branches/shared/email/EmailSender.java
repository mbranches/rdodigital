package com.branches.shared.email;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailSender {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;

    public SendEmailResponse sendEmail(SendEmailRequest request, boolean isHtml) {
        log.info("Sending email to: {}", request.to());
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

            mimeMessageHelper.setText(request.body(), isHtml);
            mimeMessageHelper.setTo(request.to());
            mimeMessageHelper.setSubject(request.subject());
            mimeMessageHelper.setFrom(new InternetAddress(sender, "RDO Digital"));

            javaMailSender.send(mimeMessage);

            log.info("Email sent successfully to: {}", request.to());
            return new SendEmailResponse(true);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", request.to(), e);
            return new SendEmailResponse(false);
        }
    }
}
