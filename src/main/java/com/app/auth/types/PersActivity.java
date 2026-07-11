package com.app.auth.types;

public class PersActivity {
    private int activityId;
    private String activityName;
    private String familyId;

    public PersActivity(int activityId, String activityName, String familyId) {
        this.activityId = activityId;
        this.activityName = activityName;
        this.familyId = familyId;
    }

    public int getActivityId() {
        return activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getFamilyId() {
        return familyId;
    }
}
