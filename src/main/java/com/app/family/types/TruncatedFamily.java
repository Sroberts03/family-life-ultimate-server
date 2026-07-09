package com.app.family.types;

public class TruncatedFamily {
    private String familyId;
    private String familyName;
    
    public TruncatedFamily(String familyId, String familyName) {
        this.familyId = familyId;
        this.familyName = familyName;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @Override
    public String toString() {
        return "TruncatedFamily [familyId=" + familyId + ", familyName=" + familyName + "]";
    }    
}
