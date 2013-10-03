package com.amediamanager.domain;

public enum Privacy {
    
    PRIVATE("Private"), 
    PUBLIC("Public"), 
    SHARED("Shared");

    
    public static final Privacy[] ALL = { PRIVATE, PUBLIC, SHARED };
        
    private final String privacyLevel;

    public static Privacy fromName(final String privacyLevel) {
        if (null == privacyLevel) {
            throw new IllegalArgumentException("Null is not a valid value for Privacy");
        }
        if (privacyLevel.toUpperCase().equals("PRIVATE")) {
            return PRIVATE;
        } else if (privacyLevel.toUpperCase().equals("PUBLIC")) {
            return PUBLIC;
        } else if (privacyLevel.toUpperCase().equals("SHARED")) {
            return SHARED;
        }
        throw new IllegalArgumentException("\"" + privacyLevel + "\" is invalid");
    }
    
    private Privacy(final String privacyLevel) {
        this.privacyLevel = privacyLevel;
    }
    
    public String getPrivacyLevel() {
        return this.privacyLevel;
    }
    
    @Override
    public String toString() {
        return getPrivacyLevel();
    }
    
}
