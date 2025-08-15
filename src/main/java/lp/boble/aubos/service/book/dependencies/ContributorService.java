package lp.boble.aubos.service.book.dependencies;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.contributor.ContributorPageProjection;
import lp.boble.aubos.dto.contributor.ContributorPageResponse;
import lp.boble.aubos.dto.contributor.ContributorRequest;
import lp.boble.aubos.dto.contributor.ContributorResponse;
import lp.boble.aubos.exception.custom.auth.CustomForbiddenActionException;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.contributor.ContributorMapper;
import lp.boble.aubos.model.book.dependencies.ContributorModel;
import lp.boble.aubos.repository.book.depedencies.ContributorRepository;
import lp.boble.aubos.repository.book.depedencies.ContributorRoleRepository;
import lp.boble.aubos.response.pages.PageResponse;
import lp.boble.aubos.util.AuthUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContributorService {
    private final ContributorRepository contributorRepository;
    private final ContributorRoleRepository contributorRoleRepository;
    private final ContributorMapper contributorMapper;
    private final AuthUtil authUtil;



    @Cacheable(value = "contributor", key = "#id", unless = "#result == null")
    public ContributorResponse getContributor(UUID id){
        return contributorMapper.fromModelToResponse(this.getContributorOrThrow(id));
    }

    public ContributorResponse createContributor(ContributorRequest request){

        this.validateContributorNameOrThrow(request.name());

        ContributorModel contributorToSave = contributorMapper.fromRequestToModel(request);
        contributorToSave.setCreatedBy(authUtil.getRequester());

        return contributorMapper.fromModelToResponse(contributorRepository.save(contributorToSave));
    }

    @CachePut(value = "contributor", key = "#id")
    @CacheEvict(value = "contributorSearch", allEntries = true)
    public ContributorResponse updateContributor(UUID id, ContributorRequest request){

        ContributorModel contributorToUpdate = this.getContributorOrThrow(id);

        if(!contributorToUpdate.getName().equals(request.name())){
            this.validateContributorNameOrThrow(request.name());
        }

        contributorMapper.updateModelFromRequest(contributorToUpdate, request);
        contributorToUpdate.setUpdatedBy(authUtil.getRequester());

        return contributorMapper.fromModelToResponse(contributorRepository.save(contributorToUpdate));
    }

    @Caching(evict = {
            @CacheEvict(value = "contributor", key = "#contributorId"),
            @CacheEvict(value = "contributorSearch", allEntries = true)}
    )
    public void deleteContributor(UUID contributorId){
        this.validateCreatedByOrThrow(contributorId);

        ContributorModel contributorToDelete = this.getContributorOrThrow(contributorId);
        contributorToDelete.setLastUpdate(Instant.now());
        contributorToDelete.setSoftDeleted(true);

        contributorRepository.save(contributorToDelete);
    }

    @Cacheable(value = "contributorSearch", key = "'search=' + #search + ',page=' + #page")
    public PageResponse<ContributorPageResponse> getContributorSuggestions(String search, int page){

        PageRequest pageRequest = PageRequest.of(page, 10);

        PageResponse<ContributorPageProjection> pagesFound = new PageResponse<>(
                contributorRepository.getAllContributorsSuggestion(pageRequest, search)
        );

        return pagesFound.map(p -> new ContributorPageResponse(p.getId(), p.getName()));
    }

    private void validateCreatedByOrThrow(UUID id){
        boolean isOwner = contributorRepository.isOwner(id, authUtil.getRequester());
        boolean isAdmin = authUtil.isAdmin();

        if(!isOwner && isAdmin){
            throw CustomForbiddenActionException.notSelfOrAdmin();
        }
    }

    private void validateContributorNameOrThrow(String name){
        boolean isNameAvailable = contributorRepository.nameIsAvailable(name);

        if(isNameAvailable){
            throw CustomDuplicateFieldException.contributorName();
        }
    }

    public ContributorModel getContributorOrThrow(UUID id){
        return contributorRepository.findContributorById(id)
                .orElseThrow(CustomNotFoundException::user);
    }




}
