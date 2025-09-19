package lp.boble.aubos.controller.book;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyCreateRequest;
import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyDeleteRequest;
import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyResponse;
import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyUpdateRequest;
import lp.boble.aubos.response.batch.BatchResponse;
import lp.boble.aubos.response.batch.BatchResponseBuilder;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.relationships.BookFamilyBatchService;
import lp.boble.aubos.service.book.relationships.BookFamilyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/book-family")
@RequiredArgsConstructor
public class BookFamilyController {
    private final BookFamilyService bookFamilyService;
    private final BookFamilyBatchService bookFamilyBatchService;

    @PostMapping("/family/{familyId}/books")
    public ResponseEntity<SuccessResponse<BookFamilyResponse>> addBookFamily(
            @PathVariable("familyId") UUID familyId,
            @RequestBody BookFamilyCreateRequest request){

        BookFamilyResponse content = bookFamilyService.addMemberToFamily(familyId, request);

        SuccessResponse<BookFamilyResponse> response =
                new SuccessResponseBuilder<BookFamilyResponse>()
                        .operation("POST")
                        .code(HttpStatus.CREATED)
                        .message("Livro adicionado à coleção.")
                        .content(content)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/family/{familyId}/books/batch")
    public ResponseEntity<BatchResponse<UUID>> addBookFamilyBatch(
            @PathVariable("familyId") UUID familyId,
            @RequestBody List<BookFamilyCreateRequest> requests
            ){

        BatchTransporter<UUID> content = bookFamilyBatchService.addBooksToFamily(familyId, requests);
        int status = content.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("POST")
                .code(status)
                .message("Requisição concluída com sucesso.")
                .content(content)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/family/{familyId}/book")
    public ResponseEntity<SuccessResponse<BookFamilyResponse>> updateBookFamily(
            @PathVariable("familyId") UUID familyId,
            @RequestBody BookFamilyUpdateRequest request){

        BookFamilyResponse content = bookFamilyService.updateMemberFamily(familyId, request);

        SuccessResponse<BookFamilyResponse> response =
                new SuccessResponseBuilder<BookFamilyResponse>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("Livro alterado com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/family/{familyId}/books/batch")
    public ResponseEntity<BatchResponse<UUID>> updateBookFamilyBatch(
            @PathVariable("familyId") UUID familyId,
            @RequestBody List<BookFamilyUpdateRequest> requests
    ){
        BatchTransporter<UUID> result = bookFamilyBatchService.updateBooksBatch(familyId, requests);
        int status = result.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("PUT")
                .code(status)
                .message("Requisição concluída com sucesso.")
                .content(result)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/family/{familyId}/books")
    public ResponseEntity<SuccessResponse<Void>> removeBookFromFamily(
            @PathVariable("familyId") UUID familyId,
            @RequestBody BookFamilyDeleteRequest deleteRequest){
        bookFamilyService.removeMemberFromFamily(familyId, deleteRequest);

        SuccessResponse<Void> response = new SuccessResponseBuilder<Void>()
                .operation("DELETE")
                .code(HttpStatus.OK)
                .message("Livro removido com sucesso.")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/family/{familyId}/books/batch")
    public ResponseEntity<BatchResponse<UUID>> removeBooksFromFamily(
            @PathVariable("familyId") UUID familyId,
            @RequestBody List<BookFamilyDeleteRequest> deleteRequests
    ){
        BatchTransporter<UUID> result = bookFamilyBatchService.removeMembersFromFamily(familyId, deleteRequests);
        int status = result.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("DELETE")
                .code(status)
                .message("Requisição concluída com sucesso.")
                .content(result)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
