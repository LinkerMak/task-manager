CREATE SCHEMA IF NOT EXISTS scheduler;

CREATE TABLE scheduler.daily_report_workflows (
      request_id UUID PRIMARY KEY,
      user_id BIGINT NOT NULL,
      recipient_email VARCHAR(100) NOT NULL,
      period_start TIMESTAMP WITH TIME ZONE NOT NULL,
      period_end TIMESTAMP WITH TIME ZONE NOT NULL,
      summary_text TEXT,
      status VARCHAR(32) NOT NULL,

      CONSTRAINT chk_daily_report_workflows_period
          CHECK (period_end > period_start),

      CONSTRAINT chk_daily_report_workflows_status
          CHECK (
              status IN (
                         'REQUESTED',
                         'SUMMARY_GENERATED',
                         'EMAIL_QUEUED'
                  )
              ),

      CONSTRAINT uq_daily_report_workflows_user_period
          UNIQUE (user_id, period_start, period_end)
);

CREATE INDEX idx_daily_report_workflows_status
    ON scheduler.daily_report_workflows (status);