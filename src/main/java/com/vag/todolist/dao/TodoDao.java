package com.vag.todolist.dao;

import com.vag.todolist.dom.Todo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TodoDao extends CrudRepository<Todo, Integer> {
    public List<Todo> findByUser(String user);
}
