package lp.boble.aubos.service.book;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.BookRequest;
import lp.boble.aubos.dto.book.BookResponse;
import lp.boble.aubos.dto.book.dependencies.*;
import lp.boble.aubos.dto.book.relationships.RelationshipsData;
import lp.boble.aubos.exception.custom.global.CustomFieldNotProvided;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.BookMapper;
import lp.boble.aubos.model.Enum.ContributorRoleEnum;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.repository.book.BookRepository;
import lp.boble.aubos.repository.book.relationships.BookContributorRepository;
import lp.boble.aubos.repository.book.relationships.BookLanguageRepository;
import lp.boble.aubos.service.book.dependencies.BookDependenciesService;
import lp.boble.aubos.service.book.dependencies.ContributorService;
import lp.boble.aubos.service.book.relationships.RelationshipsService;
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
    private final RelationshipsService relationshipsService;
    private final BookLanguageRepository bookLanguageRepository;
    private final BookContributorRepository bookContributorRepository;

    @Transactional
    public BookResponse createBook(BookRequest book) {

        validateAuthor(book.contributors());

        DependencyData dependencyData = dependenciesService.loadDependencyData(book);
        BookModel bookToSave = bookMapper.fromCreateRequestToModel(book, dependencyData);
        RelationshipsData relationshipsData = relationshipsService.loadRelationshipsData(bookToSave, book);

        bookToSave.setCreatedBy(authUtil.getRequester());
        bookToSave.setContributors(relationshipsData.contributors());
        bookToSave.setAvailableLanguages(relationshipsData.availableLanguages());

        return bookMapper.toResponse(bookRepository.save(bookToSave));
    }

    public BookResponse getBookById(UUID id){
        BookModel book = findBookOrThrow(id);

        return bookMapper.toResponse(book);
    }

    @Transactional
    public BookResponse updateBook(UUID id, BookRequest book) {

        validateAuthor(book.contributors());

        BookModel bookToUpdate = findBookOrThrow(id);

        DependencyData dependencyData = dependenciesService.loadDependencyData(book);
        bookMapper.fromUpdateToModel(bookToUpdate, book, dependencyData);

        relationshipsService.updateRelationships(bookToUpdate, book);

        bookToUpdate.setUpdatedBy(authUtil.getRequester());

        BookModel savedBook = bookRepository.save(bookToUpdate);

        return bookMapper.toResponse(savedBook);
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

    private void validateAuthor(List<BookAddContributor> contributors){
        boolean hasAuthor = contributors.stream()
                .anyMatch(c -> c.contributorRoleId() == ContributorRoleEnum.AUTHOR.getId());

        if(!hasAuthor){
            throw new CustomFieldNotProvided("Autor não informado.");
        }
    }

    private BookModel findBookOrThrow(UUID id){
        return bookRepository.findByIdAndSoftDeletedFalse(id)
                .orElseThrow(CustomNotFoundException::book);
    }



}
