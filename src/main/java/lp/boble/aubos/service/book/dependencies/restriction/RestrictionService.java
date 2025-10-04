package lp.boble.aubos.service.book.dependencies.restriction;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.restriction.RestrictionCreateRequest;
import lp.boble.aubos.dto.book.dependencies.restriction.RestrictionResponse;
import lp.boble.aubos.dto.book.dependencies.restriction.RestrictionUpdateRequest;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.dependencies.RestrictionMapper;
import lp.boble.aubos.model.book.dependencies.RestrictionModel;
import lp.boble.aubos.repository.book.depedencies.RestrictionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
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

    @Transactional
    public RestrictionResponse createRestriction(RestrictionCreateRequest request){
        this.validateRestriction(request);

        RestrictionModel restriction = restrictionMapper.toModel(request);

        return restrictionMapper.toResponse(restrictionRepository.save(restriction));
    }

    private void validateRestriction(RestrictionCreateRequest request) {
        int requestAge = request.age();
        boolean hasConflict = restrictionRepository.existsByAge(requestAge);

        if(hasConflict){
            throw CustomDuplicateFieldException.restriction();
        }
    }

    @Transactional
    public RestrictionResponse updateRestriction(Integer restrictionId, RestrictionUpdateRequest request){
        RestrictionModel restriction = this.findRestrictionOrThrow(restrictionId);

        restrictionMapper.update(restriction, request);

        return restrictionMapper.toResponse(restrictionRepository.save(restriction));
    }

    private RestrictionModel findRestrictionOrThrow(Integer restrictionId) {
        return restrictionRepository.findById(restrictionId)
                .orElseThrow(CustomNotFoundException::restriction);
    }

    @Transactional
    public void deleteRestriction(Integer restrictionId){
        boolean exists = restrictionRepository.existsById(restrictionId);

        if(!exists){
            throw CustomNotFoundException.restriction();
        }

        restrictionRepository.deleteById(restrictionId);
    }


    public List<Integer> findExistingAges(Set<RestrictionCreateRequest> requests) {

        Set<Integer> requestedAges = requests.stream().map(RestrictionCreateRequest::age).collect(Collectors.toSet());

        return restrictionRepository.findAllByAge(requestedAges);
    }
}
