package com.app.meal.dto;

import java.util.List;

import com.app.meal.types.Recipe;

public record GetRecipesResDto(
    List<Recipe> recipes
) {
}
