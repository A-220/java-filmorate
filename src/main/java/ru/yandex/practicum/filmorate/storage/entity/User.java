package ru.yandex.practicum.filmorate.storage.entity;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.api.errors.annotations.BirthdayDate;
import ru.yandex.practicum.filmorate.api.errors.annotations.NoWhiteSpace;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@NotNull(message = "User cannot be null")
public class User {

    private Long id;

    @NotNull(message = "Email cannot be null")
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Login cannot be null")
    @NotEmpty(message = "Login cannot be empty")
    @NoWhiteSpace
    private String login;

    private String name;

    @NotNull(message = "Birthday cannot be null")
    @BirthdayDate
    private LocalDate birthday;

    private final Map<Long, String> friendStatus = new HashMap<>();

    private final Set<Long> friends = new HashSet<>();

    public void deleteFriend(Long id) {
        friendStatus.remove(id);
    }

    public void setFriendStatus(Long id, String status) {
        friendStatus.put(id, status);
    }

}
