package lp.boble.aubos.dto.book.relationships.bookContributor;

import java.time.LocalDate;

public record BookContributorUpdateRequest(
        LocalDate startDate,
        LocalDate endDate
) {}
