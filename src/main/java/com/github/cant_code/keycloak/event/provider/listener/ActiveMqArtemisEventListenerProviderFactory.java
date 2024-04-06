package com.github.cant_code.keycloak.event.provider.listener;

import com.github.cant_code.keycloak.event.provider.config.MqConfig;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import static java.util.Objects.isNull;

public class ActiveMqArtemisEventListenerProviderFactory implements EventListenerProviderFactory {

    private static final Logger log = Logger.getLogger(ActiveMqArtemisEventListenerProviderFactory.class);
    private ActiveMQJMSConnectionFactory factory;
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private MqConfig config;

    @Override
    public EventListenerProvider create(final KeycloakSession keycloakSession) {
        checkAndCreateConnection();
        return new ActiveMqArtemisEventListenerProvider(keycloakSession, session, producer);
    }

    private synchronized void checkAndCreateConnection() {
        try {
            if (isNull(connection)) {
                connection = factory.createConnection();
            }
            if (isNull(session)) {
                session = connection.createSession();
            }
            if (isNull(producer)) {
                producer = session.createProducer(session.createQueue(config.getQueue()));
            }
        } catch (JMSException e) {
            log.error("keycloak-to-activemq Connection failed to activemq", e);
        }
    }

    @Override
    public void init(final Config.Scope scope) {
        config = MqConfig.create(scope);

        try {
            factory = new ActiveMQJMSConnectionFactory();
            factory.setBrokerURL(config.getBrokerUrl());
            factory.setUser(config.getUser());
            factory.setPassword(config.getPassword());
        } catch (Exception e) {
            log.error("keycloak-to-activemq Failed to create ActiveMQ connection factory", e);
        }

    }

    @Override
    public void postInit(final KeycloakSessionFactory keycloakSessionFactory) {
        // Not needed
    }

    @Override
    public void close() {
        try {
            session.close();
            connection.close();
            factory.close();
        } catch (JMSException e) {
            log.error("keycloak-to-activemq Failed to close ActiveMQ connection", e);
        }
    }

    @Override
    public String getId() {
        return "keycloak-to-activemq";
    }
}
