package lp.boble.aubos.service.book.dependencies;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.LanguageRequest;
import lp.boble.aubos.dto.book.dependencies.LanguageResponse;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.dependencies.DependenciesMapper;
import lp.boble.aubos.model.book.dependencies.LanguageModel;
import lp.boble.aubos.repository.book.depedencies.LanguageRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LanguageService {
    private final LanguageRepository languageRepository;
    private final DependenciesMapper dependenciesMapper;


    public LanguageModel getLanguage(int id){
        return this.findLanguageOrThrow(id);
    }

    public List<LanguageResponse> getAllLanguages(){
        return languageRepository.findAll().stream()
                .map(dependenciesMapper::fromLanguageModelToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "dependencies", key = "'singleton'")
    public LanguageResponse createLanguage(LanguageRequest request){
        LanguageModel language = dependenciesMapper.languageRequestToModel(request);

        return dependenciesMapper.fromLanguageModelToResponse(languageRepository.save(language));
    }


    public LanguageModel findLanguageOrThrow(int id){
        return languageRepository.findById(id)
                .orElseThrow(CustomNotFoundException::language);
    }
}
