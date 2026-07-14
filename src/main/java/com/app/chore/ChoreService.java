package com.app.chore;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import jakarta.annotation.Nullable;
import com.app.auth.types.PersActivity;
import com.app.chore.exceptions.ChoreNotFoundException;
import com.app.chore.types.Chore;
import com.app.family.FamilyDao;
import com.app.family.exceptions.FamilyNotFoundException;
import com.app.globalExceptions.UnauthorizedException;

@Service
public class ChoreService {

    private final ChoreDao choreDao;
    private final FamilyDao familyDao;

    public ChoreService(ChoreDao choreDao, FamilyDao familyDao) {
        this.choreDao = choreDao;
        this.familyDao = familyDao;
    }

    public Map<Integer, Chore> getAllChoresForFamilyForDate(String userId, String familyId, LocalDate date) throws Exception {
        boolean familyExistsCheck = familyDao.familyExists(familyId);
        if (!familyExistsCheck) {
            throw new FamilyNotFoundException(familyId);
        }
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }
        List<PersActivity> userContext = familyDao.userFamilyContext(userId, familyId);
        boolean userCanViewAllChores = userCanEdit(userContext);
        List<Chore> chores = choreDao.getAllChoresForFamilyForDate(familyId, date);
        Map<Integer, Chore> choresMap = new HashMap<>();
        if (userCanViewAllChores) {
            for (Chore chore : chores) {
                choresMap.put(chore.getId(), chore);
            }
        }
        if (!userCanViewAllChores) {
            for (Chore chore : chores) {
                if (chore.getAssigneeIds() != null 
                    && chore.getAssigneeIds().contains(userId)) {
                    choresMap.put(chore.getId(), chore);
                }
            }
        }
        
        return choresMap;
    }

    public void markChoreComplete(String userId, Date dateCompleted, int choreId) throws Exception {
        boolean choreExists = choreDao.choreExists(choreId);
        if (!choreExists) {
            throw new ChoreNotFoundException(choreId);
        }
        String familyId = choreDao.getFamilyIdFromChoreId(choreId);
        if (familyId == null) {
            throw new ChoreNotFoundException(choreId);
        }
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }
        choreDao.markChoreComplete(dateCompleted, choreId);
    }

    public Chore createChore(
        String userId, 
        String familyId, 
        String name, 
        String description, 
        String recurring, 
        String startDate, 
        @Nullable String endDate
    ) throws Exception {
        System.out.println("Reached here");
        boolean familyExistsCheck = familyDao.familyExists(familyId);
        if (!familyExistsCheck) {
            throw new FamilyNotFoundException(familyId);
        }
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }
        List<PersActivity> userContext = familyDao.userFamilyContext(userId, familyId);
        boolean userCanAddChore = userCanEdit(userContext);
        if (!userCanAddChore) {
            throw new UnauthorizedException();
        }
        int choreTemplateId = choreDao.createChoreTemplate(familyId, name, description, recurring, LocalDate.parse(startDate), endDate != "" ? LocalDate.parse(endDate) : null);
        return choreDao.getChoreFromTemplateIdAndDueDate(choreTemplateId, LocalDate.parse(startDate));
    }

    public void deleteChore(
        String userId,
        int choreId,
        boolean thisAndFuture
    ) throws Exception {
        boolean choreExists = choreDao.choreExists(choreId);
        if (!choreExists) {
            throw new ChoreNotFoundException(choreId);
        }
        String familyId = choreDao.getFamilyIdFromChoreId(choreId);
        if (familyId == null) {
            throw new ChoreNotFoundException(choreId);
        }
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }
        List<PersActivity> userContext = familyDao.userFamilyContext(userId, familyId);
        boolean userCanDeleteChore = userCanEdit(userContext);
        if (!userCanDeleteChore) {
            throw new UnauthorizedException();
        }
        choreDao.deleteChore(choreId, thisAndFuture);
    }

    private boolean userCanEdit(List<PersActivity> userActivities) {
        for (PersActivity activity : userActivities) {
            if (activity.getActivityName().equals("household_head")
                    || activity.getActivityName().equals("authorized_user")
                    || activity.getActivityName().equals("edit_chores")) {
                return true;
            }
        }
        return false;
    }
    
}
