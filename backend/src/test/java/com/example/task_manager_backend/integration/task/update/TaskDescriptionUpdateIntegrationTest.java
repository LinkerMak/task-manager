package com.example.task_manager_backend.integration.task.update;

import com.example.task_manager_backend.AuthTestSupport;
import com.example.task_manager_backend.AuthTestSupport.AuthenticatedTestUser;
import com.example.task_manager_backend.TaskTestSupport;
import com.example.task_manager_backend.dto.web.task.update.UpdateDescriptionRequest;
import com.example.task_manager_backend.integration.task.AbstractIntegrationTest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskDescriptionUpdateIntegrationTest extends AbstractIntegrationTest {

    private static final String TASKS_URL = "/tasks";

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AuthTestSupport authTestSupport;
    private TaskTestSupport taskTestSupport;
    private TaskRepository taskRepository;

    @Autowired
    void setDependencies(MockMvc mockMvc,
                         ObjectMapper objectMapper,
                         AuthTestSupport authTestSupport,
                         TaskTestSupport taskTestSupport,
                         TaskRepository taskRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.authTestSupport = authTestSupport;
        this.taskTestSupport = taskTestSupport;
        this.taskRepository = taskRepository;
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/description обновляет описание собственной задачи и возвращает 200")
    void shouldUpdateOwnTaskDescription() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();
        Task task = taskTestSupport.createTask(user, "Task title", "Old description");

        UpdateDescriptionRequest request = new UpdateDescriptionRequest("New description");

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/description", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("Task title"))
                .andExpect(jsonPath("$.description").value("New description"))
                .andExpect(jsonPath("$.taskStatus").value("TODO"));

        Task updatedTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(updatedTask.getDescription()).isEqualTo("New description");
        assertThat(updatedTask.getTitle()).isEqualTo("Task title");
        assertThat(updatedTask.getOwner().getId()).isEqualTo(user.userId());
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/description очищает описание задачи при null")
    void shouldClearTaskDescriptionWhenDescriptionIsNull() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();
        Task task = taskTestSupport.createTask(user, "Task title", "Existing description");

        UpdateDescriptionRequest request = new UpdateDescriptionRequest(null);

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/description", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").doesNotExist());

        Task updatedTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(updatedTask.getDescription()).isNull();
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/description без токена возвращает 401 и не обновляет описание")
    void shouldRejectDescriptionUpdateWithoutToken() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();
        Task task = taskTestSupport.createTask(user, "Task title", "Old description");

        UpdateDescriptionRequest request = new UpdateDescriptionRequest("New description");

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/description", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        Task unchangedTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(unchangedTask.getDescription()).isEqualTo("Old description");
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/description не позволяет обновить задачу другого пользователя и возвращает 404")
    void shouldRejectDescriptionUpdateForAnotherUsersTask() throws Exception {
        AuthenticatedTestUser owner = authTestSupport.registerNewUser();
        AuthenticatedTestUser anotherUser = authTestSupport.registerNewUser();
        Task task = taskTestSupport.createTask(owner, "Private title", "Private description");

        UpdateDescriptionRequest request = new UpdateDescriptionRequest("Attempted update");

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/description", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, anotherUser.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        Task unchangedTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(unchangedTask.getDescription()).isEqualTo("Private description");
        assertThat(unchangedTask.getOwner().getId()).isEqualTo(owner.userId());
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/description для несуществующей задачи возвращает 404")
    void shouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();

        UpdateDescriptionRequest request = new UpdateDescriptionRequest("New description");

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/description", Long.MAX_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        assertThat(taskRepository.count()).isZero();
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/description длиннее 10000 символов возвращает 400")
    void shouldRejectDescriptionUpdateWhenDescriptionIsTooLong() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();
        Task task = taskTestSupport.createTask(user, "Task title", "Old description");
        String descriptionLongerThanAllowed = "a".repeat(10_001);

        UpdateDescriptionRequest request = new UpdateDescriptionRequest(descriptionLongerThanAllowed);

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/description", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        Task unchangedTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(unchangedTask.getDescription()).isEqualTo("Old description");
    }
}
