package org.example.scheduler.service.dailyreport.generation.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scheduler.producer.dailyreport.DailyReportGenerationRequestProducer;
import org.example.taskmanager.contracts.dailyreport.DailyReportGenerationRequest;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyReportGenerationJob {

    private final DailyReportGenerationRequestProducer requestProducer;
    private final Clock clock;

    public void run() {
        OffsetDateTime periodEnd = OffsetDateTime.now(clock)
                .withOffsetSameInstant(ZoneOffset.UTC)
                .toLocalDate()
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);

        OffsetDateTime periodStart = periodEnd.minusDays(1);

        run(periodStart, periodEnd);
    }

    public void run(
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd
    ) {
        if (!periodStart.isBefore(periodEnd)) {
            throw new IllegalArgumentException(
                    "Period start must be before period end"
            );
        }

        DailyReportGenerationRequest request =
                new DailyReportGenerationRequest(
                        periodStart,
                        periodEnd
                );

        log.info(
                "Starting daily report generation: periodStart={}, periodEnd={}",
                periodStart,
                periodEnd
        );

        requestProducer.publish(request);

        log.info(
                "Daily report generation request submitted: periodStart={}, periodEnd={}",
                periodStart,
                periodEnd
        );
    }
}