package com.ecommerce.model;

import com.ecommerce.util.AuditAction;

public class AuditLog {
    private int id;
    private int userId;
    private int orderId;
    private AuditAction actionType;
    private String description;
    private String createdAt;

    public AuditLog() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public AuditAction getActionType() { return actionType; }
    public void setActionType(AuditAction actionType) { this.actionType = actionType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
