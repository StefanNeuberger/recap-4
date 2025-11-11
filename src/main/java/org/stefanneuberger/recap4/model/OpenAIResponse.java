package org.stefanneuberger.recap4.model;

import java.util.List;


public record OpenAIResponse(List<OpenAIChoices> choices) {
}
