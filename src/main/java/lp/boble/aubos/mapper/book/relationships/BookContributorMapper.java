package lp.boble.aubos.mapper.book.relationships;

import lp.boble.aubos.dto.book.BookResponse;
import lp.boble.aubos.dto.book.relationships.BookContributorResponse;
import lp.boble.aubos.dto.book.relationships.BookContributorsResponse;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.relationships.BookContributorModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface BookContributorMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "contributor.name")
    @Mapping(target = "role", source = "contributorRole.name")
    BookContributorResponse fromModelToResponse(BookContributorModel model);

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
