package lp.boble.aubos.mapper.book.dependencies;

import lp.boble.aubos.dto.book.dependencies.status.StatusRequest;
import lp.boble.aubos.dto.book.dependencies.status.StatusResponse;
import lp.boble.aubos.model.book.dependencies.StatusModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StatusMapper {

    StatusResponse toResponse(StatusModel source);

    StatusModel toModel(StatusRequest source);

    void update(@MappingTarget StatusModel target, StatusRequest source );
}
