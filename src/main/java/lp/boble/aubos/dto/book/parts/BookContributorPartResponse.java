package lp.boble.aubos.dto.book.parts;

import java.util.UUID;

public record BookContributorPartResponse(
        UUID id,
        String name
) {}
