package lp.boble.aubos.mapper.book.dependencies;

import lp.boble.aubos.dto.book.dependencies.license.LicenseRequest;
import lp.boble.aubos.dto.book.dependencies.license.LicenseResponse;
import lp.boble.aubos.model.book.dependencies.LicenseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LicenseMapper {

    LicenseResponse toResponse(LicenseModel license);

    LicenseModel toModel(LicenseRequest request);

    void update(@MappingTarget LicenseModel license, LicenseRequest request);
}
