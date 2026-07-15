package com.app.chore.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateChoreReq (
    @NotNull int choreId,
    @NotNull String familyId,
    @NotNull String name,
    @NotNull String description,
    @NotNull String recurring,
    @NotNull String startDate,
    String endDate
) {
    
}
