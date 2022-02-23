package org.paulobichara.springbootapi.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.mockito.Mockito;
import org.paulobichara.springbootapi.dto.NewUserRequest;
import org.paulobichara.springbootapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("A user controller")
public class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserService userService;

  public static String asJsonString(final Object obj) throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(obj);
  }

  @Test
  @DisplayName("Must retrieve current user info")
  void retrievesCurrentUserInfo() throws Exception {
    KeycloakAuthenticationToken mockPrincipal = Mockito.mock(KeycloakAuthenticationToken.class);
    OidcKeycloakAccount mockAccount = Mockito.mock(OidcKeycloakAccount.class);
    KeycloakSecurityContext mockContext = Mockito.mock(KeycloakSecurityContext.class);
    AccessToken mockToken = Mockito.mock(AccessToken.class);

    Mockito.when(mockPrincipal.getAccount()).thenReturn(mockAccount);
    Mockito.when(mockAccount.getKeycloakSecurityContext()).thenReturn(mockContext);
    Mockito.when(mockContext.getToken()).thenReturn(mockToken);
    Mockito.when(mockToken.getPreferredUsername()).thenReturn("username");
    Mockito.when(mockToken.getEmail()).thenReturn("user@domain");
    Mockito.when(mockToken.getGivenName()).thenReturn("Name");
    Mockito.when(mockToken.getFamilyName()).thenReturn("Surname");

    mockMvc
        .perform(get("/users/me").principal(mockPrincipal).accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.username").value("username"))
        .andExpect(jsonPath("$.email").value("user@domain"))
        .andExpect(jsonPath("$.firstName").value("Name"))
        .andExpect(jsonPath("$.lastName").value("Surname"));
  }

  @Nested
  @DisplayName("When creating a new user")
  class WhenCreatingUserTestCase {

    @Nested
    @DisplayName("And the user data is valid")
    class AndTheUserDataIsValidTestCase {
      private static final NewUserRequest DTO =
          new NewUserRequest("Name", "Surname", "user@domain.com", "username", "password");

      @BeforeEach
      void setup() throws Exception {
        mockMvc
            .perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(asJsonString(DTO)))
            .andDo(print())
            .andExpect(status().is2xxSuccessful());
      }

      @Test
      @DisplayName("Must create the user through user service")
      void createsUserThroughService() {
        verify(userService, times(1)).createUser(DTO);
      }
    }

    @Nested
    @DisplayName("And the user data is invalid")
    class AndTheUserDataIsInvalidTestCase {
      @Test
      @DisplayName("Must return a bad request response")
      void returnsBadRequestResponse() throws Exception {
        mockMvc
            .perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(asJsonString(new NewUserRequest(null, null, null, null, null))))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Request validation failed."))
            .andExpect(jsonPath("$.validationErrors").isArray())
            .andExpect(jsonPath("$.validationErrors", IsCollectionWithSize.hasSize(5)))
            .andExpect(
                jsonPath("$.validationErrors[*].field")
                    .value(
                        Matchers.containsInAnyOrder(
                            "firstName", "lastName", "username", "email", "password")));
      }
    }
  }
}
