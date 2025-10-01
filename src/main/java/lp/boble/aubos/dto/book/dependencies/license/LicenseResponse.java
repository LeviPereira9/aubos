package lp.boble.aubos.dto.book.dependencies.license;

public record LicenseResponse(
        int id,
        String label,
        String abbreviation,
        String description
) {}
