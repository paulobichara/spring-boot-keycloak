package org.paulobichara.springbootapi.exception;

import lombok.Getter;

import java.io.Serial;

public class ApiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6831948177829082903L;

    @Getter
    private final Object[] args;

    ApiException(String messageProperty, Object[] args) {
        super(messageProperty);
        this.args = args;
    }
}
