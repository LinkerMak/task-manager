package org.example.scheduler.dailyreport.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scheduler.dailyreport.service.DailyReportJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReportScheduler {

    private final DailyReportJob dailyReportJob;

    @Scheduled(
            cron = "${scheduler.daily-report-scheduling.cron}",
            zone = "${scheduler.daily-report-scheduling.zone}"
    )
    public void scheduleDailyReport() {
        log.info("Scheduled daily report execution started");

        dailyReportJob.run();

        log.info("Scheduled daily report execution finished");
    }
}
