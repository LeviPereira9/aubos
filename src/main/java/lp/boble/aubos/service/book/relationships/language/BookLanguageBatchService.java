package lp.boble.aubos.service.book.relationships.language;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.BookLanguage.BookLanguageAddRequest;
import lp.boble.aubos.dto.book.relationships.BookLanguage.BookLanguageDelRequest;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.LanguageModel;
import lp.boble.aubos.model.book.relationships.BookLanguage;
import lp.boble.aubos.repository.book.relationships.BookLanguageRepository;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.service.book.BookService;
import lp.boble.aubos.service.book.dependencies.language.LanguageService;
import lp.boble.aubos.util.ValidationResult;
import org.springframework.boot.actuate.autoconfigure.metrics.data.RepositoryMetricsAutoConfiguration;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookLanguageBatchService {
    private final BookService bookService;
    private final BookLanguageService bookLanguageService;
    private final LanguageService languageService;
    private final BookLanguageRepository bookLanguageRepository;
    private final RepositoryMetricsAutoConfiguration repositoryMetricsAutoConfiguration;

    // --- Batch
    // Adicionar diversas linguagens
    // Todas para um único livro, que deve existir -> BookService.findBook()
    // As linguagens devem existir tbm, Set -> FindAllById.
    // Se já não estão no livro -> FindAllBookLanguagesByBookId.
    public BatchTransporter<Integer> addLanguagesToBook(UUID bookId, List<BookLanguageAddRequest> requests) {

        ValidationResult<Integer, BookLanguage> validationResult = this.validateAddBatch(bookId, requests);

        this.persistBatch(validationResult);

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<Integer, BookLanguage> validateAddBatch(UUID bookId, List<BookLanguageAddRequest> requests) {
        ValidationResult<Integer, BookLanguage> validationResult = new ValidationResult<>();

        List<BookLanguageAddRequest> uniqueRequests = requests.stream().distinct().toList();

        List<Integer> requestedLanguagesId = uniqueRequests.stream().map(BookLanguageAddRequest::languageId).toList();

        BookModel book = bookService.findBookOrThrow(bookId);

        List<Integer> currentLanguages = bookLanguageService.findAllLanguagesInBook(bookId);

        Map<Integer, LanguageModel> mapRequestLanguages = languageService.findRequestedLanguages(requestedLanguagesId);

        for(BookLanguageAddRequest request : uniqueRequests) {
            int languageId = request.languageId();
            LanguageModel language = mapRequestLanguages.get(languageId);

            if(language == null) {
                validationResult.addFailure(languageId, "Linguagem não encontrada.");
                continue;
            }

            if(currentLanguages.contains(languageId)) {
                validationResult.addFailure(languageId, "Linguagem duplicada.");
                continue;
            }

            BookLanguage bookLanguageToAdd = this.generateBookLanguage(book, language);
            validationResult.addValid(bookLanguageToAdd);
            validationResult.addSuccess(languageId, "Linguagem adicionada com sucesso.");
        }

        return validationResult;
    }

    private BookLanguage generateBookLanguage(BookModel book, LanguageModel language) {
        BookLanguage bookLanguage = new BookLanguage();
        bookLanguage.setBook(book);
        bookLanguage.setLanguage(language);

        return bookLanguage;
    }

    private void persistBatch(ValidationResult<Integer, BookLanguage> validationResult) {
        if(!validationResult.getValidRequests().isEmpty()){
            bookLanguageRepository.saveAll(validationResult.getValidRequests());
        }
    }

    // Update diversas linguagens
    // Pegas todas do livro -> FindAll...
    // Pelo List, a gente checa, pera não têm update nisso aqui não. É só remover e criar dnv. Não?

    // Para todos do batch: Distinct em cima das request, não quero tratar nenhuma duplicata igual no ReOrder, pq lá era infelizmente,
    // Necessário. :(
    public BatchTransporter<UUID> batchDeleteBookLanguages(UUID bookId, List<BookLanguageDelRequest> requests) {
        ValidationResult<UUID, BookLanguage> validationResult = this.validateDeleteBatch(bookId, requests);

        this.deleteBatch(validationResult);

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<UUID, BookLanguage> validateDeleteBatch(UUID bookId, List<BookLanguageDelRequest> requests) {
        List<BookLanguageDelRequest> uniqueRequests = requests.stream().distinct().toList();

        ValidationResult<UUID, BookLanguage> validationResult = new ValidationResult<>();

        List<UUID> bookLanguageIds = uniqueRequests.stream().map(BookLanguageDelRequest::bookLanguageId).toList();

        Map<UUID, BookLanguage> currentLanguages = bookLanguageService.findRequestedBookLanguages(bookId, bookLanguageIds);

        for(BookLanguageDelRequest request : uniqueRequests) {
            UUID bookLanguageId = request.bookLanguageId();

            if(!currentLanguages.containsKey(bookLanguageId)) {
                validationResult.addFailure(bookLanguageId, "Esta língua não pertence a este livro.");
                continue;
            }

            validationResult.addValid(currentLanguages.get(bookLanguageId));
            validationResult.addSuccess(bookLanguageId, "Língua removida com sucesso.");
        }

        return validationResult;
    }

    private void deleteBatch(ValidationResult<UUID, BookLanguage> validationResult) {
        if(!validationResult.getValidRequests().isEmpty()) {
            bookLanguageRepository.deleteAll(validationResult.getValidRequests());
        }
    }
}
