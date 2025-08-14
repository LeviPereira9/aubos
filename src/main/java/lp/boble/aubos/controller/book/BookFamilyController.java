package lp.boble.aubos.controller.book;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.BookFamilyCreateRequest;
import lp.boble.aubos.dto.book.relationships.BookFamilyDeleteRequest;
import lp.boble.aubos.dto.book.relationships.BookFamilyResponse;
import lp.boble.aubos.dto.book.relationships.BookFamilyUpdateRequest;
import lp.boble.aubos.response.batch.BatchResponse;
import lp.boble.aubos.response.batch.BatchResponseBuilder;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
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

    @PostMapping("/family/{familyId}/books")
    public ResponseEntity<SuccessResponse<BookFamilyResponse>> addBookFamily(
            @PathVariable("familyId") UUID familyId,
            @RequestBody BookFamilyCreateRequest request){

        BookFamilyResponse data = bookFamilyService.addBookToFamily(familyId, request);

        SuccessResponse<BookFamilyResponse> response =
                new SuccessResponseBuilder<BookFamilyResponse>()
                        .operation("POST")
                        .code(HttpStatus.CREATED)
                        .message("Livro adicionado à coleção.")
                        .content(data)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/family/{familyId}/books/batch")
    public ResponseEntity<BatchResponse<UUID>> addBookFamilyBatch(
            @PathVariable("familyId") UUID familyId,
            @RequestBody List<BookFamilyCreateRequest> requests
            ){

        BatchTransporter<UUID> result = bookFamilyService.addBooksToFamily(familyId, requests);
        int status = result.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("POST")
                .code(status)
                .message("Requisição concluída com sucesso.")
                .data(result)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/family/{familyId}/book")
    public ResponseEntity<SuccessResponse<BookFamilyResponse>> updateBookFamily(
            @PathVariable("familyId") UUID familyId,
            @RequestBody BookFamilyUpdateRequest request){

        BookFamilyResponse data = bookFamilyService.updateBookFamily(familyId, request);

        SuccessResponse<BookFamilyResponse> response =
                new SuccessResponseBuilder<BookFamilyResponse>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("Livro alterado com sucesso.")
                        .content(data)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
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
                .message("Requisição concluída com sucesso.")
                .data(result)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/family/{familyId}/books")
    public ResponseEntity<SuccessResponse<Void>> removeBookFromFamily(
            @PathVariable("familyId") UUID familyId,
            @RequestBody BookFamilyDeleteRequest deleteRequest){
        bookFamilyService.removeBookFromFamily(familyId, deleteRequest);

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
        BatchTransporter<UUID> result = bookFamilyService.removeBooksFromFamily(familyId, deleteRequests);
        int status = result.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("DELETE")
                .code(status)
                .message("Requisição concluída com sucesso.")
                .data(result)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
