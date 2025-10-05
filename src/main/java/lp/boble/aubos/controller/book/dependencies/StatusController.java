package lp.boble.aubos.controller.book.dependencies;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.status.StatusRequest;
import lp.boble.aubos.dto.book.dependencies.status.StatusResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
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

        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<StatusResponse>> createStatus(
            @RequestBody StatusRequest statusRequest
    ){

        StatusResponse content = statusService.createStatus(statusRequest);
        HttpStatus status = HttpStatus.CREATED;

        SuccessResponse<StatusResponse> response =
                new SuccessResponseBuilder<StatusResponse>()
                        .operation("POST")
                        .code(status)
                        .message("Status criado com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/{statusId}")
    public ResponseEntity<SuccessResponse<StatusResponse>> updateStatus(
            @PathVariable Integer statusId,
            @RequestBody StatusRequest statusRequest
    ){
        StatusResponse content = statusService.updateStatus(statusId, statusRequest);
        HttpStatus status = HttpStatus.OK;

        SuccessResponse<StatusResponse> response =
                new SuccessResponseBuilder<StatusResponse>()
                        .operation("PUT")
                        .code(status)
                        .message("Status atualizado com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/{statusId}")
    public ResponseEntity<SuccessResponse<Void>> deleteStatus(
            @PathVariable Integer statusId
    ){
        statusService.deleteStatus(statusId);
        HttpStatus status = HttpStatus.OK;

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .code(status)
                        .message("Status excluído com sucesso.")
                        .build();

        return ResponseEntity.status(status).body(response);
    }
}
