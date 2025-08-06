package lp.boble.aubos.dto.book.family;

import java.util.UUID;

public record FamilyResponse (
        UUID id,
        String coverUrl,
        String name,
        String type,
        UUID shareToken,
        String visibility
) {}
