package com.app.chore.dto;

import jakarta.validation.constraints.NotNull;

public record CreateChoreReq(
    @NotNull String familyId,
    @NotNull String name,
    @NotNull String description,
    @NotNull String recurring,
    @NotNull String startDate,
    String endDate
) {
}
