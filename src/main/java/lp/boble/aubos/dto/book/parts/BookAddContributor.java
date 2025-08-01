package lp.boble.aubos.dto.book.parts;

import java.util.UUID;

public record BookAddContributor(
        UUID contributorId,
        int contributorRoleId
) {}
