package lp.boble.aubos.service.book.dependencies.license;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.license.LicenseRequest;
import lp.boble.aubos.dto.book.dependencies.license.LicenseResponse;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.dependencies.LicenseMapper;
import lp.boble.aubos.model.book.dependencies.LicenseModel;
import lp.boble.aubos.repository.book.depedencies.LicenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final LicenseMapper licenseMapper;

    public List<LicenseResponse> getAllLicense(){
        return licenseRepository.findAll().stream()
                .map(licenseMapper::toResponse)
                .toList();
    }

    @Transactional
    public LicenseResponse createLicense(LicenseRequest request){
        this.validateLicenseRequest(request);

        LicenseModel license = licenseMapper.toModel(request);

        return licenseMapper.toResponse(licenseRepository.save(license));
    }

    @Transactional
    public LicenseResponse updateLicense(int licenseId, LicenseRequest request){
        LicenseModel license = this.findLicenseOrThrow(licenseId);

        if(!license.hasSameLabel(request.label())){
            this.validateLicenseRequest(request);
        }

        licenseMapper.update(license, request);

        return licenseMapper.toResponse(licenseRepository.save(license));
    }

    private LicenseModel findLicenseOrThrow(int licenseId) {
        return licenseRepository.findById(licenseId)
                .orElseThrow(CustomNotFoundException::license);
    }

    private void validateLicenseRequest(LicenseRequest request) {
        String label = request.label();

        boolean hasLabelConflict = licenseRepository.existsByLabel(label);

        if(hasLabelConflict){
            throw CustomDuplicateFieldException.license();
        }

    }
}
