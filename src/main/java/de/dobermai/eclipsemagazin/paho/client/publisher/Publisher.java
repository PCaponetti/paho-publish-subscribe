package de.dobermai.eclipsemagazin.paho.client.publisher;

import de.dobermai.eclipsemagazin.paho.client.util.Utils;
import org.eclipse.paho.client.mqttv3.*;

/**
 * @author Dominik Obermaier
 * @author Paul Caponetti
 */
public class Publisher {


    public static final String BROKER_URL = "ssl://broker.xively.com:443";
    // channel to talk over (make/get these from app.xively.com)
    public static final String TOPIC = "xi/blue/v1/bf322efe-ddbf-46d2-94db-df240c9fde76/d/c63921c8-a650-4e7d-a900-77fa531a9d2f/wakeup";
    // username and password come from APIs at blueprint.xively.com
    // specifically, after you login to id.xively.com, go to https://blueprint.xively.com/api/v1/documentation?tags=provision&accountId=#!/access/mqtt-credentials/apiv1accessmqtt_credentials_post_0
    public static final String USERNAME = "c63921c8-a650-4e7d-a900-77fa531a9d2f";
    public static final String PASSWORD = "5XleZzK+zkJnvxDTJwOOfkZOsmQf2C0WPJU1Cm3fobE=";
    
    private MqttClient client;
    private MqttTopic mainTopic;

    public Publisher() {
        //We have to generate a unique Client id.
        String clientId = Utils.getMacAddress() + "-pub";

        try {
            client = new MqttClient(BROKER_URL, clientId);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void start() {

        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(USERNAME);
            options.setPassword(PASSWORD.toCharArray());
            options.setWill(client.getTopic(TOPIC), "I disconnected...".getBytes(), 0, false);
        	options.setConnectionTimeout(60);
        	options.setKeepAliveInterval(60);

            client.connect(options);

            mainTopic = client.getTopic(TOPIC);

            //Publish data forever
            while (true) {
                publishRandomPayload();
                Thread.sleep(500);
            }
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        	System.exit(1);
        }
    }

    private void publishRandomPayload() throws MqttException {

        final int randomInt = Utils.createRandomNumberBetween(0, 100);
        String randomPayload = "{payload:" + randomInt + "}";
        MqttMessage msg = new MqttMessage(randomPayload.getBytes());
        msg.setRetained(true);

        mainTopic.publish(msg);

        System.out.println("Published data. Topic: " + mainTopic.getName() + "   Message: " + randomPayload);
    }

    public static void main(String... args) {
        final Publisher publisher = new Publisher();
        publisher.start();
    }
}
