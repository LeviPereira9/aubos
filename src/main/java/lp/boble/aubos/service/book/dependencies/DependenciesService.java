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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DependenciesService {
    private final LanguageRepository languageRepository;
    private final TypeRepository typeRepository;
    private final StatusRepository statusRepository;
    private final RestrictionRepository restrictionRepository;
    private final LicenseRepository licenseRepository;
    private final ContributorRoleRepository contributorRoleRepository;
    private final DependenciesMapper dependenciesMapper;
    private final LanguageService languageService;



    public TypeModel getBookType(Integer id){
        return typeRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public List<TypeResponse> getAllTypes(){

        return typeRepository.findAll().stream()
                .map(dependenciesMapper::fromTypeModelToResponse)
                .collect(Collectors.toList());
    }

    public StatusModel getBookStatus(Integer id){
        return statusRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public List<StatusResponse> getAllStatus(){
        return statusRepository.findAll().stream()
                .map(dependenciesMapper::fromStatusModelToResponse)
                .collect(Collectors.toList());
    }

    public RestrictionModel getBookRestriction(Integer id){
        return restrictionRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public List<RestrictionResponse> getAllRestriction(){

        return restrictionRepository.findAll().stream()
                .map(dependenciesMapper::fromRestrictionModelToResponse)
                .collect(Collectors.toList());
    }

    public LicenseModel getBookLicense(Integer id){
        return licenseRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public List<LicenseResponse> getAllLicense(){
        return licenseRepository.findAll().stream()
                .map(dependenciesMapper::fromLicenseModelToResponse)
                .collect(Collectors.toList());
    }

    public DependencyData loadBookDependencyData(BookContextRequest contextRequest){

        LanguageModel language = languageService.getBookLanguage(contextRequest.languageId());
        TypeModel type = this.getBookType(contextRequest.typeId());
        StatusModel status = this.getBookStatus(contextRequest.statusId());
        RestrictionModel restriction = this.getBookRestriction(contextRequest.restrictionId());
        LicenseModel license = this.getBookLicense(contextRequest.licenseId());

        return new DependencyData(language, type, status, restriction, license) ;
    }

    @Cacheable(value = "dependencies", key = "'singleton'")
    public DependencyResponse loadBookDependencyResponse(){
        return new DependencyResponse(
                languageService.getAllLanguages(),
                this.getAllLicense(),
                this.getAllRestriction(),
                this.getAllStatus(),
                this.getAllTypes()
        );
    }


}
