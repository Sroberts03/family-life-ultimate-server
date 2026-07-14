package com.app.chore.types;

import java.util.Date;
import java.util.Set;

public class Chore {
    private int id;
    private String name;
    private String description;
    private Date dueDate;
    private Date dateCompleted;
    private Set<String> assigneeIds;
    private String[] assigneeNames;

    public Chore(int id, String name, String description, Date dueDate, Date dateCompleted, Set<String> assigneeIds, String[] assigneeNames){
        this.id = id;
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.dateCompleted = dateCompleted;
        this.assigneeIds = assigneeIds;
        this.assigneeNames = assigneeNames;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public Set<String> getAssigneeIds() {
        return assigneeIds;
    }

    public void setAssigneeIds(Set<String> assigneeIds) {
        this.assigneeIds = assigneeIds;
    }

    public String[] getAssigneeNames(){
        return assigneeNames;
    }

    public void setAssigneeNames(String[] assigneeNames){
        this.assigneeNames = assigneeNames;
    }

}
