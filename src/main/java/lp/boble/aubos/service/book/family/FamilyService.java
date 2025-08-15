package lp.boble.aubos.service.book.family;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.family.FamilyData;
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
        FamilyModel family = familyMapper.fromRequestToModel(request);
        family.setCreatedBy(authUtil.getRequester());
        family.setCreatedAt(Instant.now());

        this.applyDataToFamily(family, request);

        System.out.println(authUtil.getRequester().getAuthorities());

        if(isOfficial){
            family.setOfficial(true);
        }


        return familyMapper.fromModelToResponse(familyRepository.save(family));
    }

    @Transactional
    public FamilyResponse updateFamily(UUID id, FamilyRequest familyRequest) {
        FamilyModel family = this.findFamilyOrThrow(id);
        family.setUpdatedBy(authUtil.getRequester());

        familyMapper.toUpdateFromRequest(family, familyRequest);

        this.applyDataToFamily(family, familyRequest);

        return familyMapper.fromModelToResponse(familyRepository.save(family));
    }

    @Transactional
    public void deleteFamily(UUID id) {
        this.validateOwnershipOrThrow(id);

        familyRepository.deleteById(id);
    }

    public FamilyResponse getFamily(UUID id) {
        return familyMapper.fromModelToResponse(this.findFamilyOrThrow(id));
    }

    public List<FamilyTypeResponse> getAllTypes() {
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

    private FamilyData loadFamilyDependencies(FamilyModel model, FamilyRequest request){
        int currentTypeId = model.getType() != null ? model.getType().getId() : 0;
        int requestTypeId = request.type() != 0 ? request.type() : 0;


        FamilyType type = model.getType();
        if(!Objects.equals(currentTypeId, requestTypeId)){
            type = this.findFamilyTypeOrThrow(requestTypeId);
        }

        // Se o CURRENT não existir, coloca o ID padrão, se não pega o da geladeira
        // Se o REQUEST for null adiciona o padrão.
        int currentVisibilityId = model.getVisibility() != null ? model.getVisibility().getId() : 0;
        int requestVisibilityId = request.visibility() != 0 ? request.visibility() : VisiblityEnum.PUBLIC.getId();

        if(model.isOfficial()){
            requestVisibilityId = VisiblityEnum.PUBLIC.getId();
        }

        Visibility visibility = model.getVisibility();
        if(visibility == null || !Objects.equals(currentVisibilityId, requestVisibilityId)){
            visibility = this.findVisibilityOrThrow(requestVisibilityId);
        }

        return new FamilyData(
                type,
                visibility
        );
    }

    public void applyDataToFamily(FamilyModel target, FamilyRequest request) {
        FamilyData data = this.loadFamilyDependencies(target, request);

        target.setVisibility(data.visibility());
        target.setType(data.type());
    }
}
