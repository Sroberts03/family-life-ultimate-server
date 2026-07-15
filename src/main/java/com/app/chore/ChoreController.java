package com.app.chore;

import java.time.LocalDate;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.app.BaseResponseDto;
import com.app.chore.dto.CreateChoreReq;
import com.app.chore.dto.CreateChoreRes;
import com.app.chore.dto.GetAllChoresRes;
import com.app.chore.dto.GetChoreInfoRes;
import com.app.chore.dto.MarkChoreCompleteReqDto;
import com.app.chore.dto.UpdateChoreAssigneesReqDto;
import com.app.chore.dto.UpdateChoreAssignmentsDtoRes;
import com.app.chore.dto.UpdateChoreReq;
import com.app.chore.types.Chore;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chores")
public class ChoreController {

    private final ChoreService choreService;

    public ChoreController(ChoreService choreService) {
        this.choreService = choreService;
    }

    @GetMapping("/get-all-chores-date")
    public ResponseEntity<GetAllChoresRes> getAllChoresForFamilyForDate(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("familyId") String familyId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) throws Exception {
        Map<Integer, Chore> chores = choreService.getAllChoresForFamilyForDate(jwt.getSubject(), familyId, date);
        GetAllChoresRes response = new GetAllChoresRes(chores);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/mark-chore-complete")
    public ResponseEntity<BaseResponseDto> markChoreComplete(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid MarkChoreCompleteReqDto body
    ) throws Exception {
        System.out.println("date completed: " + body.dateCompleted());
        choreService.markChoreComplete(jwt.getSubject(), body.dateCompleted(), body.choreId());
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", "Chore marked complete sduccessfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<CreateChoreRes> createChore(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody @Valid CreateChoreReq body
    ) throws Exception {
        Chore chore = choreService.createChore(
            jwt.getSubject(), 
            body.familyId(), 
            body.name(), 
            body.description(), 
            body.recurring(), 
            body.startDate(), 
            body.endDate()
        );
        CreateChoreRes response = new CreateChoreRes(chore);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponseDto> deleteChore(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam("choreId") int choreId,
        @RequestParam("thisAndFuture") boolean thisAndFuture
    ) throws Exception {
        choreService.deleteChore(jwt.getSubject(), choreId, thisAndFuture);
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", "Chore deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-chore-assignees")
    public ResponseEntity<UpdateChoreAssignmentsDtoRes> updateChoreAssignees(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid UpdateChoreAssigneesReqDto body) throws Exception {
        Chore chore = choreService.updateChoreAssignees(jwt.getSubject(), body.choreId(), body.choreAssigneeIds());
        UpdateChoreAssignmentsDtoRes res = new UpdateChoreAssignmentsDtoRes(chore);
        return ResponseEntity.ok(res);
    }

    @GetMapping("get-chore-info")
    public ResponseEntity<GetChoreInfoRes> getChoreInfo(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam("choreId") int choreId
    ) throws Exception {
        CreateChoreReq chore = choreService.getChoreInfo(jwt.getSubject(), choreId);
        GetChoreInfoRes res = new GetChoreInfoRes(chore);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/update")
    public ResponseEntity<CreateChoreRes> updateChore (
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody @Valid UpdateChoreReq body
    ) throws Exception {
        Chore chore = 
            choreService.updateChore(
                jwt.getSubject(), 
                body.choreId(), 
                body.familyId(), 
                body.name(), 
                body.description(), 
                body.recurring(), 
                body.startDate(), 
                body.endDate()
            );
        CreateChoreRes response = new CreateChoreRes(chore);
        return ResponseEntity.ok(response);
    }

}
