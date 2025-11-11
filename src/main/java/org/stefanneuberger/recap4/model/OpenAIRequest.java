package org.stefanneuberger.recap4.model;

import java.util.List;

public record OpenAIRequest(String model, List<OpenAIMessage> messages) {
}
