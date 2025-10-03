package lp.boble.aubos.service.book.dependencies;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.BookContextRequest;
import lp.boble.aubos.dto.book.dependencies.dependecy.DependencyData;
import lp.boble.aubos.dto.book.dependencies.dependecy.DependencyResponse;
import lp.boble.aubos.dto.book.dependencies.license.LicenseResponse;
import lp.boble.aubos.dto.book.dependencies.restriction.RestrictionResponse;
import lp.boble.aubos.dto.book.dependencies.status.StatusResponse;
import lp.boble.aubos.dto.book.dependencies.type.TypeResponse;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.dependencies.DependenciesMapper;
import lp.boble.aubos.model.book.dependencies.*;
import lp.boble.aubos.repository.book.depedencies.*;
import lp.boble.aubos.service.book.dependencies.language.LanguageService;
import lp.boble.aubos.service.book.dependencies.license.LicenseService;
import lp.boble.aubos.service.book.dependencies.restriction.RestrictionService;
import lp.boble.aubos.service.book.dependencies.status.StatusService;
import lp.boble.aubos.service.book.dependencies.type.TypeService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DependenciesService {

    private final LanguageService languageService;
    private final LicenseService licenseService;
    private final RestrictionService restrictionService;
    private final StatusService statusService;
    private final TypeService typeService;


    public DependencyData loadBookDependencyData(BookContextRequest contextRequest){

        LanguageModel language = languageService.getBookLanguage(contextRequest.languageId());
        TypeModel type = typeService.getBookType(contextRequest.typeId());
        StatusModel status = statusService.getBookStatus(contextRequest.statusId());
        RestrictionModel restriction = restrictionService.getBookRestriction(contextRequest.restrictionId());
        LicenseModel license = licenseService.getBookLicense(contextRequest.licenseId());

        return new DependencyData(language, type, status, restriction, license) ;
    }


}
