package com.vag.todolist.listeners;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vag.todolist.config.MessagingConfig;
import com.vag.todolist.dom.Todo;
import com.vag.todolist.services.TodoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.io.IOException;

@Component
public class TodoListener implements MessageListener{
    private Logger logger = LogManager.getLogger(TodoListener.class);
    /*@JmsListener(destination = "todo-save-queue", containerFactory = "todoMessagingFactory", concurrency = "1-5")
    public void receiveMessage(Message message) {
        try {
            logger.info("Received <" + message.getJMSMessageID() + ">" + message.getClass());
        } catch (JMSException e) {
            logger.error(e);
        }
    }*/

    @Autowired
    public MessagingConfig messagingConfig;

    @Autowired
    CachingConnectionFactory connectionFactory;

    ObjectMapper objectMapper;

    @Autowired
    TodoService todoService;

    public TodoListener(){
        objectMapper = new ObjectMapper();
    }

    @Bean(name = "receiveTodoSaveMessage")
    public String receiveTodoSaveMessage(){
        logger.info("Initializing Todo Listener with conn fac=" + connectionFactory + ", config=" + messagingConfig);
        try {
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Queue saveQueue = session.createQueue(messagingConfig.getTodoSaveQueueName());
            MessageConsumer consumer = session.createConsumer(saveQueue);
            consumer.setMessageListener(this);
            connection.start();
            logger.info("Listener initialized. Awaiting messages...");
        } catch (Exception e) {
            logger.error(e);
        }

        return "receiveTodoSaveMessage";
    }

    @Override
    public void onMessage(Message message) {
        String payload = null;
        try {
            logger.info("Message received - " + message.getJMSMessageID() + ", type=" + message.getClass());

            if (message instanceof TextMessage) {
                payload = ((TextMessage) message).getText();
                logger.info(payload);
            }
            //ACK the received message manually because of the set Session.CLIENT_ACKNOWLEDGE above
            message.acknowledge();

            Todo todo = objectMapper.readValue(payload, Todo.class);
            todoService.saveTodo(todo);
        } catch (Exception ex) {
            logger.error("Error processing incoming message=" + payload, ex);
        }
    }
}
