package org.paulobichara.springbootapi.service;

import org.paulobichara.springbootapi.dto.UserDto;
import org.paulobichara.springbootapi.exception.UserAlreadyRegisteredException;
import org.paulobichara.springbootapi.model.User;
import org.paulobichara.springbootapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(UserDto newUser) {
        userRepository.findByEmail(newUser.getEmail()).ifPresent((found) -> {
            throw new UserAlreadyRegisteredException(found.getEmail());
        });

        User user = new User();
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setEmail(newUser.getEmail());
        user.setAddress(newUser.getAddress());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userRepository.save(user);
    }

    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }
}
