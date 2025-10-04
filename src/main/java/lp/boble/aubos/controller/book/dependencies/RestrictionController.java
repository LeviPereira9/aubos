package lp.boble.aubos.controller.book.dependencies;

import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("${api.prefix}/restriction")
@RequiredArgsConstructor
public class RestrictionController {
    private final RestrictionService restrictionService;
    private final RestrictionBatchService restrictionBatchService;

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

        return ResponseEntity.ok().body(response);
    }

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

    @PostMapping("/batch")
    public ResponseEntity<BatchResponse<Integer>> addRestrictionBatch(
            @RequestBody List<RestrictionCreateRequest> requests
    ){
        BatchTransporter<Integer> content = restrictionBatchService.addRestrictionsInBatch(requests);
        int code = content.getStatus();

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
