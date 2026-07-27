package com.app.family.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateRecipeBookReqDto (
    @NotNull int id,
    @NotNull String name
) {
    
}
