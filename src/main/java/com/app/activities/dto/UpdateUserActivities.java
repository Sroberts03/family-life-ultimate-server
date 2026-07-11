package com.app.activities.dto;

import java.util.Map;

import jakarta.validation.constraints.NotNull;

public record UpdateUserActivities(
    @NotNull String userId,
    @NotNull String familyId,
    @NotNull Map<Integer, Boolean> permissions) {
}
