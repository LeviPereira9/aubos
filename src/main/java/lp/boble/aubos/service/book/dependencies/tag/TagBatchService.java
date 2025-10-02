package lp.boble.aubos.service.book.dependencies.tag;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.tag.TagRequest;
import lp.boble.aubos.mapper.book.dependencies.TagMapper;
import lp.boble.aubos.model.book.dependencies.TagModel;
import lp.boble.aubos.repository.book.depedencies.tag.TagRepository;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.util.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagBatchService {

    private final TagRepository tagRepository;
    private final TagService tagService;
    private final TagMapper tagMapper;


    @Transactional
    public BatchTransporter<String> batchCreateTag(List<TagRequest> requests){

        ValidationResult<String, TagModel> validationResult = this.validateBatchCreateTag(requests);

        this.persistBatch(validationResult.getValidRequests());

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<String, TagModel> validateBatchCreateTag(List<TagRequest> requests) {
        ValidationResult<String, TagModel> validationResult = new ValidationResult<>();

        Set<TagRequest> uniqueRequests = new HashSet<>(requests);

        List<String> requestedTagNames = tagService.getRequestedNames(uniqueRequests);

        for(TagRequest request : uniqueRequests){
            String requestedName = request.name();

            if(requestedTagNames.contains(requestedName)){
                validationResult.addFailure(requestedName, "Essa tag já existe.");
                continue;
            }

            TagModel validTag = tagMapper.toModel(request);

            validationResult.addValid(validTag);
            validationResult.addSuccess(requestedName, "Tag adicionada com sucesso.");
        }

        return validationResult;
    }

    private void persistBatch(List<TagModel> validRequests) {
        if(!validRequests.isEmpty()){
            tagRepository.saveAll(validRequests);
        }
    }



}
