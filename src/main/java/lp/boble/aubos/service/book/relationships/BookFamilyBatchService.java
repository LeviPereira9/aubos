package lp.boble.aubos.service.book.relationships;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyCreateRequest;
import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyDeleteRequest;
import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyUpdateRequest;
import lp.boble.aubos.mapper.book.family.BookFamilyMapper;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.family.FamilyModel;
import lp.boble.aubos.model.book.relationships.BookFamilyModel;
import lp.boble.aubos.repository.book.relationships.BookFamilyRepository;
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
    private final BookFamilyMapper bookFamilyMapper;
    private final AuthUtil authUtil;
    private final BookFamilyService bookFamilyService;
    private final BookFamilyRepository bookFamilyRepository;

    @Transactional
    public BatchTransporter<UUID> addBooksToFamily(UUID familyId, List<BookFamilyCreateRequest> requests) {

        ValidationResult<UUID, BookFamilyModel> validationResult = this.validateBooksToFamily(familyId, requests);

        this.persistBatch(validationResult.getValidRequests());

        return validationResult.getSuccessesAndFailures();
    }

    private void persistBatch(List<BookFamilyModel> validRequests) {
        bookFamilyRepository.saveAll(validRequests);
    }

    private ValidationResult<UUID, BookFamilyModel> validateBooksToFamily(UUID familyId, List<BookFamilyCreateRequest> requests) {
        ValidationResult<UUID, BookFamilyModel> validationResult = new ValidationResult<>();

        List<UUID> requestedBookId = requests.stream().map(BookFamilyCreateRequest::bookId).toList();

        FamilyModel familyDestination = familyService.findFamilyOrThrow(familyId);
        Map<UUID, BookModel> mapRequestedBooks = bookService.getRequestedBooks(requestedBookId);

        List<BookFamilyModel> currentBookFamily = bookFamilyService.findAllMembersInFamily(familyId);

        List<UUID> booksOnCurrentFamily = currentBookFamily.stream().map(BookFamilyModel::getBookId).toList();
        List<Integer> ordersInUse = currentBookFamily.stream().map(BookFamilyModel::getOrderInFamily).collect(Collectors.toList());


        for (BookFamilyCreateRequest request : requests) {
            UUID bookId = request.bookId();

            boolean bookConflict = booksOnCurrentFamily.contains(bookId);
            boolean bookExist = mapRequestedBooks.containsKey(bookId);

            if(bookConflict) {
                validationResult.addFailure(bookId, "Este livro já está na família.");
                continue;
            };
            if(!bookExist){
                validationResult.addFailure(bookId, "Livro não encontrado.");
                continue;
            };

            BookFamilyModel bookFamilyToAdd = this.createBookToFamily(
                    request,
                    mapRequestedBooks.get(bookId),
                    familyDestination,
                    ordersInUse);

            validationResult.addSuccess(bookId, "Livro adicionado a família com sucesso.");
            validationResult.addValid(bookFamilyToAdd);
        }

        return validationResult;
    }

    private BookFamilyModel createBookToFamily(BookFamilyCreateRequest request, BookModel bookModel, FamilyModel familyDestination, List<Integer> ordersInUse) {
         BookFamilyModel createdBookFamily = bookFamilyMapper.fromCreateRequestToModel(
                request,
                bookModel,
                familyDestination);

        createdBookFamily.adjustOrderToAvoidConflict(ordersInUse);;

        return createdBookFamily;
    }


    @Transactional
    public BatchTransporter<UUID> updateBooksBatch(UUID familyId, List<BookFamilyUpdateRequest> requests){
        ValidationResult<UUID, BookFamilyModel> validationResult = this.validateUpdateBookFamily(familyId, requests);

        this.applyUpdate(validationResult);

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<UUID, BookFamilyModel> validateUpdateBookFamily(
            UUID familyId,
            List<BookFamilyUpdateRequest> requests) {

        ValidationResult<UUID, BookFamilyModel> validationResult = new ValidationResult<>();
        List<UUID> requestedMembersId = requests.stream().distinct().map(BookFamilyUpdateRequest::id).toList();

        Map<UUID, BookFamilyModel> mapRequestedMembers = bookFamilyService.getRequestedMembers(familyId, requestedMembersId);

        List<BookFamilyModel> currentMembersInFamily = mapRequestedMembers.values().stream().toList();

        Set<Integer> uniqueOrders = new HashSet<>();

        // LOOP #4
        for(BookFamilyUpdateRequest request : requests) {
            UUID memberId = request.id();
            boolean isMemberOfCollection = mapRequestedMembers.containsKey(memberId);
            boolean isDuplicateOrder = !uniqueOrders.add(request.order());

            if(!isMemberOfCollection){
                validationResult.addFailure(memberId, "Este livro não pertence à essa coleção.");
                continue;
            }
            if(isDuplicateOrder){
                validationResult.addFailure(memberId, "Ordem duplicada");
                continue;
            }
            BookFamilyModel memberToAdd = mapRequestedMembers.get(memberId);
            memberToAdd.setOrderInFamily(request.order());

            validationResult.addPendent(memberToAdd);
        }

        this.validSwap(validationResult, currentMembersInFamily);

        return validationResult;
    }

    private void validSwap(
            ValidationResult<UUID, BookFamilyModel> validationResult,
            List<BookFamilyModel> currentBookFamilyMembers ) {

        List<BookFamilyModel> pendentRequests = new ArrayList<>(validationResult.getPendentRequests());

        Map<UUID, Integer> mapMemberIdByRequestedOrder = pendentRequests.stream()
                .collect(Collectors.toMap(BookFamilyModel::getId, BookFamilyModel::getOrderInFamily));

        Map<Integer, UUID> mapOrderToCurrentMemberId = currentBookFamilyMembers.stream()
                .collect(Collectors.toMap(BookFamilyModel::getOrderInFamily, BookFamilyModel::getBookId));

        // LOOP #5
        for(BookFamilyModel request : pendentRequests) {
            UUID requestMemberId = request.getId();
            int requestMemberOrder = request.getOrderInFamily();

            UUID currentMemberIdAtRequestOrder = mapOrderToCurrentMemberId.get(requestMemberOrder);

            if(requestMemberId.equals(currentMemberIdAtRequestOrder)){
                validationResult.addSuccess(requestMemberId, "Posição do livro não alterada");
                continue;
            }

            boolean hasValidSwapPath = this.hasValidSwap(requestMemberId,  null, mapMemberIdByRequestedOrder, mapOrderToCurrentMemberId);

            if(hasValidSwapPath) {
                validationResult.addSuccess(requestMemberId, "livro reordenado com sucesso.");
                validationResult.addValid(request);
                continue;
            }

            validationResult.addFailure(requestMemberId, "Falha na reordenação, conflito na ordem especificada.");
        }

    }

    private boolean hasValidSwap(UUID originalMemberId, UUID currentMemberIdInPath, Map<UUID, Integer> memberIdByRequestedOrder, Map<Integer, UUID> orderToCurrentMemberId ){

        boolean hasIntermediateMember = currentMemberIdInPath != null;

        boolean isTargetPositionOfOriginalMember = false;

        UUID memberToEvaluate = hasIntermediateMember ? currentMemberIdInPath : originalMemberId;

        int requestedPosition = memberIdByRequestedOrder.get(memberToEvaluate);
        UUID memberAtRequestedOrder = orderToCurrentMemberId.get(requestedPosition);

        boolean positionIsFree = (memberAtRequestedOrder == null);
        boolean memberAtRequestedPositionHasRequest =  memberAtRequestedOrder != null && memberIdByRequestedOrder.containsKey(memberAtRequestedOrder);

        if(hasIntermediateMember){
            isTargetPositionOfOriginalMember = originalMemberId.equals(memberAtRequestedOrder);
        }

        if(positionIsFree || isTargetPositionOfOriginalMember){
            return true;
        }

        // LOOP #6!
        if(memberAtRequestedPositionHasRequest){
            return this.hasValidSwap(originalMemberId, memberAtRequestedOrder, memberIdByRequestedOrder, orderToCurrentMemberId);
        }

        return false;
    }

    private void applyUpdate(ValidationResult<UUID, BookFamilyModel> validationResult) {
        List<BookFamilyModel> membersToUpdate = validationResult.getValidRequests();

        this.applySafeOrderUpdate(membersToUpdate);
        bookFamilyRepository.saveAll(membersToUpdate);
    }

    public void applySafeOrderUpdate(List<BookFamilyModel> membersToUpdate) {
        membersToUpdate.forEach(BookFamilyModel::markAsTempOrder);

        bookFamilyRepository.saveAll(membersToUpdate);
        bookFamilyRepository.flush();

        membersToUpdate.forEach(BookFamilyModel::restoreFinalOrder);
    }

    @Transactional
    public BatchTransporter<UUID> removeMembersFromFamily(UUID familyId, List<BookFamilyDeleteRequest> deleteRequests) {
        ValidationResult<UUID, BookFamilyModel> validationResult = this.validateDeleteBookBatch(familyId, deleteRequests);

        this.removeMembers(validationResult.getValidRequests());

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<UUID, BookFamilyModel> validateDeleteBookBatch(
            UUID familyId,
            List<BookFamilyDeleteRequest> deleteRequests){
        ValidationResult<UUID, BookFamilyModel> validationResult = new ValidationResult<>();

        List<UUID> requestedMembersId = deleteRequests.stream().map(BookFamilyDeleteRequest::id).toList();

        Map<UUID, BookFamilyModel> mapRequestedMembers = bookFamilyService.getRequestedMembers(familyId, requestedMembersId);
        Set<UUID> membersToRemoveFromFamily = new HashSet<>();

        for(BookFamilyDeleteRequest deleteRequest : deleteRequests) {
            UUID memberId = deleteRequest.id();

            if(!membersToRemoveFromFamily.add(memberId)) {
                validationResult.addFailure(memberId, "Membro duplicado na requisição");
                continue;
            }

            if(!mapRequestedMembers.containsKey(memberId)) {
                validationResult.addFailure(memberId, "Membro não pertence a essa família");
                continue;
            }

            validationResult.addSuccess(memberId, "Membro removido da família com sucesso.");
            validationResult.addValid(mapRequestedMembers.get(memberId));
        }

        return validationResult;
    }

    private void removeMembers(List<BookFamilyModel> validRequests) {
        bookFamilyRepository.deleteAll(validRequests);
    }
}
