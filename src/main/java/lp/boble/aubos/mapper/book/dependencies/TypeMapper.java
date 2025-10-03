package lp.boble.aubos.mapper.book.dependencies;

import lp.boble.aubos.dto.book.dependencies.type.TypeRequest;
import lp.boble.aubos.dto.book.dependencies.type.TypeResponse;
import lp.boble.aubos.model.book.dependencies.TypeModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TypeMapper {

    TypeResponse toResponse(TypeModel source);

    TypeModel toModel(TypeRequest request);

    void update(@MappingTarget TypeModel target, TypeRequest source);
}
