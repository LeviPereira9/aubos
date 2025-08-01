package lp.boble.aubos.dto.book;

import lp.boble.aubos.dto.book.parts.BookAddContributor;

import java.time.LocalDate;
import java.util.List;

public record BookRequest(
        String coverUrl,
        String title,
        String subtitle,
        String synopsis,
        LocalDate publishedOn,
        LocalDate finishedOn,
        int languageId,
        int typeId,
        int statusId,
        int restrictionId,
        int licenseId,
        List<Integer> availableLanguagesId,
        List<BookAddContributor> contributors
) {}
