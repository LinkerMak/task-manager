package org.example.scheduler.client.dailyreport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.scheduler.client.dailyreport.exception.DailyReportSourceDataUnavailableException;
import org.example.scheduler.config.DailyReportSourceRpcProperties;
import org.example.taskmanager.contracts.dailyreport.DailyReportSourceDataRequest;
import org.example.taskmanager.contracts.dailyreport.DailyReportSourceDataResponse;
import org.example.taskmanager.contracts.dailyreport.topics.DailyReportTopics;
import org.springframework.kafka.requestreply.KafkaReplyTimeoutException;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDailyReportSourceDataClient implements DailyReportSourceDataClient{

    private static final String REQUEST_KEY = "daily-report-source-data";

    private final ReplyingKafkaTemplate<
            String,
            DailyReportSourceDataRequest,
            DailyReportSourceDataResponse>
            dailyReportReplyingKafkaTemplate;

    private final DailyReportSourceRpcProperties dailyReportProperties;

    @Override
    public DailyReportSourceDataResponse getSourceData(OffsetDateTime periodStart, OffsetDateTime periodEnd) {
        validatePeriod(periodStart, periodEnd);


        DailyReportSourceDataRequest request = new DailyReportSourceDataRequest(
                periodStart,
                periodEnd
        );

        log.info(
                "Requesting daily report source data: periodStart={}, periodEnd={}",
                periodStart,
                periodEnd
        );

        try{
            ProducerRecord<String, DailyReportSourceDataRequest> producerRecord =
                    new ProducerRecord<>(
                            DailyReportTopics.SOURCE_DATA_REQUEST,
                            REQUEST_KEY,
                            request
                    );

            ConsumerRecord<String, DailyReportSourceDataResponse> reply =
                    dailyReportReplyingKafkaTemplate.sendAndReceive(
                            producerRecord,
                            dailyReportProperties.replyTimeout()
                    ).get();

            DailyReportSourceDataResponse response = reply.value();

            validateResponse(response, periodStart, periodEnd);

            log.info(
                    "Daily report source data received: periodStart={}, periodEnd={}, usersCount={}",
                    response.periodStart(),
                    response.periodEnd(),
                    response.users().size()
            );

            return response;
        } catch (KafkaReplyTimeoutException exception) {
            log.error(
                    "Timed out while waiting for daily report source data: periodStart={}, periodEnd={}, timeout={}",
                    periodStart,
                    periodEnd,
                    dailyReportProperties.replyTimeout(),
                    exception
            );

            throw new DailyReportSourceDataUnavailableException(
                    "Timed out while requesting daily report source data",
                    exception
            );
        }  catch (ExecutionException e) {
            log.error(
                    "Failed to request daily report source data: periodStart={}, periodEnd={}",
                    periodStart,
                    periodEnd,
                    e
            );

            throw new DailyReportSourceDataUnavailableException(
                    "Failed to request daily report source data",
                    e.getCause()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            log.error(
                    "Interrupted while waiting for daily report source data: periodStart={}, periodEnd={}",
                    periodStart,
                    periodEnd,
                    e
            );

            throw new DailyReportSourceDataUnavailableException(
                    "Interrupted while requesting daily report source data",
                    e
            );
        }
    }

    private void validatePeriod(
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd
    ) {
        if (periodStart == null || periodEnd == null) {
            throw new IllegalArgumentException(
                    "Daily report period boundaries must not be null"
            );
        }

        if (!periodStart.isBefore(periodEnd)) {
            throw new IllegalArgumentException(
                    "Daily report period start must be before period end"
            );
        }
    }

    private void validateResponse(
            DailyReportSourceDataResponse response,
            OffsetDateTime expectedPeriodStart,
            OffsetDateTime expectedPeriodEnd
    ) {
        if (response == null) {
            throw new DailyReportSourceDataUnavailableException(
                    "Backend returned an empty daily report source data response",
                    null
            );
        }

        if (!expectedPeriodStart.equals(response.periodStart())
                || !expectedPeriodEnd.equals(response.periodEnd())) {
            throw new DailyReportSourceDataUnavailableException(
                    "Backend returned daily report source data for an unexpected period",
                    null
            );
        }
    }
}
