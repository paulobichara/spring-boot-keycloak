package org.paulobichara.springbootapi.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/** DTO used to retrieve API error messages to clients. */
public record ApiError(LocalDateTime timestamp, @NotNull String message, @JsonInclude(Include.NON_NULL) List<ApiValidationError> validationErrors) {}
