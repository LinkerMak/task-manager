package org.example.scheduler.entity.outboxevent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(
        name = "outbox_events",
        schema = "scheduler"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(
            name = "workflow_request_id",
            nullable = false,
            updatable = false
    )
    private UUID workflowRequestId;

    @Column(
            nullable = false,
            updatable = false,
            length = 255
    )
    private String topic;

    @Column(
            name = "message_key",
            nullable = false,
            updatable = false,
            length = 255
    )
    private String messageKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            nullable = false,
            updatable = false,
            columnDefinition = "jsonb"
    )
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private OutboxEventStatus status;

    private OutboxEvent(
            UUID workflowRequestId,
            String topic,
            String messageKey,
            String payload
    ) {
        this.id = UUID.randomUUID();
        this.workflowRequestId = workflowRequestId;
        this.topic = topic;
        this.messageKey = messageKey;
        this.payload = payload;
        this.status = OutboxEventStatus.PENDING;
    }

    public static OutboxEvent pending(
            UUID workflowRequestId,
            String topic,
            String messageKey,
            String payload
    ) {
        return new OutboxEvent(
                workflowRequestId,
                topic,
                messageKey,
                payload
        );
    }

    public void markPublished() {
        if (status == OutboxEventStatus.PUBLISHED) {
            return;
        }

        this.status = OutboxEventStatus.PUBLISHED;
    }
}