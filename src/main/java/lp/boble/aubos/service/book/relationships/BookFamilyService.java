package lp.boble.aubos.service.book.relationships;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.BookFamilyCreateRequest;
import lp.boble.aubos.dto.book.relationships.BookFamilyDeleteRequest;
import lp.boble.aubos.dto.book.relationships.BookFamilyResponse;
import lp.boble.aubos.dto.book.relationships.BookFamilyUpdateRequest;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.family.BookFamilyMapper;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.family.FamilyModel;
import lp.boble.aubos.model.book.relationships.BookFamilyModel;
import lp.boble.aubos.repository.book.BookRepository;
import lp.boble.aubos.repository.book.relationships.BookFamilyRepository;
import lp.boble.aubos.response.batch.BatchContent;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.service.book.BookService;
import lp.boble.aubos.service.book.family.FamilyService;
import lp.boble.aubos.util.AuthUtil;
import lp.boble.aubos.util.FamilyValidationResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookFamilyService {
    private final BookFamilyRepository bookFamilyRepository;
    private final BookService bookService;
    private final FamilyService familyService;
    private final AuthUtil authUtil;
    private final BookRepository bookRepository;
    private final BookFamilyMapper bookFamilyMapper;

    @Transactional
    public BookFamilyResponse addBookToFamily(UUID familyId, BookFamilyCreateRequest request) {

        this.validateBookConflict(familyId, request.bookId());

        BookFamilyModel bookFamilyToAdd = this.createBookFamily(familyId, request);

        return bookFamilyMapper.fromModelToResponse(bookFamilyRepository.save(bookFamilyToAdd));
    }

    private void validateBookConflict(UUID familyId, UUID bookId) {
        boolean hasConflict = bookFamilyRepository.existsByFamilyIdAndBookId(familyId, bookId);
        if(hasConflict) throw CustomDuplicateFieldException.bookFamily();
    }

    private BookFamilyModel createBookFamily(UUID familyId, BookFamilyCreateRequest request){
        BookModel bookToAdd = bookService.findBookOrThrow(request.bookId());
        FamilyModel familyDestination = familyService.findFamilyOrThrow(familyId);

        BookFamilyModel bookFamilyToAdd = bookFamilyMapper.fromCreateRequestToModel(request, bookToAdd, familyDestination);

        bookFamilyToAdd.setOrderInFamily(
                this.calculateAvailableOrderForFamily(
                        familyId,
                        bookFamilyToAdd.getOrderInFamily()
                )
        );

        return bookFamilyToAdd;
    }

    private int calculateAvailableOrderForFamily(UUID familyId, int requestedOrder) {
        boolean orderConflict = bookFamilyRepository.existsByFamilyIdAndOrderInFamily(familyId, requestedOrder);

        if(orderConflict){
            return bookFamilyRepository.findMaxOrderInFamilyByFamilyId(familyId) + 1;
        }

        return requestedOrder;
    }


    @Transactional
    public BatchTransporter<UUID> addBooksToFamily(UUID familyId, List<BookFamilyCreateRequest> requests) {
        FamilyValidationResult validationResult = this.validateBooksToFamily(familyId, requests);

        List<BookFamilyModel> booksToAddInFamily = this.createBooksToFamily(familyId, validationResult);

        bookFamilyRepository.saveAll(booksToAddInFamily);

        return getSuccessesAndFailures(validationResult);
    }

    private FamilyValidationResult validateBooksToFamily(UUID familyId, List<BookFamilyCreateRequest> requests) {
        FamilyValidationResult validationResult = new FamilyValidationResult();

        List<UUID> booksOnCurrentFamily = bookFamilyRepository.findAllBookIdsByFamilyId(familyId);

        for (BookFamilyCreateRequest request : requests) {
            UUID bookId = request.bookId();

            boolean bookConflict = booksOnCurrentFamily.contains(bookId);
            boolean bookDontExist = !bookRepository.existsById(bookId);

            if(!bookConflict && !bookDontExist) {
                validationResult.addValid(request);
                continue;
            }

            if(bookConflict) validationResult.addFailure(bookId, "Este livro já está na coleção.");
            if(bookDontExist) validationResult.addFailure(bookId, "Livro não encontrado.");
        }

        return validationResult;
    }

    private List<BookFamilyModel> createBooksToFamily(UUID familyId, FamilyValidationResult validationResult) {
        List<BookFamilyModel> booksToAddInFamily = new ArrayList<>();

        FamilyModel familyDestination = familyService.findFamilyOrThrow(familyId);

        List<Integer> ordersInUse = bookFamilyRepository.findAllOrderInFamilyByFamilyId(familyId);

        for(BookFamilyCreateRequest request: validationResult.getValidRequests()){

            BookModel bookToAdd = bookService.findBookOrThrow(request.bookId());
            BookFamilyModel bookFamilyToAdd = bookFamilyMapper.fromCreateRequestToModel(
                    request,
                    bookToAdd,
                    familyDestination);

            bookFamilyToAdd.adjustOrderToAvoidConflict(ordersInUse);

            booksToAddInFamily.add(bookFamilyToAdd);
        }

        return booksToAddInFamily;
    }

    private BatchTransporter<UUID> getSuccessesAndFailures(
            FamilyValidationResult validationResult){

        List<BatchContent<UUID>> successes = new ArrayList<>();
        List<BatchContent<UUID>> failures = new ArrayList<>();

        for(UUID bookId: validationResult.getSuccess()){
            successes.add(BatchContent.success(bookId, "Livro adicionado com sucesso."));
        }

        for(Map.Entry<UUID, String> failure: validationResult.getFailures().entrySet()){
            failures.add(BatchContent.failure(failure.getKey(), failure.getValue()));
        }

        return new BatchTransporter<>(successes, failures);
    }



    @Transactional
    public BookFamilyResponse updateBookFamily(UUID familyId, BookFamilyUpdateRequest request) {
        boolean bookConflict = !bookFamilyRepository.existsByFamilyIdAndBookId(familyId, request.bookId());
        boolean orderConflict = bookFamilyRepository.existsByFamilyIdAndOrderInFamily(familyId, request.order());

        if(orderConflict) throw CustomDuplicateFieldException.orderFamily();
        if(bookConflict) throw CustomNotFoundException.bookFamily();

        BookFamilyModel bookFamilyToUpdate = this.findByFamilyAndBookOrThrow(familyId, request.bookId());

        bookFamilyMapper.toUpdateFromRequest(bookFamilyToUpdate, request);
        bookFamilyToUpdate.setUpdatedBy(authUtil.getRequester());

        return bookFamilyMapper.fromModelToResponse(bookFamilyRepository.save(bookFamilyToUpdate));
    }

    @Transactional
    public BatchTransporter<UUID> updateBookFamilies(UUID familyId, List<BookFamilyUpdateRequest> requests) {
        List<BatchContent<UUID>> successes = new ArrayList<>();
        List<BatchContent<UUID>> failures = new ArrayList<>();

        List<BookFamilyModel> currentBooksInFamily = this.findAllBooksInFamily(familyId);

        this.validateUpdateBookFamily(currentBooksInFamily, requests);
        this.prepareOrdersToBatchUpdate(currentBooksInFamily);

        List<BookFamilyModel> booksInFamilyToUpdate = new ArrayList<>();

        Map<UUID, BookFamilyModel> currentFamilyMembersMap = currentBooksInFamily.stream()
                .collect(Collectors.toMap(b -> b.getBook().getId(), Function.identity()));

        for(BookFamilyUpdateRequest request : requests) {
            UUID bookId = request.bookId();

            BookFamilyModel bookFamilyToUpdate = currentFamilyMembersMap.get(bookId);

            if(bookFamilyToUpdate == null) {
                throw CustomNotFoundException.bookFamily();
            }

            bookFamilyMapper.toUpdateFromRequest(bookFamilyToUpdate, request);

            bookFamilyToUpdate.setUpdatedBy(authUtil.getRequester());
            booksInFamilyToUpdate.add(bookFamilyToUpdate);
            successes.add(BatchContent.success(request.bookId(), "Livro atualizado com sucesso."));
        }

        bookFamilyRepository.saveAll(booksInFamilyToUpdate);

        return new BatchTransporter<>(successes, failures);
    }

    private void prepareOrdersToBatchUpdate(List<BookFamilyModel> currentBookFamilyMembers) {
        // Como temos constraints para garantir a integridade do BD, precisamos mudar os valores para que caso haja troca de posições entre os livros, elas ocorram sem que o BD fique triste.
        currentBookFamilyMembers.forEach(b ->
                b.setOrderInFamily(-1 * b.getOrderInFamily()));

        bookFamilyRepository.saveAll(currentBookFamilyMembers);
        bookRepository.flush();
    }

    private void validateUpdateBookFamily(List<BookFamilyModel> currentBookFamilyMembers, List<BookFamilyUpdateRequest> requests) {
        Map<UUID, Integer> currentBookOrderByBookId = currentBookFamilyMembers.stream()
                .collect(Collectors.toMap(
                        c -> c.getBook().getId(),
                        BookFamilyModel::getOrderInFamily));

        // Sets para detectar requisições com duplicatas
        Set<UUID> toUpdateBooks = new HashSet<>();
        Set<Integer> toUpdateOrders = new HashSet<>();

        for(BookFamilyUpdateRequest request : requests) {
            UUID bookId = request.bookId();

            // Livro não pertence a coleção.
            if (!currentBookOrderByBookId.containsKey(bookId)) {
                throw CustomNotFoundException.bookFamily();
            }

            // Livro duplicado
            if (!toUpdateBooks.add(bookId)) {
                throw CustomDuplicateFieldException.bookFamily();
            }

            // Ordem duplicado
            if (!toUpdateOrders.add(request.order())) {
                throw CustomDuplicateFieldException.orderFamily();
            }
        }
    }

    @Transactional
    public void removeBookFromFamily(UUID familyId, BookFamilyDeleteRequest deleteRequest) {

        UUID bookId = deleteRequest.bookId();

        boolean bookFamilyExists = bookFamilyRepository.existsByFamilyIdAndBookId(familyId, bookId);

        if(!bookFamilyExists){
            throw CustomNotFoundException.book();
        }

        bookFamilyRepository.deleteByFamilyIdAndBookId(familyId, bookId);
    }

    @Transactional
    public BatchTransporter<UUID> removeBooksFromFamily(UUID familyId, List<BookFamilyDeleteRequest> deleteRequests) {
        List<BatchContent<UUID>> successes = new ArrayList<>();
        List<BatchContent<UUID>> failures = new ArrayList<>();

        List<BookFamilyModel> currenFamilyMembers = this.findAllBooksInFamily(familyId);
        List<BookFamilyModel> bookFamiliesToRemove = new ArrayList<>();

        Map<UUID, BookFamilyModel> currentFamilyMembersByBookId = currenFamilyMembers.stream()
                .collect(Collectors.toMap(b -> b.getBook().getId(), Function.identity()));

        Set<UUID> booksToRemoveFromFamily = new HashSet<>();

        for(BookFamilyDeleteRequest deleteRequest : deleteRequests) {
            UUID bookId = deleteRequest.bookId();

            if(!booksToRemoveFromFamily.add(bookId)) {
                failures.add(BatchContent.failure(bookId, "Livro duplicado na requisição"));
                continue;
            }

            if(!currentFamilyMembersByBookId.containsKey(bookId)) {
                failures.add(BatchContent.failure(bookId, "Livro não pertence a essa coleção"));
                continue;
            }

            bookFamiliesToRemove.add(currentFamilyMembersByBookId.get(bookId));
            successes.add(BatchContent.success(bookId, "Livro removido com sucesso."));
        }

        bookFamilyRepository.deleteAll(bookFamiliesToRemove);

        return new BatchTransporter<>(successes, failures);
    }

    public BookFamilyModel findByFamilyAndBookOrThrow(UUID familyId, UUID bookId){

        return bookFamilyRepository.findByFamilyIdAndBookId(familyId, bookId)
                .orElseThrow(CustomNotFoundException::bookFamily);
    }

    public List<BookFamilyModel> findAllBooksInFamily(UUID familyId){
        return bookFamilyRepository.findAllByFamilyId(familyId);
    }

}