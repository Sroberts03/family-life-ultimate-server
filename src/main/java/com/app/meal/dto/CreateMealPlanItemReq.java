package com.app.meal.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import com.app.meal.types.MealType;

import jakarta.annotation.Nullable;

public record CreateMealPlanItemReq(
    String familyId,
    @Nullable Integer recipeId,
    String name,
    LocalDate date,
    LocalTime time,
    MealType mealType
) {}
