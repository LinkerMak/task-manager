package org.example.scheduler.scheduler.dailyreport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scheduler.service.dailyreport.generation.job.DailyReportGenerationJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReportScheduler {

    private final DailyReportGenerationJob dailyReportGenerationJob;

    @Scheduled(
            cron = "${scheduler.daily-report-scheduling.cron}",
            zone = "${scheduler.daily-report-scheduling.zone}"
    )
    public void scheduleDailyReport() {
        log.info("Scheduled daily report generation started");

        dailyReportGenerationJob.run();

        log.info("Scheduled daily report generation request finished");
    }
}