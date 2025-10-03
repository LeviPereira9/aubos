package lp.boble.aubos.mapper.book.dependencies;

import lp.boble.aubos.dto.book.dependencies.restriction.RestrictionResponse;
import lp.boble.aubos.model.book.dependencies.RestrictionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RestrictionMapper {

    RestrictionResponse toResponse(RestrictionModel source);
}
