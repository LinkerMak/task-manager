package com.example.task_manager_backend.controlles;

import com.example.task_manager_backend.dto.web.security.RegisterRequest;
import com.example.task_manager_backend.services.UserRegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.example.task_manager_backend.security.SecurityConstants.BEARER_TOKEN_PREFIX;

@RestController("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRegisterService userRegisterService;

    @PostMapping
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterRequest registerRequest) {

        String jwtToken = userRegisterService.register(registerRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN_PREFIX + jwtToken)
                .build();
    }


}
