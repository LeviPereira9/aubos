package lp.boble.aubos.dto.user;

public interface UserSuggestionPageProjection {
    String getUsername();
    String getDisplayName();
    String getProfilePic();
    String getBio();
    boolean getIsVerified();
    boolean getIsOfficial();

}
