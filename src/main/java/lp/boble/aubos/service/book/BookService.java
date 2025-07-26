package lp.boble.aubos.service.book;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.BookRequest;
import lp.boble.aubos.dto.book.BookResponse;
import lp.boble.aubos.dto.book.dependencies.*;
import lp.boble.aubos.exception.custom.global.CustomFieldNotProvided;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.BookMapper;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.relationships.BookContributor;
import lp.boble.aubos.model.book.relationships.BookLanguage;
import lp.boble.aubos.repository.book.BookRepository;
import lp.boble.aubos.service.book.dependencies.BookDependenciesService;
import lp.boble.aubos.service.book.dependencies.ContributorService;
import lp.boble.aubos.util.AuthUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    public BookResponse createBook(BookRequest book) {

        hasAuthor(book.contributors());

        DependencyData dependencyData = dependenciesService.loadDependencyData(book);
        BookModel bookToSave = bookMapper.fromCreateRequestToModel(book, dependencyData);

        List<BookContributor> contributors = contributorService.getContributors(
                bookToSave,
                book.contributors());
        List<BookLanguage> availableLanguages = dependenciesService.getAvailableLanguages(
                bookToSave,
                book.availableLanguagesId());

        bookToSave.setCreatedBy(authUtil.getRequester());
        bookToSave.setContributors(contributors);
        bookToSave.setAvailableLanguages(availableLanguages);

        return bookMapper.toResponse(bookRepository.save(bookToSave));
    }

    public BookResponse getBookById(UUID id){
        BookModel book = bookRepository.findByIdAndSoftDeletedFalse(id)
                .orElseThrow(CustomNotFoundException::book);

        return bookMapper.toResponse(book);
    }

    @Transactional
    public BookResponse updateBook(UUID id, BookRequest book) {

        hasAuthor(book.contributors());

        BookModel bookToUpdate = bookRepository.findByIdAndSoftDeletedFalse(id)
                .orElseThrow(CustomNotFoundException::book);

        DependencyData dependencyData = dependenciesService.loadDependencyData(book);
        List<BookContributor> contributors = contributorService.getContributors(bookToUpdate, book.contributors());


        bookToUpdate = bookMapper.fromCreateRequestToModel(book, dependencyData);

        bookToUpdate.getContributors().clear();
        bookToUpdate.getContributors().addAll(contributors);
        bookToUpdate.setLastUpdated(Instant.now());
        bookToUpdate.setUpdatedBy(authUtil.getRequester());

        return bookMapper.toResponse(bookRepository.save(bookToUpdate));
    }

    @Transactional
    public void deleteBook(UUID id){
        BookModel book = bookRepository.findByIdAndSoftDeletedFalse(id)
                .orElseThrow(CustomNotFoundException::book);

        book.setSoftDeleted(true);
        book.setLastUpdated(Instant.now());
        book.setUpdatedBy(authUtil.getRequester());

        bookRepository.save(book);
    }

    private void hasAuthor(List<BookAddContributor> contributors){
        boolean hasAuthor = contributors.stream()
                .anyMatch(c -> c.contributorRoleId() == 1);

        if(!hasAuthor){
            throw new CustomFieldNotProvided("Autor não informado");
        }
    }

}
