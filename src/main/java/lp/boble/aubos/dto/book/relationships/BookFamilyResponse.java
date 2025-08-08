package lp.boble.aubos.dto.book.relationships;

import java.util.UUID;

public record BookFamilyResponse (
    UUID book,
    String title,
    int order
){}
