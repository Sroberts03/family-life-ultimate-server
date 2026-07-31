package com.app;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.auth.types.PersActivity;
import com.app.family.FamilyDao;

@Service
public class Permissions {

    private final FamilyDao familyDao;

    public Permissions(FamilyDao familyDao) {
        this.familyDao = familyDao;
    }

    public boolean canEdit(String userId, String familyId, String type) throws Exception {
        List<PersActivity> activities = familyDao.userFamilyContext(userId, familyId);
        for (PersActivity activity : activities) {
            if (activity.getActivityName().equals("household_head")
                        || activity.getActivityName().equals("authorized_user")) {
                    return true;
            }
            switch(type) {
                case "recipes":
                    if (activity.getActivityName().equals("edit_recipes")) {
                        return true;
                    }
                    break;
                case "meal_plan":
                    if (activity.getActivityName().equals("edit_meal_plan")) {
                        return true;
                    }
                    break;
                case "editShopping":
                    if (activity.getActivityName().equals("edit_shopping_list")) {
                        return true;
                    }
                    break;
                case "chore":
                    if (activity.getActivityName().equals("edit_chores")) {
                        return true;
                    }
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    public boolean canView(String userId, String familyId, String type) throws Exception {
        List<PersActivity> activities = familyDao.userFamilyContext(userId, familyId);
        for (PersActivity activity : activities) {
            if (activity.getActivityName().equals("household_head")
                        || activity.getActivityName().equals("authorized_user")) {
                    return true;
            }
            switch(type) {
                case "viewShopping":
                    if (activity.getActivityName().equals("view_shopping_list")) {
                        return true;
                    }
                    break;
                default:
                    break;
            }
        }
        return false;
    }
    
}
