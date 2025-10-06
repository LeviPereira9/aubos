package lp.boble.aubos.controller.book.dependencies;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
import lp.boble.aubos.dto.book.dependencies.tag.TagRequest;
import lp.boble.aubos.dto.book.dependencies.tag.TagResponse;
import lp.boble.aubos.response.batch.BatchResponse;
import lp.boble.aubos.response.batch.BatchResponseBuilder;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.response.pages.PageResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.dependencies.tag.TagBatchService;
import lp.boble.aubos.service.book.dependencies.tag.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Tags",
        description = "Operações para gerenciar tags e categorias dos livros"
)
@RestController
@RequestMapping("${api.prefix}/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TagBatchService tagBatchService;

    @Operation(
            summary = "Listar tags paginadas",
            description = "Retorna todas as tags disponíveis de forma paginada, ordenadas alfabeticamente."
    )
    @GetMapping
    public ResponseEntity<PageResponse<TagResponse>> findAllTags(
            @RequestParam(defaultValue = "0") int page){
        PageResponse<TagResponse> response = tagService.findAllTags(page);

        return ResponseEntity.ok()
                .cacheControl(CacheProfiles.searchFieldPublic())
                .body(response);
    }

    @Operation(
            summary = "Buscar tags",
            description = "Busca tags por termo de pesquisa, retornando resultados paginados para autocomplete e busca."
    )
    @GetMapping("/search")
    public ResponseEntity<PageResponse<TagResponse>> searchTags(
            @RequestParam(defaultValue = "0") int page, @RequestParam String query ){
        PageResponse<TagResponse> response = tagService.searchTag(page, query);

        return ResponseEntity.ok()
                .cacheControl(CacheProfiles.searchFieldPublic())
                .body(response);
    }

    @Operation(
            summary = "Criar nova tag",
            description = "Cadastra uma nova tag no sistema para categorização e organização dos livros."
    )
    @PostMapping
    public ResponseEntity<SuccessResponse<TagResponse>> addTag(@RequestBody TagRequest request){

        TagResponse content = tagService.createTag(request);
        HttpStatus code = HttpStatus.CREATED;

        SuccessResponse<TagResponse> response =
                new SuccessResponseBuilder<TagResponse>()
                        .operation("POST")
                        .message("Tag criada com sucesso.")
                        .code(code)
                        .content(content)
                        .build();


        return ResponseEntity.status(code).body(response);
    }

    @Operation(
            summary = "Atualizar tag",
            description = "Atualiza as informações de uma tag existente no sistema."
    )
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<TagResponse>> updateTag(@PathVariable Integer id, @RequestBody TagRequest request){

        TagResponse content = tagService.updateTag(id, request);
        HttpStatus code = HttpStatus.OK;

        SuccessResponse<TagResponse> response =
                new SuccessResponseBuilder<TagResponse>()
                        .operation("PUT")
                        .message("Tag modificado com sucesso.")
                        .code(code)
                        .content(content)
                        .build();

        return ResponseEntity.status(code).body(response) ;
    }

    @Operation(
            summary = "Excluir tag",
            description = "Remove permanentemente uma tag do sistema. Atenção: esta ação afeta a categorização dos livros."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteTag(@PathVariable int id){

        tagService.deleteTag(id);

        HttpStatus code = HttpStatus.NO_CONTENT;

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .message("Tag deletada com sucesso.")
                        .code(code)
                        .build();

        return ResponseEntity.status(code).body(response);
    }

    @Operation(
            summary = "Criar tags em lote",
            description = "Cadastra múltiplas tags de uma vez através de requisição em lote."
    )
    @PostMapping("/batch")
    public ResponseEntity<BatchResponse<String>> addBatchTag(@RequestBody List<TagRequest> requests){
        BatchTransporter<String> content = tagBatchService.batchCreateTag(requests);
        HttpStatus code = content.getStatus();

        BatchResponse<String> response =
                new BatchResponseBuilder<String>()
                        .operation("POST")
                        .message("Requisição de criação concluída com sucesso..")
                        .code(code)
                        .content(content)
                        .build();

        return ResponseEntity.status(code).body(response);
    }
}
