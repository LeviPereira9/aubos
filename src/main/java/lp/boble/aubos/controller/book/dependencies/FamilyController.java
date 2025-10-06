package lp.boble.aubos.controller.book.dependencies;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
import lp.boble.aubos.dto.book.family.FamilyRequest;
import lp.boble.aubos.dto.book.family.FamilyResponse;
import lp.boble.aubos.dto.book.family.FamilyTypeResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.dependencies.family.FamilyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Famílias de Livros",
        description = "Operações para gerenciar famílias/séries de livros (ex: Harry Potter, Senhor dos Anéis)"
)
@RestController
@RequestMapping("${api.prefix}/family")
@RequiredArgsConstructor
public class FamilyController {
    private final FamilyService familyService;

    @Operation(
            summary = "Buscar família de livros por ID",
            description = "Recupera uma família/série de livros específica pelo seu identificador único."
    )
    @GetMapping("/{familyId}")
    public ResponseEntity<SuccessResponse<FamilyResponse>> getFamily(@PathVariable UUID familyId) {

        FamilyResponse content = familyService.getFamily(familyId);

        SuccessResponse<FamilyResponse> response =
                new SuccessResponseBuilder<FamilyResponse>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Família encontrada com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.ok()
                .eTag("")
                .cacheControl(CacheProfiles.staticData())
                .body(response);
    }

    @Operation(
            summary = "Listar tipos de famílias",
            description = "Retorna todos os tipos disponíveis de famílias de livros (série, franquia, universo compartilhado, etc.)."
    )
    @GetMapping("/types")
    public ResponseEntity<SuccessResponse<List<FamilyTypeResponse>>>
    getFamilyTypes(HttpServletRequest request) {
        List<FamilyTypeResponse> content = familyService.getAllFamilyTypes();

        SuccessResponse<List<FamilyTypeResponse>> response =
                new SuccessResponseBuilder<List<FamilyTypeResponse>>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Tipos encontrado com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.ok().eTag("").cacheControl(CacheProfiles.staticData()).body(response);
    }

    @Operation(
            summary = "Criar família de livros",
            description = "Cadastra uma nova família/série de livros no sistema para agrupar livros relacionados."
    )
    @PostMapping
    public ResponseEntity<SuccessResponse<FamilyResponse>> createFamily(@RequestBody FamilyRequest request) {
        FamilyResponse content = familyService.createFamily(request);

        SuccessResponse<FamilyResponse> response =
                new SuccessResponseBuilder<FamilyResponse>()
                        .operation("POST")
                        .code(HttpStatus.CREATED)
                        .message("Família criada com sucesso")
                        .content(content)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Criar família de livros oficial",
            description = "Cadastra uma família de livros com status oficial e reconhecimento da editora/autoria."
    )
    @PostMapping("/official")
    public ResponseEntity<SuccessResponse<FamilyResponse>> createOfficialFamily(@RequestBody FamilyRequest request) {
        FamilyResponse content = familyService.createOfficialFamily(request);

        SuccessResponse<FamilyResponse> response =
                new SuccessResponseBuilder<FamilyResponse>()
                        .operation("POST")
                        .code(HttpStatus.CREATED)
                        .message("Família oficial criada com sucesso")
                        .content(content)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Atualizar família de livros",
            description = "Atualiza as informações de uma família/série de livros existente."
    )
    @PutMapping("/{familyId}")
    public ResponseEntity<SuccessResponse<FamilyResponse>> updateFamily(@PathVariable UUID familyId, @RequestBody FamilyRequest request) {
        FamilyResponse content = familyService.updateFamily(familyId, request);

        SuccessResponse<FamilyResponse> response =
                new SuccessResponseBuilder<FamilyResponse>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("Família atualizada com sucesso")
                        .content(content)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Excluir família de livros",
            description = "Remove permanentemente uma família de livros do sistema. Atenção: livros associados serão desvinculados."
    )
    @DeleteMapping("/{familyId}")
    public ResponseEntity<SuccessResponse<Void>> deleteFamily(@PathVariable UUID familyId) {
        familyService.deleteFamily(familyId);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .code(HttpStatus.OK)
                        .message("Família excluída com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
