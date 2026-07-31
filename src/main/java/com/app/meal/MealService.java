package com.app.meal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.app.auth.types.PersActivity;
import com.app.family.FamilyDao;
import com.app.family.exceptions.FamilyNotFoundException;
import com.app.globalExceptions.UnauthorizedException;
import com.app.meal.types.MealPlanItem;
import com.app.meal.types.MealType;
import com.app.meal.types.Recipe;
import com.app.meal.types.RecipeBook;
import com.app.meal.types.ShoppingListItem;

@Service
public class MealService {

    private final MealDao mealDao;
    private final FamilyDao familyDao;

    public MealService(MealDao mealDao, FamilyDao familyDao) {
        this.mealDao = mealDao;
        this.familyDao = familyDao;
    }

    public List<MealPlanItem> getMealPlansForFamilyForDate(String userId, String familyId, LocalDate date)
            throws Exception {
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }
        return mealDao.getMealPlansForFamilyForDate(familyId, date);
    }

    public Recipe getRecipeDetail(String userId, int recipeId) throws Exception {
        List<String> familyIds = mealDao.getFamilyIdFromRecipe(recipeId);
        for (String fId : familyIds) {
            if (familyDao.userIsInFamily(userId, fId)) {
                return mealDao.getRecipeDetail(recipeId);
            }
        }
        throw new UnauthorizedException();
    }

    public List<RecipeBook> getRecipeBooksForFamily(String userId, String familyId) throws Exception {
        boolean familyExists = familyDao.familyExists(familyId);
        if (!familyExists) {
            throw new FamilyNotFoundException(familyId);
        }
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }
        return mealDao.getRecipeBooksForFamily(familyId);
    }

    public List<Recipe> getRecipesForFamily(String userId, int recipeBookId) throws Exception {
        List<String> familyIds = mealDao.getFamilyIdFromRecipeBook(recipeBookId);
        for (String fId : familyIds) {
            if (familyDao.userIsInFamily(userId, fId)) {
                return mealDao.getRecipesForRecipeBook(recipeBookId);
            }
        }
        throw new UnauthorizedException();
    }

    public RecipeBook createRecipeBook(String userId, String familyId, String name) throws Exception {
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }
        List<PersActivity> userCanCreateRecipeBook = familyDao.userFamilyContext(userId, familyId);
        if (!userAllowedToEdit(userCanCreateRecipeBook, "recipes")) {
            throw new UnauthorizedException();
        }
        return mealDao.createRecipeBook(familyId, name);
    }

    public RecipeBook updateRecipeBook(String userId, int recipeBookId, String name) throws Exception {
        List<String> familyIds = mealDao.getFamilyIdFromRecipeBook(recipeBookId);
        boolean allowed = false;
        for (String fId : familyIds) {
            if (familyDao.userIsInFamily(userId, fId)
                    && userAllowedToEdit(familyDao.userFamilyContext(userId, fId), "recipes")) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new UnauthorizedException();
        }
        return mealDao.updateRecipeBook(recipeBookId, name);
    }

    public void deleteRecipeBook(String userId, int recipeBookId) throws Exception {
        List<String> familyIds = mealDao.getFamilyIdFromRecipeBook(recipeBookId);
        boolean allowed = false;
        for (String fId : familyIds) {
            if (familyDao.userIsInFamily(userId, fId)
                    && userAllowedToEdit(familyDao.userFamilyContext(userId, fId), "recipes")) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new UnauthorizedException();
        }
        mealDao.deleteRecipeBook(recipeBookId);
    }

    public Recipe updateRecipe(
            String userId,
            Integer recipeId,
            String name,
            String description,
            String url ) throws Exception {
        List<String> familyIds = mealDao.getFamilyIdFromRecipeBook(mealDao.getRecipeBookIdFromRecipeId(recipeId));
        boolean allowed = false;
        for (String fId : familyIds) {
            if (familyDao.userIsInFamily(userId, fId)
                    && userAllowedToEdit(familyDao.userFamilyContext(userId, fId), "recipes")) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new UnauthorizedException();
        }
        return mealDao.updateRecipe(recipeId, name, description, url);
    }

    public Recipe createRecipe(
        String userId,
        String name,
        String description,
        String url,
        Integer recipeBookId) throws Exception {
        List<String> familyIds = mealDao.getFamilyIdFromRecipeBook(recipeBookId);
        boolean allowed = false;
        for (String fId : familyIds) {
            if (familyDao.userIsInFamily(userId, fId)
                    && userAllowedToEdit(familyDao.userFamilyContext(userId, fId), "recipes")) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new UnauthorizedException();
        }
        return mealDao.createRecipe(name, description, url, recipeBookId);
    }

    public void deleteRecipe(String userId, int recipeId) throws Exception {
        List<String> familyIds = mealDao.getFamilyIdFromRecipeBook(mealDao.getRecipeBookIdFromRecipeId(recipeId));
        boolean allowed = false;
        for (String fId : familyIds) {
            if (familyDao.userIsInFamily(userId, fId)
                    && userAllowedToEdit(familyDao.userFamilyContext(userId, fId), "recipes")) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new UnauthorizedException();
        }
        mealDao.deleteRecipe(recipeId);
    }

    public List<Recipe> getAllRecipesForFamily(String userId, String familyId) throws Exception {
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }
        return mealDao.getAllRecipesForFamily(familyId);
    }

    public MealPlanItem createMealPlanItem(
        String userId,
        String familyId,
        Integer recipeId,
        String name,
        LocalDate date,
        LocalTime time,
        MealType mealType) throws Exception {
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }

        return mealDao.createMealPlanItem(familyId, recipeId, name, date, time, mealType);
    }

    public List<Recipe> searchRecipesForFamily(String userId, String familyId, String searchQuery) throws Exception {
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }
        return mealDao.searchRecipesForFamily(familyId, searchQuery);
    }

    public MealPlanItem updateMealPlanItem(
        String userId,
        int mealPlanItemId,
        Integer recipeId,
        String name,
        LocalDate date,
        LocalTime time,
        MealType mealType) 
    throws Exception {
        String familyId = mealDao.getFamilyIdFromMealPlan(mealPlanItemId);
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }
        List<PersActivity> userCanCreateRecipeBook = familyDao.userFamilyContext(userId, familyId);
        if (!userAllowedToEdit(userCanCreateRecipeBook, "meal_plan")) {
            throw new UnauthorizedException();
        }
        return mealDao.updateMealPlanItem(mealPlanItemId, recipeId, name, date, time, mealType);
    }

    public List<Recipe> searchRecipes(String userId, int recipeBookId, String searchQuery) throws Exception {
        List<String> familyIds = mealDao.getFamilyIdFromRecipeBook(recipeBookId);
        for (String fId : familyIds) {
            if (familyDao.userIsInFamily(userId, fId)) {
                return mealDao.searchRecipes(recipeBookId, searchQuery);
            }
        }
        throw new UnauthorizedException();
    }

    public void deleteMealPlanItem(String userId, int mealPlanItemId) throws Exception {
        String familyId = mealDao.getFamilyIdFromMealPlan(mealPlanItemId);
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }
        List<PersActivity> userRights = familyDao.userFamilyContext(userId, familyId);
        if (!userAllowedToEdit(userRights, "meal_plan")) {
            throw new UnauthorizedException();
        }
        mealDao.deleteMealPlanItem(mealPlanItemId);
    }

    public Map<Integer, ShoppingListItem> getShoppingItems(
            String userId, 
            String familyId) 
    throws Exception{
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }
        List<PersActivity> userRights = familyDao.userFamilyContext(userId, familyId);
        if (!userAllowedToEdit(userRights, "viewShopping")) {
            throw new UnauthorizedException();
        }
        return mealDao.getShoppingItems(familyId);
    }

    public void toggleItemPurchased(String userId, int shoppingItemId) throws Exception {
        String familyId = mealDao.getFamilyIdFromShoppingItem(shoppingItemId);
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }
        List<PersActivity> userRights = familyDao.userFamilyContext(userId, familyId);
        if (!userAllowedToEdit(userRights, "viewShopping")) {
            throw new UnauthorizedException();
        }
        mealDao.toggleItemPurchased(shoppingItemId);
    }

    private boolean userAllowedToEdit(List<PersActivity> context, String type) {
        if (type.equals("recipes")) {
            for (PersActivity activity : context) {
                if (activity.getActivityName().equals("household_head")
                        || activity.getActivityName().equals("authorized_user")
                        || activity.getActivityName().equals("edit_recipes")) {
                    return true;
                }
            }
            return false;
        } else if (type.equals("meal_plan")) {
            for (PersActivity activity : context) {
                if (activity.getActivityName().equals("household_head")
                        || activity.getActivityName().equals("authorized_user")
                        || activity.getActivityName().equals("edit_meal_plan")) {
                    return true;
                }
            }
            return false;
        } else if (type.equals("viewShopping")) {
            for (PersActivity activity : context) {
                if (activity.getActivityName().equals("household_head")
                        || activity.getActivityName().equals("authorized_user")
                        || activity.getActivityName().equals("view_shopping_list")) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

}
