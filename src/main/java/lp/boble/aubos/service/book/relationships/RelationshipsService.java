package lp.boble.aubos.service.book.relationships;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.BookCreateRequest;
import lp.boble.aubos.dto.book.parts.BookAddContributor;
import lp.boble.aubos.dto.book.relationships.RelationshipsData;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.LanguageModel;
import lp.boble.aubos.model.book.relationships.BookContributor;
import lp.boble.aubos.model.book.relationships.BookLanguage;
import lp.boble.aubos.repository.book.depedencies.LanguageRepository;
import lp.boble.aubos.service.book.dependencies.ContributorService;
import lp.boble.aubos.service.book.dependencies.DependenciesService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelationshipsService {
    private final LanguageRepository languageRepository;
    private final ContributorService contributorService;
    private final DependenciesService dependenciesService;

    public List<BookLanguage> getBookAvailableLanguages(BookModel book, List<Integer> ids){
        List<LanguageModel> languages = languageRepository.findAllById(ids);

        return languages.stream().map(lang ->
                new BookLanguage(
                        book,
                        lang
                )).collect(Collectors.toList());
    }

    public List<BookContributor> getBookContributors(
            BookModel book,
            List<BookAddContributor> contributors){
        return contributors.stream()
                .map(c -> new BookContributor(
                        book,
                        contributorService.getContributorOrThrow(c.contributorId()),
                        dependenciesService.getContributorRole(c.contributorRoleId())
                ))
                .collect(Collectors.toList());
    }

    public RelationshipsData loadBookRelationshipsData(BookModel bookModel, BookCreateRequest bookCreateRequest){

        return new RelationshipsData(
                this.getBookContributors(bookModel, bookCreateRequest.contributors()),
                this.getBookAvailableLanguages(bookModel, bookCreateRequest.availableLanguagesId())
        );
    }

}
