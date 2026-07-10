package com.app.auth.types;

public class PersActivity {
    private String activityName;
    private String familyId;

    public PersActivity(String activityName, String familyId) {
        this.activityName = activityName;
        this.familyId = familyId;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getFamilyId() {
        return familyId;
    }
}
