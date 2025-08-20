package lp.boble.aubos.dto.book;

import lp.boble.aubos.dto.book.dependencies.DependencyData;
import lp.boble.aubos.dto.book.parts.BookAddContributor;

import java.time.LocalDate;
import java.util.List;

public record BookCreateRequest(
        String coverUrl,
        String title,
        String subtitle,
        String synopsis,
        LocalDate publishedOn,
        LocalDate finishedOn,
        BookContextRequest contextRequest,
        List<Integer> availableLanguagesId,
        List<BookAddContributor> contributors
) {
}
