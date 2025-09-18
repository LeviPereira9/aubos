package lp.boble.aubos.response.batch;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class BatchResponseBuilder<T> {
    private String operation;
    private int code;
    private String message;
    private BatchTransporter<T> content;

    public BatchResponseBuilder<T> operation(String operation) {
        this.operation = operation;
        return this;
    }
    public BatchResponseBuilder<T> code(int code) {
        this.code = code;
        return this;
    }
    public BatchResponseBuilder<T> message(String message) {
        this.message = message;
        return this;
    }
    public BatchResponseBuilder<T> content(BatchTransporter<T> content ) {
        this.content = content;
        return this;
    }

    public BatchResponse<T> build() {
        return new BatchResponse<>(operation, code, message, content);
    }
}
