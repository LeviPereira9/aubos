package lp.boble.aubos.service.book.relationships.tag;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.bookTag.BookTagDeleteRequest;
import lp.boble.aubos.dto.book.relationships.bookTag.BookTagRequest;
import lp.boble.aubos.dto.book.relationships.bookTag.BookTagResponse;
import lp.boble.aubos.exception.custom.auth.CustomForbiddenActionException;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.relationships.BookTagMapper;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.TagModel;
import lp.boble.aubos.model.book.relationships.BookTagModel;
import lp.boble.aubos.repository.book.relationships.BookTagRepository;
import lp.boble.aubos.service.book.BookService;
import lp.boble.aubos.service.book.dependencies.tag.TagService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookTagService {

    private final BookTagRepository bookTagRepository;
    private final BookTagMapper bookTagMapper;
    private final BookService bookService;
    private final TagService tagService;

    @Transactional
    public BookTagResponse addTagToBook(UUID bookId, BookTagRequest request){
        this.validateAddTagToBook(bookId, request.id());

        BookTagModel bookTag = this.generateBookTageModel(bookId, request);

        return bookTagMapper.toResponse(bookTagRepository.save(bookTag));
    }

    private void validateAddTagToBook(UUID bookId, int tagId) {
        boolean tagHasConflict = bookTagRepository.existsByBookIdAndTagId(bookId, tagId);

        if(tagHasConflict){
            throw CustomDuplicateFieldException.bookTag();
        }
    }

    private BookTagModel generateBookTageModel(UUID bookId, BookTagRequest request) {
        BookModel book = bookService.findBookOrThrow(bookId);
        TagModel tag = tagService.findTagOrThrow(request.id());

        return bookTagMapper.toModel(book, tag);
    }

    @Transactional
    public void removeTagFromBook(UUID bookId, UUID bookTagId){
        BookTagModel bookTag = this.getBookTag(bookId, bookTagId);

        bookTagRepository.delete(bookTag);
    }

    private BookTagModel getBookTag(UUID bookId, UUID bookTagId) {
        BookTagModel bookTag = this.findBookTagOrThrow(bookTagId);

        if(!bookTag.belongsTo(bookId)){
            throw CustomForbiddenActionException.bookTagNotAssociated();
        }

        return bookTag;
    }

    private BookTagModel findBookTagOrThrow(UUID bookTagId) {
        return bookTagRepository.findById(bookTagId)
                .orElseThrow(CustomNotFoundException::bookTag);
    }

    public List<BookTagResponse> findAllTagsInBook(UUID bookId){
        List<BookTagModel> tags = this.findAllTagsByBookId(bookId);

        if(tags.isEmpty()){
            throw CustomNotFoundException.bookTag();
        }

        return tags.stream().map(bookTagMapper::toResponse).toList();
    }

    private List<BookTagModel> findAllTagsByBookId(UUID bookId){
        return bookTagRepository.findAllByBook_id(bookId);
    }

    protected List<Integer> findAlreadyAddedTags(UUID bookId, Set<BookTagRequest> requests){
        Set<Integer> requestedTagIds = requests.stream().map(BookTagRequest::id).collect(Collectors.toSet());

        return bookTagRepository.findExistingTagsId(bookId, requestedTagIds);
    }

    public Map<UUID, BookTagModel> findRequestedTagsInBook(UUID bookId, Set<BookTagDeleteRequest> requests) {

        Set<UUID> bookTagIds = requests.stream().map(BookTagDeleteRequest::id).collect(Collectors.toSet());

        List<BookTagModel> bookTags = bookTagRepository.findCurrentTagsInBook(bookId, bookTagIds);

        return bookTags.stream().collect(Collectors.toMap(BookTagModel::getId, Function.identity()));
    }
}
