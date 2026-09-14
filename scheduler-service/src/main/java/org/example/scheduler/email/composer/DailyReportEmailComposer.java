package org.example.scheduler.email.composer;

import lombok.RequiredArgsConstructor;
import org.example.scheduler.email.composer.message.id.factory.DailyReportMessageIdFactory;
import org.example.taskmanager.contracts.email.EmailSendingTask;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DailyReportEmailComposer {

    private static final String SUBJECT_PREFIX =
            "Your TaskManager daily report";

    private static final String BODY_TEMPLATE = """
                Hello!
                
                Here is your TaskManager daily report for %s.
                
                %s
                
                Report period: %s to %s.
                """;

    private static final Locale EMAIL_LOCALE =
            Locale.ENGLISH;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                    .withLocale(EMAIL_LOCALE);

    private final DailyReportMessageIdFactory messageIdFactory;

    public EmailSendingTask compose(
            Long userId,
            String recipientEmail,
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd,
            String summaryText
    ) {
        UUID messageId = messageIdFactory.create(
                userId,
                periodStart,
                periodEnd
        );

        String subject = buildSubject(periodStart, periodEnd);
        String body = buildBody(periodStart, periodEnd, summaryText);

        return new EmailSendingTask(
                messageId,
                recipientEmail,
                subject,
                body
        );
    }

    private String buildSubject(
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd
    ) {
        return "%s: %s - %s".formatted(
                SUBJECT_PREFIX,
                DATE_FORMATTER.format(periodStart),
                DATE_FORMATTER.format(periodEnd)
        );
    }

    private String buildBody(
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd,
            String summaryText
    ) {
        return BODY_TEMPLATE.formatted(
                DATE_FORMATTER.format(periodStart),
                summaryText,
                DATE_FORMATTER.format(periodStart),
                DATE_FORMATTER.format(periodEnd)
        );
    }
}
