package com.example.task_manager_backend.security.config.dailyreport.filter;

import com.example.task_manager_backend.security.config.dailyreport.InternalApiProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class InternalApiKeyAuthenticationFilter extends OncePerRequestFilter {

    public static final String INTERNAL_API_KEY_HEADER = "X-Internal-Api-Key";
    public static final String SCHEDULER_AUTHORITY = "SCHEDULER";

    private static final String DAILY_REPORT_SOURCE_DATA_PATH =
            "/internal/daily-reports/source-data";

    private final InternalApiProperties internalApiProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HttpMethod.GET.matches(request.getMethod())
                || !DAILY_REPORT_SOURCE_DATA_PATH.equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.warn(
                "Internal API key filter invoked: method={}, uri={}, apiKeyPresent={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getHeader(INTERNAL_API_KEY_HEADER) != null
        );

        String apiKey = request.getHeader(INTERNAL_API_KEY_HEADER);

        if (!internalApiProperties.schedulerApiKey().equals(apiKey)) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        "scheduler-service",
                        null,
                        AuthorityUtils.createAuthorityList(SCHEDULER_AUTHORITY)
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        log.warn(
                "Internal API key accepted: principal={}, authorities={}",
                authentication.getPrincipal(),
                authentication.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
