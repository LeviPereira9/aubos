package lp.boble.aubos.util;

import lombok.Data;
import lp.boble.aubos.response.batch.BatchContent;
import lp.boble.aubos.response.batch.BatchTransporter;

import java.util.*;

@Data
public class ValidationResult<T> {
    private final List<T> validRequests = new ArrayList<>();
    private final List<T> pendentRequests = new ArrayList<>();
    private final Map<UUID, String> failures = new HashMap<>();
    private final Map<UUID, String> success = new HashMap<>();

    public void addValid(T request) {
        validRequests.add(request);
    }

    public void setPendentRequests(Set<T> request) {
        pendentRequests.addAll(request);
    }

    public void addSuccess(UUID id, String message){
        success.put(id, message);
    }

    public void addFailure(UUID id, String message) {
        failures.put(id, message);
    }

    public <T> BatchTransporter<UUID> getSuccessesAndFailures(){

        List<BatchContent<UUID>> successes = new ArrayList<>();
        List<BatchContent<UUID>> falu = new ArrayList<>();

        for(Map.Entry<UUID, String> success: success.entrySet()){
            successes.add(BatchContent.success(success.getKey(), success.getValue()));
        }

        for(Map.Entry<UUID, String> failure: failures.entrySet()){
            falu.add(BatchContent.failure(failure.getKey(), failure.getValue()));
        }

        return new BatchTransporter<>(successes, falu);
    }

}
