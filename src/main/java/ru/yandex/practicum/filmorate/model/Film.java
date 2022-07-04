package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.annotation.AfterOrEqualsDate;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Film extends Base {

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @AfterOrEqualsDate(value = "1895-12-28")
    private LocalDate releaseDate;

    @Positive
    private double duration;
}
