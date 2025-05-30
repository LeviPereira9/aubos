package lp.boble.aubos.mapper.apikey;

import lp.boble.aubos.dto.apikey.ApiKeyResponse;
import lp.boble.aubos.model.apikey.ApiKeyModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApiKeyMapper {

    @Mapping(target = "owner", source = "owner.username")
    @Mapping(target = "status", source = "status.name")
    @Mapping(target = "publicId", source = "publicId")
    ApiKeyResponse fromModelToResponse(ApiKeyModel model);

}
