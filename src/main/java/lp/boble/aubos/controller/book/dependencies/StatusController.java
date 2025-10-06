package lp.boble.aubos.controller.book.dependencies;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
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

@Tag(
        name = "Status dos Livros",
        description = "Operações para gerenciar os status de publicação e disponibilidade dos livros"
)
@RestController
@RequestMapping("${api.prefix}/status")
@RequiredArgsConstructor
public class StatusController {


    private final StatusService statusService;
    private final StatusBatchService statusBatchService;

    @Operation(
            summary = "Listar todos os status",
            description = "Retorna a lista completa de todos os status de publicação disponíveis (ex: Rascunho, Publicado, Arquivado)."
    )
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

        return ResponseEntity.ok().cacheControl(CacheProfiles.staticData()).body(response);
    }

    @Operation(
            summary = "Criar novo status",
            description = "Cadastra um novo status de publicação no sistema para classificar o estado dos livros."
    )
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

    @Operation(
            summary = "Atualizar status",
            description = "Atualiza completamente as informações de um status de publicação existente."
    )
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

    @Operation(
            summary = "Excluir status",
            description = "Remove permanentemente um status de publicação do sistema."
    )
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

    @Operation(
            summary = "Criar status em lote",
            description = "Cadastra múltiplos status de publicação de uma vez através de requisição em lote."
    )
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
