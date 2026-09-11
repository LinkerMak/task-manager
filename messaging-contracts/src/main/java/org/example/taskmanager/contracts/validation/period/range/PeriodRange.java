package org.example.taskmanager.contracts.validation.period.range;

import java.time.OffsetDateTime;

public interface PeriodRange {

    OffsetDateTime periodStart();

    OffsetDateTime periodEnd();
}
