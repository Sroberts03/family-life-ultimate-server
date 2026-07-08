package com.app.family;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.app.BaseResponseDto;
import com.app.family.dto.CreateFamilyRequestDto;
import com.app.family.dto.JoinFamilyRequestDto;
import com.app.family.exceptions.OwnerMustBeAdultException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/family")
public class FamilyContoller {

    private final FamilyService familyService;

    public FamilyContoller(FamilyService familyService) {
        this.familyService = familyService;
    }

    @PostMapping("/join-request")
    public ResponseEntity<BaseResponseDto> requestJoin(
            @RequestBody @Valid JoinFamilyRequestDto body,
            @AuthenticationPrincipal Jwt jwt) throws Exception {
        familyService.requestJoin(jwt.getSubject(), body.familyId(), body.role());
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", "Join request sent successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponseDto> createFamily(
        @RequestBody @Valid CreateFamilyRequestDto body,
        @AuthenticationPrincipal Jwt jwt
    ) throws OwnerMustBeAdultException {
        familyService.createFamily(jwt.getSubject(), body.role());
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", "New family created successfully");
        return ResponseEntity.ok(response);
    }
}