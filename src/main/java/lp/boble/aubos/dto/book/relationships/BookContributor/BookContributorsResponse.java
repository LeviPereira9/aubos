package lp.boble.aubos.dto.book.relationships.BookContributor;

import lombok.*;
import lp.boble.aubos.dto.book.parts.BookContributorPartResponse;

import java.util.List;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC) // garante que o construtor de todos os campos é público
@NoArgsConstructor
public class BookContributorsResponse {
        private List<BookContributorPartResponse> authors;
        private List<BookContributorPartResponse> editors;
        private List<BookContributorPartResponse> illustrators;
        private List<BookContributorPartResponse> publishers;
}
