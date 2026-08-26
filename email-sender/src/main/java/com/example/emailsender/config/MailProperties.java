package com.example.emailsender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.app.mail")
public record MailProperties(String from) {
}
