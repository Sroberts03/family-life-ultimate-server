package com.app.family.dto;

import java.util.List;
import com.app.family.types.JoinRequest;
import jakarta.validation.constraints.NotNull;

public record GetJoinRequests (
    @NotNull
    List<JoinRequest> joinRequests
) {}
