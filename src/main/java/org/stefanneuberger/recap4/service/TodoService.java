package org.stefanneuberger.recap4.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.stefanneuberger.recap4.dto.CreateTodoDto;
import org.stefanneuberger.recap4.exception.ResourceNotFoundException;
import org.stefanneuberger.recap4.model.OpenAIMessage;
import org.stefanneuberger.recap4.model.OpenAIResponse;
import org.stefanneuberger.recap4.model.Todo;
import org.stefanneuberger.recap4.repository.TodoRepository;

import java.time.Instant;
import java.util.List;

@Service
public class TodoService {

    private static final String AUTOCORRECT_MODEL = "gpt-4o-mini";

    private final TodoRepository todoRepository;
    private final ChatGptService chatGptService;

    public TodoService(final TodoRepository todoRepository,
                       final ChatGptService chatGptService) {
        this.todoRepository = todoRepository;
        this.chatGptService = chatGptService;
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
        String correctedDescription = autoCorrect(dto.description());

        existingTodo.setUpdatedAt(Instant.now());
        existingTodo.setStatus(dto.status());
        existingTodo.setDescription(correctedDescription);

        return this.todoRepository.save(existingTodo);
    }

    public Todo createTodo(CreateTodoDto dto) {
        String correctedDescription = autoCorrect(dto.description());
        Todo todo = new Todo(correctedDescription, dto.status());
        return this.todoRepository.save(todo);
    }

    private String autoCorrect(String input) {
        List<OpenAIMessage> messages = List.of(new OpenAIMessage("user", buildAutocorrectPrompt(input)));
        OpenAIResponse response = chatGptService.sendChatCompletion(AUTOCORRECT_MODEL, messages);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            return input;
        }

        OpenAIMessage message = response.choices().get(0).message();
        if (message == null || !StringUtils.hasText(message.content())) {
            return input;
        }

        return message.content();
    }

    private String buildAutocorrectPrompt(String input) {
        return "Please correct the following sentence if necessary (Only return the corrected sentence): " + input;
    }
}
