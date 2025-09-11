package lp.boble.aubos.mapper.book.relationships;

import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorResponse;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorsResponse;
import lp.boble.aubos.model.book.dependencies.ContributorRole;
import lp.boble.aubos.model.book.relationships.BookContributorModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface BookContributorMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "contributor.name")
    @Mapping(target = "role", source = "contributorRole.name")
    BookContributorResponse fromModelToResponse(BookContributorModel model);

    @Mapping(target = "contributor", ignore = true)
    @Mapping(target = "contributorRole", source = "role")
    void updateBookContributor(@MappingTarget BookContributorModel model, ContributorRole role);

    BookContributorsResponse fromModelToResponse(List<BookContributorModel> models);

    @AfterMapping
    default void mapContributors(List<BookContributorModel> rawContributors, @MappingTarget BookContributorsResponse.BookContributorsResponseBuilder builder){
        Map<String, List<lp.boble.aubos.dto.book.parts.BookContributorResponse>> contributors = BookContributorModel.arrangeContributors(rawContributors);

        builder
                .authors(contributors.get("autor"))
                .editors(contributors.get("editor"))
                .illustrators(contributors.get("ilustrador"))
                .publishers(contributors.get("publicadora"));
    }

}
