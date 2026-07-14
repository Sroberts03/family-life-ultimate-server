package com.app.chore.dto;

import java.util.Set;
import jakarta.validation.constraints.NotNull;

public record UpdateChoreAssigneesReqDto(
    @NotNull int choreId,
    @NotNull Set<String> choreAssigneeIds
) {}
