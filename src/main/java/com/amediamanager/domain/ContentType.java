package com.amediamanager.domain;

public enum ContentType {
    
    MP4("video/mp4");

    public static final ContentType[] ALL = { MP4 };
        
    private final String contentType;

    public static ContentType fromName(final String contentType) {
        if (null == contentType) {
            throw new IllegalArgumentException("Null is not a valid value for ContentType");
        }
        if (contentType.toUpperCase().equals("MP4")) {
            return MP4;
        }
        throw new IllegalArgumentException("\"" + contentType + "\" is invalid");
    }
    
    private ContentType(final String contentType) {
        this.contentType = contentType;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    @Override
    public String toString() {
        return getContentType();
    }
    
}
