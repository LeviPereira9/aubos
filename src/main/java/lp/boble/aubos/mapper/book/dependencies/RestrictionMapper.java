package lp.boble.aubos.mapper.book.dependencies;

import lp.boble.aubos.dto.book.dependencies.restriction.RestrictionCreateRequest;
import lp.boble.aubos.dto.book.dependencies.restriction.RestrictionResponse;
import lp.boble.aubos.dto.book.dependencies.restriction.RestrictionUpdateRequest;
import lp.boble.aubos.model.book.dependencies.RestrictionModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RestrictionMapper {

    RestrictionResponse toResponse(RestrictionModel source);

    RestrictionModel toModel(RestrictionCreateRequest source);

    void update(@MappingTarget RestrictionModel target, RestrictionUpdateRequest source);
}
