package lp.boble.aubos.mapper.book.family;

import lp.boble.aubos.dto.book.family.FamilyRequest;
import lp.boble.aubos.dto.book.family.FamilyResponse;
import lp.boble.aubos.dto.book.family.FamilyTypeResponse;
import lp.boble.aubos.model.book.family.FamilyModel;
import lp.boble.aubos.model.book.family.FamilyType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeFamilyInformation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FamilyMapper {

    @Mapping(target = "name", source = "name")
    @Mapping(target = "coverUrl", source = "coverUrl")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "visibility", ignore = true)
    FamilyModel toModel(FamilyRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "type", source = "type.value")
    @Mapping(target = "coverUrl", source = "coverUrl")
    @Mapping(target = "visibility", source = "visibility.value")
    @Mapping(target = "shareToken", source = "shareToken")
    FamilyResponse toResponse(FamilyModel family);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "official", ignore = true)
    @Mapping(target = "coverUrl", source = "coverUrl")
    @Mapping(target = "visibility", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "shareToken", ignore = true)
    @Mapping(target = "shareTokenExpiresAt", ignore = true)
    void updateFamily(@MappingTarget FamilyModel family, FamilyRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "value", source = "value")
    FamilyTypeResponse toTypeResponse(FamilyType type);
}
