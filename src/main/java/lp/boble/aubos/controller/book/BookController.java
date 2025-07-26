package lp.boble.aubos.controller.book;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.BookRequest;
import lp.boble.aubos.dto.book.BookResponse;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.repository.book.BookRepository;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/book")
@RequiredArgsConstructor
public class BookController {
    private final BookRepository bookRepository;
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<SuccessResponse<BookResponse>> postBook(@RequestBody BookRequest book) {

        BookResponse data = bookService.createBook(book);

        SuccessResponse<BookResponse> response =
                new SuccessResponseBuilder<BookResponse>()
                        .operation("POST")
                        .code(HttpStatus.CREATED)
                        .message("Livro adicionado com sucesso")
                        .data(data)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    };

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<BookResponse>> getBook(@PathVariable UUID id){

        BookResponse data = bookService.getBookById(id);

        SuccessResponse<BookResponse> response =
                new SuccessResponseBuilder<BookResponse>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Livro encontrado com sucesso")
                        .data(data)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<BookResponse>>  updateBook(@PathVariable UUID id, @RequestBody BookRequest book){
        BookResponse data = bookService.updateBook(id, book);

        SuccessResponse<BookResponse> response =
                new SuccessResponseBuilder<BookResponse>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("Livro atualizado com sucesso")
                        .data(data)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteBook(@PathVariable UUID id){
        bookService.deleteBook(id);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .code(HttpStatus.OK)
                        .message("Livro deletado com sucesso")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
