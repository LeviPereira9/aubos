package lp.boble.aubos.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lp.boble.aubos.exception.custom.auth.CustomForbiddenActionException;
import lp.boble.aubos.exception.custom.auth.CustomHashGenerationException;
import lp.boble.aubos.exception.custom.auth.CustomPasswordException;
import lp.boble.aubos.exception.custom.auth.CustomTokenException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.response.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(CustomForbiddenActionException.class)
    public ResponseEntity<ErrorResponse> handleCustomForbiddenActionException(CustomForbiddenActionException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(CustomPasswordException.class)
    public ResponseEntity<ErrorResponse> handleCustomPasswordException(CustomPasswordException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(CustomHashGenerationException.class)
    public ResponseEntity<ErrorResponse> handleCustomHashGenerationException(CustomHashGenerationException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(CustomTokenException.class)
    public ResponseEntity<ErrorResponse> handleCustomTokenException(
            CustomTokenException ex, HttpServletRequest request
    ){
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    };

}
