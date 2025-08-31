package lp.boble.aubos.dto.book.relationships;

import lombok.Builder;
import lombok.Data;
import lp.boble.aubos.dto.book.parts.BookContributorResponse;

import java.util.List;

@Builder
@Data
public class BookContributorsResponse{
        private List<BookContributorResponse> authors;
        private List<BookContributorResponse> editors;
        private List<BookContributorResponse> illustrators;
        private List<BookContributorResponse> publishers; }
