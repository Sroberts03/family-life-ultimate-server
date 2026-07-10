package com.app.family.types;

import java.util.List;

import com.app.auth.types.PersActivity;

public class FamilyMember {
    private String userId;
    private String fullName;
    private FamilyRole role;
    private List<PersActivity> activities;

    public FamilyMember(String userId, String fullName, FamilyRole role, List<PersActivity> activities) {
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
        this.activities = activities;
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

    public List<PersActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<PersActivity> activities) {
        this.activities = activities;
    }
}
