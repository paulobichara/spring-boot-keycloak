package org.paulobichara.springbootapi.exception.keycloak;

import org.paulobichara.springbootapi.exception.ApiException;

import java.io.Serial;

public class GetTokenException extends ApiException {

  @Serial private static final long serialVersionUID = 3303857339674281179L;

  public GetTokenException() {
    super("exception.keycloak.token.failed", new Object[] {});
  }
}
