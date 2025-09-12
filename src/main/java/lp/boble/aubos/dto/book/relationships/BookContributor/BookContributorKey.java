package lp.boble.aubos.dto.book.relationships.BookContributor;

import java.util.UUID;

public record BookContributorKey(
        UUID contributorId,
        int roleId
) {}
