package org.stefanneuberger.recap4.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    public RestClient restClient(final RestClient.Builder builder) {
        return builder.build();
    }
}
