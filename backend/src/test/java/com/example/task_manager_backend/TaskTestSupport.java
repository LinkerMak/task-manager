package com.example.task_manager_backend;

import com.example.task_manager_backend.AuthTestSupport.AuthenticatedTestUser;
import com.example.task_manager_backend.dto.web.task.TaskRequest;
import com.example.task_manager_backend.models.task.Task;
import com.example.task_manager_backend.repositories.TaskRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
public class TaskTestSupport {

    private static final String TASKS_URL = "/tasks";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private final TaskRepository taskRepository;

    public Task createTask(AuthenticatedTestUser user) throws Exception {
        return createTask(user, "Test task", "Test task description");
    }

    public Task createTask(AuthenticatedTestUser user,
                           String title,
                           String description) throws Exception {
        TaskRequest request = new TaskRequest(title, description);

        String responseBody = mockMvc.perform(post(TASKS_URL)
                        .header(HttpHeaders.AUTHORIZATION, user.bearerHeaderValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode responseJson = objectMapper.readTree(responseBody);
        long taskId = responseJson.required("id").asLong();

        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException(
                        "Created task was not found: id=" + taskId
                ));
    }
}