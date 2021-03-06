package org.paulobichara.springbootapi.web;

import org.paulobichara.springbootapi.dto.NewUserRequest;
import org.paulobichara.springbootapi.dto.keycloak.User;
import org.paulobichara.springbootapi.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  void createUser(@Valid @RequestBody NewUserRequest newUser) {
    userService.createUser(newUser);
  }

  @GetMapping(path = "/me")
  public User me(Principal principal) {
    return User.from(principal);
  }
}
