package lp.boble.aubos.dto.book.family;

public record FamilyRequest (
        String coverUrl,
        String name,
        int type,
        int visibility
) {}
