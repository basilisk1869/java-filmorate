package ru.yandex.practicum.filmorate.annotation;

import ru.yandex.practicum.filmorate.validator.CheckUserDataValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CheckUserDataValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckUserData {

    String message() default "Неверные данные пользователя.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
