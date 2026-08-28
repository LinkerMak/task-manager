package com.example.emailsender.services;

import org.example.taskmanager.contracts.email.EmailSendingTask;
import com.example.emailsender.persistence.entity.EmailDelivery;
import com.example.emailsender.persistence.entity.EmailDeliveryStatus;
import com.example.emailsender.repositories.EmailDeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailDeliveryProcessingServiceImpl implements EmailDeliveryProcessingService {

    private final EmailDeliveryRepository emailDeliveryRepository;
    private final EmailSendingService emailSendingService;

    @Override
    public void process(EmailSendingTask emailSendingTask) {
        EmailDelivery emailDelivery = emailDeliveryRepository.findById(
                emailSendingTask.messageId()
        ).orElseGet(() -> createEmailDelivery(emailSendingTask));

        if (emailDelivery.isSent()) {
            log.info(
                    "Duplicate email task skipped: messageId={}, recipientEmail={}",
                    emailSendingTask.messageId(),
                    emailSendingTask.recipientEmail()
            );
            return;
        }

        if (emailDelivery.getStatus() == EmailDeliveryStatus.FAILED) {
            emailDelivery.markAsProcessing();
            emailDeliveryRepository.save(emailDelivery);

            log.info(
                    "Retrying failed email delivery: messageId={}, recipientEmail={}",
                    emailSendingTask.messageId(),
                    emailSendingTask.recipientEmail()
            );
        }

        sendAndUpdateStatus(emailDelivery, emailSendingTask);
    }

    private EmailDelivery createEmailDelivery(EmailSendingTask emailSendingTask) {
        EmailDelivery emailDelivery = new EmailDelivery(
                emailSendingTask.messageId(),
                emailSendingTask.recipientEmail(),
                emailSendingTask.subject()
        );
        emailDeliveryRepository.save(emailDelivery);
        return emailDelivery;
    }

    private void sendAndUpdateStatus(
            EmailDelivery emailDelivery,
            EmailSendingTask emailSendingTask
    ) {
        try {
            emailSendingService.send(emailSendingTask);

            emailDelivery.markAsSent();
            emailDeliveryRepository.save(emailDelivery);

            log.info(
                    "Email delivery marked as sent: messageId={}, recipientEmail={}",
                    emailSendingTask.messageId(),
                    emailSendingTask.recipientEmail()
            );
        } catch (MailException exception) {
            emailDelivery.markAsFailed();
            emailDeliveryRepository.save(emailDelivery);

            log.warn(
                    "Email delivery failed: messageId={}, recipientEmail={}",
                    emailSendingTask.messageId(),
                    emailSendingTask.recipientEmail(),
                    exception
            );

            throw exception;
        }
    }
}
