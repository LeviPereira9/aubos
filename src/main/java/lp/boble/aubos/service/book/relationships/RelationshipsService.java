package lp.boble.aubos.service.book.relationships;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.BookRequest;
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

    public List<BookLanguage> getAvailableLanguages(BookModel book, List<Integer> ids){
        List<LanguageModel> languages = languageRepository.findAllById(ids);

        return languages.stream().map(lang ->
                new BookLanguage(
                        book,
                        lang
                )).collect(Collectors.toList());
    }

    public List<BookContributor> getContributors(
            BookModel book,
            List<BookAddContributor> contributors){
        return contributors.stream()
                .map(c -> new BookContributor(
                        book,
                        contributorService.getContributorOrThrow(c.contributorId()),
                        dependenciesService.getRole(c.contributorRoleId())
                ))
                .collect(Collectors.toList());
    }

    public RelationshipsData loadRelationshipsData(BookModel bookModel, BookRequest bookRequest){

        return new RelationshipsData(
                this.getContributors(bookModel, bookRequest.contributors()),
                this.getAvailableLanguages(bookModel, bookRequest.availableLanguagesId())
        );
    }

    public void updateRelationships(BookModel bookModel, BookRequest bookRequest){
        RelationshipsData relationshipsData = this.loadRelationshipsData(bookModel, bookRequest);

        this.updateLanguages(bookModel, relationshipsData.availableLanguages());
        this.updateContributors(bookModel, relationshipsData.contributors());
    }

    public void updateLanguages(BookModel bookModel, List<BookLanguage> rawLanguages) {
        Set<BookLanguage> currentLanguages = new HashSet<>(bookModel.getAvailableLanguages());
        Set<BookLanguage> incomingLanguages = new HashSet<>(rawLanguages);

        Set<BookLanguage> toRemove = new HashSet<>(currentLanguages);
        toRemove.removeAll(incomingLanguages);

        Set<BookLanguage> toAdd = new HashSet<>(incomingLanguages);
        toAdd.removeAll(currentLanguages);

        bookModel.getAvailableLanguages().removeAll(toRemove);
        bookModel.getAvailableLanguages().addAll(toAdd);
    }

    public void updateContributors(BookModel bookModel, List<BookContributor> rawContributors) {
        Set<BookContributor> currentContributors = new HashSet<>(bookModel.getContributors());
        Set<BookContributor> incomingContributors = new HashSet<>(rawContributors);

        Set<BookContributor> toRemove = new HashSet<>(currentContributors);
        toRemove.removeAll(incomingContributors);

        Set<BookContributor> toAdd = new HashSet<>(incomingContributors);
        toAdd.removeAll(currentContributors);

        bookModel.getContributors().removeAll(toRemove);
        bookModel.getContributors().addAll(toAdd);
    }
}
