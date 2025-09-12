package lp.boble.aubos.dto.book.relationships.BookContributor;

import java.util.UUID;

public record BookContributorUpdateBatchRequest(
        UUID contributorId,
        int fromRoleId,
        int toRoleId
) {}
