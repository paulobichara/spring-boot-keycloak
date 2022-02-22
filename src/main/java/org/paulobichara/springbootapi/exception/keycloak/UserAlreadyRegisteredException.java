package org.paulobichara.springbootapi.exception.keycloak;

import org.paulobichara.springbootapi.exception.ApiException;

import java.io.Serial;

public class UserAlreadyRegisteredException extends ApiException {

  @Serial private static final long serialVersionUID = 3880771284367095506L;

  public UserAlreadyRegisteredException(String username) {
    super("exception.keycloak.addUser.conflict", new Object[] {username});
  }
}
