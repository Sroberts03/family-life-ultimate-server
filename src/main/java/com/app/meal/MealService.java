package com.app.meal;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import com.app.auth.types.PersActivity;
import com.app.family.FamilyDao;
import com.app.family.exceptions.FamilyNotFoundException;
import com.app.globalExceptions.UnauthorizedException;
import com.app.meal.types.MealPlanItem;
import com.app.meal.types.Recipe;
import com.app.meal.types.RecipeBook;

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
        if (!userAllowedToUpdateRecipeBooks(userCanCreateRecipeBook)) {
            throw new UnauthorizedException();
        }
        return mealDao.createRecipeBook(familyId, name);
    }

    public RecipeBook updateRecipeBook(String userId, int recipeBookId, String name) throws Exception {
        List<String> familyIds = mealDao.getFamilyIdFromRecipeBook(recipeBookId);
        boolean allowed = false;
        for (String fId : familyIds) {
            if (familyDao.userIsInFamily(userId, fId)
                    && userAllowedToUpdateRecipeBooks(familyDao.userFamilyContext(userId, fId))) {
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
                    && userAllowedToUpdateRecipeBooks(familyDao.userFamilyContext(userId, fId))) {
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
                    && userAllowedToUpdateRecipeBooks(familyDao.userFamilyContext(userId, fId))) {
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
                    && userAllowedToUpdateRecipeBooks(familyDao.userFamilyContext(userId, fId))) {
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
                    && userAllowedToUpdateRecipeBooks(familyDao.userFamilyContext(userId, fId))) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new UnauthorizedException();
        }
        mealDao.deleteRecipe(recipeId);
    }

    private boolean userAllowedToUpdateRecipeBooks(List<PersActivity> context) {
        for (PersActivity activity : context) {
            if (activity.getActivityName().equals("household_head")
                    || activity.getActivityName().equals("authorized_user")
                    || activity.getActivityName().equals("edit_recipes")) {
                return true;
            }
        }
        return false;
    }

}
