package com.vag.todolist.dao;

import com.vag.todolist.dom.Todo;
import org.springframework.data.repository.CrudRepository;

public interface TodoDao extends CrudRepository<Todo, String> {
    public Todo findByUser(String user);
}
