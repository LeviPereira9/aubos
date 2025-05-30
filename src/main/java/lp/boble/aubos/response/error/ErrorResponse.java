package lp.boble.aubos.response.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ErrorResponse {
    private String message;
    private int code;
    private String path;
    private LocalDateTime timestamp;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, String> errors;

    public ErrorResponse(String message, int code, String path) {
        this.message = message;
        this.code = code;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message, int code, String path, Map<String, String> errors) {
        this(message, code, path);
        this.errors = errors;
    }
}
