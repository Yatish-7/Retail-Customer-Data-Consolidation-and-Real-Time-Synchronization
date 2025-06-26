package com.newmeksolutions.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQConfig 
{

    // This code sets the connection properties to rabbitmq and returns connection type//
	private static final String QUEUE_NAME = "customer_data_queue";

    public static Channel getChannel() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost"); 
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        return channel;
    }

    public static String getQueueName() {
        return QUEUE_NAME;
    }

}
