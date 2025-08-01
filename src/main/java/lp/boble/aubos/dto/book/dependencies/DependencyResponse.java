package lp.boble.aubos.dto.book.dependencies;

import java.util.List;

public record DependencyResponse(
        List<LanguageResponse> languages,
        List<LicenseResponse> licenses,
        List<RestrictionResponse> restrictions,
        List<StatusResponse> status,
        List<TypeResponse> types
) {}
