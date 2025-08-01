package lp.boble.aubos.mapper.book.dependencies;

import lp.boble.aubos.dto.book.parts.BookLicenseResponse;
import lp.boble.aubos.dto.book.parts.BookRestrictionResponse;
import lp.boble.aubos.model.book.dependencies.LicenseModel;
import lp.boble.aubos.model.book.dependencies.RestrictionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DependenciesMapper {

    @Named("licenseMapper")
    BookLicenseResponse toLicenseResponse(LicenseModel license);

    @Named("restrictionMapper")
    BookRestrictionResponse toRestrictionResponse(RestrictionModel restriction);
}
