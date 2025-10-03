package lp.boble.aubos.service.book.dependencies.license;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.license.LicenseRequest;
import lp.boble.aubos.mapper.book.dependencies.LicenseMapper;
import lp.boble.aubos.model.book.dependencies.LicenseModel;
import lp.boble.aubos.repository.book.depedencies.LicenseRepository;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.util.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LicenseBatchService {

    private final LicenseRepository licenseRepository;
    private final LicenseService licenseService;
    private final LicenseMapper licenseMapper;

    public BatchTransporter<String> addLicensesInBatch(List<LicenseRequest> requests) {
        ValidationResult<String, LicenseModel> validationResult = this.validateAddBatch(requests);

        this.persistBatch(validationResult.getValidRequests());

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<String, LicenseModel> validateAddBatch(List<LicenseRequest> requests) {
        ValidationResult<String, LicenseModel> validationResult = new ValidationResult<>();

        Set<LicenseRequest> uniqueRequests = new HashSet<>(requests);

        List<String> existingLabels = licenseService.findExistingLabels(uniqueRequests);

        for (LicenseRequest request : uniqueRequests) {
            String label = request.label();
            boolean hasLabelConflict = existingLabels.contains(label);

            if(hasLabelConflict) {
                validationResult.addFailure(label, "Essa licença já está registrada em nosso sistema.");
                continue;
            }

            LicenseModel toAdd = licenseMapper.toModel(request);

            validationResult.addValid(toAdd);
            validationResult.addSuccess(label, "Licença criada com sucesso.");

            existingLabels.add(label);
        }

        return validationResult;
    }

    private void persistBatch(List<LicenseModel> validRequests) {
        if(!validRequests.isEmpty()){
            licenseRepository.saveAll(validRequests);
        }
    }

}
