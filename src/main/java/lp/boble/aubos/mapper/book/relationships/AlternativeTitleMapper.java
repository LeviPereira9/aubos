package lp.boble.aubos.mapper.book.relationships;

import lp.boble.aubos.dto.book.relationships.bookAlternativeTitle.AlternativeTitleRequest;
import lp.boble.aubos.dto.book.relationships.bookAlternativeTitle.AlternativeTitleResponse;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.relationships.AlternativeTitleModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AlternativeTitleMapper {

    @Mapping(target = "id", source = "id")
    AlternativeTitleResponse toResponse(AlternativeTitleModel source);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "source.title")
    AlternativeTitleModel toModel(BookModel book, AlternativeTitleRequest source);

    @Mapping(target = "title", source = "source.title")
    void update(@MappingTarget AlternativeTitleModel alternativeTitle, AlternativeTitleRequest source);
}
