package org.example.scheduler.scheduler.dailyreport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scheduler.client.dailyreport.DailyReportSourceDataClient;
import org.example.scheduler.client.dailyreport.exception.DailyReportSourceDataUnavailableException;
import org.example.taskmanager.contracts.dailyreport.DailyReportSourceDataResponse;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyReportJob {

    private final DailyReportSourceDataClient dailyReportSourceDataClient;

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

    public void run(OffsetDateTime periodStart, OffsetDateTime periodEnd) {
        log.info(
                "Starting daily report job: periodStart={}, periodEnd={}",
                periodStart,
                periodEnd
        );

        try{
            DailyReportSourceDataResponse sourceData =
                    dailyReportSourceDataClient.getSourceData(
                            periodStart,
                            periodEnd
                    );

            log.info(
                    "Daily report source data successfully received: periodStart={}, periodEnd={}, usersCount={}",
                    sourceData.periodStart(),
                    sourceData.periodEnd(),
                    sourceData.users().size()
            );

            sourceData.users().forEach(user ->
                log.info(
                        "Daily report candidate: userId={}, email={}, tasksCount={}",
                        user.userId(),
                        user.email(),
                        user.tasks().size()
                )
            );
        } catch(DailyReportSourceDataUnavailableException e) {
            log.error(
                    "Daily report job failed because source data is unavailable: periodStart={}, periodEnd={}",
                    periodStart,
                    periodEnd,
                    e
            );
        }
    }
}
