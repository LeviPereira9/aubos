package lp.boble.aubos.dto.book.relationships.bookFamily;

import java.util.UUID;

public record BookFamilyUpdateRequest(
        UUID id,
        int order,
        String note
) {}
