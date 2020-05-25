package com.vag.todolist.controllers;

import com.vag.todolist.dom.Todo;
import com.vag.todolist.services.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TodoRestController {

    @Autowired
    TodoService todoService;

    @GetMapping(value = "/backend/{userid}/todos")
    public List<Todo> getTodoListForUser(@PathVariable  String userid){
        return todoService.getTodosForUser(userid);
    }
}
