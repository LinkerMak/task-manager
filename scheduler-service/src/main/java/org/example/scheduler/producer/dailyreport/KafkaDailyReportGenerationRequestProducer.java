package org.example.scheduler.producer.dailyreport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanager.contracts.dailyreport.DailyReportGenerationRequest;
import org.example.taskmanager.contracts.dailyreport.topics.DailyReportTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDailyReportGenerationRequestProducer
        implements DailyReportGenerationRequestProducer {

    private static final String MESSAGE_KEY = "daily-report-generation";

    private final KafkaTemplate<String, DailyReportGenerationRequest> kafkaTemplate;

    @Override
    public void publish(DailyReportGenerationRequest request) {
        kafkaTemplate.send(
                DailyReportTopics.GENERATION_REQUESTS,
                MESSAGE_KEY,
                request
        ).whenComplete((result, exception) -> {
            if (exception == null) {
                log.info(
                        "Daily report generation request published: periodStart={}, periodEnd={}, topic={}, partition={}, offset={}",
                        request.periodStart(),
                        request.periodEnd(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
                return;
            }

            log.error(
                    "Failed to publish daily report generation request: periodStart={}, periodEnd={}",
                    request.periodStart(),
                    request.periodEnd(),
                    exception
            );
        });
    }
}
