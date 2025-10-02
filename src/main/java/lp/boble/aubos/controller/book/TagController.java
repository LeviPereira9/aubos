package lp.boble.aubos.controller.book;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
import lp.boble.aubos.dto.book.dependencies.tag.TagRequest;
import lp.boble.aubos.dto.book.dependencies.tag.TagResponse;
import lp.boble.aubos.response.pages.PageResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.book.dependencies.tag.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<PageResponse<TagResponse>> findAllTags(
            @RequestParam(defaultValue = "0") int page){
        PageResponse<TagResponse> response = tagService.findAllTags(page);

        return ResponseEntity.ok()
                .cacheControl(CacheProfiles.searchFieldPublic())
                .body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<TagResponse>> searchTags(
            @RequestParam(defaultValue = "0") int page, @RequestParam String query ){
        PageResponse<TagResponse> response = tagService.searchTag(page, query);

        return ResponseEntity.ok()
                .cacheControl(CacheProfiles.searchFieldPublic())
                .body(response);
    }

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
}
