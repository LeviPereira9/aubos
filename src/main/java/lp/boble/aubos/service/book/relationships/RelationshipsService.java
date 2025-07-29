package lp.boble.aubos.service.book.relationships;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.BookRequest;
import lp.boble.aubos.dto.book.relationships.RelationshipsData;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.LanguageModel;
import lp.boble.aubos.model.book.relationships.BookLanguage;
import lp.boble.aubos.repository.book.depedencies.LanguageRepository;
import lp.boble.aubos.repository.book.relationships.BookContributorRepository;
import lp.boble.aubos.repository.book.relationships.BookLanguageRepository;
import lp.boble.aubos.service.book.dependencies.ContributorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelationshipsService {
    private final LanguageRepository languageRepository;
    private final ContributorService contributorService;
    private final BookLanguageRepository bookLanguageRepository;
    private final BookContributorRepository bookContributorRepository;

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

    public void updateRelationships(BookModel bookModel, BookRequest bookRequest){
        // Deletar os antigos
        bookLanguageRepository.deleteByBook(bookModel);
        bookContributorRepository.deleteByBook(bookModel);

        // Carrega os novos
        RelationshipsData relationshipsData = this.loadRelationshipsData(bookModel, bookRequest);

        // Associa os novos
        bookModel.getAvailableLanguages().clear();
        bookModel.getContributors().clear();
        bookModel.getAvailableLanguages().addAll(relationshipsData.availableLanguages());
        bookModel.getContributors().addAll(relationshipsData.contributors());
    }
}
