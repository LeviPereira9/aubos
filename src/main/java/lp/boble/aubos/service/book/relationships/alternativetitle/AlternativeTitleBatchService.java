package lp.boble.aubos.service.book.relationships.alternativetitle;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.bookAlternativeTitle.AlternativeTitleRequest;
import lp.boble.aubos.mapper.book.relationships.AlternativeTitleMapper;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.relationships.AlternativeTitleModel;
import lp.boble.aubos.repository.book.relationships.AlternativeTitleRepository;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.service.book.BookService;
import lp.boble.aubos.util.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlternativeTitleBatchService {

    private final AlternativeTitleRepository alternativeTitleRepository;
    private final BookService bookService;
    private final AlternativeTitleService alternativeTitleService;
    private final AlternativeTitleMapper alternativeTitleMapper;

    public BatchTransporter<String> addAlternativeTitlesInBatch(UUID bookId, List<AlternativeTitleRequest> requests){

        ValidationResult<String, AlternativeTitleModel> validationResult = this.validateBatchRequests(bookId, requests);

        this.persistBatch(validationResult.getValidRequests());

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<String, AlternativeTitleModel> validateBatchRequests(UUID bookId, List<AlternativeTitleRequest> requests) {

        ValidationResult<String, AlternativeTitleModel> validationResult = new ValidationResult<>();

        Set<AlternativeTitleRequest> uniqueRequests = new HashSet<>(requests);

        BookModel book = bookService.findBookOrThrow(bookId);

        List<String> existingTitles = alternativeTitleService.findExistingTitles(bookId, uniqueRequests);

        for (AlternativeTitleRequest request : uniqueRequests) {
            String requestedTitle = request.title();
            boolean hasTitleConflict = existingTitles.contains(requestedTitle);

            if(hasTitleConflict){
                validationResult.addFailure(requestedTitle, "Este título alternativo já está no livro.");
                continue;
            }

            AlternativeTitleModel toAdd = alternativeTitleMapper.toModel(book, request);

            validationResult.addValid(toAdd);
            validationResult.addSuccess(requestedTitle, "Título alternativo adicionado com sucesso.");
        }

        return validationResult;
    }

    private void persistBatch(List<AlternativeTitleModel> validRequests) {

        if(!validRequests.isEmpty()){
            alternativeTitleRepository.saveAll(validRequests);
        }

    }

}
