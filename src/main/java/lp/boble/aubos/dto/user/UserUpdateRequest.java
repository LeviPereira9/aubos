package lp.boble.aubos.dto.user;

import java.time.Instant;
import java.time.LocalDate;

public record UserUpdateRequest(
        String displayName,
        String email,
        String profilePic,
        String bio,
        String location,
        LocalDate dateOfBirth
) {
}
