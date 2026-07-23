package com.app.meal;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

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

    public List<MealPlanItem> getMealPlansForFamilyForDate(String userId, String familyId, LocalDate date) throws Exception {
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

}
