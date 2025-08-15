package lp.boble.aubos.service.book;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.BookPageProjection;
import lp.boble.aubos.dto.book.BookPageResponse;
import lp.boble.aubos.dto.book.BookRequest;
import lp.boble.aubos.dto.book.BookResponse;
import lp.boble.aubos.dto.book.dependencies.*;
import lp.boble.aubos.dto.book.relationships.RelationshipsData;
import lp.boble.aubos.dto.book.parts.BookAddContributor;
import lp.boble.aubos.exception.custom.global.CustomFieldNotProvided;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.BookMapper;
import lp.boble.aubos.model.Enum.ContributorRoleEnum;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.repository.book.BookRepository;
import lp.boble.aubos.response.pages.PageResponse;
import lp.boble.aubos.service.book.dependencies.DependenciesService;
import lp.boble.aubos.service.book.relationships.RelationshipsService;
import lp.boble.aubos.util.AuthUtil;
import lp.boble.aubos.util.ValidationUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookService {
    private final DependenciesService dependenciesService;
    private final AuthUtil authUtil;

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final RelationshipsService relationshipsService;
    private final ValidationUtil validationUtil;

    @Transactional
    public BookResponse createBook(BookRequest book) {

        validateAuthor(book.contributors());

        DependencyData dependencyData = dependenciesService.loadBookDependencyData(book);
        BookModel bookToCreate = bookMapper.fromCreateRequestToModel(book, dependencyData);
        RelationshipsData relationshipsData = relationshipsService.loadBookRelationshipsData(bookToCreate, book);

        bookToCreate.setCreatedBy(authUtil.getRequester());
        bookToCreate.setContributors(relationshipsData.contributors());
        bookToCreate.setAvailableLanguages(relationshipsData.availableLanguages());

        return bookMapper.toResponse(bookRepository.save(bookToCreate));
    }

    @Cacheable(value = "book", key = "#bookId")
    public BookResponse getBookById(UUID bookId){
        BookModel bookFound = findBookOrThrow(bookId);

        return bookMapper.toResponse(bookFound);
    }

    @CachePut(value = "book", key = "#id")
    @Transactional
    public BookResponse updateBook(UUID id, BookRequest book) {

        validateAuthor(book.contributors());

        BookModel bookToUpdate = findBookOrThrow(id);

        DependencyData dependencyData = dependenciesService.loadBookDependencyData(book);
        bookMapper.fromUpdateToModel(bookToUpdate, book, dependencyData);

        relationshipsService.updateBookRelationships(bookToUpdate, book);

        bookToUpdate.setUpdatedBy(authUtil.getRequester());

        return bookMapper.toResponse(bookRepository.save(bookToUpdate));
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "book", key = "#bookId"),
                    @CacheEvict(value = "bookSearch", allEntries = true)}
    )
    @Transactional
    public void deleteBook(UUID bookId){
        BookModel bookToDelete = bookRepository.findByIdAndSoftDeletedFalse(bookId)
                .orElseThrow(CustomNotFoundException::book);

        bookToDelete.setSoftDeleted(true);
        bookToDelete.setLastUpdated(Instant.now());
        bookToDelete.setUpdatedBy(authUtil.getRequester());

        bookRepository.save(bookToDelete);
    }

    @Cacheable(value = "bookSearch", key = "'search=' + #search + ',page=' + #page")
    public PageResponse<BookPageResponse> getBookBySearch(String search, int page){
        validationUtil.validateSearchRequest(search, page);

        PageRequest pageRequest = PageRequest.of(
                page,
                10
        );

        PageResponse<BookPageProjection> pageFound = new PageResponse<>(
                bookRepository.getAllShortBookInfo(pageRequest, search)
        );

        return pageFound.map(bookMapper::fromProjectionToResponse);
    }

    private void validateAuthor(List<BookAddContributor> contributors){
        boolean hasAuthor = contributors.stream()
                .anyMatch(c -> c.contributorRoleId() == ContributorRoleEnum.AUTHOR.getId());

        if(!hasAuthor){
            throw new CustomFieldNotProvided("Autor não informado.");
        }
    }

    public BookModel findBookOrThrow(UUID id){
        return bookRepository.findByIdAndSoftDeletedFalse(id)
                .orElseThrow(CustomNotFoundException::book);
    }



}
