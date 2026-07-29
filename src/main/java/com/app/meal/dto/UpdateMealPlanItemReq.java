package com.app.meal.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import com.app.meal.types.MealType;
import jakarta.annotation.Nullable;

public record UpdateMealPlanItemReq (
    int mealPlanItemId,
    String familyId,
    MealType mealType,
    String name,
    @Nullable Integer recipeId,
    LocalDate date,
    LocalTime time) {
    
}
