package lp.boble.aubos.service.book.relationships;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.parts.BookAddContributor;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorResponse;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorUpdateRequest;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorsResponse;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.relationships.BookContributorMapper;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.ContributorModel;
import lp.boble.aubos.model.book.dependencies.ContributorRole;
import lp.boble.aubos.model.book.relationships.BookContributorModel;
import lp.boble.aubos.repository.book.relationships.BookContributorRepository;
import lp.boble.aubos.service.book.BookService;
import lp.boble.aubos.service.book.dependencies.contributor.ContributorRoleService;
import lp.boble.aubos.service.book.dependencies.contributor.ContributorService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        List<BookContributorModel> bookContributors = bookContributorRepository.findAllByBook_IdAndContributorRole_Id(bookId, role);

        if(bookContributors.isEmpty()){
            throw CustomNotFoundException.bookContributor("" + role);
        }

        return bookContributors;
    }

    public BookContributorsResponse findContributors(UUID bookId) {
        List<BookContributorModel> bookContributors = this.findContributorsByBookOrThrow(bookId);

        return bookContributorMapper.toResponse(bookContributors);
    }

    public List<BookContributorModel> findContributorsByBookOrThrow(UUID bookId) {
        List<BookContributorModel> bookContributors = bookContributorRepository.findAllByBook_Id(bookId);

        if(bookContributors.isEmpty()){
            throw CustomNotFoundException.bookContributor();
        }

        return bookContributors;
    }


    public BookContributorResponse addContributorToBook(UUID bookId, BookAddContributor request){

        BookContributorModel bookContributor = this.generateBookContributor(bookId, request);

        return bookContributorMapper.fromModelToResponse(bookContributorRepository.save(bookContributor));
    }

    private BookContributorModel generateBookContributor(UUID bookId, BookAddContributor request){
        BookModel book = bookService.findBookOrThrow(bookId);
        ContributorModel contributor = contributorService.findContributorOrThrow(request.contributorId());
        ContributorRole contributorRole = contributorRoleService.getContributorRoleOrThrow(request.contributorRoleId());

        return new BookContributorModel(book, contributor, contributorRole);
    }

    public void updateContributorOnBook(UUID bookId, UUID booKContributorId, BookContributorUpdateRequest request){
        BookContributorModel bookContributor = this.findBookContributorOrThrow(bookId, booKContributorId);

        bookContributorMapper.updateBookContributor(bookContributor, request);

        bookContributorRepository.save(bookContributor);
    }


    public void deleteContributorFromBook(UUID bookId, UUID bookContributorId){

        BookContributorModel bookContributor = this.findBookContributorOrThrow(bookId, bookContributorId);



        bookContributorRepository.deleteById(bookContributorId);
    }

    private BookContributorModel findBookContributorOrThrow(UUID bookId, UUID bookContributorId) {
        BookContributorModel bookContributor = bookContributorRepository.findById(bookContributorId)
                .orElseThrow(CustomNotFoundException::bookContributor);

        if(!bookContributor.belongsToBook(bookId)){
            throw CustomNotFoundException.bookContributor();
        }

        return bookContributor;
    }

    public Map<UUID, List<Integer>> getCurrentContributorRolesFromBook(UUID bookId) {
        List<BookContributorModel> bookContributors = bookContributorRepository.findAllByBook_Id(bookId);

        return bookContributors.stream()
                .collect(Collectors.groupingBy(BookContributorModel::getContributorId, Collectors.mapping(BookContributorModel::getContributorRoleId, Collectors.toList())));
    }


    public Map<UUID, BookContributorModel> getCurrentContributorsFromBook(UUID bookId, List<UUID> bookContributorsId) {
        List<BookContributorModel> currentBookContributors = bookContributorRepository.findAllByBook_IdAndIdIn(bookId, bookContributorsId);

        return currentBookContributors.stream().collect(Collectors.toMap(BookContributorModel::getId, Function.identity()));
    }

}
