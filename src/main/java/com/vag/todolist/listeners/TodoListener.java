package com.vag.todolist.listeners;

import com.vag.todolist.config.MessagingConfig;
import com.vag.todolist.dom.Todo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.stereotype.Component;

import javax.jms.*;
import javax.naming.NamingException;
import java.io.IOException;

@Component
public class TodoListener {
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

    @Bean(name = "receiveMessage")
    public String receiveMessage(){
        logger.info("Initializing Todo Listener with conn fac=" + connectionFactory + ", config=" + messagingConfig);
        try {
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession();
            Queue saveQueue = session.createQueue(messagingConfig.getTodoSaveQueueName());
            MessageConsumer consumer = session.createConsumer(saveQueue);

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        logger.info("Message received - " + message.getJMSMessageID());
                        //ACK the received message manually because of the set Session.CLIENT_ACKNOWLEDGE above
                        message.acknowledge();

                    } catch (JMSException ex) {
                        logger.error("Error processing incoming message.", ex);
                    }
                }
            });

            // Start receiving messages
            connection.start();
            logger.info("Awaiting messages...");
        } catch (Exception e) {
            logger.error(e);
        }

        return "receiveMessage";
    }
}
