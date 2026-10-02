package org.example.scheduler.repository.dailyreport;

import org.example.scheduler.entity.dailyreport.DailyReportWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailyReportWorkflowRepository extends JpaRepository<DailyReportWorkflow, UUID> {

    Optional<DailyReportWorkflow> findByUserIdAndPeriodStartAndPeriodEnd(
            Long userId,
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd);
}
