package com.newmeksolutions.listener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import com.newmeksolutions.config.RabbitMQConfig;
import com.newmeksolutions.dao.CustomerDAO;
import com.newmeksolutions.model.CustomerChangePayload;
import com.newmeksolutions.util.JSONUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class RabbitMQConsumer {
    private static final String EXCHANGE_NAME = "customer_data";
    private static final String QUEUE_NAME = "customer_data_queue";
    private static final String BINDING_KEY = "customer_key";

    public void start() throws IOException, TimeoutException {

        ConnectionFactory factory = RabbitMQConfig.getFactory();
        com.rabbitmq.client.Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true);
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, BINDING_KEY);

        System.out.println("🎯 RabbitMQ Consumer started. Waiting for messages...\n");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("📥 Received message:\n" + message);

            try {
                // Parse JSON to payload
                CustomerChangePayload payload = JSONUtil.parseCustomer(message);

                // 🔧 FIX: Build customerName from firstName + lastName if missing
                if (payload.getCustomerName() == null || payload.getCustomerName().isBlank()) {
                    String first = payload.getFirstName() != null ? payload.getFirstName().trim() : "";
                    String last = payload.getLastName() != null ? payload.getLastName().trim() : "";
                    String fullName = (first + " " + last).trim();
                    payload.setCustomerName(fullName.isBlank() ? null : fullName);
                }

                System.out.println("🔍 Parsed Customer Data:");
                System.out.println("    👤 ID: " + payload.getCustomerID());
                System.out.println("    🧑 Name: " + payload.getCustomerName());
                System.out.println("    📞 Phone: " + payload.getPhoneNumber());
                System.out.println("    📧 Email: " + payload.getEmail());
                System.out.println("    🌍 Location: " + payload.getLocation());
                System.out.println("    📦 Action: " + payload.getActionType());
                System.out.println("    🕒 LoggedAt: " + payload.getActionTime());
                System.out.println("    🏷️ Reference: " + payload.getReference());

                String action = payload.getActionType();
                if (action == null) {
                    System.err.println("⚠️ Missing actionType in payload.");
                    return;
                }

                switch (action.toUpperCase()) {
                    case "INSERT":
                        CustomerDAO.insertCustomerIfNotExists(payload);
                        break;

                    case "UPDATE":
                        if (CustomerDAO.existsByCRMID(payload.getCustomerID())) {
                            CustomerDAO.updateCustomerByCRMID(payload);
//                            CustomerDAO.updateCustomerLogByCRMID(payload);
                        } else {
                            System.out.println("⚠️ Cannot update. Customer not found with CRMID: " + payload.getCustomerID());
                        }
                        break;

                    case "DELETE":
                        if (CustomerDAO.existsByCRMID(payload.getCustomerID())) {
                            CustomerDAO.deleteCustomerByCRMID(payload.getCustomerID());
//                            CustomerDAO.deleteCustomerLogByCRMID(payload.getCRMID());
                        } else {
                            System.out.println("⚠️ Cannot delete. Customer not found with CRMID: " + payload.getCustomerID());
                        }
                        break;

                    default:
                        System.out.println("⚠️ Unknown action type: " + action);
                }

                System.out.println("✅ Successfully processed Customer_Table and Customer_Log.\n");

            } catch (SQLException e) {
                System.err.println("❌ Database error while processing message: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("❌ Unexpected error while processing message: " + e.getMessage());
                e.printStackTrace();
            }
        };

        // Auto Ack = true for simplicity
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }
}
