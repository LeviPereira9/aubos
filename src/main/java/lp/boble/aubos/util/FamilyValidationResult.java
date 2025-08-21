package lp.boble.aubos.util;

import lombok.Data;
import lp.boble.aubos.dto.book.relationships.BookFamilyCreateRequest;

import java.util.*;

@Data
public class FamilyValidationResult {
    private final List<BookFamilyCreateRequest> validRequests = new ArrayList<>();
    private final Map<UUID, String> failures = new HashMap<>();
    private final List<UUID> success = new ArrayList<>();

    public void addValid(BookFamilyCreateRequest request) {
        validRequests.add(request);
        success.add(request.bookId());
    }

    public void addFailure(UUID bookId, String message) {
        failures.put(bookId, message);
    }


}
