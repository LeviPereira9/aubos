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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
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

        this.validateNameOrThrow(request.name());

        ContributorModel contributor = contributorMapper.fromRequestToModel(request);
        contributor.setCreatedBy(authUtil.getRequester());

        return contributorMapper.fromModelToResponse(contributorRepository.save(contributor));
    }

    @CachePut(value = "contributor", key = "#id")
    @CacheEvict(value = "contributorSearch", allEntries = true)
    public ContributorResponse updateContributor(UUID id, ContributorRequest request){

        ContributorModel contributor = this.getContributorOrThrow(id);

        if(!contributor.getName().equals(request.name())){
            this.validateNameOrThrow(request.name());
        }

        contributorMapper.updateModelFromRequest(contributor, request);
        contributor.setUpdatedBy(authUtil.getRequester());

        return contributorMapper.fromModelToResponse(contributorRepository.save(contributor));
    }

    @Caching(evict = {
            @CacheEvict(value = "contributor", key = "#id"),
            @CacheEvict(value = "contributorSearch", allEntries = true)}
    )
    public void deleteContributor(UUID id){
        this.validateCreatorOrThrow(id);

        ContributorModel contributor = this.getContributorOrThrow(id);
        contributor.setLastUpdate(Instant.now());
        contributor.setSoftDeleted(true);

        contributorRepository.save(contributor);
    }

    @Cacheable(value = "contributorSearch", key = "'search=' + #search + ',page=' + #page")
    public PageResponse<ContributorPageResponse> getContributorSuggestions(String search, int page){

        PageRequest pageRequest = PageRequest.of(page, 10);

        PageResponse<ContributorPageProjection> pagesFound = new PageResponse<>(
                contributorRepository.getAllContributorsSuggestion(pageRequest, search)
        );

        return pagesFound.map(p -> new ContributorPageResponse(p.getId(), p.getName()));
    }

    private void validateCreatorOrThrow(UUID id){
        boolean isOwner = contributorRepository.isOwner(id, authUtil.getRequester());
        boolean isAdmin = authUtil.isAdmin();

        if(!isOwner && isAdmin){
            throw CustomForbiddenActionException.notSelfOrAdmin();
        }
    }

    private void validateNameOrThrow(String name){
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
