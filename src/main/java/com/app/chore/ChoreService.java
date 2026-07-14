package com.app.chore;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
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
        List<Chore> chores = choreDao.getAllChoresForFamilyForDate(familyId, date);
        Map<Integer, Chore> choresMap = new HashMap<>();
        for (Chore chore : chores) {
            choresMap.put(chore.getId(), chore);
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
    
}
