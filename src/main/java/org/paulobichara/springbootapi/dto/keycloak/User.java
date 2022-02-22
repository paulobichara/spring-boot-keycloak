package org.paulobichara.springbootapi.dto.keycloak;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.paulobichara.springbootapi.dto.NewUser;

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

    public static User from(NewUser newUser) {
        Credential credential = new Credential("password", newUser.getPassword(), false);

        return new User(
                newUser.getUsername(),
                newUser.getEmail(),
                newUser.getFirstName(),
                newUser.getLastName(),
                true,
                new Credential[] {credential});
    }

}
