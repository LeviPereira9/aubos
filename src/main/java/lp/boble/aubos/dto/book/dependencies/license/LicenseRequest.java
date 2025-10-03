package lp.boble.aubos.dto.book.dependencies.license;

public record LicenseRequest(
        String label,
        String abbreviation,
        String description
) {}
