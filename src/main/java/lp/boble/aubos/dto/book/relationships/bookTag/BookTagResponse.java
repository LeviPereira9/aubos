package lp.boble.aubos.dto.book.relationships.bookTag;

import java.util.UUID;

public record BookTagResponse(
        UUID id,
        String tag
) {}
