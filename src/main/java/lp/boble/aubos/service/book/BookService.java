package lp.boble.aubos.service.book;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.BookCreateRequest;
import lp.boble.aubos.dto.book.BookResponse;
import lp.boble.aubos.dto.book.dependencies.ContributorResponse;
import lp.boble.aubos.dto.book.dependencies.DependencyData;
import lp.boble.aubos.dto.book.dependencies.LicenseResponse;
import lp.boble.aubos.dto.book.dependencies.RestrictionResponse;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.BookMapper;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.ContributorModel;
import lp.boble.aubos.model.book.relationships.BookContributor;
import lp.boble.aubos.repository.book.BookRepository;
import lp.boble.aubos.service.book.dependencies.BookDependenciesService;
import lp.boble.aubos.service.book.dependencies.ContributorService;
import lp.boble.aubos.util.AuthUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookDependenciesService dependenciesService;
    private final AuthUtil authUtil;

    private final BookRepository bookRepository;
    private final ContributorService contributorService;
    private final BookMapper bookMapper;

    @Transactional
    public void createBook(BookCreateRequest book) {

        DependencyData dependencyData = dependenciesService.loadDependencyData(book);

        BookModel bookToSave = bookMapper.fromCreateRequestToModel(book, dependencyData);
        bookToSave.setCreatedBy(authUtil.getRequester());

        bookToSave.setContributors(contributorService.getContributors(
                bookToSave,
                book.contributors()));

        bookRepository.save(bookToSave);
    }

    public BookResponse getBookById(UUID id){
        BookModel book = bookRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);

        return bookMapper.toResponse(book);
    }

}
