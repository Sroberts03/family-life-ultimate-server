package com.app.activities.dto;

import java.util.List;

import com.app.activities.types.DetailedActivity;

public record GetAllActivities(
    List<DetailedActivity> persActivities
) {}
