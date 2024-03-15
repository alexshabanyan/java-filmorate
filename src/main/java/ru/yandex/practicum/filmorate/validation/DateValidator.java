package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateValidator implements ConstraintValidator<ValidDate, LocalDate> {
    LocalDate targetDate;
    Boolean isAfter;

    @Override
    public void initialize(ValidDate constraintAnnotation) {
        targetDate = LocalDate.parse(constraintAnnotation.targetDate());
        isAfter = constraintAnnotation.isAfter();
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        if (targetDate == null || isAfter == null || date == null) return false;
        return (isAfter && date.isAfter(targetDate)) || (!isAfter && date.isBefore(targetDate));
    }
}
