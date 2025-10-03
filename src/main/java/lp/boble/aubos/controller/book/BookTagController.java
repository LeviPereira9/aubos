package lp.boble.aubos.controller.book;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.BookTag.BookTagDeleteRequest;
import lp.boble.aubos.dto.book.relationships.BookTag.BookTagRequest;
import lp.boble.aubos.dto.book.relationships.BookTag.BookTagResponse;
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

@RestController
@RequestMapping("${api.prefix}/{bookId}/book-tag")
@RequiredArgsConstructor
public class BookTagController {


    private final BookTagService bookTagService;
    private final BookTagBatchService bookTagBatchService;

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

        return ResponseEntity.ok().eTag("").body(response);
    }

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

    @PostMapping("/batch")
    public ResponseEntity<BatchResponse<Integer>> batchAddTagsInBook(
            @PathVariable UUID bookId,
            @RequestBody List<BookTagRequest> requests
    ){
        BatchTransporter<Integer> content = bookTagBatchService.batchAddTagToBook(bookId, requests);
        int code = content.getStatus();

        BatchResponse<Integer> response =
                new BatchResponseBuilder<Integer>()
                        .operation("POST")
                        .code(code)
                        .message("Requisição POST realizada com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(code).body(response);
    }

    @DeleteMapping("/batch")
    public ResponseEntity<BatchResponse<UUID>> batchRemoveTagsInBook(
            @PathVariable UUID bookId,
            @RequestBody List<BookTagDeleteRequest> requests
    ){
        BatchTransporter<UUID> content = bookTagBatchService.batchRemoveTagsFromBook(bookId, requests);
        int code = content.getStatus();

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
