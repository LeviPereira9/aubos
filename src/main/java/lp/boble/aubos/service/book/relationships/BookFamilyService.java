package lp.boble.aubos.service.book.relationships;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyCreateRequest;
import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyDeleteRequest;
import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyResponse;
import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyUpdateRequest;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.family.BookFamilyMapper;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.family.FamilyModel;
import lp.boble.aubos.model.book.relationships.BookFamilyModel;
import lp.boble.aubos.repository.book.relationships.BookFamilyRepository;
import lp.boble.aubos.service.book.BookService;
import lp.boble.aubos.service.book.family.FamilyService;
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
    private final BookFamilyMapper bookFamilyMapper;

    @Transactional
    public BookFamilyResponse addMemberToFamily(UUID familyId, BookFamilyCreateRequest request) {

        this.validateMemberConflict(familyId, request.bookId());

        BookFamilyModel memberToAdd = this.createMemberToFamily(familyId, request);

        return bookFamilyMapper.fromModelToResponse(bookFamilyRepository.save(memberToAdd));
    }

    private void validateMemberConflict(UUID familyId, UUID bookId) {
        boolean hasConflict = bookFamilyRepository.existsByFamily_IdAndBook_Id(familyId, bookId);
        if(hasConflict) throw CustomDuplicateFieldException.bookFamily();
    }

    private BookFamilyModel createMemberToFamily(UUID familyId, BookFamilyCreateRequest request){
        BookModel bookToAdd = bookService.findBookOrThrow(request.bookId());
        FamilyModel familyDestination = familyService.findFamilyOrThrow(familyId);

        BookFamilyModel memberToAdd = bookFamilyMapper.fromCreateRequestToModel(request, bookToAdd, familyDestination);

        memberToAdd.setOrderInFamily(
                this.calculateAvailableOrderForFamily(familyId, memberToAdd.getOrderInFamily()));

        return memberToAdd;
    }

    private int calculateAvailableOrderForFamily(UUID familyId, int requestedOrder) {
        boolean orderConflict = bookFamilyRepository.existsByFamily_IdAndOrderInFamily(familyId, requestedOrder);

        if(orderConflict){
            return bookFamilyRepository.findMaxOrderInFamilyByFamilyId(familyId) + 1;
        }

        return requestedOrder;
    }

    @Transactional
    public BookFamilyResponse updateMemberFamily(UUID familyId, BookFamilyUpdateRequest request) {

        this.validateUpdateMember(familyId, request);

        BookFamilyModel bookFamilyToUpdate = this.prepareMemberToUpdate(familyId, request);

        return bookFamilyMapper.fromModelToResponse(bookFamilyRepository.save(bookFamilyToUpdate));
    }

    private BookFamilyModel prepareMemberToUpdate(UUID familyId, BookFamilyUpdateRequest request) {
        BookFamilyModel memberToUpdate = this.findMemberInFamily(familyId, request.id());

        bookFamilyMapper.toUpdateFromRequest(memberToUpdate, request);

        return memberToUpdate;
    }

    private void validateUpdateMember(UUID familyId, BookFamilyUpdateRequest request){
        boolean bookConflict = !bookFamilyRepository.existsByFamily_IdAndBook_Id(familyId, request.id());
        boolean orderConflict = bookFamilyRepository.existsByFamily_IdAndOrderInFamily(familyId, request.order());

        if(orderConflict) throw CustomDuplicateFieldException.orderFamily();
        if(bookConflict) throw CustomNotFoundException.bookFamily();
    }

    @Transactional
    public void removeMemberFromFamily(UUID familyId, BookFamilyDeleteRequest deleteRequest) {
        UUID bookId = deleteRequest.id();

        this.validateMemberToRemove(familyId, bookId);

        bookFamilyRepository.deleteByFamily_IdAndBook_Id(familyId, bookId);
    }

    private void validateMemberToRemove(UUID familyId, UUID bookId){
        boolean bookFamilyExists = bookFamilyRepository.existsByFamily_IdAndBook_Id(familyId, bookId);

        if(!bookFamilyExists){
            throw CustomNotFoundException.book();
        }
    }

    protected BookFamilyModel findMemberInFamily(UUID familyId, UUID bookId){

        return bookFamilyRepository.findByFamily_IdAndBook_Id(familyId, bookId)
                .orElseThrow(CustomNotFoundException::bookFamily);
    }

    protected Map<UUID, BookFamilyModel> getRequestedMembers(UUID familyId, List<UUID> requestedMembersId) {
        List<BookFamilyModel> requestedMembers = this.findRequestedMembersInFamily(familyId, requestedMembersId);

        return requestedMembers.stream().collect(Collectors.toMap(BookFamilyModel::getId, Function.identity()));
    }

    protected List<BookFamilyModel> findAllMembersInFamily(UUID familyId){
        return bookFamilyRepository.findAllByFamily_Id(familyId);
    }

    private List<BookFamilyModel> findRequestedMembersInFamily(UUID familyId, List<UUID> requestedFamiliesId) {
        List<BookFamilyModel> requested = bookFamilyRepository.findAllByFamily_IdAndIdIn(familyId, requestedFamiliesId);

        if(requested.isEmpty()) throw CustomNotFoundException.book();

        return requested;
    }

    public Map<UUID, BookFamilyModel> getCurrentMembers(UUID familyId) {
        List<BookFamilyModel> currentMembers = this.findAllMembersInFamily(familyId);

        return currentMembers.stream().collect(Collectors.toMap(BookFamilyModel::getId, Function.identity()));
    }
}