package com.vag.todolist.listeners;

import com.vag.todolist.config.MessagingConfig;
import com.vag.todolist.dom.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;

public class TodoListener {

    @Autowired
    private MessagingConfig messagingConfig;

    @JmsListener(destination = "todo-save-queue", containerFactory = "todoMessagingFactory")
    public void receiveMessage(Todo todo) {
        System.out.println("Received <" + todo + ">");
    }
}
