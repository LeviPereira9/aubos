package lp.boble.aubos.dto.book.relationships.BookFamily;

import java.util.UUID;

public record BookFamilyResponse (
    UUID bookId,
    String title,
    int order
){}
