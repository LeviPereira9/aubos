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
    BookLicenseResponse toLicenseResponse(LicenseModel license);

    @Named("restrictionMapper")
    BookRestrictionResponse toRestrictionResponse(RestrictionModel restriction);

    LanguageModel toLanguageModel(LanguageRequest request);

    LanguageResponse fromModelToLanguageResponse(LanguageModel language);

    LicenseResponse fromModelToLicenseResponse(LicenseModel license);

    TypeResponse fromModelToTypeResponse(TypeModel type);

    StatusResponse fromModelToStatusResponse(StatusModel status);

    RestrictionResponse fromModelToRestrictionResponse(RestrictionModel restriction);
}
