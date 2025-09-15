package lp.boble.aubos.service.book.relationships;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.BookLanguage.BookLanguageAddRequest;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.service.book.BookService;
import lp.boble.aubos.util.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookLanguageBatchService {
    private final BookService bookService;
    private final BookLanguageService bookLanguageService;

    // --- Batch
    // Adicionar diversas linguagens
    // Todas para um único livro, que deve existir -> BookService.findBook()
    // As linguagens devem existir tbm, Set -> FindAllById.
    // Se já não estão no livro -> FindAllBookLanguagesByBookId.
    public void addLanguagesToBook(UUID bookId, List<BookLanguageAddRequest> requests) {
        ValidationResult<BookLanguageAddRequest> validationResult = new ValidationResult<>();

        BookModel book = bookService.findBookOrThrow(bookId);

        List<Integer> currentLanguages = bookLanguageService.findAllLanguagesInBook(bookId);

        for(BookLanguageAddRequest request : requests) {
            int languageId = request.languageId();

            if(currentLanguages.contains(languageId)) {

            }
        }



    }

    // Update diversas linguagens
    // Pegas todas do livro -> FindAll...
    // Pelo List, a gente checa, pera não têm update nisso aqui não. É só remover e criar dnv. Não?

    // Para todos do batch: Distinct em cima das request, não quero tratar nenhuma duplicata igual no ReOrder, pq lá era infelizmente,
    // Necessário. :(
}
