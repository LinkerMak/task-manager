package com.example.task_manager_backend.services.email.normalizer;

import lombok.experimental.UtilityClass;

import java.util.Locale;

@UtilityClass
public class EmailNormalizer {

    public static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
