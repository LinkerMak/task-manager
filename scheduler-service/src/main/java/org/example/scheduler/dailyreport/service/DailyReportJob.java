package org.example.scheduler.dailyreport.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scheduler.client.dailyreport.DailyReportSourceDataClient;
import org.example.scheduler.client.dailyreport.exception.DailyReportSourceDataUnavailableException;
import org.example.taskmanager.contracts.dailyreport.DailyReportSourceDataResponse;
import org.example.taskmanager.contracts.dailyreport.DailyReportUserData;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyReportJob {

    private final DailyReportSourceDataClient dailyReportSourceDataClient;

    private final DailyReportUserProcessor dailyReportUserProcessor;

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

            processUsers(
                    sourceData.users(),
                    sourceData.periodStart(),
                    sourceData.periodEnd()
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

    private void processUsers(
            Iterable<DailyReportUserData> users,
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd
    ) {
        for (DailyReportUserData user : users) {
            try {
                dailyReportUserProcessor.process(
                        user,
                        periodStart,
                        periodEnd
                );
            } catch (Exception e) {
                log.error(
                        "Failed to process daily report for user: userId={}, recipientEmail={}, periodStart={}, periodEnd={}",
                        user.userId(),
                        user.email(),
                        periodStart,
                        periodEnd,
                        e
                );
            }
        }
    }
}
