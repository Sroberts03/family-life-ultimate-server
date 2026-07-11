package com.app.activities;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.app.activities.types.DetailedActivity;
import com.app.auth.types.PersActivity;
import com.app.globalExceptions.UnauthorizedException;

@Service
public class ActivitiesService {

    private ActivitiesDao activitiesDao;

    public ActivitiesService(ActivitiesDao activitiesDao) {
        this.activitiesDao = activitiesDao;
    }

    public List<DetailedActivity> getAllActivities() {
        List<DetailedActivity> activities = activitiesDao.getAllActivities();
        return activities;
    }
    
    public void updateUserActivities(String userId, String updatedUserId, String familyId, Map<Integer, Boolean> permissions) throws Exception {
        List<PersActivity> reqUserActivities = activitiesDao.getUserActivities(userId, familyId);
        boolean userCanEditFamilyActivities = userCanEditFamilyActivities(reqUserActivities, permissions);
        if (!userCanEditFamilyActivities) {
            throw new UnauthorizedException();
        }
        activitiesDao.updateUserActivities(updatedUserId, familyId, permissions);
    }

    private boolean userCanEditFamilyActivities(List<PersActivity> reqUserActivities, Map<Integer, Boolean> permissionsToChange) {
       for (PersActivity activity : reqUserActivities) {
            if (activity.getActivityName().equals("household_head")) {
                return true;
            }
            else if (activity.getActivityName().equals("authorized_user")) {
                List<String> permissionNames = activitiesDao.getAllActivitiesNamesFromListOfIds(permissionsToChange.keySet());
                if (!permissionNames.contains("household_head")) {
                    return true;
                }
            }
        }
       return false;
    }
}
