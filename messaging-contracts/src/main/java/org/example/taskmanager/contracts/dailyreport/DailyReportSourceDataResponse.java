package org.example.taskmanager.contracts.dailyreport;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.List;

public record DailyReportSourceDataResponse(
        @NotNull
        OffsetDateTime periodStart,

        @NotNull
        OffsetDateTime periodEnd,

        @NotNull
        List<@Valid DailyReportUserData> users
){
}
