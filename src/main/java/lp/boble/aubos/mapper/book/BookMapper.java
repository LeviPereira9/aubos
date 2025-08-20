package lp.boble.aubos.mapper.book;

import lp.boble.aubos.dto.book.*;
import lp.boble.aubos.dto.book.parts.BookContributorResponse;
import lp.boble.aubos.dto.book.dependencies.DependencyData;
import lp.boble.aubos.mapper.book.dependencies.DependenciesMapper;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.relationships.BookContributor;
import lp.boble.aubos.model.book.relationships.BookLanguage;
import lp.boble.aubos.model.user.UserModel;
import lp.boble.aubos.util.AuthUtil;
import org.mapstruct.*;

import java.util.*;

@Mapper(
        componentModel = "spring",
        uses = {DependenciesMapper.class}
)
public interface BookMapper {

    @Mapping(target = "softDeleted", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdated", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "language", source = "dp.language")
    @Mapping(target = "type", source = "dp.type")
    @Mapping(target = "status", source = "dp.status")
    @Mapping(target = "restriction", source = "dp.restriction")
    @Mapping(target = "license", source = "dp.license")
    BookModel fromCreateRequestToModel(BookCreateRequest bookCreateRequest, DependencyData dp);

    @Mapping(target = "softDeleted", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdated", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedBy", expression = "java(requester())")
    @Mapping(target = "language", source = "language")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "restriction", source = "restriction")
    @Mapping(target = "license", source = "license")
    @Mapping(target = "contributors", ignore = true)
    @Mapping(target = "availableLanguages", ignore = true)
    void fromUpdateToModel(@MappingTarget BookModel bookModel, BookUpdateData bud);

    @Mapping(target = "coverUrl", source = "bur.coverUrl")
    @Mapping(target = "title", source = "bur.title")
    @Mapping(target = "subtitle", source = "bur.subtitle")
    @Mapping(target = "synopsis", source = "bur.synopsis")
    @Mapping(target = "publishedOn", source = "bur.publishedOn")
    @Mapping(target = "finishedOn", source = "bur.finishedOn")
    @Mapping(target = "language", source = "dp.language")
    @Mapping(target = "type", source = "dp.type")
    @Mapping(target = "status", source = "dp.status")
    @Mapping(target = "restriction", source = "dp.restriction")
    @Mapping(target = "license", source = "dp.license")
    BookUpdateData fromUpdateToPreparation(BookUpdateRequest bur, DependencyData dp);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "coverUrl", source = "coverUrl")
    @Mapping(target = "language", source = "book.language.value")
    @Mapping(target = "type", source = "book.type.name")
    @Mapping(target = "status", source = "book.status.label")
    @Mapping(target = "authors", ignore = true) // Dps vem. >:)
    @Mapping(target = "editors", ignore = true)
    @Mapping(target = "illustrators", ignore = true)
    @Mapping(target = "publishers", ignore = true)
    @Mapping(target = "availableLanguages", source = "book.availableLanguages", qualifiedByName = "languageList")
    @Mapping(target = "license", source = "license", qualifiedByName = "licenseMapper")
    @Mapping(target = "restriction", source = "restriction", qualifiedByName = "restrictionMapper")
    BookResponse toResponse(BookModel book);

    @AfterMapping
    default void mapContributors(BookModel book, @MappingTarget BookResponse.BookResponseBuilder builder){
        Map<String, List<BookContributorResponse>> contributors = BookContributor.arrangeContributors(book.getContributors());

        builder
                .authors(contributors.get("autor"))
                .editors(contributors.get("editor"))
                .illustrators(contributors.get("ilustrador"))
                .publishers(contributors.get("publicadora"));
    }

    @Named("languageList")
    static List<String> languageList(List<BookLanguage> bookLanguages){
        return bookLanguages.stream().map(
                l -> l.getLanguage().getValue()
        ).toList();
    }


    BookPageResponse fromProjectionToResponse(BookPageProjection bookPageProjection);

    default UserModel requester(){
        return AuthUtil.requester();
    }


}
