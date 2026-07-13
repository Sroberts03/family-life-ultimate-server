package com.app.family;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import com.app.auth.types.PersActivity;
import com.app.family.exceptions.AlreadyMemberException;
import com.app.family.exceptions.FamilyNotFoundException;
import com.app.family.exceptions.MemberNotFoundException;
import com.app.family.exceptions.OwnerMustBeAdultException;
import com.app.family.exceptions.RequestDoesntExistException;
import com.app.family.types.Family;
import com.app.family.types.FamilyMember;
import com.app.family.types.FamilyRole;
import com.app.family.types.JoinRequest;
import com.app.family.types.TruncatedFamily;
import com.app.family.types.TruncatedFamilyMember;
import com.app.family.types.TruncatedJoinRequest;
import com.app.globalExceptions.UnauthorizedException;

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

    public void acceptOrDenyRequest(String userId, int requestId, boolean accept) throws Exception {
        boolean requestExistsCheck = familyDao.requestExists(requestId);
        if (!requestExistsCheck) {
            throw new RequestDoesntExistException(requestId);
        }
        String familyId = familyDao.getRequestFamilyId(requestId);
        List<PersActivity> userContext = familyDao.userFamilyContext(userId, familyId);
        boolean userAllowedToEditRequest = userAllowedAuthAction(userContext);
        if (!userAllowedToEditRequest) {
            throw new UnauthorizedException();
        }
        familyDao.acceptOrDenyRequest(requestId, accept);
        if (accept == true) {
            TruncatedJoinRequest request = familyDao.getRequest(requestId);
            familyDao.addUserToFamily(request.getUserId(), request.getFamilyId(), request.getRole());
        }
    }

    public List<JoinRequest> getJoinRequests(String userId, String familyId) throws Exception {
        List<PersActivity> userContext = familyDao.userFamilyContext(userId, familyId);
        boolean userAllowedAuthAction = userAllowedAuthAction(userContext);
        if (!userAllowedAuthAction) {
            throw new UnauthorizedException();
        }
        boolean familyExists = familyDao.familyExists(familyId);
        if (!familyExists) {
            throw new FamilyNotFoundException(familyId);
        }
        List<JoinRequest> requests = familyDao.getJoinRequests(familyId);
        return requests;
    }

    public List<TruncatedFamily> getAllAuthFamilies(String userId) {
        List<Family> authFamilies = familyDao.getAuthFamilies(userId);
        List<TruncatedFamily> truncatedAuthFamilies = new ArrayList<>();
        for (Family authFamily : authFamilies) {
            truncatedAuthFamilies.add(new TruncatedFamily(authFamily.getFamilyId(), authFamily.getFamilyName()));
        }
        return truncatedAuthFamilies;
    }

    public List<FamilyMember> getAllFamilyMembers(String familyId, String userId) throws Exception {
        List<FamilyMember> familyMembers = new ArrayList<>();
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }
        boolean familyExists = familyDao.familyExists(familyId);
        if (!familyExists) {
            throw new FamilyNotFoundException(familyId);
        }
        List<TruncatedFamilyMember> members = familyDao.getAllFamilyMembers(familyId);
        for (TruncatedFamilyMember member : members) {
            List<PersActivity> activities = familyDao.getAllPersonActivitiesForFamily(member.getUserId(), familyId);
            FamilyMember familyMember = new FamilyMember(member.getUserId(), member.getFullName(), member.getRole(), activities);
            familyMembers.add(familyMember);
        }
        return familyMembers;
    }

    public void removeMember(String userId, String familyId, String memberId) throws Exception {
        boolean userInFamily = familyDao.userIsInFamily(userId, familyId);
        if (!userInFamily) {
            throw new UnauthorizedException();
        }
        boolean familyExists = familyDao.familyExists(familyId);
        if (!familyExists) {
            throw new FamilyNotFoundException(familyId);
        }
        if (userId.equals(memberId)) {
            familyDao.removeUserFromFamily(userId, familyId);
            return;
        }
        boolean memberInFamily = familyDao.userIsInFamily(memberId, familyId);
        if (!memberInFamily) {
            throw new MemberNotFoundException(memberId, familyId);
        }
        List<PersActivity> userContext = familyDao.userFamilyContext(userId, familyId);
        boolean userAllowedToRemoveMembers = userAllowedAuthAction(userContext);
        if (!userAllowedToRemoveMembers) {
            throw new UnauthorizedException();
        }
        List<PersActivity> memberContext = familyDao.userFamilyContext(memberId, familyId);
        for (PersActivity activity : memberContext) {
            if (activity.getActivityName().equals("household_head")) {
                throw new UnauthorizedException();
            }
        }
        familyDao.removeUserFromFamily(memberId, familyId);
    }

    private boolean userAllowedAuthAction(List<PersActivity> context) {
        for (PersActivity activity : context) {
            if (activity.getActivityName().equals("household_head")
                    || activity.getActivityName().equals("authorized_user")) {
                return true;
            }
        }
        return false;
    }
}
