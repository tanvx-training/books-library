package com.library.user.domain.model.user;

/**
 * Gender enumeration
 */
public enum Gender {
    MALE("Male", "Nam"),
    FEMALE("Female", "Nữ"),
    OTHER("Other", "Khác"),
    PREFER_NOT_TO_SAY("Prefer not to say", "Không muốn tiết lộ");
    
    private final String englishName;
    private final String vietnameseName;
    
    Gender(String englishName, String vietnameseName) {
        this.englishName = englishName;
        this.vietnameseName = vietnameseName;
    }
    
    public String getEnglishName() {
        return englishName;
    }
    
    public String getVietnameseName() {
        return vietnameseName;
    }
    
    public String getDisplayName(String language) {
        if ("vi".equalsIgnoreCase(language)) {
            return vietnameseName;
        }
        return englishName;
    }
}