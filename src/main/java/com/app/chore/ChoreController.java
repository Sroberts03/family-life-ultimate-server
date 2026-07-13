package com.app.chore;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.chore.dto.GetAllChoresRes;
import com.app.chore.types.Chore;

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
            @RequestParam("date") Instant date
    ) throws Exception {
        Map<Integer, Chore> chores = choreService.getAllChoresForFamilyForDate(jwt.getSubject(), familyId, date);
        GetAllChoresRes response = new GetAllChoresRes(chores);
        return ResponseEntity.ok(response);
    }

}
