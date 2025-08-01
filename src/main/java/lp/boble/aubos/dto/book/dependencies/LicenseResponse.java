package lp.boble.aubos.dto.book.dependencies;

public record LicenseResponse(
        int id,
        String label,
        String abbreviation,
        String description
) {}
