package com.app.chore;

import java.time.LocalDate;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.app.BaseResponseDto;
import com.app.chore.dto.GetAllChoresRes;
import com.app.chore.dto.MarkChoreCompleteReqDto;
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
        choreService.markChoreComplete(jwt.getSubject(), body.dateCompleted(), body.choreId());
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", "Chore marked complete successfully");
        return ResponseEntity.ok(response);
    }

}
