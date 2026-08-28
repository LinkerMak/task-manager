CREATE TABLE email_deliveries
(
    message_id UUID PRIMARY KEY,
    recipient_email VARCHAR(320) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    sent_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_email_deliveries_status CHECK (
        status IN ('PROCESSING', 'SENT', 'FAILED')
        )

);