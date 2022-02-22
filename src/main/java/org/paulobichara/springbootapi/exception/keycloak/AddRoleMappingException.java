package org.paulobichara.springbootapi.exception.keycloak;

import org.paulobichara.springbootapi.exception.ApiException;

import java.io.Serial;

public class AddRoleMappingException extends ApiException {
  @Serial private static final long serialVersionUID = 6513105166785532313L;

  public AddRoleMappingException(String username) {
    super("exception.keycloak.addRoleMapping.failed", new Object[] {username});
  }
}
