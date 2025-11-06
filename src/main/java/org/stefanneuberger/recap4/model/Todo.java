package org.stefanneuberger.recap4.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "todos")
public record Todo(@Id String id, String description, Status status) {
    
    public enum Status {
        OPEN, IN_PROGRESS, DONE
    }
}
