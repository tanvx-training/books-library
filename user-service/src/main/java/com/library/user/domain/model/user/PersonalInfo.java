package com.library.user.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;

/**
 * Personal information value object
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class PersonalInfo implements Serializable {
    
    private final FirstName firstName;
    private final LastName lastName;
    private final LocalDate dateOfBirth;
    private final Gender gender;
    
    public static PersonalInfo create(String firstName, String lastName, 
                                    LocalDate dateOfBirth, Gender gender) {
        return new PersonalInfo(
            FirstName.of(firstName),
            LastName.of(lastName),
            dateOfBirth,
            gender
        );
    }
    
    public static PersonalInfo create(String firstName, String lastName) {
        return new PersonalInfo(
            FirstName.of(firstName),
            LastName.of(lastName),
            null,
            null
        );
    }
    
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
    
    public String getFullName() {
        return firstName.getValue() + " " + lastName.getValue();
    }
    
    public boolean isMinor() {
        return getAge() < 18;
    }
    
    public boolean isEligibleForLibraryCard() {
        return getAge() >= 13; // Minimum age for library card
    }
}