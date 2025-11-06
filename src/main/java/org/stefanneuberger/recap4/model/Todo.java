package org.stefanneuberger.recap4.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "todos")
public class Todo {

    @Id
    private String id;
    private String description;
    private Status status;
    private Instant createdAt;
    private Instant updatedAt;

    public Todo(final String description, final Status status) {
        this.description = description;
        this.status = status;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Todo(final String description, final Status status, final Instant updatedAt) {
        this.description = description;
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public enum Status {
        OPEN, IN_PROGRESS, DONE
    }
}
