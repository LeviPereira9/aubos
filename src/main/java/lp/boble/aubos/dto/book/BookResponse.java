package lp.boble.aubos.dto.book;

import lombok.Builder;
import lombok.Data;
import lp.boble.aubos.dto.book.dependencies.ContributorResponse;
import lp.boble.aubos.dto.book.dependencies.LicenseResponse;
import lp.boble.aubos.dto.book.dependencies.RestrictionResponse;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class BookResponse {
    private String title;
    private String subtitle;
    private String synopsis;
    private List<ContributorResponse> authors;
    private List<ContributorResponse> editors;
    private List<ContributorResponse> illustrators;
    private List<ContributorResponse> publishers;
    private LocalDate publishedOn;
    private LocalDate finishedOn;
    private String language;
    private String type;
    private String status;
    private LicenseResponse license;
    private RestrictionResponse restriction;
    private List<String> availableLanguages;
}
