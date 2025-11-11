package org.stefanneuberger.recap4.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.stefanneuberger.recap4.model.Todo;
import org.stefanneuberger.recap4.repository.TodoRepository;
import org.stefanneuberger.recap4.support.MongoTestContainerConfig;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest extends MongoTestContainerConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    void getTodos_shouldReturnListOfOneTodo_whenCalled() throws Exception {
        // GIVEN
        Todo todo = new Todo("Test", Todo.Status.OPEN);
        todoRepository.save(todo);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/todo"))
                // THEN
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value(Todo.Status.OPEN.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].updatedAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdAt").exists());
    }

}