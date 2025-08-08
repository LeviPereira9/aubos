package lp.boble.aubos.controller.book;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.BookFamilyCreateRequest;
import lp.boble.aubos.dto.book.relationships.BookFamilyDeleteRequest;
import lp.boble.aubos.dto.book.relationships.BookFamilyUpdateRequest;
import lp.boble.aubos.response.batch.BatchResponse;
import lp.boble.aubos.response.batch.BatchResponseBuilder;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.service.book.relationships.BookFamilyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/book-family")
@RequiredArgsConstructor
public class BookFamilyController {
    private final BookFamilyService bookFamilyService;

    @PostMapping("/family/{familyId}/books")
    public ResponseEntity<Void> addBookFamily(
            @PathVariable("familyId") UUID familyId,
            @RequestBody BookFamilyCreateRequest request){

        bookFamilyService.addBookToFamily(familyId, request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/family/{familyId}/books/batch")
    public ResponseEntity<BatchResponse<UUID>> addBookFamilyBatch(
            @PathVariable("familyId") UUID familyId,
            @RequestBody List<BookFamilyCreateRequest> requests
            ){

        BatchTransporter<UUID> result = bookFamilyService.addBooksToFamily(familyId, requests);
        int status = result.getStatus();

        BatchResponse<UUID> response = new BatchResponse<>();
        response.setOperation("POST");
        response.setCode(status);
        response.setSuccesses(result.getSuccesses());
        response.setFailures(result.getFailures());

        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/family/books/{bookFamilyId}")
    public ResponseEntity<Void> updateBookFamily(
            @PathVariable("bookFamilyId") UUID bookFamilyId,
            @RequestBody BookFamilyUpdateRequest request){

        bookFamilyService.updateBookFamily(bookFamilyId, request);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/family/{familyId}/books/batch")
    public ResponseEntity<BatchResponse<UUID>> updateBookFamilyBatch(
            @PathVariable("familyId") UUID familyId,
            @RequestBody List<BookFamilyUpdateRequest> requests
    ){
        BatchTransporter<UUID> result = bookFamilyService.updateBookFamilies(familyId, requests);
        int status = result.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("PUT")
                .code(status)
                .message("Success")
                .successes(result.getSuccesses())
                .failures(result.getFailures())
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/family/{familyId}/books/")
    public ResponseEntity<Void> removeBookFromFamily(
            @PathVariable("familyId") UUID familyId,
            @RequestBody BookFamilyDeleteRequest deleteRequest){
        bookFamilyService.removeBookFromFamily(familyId, deleteRequest);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/family/{familyId}/books/batch")
    public ResponseEntity<BatchResponse<UUID>> removeBooksFromFamily(
            @PathVariable("familyId") UUID familyId,
            @RequestBody List<BookFamilyDeleteRequest> deleteRequests
    ){
        BatchTransporter<UUID> result = bookFamilyService.removeBooksFromFamily(familyId, deleteRequests);
        int status = result.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("DELETE")
                .code(status)
                .message("Success")
                .successes(result.getSuccesses())
                .failures(result.getFailures())
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
