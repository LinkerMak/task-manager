package com.example.task_manager_backend.services.user;

import com.example.task_manager_backend.dto.web.user.CurrentUserResponse;
import com.example.task_manager_backend.exceptions.resource.ResourceNotFoundException;
import com.example.task_manager_backend.models.user.User;
import com.example.task_manager_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public CurrentUserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found by id:" + id)
                );

        return new CurrentUserResponse(
                user.getId(),
                user.getEmail()
        );
    }
}
