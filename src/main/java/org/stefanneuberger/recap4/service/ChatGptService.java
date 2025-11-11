package org.stefanneuberger.recap4.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.stefanneuberger.recap4.model.OpenAIMessage;
import org.stefanneuberger.recap4.model.OpenAIRequest;
import org.stefanneuberger.recap4.model.OpenAIResponse;

import java.util.List;

@Service
public class ChatGptService {

    private final RestClient.Builder restClientBuilder;
    private final String baseUrl;
    private final String apiKey;
    private final boolean enabled;
    private RestClient restClient;

    public ChatGptService(final RestClient.Builder restClientBuilder,
                          @Value("${openai.base-url:https://api.openai.com/v1}") final String baseUrl,
                          @Value("${openai.api-key:}") final String apiKey,
                          @Value("${openai.autocorrect-enabled:false}") final boolean enabled) {
        this.restClientBuilder = restClientBuilder;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.enabled = enabled;
    }

    public OpenAIResponse sendChatCompletion(String model, List<OpenAIMessage> messages) {
        if (!enabled) {
            return null;
        }
        try {
            OpenAIRequest request = new OpenAIRequest(model, messages);
            return getRestClient().post()
                    .uri("/chat/completions")
                    .body(request)
                    .retrieve()
                    .body(OpenAIResponse.class);
        } catch (Exception ex) {
            return null;
        }
    }


    private synchronized RestClient getRestClient() {
        if (restClient == null && enabled && restClientBuilder != null) {
            restClient = restClientBuilder
                    .baseUrl(baseUrl)
                    .defaultHeader("Authorization", "Bearer " + apiKey)
                    .build();
        }
        return restClient;
    }
}

