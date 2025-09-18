package lp.boble.aubos.response.batch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class BatchTransporter<T> {
    List<BatchContent<T>> successes;
    List<BatchContent<T>> failures;


    public BatchTransporter(List<BatchContent<T>> successes, List<BatchContent<T>> failures) {
        this.successes = successes;
        this.failures = failures;
    }

    @JsonIgnore
    public Integer getStatus(){
        boolean hasFailure = !failures.isEmpty();
        boolean hasSuccess = !successes.isEmpty();
        return hasFailure && hasSuccess ? HttpStatus.MULTI_STATUS.value() : hasFailure ? HttpStatus.BAD_REQUEST.value() : HttpStatus.OK.value();
    }
}
