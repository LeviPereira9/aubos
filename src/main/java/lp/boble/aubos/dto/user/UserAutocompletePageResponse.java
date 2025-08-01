package lp.boble.aubos.dto.user;

public record UserAutocompletePageResponse(
        String profilePic,
        String username,
        String displayName,
        boolean isVerified,
        boolean isOfficial
) {}
