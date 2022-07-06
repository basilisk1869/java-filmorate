package ru.yandex.practicum.filmorate.model;

import javax.validation.Validation;
import javax.validation.Validator;

public class BaseTest {

    protected final Validator validator;

    public BaseTest() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

}
