package org.paulobichara.springbootapi.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO used to retrieve API error messages to clients.
 */
@Getter
@RequiredArgsConstructor
public class ApiError {
    @NonNull
    private String message;

    private final LocalDateTime timestamp = LocalDateTime.now();

    @Setter
    @JsonInclude(Include.NON_NULL)
    private List<ApiValidationError> validationErrors;
}
