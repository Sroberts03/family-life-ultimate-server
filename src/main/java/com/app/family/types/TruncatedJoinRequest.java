package com.app.family.types;

public class TruncatedJoinRequest {
    
    private String userId;
    private String familyId;
    private FamilyRole role;

    public TruncatedJoinRequest(String userId, String familyId, FamilyRole role) {
        this.userId = userId;
        this.familyId = familyId;
        this.role = role;
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
}
