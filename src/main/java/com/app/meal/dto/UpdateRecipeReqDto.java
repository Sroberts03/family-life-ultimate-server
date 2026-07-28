package com.app.meal.dto;

import jakarta.annotation.Nullable;

public record UpdateRecipeReqDto ( 
    @Nullable Integer id,
    @Nullable Integer recipeBookId,
    String name,
    @Nullable String description,
    String url
) {
    
}
