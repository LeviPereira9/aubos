package lp.boble.aubos.dto.book.relationships.bookFamily;

import java.util.UUID;

public record BookFamilyCreateRequest(
        UUID bookId,
        int order,
        String note
){}
