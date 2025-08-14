package lp.boble.aubos.mapper.book.dependencies;

import lp.boble.aubos.dto.book.dependencies.*;
import lp.boble.aubos.dto.book.parts.BookLicenseResponse;
import lp.boble.aubos.dto.book.parts.BookRestrictionResponse;
import lp.boble.aubos.model.book.dependencies.*;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DependenciesMapper {

    @Named("licenseMapper")
    BookLicenseResponse licenseModelToResponse(LicenseModel license);

    @Named("restrictionMapper")
    BookRestrictionResponse restrictionModelToResponse(RestrictionModel restriction);

    LanguageModel languageRequestToModel(LanguageRequest request);

    LanguageResponse fromLanguageModelToResponse(LanguageModel language);

    LicenseResponse fromLicenseModelToResponse(LicenseModel license);

    TypeResponse fromTypeModelToResponse(TypeModel type);

    StatusResponse fromStatusModelToResponse(StatusModel status);

    RestrictionResponse fromRestrictionModelToResponse(RestrictionModel restriction);
}
