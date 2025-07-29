package lp.boble.aubos.service.book.relationships;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.BookRequest;
import lp.boble.aubos.dto.book.relationships.RelationshipsData;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.LanguageModel;
import lp.boble.aubos.model.book.relationships.BookLanguage;
import lp.boble.aubos.repository.book.depedencies.LanguageRepository;
import lp.boble.aubos.service.book.dependencies.ContributorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelationshipsService {
    private final LanguageRepository languageRepository;
    private final ContributorService contributorService;

    public List<BookLanguage> getAvailableLanguages(BookModel book, List<Integer> ids){
        List<LanguageModel> languages = languageRepository.findAllById(ids);

        return languages.stream().map(lang ->
                new BookLanguage(
                        book,
                        lang
                )).collect(Collectors.toList());
    }

    public RelationshipsData loadRelationshipsData(BookModel bookModel, BookRequest bookRequest){

        return new RelationshipsData(
                contributorService.getContributors(bookModel, bookRequest.contributors()),
                this.getAvailableLanguages(bookModel, bookRequest.availableLanguagesId())
        );
    }
}
