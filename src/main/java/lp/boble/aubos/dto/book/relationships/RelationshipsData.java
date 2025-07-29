package lp.boble.aubos.dto.book.relationships;

import lp.boble.aubos.model.book.relationships.BookContributor;
import lp.boble.aubos.model.book.relationships.BookLanguage;

import java.util.List;

public record RelationshipsData(
        List<BookContributor> contributors,
        List<BookLanguage> availableLanguages
) {}
