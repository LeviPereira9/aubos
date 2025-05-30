package lp.boble.aubos.response.success;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SuccessResponse<T> {

    private String operation;
    private int code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    // Aqui é que se não tiver nada para actions, nem mostra.
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ActionLink> actions;

    public SuccessResponse(String operation, int code, String message, T data) {
        this.operation = operation;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

}
