package com.example.task_manager_backend.controllers.dailyreport;

import com.example.task_manager_backend.dto.web.dailyreport.DailyReportSourceDataResponse;
import com.example.task_manager_backend.services.dailyreport.DailyReportSourceDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/daily-reports")
public class DailyReportInternalController {

    private final DailyReportSourceDataService dailyReportSourceDataService;

    @GetMapping("source-data")
    public ResponseEntity<DailyReportSourceDataResponse> getSourceData(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime periodStart,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime periodEnd
    ) {
        DailyReportSourceDataResponse response =
                dailyReportSourceDataService.getDataSource(periodStart, periodEnd);

        return ResponseEntity.ok(response);
    }
}
