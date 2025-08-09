package lp.boble.aubos.response.batch;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BatchResponseBuilder<T> {
    private String operation;
    private int code;
    private String message;
    private BatchTransporter<T> data;

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
    public BatchResponseBuilder<T> data(BatchTransporter<T> data ) {
        this.data = data;
        return this;
    }

    public BatchResponse<T> build() {
        return new BatchResponse<>(operation, code, message, data);
    }
}
