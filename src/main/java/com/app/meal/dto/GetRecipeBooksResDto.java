package com.app.meal.dto;

import java.util.List;

import com.app.meal.types.RecipeBook;

public record GetRecipeBooksResDto(List<RecipeBook> recipeBooks) {
}
