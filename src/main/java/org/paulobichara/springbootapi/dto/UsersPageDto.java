package org.paulobichara.springbootapi.dto;

import org.paulobichara.springbootapi.model.User;

import java.util.List;

public record UsersPageDto(List<User> content, int pageNumber, int pageSize, long totalElements) {
}
