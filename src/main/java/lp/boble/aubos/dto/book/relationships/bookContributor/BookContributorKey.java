package lp.boble.aubos.dto.book.relationships.bookContributor;

import java.util.UUID;

public record BookContributorKey(
        UUID contributorId,
        int roleId
) {}
