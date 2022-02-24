package org.paulobichara.springbootapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.paulobichara.springbootapi.config.property.KeycloakBaseProperties;
import org.paulobichara.springbootapi.config.property.KeycloakClientProperties;
import org.paulobichara.springbootapi.config.property.KeycloakLocalProperties;
import org.paulobichara.springbootapi.dto.NewUserRequest;
import org.paulobichara.springbootapi.dto.keycloak.AccessToken;
import org.paulobichara.springbootapi.dto.keycloak.User;
import org.paulobichara.springbootapi.exception.keycloak.AddRoleMappingException;
import org.paulobichara.springbootapi.exception.keycloak.GetTokenException;
import org.paulobichara.springbootapi.exception.keycloak.UserAlreadyRegisteredException;
import org.paulobichara.springbootapi.exception.keycloak.UserRegistrationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("A user service")
public class UserServiceTest {
  private static final AccessToken TOKEN =
      new AccessToken("token", 123L, "refresh", 456L, "Bearer", 10, "state", "scope");

  private static final String USER_ID = "user-id";

  private static MockWebServer MOCK_KEYCLOAK;
  private static String BASE_URL;
  private static KeycloakBaseProperties BASE_PROPS;
  private static KeycloakLocalProperties LOCAL_PROPS;

  @BeforeAll
  static void setUp() throws IOException {
    MOCK_KEYCLOAK = new MockWebServer();
    MOCK_KEYCLOAK.start();

    BASE_URL = String.format("http://localhost:%s", MOCK_KEYCLOAK.getPort());
    BASE_PROPS = new KeycloakBaseProperties(BASE_URL, "realm");

    var client = new KeycloakClientProperties("client-id", "client-secret");
    LOCAL_PROPS = new KeycloakLocalProperties(client, "role-id");
  }

  @AfterAll
  static void tearDown() throws IOException {
    MOCK_KEYCLOAK.shutdown();
  }

  @Nested
  @DisplayName("When getting the access token")
  class WhenGettingTokenTestCase {

    @Nested
    @DisplayName("And the request to the Keycloak API succeeds")
    class AndRequestSucceedsTestCase {
      @BeforeEach
      void setup() throws JsonProcessingException {
        MOCK_KEYCLOAK.enqueue(
            new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(TOKEN))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
      }

      @Test
      @DisplayName("Must return the access token")
      void mustReturnAccessToken() throws InterruptedException {
        assertEquals(TOKEN, new UserService(BASE_PROPS, LOCAL_PROPS).getAccessToken());
        MOCK_KEYCLOAK.takeRequest();
      }

      @Test
      @DisplayName("Must send the correct token request")
      void mustSendCorrectTokenRequest() throws InterruptedException {
        new UserService(BASE_PROPS, LOCAL_PROPS).getAccessToken();

        RecordedRequest request = MOCK_KEYCLOAK.takeRequest();
        assertEquals(HttpMethod.POST.name(), request.getMethod());
        assertEquals(
            String.format("/realms/%s/protocol/openid-connect/token", BASE_PROPS.realm()),
            request.getPath());

        assertEquals(
            String.format(
                "client_id=%s&client_secret=%s&grant_type=client_credentials",
                LOCAL_PROPS.client().id(), LOCAL_PROPS.client().secret()),
            request.getBody().readUtf8());

        assertEquals(
            String.format(
                "%s;charset=%s",
                MediaType.APPLICATION_FORM_URLENCODED_VALUE, StandardCharsets.UTF_8.name()),
            request.getHeader(HttpHeaders.CONTENT_TYPE));
        assertEquals(MediaType.APPLICATION_JSON_VALUE, request.getHeader(HttpHeaders.ACCEPT));
      }
    }

    @Nested
    @DisplayName("And the request to the Keycloak API fails")
    class AndRequestFailsTestCase {
      @BeforeEach
      void setup() {
        MOCK_KEYCLOAK.enqueue(
            new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
      }

      @Test
      @DisplayName("Must throw GetTokenException")
      void mustThrowGetTokenException() throws InterruptedException {
        assertThrows(
            GetTokenException.class,
            () -> new UserService(BASE_PROPS, LOCAL_PROPS).getAccessToken());

        MOCK_KEYCLOAK.takeRequest();
      }
    }
  }

  @Nested
  @DisplayName("When adding a user to a role")
  class WhenAddingUserToRoleTestCase {
    @Nested
    @DisplayName("And the request to the Keycloak API fails")
    class AndTheRequestFailsTestCase {
      @BeforeEach
      void setup() {
        MOCK_KEYCLOAK.enqueue(
            new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
      }

      @Test
      @DisplayName("Must throw AddRoleMappingException")
      void mustThrowAddRoleMappingException() throws InterruptedException {
        assertThrows(
            AddRoleMappingException.class,
            () -> new UserService(BASE_PROPS, LOCAL_PROPS).addUserToRole(USER_ID, TOKEN));

        MOCK_KEYCLOAK.takeRequest();
      }
    }

