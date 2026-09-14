package org.example.scheduler.scheduler.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scheduler.service.outbox.OutboxPublisherService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisherScheduler {

    private final OutboxPublisherService outboxPublisherService;

    @Scheduled(
            fixedDelayString = "${scheduler.outbox.publishing.fixed-delay}"
    )
    public void publishPendingEvents() {
        outboxPublisherService.publishPendingEvents();
    }
}