package lp.boble.aubos.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lp.boble.aubos.exception.custom.user.CustomEmailAlreadyVerified;
import lp.boble.aubos.exception.custom.user.CustomUserBannedException;
import lp.boble.aubos.response.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(CustomEmailAlreadyVerified.class)
    public ResponseEntity<ErrorResponse> handleCustomEmailAlreadyVerified(CustomEmailAlreadyVerified ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(), HttpStatus.CONFLICT.value(), request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(CustomUserBannedException.class)
    public ResponseEntity<ErrorResponse> handleCustomUserBanned(CustomUserBannedException ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(), HttpStatus.FORBIDDEN.value(), request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

}
