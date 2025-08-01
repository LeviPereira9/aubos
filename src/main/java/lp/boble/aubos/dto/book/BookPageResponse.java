package lp.boble.aubos.dto.book;

import java.util.UUID;

public record BookPageResponse(
    UUID id,
    String coverUrl,
    String title,
    String subtitle,
    String status
) {}
