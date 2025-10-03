package lp.boble.aubos.mapper.book.relationships;

import lp.boble.aubos.dto.book.relationships.bookTag.BookTagResponse;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.TagModel;
import lp.boble.aubos.model.book.relationships.BookTagModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookTagMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "tag", source = "tag.name")
    BookTagResponse toResponse(BookTagModel source);

    @Mapping(target = "id", ignore = true)
    BookTagModel toModel(BookModel book, TagModel tag);
}
