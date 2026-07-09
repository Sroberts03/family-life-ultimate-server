package com.app.family.types;

import java.sql.Date;

public class JoinRequest {

    private String fullName;
    private int requestId;
    private String userId;
    private String familyId;
    private FamilyRole role;
    private Date createdAt;

    public JoinRequest(String fullName, int requestId, String userId, String familyId, FamilyRole role, Date createdAt) {
        this.fullName = fullName;
        this.requestId = requestId;
        this.userId = userId;
        this.familyId = familyId;
        this.role = role;
        this.createdAt = createdAt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public FamilyRole getRole() {
        return role;
    }

    public void setRole(FamilyRole role) {
        this.role = role;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
