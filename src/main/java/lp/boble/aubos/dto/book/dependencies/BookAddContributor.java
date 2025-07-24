package lp.boble.aubos.dto.book.dependencies;

import java.util.UUID;

public record BookAddContributor(
        UUID contributorId,
        int contributorRole
) {}
