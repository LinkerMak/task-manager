package org.example.scheduler.service.outbox;

import lombok.RequiredArgsConstructor;
import org.example.scheduler.entity.outboxevent.OutboxEvent;
import org.example.scheduler.repository.outbox.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxEventStatusService {

    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public void markPublished(UUID eventId) {
        OutboxEvent event = outboxEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalStateException(
                        "Outbox event not found: " + eventId
                ));

        event.markPublished();
    }
}