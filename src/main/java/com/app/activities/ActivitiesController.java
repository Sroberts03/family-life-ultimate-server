package com.app.activities;

import org.springframework.web.bind.annotation.RestController;

import com.app.BaseResponseDto;
import com.app.activities.dto.GetAllActivities;
import com.app.activities.dto.UpdateUserActivities;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.List;
import com.app.activities.types.DetailedActivity;

import jakarta.validation.Valid;

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

    @PostMapping("/set-permissions")
    public ResponseEntity<BaseResponseDto> setPermissions(
            @AuthenticationPrincipal Jwt jwt, 
            @RequestBody @Valid UpdateUserActivities request) throws Exception {
        activitiesService.updateUserActivities(jwt.getSubject(), request.userId(), request.familyId(), request.permissions());
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", "Activities updated successfully");
        return ResponseEntity.ok(response);
    }

}
