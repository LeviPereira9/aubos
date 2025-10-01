package lp.boble.aubos.dto.book.relationships.BookContributor;

import java.time.LocalDate;
import java.util.UUID;

public record BookContributorUpdateRequest(
        LocalDate startDate,
        LocalDate endDate
) {}
