package org.paulobichara.springbootapi.dto.keycloak;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.paulobichara.springbootapi.dto.NewUserRequest;

import java.security.Principal;

public record User(String username,
                   String email,
                   String firstName,
                   String lastName,
                   Boolean enabled,
                   Credential[] credentials) {

    public static User from(Principal principal) {
        if (principal instanceof KeycloakAuthenticationToken keycloakToken) {
            AccessToken token = keycloakToken.getAccount().getKeycloakSecurityContext().getToken();
            return new User(token.getPreferredUsername(), token.getEmail(), token.getGivenName(), token.getFamilyName(), true, null);
        }
        return null;
    }

    public static User from(NewUserRequest newUser) {
        Credential credential = new Credential("password", newUser.password(), false);

        return new User(
                newUser.username(),
                newUser.email(),
                newUser.firstName(),
                newUser.lastName(),
                true,
                new Credential[] {credential});
    }

}
