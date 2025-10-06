package lp.boble.aubos.response.batch;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class BatchResponse<T> {

    private String operation;
    private HttpStatus code;
    private String message;
    private BatchTransporter<T> content;

    public BatchResponse(String operation, HttpStatus code, String message, BatchTransporter<T> content) {
        this.operation = operation;
        this.code = code;
        this.message = message;
        this.content = content;
    }


}
