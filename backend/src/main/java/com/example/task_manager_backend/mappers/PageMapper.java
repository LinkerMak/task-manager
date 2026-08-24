package com.example.task_manager_backend.mappers;

import com.example.task_manager_backend.dto.web.pages.PagedResponse;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

@UtilityClass
public class PageMapper {

    public static <T> PagedResponse<T> from(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

}
