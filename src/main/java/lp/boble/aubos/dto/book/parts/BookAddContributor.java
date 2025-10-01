package lp.boble.aubos.dto.book.parts;

import java.time.LocalDate;
import java.util.UUID;

public record BookAddContributor(
        UUID contributorId,
        int contributorRoleId,
        LocalDate startDate,
        LocalDate endDate
) {}
