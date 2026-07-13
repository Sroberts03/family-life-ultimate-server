package com.app.chore;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
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

    public Map<Integer, Chore> getAllChoresForFamilyForDate(String userId, String familyId, Instant date) throws Exception {
        boolean familyExistsCheck = familyDao.familyExists(familyId);
        if (!familyExistsCheck) {
            throw new FamilyNotFoundException(familyId);
        }
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }
        List<Chore> chores = choreDao.getAllChoresForFamilyForDate(familyId, date);
        System.out.println("CHORES DEBUG: " + chores);
        Map<Integer, Chore> choresMap = new HashMap<>();
        for (Chore chore : chores) {
            choresMap.put(chore.getId(), chore);
        }
        return choresMap;
    }
    
}
