package lp.boble.aubos.response.batch;

import lombok.Data;

@Data
public class BatchContent<T> {
    private T id;
    private String message;

    public BatchContent(T id, String message) {
        this.id = id;
        this.message = message;
    }

    public static <T> BatchContent<T> success(T id, String message) {
        return new BatchContent<>(id, message);
    }

    public static <T> BatchContent<T> failure(T id, String message) {
        return new BatchContent<>(id, message);
    }
}
