package com.app.family.dto;

import java.util.List;
import com.app.family.types.TruncatedFamily;
import jakarta.validation.constraints.NotNull;

public record GetAuthFamiliesRes(
    @NotNull
    List<TruncatedFamily> families
) {}
