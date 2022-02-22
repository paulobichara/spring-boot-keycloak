package org.paulobichara.springbootapi.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "keycloak")
public record KeycloakBaseProperties(@NotBlank String authServerUrl, @NotBlank String realm) {}
