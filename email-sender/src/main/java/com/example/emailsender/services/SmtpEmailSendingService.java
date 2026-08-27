package com.example.emailsender.services;

import com.example.emailsender.config.MailProperties;
import com.example.emailsender.messaging.model.EmailSendingTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SmtpEmailSendingService implements EmailSendingService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    public void send(EmailSendingTask emailSendingTask) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailProperties.from());
        mailMessage.setTo(emailSendingTask.recipientEmail());
        mailMessage.setSubject(emailSendingTask.subject());
        mailMessage.setText(emailSendingTask.body());


        mailSender.send(mailMessage);

        log.info(
                "Email was sent: messageId={}, recipientEmail={}",
                emailSendingTask.messageId(),
                emailSendingTask.recipientEmail()
        );
    }
}
