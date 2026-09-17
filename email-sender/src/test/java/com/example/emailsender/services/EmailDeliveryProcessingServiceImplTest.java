package com.example.emailsender.services;

import com.example.emailsender.persistence.entity.EmailDelivery;
import com.example.emailsender.persistence.entity.EmailDeliveryStatus;
import com.example.emailsender.repositories.EmailDeliveryRepository;
import org.example.taskmanager.contracts.email.EmailSendingTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailDeliveryProcessingServiceImplTest {

    @Mock
    private EmailDeliveryRepository emailDeliveryRepository;

    @Mock
    private EmailSendingService emailSendingService;

    @InjectMocks
    private EmailDeliveryProcessingServiceImpl emailDeliveryProcessingService;

    @Test
    void processCreatesDeliverySendsEmailAndMarksDeliveryAsSent() {
        EmailSendingTask task = newEmailSendingTask();

        when(emailDeliveryRepository.findById(task.messageId()))
                .thenReturn(Optional.empty());

        emailDeliveryProcessingService.process(task);

        verify(emailSendingService).send(task);

        ArgumentCaptor<EmailDelivery> deliveryCaptor =
                ArgumentCaptor.forClass(EmailDelivery.class);

        verify(emailDeliveryRepository, times(2))
                .save(deliveryCaptor.capture());

        EmailDelivery delivery = deliveryCaptor.getValue();

        assertThat(delivery.getMessageId())
                .isEqualTo(task.messageId());

        assertThat(delivery.getRecipientEmail())
                .isEqualTo(task.recipientEmail());

        assertThat(delivery.getSubject())
                .isEqualTo(task.subject());

        assertThat(delivery.getStatus())
                .isEqualTo(EmailDeliveryStatus.SENT);

        assertThat(delivery.getSentAt()).isNotNull();
    }

    @Test
    void processSkipsDuplicateTaskWhenEmailWasAlreadySent() {
        EmailSendingTask task = newEmailSendingTask();

        EmailDelivery alreadySentDelivery =
                new EmailDelivery(
                        task.messageId(),
                        task.recipientEmail(),
                        task.subject()
                );

        alreadySentDelivery.markAsSent();

        when(emailDeliveryRepository.findById(task.messageId()))
                .thenReturn(Optional.of(alreadySentDelivery));

        emailDeliveryProcessingService.process(task);

        verify(emailSendingService, never()).send(any());
        verify(emailDeliveryRepository, never()).save(any());
    }

    @Test
    void processMarksDeliveryAsFailedAndRethrowsMailException() {
        EmailSendingTask task = newEmailSendingTask();

        when(emailDeliveryRepository.findById(task.messageId()))
                .thenReturn(Optional.empty());

        MailSendException mailSendException =
                new MailSendException("SMTP server unavailable");

        org.mockito.Mockito.doThrow(mailSendException)
                .when(emailSendingService)
                .send(task);

        assertThatThrownBy(
                () -> emailDeliveryProcessingService.process(task)
        ).isSameAs(mailSendException);

        ArgumentCaptor<EmailDelivery> deliveryCaptor =
                ArgumentCaptor.forClass(EmailDelivery.class);

        verify(emailDeliveryRepository, times(2))
                .save(deliveryCaptor.capture());

        EmailDelivery failedDelivery =
                deliveryCaptor.getValue();

        assertThat(failedDelivery.getMessageId())
                .isEqualTo(task.messageId());

        assertThat(failedDelivery.getStatus())
                .isEqualTo(EmailDeliveryStatus.FAILED);

        assertThat(failedDelivery.getSentAt()).isNull();
    }

    @Test
    void processRetriesPreviouslyFailedDelivery() {
        EmailSendingTask task = newEmailSendingTask();

        EmailDelivery failedDelivery =
                new EmailDelivery(
                        task.messageId(),
                        task.recipientEmail(),
                        task.subject()
                );

        failedDelivery.markAsFailed();

        when(emailDeliveryRepository.findById(task.messageId()))
                .thenReturn(Optional.of(failedDelivery));

        emailDeliveryProcessingService.process(task);

        verify(emailSendingService).send(task);

        verify(emailDeliveryRepository, times(2))
                .save(failedDelivery);

        assertThat(failedDelivery.getStatus())
                .isEqualTo(EmailDeliveryStatus.SENT);

        assertThat(failedDelivery.getSentAt()).isNotNull();
    }

    private EmailSendingTask newEmailSendingTask() {
        return new EmailSendingTask(
                UUID.randomUUID(),
                "user@example.com",
                "Welcome to TaskManager",
                "Welcome to Task Manager!"
        );
    }
}