package com.newmeksolutions.config;

import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQConfig {
	public static ConnectionFactory getFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("yatish");
        factory.setPassword("803671");
        return factory;
    }

}
