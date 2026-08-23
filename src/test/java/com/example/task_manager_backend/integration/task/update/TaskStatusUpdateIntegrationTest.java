package com.example.task_manager_backend.integration.task.update;

import com.example.task_manager_backend.AuthTestSupport;
import com.example.task_manager_backend.AuthTestSupport.AuthenticatedTestUser;
import com.example.task_manager_backend.TaskTestSupport;
import com.example.task_manager_backend.dto.web.task.update.UpdateTaskStatusRequest;
import com.example.task_manager_backend.integration.task.AbstractIntegrationTest;
import com.example.task_manager_backend.models.task.Task;
import com.example.task_manager_backend.models.task.TaskStatus;
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
class TaskStatusUpdateIntegrationTest extends AbstractIntegrationTest {

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
    @DisplayName("PATCH /tasks/{taskId}/status меняет статус собственной задачи на DONE и возвращает 200")
    void shouldChangeOwnTaskStatusToDone() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();
        Task task = taskTestSupport.createTask(user, "Buy milk", "2 liters");

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.DONE);

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/status", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("Buy milk"))
                .andExpect(jsonPath("$.description").value("2 liters"))
                .andExpect(jsonPath("$.taskStatus").value("DONE"))
                .andExpect(jsonPath("$.completedAt").isNotEmpty());

        Task updatedTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(updatedTask.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(updatedTask.getCompletedAt()).isNotNull();
        assertThat(updatedTask.getOwner().getId()).isEqualTo(user.userId());
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/status меняет DONE на TODO и очищает completedAt")
    void shouldChangeDoneTaskToTodoAndClearCompletedAt() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();
        Task task = taskTestSupport.createTask(user, "Buy milk", "2 liters");

        changeStatus(user, task.getId(), TaskStatus.DONE);

        Task completedTask = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(completedTask.getCompletedAt()).isNotNull();

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.TODO);

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/status", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskStatus").value("TODO"))
                .andExpect(jsonPath("$.completedAt").isEmpty());

        Task updatedTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(updatedTask.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(updatedTask.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/status без токена возвращает 401 и не изменяет задачу")
    void shouldRejectStatusChangeWithoutToken() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();
        Task task = taskTestSupport.createTask(user);

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.DONE);

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/status", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        Task unchangedTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(unchangedTask.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(unchangedTask.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/status не позволяет изменить задачу другого пользователя и возвращает 404")
    void shouldRejectStatusChangeForAnotherUsersTask() throws Exception {
        AuthenticatedTestUser owner = authTestSupport.registerNewUser();
        AuthenticatedTestUser anotherUser = authTestSupport.registerNewUser();
        Task task = taskTestSupport.createTask(owner, "Private task", "Not available to another user");

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.DONE);

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/status", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, anotherUser.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        Task unchangedTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(unchangedTask.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(unchangedTask.getCompletedAt()).isNull();
        assertThat(unchangedTask.getOwner().getId()).isEqualTo(owner.userId());
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/status для несуществующей задачи возвращает 404")
    void shouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.DONE);

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/status", Long.MAX_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        assertThat(taskRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/status без status возвращает 400 и не изменяет задачу")
    void shouldRejectStatusChangeWithoutStatus() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();
        Task task = taskTestSupport.createTask(user);

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/status", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {}
                                """))
                .andExpect(status().isBadRequest());

        Task unchangedTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(unchangedTask.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(unchangedTask.getCompletedAt()).isNull();
    }

    private void changeStatus(AuthenticatedTestUser user,
                              Long taskId,
                              TaskStatus status) throws Exception {
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(status);

        mockMvc.perform(patch(TASKS_URL + "/{taskId}/status", taskId)
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}