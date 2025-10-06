package lp.boble.aubos.controller.book.relationships;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
import lp.boble.aubos.dto.book.relationships.bookLanguage.BookLanguageAddRequest;
import lp.boble.aubos.dto.book.relationships.bookLanguage.BookLanguageCreatedResponse;
import lp.boble.aubos.dto.book.relationships.bookLanguage.BookLanguageDelRequest;
import lp.boble.aubos.dto.book.relationships.bookLanguage.BookLanguageResponse;
import lp.boble.aubos.response.batch.BatchResponse;
import lp.boble.aubos.response.batch.BatchResponseBuilder;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.relationships.language.BookLanguageBatchService;
import lp.boble.aubos.service.book.relationships.language.BookLanguageService;
import lp.boble.aubos.util.ResourceLocationUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(
        name = "Idiomas do Livro",
        description = "Operações para gerenciar os idiomas em que um livro está disponível"
)
@RestController
@RequestMapping("${api.prefix}/book/{bookId}/languages")
@RequiredArgsConstructor
public class BookLanguageController {
    private final BookLanguageService bookLanguageService;
    private final BookLanguageBatchService bookLanguageBatchService;

    @Operation(
            summary = "Listar idiomas disponíveis do livro",
            description = "Retorna todos os idiomas em que o livro está disponível ou foi traduzido."
    )
    @GetMapping
    public ResponseEntity<SuccessResponse<List<BookLanguageResponse>>>
    getAllAvailableLanguagesInBook(@PathVariable UUID bookId){
        List<BookLanguageResponse> content = bookLanguageService.getAllAvailableLanguages(bookId);

        SuccessResponse<List<BookLanguageResponse>> response =
                new SuccessResponseBuilder<List<BookLanguageResponse>>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Línguas encontradas com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.ok().eTag("").cacheControl(CacheProfiles.bookPublic()).body(response);
    }

    @Operation(
            summary = "Adicionar idioma ao livro",
            description = "Adiciona um novo idioma à lista de disponibilidade do livro (tradução ou versão original)."
    )
    @PostMapping
    public ResponseEntity<SuccessResponse<BookLanguageCreatedResponse>> addBookLanguage(
            @PathVariable UUID bookId,
            @RequestBody BookLanguageAddRequest request){

        BookLanguageCreatedResponse content = bookLanguageService.addLanguageToBook(bookId, request);

        URI location = ResourceLocationUtil.buildLocation(content.id());

        SuccessResponse<BookLanguageCreatedResponse> response =
                new SuccessResponseBuilder<BookLanguageCreatedResponse>()
                        .operation("POST")
                        .code(HttpStatus.CREATED)
                        .message("Língua adicionada ao livro com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(
            summary = "Remover idioma do livro",
            description = "Remove um idioma específico da lista de disponibilidade do livro."
    )
    @DeleteMapping("/{bookLanguageId}")
    public ResponseEntity<SuccessResponse<Void>> deleteBookLanguage(@PathVariable UUID bookId, @PathVariable UUID bookLanguageId){
        bookLanguageService.deleteBookLanguage(bookId, bookLanguageId);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .code(HttpStatus.OK)
                        .message("Língua removida do livro com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Adicionar idiomas em lote ao livro",
            description = "Adiciona múltiplos idiomas à lista de disponibilidade do livro de uma vez através de requisição em lote."
    )
    @PostMapping("/batch")
    public ResponseEntity<BatchResponse<Integer>> addLanguagesToBook(
            @PathVariable UUID bookId,
            @RequestBody List<BookLanguageAddRequest> requests){

        BatchTransporter<Integer> content = bookLanguageBatchService.addLanguagesToBook(bookId, requests);
        HttpStatus code = content.getStatus();

        BatchResponse<Integer> response = new BatchResponseBuilder<Integer>()
                .operation("POST")
                .message("Requisição de POST concluída com sucesso.")
                .content(content)
                .code(code)
                .build();

        return ResponseEntity.status(code).body(response);
    }

    @Operation(
            summary = "Remover idiomas em lote do livro",
            description = "Remove múltiplos idiomas da lista de disponibilidade do livro de uma vez através de requisição em lote."
    )
    @DeleteMapping("/batch")
    public ResponseEntity<BatchResponse<UUID>> deleteLanguagesFromBook(
            @PathVariable UUID bookId,
            @RequestBody List<BookLanguageDelRequest> requests
    ){
        BatchTransporter<UUID> content = bookLanguageBatchService.batchDeleteBookLanguages(bookId, requests);
        HttpStatus code = content.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("DELETE")
                .message("Requisição de DELETE concluída com sucesso.")
                .content(content)
                .code(code)
                .build();

        return ResponseEntity.status(code).body(response);
    }


}
