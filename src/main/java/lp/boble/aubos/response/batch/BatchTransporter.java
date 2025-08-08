package lp.boble.aubos.response.batch;

import lombok.Data;

import java.util.List;

@Data
public class BatchTransporter<T> {
    List<BatchContent<T>> successes;
    List<BatchContent<T>> failures;


    public BatchTransporter(List<BatchContent<T>> successes, List<BatchContent<T>> failures) {
        this.successes = successes;
        this.failures = failures;
    }

    public int getStatus(){
        boolean hasFailure = !failures.isEmpty();
        boolean hasSuccess = !successes.isEmpty();
        return hasFailure && hasSuccess ? 207 : hasFailure ? 400 : 200;
    }
}
