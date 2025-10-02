package lp.boble.aubos.mapper.book.dependencies;

import lp.boble.aubos.dto.book.dependencies.tag.TagRequest;
import lp.boble.aubos.dto.book.dependencies.tag.TagResponse;
import lp.boble.aubos.model.book.dependencies.TagModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagResponse toResponse(TagModel source);

    TagModel toModel(TagRequest source);

}
