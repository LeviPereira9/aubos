package lp.boble.aubos.service.book.dependencies.status;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.status.StatusRequest;
import lp.boble.aubos.mapper.book.dependencies.StatusMapper;
import lp.boble.aubos.model.book.dependencies.StatusModel;
import lp.boble.aubos.repository.book.depedencies.StatusRepository;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.util.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StatusBatchService {

    private final StatusRepository statusRepository;
    private final StatusService statusService;
    private final StatusMapper statusMapper;

    @Transactional
    public BatchTransporter<String> createStatusInBatch(List<StatusRequest> requests){

        ValidationResult<String, StatusModel> validationResult = this.validateCreateBatch(requests);

        this.persistBatch(validationResult.getValidRequests());

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<String, StatusModel> validateCreateBatch(List<StatusRequest> requests) {

        ValidationResult<String, StatusModel> validationResult = new ValidationResult<>();

        Set<StatusRequest> uniqueRequests = new HashSet<>(requests);

        List<String> existingLabels = statusService.findExistingLabels(uniqueRequests);

        for(StatusRequest request : uniqueRequests){
            String label = request.label();
            boolean hasConflict = existingLabels.contains(label);

            if(hasConflict){
                validationResult.addFailure(label, "Status já existe.");
                continue;
            }

            StatusModel toAdd = statusMapper.toModel(request);
            validationResult.addValid(toAdd);
            validationResult.addSuccess(label, "Status criado com sucesso.");
            existingLabels.add(label);
        }

        return validationResult;
    }

    private void persistBatch(List<StatusModel> validRequests) {
        if(!validRequests.isEmpty()){
            statusRepository.saveAll(validRequests);
        }
    }

}
