package lp.boble.aubos.dto.book;

import lp.boble.aubos.model.book.relationships.BookContributorModel;
import lp.boble.aubos.util.ValidationResult;

import java.util.List;

public record BatchResult<T>(
        ValidationResult<T> validationResult,
        List<BookContributorModel> entitiesToUpdate
) {}
