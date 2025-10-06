package lp.boble.aubos.controller.book.dependencies;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.language.LanguageRequest;
import lp.boble.aubos.dto.book.dependencies.language.LanguageResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.dependencies.language.LanguageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Idiomas",
        description = "Operações para gerenciar idiomas e línguas suportadas pelo sistema"
)
@RestController
@RequestMapping("${api.prefix}/language")
@RequiredArgsConstructor
public class LanguageController {
    private final LanguageService languageService;

    @Operation(
            summary = "Adicionar novo idioma",
            description = "Cadastra um novo idioma/língua no sistema com suas informações básicas e metadados."
    )
    @PostMapping
    public ResponseEntity<SuccessResponse<LanguageResponse>> addLanguage(@RequestBody LanguageRequest request){
        LanguageResponse content = languageService.createLanguage(request);

        SuccessResponse<LanguageResponse> response =
                new SuccessResponseBuilder<LanguageResponse>()
                        .operation("POST")
                        .code(HttpStatus.CREATED)
                        .content(content)
                        .message("Língua adicionada com sucesso")
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Listar todos os idiomas",
            description = "Retorna a lista completa de todos os idiomas e línguas cadastradas no sistema."
    )
    @GetMapping
    public ResponseEntity<SuccessResponse<List<LanguageResponse>>> getAllLanguages(){
        List<LanguageResponse> content = languageService.getAllLanguages();

        SuccessResponse<List<LanguageResponse>> response =
                new SuccessResponseBuilder<List<LanguageResponse>>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Línguas encontradas com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.ok(response);
    }
}
