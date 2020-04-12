package com.vag.todolist.listeners;

import com.vag.todolist.config.MessagingConfig;
import com.vag.todolist.dom.Todo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;

@Component
public class TodoListener {
    private Logger logger = LogManager.getLogger(MessagingConfig.class);
    @JmsListener(destination = "todo-save-queue", containerFactory = "todoMessagingFactory", concurrency = "1-5")
    public void receiveMessage(Message message) {
        try {
            logger.info("Received <" + message.getJMSMessageID() + ">" + message.getClass());
        } catch (JMSException e) {
            logger.error(e);
        }
    }
}
