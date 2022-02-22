package org.paulobichara.springbootapi.config.property;

import javax.validation.constraints.NotBlank;

public record KeycloakClientProperties(@NotBlank String id, @NotBlank String secret) {}
