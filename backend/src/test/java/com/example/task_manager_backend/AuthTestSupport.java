package com.example.task_manager_backend;

import com.example.task_manager_backend.dto.web.security.RegisterRequest;
import com.example.task_manager_backend.services.jwt.JwtService;
import com.example.task_manager_backend.services.registration.UserRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.util.UUID;

@TestComponent
@RequiredArgsConstructor
public class AuthTestSupport {
    private static final String DEFAULT_TEST_PASSWORD = "Password1";

    private final UserRegisterService userRegisterService;
    private final JwtService jwtService;

    public AuthenticatedTestUser registerNewUser() {
        String email =
                "test-" + UUID.randomUUID() + "@example.com";

        String jwtToken = userRegisterService.register(new RegisterRequest(
                email,
                DEFAULT_TEST_PASSWORD
        ));

        return new AuthenticatedTestUser(
                jwtService.extractUserId(jwtToken),
                email,
                jwtToken);
    }

    public record AuthenticatedTestUser(Long userId, String email, String accessToken) {
        public String bearerHeaderValue() {
            return "Bearer " + accessToken;
        }
    }
}
