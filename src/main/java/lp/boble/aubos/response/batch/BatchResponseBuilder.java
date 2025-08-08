package lp.boble.aubos.response.batch;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BatchResponseBuilder<T> {
    private String operation;
    private int code;
    private String message;
    private List<BatchContent<T>> successes;
    private List<BatchContent<T>> failures;

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
    public BatchResponseBuilder<T> successes(List<BatchContent<T>> successes) {
        this.successes = successes;
        return this;
    }
    public BatchResponseBuilder<T> failures(List<BatchContent<T>> failures) {
        this.failures = failures;
        return this;
    }

    public BatchResponse<T> build() {
        return new BatchResponse<>(operation, code, message, successes, failures);
    }
}
