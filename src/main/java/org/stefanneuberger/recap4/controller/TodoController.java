package org.stefanneuberger.recap4.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stefanneuberger.recap4.model.Todo;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
public class TodoController {

    @GetMapping("")
    @Operation(
            summary = "Get todos",
            description = "Returns a list of all todos"
    )
    public ResponseEntity<List<Todo>> getTodos() {
        return null;
    }

    @PostMapping("")
    @Operation(
            summary = "Create todo",
            description = "Creates a new todo"
    )
    public ResponseEntity<Todo> createTodo(@RequestBody final Todo todo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(todo);
    }
}
