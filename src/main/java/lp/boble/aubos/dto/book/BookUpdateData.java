package lp.boble.aubos.dto.book;

import lombok.Data;
import lp.boble.aubos.model.book.dependencies.*;

import java.time.LocalDate;

@Data
public class BookUpdateData {
    private String coverUrl;
    private String title;
    private String subtitle;
    private String synopsis;
    private LocalDate publishedOn;
    private LocalDate finishedOn;
    private LanguageModel language;
    private TypeModel type;
    private StatusModel status;
    private RestrictionModel restriction;
    private LicenseModel license;
}
