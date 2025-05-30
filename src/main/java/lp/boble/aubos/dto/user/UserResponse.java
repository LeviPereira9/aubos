package lp.boble.aubos.dto.user;

import java.time.Instant;

public record UserResponse(
        String username,
        String displayName,
        String email,
        String profilePic,
        String bio,
        Instant joinDate,
        String location,
        String status,
        boolean isVerified,
        boolean isOfficial,
        String role
) {
}
