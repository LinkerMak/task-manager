package com.example.task_manager_backend.integration.tests;

import com.example.task_manager_backend.AuthTestSupport;
import com.example.task_manager_backend.AuthTestSupport.AuthenticatedTestUser;
import com.example.task_manager_backend.TaskTestSupport;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskDeletionIntegrationTest extends AbstractIntegrationTest {

    private static final String TASKS_URL = "/tasks";

    private MockMvc mockMvc;
    private TaskTestSupport taskTestSupport;
    private AuthTestSupport authTestSupport;
    private TaskRepository taskRepository;

    @Autowired
    void setDependencies(MockMvc mockMvc,
                         TaskTestSupport taskTestSupport,
                         AuthTestSupport authTestSupport,
                         TaskRepository taskRepository) {
        this.mockMvc = mockMvc;
        this.taskTestSupport = taskTestSupport;
        this.authTestSupport = authTestSupport;
        this.taskRepository = taskRepository;
    }

    @Test
    @DisplayName("DELETE /tasks/{taskId} удаляет собственную задачу и возвращает 204")
    void shouldDeleteOwnTaskForAuthenticatedUser() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();

        Task task = taskTestSupport.createTask(user,
                "Buy milk",
                "2 liters"
        );

        mockMvc.perform(delete(TASKS_URL + "/{taskId}", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue()))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.findById(task.getId())).isEmpty();
        assertThat(taskRepository.count()).isZero();
    }

    @Test
    @DisplayName("DELETE /tasks/{taskId} без токена возвращает 401 и не удаляет задачу")
    void shouldRejectTaskDeletionWithoutToken() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();

        Task task = taskTestSupport.createTask(user,
                "Buy milk",
                "2 liters"
        );

        mockMvc.perform(delete(TASKS_URL + "/{taskId}", task.getId()))
                .andExpect(status().isUnauthorized());

        assertThat(taskRepository.findById(task.getId())).isPresent();
    }

    @Test
    @DisplayName("DELETE /tasks/{taskId} не позволяет удалить задачу другого пользователя и возвращает 404")
    void shouldRejectDeletionOfAnotherUsersTask() throws Exception {
        AuthenticatedTestUser owner = authTestSupport.registerNewUser();
        AuthenticatedTestUser anotherUser = authTestSupport.registerNewUser();

        Task task = taskTestSupport.createTask(owner,
                "Private task",
                "Must not be deleted"
        );

        mockMvc.perform(delete(TASKS_URL + "/{taskId}", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, anotherUser.bearerHeaderValue()))
                .andExpect(status().isNotFound());

        assertThat(taskRepository.findById(task.getId())).isPresent();
        assertThat(taskRepository.findById(task.getId()).orElseThrow().getOwner().getId())
                .isEqualTo(owner.userId());
    }

    @Test
    @DisplayName("DELETE /tasks/{taskId} для несуществующей задачи возвращает 404")
    void shouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();

        mockMvc.perform(delete(TASKS_URL + "/{taskId}", Long.MAX_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue()))
                .andExpect(status().isNotFound());

        assertThat(taskRepository.count()).isZero();
    }
}
