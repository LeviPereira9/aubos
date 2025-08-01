package lp.boble.aubos.dto.book.parts;

import java.util.UUID;

public record BookContributorResponse(
        UUID id,
        String name
) {}
