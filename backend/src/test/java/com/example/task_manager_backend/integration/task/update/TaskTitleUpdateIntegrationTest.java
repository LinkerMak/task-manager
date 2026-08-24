package com.example.task_manager_backend.integration.task.update;

import com.example.task_manager_backend.AuthTestSupport;
import com.example.task_manager_backend.AuthTestSupport.AuthenticatedTestUser;
import com.example.task_manager_backend.TaskTestSupport;
import com.example.task_manager_backend.dto.web.task.update.UpdateTitleRequest;
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
class TaskTitleUpdateIntegrationTest extends AbstractIntegrationTest {

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
    @DisplayName("PATCH /tasks/{taskId}/title обновляет заголовок собственной задачи и возвращает 200")
    void shouldUpdateOwnTaskTitle() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();
        Task task = taskTestSupport.createTask(user, "Old title", "Task description");

        UpdateTitleRequest request = new UpdateTitleRequest("New title");

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/title", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.description").value("Task description"))
                .andExpect(jsonPath("$.taskStatus").value("TODO"));

        Task updatedTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(updatedTask.getTitle()).isEqualTo("New title");
        assertThat(updatedTask.getDescription()).isEqualTo("Task description");
        assertThat(updatedTask.getOwner().getId()).isEqualTo(user.userId());
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/title без токена возвращает 401 и не обновляет задачу")
    void shouldRejectTitleUpdateWithoutToken() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();
        Task task = taskTestSupport.createTask(user, "Old title", "Task description");

        UpdateTitleRequest request = new UpdateTitleRequest("New title");

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/title", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        Task unchangedTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(unchangedTask.getTitle()).isEqualTo("Old title");
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/title не позволяет обновить задачу другого пользователя и возвращает 404")
    void shouldRejectTitleUpdateForAnotherUsersTask() throws Exception {
        AuthenticatedTestUser owner = authTestSupport.registerNewUser();
        AuthenticatedTestUser anotherUser = authTestSupport.registerNewUser();
        Task task = taskTestSupport.createTask(owner, "Private title", "Private description");

        UpdateTitleRequest request = new UpdateTitleRequest("Attempted update");

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/title", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, anotherUser.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        Task unchangedTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(unchangedTask.getTitle()).isEqualTo("Private title");
        assertThat(unchangedTask.getOwner().getId()).isEqualTo(owner.userId());
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/title для несуществующей задачи возвращает 404")
    void shouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();

        UpdateTitleRequest request = new UpdateTitleRequest("New title");

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/title", Long.MAX_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        assertThat(taskRepository.count()).isZero();
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/title с пустым заголовком возвращает 400 и не обновляет задачу")
    void shouldRejectTitleUpdateWithBlankTitle() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();
        Task task = taskTestSupport.createTask(user, "Old title", "Task description");

        UpdateTitleRequest request = new UpdateTitleRequest("   ");

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/title", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        Task unchangedTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(unchangedTask.getTitle()).isEqualTo("Old title");
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/title с заголовком длиннее 255 символов возвращает 400")
    void shouldRejectTitleUpdateWhenTitleIsTooLong() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();
        Task task = taskTestSupport.createTask(user, "Old title", "Task description");
        String titleLongerThanAllowed = "a".repeat(256);

        UpdateTitleRequest request = new UpdateTitleRequest(titleLongerThanAllowed);

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/title", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        Task unchangedTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(unchangedTask.getTitle()).isEqualTo("Old title");
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/title без поля title возвращает 400")
    void shouldRejectTitleUpdateWithoutTitle() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();
        Task task = taskTestSupport.createTask(user, "Old title", "Task description");

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/title", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {}
                                """))
                .andExpect(status().isBadRequest());

        Task unchangedTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(unchangedTask.getTitle()).isEqualTo("Old title");
    }
}