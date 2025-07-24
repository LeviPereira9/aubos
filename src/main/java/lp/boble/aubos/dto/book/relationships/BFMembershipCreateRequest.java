package lp.boble.aubos.dto.book.relationships;

import java.util.UUID;

public record BFMembershipCreateRequest(
        UUID book_id,
        UUID family_id,
        int orderInFamily,
        String note
) {}
