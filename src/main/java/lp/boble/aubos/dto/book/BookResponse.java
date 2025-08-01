package lp.boble.aubos.dto.book;

import lombok.Builder;
import lombok.Data;
import lp.boble.aubos.dto.book.parts.BookContributorResponse;
import lp.boble.aubos.dto.book.parts.BookLicenseResponse;
import lp.boble.aubos.dto.book.parts.BookRestrictionResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Data
public class BookResponse {
    private UUID id;
    private String coverUrl;
    private String title;
    private String subtitle;
    private String synopsis;
    private List<BookContributorResponse> authors;
    private List<BookContributorResponse> editors;
    private List<BookContributorResponse> illustrators;
    private List<BookContributorResponse> publishers;
    private LocalDate publishedOn;
    private LocalDate finishedOn;
    private String language;
    private String type;
    private String status;
    private BookLicenseResponse license;
    private BookRestrictionResponse restriction;
    private List<String> availableLanguages;
}
