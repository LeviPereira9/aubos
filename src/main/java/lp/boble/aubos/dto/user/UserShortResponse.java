package lp.boble.aubos.dto.user;

import java.time.Instant;

public record UserShortResponse(
        String username,
        String displayName,
        String profilePic,
        String location,
        String status,
        Instant joinDate,
        boolean isVerified,
        boolean isOfficial,
        String role
) {
}
