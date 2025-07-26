package lp.boble.aubos.dto.book.dependencies;

import java.util.UUID;

public record ContributorResponse(
        UUID id,
        String name,
        String role
) {}
