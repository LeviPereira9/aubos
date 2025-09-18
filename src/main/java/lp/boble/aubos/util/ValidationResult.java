package lp.boble.aubos.util;

import lombok.Data;
import lp.boble.aubos.response.batch.BatchContent;
import lp.boble.aubos.response.batch.BatchTransporter;

import java.util.*;

@Data
public class ValidationResult<K, T> {
    private final List<T> validRequests = new ArrayList<>();
    private final List<T> pendentRequests = new ArrayList<>();
    private final Map<K, String> failures = new HashMap<>();
    private final Map<K, String> success = new HashMap<>();

    public void addValid(T request) {
        validRequests.add(request);
    }

    public void addPendent(T request) {
        pendentRequests.add(request);
    }

    public void setPendentRequests(Set<T> request) {
        pendentRequests.addAll(request);
    }

    public void addSuccess(K id, String message){
        success.put(id, message);
    }

    public void addFailure(K id, String message) {
        failures.put(id, message);
    }

    public BatchTransporter<K> getSuccessesAndFailures(){

        List<BatchContent<K>> successes = new ArrayList<>();
        List<BatchContent<K>> failed = new ArrayList<>();

        for(Map.Entry<K, String> success: success.entrySet()){
            successes.add(BatchContent.success(success.getKey(), success.getValue()));
        }

        for(Map.Entry<K, String> failure: failures.entrySet()){
            failed.add(BatchContent.failure(failure.getKey(), failure.getValue()));
        }

        return new BatchTransporter<>(successes, failed);
    }

}
