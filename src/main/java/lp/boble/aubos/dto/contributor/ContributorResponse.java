package lp.boble.aubos.dto.contributor;

import java.io.Serializable;
import java.util.UUID;

public record ContributorResponse(
        UUID id,
        String name
) implements Serializable {}
