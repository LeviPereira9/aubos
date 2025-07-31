package lp.boble.aubos.service.book.dependencies;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.BookRequest;
import lp.boble.aubos.dto.book.dependencies.DependencyData;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.*;
import lp.boble.aubos.model.book.relationships.BookLanguage;
import lp.boble.aubos.repository.book.depedencies.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookDependenciesService {
    private final LanguageRepository languageRepository;
    private final TypeRepository typeRepository;
    private final StatusRepository statusRepository;
    private final RestrictionRepository restrictionRepository;
    private final LicenseRepository licenseRepository;


    public LanguageModel getLanguage(Integer id){
        return languageRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public TypeModel getType(Integer id){
        return typeRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public StatusModel getStatus(Integer id){
        return statusRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public RestrictionModel getRestriction(Integer id){
        return restrictionRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public LicenseModel getLicense(Integer id){
        return licenseRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public List<LanguageModel> getAllAvailableLanguages(List<Integer> ids){
        return languageRepository.findAllById(ids);
    }

    public DependencyData loadDependencyData(BookRequest book){

        LanguageModel language = this.getLanguage(book.languageId());
        TypeModel type = this.getType(book.typeId());
        StatusModel status = this.getStatus(book.statusId());
        RestrictionModel restriction = this.getRestriction(book.restrictionId());
        LicenseModel license = this.getLicense(book.licenseId());

        return new DependencyData(language, type, status, restriction, license) ;
    }

}
