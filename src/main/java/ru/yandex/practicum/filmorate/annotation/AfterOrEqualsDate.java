package ru.yandex.practicum.filmorate.annotation;

import ru.yandex.practicum.filmorate.validator.AfterOrEqualsDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AfterOrEqualsDateValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterOrEqualsDate {

    String message() default "Неверная дата.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value();

}
