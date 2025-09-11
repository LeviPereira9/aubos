package lp.boble.aubos.dto.book.relationships.BookFamily;

import java.util.UUID;

public record BookFamilyUpdateRequest(
        UUID bookId,
        int order,
        String note
) {}
