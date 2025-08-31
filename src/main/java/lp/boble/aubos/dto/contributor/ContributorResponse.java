package lp.boble.aubos.dto.contributor;

import java.util.UUID;

public record ContributorResponse(
        UUID id,
        String name
) {}
