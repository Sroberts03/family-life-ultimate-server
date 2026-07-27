package com.app.meal.dto;

import jakarta.validation.constraints.NotNull;

public record CreateRecipeBook (
    @NotNull
    String name,
    @NotNull
    String familyId
) {}
