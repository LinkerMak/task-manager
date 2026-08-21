package com.example.task_manager_backend;

import com.example.task_manager_backend.AuthTestSupport.AuthenticatedTestUser;
import com.example.task_manager_backend.dto.web.task.TaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
@RequiredArgsConstructor
public class TaskTestSupport {

    private static final String TASKS_URL = "/tasks";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public CreatedTestTask createTask(AuthenticatedTestUser user) throws Exception {
        return createTask(user, "Test task", "Test task description");
    }

    public CreatedTestTask createTask(AuthenticatedTestUser user,
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

        JsonNode response = objectMapper.readTree(responseBody);

        return new CreatedTestTask(
                response.get("id").asLong(),
                response.get("title").asText(),
                response.get("description").asText()
        );
    }

    public record CreatedTestTask(
            Long id,
            String title,
            String description
    ) {
    }
}