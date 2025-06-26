package com.newmeksolutions.model;

public class CustomerChangePayload {
	public int customerId;
    public String customerName;
    public String phoneNumber;
    public String email;
    public String location;
    public String actionType;
    public String loggedAt;

    public CustomerChangePayload() {}

    @Override
    public String toString() {
        return "CustomerChangePayload{" +
                "customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", location='" + location + '\'' +
                ", actionType='" + actionType + '\'' +
                ", loggedAt='" + loggedAt + '\'' +
                '}';
        
    }

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getLoggedAt() {
		return loggedAt;
	}

	public void setLoggedAt(String loggedAt) {
		this.loggedAt = loggedAt;
	}

}
