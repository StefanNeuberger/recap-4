package org.stefanneuberger.recap4.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stefanneuberger.recap4.dto.CreateTodoDto;
import org.stefanneuberger.recap4.model.Todo;
import org.stefanneuberger.recap4.service.TodoService;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoService todoService;

    public TodoController(final TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("")
    @Operation(
            summary = "Get todos",
            description = "Returns a list of all todos"
    )
    public ResponseEntity<List<Todo>> getTodos() {
        return ResponseEntity.ok(todoService.getTodos());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get todo",
            description = "Returns specific todo"
    )
    public ResponseEntity<Todo> getTodo(@PathVariable String id) {
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    @PostMapping("")
    @Operation(
            summary = "Create todo",
            description = "Creates a new todo"
    )
    public ResponseEntity<Todo> createTodo(
            @RequestBody(
                    description = "Todo creation data", required = true,
                    content = @Content(schema = @Schema(implementation = CreateTodoDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateTodoDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(todoService.createTodo(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete todo",
            description = "Deletes todo by id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Todo deleted"),
            @ApiResponse(responseCode = "404", description = "Todo not found",
                    content = @Content(schema = @Schema(implementation = Error.class))),
    })
    public ResponseEntity<Void> deleteTodo(
            @Parameter(description = "Image ID", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update todo",
            description = "Update todo by id"
    )
    public ResponseEntity<Todo> updateTodo(
            @Parameter(description = "Todo ID", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id,
            @RequestBody(
                    description = "Todo creation data", required = true,
                    content = @Content(schema = @Schema(implementation = CreateTodoDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateTodoDto dto) {
        Todo todo = todoService.updateTodo(id, dto);
        return ResponseEntity.ok(todo);
    }


}
