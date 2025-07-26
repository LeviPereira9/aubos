package lp.boble.aubos.controller.book;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.BookCreateRequest;
import lp.boble.aubos.dto.book.BookResponse;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.repository.book.BookRepository;
import lp.boble.aubos.service.book.BookService;
import org.springframework.http.HttpStatusCode;
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
    public ResponseEntity<BookModel> postBook(@RequestBody BookCreateRequest book) {

        bookService.createBook(book);

        return ResponseEntity.ok().build();

    };

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBook(@PathVariable UUID id){
        BookResponse data = bookService.getBookById(id);

        return ResponseEntity.ok().body(data);
    }
}
