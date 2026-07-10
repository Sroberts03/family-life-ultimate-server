package com.app.family.dto;

import java.util.List;
import com.app.family.types.FamilyMember;

public record GetFamilyMembers(
    List<FamilyMember> members
) {}
