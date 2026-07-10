package com.app.family.types;

public class TruncatedFamilyMember {
    private String userId;
    private String fullName;
    private FamilyRole role;

    public TruncatedFamilyMember(String userId, String fullName, FamilyRole role) {
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public FamilyRole getRole() {
        return role;
    }

    public void setRole(FamilyRole role) {
        this.role = role;
    }
}
