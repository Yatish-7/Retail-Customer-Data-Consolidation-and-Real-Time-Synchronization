package com.newmeksolutions.model;

import java.sql.Date;
import java.sql.Timestamp;

public class CustomerChangePayload {

    private int CRMID;
    private int CustomerID;
    private String FirstName;
    private String LastName;
    private String CustomerName;
    private String PhoneNumber;
    private String Email;
    private String Address;
    private String Location;
    private String State;
    private String PinCode;
    private Date DOB;
    private Timestamp LastPurchase;
    private String Reference;
    private String ActionType;
    private Timestamp ActionTime;

    // --- Getters and Setters ---

    public int getCRMID() {
        return CRMID;
    }

    public void setCRMID(int CRMID) {
        this.CRMID = CRMID;
    }

    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int customerID) {
        this.CustomerID = customerID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        this.FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        this.LastName = lastName;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        this.CustomerName = customerName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.PhoneNumber = phoneNumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        this.Location = location;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        this.State = state;
    }

    public String getPinCode() {
        return PinCode;
    }

    public void setPinCode(String pinCode) {
        this.PinCode = pinCode;
    }

    public Date getDOB() {
        return DOB;
    }

    public void setDOB(Date DOB) {
        this.DOB = DOB;
    }

    public Timestamp getLastPurchase() {
        return LastPurchase;
    }

    public void setLastPurchase(Timestamp lastPurchase) {
        this.LastPurchase = lastPurchase;
    }

    public String getReference() {
        return Reference;
    }

    public void setReference(String reference) {
        this.Reference = reference;
    }

    public String getActionType() {
        return ActionType;
    }

    public void setActionType(String actionType) {
        this.ActionType = actionType;
    }

    public Timestamp getActionTime() {
        return ActionTime;
    }

    public void setActionTime(Timestamp actionTime) {
        this.ActionTime = actionTime;
    }
    
    public void determineReference() {
        boolean isBasicNull = 
            (this.DOB == null) &&
            (isNullOrEmpty(this.Address)) &&
            (isNullOrEmpty(this.FirstName)) &&
            (isNullOrEmpty(this.LastName)) &&
            (isNullOrEmpty(this.PinCode)) &&
            (isNullOrEmpty(this.State));

        this.Reference = isBasicNull ? "POS_KPHB" : "OnlineReg";
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
