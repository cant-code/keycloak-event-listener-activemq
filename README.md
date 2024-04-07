# keycloak-event-listener-activemq

##### A Keycloak SPI plugin that publishes events to a ActiveMQ Artemis server.

| Plugin | min Keycloak ver |
|--------|------------------|
| 1.x    | 16.x             |

Example for a user created by administrator

* Published to queue: `jms/keycloak`
* Content:
```
{
  "id": "35db51dc-b9c4-401a-8d15-5a34ef314d67",
  "time": 1712416880767,
  "realmId": "f17dd632-a821-4161-a0cc-36bde3724033",
  "authDetails": {
    "realmId": "032888e4-0f0a-421c-a0d2-0d247d391420",
    "clientId": "91f0f1cc-3784-4410-afb1-0b7fca4b7468",
    "userId": "0c12c588-98ab-488d-8822-1776751bfba4",
    "ipAddress": "192.168.1.1"
  },
  "resourceType": "USER",
  "operationType": "CREATE",
  "resourcePath": "users/e6e0058d-038e-4012-82b8-af7c9f56151e",
  "representation": "{\"username\":\"test2\",\"firstName\":\"test2\",...}",
  "resourceTypeAsString": "USER"
}
```
* Reference Admin Events: @org.keycloak.events.admin.AdminEvent
* Reference User Events: @org.keycloak.events.Events

## USAGE:
1. [Download the latest jar](https://github.com/cant-code/keycloak-event-listener-activemq/releases) or build from source: ``mvn clean install``
2. Copy jar into your Keycloak
    1. Keycloak version 17+ (Quarkus) `/opt/keycloak/providers/keycloak-to-activemq-1.4-SNAPSHOT.jar`
3. Configure as described below
4. Restart the Keycloak server
5. Enable in Keycloak UI by adding **keycloak-to-activemq**  
   `Manage > Events > Config > Events Config > Event Listeners`

#### Configuration
###### **ENVIRONMENT VARIABLES**
- `KC_TO_AMQ_URL` - default: *tcp://localhost:61616*
- `KC_TO_AMQ_QUEUE` - default: *jms/keycloak*
- `KC_TO_AMQ_USER` - default: *artemis*
- `KC_TO_AMQ_PASSWORD` - default: *artemis*

## References:
* https://github.com/aznamier/keycloak-event-listener-rabbitmq
* https://activemq.apache.org/components/artemis/documentation/1.0.0/using-core.html