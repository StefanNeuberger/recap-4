package org.stefanneuberger.recap4.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.stefanneuberger.recap4.dto.CreateTodoDto;
import org.stefanneuberger.recap4.exception.ResourceNotFoundException;
import org.stefanneuberger.recap4.model.OpenAIMessage;
import org.stefanneuberger.recap4.model.OpenAIRequest;
import org.stefanneuberger.recap4.model.OpenAIResponse;
import org.stefanneuberger.recap4.model.Todo;
import org.stefanneuberger.recap4.repository.TodoRepository;

import java.time.Instant;
import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final RestClient.Builder restClientBuilder;
    private final String openAiBaseUrl;
    private final String openAiApiKey;
    private final RestClient openAiRestClient;

    public TodoService(final TodoRepository todoRepository,
                       final RestClient.Builder restClientBuilder,
                       @Value("${openai.base-url}") final String openAiBaseUrl,
                       @Value("${openai.api-key}") final String openAiApiKey) {
        this.todoRepository = todoRepository;
        this.restClientBuilder = restClientBuilder;
        this.openAiBaseUrl = openAiBaseUrl;
        this.openAiApiKey = openAiApiKey;
        this.openAiRestClient = createOpenAiRestClient();
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
        OpenAIResponse response = autoCorrectUserInput(dto.description());

        existingTodo.setUpdatedAt(Instant.now());
        existingTodo.setStatus(dto.status());
        existingTodo.setDescription(response.choices().get(0).message().content());


        return this.todoRepository.save(existingTodo);
    }

    public Todo createTodo(CreateTodoDto dto) {
        OpenAIResponse response = autoCorrectUserInput(dto.description());
        Todo todo = new Todo(response.choices().get(0).message().content(), dto.status());
        return this.todoRepository.save(todo);
    }

    private RestClient createOpenAiRestClient() {
        return this.restClientBuilder
                .baseUrl(this.openAiBaseUrl)
                .defaultHeader("Authorization", "Bearer " + this.openAiApiKey)
                .build();
    }

    private OpenAIResponse autoCorrectUserInput(String input) {
        String prompt = "Please correct the following sentence if necessary (Only return the corrected sentence): " + input;
        OpenAIRequest request = new OpenAIRequest("gpt-4o-mini", List.of(new OpenAIMessage("user", prompt)));
        OpenAIResponse response = this.openAiRestClient.post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .body(OpenAIResponse.class);
        System.out.println(response);
        return response;
    }
}
