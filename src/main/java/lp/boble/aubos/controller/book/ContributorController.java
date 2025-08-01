package lp.boble.aubos.controller.book;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
import lp.boble.aubos.dto.contributor.ContributorPageProjection;
import lp.boble.aubos.dto.contributor.ContributorPageResponse;
import lp.boble.aubos.dto.contributor.ContributorRequest;
import lp.boble.aubos.dto.contributor.ContributorResponse;
import lp.boble.aubos.exception.custom.global.CustomNotModifiedException;
import lp.boble.aubos.repository.book.depedencies.ContributorRepository;
import lp.boble.aubos.response.pages.PageResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.dependencies.ContributorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/contributor")
@RequiredArgsConstructor
public class ContributorController {
    private final ContributorService contributorService;
    private final ContributorRepository contributorRepository;

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<ContributorResponse>>
    getContributor(@PathVariable UUID id, HttpServletRequest request
            ) {
        String eTag = this.generateContributorEtag(id);
        String ifNoneMatch = request.getHeader("If-None-Match");

        if(eTag.equals(ifNoneMatch)) {
            throw new CustomNotModifiedException();
        }

        ContributorResponse data = contributorService.getContributor(id);

        SuccessResponse<ContributorResponse> response =
                new SuccessResponseBuilder<ContributorResponse>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Contribuidor encontrado com sucesso.")
                        .data(data)
                        .build();


        return ResponseEntity.ok()
                .eTag(eTag)
                .cacheControl(CacheProfiles.contributor())
                .body(response);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<PageResponse<ContributorPageResponse>>
    getContributorsSuggestions(
            @RequestParam String search,
            @RequestParam(defaultValue = "0") int page){

        PageResponse<ContributorPageResponse> data = contributorService.getContributorSuggestions(search, page);

        return ResponseEntity.ok()
                .cacheControl(CacheProfiles.search())
                .body(data);
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<ContributorResponse>> createContributor(
            @RequestBody ContributorRequest contributorRequest
    ){
       ContributorResponse data = contributorService.createContributor(contributorRequest);

       SuccessResponse<ContributorResponse> response =
               new SuccessResponseBuilder<ContributorResponse>()
                       .operation("POST")
                       .code(HttpStatus.CREATED)
                       .message("Contribuidor criado com sucesso.")
                       .data(data)
                       .build();

       return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<ContributorResponse>> updateContributor(
            @PathVariable UUID id, @RequestBody ContributorRequest contributorRequest
    ){
        ContributorResponse data = contributorService.updateContributor(id, contributorRequest);

        SuccessResponse<ContributorResponse> response =
                new SuccessResponseBuilder<ContributorResponse>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("Contribuidor atualizado com sucesso.")
                        .data(data)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteContributor(
            @PathVariable UUID id
    ){
        contributorService.deleteContributor(id);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .code(HttpStatus.OK)
                        .message("Contribuidor deletado com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private String generateContributorEtag(UUID id){
        Instant lastUpdate = contributorRepository.getLastUpdate(id)
                .orElse(null);

        String base = (lastUpdate != null)
                ? lastUpdate.toString()
                : "no-update"+id.toString();

        return "\"" + DigestUtils.md5DigestAsHex(base.getBytes(StandardCharsets.UTF_8)) + "\"";
    }

}
