package lp.boble.aubos.controller.book;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.LanguageRequest;
import lp.boble.aubos.dto.book.dependencies.LanguageResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.dependencies.LanguageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/language")
@RequiredArgsConstructor
public class LanguageController {
    private final LanguageService languageService;

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
}
