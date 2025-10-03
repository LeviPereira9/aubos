package lp.boble.aubos.dto.book.relationships.bookLanguage;

import java.util.UUID;

public record BookLanguageCreatedResponse(
        UUID id,
        int languageId,
        UUID bookId
) {}
