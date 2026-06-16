package com.ecommerce.model;

public class VerificationLog {
    private int id;
    private int orderId;
    private int userId;
    private String verifyTime;
    private String result;
    private String message;

    public VerificationLog() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getVerifyTime() { return verifyTime; }
    public void setVerifyTime(String verifyTime) { this.verifyTime = verifyTime; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
