package lp.boble.aubos.mapper.book.dependencies;

import lp.boble.aubos.dto.book.dependencies.LicenseResponse;
import lp.boble.aubos.dto.book.dependencies.RestrictionResponse;
import lp.boble.aubos.model.book.dependencies.LicenseModel;
import lp.boble.aubos.model.book.dependencies.RestrictionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DependenciesMapper {

    @Named("licenseMapper")
    LicenseResponse toLicenseResponse(LicenseModel license);

    @Named("restrictionMapper")
    RestrictionResponse toRestrictionResponse(RestrictionModel restriction);
}
