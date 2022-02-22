package org.paulobichara.springbootapi.dto.keycloak;

public record Credential(String type,
                         String value,
                         Boolean temporary) {
}
