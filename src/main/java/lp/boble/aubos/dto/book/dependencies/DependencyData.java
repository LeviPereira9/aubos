package lp.boble.aubos.dto.book.dependencies;

import lp.boble.aubos.model.book.dependencies.*;
import lp.boble.aubos.model.book.relationships.BookLanguage;

import java.util.List;

public record DependencyData(
        LanguageModel language,
        TypeModel type,
        StatusModel status,
        RestrictionModel restriction,
        LicenseModel license,
        List<BookLanguage> availableLanguages
) {}
