package org.stefanneuberger.recap4.dto;

import org.stefanneuberger.recap4.model.Todo;

public record CreateTodoDto(String description, Todo.Status status) {
}
