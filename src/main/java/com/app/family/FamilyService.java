package com.app.family;

import org.springframework.stereotype.Service;

import com.app.family.exceptions.AlreadyMemberException;
import com.app.family.exceptions.FamilyNotFoundException;
import com.app.family.exceptions.OwnerMustBeAdultException;
import com.app.family.types.FamilyRole;

@Service
public class FamilyService {

    private final FamilyDao familyDao;

    public FamilyService(FamilyDao familyDao) {
        this.familyDao = familyDao;
    }

    public void requestJoin(String userId, String familyId, FamilyRole role) throws Exception {
        boolean validFamilyId = familyId.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        if (!validFamilyId) {
            throw new FamilyNotFoundException(familyId);
        }
        boolean familyExistsCheck = familyDao.familyExists(familyId);
        if (!familyExistsCheck) {
            throw new FamilyNotFoundException(familyId);
        }
        boolean alreadyInFamilyCheck = familyDao.userIsInFamily(userId, familyId);
        if (alreadyInFamilyCheck) {
            throw new AlreadyMemberException(familyId);
        }
        familyDao.requestJoin(userId, familyId, role);
    }

    public void createFamily(String userId, FamilyRole role) throws OwnerMustBeAdultException {
        if (role != FamilyRole.ADULT) {
            throw new OwnerMustBeAdultException();
        }
        familyDao.createFamily(userId, role);
    }
    
}
