package lp.boble.aubos.service.book.relationships.alternativetitle;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.bookAlternativeTitle.AlternativeTitleRequest;
import lp.boble.aubos.dto.book.relationships.bookAlternativeTitle.AlternativeTitleResponse;
import lp.boble.aubos.exception.custom.auth.CustomForbiddenActionException;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.relationships.AlternativeTitleMapper;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.relationships.AlternativeTitleModel;
import lp.boble.aubos.repository.book.relationships.AlternativeTitleRepository;
import lp.boble.aubos.service.book.BookService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlternativeTitleService {

    private final AlternativeTitleRepository alternativeTitleRepository;
    private final AlternativeTitleMapper alternativeTitleMapper;
    private final BookService bookService;

    public List<AlternativeTitleResponse> findAllAlternativeTitleAtBook(UUID bookId){
        List<AlternativeTitleModel> alternativeTitles = this.findAllByBookId(bookId);

        return alternativeTitles.stream().map(alternativeTitleMapper::toResponse).toList();
    }

    private List<AlternativeTitleModel> findAllByBookId(UUID bookId) {
        return alternativeTitleRepository.findAllByBook_Id(bookId);
    }

    @Transactional
    public AlternativeTitleResponse addAlternativeTitleToBook(UUID bookId, AlternativeTitleRequest request){

        this.validateTitle(bookId, request);

        AlternativeTitleModel bookAlternativeTitle = this.generateAlternativeTitle(bookId, request);

        return alternativeTitleMapper.toResponse(alternativeTitleRepository.save(bookAlternativeTitle));
    }

    private void validateTitle(UUID bookId, AlternativeTitleRequest request) {
        boolean titleHasConflict = alternativeTitleRepository.existsByBook_IdAndTitleIgnoreCase(bookId, request.title());

        if(titleHasConflict){
            throw CustomDuplicateFieldException.bookAlternativeTitle();
        }
    }

    private AlternativeTitleModel generateAlternativeTitle(UUID bookId, AlternativeTitleRequest request) {
        BookModel book = bookService.findBookOrThrow(bookId);

        return alternativeTitleMapper.toModel(book, request);
    }

    @Transactional
    public AlternativeTitleResponse updateAlternativeTitle(UUID bookId, UUID alternativeTitleId, AlternativeTitleRequest request){
        AlternativeTitleModel alternativeTitle = this.findAlternativeTitleOrThrow(bookId, alternativeTitleId);

        alternativeTitleMapper.update(alternativeTitle, request);

        return alternativeTitleMapper.toResponse(alternativeTitle);
    }

    @Transactional
    public void deleteAlternativeTitle(UUID bookId, UUID alternativeTitleId){
        AlternativeTitleModel alternativeTitle = this.findAlternativeTitleOrThrow(bookId, alternativeTitleId);

        alternativeTitleRepository.delete(alternativeTitle);
    }

    private AlternativeTitleModel findAlternativeTitleOrThrow(UUID bookId, UUID alternativeTitleId){
        AlternativeTitleModel alternativeTitle = alternativeTitleRepository.findById(alternativeTitleId)
                .orElseThrow(CustomNotFoundException::alternativeTitle);

        if(!alternativeTitle.belongsTo(bookId)){
            throw CustomForbiddenActionException.alternativeTitleNotAssociated();
        }

        return alternativeTitle;
    }
}
