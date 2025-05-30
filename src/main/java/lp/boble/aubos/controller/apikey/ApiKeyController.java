package lp.boble.aubos.controller.apikey;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.apikey.ApiKeyCreateResponse;
import lp.boble.aubos.dto.apikey.ApiKeyResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.apikey.ApiKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/{username}/apikey")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping("/create")
    public ResponseEntity<SuccessResponse<ApiKeyCreateResponse>>
    createApiKey(@PathVariable String username) {

        ApiKeyCreateResponse dataResponse = apiKeyService.generateAndStoreApiKey(username);

        SuccessResponse<ApiKeyCreateResponse> response =
                new SuccessResponseBuilder<ApiKeyCreateResponse>()
                        .operation("POST")
                        .message("Api key criada com sucesso.")
                        .code(HttpStatus.CREATED)
                        .data(dataResponse)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    };

    @GetMapping
    public ResponseEntity<SuccessResponse<List<ApiKeyResponse>>>
    getApiKeys(@PathVariable String username) {
        List<ApiKeyResponse> dataResponse = apiKeyService.findAllUserKeys(username);

        String messageResponse = dataResponse.isEmpty() ?
                "Usuário ainda não possui nenhuma chave." // true
                :
                "Chaves encontradas com sucesso."; // false

        SuccessResponse<List<ApiKeyResponse>> response =
                new SuccessResponseBuilder<List<ApiKeyResponse>>()
                        .operation("GET")
                        .message(messageResponse)
                        .code(HttpStatus.OK)
                        .data(dataResponse)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<SuccessResponse<Void>> deleteApiKey
    (@PathVariable String username, @PathVariable String publicId) {
        apiKeyService.deleteApiKey(username, publicId);

        SuccessResponse<Void> successResponse =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .message("Api key deletado com sucesso.")
                        .code(HttpStatus.OK)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

}
