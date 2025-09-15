package lp.boble.aubos.service.book.relationships;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.parts.BookAddContributor;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorResponse;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorUpdateRequest;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorsResponse;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.relationships.BookContributorMapper;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.ContributorModel;
import lp.boble.aubos.model.book.dependencies.ContributorRole;
import lp.boble.aubos.model.book.relationships.BookContributorModel;
import lp.boble.aubos.repository.book.relationships.BookContributorRepository;
import lp.boble.aubos.service.book.BookService;
import lp.boble.aubos.service.book.dependencies.ContributorRoleService;
import lp.boble.aubos.service.book.dependencies.ContributorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookContributorService {

    private final BookContributorRepository bookContributorRepository;
    private final BookContributorMapper bookContributorMapper;
    private final BookService bookService;
    private final ContributorService contributorService;
    private final ContributorRoleService contributorRoleService;

    public List<BookContributorResponse> findContributorsByRole(UUID bookId, int role) {
        List<BookContributorModel> bookContributors = this.findContributorsByBookAndRoleOrThrow(bookId, role);

        return bookContributors.stream()
                .map(bookContributorMapper::fromModelToResponse)
                .toList();
    }

    private List<BookContributorModel> findContributorsByBookAndRoleOrThrow(UUID bookId, int role) {
        List<BookContributorModel> bookContributors = bookContributorRepository.findAllByBookIdAndContributorRoleId(bookId, role);

        if(bookContributors.isEmpty()){
            throw CustomNotFoundException.bookContributor("" + role);
        }

        return bookContributors;
    }

    public BookContributorsResponse findContributors(UUID bookId) {
        List<BookContributorModel> bookContributors = this.findContributorsByBookOrThrow(bookId);

        return bookContributorMapper.fromModelToResponse(bookContributors);
    }

    public List<BookContributorModel> findContributorsByBookOrThrow(UUID bookId) {
        List<BookContributorModel> bookContributors = bookContributorRepository.findAllByBookId(bookId);

        if(bookContributors.isEmpty()){
            throw CustomNotFoundException.bookContributor();
        }

        return bookContributors;
    }

    public void addContributorToBook(UUID bookId, BookAddContributor request){

        this.validateAddContributorDoesNotExist(bookId, request);

        BookContributorModel bookContributor = this.generateBookContributor(bookId, request);

        bookContributorRepository.save(bookContributor);
    }

    private void validateAddContributorDoesNotExist(UUID bookId, BookAddContributor request){
        boolean exists = bookContributorRepository.existsByBookIdAndContributorIdAndContributorRoleId(
                bookId,
                request.contributorId(),
                request.contributorRoleId());

        if(exists){
            throw CustomDuplicateFieldException.bookFamily();
        }
    }

    private BookContributorModel generateBookContributor(UUID bookId, BookAddContributor request){
        BookModel book = bookService.findBookOrThrow(bookId);
        ContributorModel contributor = contributorService.findContributorOrThrow(request.contributorId());
        ContributorRole contributorRole = contributorRoleService.getContributorRoleOrThrow(request.contributorRoleId());

        return new BookContributorModel(book, contributor, contributorRole);
    }

    public void updateContributorOnBook(UUID id, BookContributorUpdateRequest request){
        BookContributorModel bookContributor = this.findBookContributorOrThrow(id);
        ContributorRole role = contributorRoleService.getContributorRoleOrThrow(request.contributorRoleId());

        bookContributorMapper.updateBookContributor(bookContributor, role);

        bookContributorRepository.save(bookContributor);
    }


    public void deleteContributorFromBook(UUID bookId, UUID bookContributorId){

        BookContributorModel bookContributor = this.findBookContributorOrThrow(bookContributorId);

        if(!bookContributor.belongsToBook(bookId)){
            throw CustomNotFoundException.bookContributor();
        }

        bookContributorRepository.deleteById(bookContributorId);
    }

    private BookContributorModel findBookContributorOrThrow(UUID bookContributorId) {
        return bookContributorRepository.findById(bookContributorId)
                .orElseThrow(CustomNotFoundException::bookContributor);
    }



    /*private BookContributorPayload getBookContributor(BookContributorUpdateRequest request){

        ContributorModel contributor = contributorService.findContributorOrThrow(request.contributorId());
        ContributorRole contributorRole = dependenciesService.getContributorRole(request.toRoleId());

        return new BookContributorPayload(contributor, contributorRole);
    }*/
}
