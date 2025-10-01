package lp.boble.aubos.dto.book.dependencies.dependecy;

import lp.boble.aubos.dto.book.dependencies.license.LicenseResponse;
import lp.boble.aubos.dto.book.dependencies.restriction.RestrictionResponse;
import lp.boble.aubos.dto.book.dependencies.status.StatusResponse;
import lp.boble.aubos.dto.book.dependencies.type.TypeResponse;
import lp.boble.aubos.dto.book.dependencies.language.LanguageResponse;

import java.util.List;

public record DependencyResponse(
        List<LanguageResponse> languages,
        List<LicenseResponse> licenses,
        List<RestrictionResponse> restrictions,
        List<StatusResponse> status,
        List<TypeResponse> types
) {}
