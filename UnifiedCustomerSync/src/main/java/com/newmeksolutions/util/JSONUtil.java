package com.newmeksolutions.util;

import com.newmeksolutions.model.CustomerChangePayload;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Timestamp;

public class JSONUtil {

    public static CustomerChangePayload parseCustomer(String jsonStr) {
        JSONObject json = new JSONObject(jsonStr);
        CustomerChangePayload payload = new CustomerChangePayload();

        payload.setCustomerID(json.optInt("customerId", 0));
        payload.setFirstName(json.optString("firstName", null));
        payload.setLastName(json.optString("lastName", null));
        payload.setCustomerName(json.optString("customerName", null));
        payload.setPhoneNumber(json.optString("phoneNumber", null));
        payload.setEmail(json.optString("email", null));
        payload.setAddress(json.optString("address", null));
        payload.setLocation(json.optString("location", null));
        payload.setState(json.optString("state", null));
        payload.setPinCode(json.optString("pinCode", null));

        // DOB
        if (json.has("dob") && !json.isNull("dob")) {
            payload.setDOB(new Date(json.optLong("dob")));
        }

        // LastPurchase
        if (json.has("lastPurchase") && !json.isNull("lastPurchase")) {
            payload.setLastPurchase(new Timestamp(json.optLong("lastPurchase")));
        }

        // LoggedAt → ActionTime
        if (json.has("loggedAt") && !json.isNull("loggedAt")) {
            try {
                payload.setActionTime(Timestamp.valueOf(json.getString("loggedAt")));
            } catch (Exception e) {
                payload.setActionTime(new Timestamp(System.currentTimeMillis()));
            }
        } else {
            payload.setActionTime(new Timestamp(System.currentTimeMillis()));
        }

        // ActionType
        payload.setActionType(json.optString("actionType", "INSERT"));

        // Reference logic
        if (
                isNullOrEmpty(payload.getFirstName()) &&
                isNullOrEmpty(payload.getLastName()) &&
                isNullOrEmpty(payload.getAddress()) &&
                isNullOrEmpty(payload.getState()) &&
                isNullOrEmpty(payload.getPinCode()) &&
                payload.getDOB() == null
        ) {
            payload.setReference("POS_KPHB");
        } else {
            payload.setReference("OnlineReg");
        }

        return payload;
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
