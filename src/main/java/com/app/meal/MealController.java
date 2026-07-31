package com.app.meal;

import java.time.LocalDate;
import java.util.List;
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
import com.app.family.dto.UpdateRecipeBookReqDto;
import com.app.meal.dto.CreateMealPlanItemReq;
import com.app.meal.dto.CreateMealPlanItemRes;
import com.app.meal.dto.CreateNewShoppingItemReq;
import com.app.meal.dto.CreateNewShoppingItemRes;
import com.app.meal.dto.CreateRecipeBook;
import com.app.meal.dto.GetMealPlansResDto;
import com.app.meal.dto.GetRecipeBooksResDto;
import com.app.meal.dto.GetRecipeDetailResDto;
import com.app.meal.dto.GetRecipesResDto;
import com.app.meal.dto.GetShoppingItemsRes;
import com.app.meal.dto.UpdateMealPlanItemReq;
import com.app.meal.dto.UpdateMealPlanItemRes;
import com.app.meal.dto.UpdateRecipeBookResDto;
import com.app.meal.dto.UpdateRecipeReqDto;
import com.app.meal.dto.UpdateRecipeResDto;
import com.app.meal.dto.UpdateShoppingItemReq;
import com.app.meal.types.MealPlanItem;
import com.app.meal.types.Recipe;
import com.app.meal.types.RecipeBook;
import com.app.meal.types.ShoppingListItem;

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
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws Exception {
        List<MealPlanItem> mealPlans = mealService.getMealPlansForFamilyForDate(jwt.getSubject(), familyId, date);
        GetMealPlansResDto response = new GetMealPlansResDto(mealPlans);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-recipe-detail")
    public ResponseEntity<GetRecipeDetailResDto> getRecipeDetail(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("recipeId") int recipeId) throws Exception {
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
    public ResponseEntity<UpdateRecipeBookResDto> createRecipeBook(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid CreateRecipeBook body) throws Exception {
        RecipeBook book = mealService.createRecipeBook(jwt.getSubject(), body.familyId(), body.name());
        return ResponseEntity.ok(new UpdateRecipeBookResDto(book));
    }

    @PutMapping("update-recipe-book")
    public ResponseEntity<UpdateRecipeBookResDto> updateRecipeBook(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid UpdateRecipeBookReqDto body) throws Exception {
        RecipeBook book = mealService.updateRecipeBook(jwt.getSubject(), body.id(), body.name());
        return ResponseEntity.ok(new UpdateRecipeBookResDto(book));
    }

    @DeleteMapping("delete-recipe-book")
    public ResponseEntity<BaseResponseDto> deleteRecipeBook(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("recipeBookId") int recipeBookId) throws Exception {
        mealService.deleteRecipeBook(jwt.getSubject(), recipeBookId);
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", "Recipe book deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("update-recipe")
    public ResponseEntity<UpdateRecipeResDto> updateRecipe(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody @Valid UpdateRecipeReqDto body) throws Exception
    {
        Recipe recipe = 
            mealService.updateRecipe(
                jwt.getSubject(), 
                body.id(), 
                body.name(), 
                body.description(), 
                body.url());
        return ResponseEntity.ok(new UpdateRecipeResDto(recipe));
    }

    @PostMapping("create-recipe")
    public ResponseEntity<UpdateRecipeResDto> createRecipe(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody @Valid UpdateRecipeReqDto body) throws Exception
    {
        Recipe recipe = 
            mealService.createRecipe(
                jwt.getSubject(), 
                body.name(), 
                body.description(), 
                body.url(), 
                body.recipeBookId());
        return ResponseEntity.ok(new UpdateRecipeResDto(recipe));
    }

    @DeleteMapping("delete-recipe")
    public ResponseEntity<BaseResponseDto> deleteRecipe(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam("recipeId") int recipeId) throws Exception
    {
        mealService.deleteRecipe(jwt.getSubject(), recipeId);
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", "Recipe deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("get-all-recipes-for-family")
    public ResponseEntity<GetRecipesResDto> getAllRecipesForFamily(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam("familyId") String familyId) throws Exception
    {
        List<Recipe> recipes = mealService.getAllRecipesForFamily(jwt.getSubject(), familyId);
        return ResponseEntity.ok(new GetRecipesResDto(recipes));
    }

    @PostMapping("create-meal-plan-item")
    public ResponseEntity<CreateMealPlanItemRes> createNewMealPlanItem(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody @Valid CreateMealPlanItemReq body) throws Exception
    {
        MealPlanItem mealPlanItem = 
            mealService.createMealPlanItem(
                jwt.getSubject(), 
                body.familyId(), 
                body.recipeId(), 
                body.name(), 
                body.date(), 
                body.time(), 
                body.mealType());
        return ResponseEntity.ok(new CreateMealPlanItemRes(mealPlanItem));
    }

    @GetMapping("search-recipes-for-family")
    public ResponseEntity<GetRecipesResDto> searchRecipesForFamily(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam("familyId") String familyId,
        @RequestParam("searchQuery") String searchQuery) throws Exception
    {
        List<Recipe> recipes = mealService.searchRecipesForFamily(jwt.getSubject(), familyId, searchQuery);
        return ResponseEntity.ok(new GetRecipesResDto(recipes));
    }

    @PutMapping("update-meal-plan-item")
    public ResponseEntity<UpdateMealPlanItemRes> updateMealPlanItem(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody @Valid UpdateMealPlanItemReq body) throws Exception
    {
        MealPlanItem mealPlanItem = 
            mealService.updateMealPlanItem(
                jwt.getSubject(), 
                body.mealPlanItemId(), 
                body.recipeId(), 
                body.name(), 
                body.date(), 
                body.time(), 
                body.mealType());
        return ResponseEntity.ok(new UpdateMealPlanItemRes(mealPlanItem));
    }

    @GetMapping("search-recipes")
    public ResponseEntity<GetRecipesResDto> searchRecipes(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam("recipeBookId") int recipeBookId,
        @RequestParam("searchQuery") String searchQuery) throws Exception
    {
        List<Recipe> recipes = mealService.searchRecipes(jwt.getSubject(), recipeBookId, searchQuery);
        return ResponseEntity.ok(new GetRecipesResDto(recipes));
    }

    @DeleteMapping("delete-meal-plan")
    public ResponseEntity<BaseResponseDto> deleteMealPlanItem(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam("mealPlanId") int mealPlanItemId) throws Exception
    {
        mealService.deleteMealPlanItem(jwt.getSubject(), mealPlanItemId);
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", "Meal plan item deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("get-shopping-list")
    public ResponseEntity<GetShoppingItemsRes> getShoppingItems(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam("familyId") String familyId) throws Exception
    {
        Map<Integer, ShoppingListItem> shoppingItems = mealService.getShoppingItems(jwt.getSubject(), familyId);
        return ResponseEntity.ok(new GetShoppingItemsRes(shoppingItems));
    }

    @PutMapping("toggle-item-purchased")
    public ResponseEntity<BaseResponseDto> toggleItemPurchased(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam("itemId") int shoppingItemId) throws Exception
    {
        mealService.toggleItemPurchased(jwt.getSubject(), shoppingItemId);
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", "Item purchased successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("delete-item")
    public ResponseEntity<BaseResponseDto> deleteShoppingListItem(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam("itemId") int shoppingItemId) throws Exception
    {
        mealService.deleteShoppingListItem(jwt.getSubject(), shoppingItemId);
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", "Item deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("create-item")
    public ResponseEntity<CreateNewShoppingItemRes> createShoppingItem(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody @Valid CreateNewShoppingItemReq body) throws Exception
    {
        ShoppingListItem shoppingItem = 
            mealService.createShoppingItem(jwt.getSubject(), body.familyId(), body.quantity(), body.unit(), body.item());
        return ResponseEntity.ok(new CreateNewShoppingItemRes(shoppingItem));
    }

    @PutMapping("update-item")
    public ResponseEntity<BaseResponseDto> updateShoppingItem(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody @Valid UpdateShoppingItemReq body) throws Exception
    {
        mealService.updateShoppingItem(
            jwt.getSubject(),
            body.id(), 
            body.quantity(), 
            body.unit(), 
            body.item()
        );
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", "Item updated successfully");
        return ResponseEntity.ok(response);
    }
}
