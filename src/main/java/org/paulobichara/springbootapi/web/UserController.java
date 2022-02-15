package org.paulobichara.springbootapi.web;

import org.paulobichara.springbootapi.dto.NewUserDto;
import org.paulobichara.springbootapi.dto.UsersPageDto;
import org.paulobichara.springbootapi.model.User;
import org.paulobichara.springbootapi.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  UsersPageDto getUsers(Pageable pageable) {
    Page<User> page = userService.getUsers(pageable);

    return new UsersPageDto(
            page.getContent(),
            page.getPageable().getPageNumber(),
            page.getPageable().getPageSize(),
            page.getTotalElements());
  }

  @PostMapping
  User createUser(@RequestBody NewUserDto newUser) {
    return userService.createUser(newUser);
  }
}
