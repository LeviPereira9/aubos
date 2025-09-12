package lp.boble.aubos.service.book.dependencies;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.contributor.ContributorPageProjection;
import lp.boble.aubos.dto.contributor.ContributorPageResponse;
import lp.boble.aubos.dto.contributor.ContributorRequest;
import lp.boble.aubos.dto.contributor.ContributorResponse;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.contributor.ContributorMapper;
import lp.boble.aubos.model.book.dependencies.ContributorModel;
import lp.boble.aubos.repository.book.depedencies.ContributorRepository;
import lp.boble.aubos.response.pages.PageResponse;
import lp.boble.aubos.util.AuthUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContributorService {
    private final ContributorRepository contributorRepository;
    private final ContributorMapper contributorMapper;
    private final AuthUtil authUtil;

    @Cacheable(value = "contributor", key = "#id", unless = "#result == null")
    public ContributorResponse getContributor(UUID id){
        return contributorMapper.fromModelToResponse(this.findContributorOrThrow(id));
    }

    public ContributorResponse createContributor(ContributorRequest request){

        ContributorModel contributorToSave = this.generateContributor(request);

        return contributorMapper.fromModelToResponse(contributorRepository.save(contributorToSave));
    }

    public ContributorModel generateContributor(ContributorRequest request){
        this.validateContributorNameOrThrow(request.name());

        return contributorMapper.fromRequestToModel(request);
    }

    @CachePut(value = "contributor", key = "#id")
    @CacheEvict(value = "contributorSearch", allEntries = true)
    public ContributorResponse updateContributor(UUID id, ContributorRequest request){

        ContributorModel contributorToUpdate = this.loadContributorToUpdate(id, request);

        return contributorMapper.fromModelToResponse(contributorRepository.save(contributorToUpdate));
    }

    private ContributorModel loadContributorToUpdate(UUID id, ContributorRequest request){
        ContributorModel contributorToUpdate = this.findContributorOrThrow(id);

        this.validateContributorUpdate(contributorToUpdate.getName(), request.name());

        contributorMapper.updateModelFromRequest(contributorToUpdate, request);

        return contributorToUpdate;
    }

    private void validateContributorUpdate(String currentName, String requestedName){
        if(!currentName.equals(requestedName)){
            this.validateContributorNameOrThrow(requestedName);
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "contributor", key = "#contributorId"),
            @CacheEvict(value = "contributorSearch", allEntries = true)}
    )
    public void deleteContributor(UUID contributorId){

        ContributorModel contributor = this.markContributorAsDeleted(contributorId);

        contributorRepository.save(contributor);
    }

    public ContributorModel markContributorAsDeleted(UUID contributorId){
        ContributorModel contributor = this.findContributorOrThrow(contributorId);
        contributor.setLastUpdate(Instant.now());
        contributor.setSoftDeleted(true);

        return contributor;
    }

    @Cacheable(value = "contributorSearch", key = "'search=' + #search + ',page=' + #page")
    public PageResponse<ContributorPageResponse> getContributorSuggestions(String search, int page){

        PageRequest pageRequest = PageRequest.of(page, 10);

        PageResponse<ContributorPageProjection> pagesFound = new PageResponse<>(
                contributorRepository.getAllContributorsSuggestion(pageRequest, search)
        );

        return pagesFound.map(p -> new ContributorPageResponse(p.getId(), p.getName()));
    }


    private void validateContributorNameOrThrow(String name){
        boolean isNameAvailable = contributorRepository.nameIsAvailable(name);

        if(isNameAvailable){
            throw CustomDuplicateFieldException.contributorName();
        }
    }

    public ContributorModel findContributorOrThrow(UUID id){
        return contributorRepository.findContributorById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public Set<UUID> getAllContributorsId(List<UUID> ids){
        return new HashSet<>(contributorRepository.findAllByIdIn(ids));
    }

    public List<ContributorModel> findAllContributorsById(Set<UUID> ids){
        return contributorRepository.findAllById(ids);
    }



}
