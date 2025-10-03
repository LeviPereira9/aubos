package lp.boble.aubos.controller.book.dependencies;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.type.TypeRequest;
import lp.boble.aubos.dto.book.dependencies.type.TypeResponse;
import lp.boble.aubos.response.batch.BatchResponse;
import lp.boble.aubos.response.batch.BatchResponseBuilder;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.dependencies.type.TypeBatchService;
import lp.boble.aubos.service.book.dependencies.type.TypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/type")
@RequiredArgsConstructor
public class TypeController {

    private final TypeService typeService;
    private final TypeBatchService typeBatchService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<TypeResponse>>> getAllTypes() {
        List<TypeResponse> content = typeService.getAllTypes();

        SuccessResponse<List<TypeResponse>> response =
                new SuccessResponseBuilder<List<TypeResponse>>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Tipos encontrados com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<TypeResponse>> createType(
            @RequestBody TypeRequest typeRequest
    ){
        TypeResponse content = typeService.createType(typeRequest);
        HttpStatus code = HttpStatus.CREATED;

        SuccessResponse<TypeResponse> response =
                new SuccessResponseBuilder<TypeResponse>()
                        .operation("POST")
                        .code(code)
                        .message("Tipo criado com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(code).body(response);
    }

    @PutMapping("/{typeId}")
    public ResponseEntity<SuccessResponse<TypeResponse>> updateType(@PathVariable Integer typeId, @RequestBody TypeRequest typeRequest ) {
        TypeResponse content = typeService.updateType(typeId, typeRequest);
        HttpStatus code = HttpStatus.OK;

        SuccessResponse<TypeResponse> response =
                new SuccessResponseBuilder<TypeResponse>()
                        .operation("POST")
                        .code(code)
                        .message("Tipo criado com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(code).body(response);
    }

    @DeleteMapping("/{typeId}")
    public ResponseEntity<SuccessResponse<Void>> deleteType(@PathVariable Integer typeId) {
        typeService.deleteType(typeId);

        HttpStatus code = HttpStatus.OK;

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .code(code)
                        .message("Tipo removido com sucesso.")
                        .build();

        return ResponseEntity.status(code).body(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<BatchResponse<String>> addTypesBatch(
            @RequestBody List<TypeRequest> requests
    ){
        BatchTransporter<String> content = typeBatchService.addTypesInBatch(requests);
        int code = content.getStatus();

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
