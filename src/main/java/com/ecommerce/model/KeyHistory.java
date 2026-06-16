package com.ecommerce.model;

public class KeyHistory {
    private int id;
    private int userId;
    private int keyId;
    private String action;
    private String actionDate;

    public KeyHistory() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getKeyId() { return keyId; }
    public void setKeyId(int keyId) { this.keyId = keyId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getActionDate() { return actionDate; }
    public void setActionDate(String actionDate) { this.actionDate = actionDate; }
}
