package lp.boble.aubos.controller.book.relationships;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
import lp.boble.aubos.dto.book.parts.BookAddContributor;
import lp.boble.aubos.dto.book.relationships.bookContributor.*;
import lp.boble.aubos.response.batch.BatchResponse;
import lp.boble.aubos.response.batch.BatchResponseBuilder;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.relationships.contributor.BookContributorBatchService;
import lp.boble.aubos.service.book.relationships.contributor.BookContributorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Contribuidores do Livro",
        description = "Operações para gerenciar a relação entre livros e seus contribuidores (autores, tradutores, etc.)"
)
@RestController
@RequestMapping("${api.prefix}/book/{bookId}/contributors")
@RequiredArgsConstructor
public class BookContributorController {


    private final BookContributorService bookContributorService;
    private final BookContributorBatchService bookContributorBatchService;

    @Operation(
            summary = "Listar todos os contribuidores do livro",
            description = "Retorna todos os contribuidores associados a um livro específico, organizados por função."
    )
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
                .cacheControl(CacheProfiles.relationships())
                .body(response);
    }

    @Operation(
            summary = "Listar contribuidores por função",
            description = "Retorna os contribuidores de um livro filtrados por uma função específica (autor, tradutor, etc.)."
    )
    @GetMapping("/role")
    public ResponseEntity<SuccessResponse<List<BookContributorResponse>>>
    getBookContributorsByRole(
            @PathVariable UUID bookId,
            @RequestParam(defaultValue = "1") int roleId){
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
                .cacheControl(CacheProfiles.relationships())
                .body(response);
    }

    @Operation(
            summary = "Adicionar contribuidor ao livro",
            description = "Associa um novo contribuidor a um livro com uma função específica."
    )
    @PostMapping
    public ResponseEntity<SuccessResponse<BookContributorResponse>> addContributorToBook(
            @PathVariable UUID bookId,
            @RequestBody BookAddContributor request){

        BookContributorResponse content = bookContributorService.addContributorToBook(bookId, request);

        SuccessResponse<BookContributorResponse> response =
                new SuccessResponseBuilder<BookContributorResponse>()
                        .operation("POST")
                        .code(HttpStatus.CREATED)
                        .message("Contribuidor adicionado com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Atualizar contribuidor do livro",
            description = "Atualiza as informações de um contribuidor específico em um livro (função, datas, etc.)."
    )
    @PatchMapping("/{booKContributorId}")
    public ResponseEntity<SuccessResponse<Void>> updateBookContributor(
            @PathVariable UUID bookId,
            @PathVariable UUID booKContributorId,
            @RequestBody BookContributorUpdateRequest request){

        bookContributorService.updateContributorOnBook(bookId, booKContributorId, request);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("PATCH")
                        .code(HttpStatus.OK)
                        .message("Contribuidor atualizado com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Remover contribuidor do livro",
            description = "Remove a associação de um contribuidor específico com o livro."
    )
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

    @Operation(
            summary = "Adicionar contribuidores em lote",
            description = "Associa múltiplos contribuidores a um livro de uma vez através de requisição em lote."
    )
    @PostMapping("/batch")
    public ResponseEntity<BatchResponse<String>> addBatchContributors(@PathVariable UUID bookId, @RequestBody List<BookAddContributor> requests){
        BatchTransporter<String> result = bookContributorBatchService.addContributorsToBook(bookId, requests);
        HttpStatus code = result.getStatus();

        BatchResponse<String> response = new BatchResponseBuilder<String>()
                .operation("POST")
                .code(code)
                .message("Requisição de POST concluída com sucesso.")
                .content(result)
                .build();

        return ResponseEntity.status(code).body(response);
    }

    @Operation(
            summary = "Atualizar contribuidores em lote",
            description = "Atualiza múltiplos contribuidores de um livro de uma vez através de requisição em lote."
    )
    @PatchMapping("/batch")
    public ResponseEntity<BatchResponse<UUID>> updateBatchContributors(@PathVariable UUID bookId, @RequestBody List<BookContributorUpdateBatchRequest> requests){
        BatchTransporter<UUID> result = bookContributorBatchService.updateBatch(bookId, requests);
        HttpStatus code = result.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("PATCH")
                .code(code)
                .message("Requisição de POST concluída com sucesso.")
                .content(result)
                .build();

        return ResponseEntity.status(code).body(response);
    }

    @Operation(
            summary = "Remover contribuidores em lote",
            description = "Remove múltiplas associações de contribuidores com o livro de uma vez através de requisição em lote."
    )
    @DeleteMapping("/batch")
    public ResponseEntity<BatchResponse<UUID>> deleteBatchContributors(@PathVariable UUID bookId, @RequestBody List<BookContributorDeleteRequest> requests){
        BatchTransporter<UUID> result = bookContributorBatchService.deleteBatch(bookId, requests);
        HttpStatus code = result.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("DELETE")
                .code(code)
                .message("Requisição de POST concluída com sucesso.")
                .content(result)
                .build();

        return ResponseEntity.status(code).body(response);
    }
}
