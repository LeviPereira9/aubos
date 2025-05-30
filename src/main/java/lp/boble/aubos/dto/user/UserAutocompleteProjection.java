package lp.boble.aubos.dto.user;

public interface UserAutocompleteProjection{
        String getUsername();
        String getDisplayName();
        boolean getIsVerified();
        boolean getIsOfficial();
        String getProfilePic();
}
