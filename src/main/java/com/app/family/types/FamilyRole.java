package com.app.family.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FamilyRole {
    
    @JsonProperty("adult")
    ADULT,
    
    @JsonProperty("child")
    CHILD,
    
    @JsonProperty("other")
    OTHER
}