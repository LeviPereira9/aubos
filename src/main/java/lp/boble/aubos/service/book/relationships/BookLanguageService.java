package lp.boble.aubos.service.book.relationships;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.BookLanguage.BookLanguageAddRequest;
import lp.boble.aubos.dto.book.relationships.BookLanguage.BookLanguageResponse;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.relationships.BookLanguageMapper;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.LanguageModel;
import lp.boble.aubos.model.book.relationships.BookLanguage;
import lp.boble.aubos.repository.book.relationships.BookLanguageRepository;
import lp.boble.aubos.service.book.BookService;
import lp.boble.aubos.service.book.dependencies.LanguageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookLanguageService {
    private final BookLanguageRepository bookLanguageRepository;
    private final BookLanguageMapper bookLanguageMapper;
    private final BookService bookService;
    private final LanguageService languageService;

    // GET All Languages
    public List<BookLanguageResponse> getAllAvailableLanguages(UUID bookId) {
        List<BookLanguage> availableLanguages = this.findAllAvailableLanguages(bookId);

        return availableLanguages.stream().map(bookLanguageMapper::toResponse).toList();
    }

    private List<BookLanguage> findAllAvailableLanguages(UUID bookId) {
        List<BookLanguage> availableLanguages = bookLanguageRepository.findAllByBookId(bookId);

        if(availableLanguages.isEmpty()) {
            throw CustomNotFoundException.language();
        }

        return availableLanguages;
    }

    // Add Language
    // Checar se não está adicionando uma linguagem que já está.
    // Como? Book + Language -> Aqui [
    // Em um livro que existe. -> BookService [
    // Com uma linguagem que existe. -> LanguageService [
    public void addLanguageToBook(UUID bookId, BookLanguageAddRequest request) {
        this.validateLanguage(bookId, request);

        BookLanguage bookToAdd = this.generateBookLanguage(bookId, request);

        bookLanguageRepository.save(bookToAdd);
    }

    private void validateLanguage(UUID bookId, BookLanguageAddRequest request) {
        boolean requestHasConflict = bookLanguageRepository.existsByBookIdAndLanguageId(bookId, request.languageId());

        if(requestHasConflict){
            throw new CustomDuplicateFieldException("Language");
        }
    }

    private BookLanguage generateBookLanguage(UUID bookId, BookLanguageAddRequest request) {
        BookModel book = bookService.findBookOrThrow(bookId);
        LanguageModel language = languageService.findLanguageOrThrow(request.languageId());

        BookLanguage bookLanguage = new BookLanguage();
        bookLanguage.setBook(book);
        bookLanguage.setLanguage(language);

        return bookLanguage;
    }

    // Delete :)
    // Só precisamos buscar a entidade, nem isso, vamos direto por ID, não, vamos buscar, pq ai se n tiver é vapo no erro.
    public void deleteBookLanguage(UUID bookId, UUID bookLanguageId) {
        BookLanguage bookLanguage = this.findBookLanguageByIdOrThrow(bookLanguageId);

        if(!bookLanguage.belongsToBook(bookId)) {
            throw CustomNotFoundException.language();
        }

        bookLanguageRepository.delete(bookLanguage);
    }

    public BookLanguage findBookLanguageByIdOrThrow(UUID bookLanguageId) {
        return bookLanguageRepository.findById(bookLanguageId).orElseThrow(
                ()-> new CustomNotFoundException("Book Language não encontrado.")
        );
    }

    public List<Integer> findAllLanguagesInBook(UUID bookId) {
        return bookLanguageRepository.findAllLanguageIdByBookId(bookId);
    }

    public Map<UUID, BookLanguage> findRequestedBookLanguages(UUID bookId, List<UUID> bookLanguageIds) {
        List<BookLanguage> bookLanguages = this.findAllLanguagesInBook(bookId, bookLanguageIds);

        return bookLanguages.stream().collect(Collectors.toMap(BookLanguage::getId, Function.identity()));
    }

    private List<BookLanguage> findAllLanguagesInBook(UUID bookId, List<UUID> bookLanguageIds){
        List<BookLanguage> found = bookLanguageRepository.findAllByBookIdAndIdIn(bookId, bookLanguageIds);

        if(found.isEmpty()){
            throw CustomNotFoundException.language();
        }

        return found;
    }

}
