package lp.boble.aubos.controller.book.dependencies;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.status.StatusRequest;
import lp.boble.aubos.dto.book.dependencies.status.StatusResponse;
import lp.boble.aubos.response.batch.BatchResponse;
import lp.boble.aubos.response.batch.BatchResponseBuilder;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.dependencies.status.StatusBatchService;
import lp.boble.aubos.service.book.dependencies.status.StatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/status")
@RequiredArgsConstructor
public class StatusController {


    private final StatusService statusService;
    private final StatusBatchService statusBatchService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<StatusResponse>>> getAllStatus(){
        List<StatusResponse> content = statusService.getAllStatus();

        SuccessResponse<List<StatusResponse>> response =
                new SuccessResponseBuilder<List<StatusResponse> >()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Status encontrados com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<StatusResponse>> createStatus(
            @RequestBody StatusRequest statusRequest
    ){

        StatusResponse content = statusService.createStatus(statusRequest);
        HttpStatus code = HttpStatus.CREATED;

        SuccessResponse<StatusResponse> response =
                new SuccessResponseBuilder<StatusResponse>()
                        .operation("POST")
                        .code(code)
                        .message("Status criado com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(code).body(response);
    }

    @PutMapping("/{statusId}")
    public ResponseEntity<SuccessResponse<StatusResponse>> updateStatus(
            @PathVariable Integer statusId,
            @RequestBody StatusRequest statusRequest
    ){
        StatusResponse content = statusService.updateStatus(statusId, statusRequest);

        SuccessResponse<StatusResponse> response =
                new SuccessResponseBuilder<StatusResponse>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("Status atualizado com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{statusId}")
    public ResponseEntity<SuccessResponse<Void>> deleteStatus(
            @PathVariable Integer statusId
    ){
        statusService.deleteStatus(statusId);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .code(HttpStatus.OK)
                        .message("Status excluído com sucesso.")
                        .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<BatchResponse<String>> createStatusInBatch(
            @RequestBody List<StatusRequest> requests
    ){
        BatchTransporter<String> content = statusBatchService.createStatusInBatch(requests);
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
