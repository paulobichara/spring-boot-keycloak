package org.paulobichara.springbootapi.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "spring-boot-api.keycloak")
public record KeycloakLocalProperties(KeycloakClientProperties client, @NotBlank String roleId) {}
