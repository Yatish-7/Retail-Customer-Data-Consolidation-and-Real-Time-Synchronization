package com.newmeksolutions;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.newmeksolutions.listener.RabbitMQConsumer;

public class AppConsumer {
    public static void main(String[] args) throws IOException, TimeoutException {
        RabbitMQConsumer consumer = new RabbitMQConsumer();
        consumer.start();
        
    }
}
