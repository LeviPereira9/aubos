package lp.boble.aubos.dto.book.dependencies.restriction;

public record RestrictionCreateRequest(
        int age,
        String description
) {}
