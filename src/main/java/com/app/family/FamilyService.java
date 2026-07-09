package com.app.family;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.app.family.exceptions.AlreadyMemberException;
import com.app.family.exceptions.FamilyNotFoundException;
import com.app.family.exceptions.OwnerMustBeAdultException;
import com.app.family.exceptions.RequestDoesntExistException;
import com.app.family.exceptions.UnauthorizedException;
import com.app.family.types.Family;
import com.app.family.types.FamilyRole;
import com.app.family.types.JoinRequest;
import com.app.family.types.TruncatedFamily;
import com.app.family.types.TruncatedJoinRequest;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

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
        String userContext = familyDao.userFamilyContext(userId, familyId);
        boolean userAllowedToEditRequest = userAllowedToSeeRequests(userContext);
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
        String userContext = familyDao.userFamilyContext(userId, familyId);
        boolean userAllowedToSeeRequests = userAllowedToSeeRequests(userContext);
        if (!userAllowedToSeeRequests) {
            throw new UnauthorizedException();
        }
        boolean familyExists = familyDao.familyExists(familyId);
        if (!familyExists) {
            throw new FamilyNotFoundException(familyId);
        }
        List<JoinRequest> requests = familyDao.getJoinRequests(familyId);
        for(JoinRequest request : requests) {
            String fullName = getFullName(request.getFullName());
            request.setFullName(fullName);
        }
        return requests;
    }

    public List<TruncatedFamily> getAllAuthFamilies(String userId) {
        List<Family> ownedFamilies = familyDao.getOwnedFamilies(userId);
        List<TruncatedFamily> truncatedOwnedFamilies = new ArrayList<>();
        for (Family ownedFamily : ownedFamilies) {
            truncatedOwnedFamilies.add(new TruncatedFamily(ownedFamily.getFamilyId(), ownedFamily.getFamilyName()));
        }

        List<Family> authFamilies = familyDao.getAuthFamilies(userId);
        List<TruncatedFamily> truncatedAuthFamilies = new ArrayList<>();
        for (Family authFamily : authFamilies) {
            truncatedAuthFamilies.add(new TruncatedFamily(authFamily.getFamilyId(), authFamily.getFamilyName()));
        }

        truncatedOwnedFamilies.addAll(truncatedAuthFamilies);
        System.out.println(truncatedOwnedFamilies.toString());
        return truncatedOwnedFamilies;
    }

    private boolean userAllowedToSeeRequests(String context) {
        return context == "owner" | context == "authUser";
    }
    
    private String getFullName(String fullName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(fullName);
        String displayName = node.get("display_name").asString();
        return displayName;
    }
}
