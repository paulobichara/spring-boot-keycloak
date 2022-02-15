package org.paulobichara.springbootapi.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserDto {

    @NotBlank(message = "{validation.user.firstName.notBlank}")
    private String firstName;

    @NotBlank(message = "{validation.user.lastName.notBlank}")
    private String lastName;

    @Email(message = "{validation.user.email.invalid}")
    @NotBlank(message = "{validation.user.email.notBlank}")
    private String email;

    @NotBlank(message = "{validation.user.address.notBlank}")
    private String address;

    @Size(min = 8, message = "{validation.user.password.size}")
    @NotBlank(message = "{validation.user.password.notBlank}")
    private String password;

}
