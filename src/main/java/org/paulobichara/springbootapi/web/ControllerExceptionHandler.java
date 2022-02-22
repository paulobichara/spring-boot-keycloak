package org.paulobichara.springbootapi.web;

import org.paulobichara.springbootapi.dto.error.ApiError;
import org.paulobichara.springbootapi.dto.error.ApiValidationError;
import org.paulobichara.springbootapi.exception.ApiException;
import org.paulobichara.springbootapi.exception.keycloak.UserAlreadyRegisteredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);
    private static final String VALIDATION_FAILED = "exception.validation.failure";

    private final MessageSource messageSource;

    ControllerExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<ApiValidationError> subErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                subErrors.add(new ApiValidationError(fieldError.getField(), fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage()));
            } else {
                subErrors.add(new ApiValidationError(null, null, error.getDefaultMessage()));
            }
        });

        ApiError apiError = new ApiError(LocalDateTime.now(), messageSource.getMessage(VALIDATION_FAILED, new Object[]{}, request.getLocale()), subErrors);
        LOGGER.error(apiError.message(), ex);

        return new ResponseEntity<>(apiError, headers, status);
    }

    @ExceptionHandler({UserAlreadyRegisteredException.class})
    protected ResponseEntity<ApiError> handleConflictApiExceptions(ApiException exception, Locale locale) {
        return handleApiException(HttpStatus.CONFLICT, exception, locale);
    }

    private ResponseEntity<ApiError> handleApiException(HttpStatus status, ApiException exception, Locale locale) {
        String message = messageSource.getMessage(exception.getMessage(), exception.getArgs(), locale);
        ApiError error = new ApiError(LocalDateTime.now(), message, null);
        LOGGER.error(error.message(), exception);
        return new ResponseEntity<>(error, status);
    }
}
