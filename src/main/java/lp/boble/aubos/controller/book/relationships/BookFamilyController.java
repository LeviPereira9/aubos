package lp.boble.aubos.controller.book.relationships;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.bookFamily.BookFamilyCreateRequest;
import lp.boble.aubos.dto.book.relationships.bookFamily.BookFamilyDeleteRequest;
import lp.boble.aubos.dto.book.relationships.bookFamily.BookFamilyResponse;
import lp.boble.aubos.dto.book.relationships.bookFamily.BookFamilyUpdateRequest;
import lp.boble.aubos.response.batch.BatchResponse;
import lp.boble.aubos.response.batch.BatchResponseBuilder;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.relationships.family.BookFamilyBatchService;
import lp.boble.aubos.service.book.relationships.family.BookFamilyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Famílias de Livros",
        description = "Operações para gerenciar a relação entre livros e famílias/séries literárias"
)
@RestController
@RequestMapping("${api.prefix}/book-family")
@RequiredArgsConstructor
public class BookFamilyController {
    private final BookFamilyService bookFamilyService;
    private final BookFamilyBatchService bookFamilyBatchService;

    @Operation(
            summary = "Adicionar livro à família",
            description = "Associa um livro a uma família/série literária específica."
    )
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

    @Operation(
            summary = "Adicionar livros em lote à família",
            description = "Associa múltiplos livros a uma família/série literária de uma vez através de requisição em lote."
    )
    @PostMapping("/family/{familyId}/books/batch")
    public ResponseEntity<BatchResponse<UUID>> addBookFamilyBatch(
            @PathVariable("familyId") UUID familyId,
            @RequestBody List<BookFamilyCreateRequest> requests
            ){

        BatchTransporter<UUID> content = bookFamilyBatchService.addBooksToFamily(familyId, requests);
        HttpStatus code = content.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("POST")
                .code(code)
                .message("Requisição concluída com sucesso.")
                .content(content)
                .build();

        return ResponseEntity.status(code).body(response);
    }

    @Operation(
            summary = "Atualizar livro na família",
            description = "Atualiza as informações de um livro específico dentro de uma família/série literária."
    )
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

    @Operation(
            summary = "Atualizar livros em lote na família",
            description = "Atualiza múltiplos livros dentro de uma família/série literária de uma vez através de requisição em lote."
    )
    @PutMapping("/family/{familyId}/books/batch")
    public ResponseEntity<BatchResponse<UUID>> updateBookFamilyBatch(
            @PathVariable("familyId") UUID familyId,
            @RequestBody List<BookFamilyUpdateRequest> requests
    ){
        BatchTransporter<UUID> result = bookFamilyBatchService.updateBooksBatch(familyId, requests);
        HttpStatus code = result.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("PUT")
                .code(code)
                .message("Requisição concluída com sucesso.")
                .content(result)
                .build();

        return ResponseEntity.status(code).body(response);
    }

    @Operation(
            summary = "Remover livro da família",
            description = "Remove a associação de um livro específico com uma família/série literária."
    )
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

    @Operation(
            summary = "Remover livros em lote da família",
            description = "Remove a associação de múltiplos livros com uma família/série literária de uma vez através de requisição em lote."
    )
    @DeleteMapping("/family/{familyId}/books/batch")
    public ResponseEntity<BatchResponse<UUID>> removeBooksFromFamily(
            @PathVariable("familyId") UUID familyId,
            @RequestBody List<BookFamilyDeleteRequest> deleteRequests
    ){
        BatchTransporter<UUID> result = bookFamilyBatchService.removeMembersFromFamily(familyId, deleteRequests);
        HttpStatus code = result.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("DELETE")
                .code(code)
                .message("Requisição concluída com sucesso.")
                .content(result)
                .build();

        return ResponseEntity.status(code).body(response);
    }
}
