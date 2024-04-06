package com.github.cant_code.keycloak.event.provider.listener;

import com.github.cant_code.keycloak.event.provider.utils.MqUtils;
import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerTransaction;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import static java.util.Objects.nonNull;

public class ActiveMqArtemisEventListenerProvider implements EventListenerProvider {

    private static final Logger log = Logger.getLogger(ActiveMqArtemisEventListenerProvider.class);

    private final KeycloakSession keycloakSession;
    private final Session session;
    private final MessageProducer producer;
    private final EventListenerTransaction transaction = new EventListenerTransaction(this::publishAdminEvent, this::publishEvent);

    public ActiveMqArtemisEventListenerProvider(final KeycloakSession keycloakSession,
                                                final Session session,
                                                final MessageProducer producer) {
        this.keycloakSession = keycloakSession;
        this.session = session;
        this.producer = producer;
        keycloakSession.getTransactionManager().enlistAfterCompletion(transaction);
    }

    @Override
    public void onEvent(final Event event) {
        transaction.addEvent(event);
    }

    @Override
    public void onEvent(final AdminEvent adminEvent, final boolean includeRepresentation) {
        transaction.addAdminEvent(adminEvent, includeRepresentation);
    }

    @Override
    public void close() {
        // Nothing to close
    }

    private void publishEvent(final Event event) {
        publishEvents(event);
    }

    private void publishAdminEvent(final AdminEvent adminEvent, final boolean includeRepresentation) {
        publishEvents(adminEvent);
    }

    private void publishEvents(final Object event) {
        final String messageString = MqUtils.writeAsJson(event);
        final TextMessage textMessage = MqUtils.createTextMessage(messageString, session);
        this.publishNotification(textMessage);
    }

    private void publishNotification(final TextMessage message) {
        try {
            if (nonNull(message)) {
                producer.send(message);
            }
        } catch (JMSException e) {
            log.error("keycloak-to-activemq Failed to send message", e);
        }
    }
}
