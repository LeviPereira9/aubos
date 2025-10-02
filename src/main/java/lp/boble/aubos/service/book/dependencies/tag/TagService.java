package lp.boble.aubos.service.book.dependencies.tag;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.tag.TagRequest;
import lp.boble.aubos.dto.book.dependencies.tag.TagResponse;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.mapper.book.dependencies.TagMapper;
import lp.boble.aubos.model.book.dependencies.TagModel;
import lp.boble.aubos.repository.book.depedencies.tag.TagRepository;
import lp.boble.aubos.response.pages.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public PageResponse<TagResponse> findAllTags(int page){

        PageRequest pageRequest = PageRequest.of(page, 30);

        PageResponse<TagModel> tags = new PageResponse<>(tagRepository.findAllByOrderByNameAsc(pageRequest));

        return tags.map(tagMapper::toResponse);
    }

    public PageResponse<TagResponse> searchTag(int page, String query){

        PageRequest pageRequest = PageRequest.of(page, 15);

        PageResponse<TagModel> tags = new PageResponse<>(tagRepository.findTagsBySearch(query, pageRequest));

        return tags.map(tagMapper::toResponse);
    }

    public TagResponse createTag(TagRequest request){
        if(tagRepository.existsByName(request.name())){
            throw CustomDuplicateFieldException.tag();
        }

        TagModel tag = tagMapper.toModel(request);

        return tagMapper.toResponse(tagRepository.save(tag));
    }

    public void deleteTag(int id){
        if(tagRepository.existsById(id)){
            tagRepository.deleteById(id);
        }
    }

}
