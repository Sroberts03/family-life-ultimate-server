package com.app.activities;

import org.springframework.web.bind.annotation.RestController;
import com.app.activities.dto.GetAllActivities;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.List;
import com.app.activities.types.DetailedActivity;

@RestController
@RequestMapping("/api/activity")
public class ActivitiesController {
    
    private final ActivitiesService activitiesService;

    public ActivitiesController(ActivitiesService activitiesService) {
        this.activitiesService = activitiesService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<GetAllActivities> getAllActivities(@AuthenticationPrincipal Jwt jwt) throws Exception {
        List<DetailedActivity> activities = activitiesService.getAllActivities();
        GetAllActivities response = new GetAllActivities(activities);
        return ResponseEntity.ok(response);
    }

}
