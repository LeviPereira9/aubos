package lp.boble.aubos.controller.book.dependencies;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.license.LicenseRequest;
import lp.boble.aubos.dto.book.dependencies.license.LicenseResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.dependencies.license.LicenseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/license")
@RequiredArgsConstructor
public class LicenseController {


    private final LicenseService licenseService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<LicenseResponse>>> getAllLicenses() {
        List<LicenseResponse> content = licenseService.getAllLicense();

        SuccessResponse<List<LicenseResponse>> response =
                new SuccessResponseBuilder<List<LicenseResponse>>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Todas as licenças foram encontradas.")
                        .content(content)
                        .build();

        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<LicenseResponse>> createLicense(@RequestBody LicenseRequest request) {
        LicenseResponse content = licenseService.createLicense(request);

        HttpStatus code = HttpStatus.CREATED;

        SuccessResponse<LicenseResponse> response =
                new SuccessResponseBuilder<LicenseResponse>()
                        .operation("POST")
                        .code(code)
                        .message("Licença criada com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(code).body(response);
    }

    @PutMapping("/{licenseId}")
    public ResponseEntity<SuccessResponse<LicenseResponse>> updateLicense(
            @PathVariable Integer licenseId,
            @RequestBody LicenseRequest request) {
        LicenseResponse content = licenseService.updateLicense(licenseId, request);
        HttpStatus code = HttpStatus.OK;

        SuccessResponse<LicenseResponse> response =
                new SuccessResponseBuilder<LicenseResponse>()
                        .operation("PUT")
                        .code(code)
                        .message("Licença atualizada com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(code).body(response);
    }

}
