package de.dobermai.eclipsemagazin.paho.client.subscriber;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author Dominik Obermaier
 */
public class SubscribeCallback implements MqttCallback {

    public void connectionLost(Throwable cause) {
        //This is called when the connection is lost. We could reconnect here.
    }

	public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Message arrived. Topic: " + topic + "  Message: " + message.toString());
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
        //no-op
	}
}
