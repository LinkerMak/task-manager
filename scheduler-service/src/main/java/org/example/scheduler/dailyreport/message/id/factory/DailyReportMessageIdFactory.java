package org.example.scheduler.dailyreport.message.id.factory;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class DailyReportMessageIdFactory {

    public UUID create(
            Long userId,
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd
    ) {
        String source = String.join(
                ":",
                "daily-report",
                userId.toString(),
                periodStart.toInstant().toString(),
                periodEnd.toInstant().toString()
        );

        return UUID.nameUUIDFromBytes(
                source.getBytes(StandardCharsets.UTF_8)
        );
    }


}
