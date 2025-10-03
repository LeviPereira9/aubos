package lp.boble.aubos.service.book.relationships.tag;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.BookTag.BookTagDeleteRequest;
import lp.boble.aubos.dto.book.relationships.BookTag.BookTagRequest;
import lp.boble.aubos.mapper.book.relationships.BookTagMapper;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.TagModel;
import lp.boble.aubos.model.book.relationships.BookTagModel;
import lp.boble.aubos.repository.book.relationships.BookTagRepository;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.service.book.BookService;
import lp.boble.aubos.service.book.dependencies.tag.TagService;
import lp.boble.aubos.util.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookTagBatchService {
    private final BookTagService bookTagService;
    private final TagService tagService;
    private final BookService bookService;
    private final BookTagMapper bookTagMapper;
    private final BookTagRepository bookTagRepository;

    @Transactional
    public BatchTransporter<Integer> batchAddTagToBook(UUID bookId, List<BookTagRequest> requests){
        ValidationResult<Integer, BookTagModel> validationResult = this.validateBatchAdd(bookId, requests);

        this.persistBatch(validationResult.getValidRequests());

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<Integer, BookTagModel> validateBatchAdd(UUID bookId, List<BookTagRequest> requests) {
        ValidationResult<Integer, BookTagModel> validationResult = new ValidationResult<>();

        BookModel book = bookService.findBookOrThrow(bookId);

        Set<BookTagRequest> uniqueRequests = new HashSet<>(requests);

        List<Integer> currentTagsId = bookTagService.findAlreadyAddedTags(bookId, uniqueRequests);

        Map<Integer, TagModel> mapRequestedTags = tagService.getRequestedTags(uniqueRequests);

        for(BookTagRequest request: uniqueRequests){
            int tagId = request.id();
            boolean hasTagConflict = currentTagsId.contains(tagId);
            TagModel tag = mapRequestedTags.get(tagId);

            if(hasTagConflict){
                validationResult.addFailure(tagId, "O livro já contêm essa tag.");
                continue;
            }

            if(tag == null){
                validationResult.addFailure(tagId, "tag não encontrada.");
                continue;
            }

            BookTagModel bookTag = bookTagMapper.toModel(book, tag);
            validationResult.addValid(bookTag);
            validationResult.addSuccess(tagId, "Tag adicionada ao livro com sucesso.");
        }

        return validationResult;
    }

    private void persistBatch(List<BookTagModel> validRequests) {
        if(!validRequests.isEmpty()){
            bookTagRepository.saveAll(validRequests);
        }
    }

    @Transactional
    public BatchTransporter<UUID> batchRemoveTagsFromBook(
            UUID bookId,
            List<BookTagDeleteRequest> requests){

        ValidationResult<UUID, BookTagModel> validationResult = this.validateRemoveBatch(bookId, requests);

        this.deleteBatch(validationResult.getValidRequests());

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<UUID, BookTagModel> validateRemoveBatch(UUID bookId, List<BookTagDeleteRequest> requests) {
        ValidationResult<UUID, BookTagModel> validationResult = new ValidationResult<>();

        Set<BookTagDeleteRequest> uniqueRequests = new HashSet<>(requests);

        bookService.bookExistsById(bookId);

        Map<UUID, BookTagModel> mapRequestedTags = bookTagService.findRequestedTagsInBook(bookId, uniqueRequests);

        for(BookTagDeleteRequest request: uniqueRequests){
            UUID bookTagId = request.id();
            boolean hasAssociation = mapRequestedTags.containsKey(bookTagId);

            if(!hasAssociation){
                validationResult.addFailure(bookTagId, "Essa tag não pertence a esse livro.");
                continue;
            }

            validationResult.addValid(mapRequestedTags.get(bookTagId));
            validationResult.addSuccess(bookTagId, "Tag removida do livro com sucesso.");
        }

        return validationResult;
    }

    private void deleteBatch(List<BookTagModel> validRequests) {
        if(!validRequests.isEmpty()){
            bookTagRepository.deleteAll(validRequests);
        }
    }
}
