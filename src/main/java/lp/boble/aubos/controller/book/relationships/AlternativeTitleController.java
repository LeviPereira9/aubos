package lp.boble.aubos.controller.book.relationships;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.bookAlternativeTitle.AlternativeTitleRequest;
import lp.boble.aubos.dto.book.relationships.bookAlternativeTitle.AlternativeTitleResponse;
import lp.boble.aubos.response.batch.BatchResponse;
import lp.boble.aubos.response.batch.BatchResponseBuilder;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.relationships.alternativetitle.AlternativeTitleBatchService;
import lp.boble.aubos.service.book.relationships.alternativetitle.AlternativeTitleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/{bookId}/alternative-title")
@RequiredArgsConstructor
public class AlternativeTitleController {

    private final AlternativeTitleService alternativeTitleService;
    private final AlternativeTitleBatchService alternativeTitleBatchService;

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

        return ResponseEntity.ok().eTag("").body(response);
    }

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
