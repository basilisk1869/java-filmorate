package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

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

    @NotNull
    private LocalDate releaseDate;

    @Positive
    private double duration;

    @AssertTrue(message = "Дата релиза должна быть не раньше 28 декабря 1895 года")
    boolean isReleaseDateNotLaterCinemaBirth() {
        if (releaseDate == null) {
            return true;
        } else {
            LocalDate cinemaBirth = LocalDate.of(1895, 12, 28);
            return releaseDate.isAfter(cinemaBirth) || releaseDate.isEqual(cinemaBirth);
        }
    }

}
