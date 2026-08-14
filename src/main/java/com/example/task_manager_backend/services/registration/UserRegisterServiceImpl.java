package com.example.task_manager_backend.services.registration;

import com.example.task_manager_backend.dto.web.security.RegisterRequest;
import com.example.task_manager_backend.exceptions.resource.ResourceAlreadyExistsException;
import com.example.task_manager_backend.models.User;
import com.example.task_manager_backend.repositories.UserRepository;
import com.example.task_manager_backend.services.email.normalizer.EmailNormalizer;
import com.example.task_manager_backend.services.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegisterServiceImpl implements UserRegisterService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public String register(RegisterRequest registerRequest) {
        String normalizedEmail = EmailNormalizer.normalize(
                registerRequest.getEmail()
        );

        log.info("Register attempt started: email={}", normalizedEmail);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResourceAlreadyExistsException("This email is already taken");
        }

        User user = userRepository.save(new User(
                normalizedEmail,
                passwordEncoder.encode(registerRequest.getPassword())
        ));

        String accessToken = jwtService.generateAccessToken(user.getId(), normalizedEmail);

        log.info("Register successful: userId={}, email={}", user.getId(), normalizedEmail);

        return accessToken;
    }
}
