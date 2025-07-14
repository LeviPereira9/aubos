package lp.boble.aubos.controller.apikey;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
import lp.boble.aubos.config.docSnippets.SelfOrModError;
import lp.boble.aubos.config.docSnippets.UsernameErrors;
import lp.boble.aubos.dto.apikey.ApiKeyCreateResponse;
import lp.boble.aubos.dto.apikey.ApiKeyResponse;
import lp.boble.aubos.exception.custom.global.CustomNotModifiedException;
import lp.boble.aubos.repository.apikey.ApiKeyRepository;
import lp.boble.aubos.response.error.ErrorResponse;
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

    @Operation(
            summary = "Gerar nova chave API",
            description = "Apenas o próprio usuário pode realizar esta ação",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave gerada com sucesso")
    })
    @SelfOrModError
    @UsernameErrors
    @PostMapping("/create")
    public ResponseEntity<SuccessResponse<ApiKeyCreateResponse>>
    createApiKey(@PathVariable String username) {

        ApiKeyCreateResponse dataResponse = apiKeyService.generateAndStoreApiKey(username);

        SuccessResponse<ApiKeyCreateResponse> response =
                new SuccessResponseBuilder<ApiKeyCreateResponse>()
                        .operation("POST")
                        .message("Chave gerada com sucesso.")
                        .code(HttpStatus.CREATED)
                        .data(dataResponse)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Listar todas as chaves ativas",
            description = "Apenas o próprio usuário ou um MOD podem realizar esta ação",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chaves encontradas com sucesso/Usuário não possui nenhuma chave")
    })
    @UsernameErrors
    @SelfOrModError
    @GetMapping
    public ResponseEntity<SuccessResponse<List<ApiKeyResponse>>>
    getApiKeys(@PathVariable String username, HttpServletRequest request) {

        String eTag = this.generateApiKeysEtag(username);
        String ifNoneMatch = request.getHeader("if-none-match");

        if(eTag.equals(ifNoneMatch)){
            throw new CustomNotModifiedException();
        }

        List<ApiKeyResponse> dataResponse = apiKeyService.findAllUserKeys(username);

        String messageResponse = dataResponse.isEmpty() ?
                "Usuário não possui nenhuma chave." // true
                :
                "Chaves encontradas com sucesso."; // false

        SuccessResponse<List<ApiKeyResponse>> response =
                new SuccessResponseBuilder<List<ApiKeyResponse>>()
                        .operation("GET")
                        .message(messageResponse)
                        .code(HttpStatus.OK)
                        .data(dataResponse)
                        .build();

        return ResponseEntity
                .ok()
                .eTag(eTag)
                .cacheControl(CacheProfiles.apiKeyPrivate())
                .body(response);
    }

    @Operation(
            summary = "Desativar chave",
            description = "Apenas o próprio usuário ou um MOD podem realizar esta ação"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave desativada com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Username/Public ID inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Username/Public ID não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @SelfOrModError
    @DeleteMapping("/{publicId}")
    public ResponseEntity<SuccessResponse<Void>> deleteApiKey
    (@PathVariable String username, @PathVariable String publicId) {
        apiKeyService.deleteApiKey(username, publicId);

        SuccessResponse<Void> successResponse =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .message("Chave desativada com sucesso.")
                        .code(HttpStatus.OK)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @Operation(
            summary = "Rotação de Chave",
            description = "Gera uma nova secret, mantendo ainda a mesma chave"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Chave rotacionada com sucesso..."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Chave não encontrada.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @UsernameErrors
    @SelfOrModError
    @PutMapping("/{publicId}/rotate-key")
    public ResponseEntity<SuccessResponse<ApiKeyCreateResponse>>
    rotateApiKey(@PathVariable String username, @PathVariable String publicId) {
        ApiKeyCreateResponse data = apiKeyService.rotateKey(username, publicId);

        SuccessResponse<ApiKeyCreateResponse> response =
                new SuccessResponseBuilder<ApiKeyCreateResponse>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("Chave rotacionada com sucesso. A chave anterior será desativada em 6 horas ou você pode revogar ela agora.")
                        .data(data)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Revogar chave anterior",
            description = "Revoga a chave anterior a rotação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave revogada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Chave não encontrada")
    })
    @UsernameErrors
    @SelfOrModError
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
