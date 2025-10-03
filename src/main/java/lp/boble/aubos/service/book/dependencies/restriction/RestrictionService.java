package lp.boble.aubos.service.book.dependencies.restriction;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.restriction.RestrictionResponse;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.dependencies.RestrictionMapper;
import lp.boble.aubos.model.book.dependencies.RestrictionModel;
import lp.boble.aubos.repository.book.depedencies.RestrictionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestrictionService {

    private final RestrictionRepository restrictionRepository;
    private final RestrictionMapper restrictionMapper;

    public List<RestrictionResponse> getAllRestriction(){
        return restrictionRepository.findAll().stream()
                .map(restrictionMapper::toResponse)
                .collect(Collectors.toList());
    }

    public RestrictionModel getBookRestriction(Integer id){
        return restrictionRepository.findById(id)
                .orElseThrow(CustomNotFoundException::restriction);
    }

}
