package lp.boble.aubos.service.book.relationships;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.BookContributorResponse;
import lp.boble.aubos.dto.book.relationships.BookContributorsResponse;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.relationships.BookContributorMapper;
import lp.boble.aubos.model.book.relationships.BookContributorModel;
import lp.boble.aubos.repository.book.relationships.BookContributorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

// TODO: GetSpecificsContributors
// TODO: Add Contributor to Book, Single or Batch
// TODO: Remove Contributor from Book, Single or Batch
@Service
@RequiredArgsConstructor
public class BookContributorService {

    private final BookContributorRepository bookContributorRepository;
    private final BookContributorMapper bookContributorMapper;

    public List<BookContributorResponse> findContributorsByRole(UUID bookId, String role) {
        List<BookContributorModel> bookContributors = this.findContributorsByBookAndRoleOrThrow(bookId, role);

        return bookContributors.stream()
                .map(bookContributorMapper::fromModelToResponse)
                .toList();
    }

    private List<BookContributorModel> findContributorsByBookAndRoleOrThrow(UUID bookId, String role) {
        List<BookContributorModel> bookContributors = bookContributorRepository.findAllByBookIdAndContributorRoleName(bookId, role);

        if(bookContributors.isEmpty()){
            throw CustomNotFoundException.bookContributor(role);
        }

        return bookContributors;
    }

    public BookContributorsResponse findContributorsByBook(UUID bookId) {
        List<BookContributorModel> bookContributors = this.findContributorsByBookOrThrow(bookId);

        return bookContributorMapper.fromModelToResponse(bookContributors);
    }

    private List<BookContributorModel> findContributorsByBookOrThrow(UUID bookId) {
        List<BookContributorModel> bookContributors = bookContributorRepository.findAllByBookId(bookId);

        if(bookContributors.isEmpty()){
            throw CustomNotFoundException.bookContributor();
        }

        return bookContributors;
    }


}
