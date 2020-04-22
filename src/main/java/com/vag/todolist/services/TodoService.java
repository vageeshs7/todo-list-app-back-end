package com.vag.todolist.services;

import com.vag.todolist.dao.TodoDao;
import com.vag.todolist.dom.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TodoService {

    @Autowired
    private TodoDao todoDao;

    public void saveTodo(Todo todo){
        if(todo != null){
            todoDao.save(todo);
        }
    }
}
