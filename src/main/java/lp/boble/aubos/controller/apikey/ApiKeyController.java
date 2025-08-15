package lp.boble.aubos.controller.apikey;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
import lp.boble.aubos.config.documentation.apikey.*;
import lp.boble.aubos.dto.apikey.ApiKeyCreateResponse;
import lp.boble.aubos.dto.apikey.ApiKeyResponse;
import lp.boble.aubos.exception.custom.global.CustomNotModifiedException;
import lp.boble.aubos.repository.apikey.ApiKeyRepository;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.apikey.ApiKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Tag(
        name = "API Keys",
        description = "Endpoints para gerencimaneto das chaves de Api, incluindo e revogação de chaves de acesso")
@RestController
@RequestMapping("${api.prefix}/user/{username}/apikey")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final ApiKeyRepository apiKeyRepository;

    @DocCreateApiKey
    @PostMapping("/create")
    public ResponseEntity<SuccessResponse<ApiKeyCreateResponse>>
    createApiKey(@PathVariable String username) {

        ApiKeyCreateResponse content = apiKeyService.generateApiKey(username);

        SuccessResponse<ApiKeyCreateResponse> response =
                new SuccessResponseBuilder<ApiKeyCreateResponse>()
                        .operation("POST")
                        .message("Chave gerada com sucesso.")
                        .code(HttpStatus.CREATED)
                        .content(content)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DocGetApiKeys
    @GetMapping
    public ResponseEntity<SuccessResponse<List<ApiKeyResponse>>>
    getApiKeys(@PathVariable String username, HttpServletRequest request) {

        String eTag = this.generateApiKeysEtag(username);
        String ifNoneMatch = request.getHeader("if-none-match");

        if(eTag.equals(ifNoneMatch)){
            throw new CustomNotModifiedException();
        }

        List<ApiKeyResponse> content = apiKeyService.findAllUserKeys(username);

        String message = content.isEmpty() ?
                "Usuário não possui nenhuma chave." // true
                :
                "Chaves encontradas com sucesso."; // false

        SuccessResponse<List<ApiKeyResponse>> response =
                new SuccessResponseBuilder<List<ApiKeyResponse>>()
                        .operation("GET")
                        .message(message)
                        .code(HttpStatus.OK)
                        .content(content)
                        .build();

        return ResponseEntity
                .ok()
                .eTag(eTag)
                .cacheControl(CacheProfiles.apiKeyPrivate())
                .body(response);
    }


    @DocDeleteApiKey
    @DeleteMapping("/{publicId}")
    public ResponseEntity<SuccessResponse<Void>> deleteApiKey
    (@PathVariable String username, @PathVariable String publicId) {
        apiKeyService.disableApiKey(username, publicId);

        SuccessResponse<Void> successResponse =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .message("Chave desativada com sucesso.")
                        .code(HttpStatus.OK)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @DocRotateApiKey
    @PutMapping("/{publicId}/rotate-key")
    public ResponseEntity<SuccessResponse<ApiKeyCreateResponse>>
    rotateApiKey(@PathVariable String username, @PathVariable String publicId) {
        ApiKeyCreateResponse content = apiKeyService.rotateKey(username, publicId);

        SuccessResponse<ApiKeyCreateResponse> response =
                new SuccessResponseBuilder<ApiKeyCreateResponse>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("Chave rotacionada com sucesso. A chave anterior será desativada em 6 horas ou você pode revogar ela agora.")
                        .content(content)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DocRevokePreviousHash
    @PutMapping("/{publicId}/revoke-previous")
    public ResponseEntity<SuccessResponse<Void>> revokePreviousHash(
            @PathVariable String username,
            @PathVariable String publicId){

        apiKeyService.revokePreviousHashSecret(username, publicId);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("Chave anterior revogada com sucesso.")
                        .build();


        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private String generateApiKeysEtag(String username){
        List<Instant> updates = apiKeyRepository.getLastUpdatedKeys(username)
                .orElse(new ArrayList<>());

        String toHash = String.join(updates
                .stream()
                .sorted()
                .toString(), ",");

        return DigestUtils.md5DigestAsHex(toHash.getBytes(StandardCharsets.UTF_8));
    }

}
