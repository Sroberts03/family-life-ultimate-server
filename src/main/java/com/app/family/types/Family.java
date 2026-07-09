package com.app.family.types;

import java.sql.Date;

public class Family {
    private String familyId;
    private String familyName;
    private String ownerId;
    private String subscriptionLevel;
    private Date createdAt;
    private Date updatedAt;

    public Family(String familyId, String familyName, String ownerId, String subscriptionLevel, Date createdAt, Date updatedAt) {
        this.familyId = familyId;
        this.familyName = familyName;
        this.ownerId = ownerId;
        this.subscriptionLevel = subscriptionLevel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getSubscriptionLevel() {
        return subscriptionLevel;
    }

    public void setSubscriptionLevel(String subscriptionLevel) {
        this.subscriptionLevel = subscriptionLevel;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Family [familyId=" + familyId + ", familyName=" + familyName + ", ownerId=" + ownerId
                + ", subscriptionLevel=" + subscriptionLevel + ", createdAt=" + createdAt + ", updatedAt="
                + updatedAt + "]";
    }
}
