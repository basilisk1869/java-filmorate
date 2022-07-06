package ru.yandex.practicum.filmorate.validator;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotation.AfterOrEqualsDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
public class AfterOrEqualsDateValidator implements ConstraintValidator<AfterOrEqualsDate, LocalDate> {

    private LocalDate targetDate;

    @Override
    public void initialize(AfterOrEqualsDate annotation) {
        targetDate = LocalDate.parse(annotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value != null && !value.isBefore(targetDate);
    }
}