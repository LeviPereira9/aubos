package lp.boble.aubos.dto.user;

public interface UserAutocompletePageProjection {
        String getUsername();
        String getDisplayName();
        boolean getIsVerified();
        boolean getIsOfficial();
        String getProfilePic();
}
