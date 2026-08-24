package com.example.task_manager_backend.integration.task.getting;

import com.example.task_manager_backend.AuthTestSupport;
import com.example.task_manager_backend.AuthTestSupport.AuthenticatedTestUser;
import com.example.task_manager_backend.TaskTestSupport;
import com.example.task_manager_backend.integration.task.AbstractIntegrationTest;
import com.example.task_manager_backend.models.task.Task;
import com.example.task_manager_backend.repositories.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskGettingIntegrationTest extends AbstractIntegrationTest {

    private static final String TASKS_URL = "/tasks";

    private MockMvc mockMvc;
    private AuthTestSupport authTestSupport;
    private TaskTestSupport taskTestSupport;
    private TaskRepository taskRepository;

    @Autowired
    void setDependencies(MockMvc mockMvc,
                         AuthTestSupport authTestSupport,
                         TaskTestSupport taskTestSupport,
                         TaskRepository taskRepository) {
        this.mockMvc = mockMvc;
        this.authTestSupport = authTestSupport;
        this.taskTestSupport = taskTestSupport;
        this.taskRepository = taskRepository;
    }

    @Test
    @DisplayName("GET /tasks возвращает 200 и задачи только текущего пользователя")
    void shouldReturnOnlyTasksOfAuthenticatedUser() throws Exception {
        AuthenticatedTestUser firstUser = authTestSupport.registerNewUser();
        AuthenticatedTestUser secondUser = authTestSupport.registerNewUser();

        Task firstTask = taskTestSupport.createTask(
                firstUser,
                "First user task",
                "Visible only to the first user"
        );
        Task secondTask = taskTestSupport.createTask(
                firstUser,
                "Second user task",
                "Visible only to the first user"
        );
        Task anotherUsersTask = taskTestSupport.createTask(
                secondUser,
                "Private task",
                "Must not appear in first user's response"
        );

        mockMvc.perform(get(TASKS_URL)
                        .header(HttpHeaders.AUTHORIZATION, firstUser.bearerHeaderValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[*].id").value(
                        org.hamcrest.Matchers.containsInAnyOrder(
                                firstTask.getId().intValue(),
                                secondTask.getId().intValue()
                        )
                ))
                .andExpect(jsonPath("$.content[*].id").value(
                        org.hamcrest.Matchers.not(
                                org.hamcrest.Matchers.hasItem(
                                        anotherUsersTask.getId().intValue()
                                )
                        )
                ));

        assertThat(taskRepository.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("GET /tasks без токена возвращает 401")
    void shouldRejectGettingTasksWithoutToken() throws Exception {
        mockMvc.perform(get(TASKS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /tasks возвращает пустую страницу для пользователя без задач")
    void shouldReturnEmptyPageForUserWithoutTasks() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();

        mockMvc.perform(get(TASKS_URL)
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /tasks без параметров использует сортировку id по убыванию")
    void shouldSortTasksByIdDescendingByDefault() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();

        Task firstCreatedTask = taskTestSupport.createTask(user, "First task", "First");
        Task secondCreatedTask = taskTestSupport.createTask(user, "Second task", "Second");
        Task thirdCreatedTask = taskTestSupport.createTask(user, "Third task", "Third");

        mockMvc.perform(get(TASKS_URL)
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].id").value(thirdCreatedTask.getId()))
                .andExpect(jsonPath("$.content[1].id").value(secondCreatedTask.getId()))
                .andExpect(jsonPath("$.content[2].id").value(firstCreatedTask.getId()));
    }

    @Test
    @DisplayName("GET /tasks с page и size возвращает нужную страницу")
    void shouldReturnRequestedPage() throws Exception {
        AuthenticatedTestUser user = authTestSupport.registerNewUser();

        Task firstCreatedTask = taskTestSupport.createTask(user, "First task", "First");
        Task secondCreatedTask = taskTestSupport.createTask(user, "Second task", "Second");
        Task thirdCreatedTask = taskTestSupport.createTask(user, "Third task", "Third");

        mockMvc.perform(get(TASKS_URL)
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(firstCreatedTask.getId()))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));
    }
}