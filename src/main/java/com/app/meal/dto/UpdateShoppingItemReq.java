package com.app.meal.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateShoppingItemReq(
        @NotNull(message = "ID is required")
        @Min(value = 0, message = "ID must be non-negative")
        Integer id,

        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity must be non-negative")
        Integer quantity,

        @NotBlank(message = "Unit is required")
        @Size(max = 50, message = "Unit must not exceed 50 characters")
        String unit,

        @NotBlank(message = "Item name is required")
        @Size(max = 255, message = "Item name must not exceed 255 characters")
        String item) {
    
}
