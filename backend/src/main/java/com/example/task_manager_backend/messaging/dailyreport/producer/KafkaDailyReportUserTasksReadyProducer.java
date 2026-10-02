package com.example.task_manager_backend.messaging.dailyreport.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanager.contracts.dailyreport.DailyReportUserTasksReady;
import org.example.taskmanager.contracts.dailyreport.topics.DailyReportTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDailyReportUserTasksReadyProducer
        implements DailyReportUserTasksReadyProducer {

    private final KafkaTemplate<String, DailyReportUserTasksReady> kafkaTemplate;

    @Override
    public void publish(DailyReportUserTasksReady message) {
        kafkaTemplate.send(
                DailyReportTopics.USER_TASKS_READY,
                message.userId().toString(),
                message
        ).whenComplete((result, exception) -> {
            if (exception == null) {
                log.info(
                        "Daily report user tasks published: userId={}, recipientEmail={}, topic={}, partition={}, offset={}",
                        message.userId(),
                        message.email(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
                return;
            }

            log.error(
                    "Failed to publish daily report user tasks: userId={}, recipientEmail={}",
                    message.userId(),
                    message.email(),
                    exception
            );
        });
    }
}