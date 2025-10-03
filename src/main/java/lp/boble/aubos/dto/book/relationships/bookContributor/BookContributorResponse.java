package lp.boble.aubos.dto.book.relationships.bookContributor;

import java.util.UUID;

public record BookContributorResponse(
        UUID id,
        String name,
        String role
) {}
