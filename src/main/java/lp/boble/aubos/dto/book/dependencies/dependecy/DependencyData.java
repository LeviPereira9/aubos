package lp.boble.aubos.dto.book.dependencies.dependecy;

import lp.boble.aubos.model.book.dependencies.*;

public record DependencyData(
        LanguageModel language,
        TypeModel type,
        StatusModel status,
        RestrictionModel restriction,
        LicenseModel license
) {}
