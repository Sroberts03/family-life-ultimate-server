package com.app.meal.dto;

import java.util.List;

import com.app.meal.types.MealPlanItem;

public record GetMealPlansResDto(List<MealPlanItem> mealPlans) {
}
