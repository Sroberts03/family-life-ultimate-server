package com.app.meal;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.app.meal.dto.GetMealPlansResDto;
import com.app.meal.dto.GetRecipeBooksResDto;
import com.app.meal.dto.GetRecipeDetailResDto;
import com.app.meal.types.MealPlanItem;
import com.app.meal.types.Recipe;
import com.app.meal.types.RecipeBook;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @GetMapping("/get-all-meal-plans-date")
    public ResponseEntity<GetMealPlansResDto> getMealPlansForFamilyForDate(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("familyId") String familyId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) throws Exception {
        List<MealPlanItem> mealPlans = mealService.getMealPlansForFamilyForDate(jwt.getSubject(), familyId, date);
        GetMealPlansResDto response = new GetMealPlansResDto(mealPlans);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-recipe-detail")
    public ResponseEntity<GetRecipeDetailResDto> getRecipeDetail(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("recipeId") int recipeId
    ) throws Exception {
        Recipe recipeDetail = mealService.getRecipeDetail(jwt.getSubject(), recipeId);
        GetRecipeDetailResDto response = new GetRecipeDetailResDto(recipeDetail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-recipe-books")
    public ResponseEntity<GetRecipeBooksResDto> getRecipeBooksForFamily(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("familyId") String familyId) throws Exception {
        List<RecipeBook> recipeBooks = mealService.getRecipeBooksForFamily(jwt.getSubject(), familyId);
        GetRecipeBooksResDto response = new GetRecipeBooksResDto(recipeBooks);
        return ResponseEntity.ok(response);
    }
    
}
