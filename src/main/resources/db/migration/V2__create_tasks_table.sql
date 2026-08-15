CREATE TABLE tasks (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       status VARCHAR(32) NOT NULL DEFAULT 'TODO',
                       completed_at TIMESTAMP WITH TIME ZONE,
                       owner_id BIGINT NOT NULL,

                       CONSTRAINT chk_tasks_status
                           CHECK (status IN ('TODO', 'DONE')),

                       CONSTRAINT chk_tasks_completed_at
                           CHECK (
                               (status = 'TODO' AND completed_at IS NULL)
                                   OR
                               (status = 'DONE' AND completed_at IS NOT NULL)
                               ),

                       CONSTRAINT fk_tasks_owner
                           FOREIGN KEY (owner_id)
                               REFERENCES users (id)
                               ON DELETE CASCADE
);

CREATE INDEX idx_tasks_owner_id ON tasks (owner_id);