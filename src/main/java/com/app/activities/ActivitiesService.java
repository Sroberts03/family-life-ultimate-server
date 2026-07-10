package com.app.activities;

import java.util.List;
import org.springframework.stereotype.Service;
import com.app.activities.types.DetailedActivity;

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
    
}
