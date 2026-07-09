package com.app.family.dto;

import jakarta.validation.constraints.NotNull;

public record AcceptOrDenyRequestDto(
    @NotNull
    int requestId,
    @NotNull
    boolean accept
) {
    
}
