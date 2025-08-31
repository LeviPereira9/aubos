package lp.boble.aubos.dto.book.relationships;

import lp.boble.aubos.model.book.relationships.BookContributorModel;
import lp.boble.aubos.model.book.relationships.BookLanguage;

import java.util.List;

public record RelationshipsData(
        List<BookContributorModel> contributors,
        List<BookLanguage> availableLanguages
) {}
