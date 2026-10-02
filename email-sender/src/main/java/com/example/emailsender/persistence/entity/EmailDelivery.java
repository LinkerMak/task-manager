package com.example.emailsender.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "email_deliveries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailDelivery {

    @Id
    @Column(name = "message_id", nullable = false, updatable = false)
    private UUID messageId;

    @Column(name = "recipient_email", nullable = false, length = 320)
    private String recipientEmail;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private EmailDeliveryStatus status;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public EmailDelivery(
            UUID messageId,
            String recipientEmail,
            String subject
    ) {
        this.messageId = messageId;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.status = EmailDeliveryStatus.PROCESSING;
        this.createdAt = OffsetDateTime.now();
    }

    public void markAsSent() {
        this.status = EmailDeliveryStatus.SENT;
        this.sentAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public void markAsProcessing() {
        this.status = EmailDeliveryStatus.PROCESSING;
    }

    public void markAsFailed() {
        this.status = EmailDeliveryStatus.FAILED;
        this.sentAt = null;
    }

    public boolean isSent() {
        return this.status == EmailDeliveryStatus.SENT;
    }
}
