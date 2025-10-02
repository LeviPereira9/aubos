package lp.boble.aubos.dto.book.relationships.BookTag;

import java.util.UUID;

public record BookTagResponse(
        UUID id,
        String tag
) {}
