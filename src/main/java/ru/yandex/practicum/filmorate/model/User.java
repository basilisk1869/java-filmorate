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
public class User extends Base {

    //@Pattern(regexp = "@", message = "User.email имеет неверный формат")
    @NotBlank(message = "User.email не может быть пустым.")
    @Email(message = "User.email имеет неверный формат.")
    private String email;

    @NotBlank(message = "User.login не может быть пустым.")
    @Pattern(regexp = "^\\S+$", message = "User.login не может содержать пробелы.")
    private String login;

    @NotNull(message = "User.name не может быть null.")
    private String name;

    @NotNull(message = "User.birthday не может быть null.")
    @Past(message = "User.birthday должно быть в прошлом.")
    LocalDate birthday;

    @AssertTrue
    boolean isNameReplacedByLogin() {
        if (name != null && name.isEmpty()) {
            if (login != null) {
                name = login;
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

}