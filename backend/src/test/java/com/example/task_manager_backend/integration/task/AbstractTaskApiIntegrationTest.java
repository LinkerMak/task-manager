package com.example.task_manager_backend.integration.task;

import com.example.task_manager_backend.AuthTestSupport;
import com.example.task_manager_backend.TaskTestSupport;
import com.example.task_manager_backend.integration.AbstractDatabaseIntegrationTest;
import com.example.task_manager_backend.messaging.email.producer.EmailSendingTaskProducer;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Import({
        AuthTestSupport.class,
        TaskTestSupport.class
})
@AutoConfigureMockMvc
public class AbstractTaskApiIntegrationTest extends AbstractDatabaseIntegrationTest {

    @MockitoBean
    protected EmailSendingTaskProducer emailSendingTaskProducer;

}
