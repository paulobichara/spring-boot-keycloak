package org.paulobichara.springbootapi.exception;

import java.io.Serial;

public class UserAlreadyRegisteredException extends ApiException {

    @Serial
    private static final long serialVersionUID = 3880771284367095506L;

    public UserAlreadyRegisteredException(String email) {
        super("exception.user.alreadyRegistered", new Object[]{email});
    }
}
