package com.library.book.utils.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.Objects;

public class ValidYearValidator implements ConstraintValidator<ValidYear, Integer> {
    @Override
    public boolean isValid(Integer year, ConstraintValidatorContext context) {
        if (Objects.isNull(year)) {
            return true;
        }
        int currentYear = LocalDate.now().getYear();
        return year >= 1000 && year <= currentYear;
    }
}
