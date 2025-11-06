package org.stefanneuberger.recap4.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stefanneuberger.recap4.dto.CreateTodoDto;
import org.stefanneuberger.recap4.exception.ResourceNotFoundException;
import org.stefanneuberger.recap4.model.Todo;
import org.stefanneuberger.recap4.repository.TodoRepository;

import java.time.Instant;
import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(final TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getTodos() {
        return this.todoRepository.findAll();
    }

    public Todo getTodoById(String id) {
        return this.todoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Todo with id " + id + " not found"));
    }

    @Transactional
    public void deleteTodo(String id) {
        Todo existingTodo = getTodoById(id);
        this.todoRepository.deleteById(existingTodo.getId());
    }

    @Transactional
    public Todo updateTodo(String id, CreateTodoDto dto) {
        Todo existingTodo = getTodoById(id);
        if (existingTodo == null) {
            return null;
        }
        existingTodo.setUpdatedAt(Instant.now());
        existingTodo.setStatus(dto.status());
        existingTodo.setDescription(dto.description());

        return this.todoRepository.save(existingTodo);
    }

    public Todo createTodo(CreateTodoDto dto) {
        Todo todo = new Todo(dto.description(), dto.status());
        return this.todoRepository.save(todo);
    }
}
