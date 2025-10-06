package lp.boble.aubos.controller.book.relationships;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
import lp.boble.aubos.dto.book.relationships.bookTag.BookTagDeleteRequest;
import lp.boble.aubos.dto.book.relationships.bookTag.BookTagRequest;
import lp.boble.aubos.dto.book.relationships.bookTag.BookTagResponse;
import lp.boble.aubos.response.batch.BatchResponse;
import lp.boble.aubos.response.batch.BatchResponseBuilder;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.relationships.tag.BookTagBatchService;
import lp.boble.aubos.service.book.relationships.tag.BookTagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Tags do Livro",
        description = "Operações para gerenciar as tags e categorias associadas a um livro específico"
)
@RestController
@RequestMapping("${api.prefix}/{bookId}/book-tag")
@RequiredArgsConstructor
public class BookTagController {


    private final BookTagService bookTagService;
    private final BookTagBatchService bookTagBatchService;

    @Operation(
            summary = "Listar tags do livro",
            description = "Retorna todas as tags e categorias associadas a um livro específico."
    )
    @GetMapping
    public ResponseEntity<SuccessResponse<List<BookTagResponse>>>
    getAllBookTagsInBook(@PathVariable UUID bookId) {
        List<BookTagResponse> content = bookTagService.findAllTagsInBook(bookId);
        HttpStatus code = HttpStatus.OK;

        SuccessResponse<List<BookTagResponse>> response =
                new SuccessResponseBuilder<List<BookTagResponse>>()
                        .operation("GET")
                        .code(code)
                        .message("Tags encontradas com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.ok()
                .eTag("")
                .cacheControl(CacheProfiles.relationships())
                .body(response);
    }

    @Operation(
            summary = "Adicionar tag ao livro",
            description = "Associa uma nova tag ou categoria a um livro específico."
    )
    @PostMapping
    public ResponseEntity<SuccessResponse<BookTagResponse>> addTagOnBook(@PathVariable UUID bookId, @RequestBody BookTagRequest request){

        BookTagResponse content = bookTagService.addTagToBook(bookId, request);
        HttpStatus code = HttpStatus.CREATED;

        SuccessResponse<BookTagResponse> response =
                new SuccessResponseBuilder<BookTagResponse>()
                        .operation("POST")
                        .code(code)
                        .message("Tag adicionada com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(code).body(response);
    }

    @Operation(
            summary = "Remover tag do livro",
            description = "Remove uma tag específica da associação com o livro."
    )
    @DeleteMapping("/{bookTagId}")
    public ResponseEntity<SuccessResponse<Void>> removeTagFromBook(
            @PathVariable UUID bookId,
            @PathVariable UUID bookTagId) {
        bookTagService.removeTagFromBook(bookId, bookTagId);
        HttpStatus code = HttpStatus.OK;

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .code(code)
                        .message("Tag removida com sucesso.")
                        .build();

        return ResponseEntity.status(code).body(response);
    }

    @Operation(
            summary = "Adicionar tags em lote ao livro",
            description = "Associa múltiplas tags a um livro de uma vez através de requisição em lote."
    )
    @PostMapping("/batch")
    public ResponseEntity<BatchResponse<Integer>> batchAddTagsInBook(
            @PathVariable UUID bookId,
            @RequestBody List<BookTagRequest> requests
    ){
        BatchTransporter<Integer> content = bookTagBatchService.batchAddTagToBook(bookId, requests);
        HttpStatus code = content.getStatus();

        BatchResponse<Integer> response =
                new BatchResponseBuilder<Integer>()
                        .operation("POST")
                        .code(code)
                        .message("Requisição POST realizada com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(code).body(response);
    }

    @Operation(
            summary = "Remover tags em lote do livro",
            description = "Remove múltiplas tags da associação com o livro de uma vez através de requisição em lote."
    )
    @DeleteMapping("/batch")
    public ResponseEntity<BatchResponse<UUID>> batchRemoveTagsInBook(
            @PathVariable UUID bookId,
            @RequestBody List<BookTagDeleteRequest> requests
    ){
        BatchTransporter<UUID> content = bookTagBatchService.batchRemoveTagsFromBook(bookId, requests);
        HttpStatus code = content.getStatus();

        BatchResponse<UUID> response =
                new BatchResponseBuilder<UUID>()
                        .operation("DELETE")
                        .code(code)
                        .message("Requisição de DELETE realizada com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(code).body(response);
    }
}
