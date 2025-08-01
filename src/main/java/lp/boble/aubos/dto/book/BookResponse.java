package lp.boble.aubos.dto.book;

import lombok.Builder;
import lombok.Data;
import lp.boble.aubos.dto.book.dependencies.ContributorBookResponse;
import lp.boble.aubos.dto.book.dependencies.LicenseResponse;
import lp.boble.aubos.dto.book.dependencies.RestrictionResponse;

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
    private List<ContributorBookResponse> authors;
    private List<ContributorBookResponse> editors;
    private List<ContributorBookResponse> illustrators;
    private List<ContributorBookResponse> publishers;
    private LocalDate publishedOn;
    private LocalDate finishedOn;
    private String language;
    private String type;
    private String status;
    private LicenseResponse license;
    private RestrictionResponse restriction;
    private List<String> availableLanguages;
}
