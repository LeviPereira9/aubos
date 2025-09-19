package lp.boble.aubos.dto.book.relationships.BookContributor;

import java.time.LocalDate;
import java.util.UUID;

public record BookContributorUpdateBatchRequest(
        UUID bookContributorId,
        LocalDate startDate,
        LocalDate endDate
) {}
