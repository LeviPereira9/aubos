package lp.boble.aubos.controller.book;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.family.FamilyRequest;
import lp.boble.aubos.dto.book.family.FamilyResponse;
import lp.boble.aubos.dto.book.family.FamilyTypeResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.family.FamilyService;
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

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<FamilyResponse>> getFamily(@PathVariable("id") UUID id) {

        FamilyResponse content = familyService.getFamily(id);

        SuccessResponse<FamilyResponse> response =
                new SuccessResponseBuilder<FamilyResponse>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Coleção encontrada com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.ok().eTag("").body(response);
    }

    @GetMapping("/types")
    public ResponseEntity<SuccessResponse<List<FamilyTypeResponse>>>
    getFamilyTypes(HttpServletRequest request) {
        List<FamilyTypeResponse> content = familyService.getAllTypes();

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
        FamilyResponse content = familyService.createFamily(request, false);

        SuccessResponse<FamilyResponse> response =
                new SuccessResponseBuilder<FamilyResponse>()
                        .operation("POST")
                        .code(HttpStatus.CREATED)
                        .message("Coleção criada com sucesso")
                        .content(content)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/official")
    public ResponseEntity<SuccessResponse<FamilyResponse>> createOfficialFamily(@RequestBody FamilyRequest request) {
        FamilyResponse content = familyService.createFamily(request, true);

        SuccessResponse<FamilyResponse> response =
                new SuccessResponseBuilder<FamilyResponse>()
                        .operation("POST")
                        .code(HttpStatus.CREATED)
                        .message("Coleção oficial criada com sucesso")
                        .content(content)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<FamilyResponse>> updateFamily(@PathVariable UUID id, @RequestBody FamilyRequest request) {
        FamilyResponse content = familyService.updateFamily(id, request);

        SuccessResponse<FamilyResponse> response =
                new SuccessResponseBuilder<FamilyResponse>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("Coleção atualizada com sucesso")
                        .content(content)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteFamily(@PathVariable UUID id) {
        familyService.deleteFamily(id);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .code(HttpStatus.OK)
                        .message("Coleção excluída com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
