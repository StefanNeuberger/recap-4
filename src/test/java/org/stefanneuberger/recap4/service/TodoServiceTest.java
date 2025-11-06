package org.stefanneuberger.recap4.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.stefanneuberger.recap4.dto.CreateTodoDto;
import org.stefanneuberger.recap4.exception.ResourceNotFoundException;
import org.stefanneuberger.recap4.model.Todo;
import org.stefanneuberger.recap4.repository.TodoRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @Test
    void createTodo_shouldReturnTodo_whenCalledWithValidDto() {
        // GIVEN
        CreateTodoDto dto = new CreateTodoDto("test", Todo.Status.OPEN);
        Todo todo = new Todo(dto.description(), dto.status());
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        // WHEN
        Todo actualTodo = todoService.createTodo(dto);

        // THEN
        assertEquals(todo.getDescription(), actualTodo.getDescription());
        assertEquals(todo.getStatus(), actualTodo.getStatus());
    }

    @Test
    void getTodos_shouldReturnListOfTodos_whenCalled() {
        // GIVEN
        Todo todo1 = new Todo("test1", Todo.Status.OPEN);
        Todo todo2 = new Todo("test2", Todo.Status.IN_PROGRESS);
        List<Todo> expectedTodos = Arrays.asList(todo1, todo2);
        when(todoRepository.findAll()).thenReturn(expectedTodos);

        // WHEN
        List<Todo> actualTodos = todoService.getTodos();

        // THEN
        assertEquals(expectedTodos.size(), actualTodos.size());
        assertEquals(expectedTodos, actualTodos);
        verify(todoRepository).findAll();
    }


    @Test
    void getTodoById_shouldReturnTodo_whenTodoExists() {
        // GIVEN
        String todoId = "123";
        Todo expectedTodo = new Todo("test", Todo.Status.OPEN);
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(expectedTodo));

        // WHEN
        Todo actualTodo = todoService.getTodoById(todoId);

        // THEN
        assertEquals(expectedTodo, actualTodo);
        verify(todoRepository).findById(todoId);
    }

    @Test
    void getTodoById_shouldThrowResourceNotFoundException_whenTodoDoesNotExist() {
        // GIVEN
        String todoId = "999";
        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        // WHEN

        // THEN
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            todoService.getTodoById(todoId);
        });

        assertEquals("Todo with id " + todoId + " not found", exception.getMessage());
        verify(todoRepository).findById(todoId);
    }

    @Test
    void deleteTodo_shouldDeleteTodo_whenTodoExists() {
        // GIVEN
        String todoId = "123";
        Todo todo = new Todo("test", Todo.Status.OPEN);
        todo.setId(todoId);
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        doNothing().when(todoRepository).deleteById(todoId);

        // WHEN
        todoService.deleteTodo(todoId);

        // THEN
        verify(todoRepository).findById(todoId);
        verify(todoRepository).deleteById(todoId);
    }

    @Test
    void deleteTodo_shouldThrowResourceNotFoundException_whenTodoDoesNotExist() {
        // GIVEN
        String todoId = "999";
        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        // WHEN & THEN
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            todoService.deleteTodo(todoId);
        });

        assertEquals("Todo with id " + todoId + " not found", exception.getMessage());
        verify(todoRepository).findById(todoId);
        verify(todoRepository, never()).deleteById(anyString());
    }


}