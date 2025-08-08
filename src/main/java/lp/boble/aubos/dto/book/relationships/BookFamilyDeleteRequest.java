package lp.boble.aubos.dto.book.relationships;

import java.util.UUID;

public record BookFamilyDeleteRequest(
        UUID bookId
) {}
