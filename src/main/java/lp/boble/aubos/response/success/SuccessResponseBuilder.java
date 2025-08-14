package lp.boble.aubos.response.success;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Data
public class SuccessResponseBuilder<T> {
    private String operation;
    private int code;
    private String message;
    private T content;
    private List<ActionLink> actions = new ArrayList<ActionLink>();

    public SuccessResponseBuilder<T> operation(String operation) {
        this.operation = operation;
        return this;
    }

    public SuccessResponseBuilder<T> code(HttpStatus code) {
        this.code = code.value();
        return this;
    }

    public SuccessResponseBuilder<T> message(String message) {
        this.message = message;
        return this;
    }

    public SuccessResponseBuilder<T> content(T data) {
        this.content = data;
        return this;
    }

    public SuccessResponseBuilder<T> action(ActionLink action) {
        this.actions.add(action);
        return this;
    }

    public SuccessResponseBuilder<T> actions(List<ActionLink> actions) {
        this.actions = actions;
        return this;
    }
    public SuccessResponse<T> build() {
        SuccessResponse<T> response = new SuccessResponse<>(operation, code, message, content);
        response.setActions(actions);

        return response;
    }
}
