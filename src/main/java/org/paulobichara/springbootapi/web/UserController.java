package org.paulobichara.springbootapi.web;

import org.paulobichara.springbootapi.dto.UserDto;
import org.paulobichara.springbootapi.model.User;
import org.paulobichara.springbootapi.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    Iterable<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    User createUser(@RequestBody UserDto newUser) {
        return userService.createUser(newUser);
    }

}
