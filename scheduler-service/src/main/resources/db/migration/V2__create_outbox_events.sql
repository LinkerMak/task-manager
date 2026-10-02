CREATE TABLE scheduler.outbox_events (
         id UUID PRIMARY KEY,

         workflow_request_id UUID NOT NULL,

         topic VARCHAR(255) NOT NULL,

         message_key VARCHAR(255) NOT NULL,

         payload JSONB NOT NULL,

         status VARCHAR(16) NOT NULL,

         CONSTRAINT fk_outbox_events_workflow
             FOREIGN KEY (workflow_request_id)
                 REFERENCES scheduler.daily_report_workflows (request_id),

         CONSTRAINT uq_outbox_events_workflow_topic
             UNIQUE (workflow_request_id, topic),

         CONSTRAINT chk_outbox_events_status
             CHECK (
                 status IN (
                            'PENDING',
                            'PUBLISHED'
                     )
                 )
);

CREATE INDEX idx_outbox_events_pending
    ON scheduler.outbox_events (status)
    WHERE status = 'PENDING';