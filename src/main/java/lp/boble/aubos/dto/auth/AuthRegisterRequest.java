package lp.boble.aubos.dto.auth;

import java.time.LocalDate;

public record AuthRegisterRequest(
        String username,
        String displayName,
        String email,
        String password,
        LocalDate dateOfBirth,
        String location){
}
