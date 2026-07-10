package com.app.family;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.app.BaseResponseDto;
import com.app.family.dto.AcceptOrDenyRequestDto;
import com.app.family.dto.CreateFamilyRequestDto;
import com.app.family.dto.GetAuthFamiliesRes;
import com.app.family.dto.GetFamilyMembers;
import com.app.family.dto.GetJoinRequests;
import com.app.family.dto.JoinFamilyRequestDto;
import com.app.family.exceptions.OwnerMustBeAdultException;
import com.app.family.types.FamilyMember;
import com.app.family.types.JoinRequest;
import com.app.family.types.TruncatedFamily;

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

    @PutMapping("/edit-request")
    public ResponseEntity<BaseResponseDto> acceptOrDenyRequest (
        @RequestBody @Valid AcceptOrDenyRequestDto body,
        @AuthenticationPrincipal Jwt jwt
    ) throws Exception {
        familyService.acceptOrDenyRequest(jwt.getSubject(), body.requestId(), body.accept());
        BaseResponseDto response = new BaseResponseDto();
        String accepted = body.accept() == true ? "accepted" : "denied"; 
        response.getBody().put("message", "Request" + accepted  + "successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-requests")
    public ResponseEntity<GetJoinRequests> getJoinRequests (
        @RequestParam("familyId") String familyId,
        @AuthenticationPrincipal Jwt jwt
    ) throws Exception {
        List<JoinRequest> requests = familyService.getJoinRequests(jwt.getSubject(), familyId);
        GetJoinRequests response = new GetJoinRequests(requests);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all-auth")
    public ResponseEntity<GetAuthFamiliesRes> getAllAuthFamilies(
        @AuthenticationPrincipal Jwt jwt
    ) {
        List<TruncatedFamily> families = familyService.getAllAuthFamilies(jwt.getSubject());
        GetAuthFamiliesRes response = new GetAuthFamiliesRes(families);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-members")
    public ResponseEntity<GetFamilyMembers> getFamilyMembers(
        @RequestParam("familyId") String familyId,
        @AuthenticationPrincipal Jwt jwt
    ) throws Exception {
        List<FamilyMember> members = familyService.getAllFamilyMembers(familyId, jwt.getSubject());
        GetFamilyMembers response = new GetFamilyMembers(members);
        return ResponseEntity.ok(response);
    }

}