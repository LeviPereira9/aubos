package lp.boble.aubos.service.book.dependencies;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.BookRequest;
import lp.boble.aubos.dto.book.dependencies.*;
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

    public ContributorRole getRole(Integer id){
        return contributorRoleRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public List<ContributorRole> getAllRoles(){
        return contributorRoleRepository.findAll();
    }

    public LanguageModel getLanguage(Integer id){
        return languageRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public TypeModel getType(Integer id){
        return typeRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public List<TypeResponse> getAllTypes(){

        return typeRepository.findAll().stream()
                .map(dependenciesMapper::fromModelToTypeResponse)
                .collect(Collectors.toList());
    }

    public StatusModel getStatus(Integer id){
        return statusRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public List<StatusResponse> getAllStatus(){
        return statusRepository.findAll().stream()
                .map(dependenciesMapper::fromModelToStatusResponse)
                .collect(Collectors.toList());
    }

    public RestrictionModel getRestriction(Integer id){
        return restrictionRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public List<RestrictionResponse> getAllRestriction(){

        return restrictionRepository.findAll().stream()
                .map(dependenciesMapper::fromModelToRestrictionResponse)
                .collect(Collectors.toList());
    }

    public LicenseModel getLicense(Integer id){
        return licenseRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public List<LicenseResponse> getAllLicense(){
        return licenseRepository.findAll().stream()
                .map(dependenciesMapper::fromModelToLicenseResponse)
                .collect(Collectors.toList());
    }

    public DependencyData loadDependencyData(BookRequest book){

        LanguageModel language = languageService.getLanguage(book.languageId());
        TypeModel type = this.getType(book.typeId());
        StatusModel status = this.getStatus(book.statusId());
        RestrictionModel restriction = this.getRestriction(book.restrictionId());
        LicenseModel license = this.getLicense(book.licenseId());

        return new DependencyData(language, type, status, restriction, license) ;
    }

    @Cacheable(value = "dependencies", key = "'singleton'")
    public DependencyResponse loadDependencyResponse(){
        return new DependencyResponse(
                languageService.getAllLanguages(),
                this.getAllLicense(),
                this.getAllRestriction(),
                this.getAllStatus(),
                this.getAllTypes()
        );
    }


}
