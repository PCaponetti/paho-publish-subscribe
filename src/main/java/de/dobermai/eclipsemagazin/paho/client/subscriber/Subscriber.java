package de.dobermai.eclipsemagazin.paho.client.subscriber;

import de.dobermai.eclipsemagazin.paho.client.util.Utils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * @author Dominik Obermaier
 * @author Paul Caponetti
 */
public class Subscriber {

    public static final String BROKER_URL = "ssl://broker.xively.com:8883";
    // channel to talk over (make/get these from app.xively.com)
    public static final String TOPIC = "xi/blue/v1/17b2da03-13b3-4b81-b092-08e6d07d8451/d/172e878e-b259-420b-a423-93c1ac3083e7/temp";
    // username and password come from APIs at blueprint.xively.com
    // specifically, after you login to id.xively.com, go to https://blueprint.xively.com/api/v1/documentation?tags=provision&accountId=#!/access/mqtt-credentials/apiv1accessmqtt_credentials_post_0
    public static final String USERNAME = "b335ccba-9cec-445b-8321-a97d2a0a5155";
    public static final String PASSWORD = "Z7rmxOk5R0MV0xCa3dhfEtc2NUEu8f8tHceQ4MfjdjE=";

    //We have to generate a unique Client id.
    String clientId = USERNAME;
    private MqttClient client;

    public Subscriber() {

        try {
        	client = new MqttClient(BROKER_URL, clientId);
            
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(USERNAME);
            options.setPassword(PASSWORD.toCharArray());
            options.setWill(client.getTopic(TOPIC), "I disconnected...".getBytes(), 0, false);
        	options.setConnectionTimeout(60);
        	options.setKeepAliveInterval(60);

            client.setCallback(new SubscribeCallback());
            client.connect();

            client.subscribe(TOPIC);

        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
        	e.printStackTrace();
        	System.exit(1);
        }
    }

    public static void main(String... args) {
        final Subscriber subscriber = new Subscriber();
        subscriber.start();
    }

}
