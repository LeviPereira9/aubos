package lp.boble.aubos.dto.book.dependencies;

import java.util.UUID;

public record ContributorBookResponse(
        UUID id,
        String name,
        String role
) {}
