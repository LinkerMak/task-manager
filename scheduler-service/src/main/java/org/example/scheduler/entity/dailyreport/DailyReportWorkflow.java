package org.example.scheduler.entity.dailyreport;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "daily_report_workflows", schema = "scheduler")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyReportWorkflow {

    @Id
    @Column(name = "request_id", nullable = false, updatable = false)
    private UUID requestId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "recipient_email", nullable = false, updatable = false, length = 100)
    private String recipientEmail;

    @Column(name = "period_start", nullable = false, updatable = false)
    private OffsetDateTime periodStart;

    @Column(name = "period_end", nullable = false, updatable = false)
    private OffsetDateTime periodEnd;

    @Column(name = "summary_text")
    private String summaryText;

    @Column(name = "status", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private DailyReportWorkflowStatus status;

    private DailyReportWorkflow(
            UUID requestId,
            Long userId,
            String recipientEmail,
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd
    ) {
        this.requestId = requestId;
        this.userId = userId;
        this.recipientEmail = recipientEmail;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.status = DailyReportWorkflowStatus.REQUESTED;
    }

    public static DailyReportWorkflow requested(
            UUID requestId,
            Long userId,
            String recipientEmail,
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd
    ) {
        return new DailyReportWorkflow(
                requestId,
                userId,
                recipientEmail,
                periodStart,
                periodEnd
        );
    }

    public void markSummaryGenerated() {
        if (this.status != DailyReportWorkflowStatus.REQUESTED) {
            throw new IllegalStateException(
                    "Cannot mark workflow as summary requested with status:" + status
            );
        }

        this.status = DailyReportWorkflowStatus.SUMMARY_GENERATED;
    }

    public void markEmailQueued() {
        if(this.status != DailyReportWorkflowStatus.SUMMARY_GENERATED) {
            throw new IllegalStateException(
                    "Cannot queued email for workflow with status:" + status
            );
        }
        this.status = DailyReportWorkflowStatus.EMAIL_QUEUED;
    }

}
