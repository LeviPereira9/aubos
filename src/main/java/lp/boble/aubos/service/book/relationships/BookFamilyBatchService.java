package lp.boble.aubos.service.book.relationships;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyCreateRequest;
import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyDeleteRequest;
import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyUpdateRequest;
import lp.boble.aubos.mapper.book.family.BookFamilyMapperImpl;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.family.FamilyModel;
import lp.boble.aubos.model.book.relationships.BookFamilyModel;
import lp.boble.aubos.repository.book.relationships.BookFamilyRepository;
import lp.boble.aubos.response.batch.BatchContent;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.service.book.BookService;
import lp.boble.aubos.service.book.family.FamilyService;
import lp.boble.aubos.util.AuthUtil;
import lp.boble.aubos.util.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookFamilyBatchService {
    private final FamilyService familyService;
    private final BookService bookService;
    private final BookFamilyMapperImpl bookFamilyMapperImpl;
    private final AuthUtil authUtil;
    private final BookFamilyService bookFamilyService;
    private final BookFamilyRepository bookFamilyRepository;

    @Transactional
    public BatchTransporter<UUID> addBooksToFamily(UUID familyId, List<BookFamilyCreateRequest> requests) {
        ValidationResult<BookFamilyCreateRequest> validationResult = this.validateBooksToFamily(familyId, requests);

        List<BookFamilyModel> booksToAddInFamily = this.createBooksToFamily(familyId, validationResult);

        bookFamilyRepository.saveAll(booksToAddInFamily);

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<BookFamilyCreateRequest> validateBooksToFamily(UUID familyId, List<BookFamilyCreateRequest> requests) {
        ValidationResult<BookFamilyCreateRequest> validationResult = new ValidationResult<>();

        List<UUID> booksOnCurrentFamily = this.findBookIdsInFamily(familyId);

        for (BookFamilyCreateRequest request : requests) {
            UUID bookId = request.bookId();

            boolean bookConflict = booksOnCurrentFamily.contains(bookId);
            boolean bookDontExist = !bookService.bookExistsById(bookId);

            if(!bookConflict && !bookDontExist) {
                validationResult.addValid(request);
                continue;
            }

            if(bookConflict) validationResult.addFailure(bookId, "Este livro já está na coleção.");
            if(bookDontExist) validationResult.addFailure(bookId, "Livro não encontrado.");
        }

        return validationResult;
    }

    private List<BookFamilyModel> createBooksToFamily(
            UUID familyId,
            ValidationResult<BookFamilyCreateRequest> validationResult) {

        List<BookFamilyModel> booksToAddInFamily = new ArrayList<>();

        FamilyModel familyDestination = familyService.findFamilyOrThrow(familyId);

        List<Integer> ordersInUse = this.findOrdersInFamily(familyId);

        for(BookFamilyCreateRequest request: validationResult.getValidRequests()){

            BookModel bookToAdd = bookService.findBookOrThrow(request.bookId());
            BookFamilyModel bookFamilyToAdd = bookFamilyMapperImpl.fromCreateRequestToModel(
                    request,
                    bookToAdd,
                    familyDestination);

            bookFamilyToAdd.adjustOrderToAvoidConflict(ordersInUse);

            booksToAddInFamily.add(bookFamilyToAdd);
        }

        return booksToAddInFamily;
    }

    @Transactional
    public BatchTransporter<UUID> updateBooksBatch(UUID familyId, List<BookFamilyUpdateRequest> requests){
        List<BookFamilyModel> currentMembers = this.findAllBooksInFamily(familyId);

        ValidationResult<BookFamilyUpdateRequest> validationResult = this.validateUpdateBookFamily(currentMembers, requests);

        List<BookFamilyModel> booksToUpdate = this.prepareMembersToUpdate(validationResult, currentMembers);

        bookFamilyRepository.saveAll(booksToUpdate);

        return validationResult.getSuccessesAndFailures();
    }

    public List<BookFamilyModel> prepareMembersToUpdate(ValidationResult<BookFamilyUpdateRequest> validationResult, List<BookFamilyModel> currentMembers){
        Map<UUID, BookFamilyModel> membersInFamilyByBookId = new HashMap<>();

        for(BookFamilyUpdateRequest request : validationResult.getValidRequests()) {
            BookFamilyModel toAdd = currentMembers.stream()
                    .filter(b -> b.getBook().getId().equals(request.bookId()))
                    .findFirst().orElse(null);

            membersInFamilyByBookId.put(request.bookId(), toAdd);
        }

        List<BookFamilyModel> booksToUpdate = membersInFamilyByBookId.values().stream().collect(Collectors.toList());

        this.prepareOrdersToBatchUpdate(booksToUpdate);

        for(BookFamilyUpdateRequest request : validationResult.getValidRequests()) {
            BookFamilyModel toAdd = membersInFamilyByBookId.get(request.bookId());
            toAdd.setUpdatedBy(authUtil.getRequester());
            toAdd.setOrderInFamily(request.order());
        }

        return booksToUpdate;
    }

    private void prepareOrdersToBatchUpdate(List<BookFamilyModel> currentBookFamilyMembers) {
        // Como temos constraints para garantir a integridade do BD, precisamos mudar os valores para que caso haja troca de posições entre os livros, elas ocorram sem que o BD fique triste.
        currentBookFamilyMembers.forEach(b ->
                b.setOrderInFamily(-1 * b.getOrderInFamily()));

        bookFamilyRepository.saveAll(currentBookFamilyMembers);
        bookFamilyRepository.flush();
    }

    private ValidationResult<BookFamilyUpdateRequest> validateUpdateBookFamily(
            List<BookFamilyModel> currentMembersInFamily,
            List<BookFamilyUpdateRequest> requests) {

        ValidationResult<BookFamilyUpdateRequest> validationResult = new ValidationResult<>();

        Set<UUID> currentBooksInFamily = currentMembersInFamily.stream()
                .map(bf -> bf.getBook().getId())
                .collect(Collectors.toSet());

        Set<BookFamilyUpdateRequest> duplicateRequests = new HashSet<>();
        Set<BookFamilyUpdateRequest> goodRequests = new HashSet<>();

        // Sets para detectar requisições com duplicatas
        Set<UUID> toUpdateBooks = new HashSet<>();
        Set<Integer> toUpdateOrders = new HashSet<>();

        for(BookFamilyUpdateRequest request : requests) {
            UUID bookId = request.bookId();
            boolean isNotOnCollection = !currentBooksInFamily.contains(bookId);
            boolean isDuplicateBook = !toUpdateBooks.add(bookId);
            boolean isDuplicateOrder = !toUpdateOrders.add(request.order());

            if(!isNotOnCollection && !isDuplicateBook && !isDuplicateOrder) {
                goodRequests.add(request);
                continue;
            }

            if(isNotOnCollection) validationResult.addFailure(bookId, "Este livro não pertence à essa coleção.");
            if(isDuplicateBook) validationResult.addFailure(bookId, "Este livro está duplicado.");
            if(isDuplicateOrder) validationResult.addFailure(bookId, "Ordem duplicada");
            duplicateRequests.add(request);
        }
        goodRequests.removeAll(duplicateRequests);
        validationResult.setPendentRequests(goodRequests);

        this.validSwap(validationResult, currentMembersInFamily);

        return validationResult;
    }

    private void validSwap(
            ValidationResult<BookFamilyUpdateRequest> validationResult,
            List<BookFamilyModel> currentBookFamilyMembers ) {

        List<BookFamilyUpdateRequest> requests = new ArrayList<>(validationResult.getPendentRequests());

        Map<UUID, Integer> bookIdByRequestedOrder = requests.stream()
                .collect(Collectors.toMap(BookFamilyUpdateRequest::bookId, BookFamilyUpdateRequest::order));

        Map<Integer, UUID> orderToCurrentBookId = currentBookFamilyMembers.stream()
                .collect(Collectors.toMap(BookFamilyModel::getOrderInFamily, bf -> bf.getBook().getId()));

        for(BookFamilyUpdateRequest request : requests) {
            UUID requestBookId = request.bookId();
            UUID currentBookIdAtOrder = orderToCurrentBookId.get(request.order());

            if(requestBookId.equals(currentBookIdAtOrder)){
                validationResult.addSuccess(requestBookId, "Posição do livro não alterada");
                continue;
            }

            boolean hasValidSwapPath = this.hasValidSwap(requestBookId,  null, bookIdByRequestedOrder, orderToCurrentBookId);

            if(hasValidSwapPath) {
                validationResult.addSuccess(requestBookId, "livro reordenado com sucesso.");
                validationResult.addValid(request);
                continue;
            }

            validationResult.addFailure(requestBookId, "Falha na reordenação, conflito na ordem especificada.");
        }

    }

    private boolean hasValidSwap(UUID originalBookId, UUID currentBookIdInPath, Map<UUID, Integer> bookIdByRequestedOrder, Map<Integer, UUID> orderToCurrentBookId ){

        boolean hasIntermediateBook = currentBookIdInPath != null;

        boolean isTargetPositionOfOriginalBook = false;

        UUID bookToEvaluate = hasIntermediateBook ? currentBookIdInPath : originalBookId;

        int requestedPosition = bookIdByRequestedOrder.get(bookToEvaluate);
        UUID bookAtRequestedOrder = orderToCurrentBookId.get(requestedPosition);

        boolean positionIsFree = (bookAtRequestedOrder == null);
        boolean bookAtRequestedPositionHasRequest = (bookIdByRequestedOrder.get(bookAtRequestedOrder) != null);

        if(hasIntermediateBook){
            isTargetPositionOfOriginalBook = originalBookId.equals(bookAtRequestedOrder);
        }

        if(positionIsFree || isTargetPositionOfOriginalBook){
            return true;
        }

        if(bookAtRequestedPositionHasRequest){
            return this.hasValidSwap(originalBookId, bookAtRequestedOrder, bookIdByRequestedOrder, orderToCurrentBookId);
        }

        return false;
    }

    @Transactional
    public BatchTransporter<UUID> removeBooksFromFamily(UUID familyId, List<BookFamilyDeleteRequest> deleteRequests) {
        ValidationResult<BookFamilyDeleteRequest> validationResult = this.validateDeleteBookBatch(familyId, deleteRequests);

        List<UUID> booksToRemove = this.prepareBookIdsToRemove(validationResult);

        bookFamilyRepository.deleteAllById(booksToRemove);

        return validationResult.getSuccessesAndFailures();
    }

    private List<UUID> prepareBookIdsToRemove(ValidationResult<BookFamilyDeleteRequest> validationResult) {

        return validationResult.getValidRequests().stream().map(BookFamilyDeleteRequest::bookId).toList();
    }

    private ValidationResult<BookFamilyDeleteRequest> validateDeleteBookBatch(
            UUID familyId,
            List<BookFamilyDeleteRequest> deleteRequests){
        ValidationResult<BookFamilyDeleteRequest> validationResult = new ValidationResult<>();

        List<UUID> bookIdsInFamily = this.findBookIdsInFamily(familyId);
        Set<UUID> booksToRemoveFromFamily = new HashSet<>();

        for(BookFamilyDeleteRequest deleteRequest : deleteRequests) {
            UUID bookId = deleteRequest.bookId();

            if(!booksToRemoveFromFamily.add(bookId)) {
                validationResult.addFailure(bookId, "Livro duplicado na requisição");
                continue;
            }

            if(!bookIdsInFamily.contains(bookId)) {
                validationResult.addFailure(bookId, "Livro não pertence a essa coleção");
                continue;
            }

            validationResult.addSuccess(bookId, "Livro removido com sucesso.");
            validationResult.addValid(deleteRequest);
        }

        return validationResult;
    }



    private List<BookFamilyModel> findAllBooksInFamily(UUID familyId){
        return bookFamilyRepository.findAllByFamilyId(familyId);
    }

    private List<UUID> findBookIdsInFamily(UUID familyId){
        return bookFamilyRepository.findAllBookIdsByFamilyId(familyId);
    }

    private List<Integer> findOrdersInFamily(UUID familyId){
        return bookFamilyRepository.findAllOrderInFamilyByFamilyId(familyId);
    }
}
