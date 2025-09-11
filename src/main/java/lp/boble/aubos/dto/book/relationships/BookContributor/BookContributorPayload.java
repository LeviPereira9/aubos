package lp.boble.aubos.dto.book.relationships.BookContributor;

import lp.boble.aubos.model.book.dependencies.ContributorModel;
import lp.boble.aubos.model.book.dependencies.ContributorRole;

public record BookContributorPayload(
        ContributorModel contributor,
        ContributorRole role
) {}
