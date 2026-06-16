package com.ecommerce.model;

public class OrderSignRequest {
    private int id;
    private int orderId;
    private int userId;
    private String originalOrderJson;
    private String originalOrderHash;
    private String downloadTime;

    public OrderSignRequest() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getOriginalOrderJson() { return originalOrderJson; }
    public void setOriginalOrderJson(String originalOrderJson) { this.originalOrderJson = originalOrderJson; }

    public String getOriginalOrderHash() { return originalOrderHash; }
    public void setOriginalOrderHash(String originalOrderHash) { this.originalOrderHash = originalOrderHash; }

    public String getDownloadTime() { return downloadTime; }
    public void setDownloadTime(String downloadTime) { this.downloadTime = downloadTime; }
}
