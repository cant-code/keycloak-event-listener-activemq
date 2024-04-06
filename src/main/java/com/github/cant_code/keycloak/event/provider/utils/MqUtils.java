package com.github.cant_code.keycloak.event.provider.utils;

import org.jboss.logging.Logger;
import org.keycloak.util.JsonSerialization;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

public class MqUtils {

    private static final Logger log = Logger.getLogger(MqUtils.class);

    private MqUtils() {}

    public static String writeAsJson(final Object object) {
        try {
            return JsonSerialization.writeValueAsString(object);
        } catch (Exception e) {
            log.error("keycloak-to-activemq Could not serialize to JSON", e);
        }
        return "unparseable";
    }

    public static TextMessage createTextMessage(final String text, final Session session) {
        try {
            return session.createTextMessage(text);
        } catch (JMSException e) {
            log.error("keycloak-to-activemq Could not create TextMessage", e);
        }
        return null;
    }
}
