package lp.boble.aubos.dto.user;

import java.time.LocalDate;
import java.util.Date;

public record UserRegisterRequest(
        String username,
        String displayName,
        String email,
        String password,
        LocalDate dateOfBirth,
        String location){
}
