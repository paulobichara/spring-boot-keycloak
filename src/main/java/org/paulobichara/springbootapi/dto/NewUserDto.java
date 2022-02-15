package org.paulobichara.springbootapi.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record NewUserDto(@NotBlank(message = "{validation.user.firstName.notBlank}") String firstName,
                         @NotBlank(message = "{validation.user.lastName.notBlank}") String lastName,
                         @Email(message = "{validation.user.email.invalid}") @NotBlank(message = "{validation.user.email.notBlank}") String email,
                         @NotBlank(message = "{validation.user.address.notBlank}") String address,
                         @Size(min = 8, message = "{validation.user.password.size}") @NotBlank(message = "{validation.user.password.notBlank}") String password) {

}
