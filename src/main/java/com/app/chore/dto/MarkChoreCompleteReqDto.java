package com.app.chore.dto;

import java.util.Date;
import jakarta.validation.constraints.NotNull;

public record MarkChoreCompleteReqDto (
    @NotNull
    int choreId,
    @NotNull
    Date dateCompleted
) {}
