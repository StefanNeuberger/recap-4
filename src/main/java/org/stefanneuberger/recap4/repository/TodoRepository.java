package org.stefanneuberger.recap4.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.stefanneuberger.recap4.model.Todo;

public interface TodoRepository extends MongoRepository<Todo, String> {
}
