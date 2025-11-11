package org.stefanneuberger.recap4.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.stefanneuberger.recap4.model.OpenAIMessage;
import org.stefanneuberger.recap4.model.OpenAIResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ChatGptServiceTest {

    private static final String BASE_URL = "https://mock-openai.test";
    private static final String API_KEY = "unit-test-key";

    private ChatGptService chatGptService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        // use the same builder as the one used by ChatGptService
        mockServer = MockRestServiceServer.bindTo(builder).ignoreExpectOrder(true).build();
        chatGptService = new ChatGptService(builder, BASE_URL, API_KEY, true);
    }

    @AfterEach
    void tearDown() {
        mockServer.verify();
    }

    @Test
    void sendChatCompletion_returnsResponseOnSuccess() {
        String responsePayload = """
                {
                  "choices": [
                    {
                      "message": {
                        "role": "assistant",
                        "content": "Corrected text"
                      }
                    }
                  ]
                }
                """;

        mockServer.expect(requestTo(BASE_URL + "/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY))
                .andRespond(withSuccess(responsePayload, MediaType.APPLICATION_JSON));

        OpenAIResponse response = chatGptService.sendChatCompletion(
                "gpt-4o-mini",
                List.of(new OpenAIMessage("user", "Fix this sentence"))
        );

        assertThat(response).isNotNull();
        assertThat(response.choices()).hasSize(1);
        assertThat(response.choices().get(0).message().content()).isEqualTo("Corrected text");
    }

    @Test
    void sendChatCompletion_returnsNullOnFailure() {
        mockServer.expect(requestTo(BASE_URL + "/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        OpenAIResponse response = chatGptService.sendChatCompletion(
                "gpt-4o-mini",
                List.of(new OpenAIMessage("user", "Fix this sentence"))
        );

        assertThat(response).isNull();
    }

    @Test
    void sendChatCompletion_skipsCallWhenDisabled() {
        chatGptService = new ChatGptService(RestClient.builder(), BASE_URL, API_KEY, false);

        OpenAIResponse response = chatGptService.sendChatCompletion(
                "gpt-4o-mini",
                List.of(new OpenAIMessage("user", "Fix this sentence"))
        );

        assertThat(response).isNull();
        mockServer.verify(); // ensures no unexpected HTTP interaction occurred
    }
}