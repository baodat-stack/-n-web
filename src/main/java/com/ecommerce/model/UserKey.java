package com.ecommerce.model;

public class UserKey {
    private int id;
    private int userId;
    private String publicKey;
    private String fingerprint;
    private String issueDate;
    private String revokeDate;
    private String status;

    public UserKey() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public String getFingerprint() { return fingerprint; }
    public void setFingerprint(String fingerprint) { this.fingerprint = fingerprint; }

    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }

    public String getRevokeDate() { return revokeDate; }
    public void setRevokeDate(String revokeDate) { this.revokeDate = revokeDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
