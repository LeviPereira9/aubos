package lp.boble.aubos.controller.book.dependencies;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
import lp.boble.aubos.dto.book.dependencies.restriction.RestrictionCreateRequest;
import lp.boble.aubos.dto.book.dependencies.restriction.RestrictionResponse;
import lp.boble.aubos.dto.book.dependencies.restriction.RestrictionUpdateRequest;
import lp.boble.aubos.response.batch.BatchResponse;
import lp.boble.aubos.response.batch.BatchResponseBuilder;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.dependencies.restriction.RestrictionBatchService;
import lp.boble.aubos.service.book.dependencies.restriction.RestrictionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Restrições Etárias",
        description = "Operações para gerenciar classificações indicativas e restrições etárias dos livros"
)
@RestController
@RequestMapping("${api.prefix}/restriction")
@RequiredArgsConstructor
public class RestrictionController {
    private final RestrictionService restrictionService;
    private final RestrictionBatchService restrictionBatchService;

    @Operation(
            summary = "Listar todas as restrições",
            description = "Retorna a lista completa de todas as classificações indicativas e restrições etárias disponíveis."
    )
    @GetMapping
    public ResponseEntity<SuccessResponse<List<RestrictionResponse>>> getAllRestrictions(){
        List<RestrictionResponse> content = restrictionService.getAllRestriction();

        SuccessResponse<List<RestrictionResponse>> response =
                new SuccessResponseBuilder<List<RestrictionResponse>>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Restrições encontradas com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.ok().cacheControl(CacheProfiles.staticData()).body(response);
    }

    @Operation(
            summary = "Criar nova restrição",
            description = "Cadastra uma nova classificação indicativa/restrição etária no sistema (ex: Livre, 12+, 16+, 18+)."
    )
    @PostMapping
    public ResponseEntity<SuccessResponse<RestrictionResponse>> createRestriction(@RequestBody RestrictionCreateRequest request){
        RestrictionResponse content = restrictionService.createRestriction(request);
        HttpStatus status = HttpStatus.CREATED;

        SuccessResponse<RestrictionResponse> response =
                new SuccessResponseBuilder<RestrictionResponse>()
                        .operation("POST")
                        .code(status)
                        .message("Restrição criada com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(status).body(response);
    }

    @Operation(
            summary = "Atualizar restrição",
            description = "Atualiza parcialmente as informações de uma restrição etária existente."
    )
    @PatchMapping("/{restrictionId}")
    public ResponseEntity<SuccessResponse<RestrictionResponse>> updateRestriction(
            @PathVariable Integer restrictionId,
            @RequestBody RestrictionUpdateRequest request){
        RestrictionResponse content = restrictionService.updateRestriction(restrictionId, request);
        HttpStatus status = HttpStatus.OK;

        SuccessResponse<RestrictionResponse> response =
                new SuccessResponseBuilder<RestrictionResponse>()
                        .operation("PATCH")
                        .code(status)
                        .message("Restrição atualizada com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(status).body(response);
    }

    @Operation(
            summary = "Excluir restrição",
            description = "Remove permanentemente uma restrição etária do sistema."
    )
    @DeleteMapping("/{restrictionId}")
    public ResponseEntity<SuccessResponse<RestrictionResponse>> updateRestriction(
            @PathVariable Integer restrictionId){
        restrictionService.deleteRestriction(restrictionId);
        HttpStatus status = HttpStatus.OK;

        SuccessResponse<RestrictionResponse> response =
                new SuccessResponseBuilder<RestrictionResponse>()
                        .operation("DELETE")
                        .code(status)
                        .message("Restrição removida com sucesso.")
                        .build();

        return ResponseEntity.status(status).body(response);
    }

    @Operation(
            summary = "Adicionar restrições em lote",
            description = "Cadastra múltiplas restrições etárias de uma vez através de requisição em lote."
    )
    @PostMapping("/batch")
    public ResponseEntity<BatchResponse<Integer>> addRestrictionBatch(
            @RequestBody List<RestrictionCreateRequest> requests
    ){
        BatchTransporter<Integer> content = restrictionBatchService.addRestrictionsInBatch(requests);
        HttpStatus code = content.getStatus();

        BatchResponse<Integer> response =
                new BatchResponseBuilder<Integer>()
                        .operation("POST")
                        .code(code)
                        .message("Requisição POST concluída com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(code).body(response);
    }
}
