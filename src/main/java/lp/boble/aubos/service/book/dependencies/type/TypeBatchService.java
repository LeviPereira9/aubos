package lp.boble.aubos.service.book.dependencies.type;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.type.TypeRequest;
import lp.boble.aubos.mapper.book.dependencies.TypeMapper;
import lp.boble.aubos.model.book.dependencies.TypeModel;
import lp.boble.aubos.repository.book.depedencies.TypeRepository;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.util.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TypeBatchService {

    private final TypeRepository typeRepository;
    private final TypeService typeService;
    private final TypeMapper typeMapper;

    public BatchTransporter<String> addTypesInBatch(List<TypeRequest> requests){
        ValidationResult<String, TypeModel> validationResult = this.validateAddBatch(requests);

        this.persistBatch(validationResult.getValidRequests());

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<String, TypeModel> validateAddBatch(List<TypeRequest> requests) {
        ValidationResult<String, TypeModel> validationResult = new ValidationResult<>();

        Set<TypeRequest> uniqueRequests = new HashSet<>(requests);

        List<String> existingTypes = typeService.findExistingTypes(uniqueRequests);

        for(TypeRequest request: uniqueRequests){
            String type = request.name();
            boolean hasConflict = existingTypes.contains(type);

            if(hasConflict){
                validationResult.addFailure(type, "Esse tipo já existe.");
                continue;
            }

            TypeModel toAdd = typeMapper.toModel(request);

            validationResult.addValid(toAdd);
            validationResult.addSuccess(type, "Tipo adicionado com sucesso.");
            existingTypes.add(type);
        }

        return validationResult;
    }

    private void persistBatch(List<TypeModel> validRequests) {
        if(!validRequests.isEmpty()){
            typeRepository.saveAll(validRequests);
        }
    }
}
