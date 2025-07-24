package lp.boble.aubos.dto.book.relationships;

import java.util.UUID;

public record MetricsResponse(
        UUID book_id,
        Long qty_chapters,
        Long qty_views,
        Long qty_favorites
) {}
