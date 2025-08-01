package lp.boble.aubos.dto.user;

public record UserSuggestionPageResponse(
        String username,
        String displayName,
        String profilePic,
        String bio,
        boolean isVerified,
        boolean isOfficial
) {}
