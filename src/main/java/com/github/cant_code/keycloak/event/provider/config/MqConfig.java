package com.github.cant_code.keycloak.event.provider.config;

import org.jboss.logging.Logger;
import org.keycloak.Config;

import java.util.Locale;

import static java.util.Objects.nonNull;

public class MqConfig {

    private static final Logger log = Logger.getLogger(MqConfig.class);

    private String brokerUrl;
    private String user;
    private String password;
    private String queue;

    public static MqConfig create(final Config.Scope config) {
        MqConfig mqConfig = new MqConfig();
        mqConfig.brokerUrl = getConfigFromScope(config, "brokerUrl", "tcp://localhost:61616");
        mqConfig.user = getConfigFromScope(config, "user", "artemis");
        mqConfig.password = getConfigFromScope(config, "password", "artemis");
        mqConfig.queue = getConfigFromScope(config, "queue", "queue/keycloak");

        return mqConfig;
    }

    private static String getConfigFromScope(final Config.Scope scope, final String key, final String defaultValue) {
        String value = defaultValue;
        if(nonNull(scope) && nonNull(scope.get(key))) {
            value = scope.get(key);
        } else {
            String env = System.getenv("KC_TO_AMQ_" + key.toUpperCase(Locale.ENGLISH));
            if(env != null) {
                value = env;
            }
        }
        if (!"password".equals(key)) {
            log.infof("keycloak-to-activemq configuration: %s=%s%n", key, value);
        }
        return value;
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }
}
