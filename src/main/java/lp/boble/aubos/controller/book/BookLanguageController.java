package lp.boble.aubos.controller.book;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
import lp.boble.aubos.dto.book.relationships.BookLanguage.BookLanguageAddRequest;
import lp.boble.aubos.dto.book.relationships.BookLanguage.BookLanguageCreatedResponse;
import lp.boble.aubos.dto.book.relationships.BookLanguage.BookLanguageDelRequest;
import lp.boble.aubos.dto.book.relationships.BookLanguage.BookLanguageResponse;
import lp.boble.aubos.response.batch.BatchResponse;
import lp.boble.aubos.response.batch.BatchResponseBuilder;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.relationships.BookLanguageBatchService;
import lp.boble.aubos.service.book.relationships.BookLanguageService;
import lp.boble.aubos.util.ResourceLocationUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/book/{bookId}/languages")
@RequiredArgsConstructor
public class BookLanguageController {
    private final BookLanguageService bookLanguageService;
    private final BookLanguageBatchService bookLanguageBatchService;

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

    @PostMapping("/batch")
    public ResponseEntity<BatchResponse<Integer>> addLanguagesToBook(
            @PathVariable UUID bookId,
            @RequestBody List<BookLanguageAddRequest> requests){

        BatchTransporter<Integer> content = bookLanguageBatchService.addLanguagesToBook(bookId, requests);
        int code = content.getStatus();

        BatchResponse<Integer> response = new BatchResponseBuilder<Integer>()
                .operation("POST")
                .message("Requisição de POST concluída com sucesso.")
                .code(code)
                .build();

        return ResponseEntity.status(code).body(response);
    }

    @DeleteMapping("/batch")
    public ResponseEntity<BatchResponse<UUID>> deleteLanguagesFromBook(
            @PathVariable UUID bookId,
            @RequestBody List<BookLanguageDelRequest> requests
    ){
        BatchTransporter<UUID> content = bookLanguageBatchService.batchDeleteBookLanguages(bookId, requests);
        int code = content.getStatus();

        BatchResponse<UUID> response = new BatchResponseBuilder<UUID>()
                .operation("DELETE")
                .message("Requisição de DELETE concluída com sucesso.")
                .content(content)
                .code(code)
                .build();

        return ResponseEntity.status(code).body(response);
    }


}
