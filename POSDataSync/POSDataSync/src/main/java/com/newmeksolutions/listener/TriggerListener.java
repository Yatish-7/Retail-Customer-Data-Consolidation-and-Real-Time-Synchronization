package com.newmeksolutions.listener;

import com.newmeksolutions.config.DBConfig;
import com.newmeksolutions.config.RabbitMQConfig;
import com.newmeksolutions.model.CustomerChangePayload;
import com.newmeksolutions.model.OnlineRegChangePayload;
import com.newmeksolutions.util.JSONUtil;
import com.rabbitmq.client.Channel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TriggerListener {

    private int lastPOSLogId = 0;
    private int lastOnlineLogId = 0;

    public void pollChanges() {
        try (
            Connection posConn = DBConfig.getPOSDBConnection();
            Connection onlineConn = DBConfig.getOnlineDBConnection();
            Channel channel = RabbitMQConfig.getChannel()
        ) {
            // Poll POS_KPHB_Log table
            PreparedStatement psPOS = posConn.prepareStatement(
                "SELECT * FROM POS_KPHB_Log WHERE LogID > ? ORDER BY LogID ASC"
            );
            psPOS.setInt(1, lastPOSLogId);
            ResultSet rsPOS = psPOS.executeQuery();

            while (rsPOS.next()) {
                // Get action type
                String actionType = rsPOS.getString("ActionType");

                // Create payload
                CustomerChangePayload payload = new CustomerChangePayload();
                payload.setCustomerId(rsPOS.getInt("CustomerID"));
                payload.setCustomerName(rsPOS.getString("CustomerName"));
                payload.setPhoneNumber(rsPOS.getString("PhoneNumber"));
                payload.setEmail(rsPOS.getString("Email"));
                payload.setLocation(rsPOS.getString("Location"));
                payload.setActionType(actionType);
                payload.setLoggedAt(rsPOS.getString("LoggedAt"));

                // Convert to JSON
                String json = JSONUtil.toJSON(payload);

                // Log action and data sent to RabbitMQ with emojis
                System.out.println("🔔 Got POS " + actionType + " data -> " + json);
                System.out.println("🚀 Sent to RabbitMQ: " + json);

                // Send to RabbitMQ
                channel.basicPublish("", RabbitMQConfig.getQueueName(), null, json.getBytes());

                // Update the last LogID
                lastPOSLogId = rsPOS.getInt("LogID");
            }

            // Poll OnlineRegistration_Log table
            PreparedStatement psOnline = onlineConn.prepareStatement(
                "SELECT * FROM OnlineRegistration_Log WHERE LogID > ? ORDER BY LogID ASC"
            );
            psOnline.setInt(1, lastOnlineLogId);
            ResultSet rsOnline = psOnline.executeQuery();

            while (rsOnline.next()) {
                // Get action type
                String actionType = rsOnline.getString("ActionType");

                // Create payload
                OnlineRegChangePayload payload = new OnlineRegChangePayload();
                payload.setCustomerId(rsOnline.getInt("CustomerID"));
                payload.setFirstName(rsOnline.getString("FirstName"));
                payload.setLastName(rsOnline.getString("LastName"));
                payload.setLastPurchase(rsOnline.getTimestamp("LastPurchase"));
                payload.setEmail(rsOnline.getString("Email"));
                payload.setPhoneNumber(rsOnline.getString("PhoneNumber"));
                payload.setDob(rsOnline.getDate("DOB"));
                payload.setAddress(rsOnline.getString("Address"));
                payload.setState(rsOnline.getString("State"));
                payload.setPinCode(rsOnline.getString("PinCode"));
                payload.setActionType(actionType);
                payload.setLoggedAt(rsOnline.getString("LoggedAt"));

                // Convert to JSON
                String json = JSONUtil.toJSON(payload);

                // Log action and data sent to RabbitMQ with emojis
                System.out.println("🔔 Got Online Reg " + actionType + " data -> " + json);
                System.out.println("🚀 Sent to RabbitMQ: " + json);

                // Send to RabbitMQ
                channel.basicPublish("", RabbitMQConfig.getQueueName(), null, json.getBytes());

                // Update the last LogID
                lastOnlineLogId = rsOnline.getInt("LogID");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}