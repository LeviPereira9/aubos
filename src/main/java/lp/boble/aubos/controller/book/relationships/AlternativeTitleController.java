package lp.boble.aubos.controller.book.relationships;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
import lp.boble.aubos.dto.book.relationships.bookAlternativeTitle.AlternativeTitleRequest;
import lp.boble.aubos.dto.book.relationships.bookAlternativeTitle.AlternativeTitleResponse;
import lp.boble.aubos.response.batch.BatchResponse;
import lp.boble.aubos.response.batch.BatchResponseBuilder;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.relationships.alternativetitle.AlternativeTitleBatchService;
import lp.boble.aubos.service.book.relationships.alternativetitle.AlternativeTitleService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Títulos Alternativos",
        description = "Operações para gerenciar títulos alternativos de um livro específico"
)
@RestController
@RequestMapping("${api.prefix}/{bookId}/alternative-title")
@RequiredArgsConstructor
public class AlternativeTitleController {

    private final AlternativeTitleService alternativeTitleService;
    private final AlternativeTitleBatchService alternativeTitleBatchService;

    @Operation(
            summary = "Listar títulos alternativos do livro",
            description = "Retorna todos os títulos alternativos associados a um livro específico."
    )
    @GetMapping
    public ResponseEntity<SuccessResponse<List<AlternativeTitleResponse>>>
    findAllAlternativeTitlesAtBook(@PathVariable UUID bookId){

        List<AlternativeTitleResponse> content = alternativeTitleService.getAlternativeTitlesByBook(bookId);

        SuccessResponse<List<AlternativeTitleResponse>> response =
                new SuccessResponseBuilder<List<AlternativeTitleResponse>>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Títulos alternativos encontrados com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.ok().eTag("").cacheControl(CacheProfiles.relationships()).body(response);
    }

    @Operation(
            summary = "Adicionar título alternativo",
            description = "Adiciona um novo título alternativo a um livro específico."
    )
    @PostMapping
    public ResponseEntity<SuccessResponse<AlternativeTitleResponse>>
    addAlternativeTitle(@PathVariable UUID bookId, @RequestBody AlternativeTitleRequest request){

        AlternativeTitleResponse content = alternativeTitleService.addAlternativeTitle(bookId, request);
        HttpStatus code = HttpStatus.CREATED;

        SuccessResponse<AlternativeTitleResponse> response =
                new SuccessResponseBuilder<AlternativeTitleResponse>()
                        .operation("POST")
                        .code(code)
                        .message("Título alternativo adicionado com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(code).body(response);
    }

    @Operation(
            summary = "Atualizar título alternativo",
            description = "Atualiza um título alternativo específico de um livro."
    )
    @PutMapping("/{alternativeTitleId}")
    public ResponseEntity<SuccessResponse<AlternativeTitleResponse>>
    updateAlternativeTitle(@PathVariable UUID bookId, @PathVariable UUID alternativeTitleId,  @RequestBody AlternativeTitleRequest request){
        AlternativeTitleResponse content = alternativeTitleService.updateAlternativeTitle(bookId, alternativeTitleId, request);
        HttpStatus code = HttpStatus.OK;

        SuccessResponse<AlternativeTitleResponse> response =
                new SuccessResponseBuilder<AlternativeTitleResponse>()
                        .operation("PUT")
                        .code(code)
                        .message("Título alternativo alterado com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(code).body(response);
    }

    @Operation(
            summary = "Remover título alternativo",
            description = "Remove um título alternativo específico de um livro."
    )
    @DeleteMapping("/{alternativeTitleId}")
    public ResponseEntity<SuccessResponse<Void>> deleteAlternativeTitleFromBook(@PathVariable UUID bookId, @PathVariable UUID alternativeTitleId){
        alternativeTitleService.removeAlternativeTitle(bookId, alternativeTitleId);

        HttpStatus code = HttpStatus.OK;

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .code(code)
                        .message("Título alternativo removido com sucesso.")
                        .build()
                ;

        return ResponseEntity.status(code).body(response);
    }

    @Operation(
            summary = "Adicionar títulos alternativos em lote",
            description = "Adiciona múltiplos títulos alternativos a um livro de uma vez através de requisição em lote."
    )
    @PostMapping("/batch")
    public ResponseEntity<BatchResponse<String>>
    addAlternativeTitleInBatch(@PathVariable UUID bookId, @RequestBody List<AlternativeTitleRequest> requests){

        BatchTransporter<String> content = alternativeTitleBatchService.addAlternativeTitlesInBatch(bookId, requests);
        HttpStatus code = content.getStatus();

        BatchResponse<String> response =
                new BatchResponseBuilder<String>()
                        .operation("POST")
                        .code(code)
                        .message("Requisição POST concluída com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(code).body(response);
    }
}
