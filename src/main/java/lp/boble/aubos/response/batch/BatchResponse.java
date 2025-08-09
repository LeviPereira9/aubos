package lp.boble.aubos.response.batch;

import lombok.Data;

import java.util.List;

@Data
public class BatchResponse<T> {

    private String operation;
    private int code;
    private String message;
    private BatchTransporter<T> data;

    public BatchResponse(String operation, int code, String message, BatchTransporter<T> data) {
        this.operation = operation;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BatchResponse() {}

}
