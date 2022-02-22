package org.paulobichara.springbootapi.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record ApiValidationError(String field, Object rejectedValue, String message) {}
