package lp.boble.aubos.controller.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
import lp.boble.aubos.dto.book.dependencies.dependecy.DependencyResponse;
import lp.boble.aubos.exception.custom.global.CustomNotModifiedException;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.dependencies.DependenciesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("${api.prefix}/dependencies")
@RequiredArgsConstructor
public class DependenciesController {

    private final ObjectMapper objectMapper;
    private final DependenciesService dependenciesService;

    @GetMapping
    public ResponseEntity<SuccessResponse<DependencyResponse>> getDependencies(
            HttpServletRequest request
    ) {
        DependencyResponse content = dependenciesService.loadBookDependencyResponse();

        String eTag = generateEtag(content);
        String ifNoneMatch = request.getHeader("If-None-Match");

        if(eTag.equals(ifNoneMatch)) {
            throw new CustomNotModifiedException();
        }

        SuccessResponse<DependencyResponse> response =
                new SuccessResponseBuilder<DependencyResponse>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Dependências do livro encontradas com sucesso")
                        .content(content)
                        .build();

        return ResponseEntity.ok()
                .eTag(eTag)
                .cacheControl(CacheProfiles.dependenciesPublic())
                .body(response);
    }

    private String generateEtag(DependencyResponse data) {
        String base;

       try{
           base = objectMapper.writeValueAsString(data);
       } catch (Exception e){
           base = "no-cache-dependencies";
       }

       return "\"" + DigestUtils.md5DigestAsHex(base.getBytes(StandardCharsets.UTF_8)) + "\"";
    }
}
