package org.paulobichara.springbootapi.service;

import org.paulobichara.springbootapi.dto.NewUserDto;
import org.paulobichara.springbootapi.exception.UserAlreadyRegisteredException;
import org.paulobichara.springbootapi.model.Role;
import org.paulobichara.springbootapi.model.User;
import org.paulobichara.springbootapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User createUser(NewUserDto newUser) {
    userRepository
        .findByEmail(newUser.email())
        .ifPresent(
            (found) -> {
              throw new UserAlreadyRegisteredException(found.getEmail());
            });

    User user = new User();
    user.setFirstName(newUser.firstName());
    user.setLastName(newUser.lastName());
    user.setEmail(newUser.email());
    user.setAddress(newUser.address());
    user.setPassword(passwordEncoder.encode(newUser.password()));
    user.setActive(true);
    user.setRole(Role.USER);
    return userRepository.save(user);
  }

  public Page<User> getUsers(Pageable pageable) {
    return userRepository.findAll(pageable);
  }
}
