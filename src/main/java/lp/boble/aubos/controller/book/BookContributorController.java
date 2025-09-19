package lp.boble.aubos.controller.book;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
import lp.boble.aubos.dto.book.parts.BookAddContributor;
import lp.boble.aubos.dto.book.relationships.BookContributor.*;
import lp.boble.aubos.response.batch.BatchResponse;
import lp.boble.aubos.response.batch.BatchResponseBuilder;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.relationships.BookContributorBatchService;
import lp.boble.aubos.service.book.relationships.BookContributorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/book/{bookId}/contributors")
@RequiredArgsConstructor
public class BookContributorController {


    private final BookContributorService bookContributorService;
    private final BookContributorBatchService bookContributorBatchService;

    @GetMapping
    public ResponseEntity<SuccessResponse<BookContributorsResponse>>
    getBookContributors(@PathVariable UUID bookId){

        BookContributorsResponse content = bookContributorService.findContributors(bookId);

        SuccessResponse<BookContributorsResponse> response =
                new SuccessResponseBuilder<BookContributorsResponse>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Contribuidores encontrados com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.ok()
                .eTag("Aham")
                .cacheControl(CacheProfiles.bookPublic())
                .body(response);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<SuccessResponse<List<BookContributorResponse>>>
    getBookContributorsByRole(@PathVariable UUID bookId, @PathVariable int roleId){
        List<BookContributorResponse> content = bookContributorService.findContributorsByRole(bookId, roleId);

        SuccessResponse<List<BookContributorResponse>> response =
                new SuccessResponseBuilder<List<BookContributorResponse>>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Contribuidores encontrados com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.ok()
                .eTag("")
                .cacheControl(CacheProfiles.bookPublic())
                .body(response);
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> addContributorToBook(
            @PathVariable UUID bookId,
            @RequestBody BookAddContributor request){

        bookContributorService.addContributorToBook(bookId, request);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("POST")
                        .code(HttpStatus.OK)
                        .message("Contribuidor adicionado com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping
    public ResponseEntity<SuccessResponse<Void>> updateBookContributor(
            @PathVariable UUID bookId,
            @RequestBody BookContributorUpdateRequest request){

        bookContributorService.updateContributorOnBook(bookId, request);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("PATCH")
                        .code(HttpStatus.OK)
                        .message("Contribuidor adicionado com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{bookContributorId}")
    public ResponseEntity<SuccessResponse<Void>> deleteBookContributor(@PathVariable UUID bookId, @PathVariable UUID bookContributorId){
        bookContributorService.deleteContributorFromBook(bookId, bookContributorId);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .code(HttpStatus.OK)
                        .message("Contribuidor removido com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<BatchResponse<UUID>> addBatchContributors(@PathVariable UUID bookId, @RequestBody List<BookAddContributor> requests){
        BatchTransporter<UUID> result = bookContributorBatchService.addContributorsToBook(bookId, requests);
        int code = result.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("POST")
                .code(code)
                .message("Requisição de POST concluída com sucesso.")
                .content(result)
                .build();

        return ResponseEntity.status(code).body(response);
    }

    @PatchMapping("/batch")
    public ResponseEntity<BatchResponse<UUID>> updateBatchContributors(@PathVariable UUID bookId, @RequestBody List<BookContributorUpdateBatchRequest> requests){
        BatchTransporter<UUID> result = bookContributorBatchService.updateBatch(bookId, requests);
        int code = result.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("PATCH")
                .code(code)
                .message("Requisição de POST concluída com sucesso.")
                .content(result)
                .build();

        return ResponseEntity.status(code).body(response);
    }

    @PatchMapping("/batch")
    public ResponseEntity<BatchResponse<UUID>> deleteBatchContributors(@PathVariable UUID bookId, @RequestBody List<BookContributorDeleteRequest> requests){
        BatchTransporter<UUID> result = bookContributorBatchService.deleteBatch(bookId, requests);
        int code = result.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("DELETE")
                .code(code)
                .message("Requisição de POST concluída com sucesso.")
                .content(result)
                .build();

        return ResponseEntity.status(code).body(response);
    }
}
