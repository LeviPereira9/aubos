package lp.boble.aubos.mapper.book.family;

import lp.boble.aubos.dto.book.family.FamilyRequest;
import lp.boble.aubos.dto.book.family.FamilyResponse;
import lp.boble.aubos.dto.book.family.FamilyTypeResponse;
import lp.boble.aubos.model.book.family.FamilyModel;
import lp.boble.aubos.model.book.family.FamilyType;
import lp.boble.aubos.model.user.UserModel;
import lp.boble.aubos.util.AuthUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface FamilyMapper {

    @Mapping(target = "name", source = "name")
    @Mapping(target = "coverUrl", source = "coverUrl")
    @Mapping(target = "createdBy", expression = "java(requester())")
    @Mapping(target = "createdAt", expression = "java(now())")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "visibility", ignore = true)
    FamilyModel fromRequestToModel(FamilyRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "type", source = "type.value")
    @Mapping(target = "coverUrl", source = "coverUrl")
    @Mapping(target = "visibility", source = "visibility.value")
    FamilyResponse fromModelToResponse(FamilyModel family);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdate", expression = "java(now())")
    @Mapping(target = "updatedBy", expression = "java(requester())")
    @Mapping(target = "official", ignore = true)
    @Mapping(target = "coverUrl", source = "coverUrl")
    @Mapping(target = "visibility", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void toUpdateFromRequest(@MappingTarget FamilyModel family, FamilyRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "value", source = "value")
    FamilyTypeResponse fromFamilyTypeModelToResponse(FamilyType type);

    default Instant now(){
        return Instant.now();
    }

    default UserModel requester(){
        return AuthUtil.requester();
    }
}
