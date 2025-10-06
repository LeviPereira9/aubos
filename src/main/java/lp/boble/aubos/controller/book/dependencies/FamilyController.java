package lp.boble.aubos.controller.book.dependencies;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("${api.prefix}/family")
@RequiredArgsConstructor
public class FamilyController {
    private final FamilyService familyService;

    @GetMapping("/{languageId}")
    public ResponseEntity<SuccessResponse<FamilyResponse>> getFamily(@PathVariable UUID languageId) {

        FamilyResponse content = familyService.getFamily(languageId);

        SuccessResponse<FamilyResponse> response =
                new SuccessResponseBuilder<FamilyResponse>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Família encontrada com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.ok().eTag("").body(response);
    }

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

        return ResponseEntity.ok().eTag("").body(response);
    }

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

    @PutMapping("/{languageId}")
    public ResponseEntity<SuccessResponse<FamilyResponse>> updateFamily(@PathVariable UUID languageId, @RequestBody FamilyRequest request) {
        FamilyResponse content = familyService.updateFamily(languageId, request);

        SuccessResponse<FamilyResponse> response =
                new SuccessResponseBuilder<FamilyResponse>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("Família atualizada com sucesso")
                        .content(content)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{languageId}")
    public ResponseEntity<SuccessResponse<Void>> deleteFamily(@PathVariable UUID languageId) {
        familyService.deleteFamily(languageId);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .code(HttpStatus.OK)
                        .message("Família excluída com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
