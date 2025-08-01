package lp.boble.aubos.service.book.dependencies;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.model.book.dependencies.LanguageModel;
import lp.boble.aubos.repository.book.depedencies.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LanguageService {
    private final LanguageRepository languageRepository;

    public  getLanguage(int id){

    }


    public LanguageModel findLanguageOrThrow(int id){
        return languageRepository.findById(id)
                .orElseThrow(CustomNotFoundException::language);
    }
}
