package lp.boble.aubos.util;

import lombok.Data;

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

    public void addSuccess(UUID bookId, String message){
        success.put(bookId, message);
    }

    public void addFailure(UUID bookId, String message) {
        failures.put(bookId, message);
    }


}
