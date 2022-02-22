package org.paulobichara.springbootapi.exception.keycloak;

import org.paulobichara.springbootapi.exception.ApiException;

import java.io.Serial;

public class UserRegistrationException extends ApiException {

  @Serial private static final long serialVersionUID = -35925900849101645L;

  public UserRegistrationException(String username) {
    super("exception.keycloak.addUser.failed", new Object[] {username});
  }
}
