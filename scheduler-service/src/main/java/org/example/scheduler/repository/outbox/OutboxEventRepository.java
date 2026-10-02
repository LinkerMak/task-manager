package org.example.scheduler.repository.outbox;

import org.example.scheduler.entity.outboxevent.OutboxEvent;
import org.example.scheduler.entity.outboxevent.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findAllByStatus(OutboxEventStatus status);
}
