package lp.boble.aubos.repository.book;

import lp.boble.aubos.model.book.BookModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookRepository extends JpaRepository<BookModel, UUID> {}
