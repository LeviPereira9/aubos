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

@Service
@RequiredArgsConstructor
public class BookFamilyService {
    private final BookFamilyRepository bookFamilyRepository;
    private final BookService bookService;
    private final FamilyService familyService;
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
    public BookFamilyResponse updateBookFamily(UUID familyId, BookFamilyUpdateRequest request) {

        this.validateUpdateBook(familyId, request);

        BookFamilyModel bookFamilyToUpdate = this.prepareBookToUpdate(familyId, request);

        return bookFamilyMapper.fromModelToResponse(bookFamilyRepository.save(bookFamilyToUpdate));
    }

    private BookFamilyModel prepareBookToUpdate(UUID familyId, BookFamilyUpdateRequest request) {
        BookFamilyModel toUpdate = this.findBookInFamily(familyId, request.bookId());

        bookFamilyMapper.toUpdateFromRequest(toUpdate, request);

        return toUpdate;
    }

    private void validateUpdateBook(UUID familyId, BookFamilyUpdateRequest request){
        boolean bookConflict = !bookFamilyRepository.existsByFamilyIdAndBookId(familyId, request.bookId());
        boolean orderConflict = bookFamilyRepository.existsByFamilyIdAndOrderInFamily(familyId, request.order());

        if(orderConflict) throw CustomDuplicateFieldException.orderFamily();
        if(bookConflict) throw CustomNotFoundException.bookFamily();
    }

    @Transactional
    public void removeBookFromFamily(UUID familyId, BookFamilyDeleteRequest deleteRequest) {
        UUID bookId = deleteRequest.bookId();

        this.validateBookToRemove(familyId, bookId);

        bookFamilyRepository.deleteByFamilyIdAndBookId(familyId, bookId);
    }

    private void validateBookToRemove(UUID familyId, UUID bookId){
        boolean bookFamilyExists = bookFamilyRepository.existsByFamilyIdAndBookId(familyId, bookId);

        if(!bookFamilyExists){
            throw CustomNotFoundException.book();
        }
    }

    public BookFamilyModel findBookInFamily(UUID familyId, UUID bookId){

        return bookFamilyRepository.findByFamilyIdAndBookId(familyId, bookId)
                .orElseThrow(CustomNotFoundException::bookFamily);
    }

}