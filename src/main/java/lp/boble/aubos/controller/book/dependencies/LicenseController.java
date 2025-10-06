package lp.boble.aubos.controller.book.dependencies;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.license.LicenseRequest;
import lp.boble.aubos.dto.book.dependencies.license.LicenseResponse;
import lp.boble.aubos.response.batch.BatchResponse;
import lp.boble.aubos.response.batch.BatchResponseBuilder;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.dependencies.license.LicenseBatchService;
import lp.boble.aubos.service.book.dependencies.license.LicenseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Licenças",
        description = "Operações para gerenciar licenças e direitos autorais dos livros"
)
@RestController
@RequestMapping("${api.prefix}/license")
@RequiredArgsConstructor
public class LicenseController {


    private final LicenseService licenseService;
    private final LicenseBatchService licenseBatchService;

    @Operation(
            summary = "Listar todas as licenças",
            description = "Retorna a lista completa de todas as licenças de direitos autorais disponíveis no sistema."
    )
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

    @Operation(
            summary = "Criar nova licença",
            description = "Cadastra uma nova licença de direitos autorais no sistema (ex: Creative Commons, All Rights Reserved)."
    )
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

    @Operation(
            summary = "Atualizar licença",
            description = "Atualiza as informações de uma licença de direitos autorais existente."
    )
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

    @Operation(
            summary = "Adicionar licenças em lote",
            description = "Cadastra múltiplas licenças de uma vez através de requisição em lote. Retorna sucessos e falhas individuais."
    )
    @PostMapping("/batch")
    public ResponseEntity<BatchResponse<String>> addLicensesBatch(
            @RequestBody List<LicenseRequest> requests
    ){
        BatchTransporter<String> content = licenseBatchService.addLicensesInBatch(requests);
        HttpStatus code = content.getStatus();

        BatchResponse<String> response =
                new BatchResponseBuilder<String>()
                        .operation("POST")
                        .code(code)
                        .message("Requisição POST concluída com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(code).body(response);
    }

}
