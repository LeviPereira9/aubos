package lp.boble.aubos.dto.contributor;

import java.io.Serializable;
import java.util.UUID;

public record ContributorPageResponse(
        UUID id,
        String name
) implements Serializable {}
