package com.vag.todolist.dao;

import com.vag.todolist.dom.Todo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TodoDao extends MongoRepository<Todo, String> {
    public Todo findByUser(String user);
}
