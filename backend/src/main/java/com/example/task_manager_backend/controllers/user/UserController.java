package com.example.task_manager_backend.controllers.user;

import com.example.task_manager_backend.dto.web.security.RegisterRequest;
import com.example.task_manager_backend.dto.web.user.CurrentUserResponse;
import com.example.task_manager_backend.services.registration.UserRegisterService;
import com.example.task_manager_backend.services.user.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.task_manager_backend.security.constants.SecurityConstants.BEARER_TOKEN_PREFIX;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRegisterService userRegisterService;
    private final CurrentUserService currentUserService;

    @PostMapping
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterRequest registerRequest) {

        String jwtToken = userRegisterService.register(registerRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN_PREFIX + jwtToken)
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getCurrentUser(
            @AuthenticationPrincipal Long userId) {

        CurrentUserResponse userResponse = currentUserService.getUser(userId);

        return ResponseEntity
                .ok()
                .body(userResponse);
    }
}
