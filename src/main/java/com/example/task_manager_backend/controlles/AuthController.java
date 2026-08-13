package com.example.task_manager_backend.controlles;

import com.example.task_manager_backend.dto.web.security.LoginRequest;
import com.example.task_manager_backend.services.UserAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.example.task_manager_backend.security.SecurityConstants.BEARER_TOKEN_PREFIX;

@RestController("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request) {
        String jwtToken = userAuthService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN_PREFIX + jwtToken)
                .build();
    }

}
