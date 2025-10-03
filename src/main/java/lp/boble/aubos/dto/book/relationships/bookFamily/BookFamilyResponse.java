package lp.boble.aubos.dto.book.relationships.bookFamily;

import java.util.UUID;

public record BookFamilyResponse (
    UUID bookId,
    String title,
    int order
){}
