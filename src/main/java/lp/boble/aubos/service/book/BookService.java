package lp.boble.aubos.service.book;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.BookCreateRequest;
import lp.boble.aubos.dto.book.dependencies.DependencyData;
import lp.boble.aubos.mapper.book.BookMapper;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.repository.book.BookRepository;
import lp.boble.aubos.service.book.dependencies.BookDependenciesService;
import lp.boble.aubos.service.book.dependencies.ContributorService;
import lp.boble.aubos.util.AuthUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookDependenciesService dependenciesService;
    private final AuthUtil authUtil;

    private final BookRepository bookRepository;
    private final ContributorService contributorService;
    private final BookMapper bookMapper;

    @Transactional
    public void createBook(BookCreateRequest book) {

        DependencyData dependencyData = dependenciesService.getDependencyData(book);

        BookModel bookToSave = bookMapper.fromCreateRequestToModel(book, dependencyData);
        bookToSave.setCreatedBy(authUtil.getRequester());

        bookToSave.setContributors(contributorService.getContributors(
                bookToSave,
                book.contributors()));

        bookRepository.save(bookToSave);
    }
}
