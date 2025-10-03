package lp.boble.aubos.service.book;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.*;
import lp.boble.aubos.dto.book.dependencies.dependecy.DependencyData;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final DependenciesService dependenciesService;
    private final AuthUtil authUtil;

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final RelationshipsService relationshipsService;

    @Transactional
    public BookResponse createAndPersistBook(BookCreateRequest createRequest) {

        BookModel bookToCreate = this.generateBookToPersist(createRequest);

        return bookMapper.toResponse(bookRepository.save(bookToCreate));
    }

    @Cacheable(value = "book", key = "#bookId")
    public BookResponse getBookById(UUID bookId){
        BookModel bookFound = this.findBookOrThrow(bookId);

        return bookMapper.toResponse(bookFound);
    }

    @CachePut(value = "book", key = "#id")
    @Transactional
    public BookResponse updateBook(UUID id, BookUpdateRequest bookUpdateRequest) {
        BookModel bookToUpdate = this.findBookOrThrow(id);

        BookUpdateData bookUpdateData = this.loadBookUpdateData(bookUpdateRequest);

        bookMapper.fromUpdateToModel(bookToUpdate, bookUpdateData);

        return bookMapper.toResponse(this.saveBook(bookToUpdate));
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "book", key = "#bookId"),
                    @CacheEvict(value = "bookSearch", allEntries = true)}
    )
    @Transactional
    public void deleteBook(UUID bookId){
        BookModel bookToSoftDelete = this.findBookOrThrow(bookId);

        bookToSoftDelete.softDelete(authUtil.getRequester());

        this.saveBook(bookToSoftDelete);
    }

    @Cacheable(value = "bookSearch", key = "'search=' + #search + ',page=' + #page")
    public PageResponse<BookPageResponse> getBookBySearch(String search, int page){

        PageRequest pageRequest = PageRequest.of(
                page,
                10
        );

        PageResponse<BookPageProjection> pageFound = new PageResponse<>(
                bookRepository.getAllShortBookInfo(pageRequest, search)
        );

        return pageFound.map(bookMapper::fromProjectionToResponse);
    }

    private void validateRequestHasAuthor(List<BookAddContributor> contributors){
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

    private BookUpdateData loadBookUpdateData(BookUpdateRequest bookUpdateRequest){
        DependencyData dependencyData = dependenciesService.loadBookDependencyData(bookUpdateRequest.contextRequest());

        return bookMapper.fromUpdateToPreparation(bookUpdateRequest, dependencyData);
    }

    private BookModel generateBookToPersist(BookCreateRequest createRequest){
        validateRequestHasAuthor(createRequest.contributors());

        DependencyData dependencyData = dependenciesService.loadBookDependencyData(createRequest.contextRequest());
        BookModel bookToPersist = bookMapper.fromCreateRequestToModel(createRequest, dependencyData);
        this.applyRelationships(bookToPersist, createRequest);

        return bookToPersist;
    }

    private void applyRelationships(BookModel bookToPersist, BookCreateRequest createRequest){
        RelationshipsData relationshipsData = relationshipsService.loadBookRelationshipsData(bookToPersist, createRequest);

        bookToPersist.setCreatedBy(authUtil.getRequester());
        bookToPersist.setContributors(relationshipsData.contributors());
        bookToPersist.setAvailableLanguages(relationshipsData.availableLanguages());
    }

    private BookModel saveBook(BookModel bookToSave){
        return bookRepository.save(bookToSave);
    }

    public void bookExistsById(UUID bookId){
        boolean exists = bookRepository.existsById(bookId);

        if(!exists){
            throw CustomNotFoundException.book();
        }
    }

    private List<BookModel> findAllBooksById(List<UUID> requestedBookId) {
        return bookRepository.findAllById(requestedBookId);
    }

    public Map<UUID, BookModel> getRequestedBooks(List<UUID> requestedBookId){
        List<BookModel> books = this.findAllBooksById(requestedBookId);

        return books.stream().collect(Collectors.toMap(BookModel::getId, Function.identity()));
    }
}
