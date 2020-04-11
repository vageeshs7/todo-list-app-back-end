package com.vag.todolist.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.Properties;

@Configuration
@PropertySource("classpath:messaging.properties")
@Component
public class MessagingConfig {
    @Value("${jms.destination.todo-save}")
    private String todoSaveQueueName;

    @Value("${java.naming.factory.initial}")
    private String initialContext;

    @Value("${jms.provider.url}")
    private String jmsProviderURL;

    @Value("${jms.username}")
    private String jmsUsername;

    @Value("${jms.password}")
    private String jmsPassword;

    public String getTodoSaveQueueName() {
        return todoSaveQueueName;
    }

    private Logger logger = LogManager.getLogger(MessagingConfig.class);

    @Bean
    public ConnectionFactory messagingConnectionFactory() throws NamingException, IOException {
        logger.info("Creating ConnectionFactory with" + ConnectionFactory.class);
        Properties properties = new Properties();
        properties.put("java.naming.factory.initial", initialContext);
        properties.put("connectionfactory.qpidConnectionFactory", jmsProviderURL);
        properties.put("jms.username", jmsUsername);
        properties.put("jms.password", jmsPassword);
        Context context = new InitialContext(properties);

        ConnectionFactory connectionFactory
                = (ConnectionFactory) context.lookup("qpidConnectionFactory");
        return connectionFactory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() throws IOException, NamingException {
        logger.info("Creating caching con fac " + CachingConnectionFactory.class);
        CachingConnectionFactory cachingConnectionFactory =
                new CachingConnectionFactory(messagingConnectionFactory());
        cachingConnectionFactory.setSessionCacheSize(10);

        return cachingConnectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate()  {
        logger.info("Creating jms template : " + JmsTemplate.class);
        JmsTemplate jmsTemplate =
                null;
        try {
            jmsTemplate = new JmsTemplate(cachingConnectionFactory());
            jmsTemplate.setReceiveTimeout(5000);
        } catch (Exception e) {
            logger.error(e);
        }
        //jmsTemplate.setDefaultDestination(orderDestination());


        return jmsTemplate;
    }

    @Bean
    public JmsListenerContainerFactory<?> todoMessagingFactory(DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all boot's default to this factory, including the message converter
        try {
            // anonymous class
            factory.setErrorHandler(
                    new ErrorHandler() {
                        @Override
                        public void handleError(Throwable t) {
                            logger.error("An error has occurred in the transaction");
                        }
                    });


            factory.setConnectionFactory(cachingConnectionFactory());
            configurer.configure(factory, cachingConnectionFactory());
        } catch (Exception e) {
            logger.error(e);
        }

        return factory;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
