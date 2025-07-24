package lp.boble.aubos.controller.book;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.repository.book.BookRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/book")
@RequiredArgsConstructor
public class BookController {
    private final BookRepository bookRepository;

    @PostMapping
    public ResponseEntity<BookModel> postBook(@RequestBody BookModel book) {

        bookRepository.save(book);

        return ResponseEntity.ok().build();

    };
}
