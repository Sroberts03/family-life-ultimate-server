package com.app.family.dto;

import jakarta.validation.constraints.NotNull;
import com.app.family.types.FamilyRole;

public record JoinFamilyRequestDto(
    @NotNull
    String familyId,

    @NotNull
    FamilyRole role) {}