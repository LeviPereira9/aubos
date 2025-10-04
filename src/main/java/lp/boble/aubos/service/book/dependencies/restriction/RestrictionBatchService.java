package lp.boble.aubos.service.book.dependencies.restriction;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.restriction.RestrictionCreateRequest;
import lp.boble.aubos.mapper.book.dependencies.RestrictionMapper;
import lp.boble.aubos.model.book.dependencies.RestrictionModel;
import lp.boble.aubos.repository.book.depedencies.RestrictionRepository;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.util.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RestrictionBatchService {
    private final RestrictionService restrictionService;
    private final RestrictionMapper restrictionMapper;
    private final RestrictionRepository restrictionRepository;

    @Transactional
    public BatchTransporter<Integer> addRestrictionsInBatch(List<RestrictionCreateRequest> requests){
        ValidationResult<Integer, RestrictionModel> validationResult = this.validateAddBatch(requests);

        this.persistBatch(validationResult.getValidRequests());

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<Integer, RestrictionModel> validateAddBatch(List<RestrictionCreateRequest> requests) {
        ValidationResult<Integer, RestrictionModel> validationResult = new ValidationResult<>();

        Set<RestrictionCreateRequest> uniqueRequests = new HashSet<>(requests);

        List<Integer> existingAges = restrictionService.findExistingAges(uniqueRequests);

        for(RestrictionCreateRequest request : uniqueRequests){
            int requestedAge = request.age();
            boolean hasConflict = existingAges.contains(requestedAge);

            if(hasConflict){
                validationResult.addFailure(requestedAge, "Essa classificação já existe.");
                continue;
            }

            RestrictionModel toAdd = restrictionMapper.toModel(request);

            validationResult.addValid(toAdd);
            validationResult.addSuccess(requestedAge, "Restrição adicionada com sucesso.");
            existingAges.add(requestedAge);
        }

        return validationResult;
    }

    private void persistBatch(List<RestrictionModel> validRequests) {

        if(!validRequests.isEmpty()){
            this.restrictionRepository.saveAll(validRequests);
        }

    }
}