    @Nested
    @DisplayName("And the request to the Keycloak API succeeds")
    class AndRequestSucceedsTestCase {
      @BeforeEach
      void setup() {
        MOCK_KEYCLOAK.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value()));
      }

      @Test
      @DisplayName("Must send the correct role mapping request")
      void mustSendCorrectRoleMappingRequest() throws InterruptedException {
        new UserService(BASE_PROPS, LOCAL_PROPS).addUserToRole(USER_ID, TOKEN);

        RecordedRequest request = MOCK_KEYCLOAK.takeRequest();
        assertEquals(HttpMethod.POST.name(), request.getMethod());
        assertEquals(
            String.format(
                "/admin/realms/%s/users/%s/role-mappings/realm", BASE_PROPS.realm(), USER_ID),
            request.getPath());

        assertEquals(MediaType.APPLICATION_JSON_VALUE, request.getHeader(HttpHeaders.CONTENT_TYPE));
        assertEquals(MediaType.APPLICATION_JSON_VALUE, request.getHeader(HttpHeaders.ACCEPT));
        assertEquals(
            String.format("Bearer %s", TOKEN.accessToken()),
            request.getHeader(HttpHeaders.AUTHORIZATION));
        assertEquals(
            String.format("[{\"id\":\"%s\",\"name\":\"USER\"}]", LOCAL_PROPS.roleId()),
            request.getBody().readUtf8());
      }
    }
  }

  @Nested
  @DisplayName("When creating a user")
  class WhenCreatingUserTestCase {

    private static final NewUserRequest NEW_USER =
        new NewUserRequest("Name", "Surname", "user@domain", "username", "password");

    UserService userService;

    @BeforeEach
    void setup() {
      userService = Mockito.spy(new UserService(BASE_PROPS, LOCAL_PROPS));
      Mockito.doReturn(TOKEN).when(userService).getAccessToken();
      Mockito.doNothing().when(userService).addUserToRole(USER_ID, TOKEN);
    }

    @Nested
    @DisplayName("And the request to the Keycloak API fails due to conflict")
    class AndTheRequestFailsConflictTestCase {
      @BeforeEach
      void setup() {
        MOCK_KEYCLOAK.enqueue(new MockResponse().setResponseCode(HttpStatus.CONFLICT.value()));
      }

      @Test
      @DisplayName("Must throw UserAlreadyRegisteredException")
      void mustThrowUserAlreadyRegisteredException() throws InterruptedException {
        assertThrows(UserAlreadyRegisteredException.class, () -> userService.createUser(NEW_USER));
        MOCK_KEYCLOAK.takeRequest();
      }
    }

    @Nested
    @DisplayName("And the request to the Keycloak API fails due to other reason")
    class AndTheRequestFailsOtherTestCase {
      @BeforeEach
      void setup() {
        MOCK_KEYCLOAK.enqueue(
            new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
      }

      @Test
      @DisplayName("Must throw UserRegistrationException")
      void mustThrowUserRegistrationException() throws InterruptedException {
        assertThrows(UserRegistrationException.class, () -> userService.createUser(NEW_USER));
        MOCK_KEYCLOAK.takeRequest();
      }
    }

    @Nested
    @DisplayName("And the request to the Keycloak API succeeds")
    class AndRequestSucceedsTestCase {
      @BeforeEach
      void setup() {
        var location =
            String.format("%s/admin/realms/%s/users/%s", BASE_URL, BASE_PROPS.realm(), USER_ID);

        MOCK_KEYCLOAK.enqueue(
            new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .addHeader("location", location));
      }

      @Test
      @DisplayName("Must send the correct create request")
      void mustSendCorrectCreateRequest() throws InterruptedException, JsonProcessingException {
        userService.createUser(NEW_USER);

        var request = MOCK_KEYCLOAK.takeRequest();
        assertEquals(HttpMethod.POST.name(), request.getMethod());
        assertEquals(
            String.format("/admin/realms/%s/users", BASE_PROPS.realm()), request.getPath());

        assertEquals(MediaType.APPLICATION_JSON_VALUE, request.getHeader(HttpHeaders.CONTENT_TYPE));
        assertEquals(MediaType.APPLICATION_JSON_VALUE, request.getHeader(HttpHeaders.ACCEPT));
        assertEquals(
            String.format("Bearer %s", TOKEN.accessToken()),
            request.getHeader(HttpHeaders.AUTHORIZATION));

        assertEquals(
            new ObjectMapper().writeValueAsString(User.from(NEW_USER)),
            request.getBody().readUtf8());
      }

      @Test
      @DisplayName("Must send the correct create request")
      void mustAddUserToRole() throws InterruptedException {
        userService.createUser(NEW_USER);
        Mockito.verify(userService).addUserToRole(USER_ID, TOKEN);
        MOCK_KEYCLOAK.takeRequest();
      }
    }
  }
}
