package com.library.book.domain.model.bookcopy;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class Location {
    private String value;
    
    private Location(String value) {
        this.value = value;
    }
    
    public static Location of(String location) {
        return new Location(location);
    }
    
    public static Location empty() {
        return new Location(null);
    }
    
    @Override
    public String toString() {
        return value != null ? value : "";
    }
} 