package com.example.task_manager_backend.security.filter;

import com.example.task_manager_backend.repositories.UserRepository;
import com.example.task_manager_backend.security.constants.SecurityConstants;
import com.example.task_manager_backend.services.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return true;
        }

        String authorizationHeader = request.getHeader(
                HttpHeaders.AUTHORIZATION
        );

        return authorizationHeader == null
                || !authorizationHeader.startsWith(
                SecurityConstants.BEARER_TOKEN_PREFIX
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.debug(
                "JWT authentication filter started: method={}, path={}",
                request.getMethod(),
                request.getRequestURI()
        );

        String authorizationHeader = request.getHeader(
                HttpHeaders.AUTHORIZATION
        );

        String accessToken = authorizationHeader.substring(
                SecurityConstants.BEARER_TOKEN_PREFIX.length()
        );

        if (!jwtService.isAccessTokenValid(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = jwtService.extractUserId(accessToken);

        if (!userRepository.existsById(userId)) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        log.debug(
                "JWT authentication successful: userId={}, method={}, path={}",
                userId,
                request.getMethod(),
                request.getRequestURI()
        );

        filterChain.doFilter(request, response);
    }

}
