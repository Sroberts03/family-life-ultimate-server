package com.app.meal;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

import com.app.family.FamilyDao;
import com.app.globalExceptions.UnauthorizedException;
import com.app.meal.types.MealPlanItem;

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
}
