package lp.boble.aubos.dto.user;

import java.time.Instant;

public interface UserSuggestionProjection{
    String getUsername();
    String getDisplayName();
    String getProfilePic();
    String getBio();
    boolean getIsVerified();
    boolean getIsOfficial();
    Instant getLastLogin();

}
