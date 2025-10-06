package lp.boble.aubos.mapper.user.role;

import lp.boble.aubos.dto.user.role.RoleResponse;
import lp.boble.aubos.model.user.RoleModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleResponse toResponse(RoleModel source);

}
