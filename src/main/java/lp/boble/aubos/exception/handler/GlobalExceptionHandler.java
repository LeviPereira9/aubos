package lp.boble.aubos.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lp.boble.aubos.exception.custom.apikey.CustomApiKeyValidationException;
import lp.boble.aubos.exception.custom.global.*;
import lp.boble.aubos.response.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomDuplicateFieldException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateFieldException(
            CustomDuplicateFieldException ex, HttpServletRequest request
    ){
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CustomFieldNotProvided.class)
    public ResponseEntity<ErrorResponse> handleFieldNotProvidedException(
            CustomFieldNotProvided ex, HttpServletRequest request
    ){
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CustomNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            CustomNotFoundException ex, HttpServletRequest request
    ){
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CustomDeactivatedException.class)
    public ResponseEntity<ErrorResponse> handleDeactivatedException(
            CustomDeactivatedException ex, HttpServletRequest request
    ){
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(CustomNotModifiedException.class)
    public ResponseEntity<ErrorResponse> handleNotModified(){
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }

    @ExceptionHandler(CustomApiKeyValidationException.class)
    public ResponseEntity<ErrorResponse> handleApiKeyValidationException(
            CustomApiKeyValidationException ex, HttpServletRequest request
    ){
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }


}
