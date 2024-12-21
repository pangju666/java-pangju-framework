package io.github.pangju666.framework.core.model.dto;

import jakarta.validation.Valid;

import java.util.List;

public record ListDTO<T>(@Valid List<T> list) {
}