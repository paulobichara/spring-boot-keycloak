package org.paulobichara.springbootapi.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.paulobichara.springbootapi.config.property.KeycloakBaseProperties;
import org.paulobichara.springbootapi.config.property.KeycloakLocalProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({KeycloakLocalProperties.class, KeycloakBaseProperties.class})
public class KeycloakConfig {

  @Bean
  public KeycloakSpringBootConfigResolver KeycloakConfigResolver() {
    return new KeycloakSpringBootConfigResolver();
  }
}
