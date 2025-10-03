package lp.boble.aubos.mapper.book.dependencies;

import lp.boble.aubos.dto.book.dependencies.type.TypeResponse;
import lp.boble.aubos.model.book.dependencies.TypeModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TypeMapper {

    TypeResponse toResponse(TypeModel source);
}
