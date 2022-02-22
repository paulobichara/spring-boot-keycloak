package org.paulobichara.springbootapi.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessToken(@JsonProperty("access_token") String accessToken,
                          @JsonProperty("expires_in") Long expiresIn,
                          @JsonProperty("refresh_token") String refreshToken,
                          @JsonProperty("refresh_expires_in") Long refreshTokenExpiresIn,
                          @JsonProperty("token_type") String tokenType,
                          @JsonProperty("not-before-policy") Integer notBeforePolicy,
                          @JsonProperty("session_state") String sessionState,
                          String scope) {
}
