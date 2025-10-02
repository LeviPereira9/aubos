package lp.boble.aubos.controller.book;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.BookTag.BookTagRequest;
import lp.boble.aubos.dto.book.relationships.BookTag.BookTagResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
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
}
