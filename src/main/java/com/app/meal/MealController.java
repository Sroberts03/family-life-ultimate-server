package com.app.meal;

import java.time.LocalDate;
import java.util.List;
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
import com.app.family.dto.UpdateRecipeBookReqDto;
import com.app.meal.dto.CreateRecipeBook;
import com.app.meal.dto.GetMealPlansResDto;
import com.app.meal.dto.GetRecipeBooksResDto;
import com.app.meal.dto.GetRecipeDetailResDto;
import com.app.meal.dto.GetRecipesResDto;
import com.app.meal.dto.UpdateRecipeBookResDto;
import com.app.meal.types.MealPlanItem;
import com.app.meal.types.Recipe;
import com.app.meal.types.RecipeBook;

import jakarta.validation.Valid;

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

    @GetMapping("/get-recipes")
    public ResponseEntity<GetRecipesResDto> getRecipesForFamily(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("recipeBookId") int recipeBookId) throws Exception {
        List<Recipe> recipes = mealService.getRecipesForFamily(jwt.getSubject(), recipeBookId);
        GetRecipesResDto response = new GetRecipesResDto(recipes);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-recipe-book")
    public ResponseEntity<UpdateRecipeBookResDto> createRecipeBook (
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody @Valid CreateRecipeBook body
    ) throws Exception {
        RecipeBook book = mealService.createRecipeBook(jwt.getSubject(), body.familyId(), body.name());
        return ResponseEntity.ok(new UpdateRecipeBookResDto(book));
    }

    @PutMapping("update-recipe-book")
    public ResponseEntity<UpdateRecipeBookResDto> updateRecipeBook (
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody @Valid UpdateRecipeBookReqDto body
    ) throws Exception {
        RecipeBook book = mealService.updateRecipeBook(jwt.getSubject(), body.id(), body.name());
        return ResponseEntity.ok(new UpdateRecipeBookResDto(book));
    }

    @DeleteMapping("delete-recipe-book")
    public ResponseEntity<BaseResponseDto> deleteRecipeBook (
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam("recipeBookId") int recipeBookId
    ) throws Exception {
        mealService.deleteRecipeBook(jwt.getSubject(), recipeBookId);
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", "Recipe book deleted successfully");
        return ResponseEntity.ok(response);
    }
}
