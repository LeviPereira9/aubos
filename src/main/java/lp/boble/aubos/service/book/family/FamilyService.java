package lp.boble.aubos.service.book.family;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.family.FamilyDependenciesData;
import lp.boble.aubos.dto.book.family.FamilyRequest;
import lp.boble.aubos.dto.book.family.FamilyResponse;
import lp.boble.aubos.dto.book.family.FamilyTypeResponse;
import lp.boble.aubos.exception.custom.auth.CustomForbiddenActionException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.family.FamilyMapper;
import lp.boble.aubos.model.Enum.VisiblityEnum;
import lp.boble.aubos.model.book.family.FamilyModel;
import lp.boble.aubos.model.book.family.FamilyType;
import lp.boble.aubos.model.book.family.Visibility;
import lp.boble.aubos.repository.book.family.FamilyRepository;
import lp.boble.aubos.repository.book.family.FamilyTypeRepository;
import lp.boble.aubos.repository.book.family.VisibilityRepository;
import lp.boble.aubos.util.AuthUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FamilyService {
    private final FamilyRepository familyRepository;
    private final FamilyTypeRepository familyTypeRepository;
    private final AuthUtil authUtil;
    private final FamilyMapper familyMapper;
    private final VisibilityRepository visibilityRepository;

    @Transactional
    public FamilyResponse createFamily(FamilyRequest request, boolean isOfficial) {
        FamilyModel familyToCreate = familyMapper.fromRequestToModel(request);
        familyToCreate.setCreatedBy(authUtil.getRequester());
        familyToCreate.setCreatedAt(Instant.now());

        this.applyDependenciesToFamily(familyToCreate, request);

        System.out.println(authUtil.getRequester().getAuthorities());

        if(isOfficial){
            familyToCreate.setOfficial(true);
        }


        return familyMapper.fromModelToResponse(familyRepository.save(familyToCreate));
    }

    @Transactional
    public FamilyResponse updateFamily(UUID id, FamilyRequest familyRequest) {
        FamilyModel familyToUpdate = this.findFamilyOrThrow(id);
        familyToUpdate.setUpdatedBy(authUtil.getRequester());

        familyMapper.toUpdateFromRequest(familyToUpdate, familyRequest);

        this.applyDependenciesToFamily(familyToUpdate, familyRequest);

        return familyMapper.fromModelToResponse(familyRepository.save(familyToUpdate));
    }

    @Transactional
    public void deleteFamily(UUID id) {
        this.validateOwnershipOrThrow(id);

        familyRepository.deleteById(id);
    }

    public FamilyResponse getFamily(UUID id) {
        return familyMapper.fromModelToResponse(this.findFamilyOrThrow(id));
    }

    public List<FamilyTypeResponse> getAllFamilyTypes() {
        return familyTypeRepository.findAll().stream()
                .map(familyMapper::fromFamilyTypeModelToResponse).collect(Collectors.toList());
    }

    public FamilyModel findFamilyOrThrow(UUID id){
        return familyRepository.findById(id).orElseThrow(CustomNotFoundException::family
        );
    };

    private FamilyType findFamilyTypeOrThrow(int id){
        return familyTypeRepository.findById(id).orElseThrow(CustomNotFoundException::familyType);
    }

    private Visibility findVisibilityOrThrow(int id){
        return visibilityRepository.findById(id).orElseThrow(CustomNotFoundException::visibility);
    }

    private void validateOwnershipOrThrow(UUID id){
        if(!familyTypeRepository.isOwner(id, authUtil.getRequester()) && !authUtil.isAdmin()){
            throw CustomForbiddenActionException.notSelfOrAdmin();
        }
    }

    private FamilyDependenciesData loadFamilyDependencies(FamilyModel current, FamilyRequest request){
        int currentTypeId = current.getType() != null ? current.getType().getId() : 0;
        int requestTypeId = request.type() != 0 ? request.type() : 0;

        FamilyType type = current.getType();
        boolean typeDontMatch = !Objects.equals(currentTypeId, requestTypeId);
        if(typeDontMatch){
            type = this.findFamilyTypeOrThrow(requestTypeId);
        }

        // TODO: Default public no 0
        int currentVisibilityId = current.getVisibility() != null ? current.getVisibility().getId() : 0;
        int requestVisibilityId = request.visibility() != 0 ? request.visibility() : VisiblityEnum.PUBLIC.getId();

        if(current.isOfficial()){
            requestVisibilityId = VisiblityEnum.PUBLIC.getId();
        }

        Visibility visibility = current.getVisibility();
        boolean visibilityDontMatch = !Objects.equals(currentVisibilityId, requestVisibilityId);

        if(visibility == null || visibilityDontMatch ){
            visibility = this.findVisibilityOrThrow(requestVisibilityId);
        }

        return new FamilyDependenciesData(
                type,
                visibility
        );
    }

    public void applyDependenciesToFamily(FamilyModel target, FamilyRequest source) {
        FamilyDependenciesData dependenciesData = this.loadFamilyDependencies(target, source);

        target.setVisibility(dependenciesData.visibility());
        target.setType(dependenciesData.type());
    }
}
