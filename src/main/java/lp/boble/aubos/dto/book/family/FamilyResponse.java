package lp.boble.aubos.dto.book.family;

import java.util.UUID;

public record FamilyResponse (
        UUID id,
        String name,
        String type
) {}
