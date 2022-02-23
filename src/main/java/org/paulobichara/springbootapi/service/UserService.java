package org.paulobichara.springbootapi.service;

import org.paulobichara.springbootapi.config.property.KeycloakBaseProperties;
import org.paulobichara.springbootapi.config.property.KeycloakLocalProperties;
import org.paulobichara.springbootapi.dto.NewUserRequest;
import org.paulobichara.springbootapi.dto.keycloak.AccessToken;
import org.paulobichara.springbootapi.dto.keycloak.Role;
import org.paulobichara.springbootapi.dto.keycloak.RoleMapping;
import org.paulobichara.springbootapi.dto.keycloak.User;
import org.paulobichara.springbootapi.exception.keycloak.AddRoleMappingException;
import org.paulobichara.springbootapi.exception.keycloak.GetTokenException;
import org.paulobichara.springbootapi.exception.keycloak.UserAlreadyRegisteredException;
import org.paulobichara.springbootapi.exception.keycloak.UserRegistrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

  private final KeycloakBaseProperties baseProps;
  private final KeycloakLocalProperties localProps;

  public UserService(KeycloakBaseProperties baseProps, KeycloakLocalProperties localProps) {
    this.baseProps = baseProps;
    this.localProps = localProps;
  }

  protected AccessToken getAccessToken() {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("client_id", localProps.client().id());
    formData.add("client_secret", localProps.client().secret());
    formData.add("grant_type", "client_credentials");

    return WebClient.create(baseProps.authServerUrl() + "/realms/" + baseProps.realm())
        .post()
        .uri("/protocol/openid-connect/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(formData))
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(
            HttpStatus::isError,
            response -> {
              response
                  .bodyToMono(String.class)
                  .subscribe(body -> LOGGER.error("Failed to get access token! Response: " + body));
              return Mono.error(new GetTokenException());
            })
        .bodyToMono(AccessToken.class)
        .block();
  }

  protected void addUserToRole(String userId, AccessToken token) {
    WebClient.create(baseProps.authServerUrl() + "/admin/realms/" + baseProps.realm())
        .post()
        .uri("/users/" + userId + "/role-mappings/realm")
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            BodyInserters.fromValue(
                new RoleMapping[] {new RoleMapping(localProps.roleId(), Role.USER.name())}))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(
            HttpStatus::isError,
            response -> {
              response
                  .bodyToMono(String.class)
                  .subscribe(body -> LOGGER.error("Failed to add user to role! Response: " + body));
              return Mono.error(new AddRoleMappingException(userId));
            })
        .bodyToMono(Void.class)
        .block();
  }

  public void createUser(NewUserRequest newUser) {
    User user = User.from(newUser);

    String baseUrl = baseProps.authServerUrl() + "/admin/realms/" + baseProps.realm() + "/users";

    AccessToken token = getAccessToken();

    String userId =
        WebClient.create(baseUrl)
            .post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(user))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono(
                response -> {
                  if (HttpStatus.CONFLICT == response.statusCode()) {
                    return Mono.error(new UserAlreadyRegisteredException(newUser.username()));
                  } else if (response.statusCode().isError()) {
                    return Mono.error(new UserRegistrationException(newUser.username()));
                  } else {
                    Pattern pattern = Pattern.compile("^" + baseUrl + "/((\\w|-)*)$");
                    Matcher matcher = pattern.matcher(response.headers().header("location").get(0));

                    return matcher.find()
                        ? Mono.just(matcher.group(1))
                        : Mono.error(new UserRegistrationException(newUser.username()));
                  }
                })
            .block();

    addUserToRole(userId, token);
  }
}
