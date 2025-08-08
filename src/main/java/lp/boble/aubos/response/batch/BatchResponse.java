package lp.boble.aubos.response.batch;

import lombok.Data;

import java.util.List;

@Data
public class BatchResponse<T> {

    private String operation;
    private int code;
    private String message;
    private List<BatchContent<T>> successes;
    private List<BatchContent<T>> failures;

    public BatchResponse(String operation, int code, String message, List<BatchContent<T>> successes, List<BatchContent<T>> failures) {
        this.operation = operation;
        this.code = code;
        this.message = message;
        this.successes = successes;
        this.failures = failures;
    }

    public BatchResponse() {}

}
