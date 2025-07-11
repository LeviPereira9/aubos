package lp.boble.aubos.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lp.boble.aubos.exception.custom.email.CustomEmailException;
import lp.boble.aubos.response.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class EmailExceptionHandler {
    @ExceptionHandler(CustomEmailException.class)
    public ResponseEntity<ErrorResponse> customEmailExceptionHandler
            (CustomEmailException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
}
