package com.app.activities.types;

public class DetailedActivity {
    private String name;
    private String description;
    private int activityId;
    private boolean isAdmin;

    public DetailedActivity(String name, String description, int activityId, boolean isAdmin) {
        this.name = name;
        this.description = description;
        this.activityId = activityId;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
