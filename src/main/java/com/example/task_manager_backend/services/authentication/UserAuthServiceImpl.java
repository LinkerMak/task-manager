package com.example.task_manager_backend.services.authentication;

import com.example.task_manager_backend.dto.web.security.LoginRequest;
import com.example.task_manager_backend.exceptions.authentication.InvalidCredentialsException;
import com.example.task_manager_backend.models.User;
import com.example.task_manager_backend.repositories.UserRepository;
import com.example.task_manager_backend.services.email.normalizer.EmailNormalizer;
import com.example.task_manager_backend.services.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String login(LoginRequest loginRequest) {

        String normalizedEmail = EmailNormalizer.normalize(
                loginRequest.getEmail()
        );

        log.info("Login attempt started: email={}", normalizedEmail);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> createInvalidCredentialException(normalizedEmail));

        if (!passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPasswordHash()
        )) {
            throw createInvalidCredentialException(normalizedEmail);
        }

        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail()
        );

        log.info("Login successful: userId={}, email={}",
                user.getId(),
                user.getEmail());

        return accessToken;
    }

    private InvalidCredentialsException createInvalidCredentialException(String email) {
        log.warn(
                "Login failed: invalid credentials for email={}",
                email
        );

        return new InvalidCredentialsException(
                "Invalid email or password"
        );
    }
}
