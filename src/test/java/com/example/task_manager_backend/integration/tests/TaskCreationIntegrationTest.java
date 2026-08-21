package com.example.task_manager_backend.integration.tests;

import com.example.task_manager_backend.AuthTestSupport;
import com.example.task_manager_backend.AuthTestSupport.AuthenticatedTestUser;
import com.example.task_manager_backend.dto.web.task.TaskRequest;
import com.example.task_manager_backend.models.task.Task;
import com.example.task_manager_backend.repositories.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskCreationIntegrationTest extends AbstractIntegrationTest {

    private static final String TASKS_URL = "/tasks";

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AuthTestSupport authTestSupport;
    private TaskRepository taskRepository;

    @Autowired
    void setDependencies(MockMvc mockMvc,
                         ObjectMapper objectMapper,
                         AuthTestSupport authTestSupport,
                         TaskRepository taskRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.authTestSupport = authTestSupport;
        this.taskRepository = taskRepository;
    }

    @Test
    @DisplayName("POST /tasks создаёт задачу и возвращает 201 с Location и телом ответа")
    void shouldCreateTaskForAuthenticatedUser() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();
        TaskRequest request = new TaskRequest("Buy milk", "2 liters");

        mockMvc.perform(post(TASKS_URL)
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Buy milk"))
                .andExpect(jsonPath("$.description").value("2 liters"))
                .andExpect(jsonPath("$.taskStatus").value("TODO"));

        List<Task> tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(1);

        Task persistedTask = tasks.getFirst();
        assertThat(persistedTask.getTitle()).isEqualTo("Buy milk");
        assertThat(persistedTask.getDescription()).isEqualTo("2 liters");
        assertThat(persistedTask.getOwner().getId()).isEqualTo(user.userId());
        assertThat(persistedTask.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("POST /tasks без токена возвращает 401 и не сохраняет задачу")
    void shouldRejectTaskCreationWithoutToken() throws Exception {
        TaskRequest request = new TaskRequest("Buy milk", "2 liters");

        mockMvc.perform(post(TASKS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        assertThat(taskRepository.count()).isZero();
    }

    @Test
    @DisplayName("POST /tasks с пустым заголовком возвращает 400")
    void shouldRejectTaskWithBlankTitle() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();
        TaskRequest request = new TaskRequest("   ", "2 liters");

        mockMvc.perform(post(TASKS_URL)
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertThat(taskRepository.count()).isZero();
    }
}