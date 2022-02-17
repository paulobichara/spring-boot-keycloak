package org.paulobichara.springbootapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "app_user")
public class User {

  @Getter
  @Setter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Getter
  @Setter
  @Column(nullable = false)
  @NotBlank(message = "{validation.user.firstName.notBlank}")
  private String firstName;

  @Getter
  @Setter
  @Column(nullable = false)
  @NotBlank(message = "{validation.user.lastName.notBlank}")
  private String lastName;

  @Getter
  @Setter
  @Column(nullable = false)
  @Email(message = "{validation.user.email.invalid}")
  @NotBlank(message = "{validation.user.email.notBlank}")
  private String email;

  @Getter
  @Setter
  @Column(nullable = false)
  @NotBlank(message = "{validation.user.address.notBlank}")
  private String address;

  @Getter
  @Setter
  @Column(nullable = false)
  @JsonIgnore
  @Size(min = 8, message = "{validation.user.password.size}")
  @NotBlank(message = "{validation.user.password.notBlank}")
  private String password;

  @Getter
  @Setter
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  @NotNull(message = "{validation.user.role.notNull}")
  private Role role;

  @Getter
  @Setter
  @Column(nullable = false)
  @NotNull(message = "{validation.user.active.notNull}")
  private Boolean active;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    User user = (User) o;
    return id != null && Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
