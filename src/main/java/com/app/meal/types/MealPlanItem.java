package com.app.meal.types;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class MealPlanItem {
    int id;
    String familyId;
    int recipeId;
    String name;
    LocalDate date;
    LocalTime time;
    MealType mealType;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public MealPlanItem(
        int id, 
        String familyId, 
        int recipeId, 
        String name, 
        LocalDate date, 
        LocalTime time, 
        MealType mealType, 
        LocalDateTime createdAt, 
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.familyId = familyId;
        this.recipeId = recipeId;
        this.name = name;
        this.date = date;
        this.time = time;
        this.mealType = mealType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
