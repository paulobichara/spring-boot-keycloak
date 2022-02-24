package org.paulobichara.springbootapi.exception.keycloak;

import org.paulobichara.springbootapi.exception.ApiException;

import java.io.Serial;

public class UserAlreadyRegisteredException extends ApiException {

  @Serial private static final long serialVersionUID = 4125982132788720638L;

  public UserAlreadyRegisteredException(String username) {
    super("exception.keycloak.addUser.conflict", new Object[] {username});
  }
}
